package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.BASE_WIDTH;
import static ca.mcgill.ecse211.project.Resources.FORWARD_SPEED;
import static ca.mcgill.ecse211.project.Resources.ROTATE_SPEED;
import static ca.mcgill.ecse211.project.Resources.TEAM_NUMBER;
import static ca.mcgill.ecse211.project.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.project.Resources.WHEEL_RAD;
import static ca.mcgill.ecse211.project.Resources.green;
import static ca.mcgill.ecse211.project.Resources.island;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.objectDetector;
import static ca.mcgill.ecse211.project.Resources.odometer;
import static ca.mcgill.ecse211.project.Resources.red;
import static ca.mcgill.ecse211.project.Resources.redTeam;
import static ca.mcgill.ecse211.project.Resources.rightMotor;
import static ca.mcgill.ecse211.project.Resources.szg;
import static ca.mcgill.ecse211.project.Resources.szr;
import static ca.mcgill.ecse211.project.Resources.tng;
import static ca.mcgill.ecse211.project.Resources.tnr;
import static ca.mcgill.ecse211.project.Resources.usLocalizer;

import ca.mcgill.ecse211.playingfield.Point;
import ca.mcgill.ecse211.playingfield.Region;
import java.util.ArrayList;
import lejos.hardware.Sound;

/**
 * This class contains methods for general purpose navigation. It is also used for sequencing the
 * travel to and from the search zone, and contains the searching algorithm. This class essentially
 * sequences the entire gameplay from start to finish within the run() method.
 * 
 * @author Dimitrios Sinodinos
 * @author Minh Quan Hoang
 * 
 */
public class Navigation implements Runnable {
  
  // Tunnel entrance coordinates.
  private double tunnelEntranceX;
  private double tunnelEntranceY;
  
  // Tunnel exit coordinates.
  private double tunnelExitX;
  private double tunnelExitY;
  
  /**
   * Starting corner point specific to our team.
   */
  public Point startingCoordinates;
  
  /**
   *  List of points traveled once on the island. Traversed in reverse for backtracking after
   *  trailer collection.
   */
  public ArrayList<Point> pointsTraveled = new ArrayList<Point>();
  
  /**
   * The search zone region specific to our team.
   */
  public Region searchZone;
  
  /**
   * The run method contains the sequence of operations necessary to complete the objective of
   * search and rescue. It calls for the initial localization, then travels and aligns 
   * perpendicularly to the tunnel entrance, travels through the tunnel, relocalizes, starts 
   * the obstacle detection thread, travels to the search zone, initiates the search algorithm,
   * and finally performs backtracking once the trailer is attached.
   */
  public void run() {
    // Perform initial localization.
    usLocalizer.localize();
    LightLocalization.localize();
    beeps(3);

    // Initiate travel to the search area.
    navigateToSearchArea();
    beeps(3);
    
    // Initiate the perpendicular search algorithm.
    search();
    
    // Backtrack the pointsTravelled array in reverse order to return to the tunnel.
    for (int i = pointsTraveled.size() - 1; i >= 0; i--) {
      travelToPerpendicular(pointsTraveled.get(i).x,pointsTraveled.get(i).y);
    }
    
    // Travel back through the tunnel.
    travelToPerpendicular(tunnelEntranceX,tunnelEntranceY);
    
    // Travel to the starting corner (perpendicular movement is not longer necessary).
    travelTo(startingCoordinates.x, startingCoordinates.y);
    beeps(5);
  }

  /**
   * Navigate to a specified location on the board. Should be used post-coordinates localization.
   * This method also works with the ObjectDetection class for obstacle avoidance. It also
   * populates the pointsTravelled list once we are on the island.
   * 
   * @param x horizontal coordinates in tile size.
   * @param y vertical coordinates in tile size.
   */
  public void travelTo(double x, double y) {
    
    // Append new point (if on island) to the pointsTravelled list.
    Point newPoint = new Point(x,y);
    if (isOnIsland() && !ObjectDetection.isTrailerAttached()) {
      pointsTraveled.add(newPoint);
    }
    
    // Compute required changes in x and y.
    double deltaX = TILE_SIZE * x - odometer.getXyt()[0];
    double deltaY = TILE_SIZE * y - odometer.getXyt()[1];

    double xi = odometer.getXyt()[0];
    double yi = odometer.getXyt()[1];

    double finalTheta = Math.toDegrees(Math.atan2(deltaX, deltaY));

    // Turn to required heading.
    turnTo(finalTheta);

    // Move forward toward destination.
    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);    

    leftMotor.forward();
    rightMotor.forward();

    double distanceTraveled = 0;

    //Compute required distance to travel.
    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    // Object detection routine.
    while (distanceTraveled < distance) {
      if (ObjectDetection.isObjectDetected() && !ObjectDetection.isTrailerAttached()) {
        // Stop traveling the original path and remove the point from pointsTravelled.
        if (isOnIsland()) {
          pointsTraveled.remove(newPoint); 
        }
        break;
      }
      double deltaXf = odometer.getXyt()[0] - xi;
      double deltaYf = odometer.getXyt()[1] - yi;
      distanceTraveled = Math.sqrt(deltaXf * deltaXf + deltaYf * deltaYf);
    }
    
    leftMotor.setSpeed(0);
    rightMotor.setSpeed(0);
  }

  /**
   * Method that breaks down the travel into strict perpendicular movement. 
   * This is accomplished by first traveling the x displacement, then the y displacement.
   * 
   * @param x target x position.
   * @param y target y position.
   */
  public void travelToPerpendicular(double x, double y) {
    travelTo(x, odometer.getXyt()[1]);
    travelTo(odometer.getXyt()[0], y);
  }

  /**
   * Rotates the robot to the specified heading. Should be used post-heading localization.
   * 
   * @param absTheta specified heading.
   */
  public void turnTo(double absTheta) {

    // Get current heading.
    double currTheta = odometer.getXyt()[2];

    // Set angle displacement.
    double deltaTheta = absTheta - currTheta;

    // Set motor speeds to rotational speed.
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);

    // Compute the smallest angle to target.
    if (deltaTheta > 180) {
      deltaTheta -= 360;
    } else if (deltaTheta <= -180) {
      deltaTheta += 360;
    }

    // Rotate appropriate amount to align with target.
    leftMotor.rotate(convertAngle(deltaTheta), true);
    rightMotor.rotate(-convertAngle(deltaTheta), false);

    // Stop the motors.
    leftMotor.setSpeed(0);
    rightMotor.setSpeed(0); 
  }

  /**
   * Rotates the robot by a specified angle in the clockwise direction.
   * 
   * @param angle the desired angle to rotate in the clockwise direction.
   */
  public void turnBy(double angle) {
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.rotate(convertAngle(angle), true);
    rightMotor.rotate(-convertAngle(angle), false);
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   * 
   * @param angle the input angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public int convertAngle(double angle) {
    return convertDistance(Math.PI * BASE_WIDTH * angle / 360.0);
  }

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance the input distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Plays a specified number of "beep" sounds.
   * 
   * @param count representing the number of beeps.
   */
  private void beeps(int count) {
    for (int i = 0; i < count; i++) {
      Sound.beep();
    }
  }

  /**
   * Linear sequence that travels to arbitrary bridge location, aligns 
   * perpendicularly the bridge entrance, travels through the bridge, relocalizes, 
   * starts object detection, and travels to the search zone.
   */
  public void navigateToSearchArea() {
    Region tunnel;
    Region teamZone;

    // Get teamZone and tunnel.
    if (TEAM_NUMBER == redTeam) {
      tunnel = tnr;
      teamZone = red;
    } else {
      tunnel = tng;
      teamZone = green;
    }

    // Checks if tunnel LL is connected to the teamZone.
    if (tunnel.ll.x >= teamZone.ll.x && tunnel.ll.y >= teamZone.ll.y) {
      // Checks if tunnel is oriented in the y direction.
      if (tunnel.ur.x - tunnel.ll.x == 1) {
        tunnelEntranceX = tunnel.ll.x + 0.5;
        tunnelEntranceY = tunnel.ll.y - 0.5;
        tunnelExitX = tunnel.ur.x - 0.5;
        tunnelExitY = tunnel.ur.y + 0.5;
      } else {
        tunnelEntranceX = tunnel.ll.x - 0.5;
        tunnelEntranceY = tunnel.ll.y + 0.5;
        tunnelExitX = tunnel.ur.x + 0.5;
        tunnelExitY = tunnel.ur.y - 0.5;
      }
    } else {
      // Checks if tunnel is oriented in the x direction.
      if (tunnel.ur.y - tunnel.ll.y == 1) {
        tunnelEntranceX = tunnel.ur.x + 0.5;
        tunnelEntranceY = tunnel.ur.y - 0.5;
        tunnelExitX = tunnel.ll.x - 0.5;
        tunnelExitY = tunnel.ll.y + 0.5;
      } else {
        tunnelEntranceX = tunnel.ur.x - 0.5;
        tunnelEntranceY = tunnel.ur.y + 0.5;
        tunnelExitX = tunnel.ll.x + 0.5;
        tunnelExitY = tunnel.ll.y - 0.5;
      }
    }

    // Travel to the tunnel.
    travelToPerpendicular(tunnelEntranceX, tunnelEntranceY);
    // Travel through the tunnel.
    travelToPerpendicular(tunnelExitX, tunnelExitY);

    // Relocalize after traveling through the tunnel.
    LightLocalization.lightRelocalize();

    // Start objectDetection.
    new Thread(objectDetector).start();

    // Define searchZone.
    if (TEAM_NUMBER == redTeam) {
      searchZone = szr;
    } else {
      searchZone = szg;
    }
    
    // Travel to the search zone.
    if (searchZone.ll.y < tunnelExitY) {
      travelToPerpendicular(searchZone.ur.x,searchZone.ur.y);
    } else {
      travelToPerpendicular(searchZone.ll.x,searchZone.ll.y);
    }
  }

  /**
   * This method performs the perpendicular sweep search algorithm until the desired 
   * trailer is identified. We travel perpendicularly from the LL to the UR if the search
   * zone is the above the tunnel. We travel perpendicularly from UR to LL if the search 
   * zone is below the tunnel. IMPLIMENTATION REQUIRED.
   */
  public static void search() {
    while (!ObjectDetection.isObjectDetected()) {
      /* TODO Implement the perpendicular sweep algorithm */
    }
    return;
  }
  
  /**
   * Detects if the robot is on the island.
   * 
   * @return boolean is on the island.
   */
  private static boolean isOnIsland() {
    double x = odometer.getXyt()[0];
    double y = odometer.getXyt()[1];
    return (x >= island.ll.x && x <= island.ur.x && y >= island.ll.y && y <= island.ur.y);
  }

  /**
   * Detects if the robot is in the search zone.
   * 
   * @return boolean is in the search zone.
   */
  public boolean isInSearchZone() {
    double x = odometer.getXyt()[0];
    double y = odometer.getXyt()[1];
    return (x >= searchZone.ll.x && x <= searchZone.ur.x 
        && y >= searchZone.ll.y && y <= searchZone.ur.y);
  }
  
  /**
   * Sets the starting corner coordinates. Necessary for return with trailer.
   * 
   * @param x starting coordinate.
   * @param y starting coordinate.
   */
  public void setStartingCoordinates(double x, double y) {
    this.startingCoordinates.x = x;
    this.startingCoordinates.y = y;
  }
  
  /**
   * Sleeps for the specified duration.
   * @param millis the duration in milliseconds
   */
  public static void sleepFor(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      // Nothing to do here
    }
  }
}
