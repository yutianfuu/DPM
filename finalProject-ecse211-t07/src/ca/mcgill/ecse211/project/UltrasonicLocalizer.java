package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.MINIMUM_EDGE_SEPARATION;
import static ca.mcgill.ecse211.project.Resources.NOISE_MARGIN;
import static ca.mcgill.ecse211.project.Resources.ROTATE_SPEED;
import static ca.mcgill.ecse211.project.Resources.SAMPLING_SIZE;
import static ca.mcgill.ecse211.project.Resources.SENSOR_LENGTH_OFFSET;
import static ca.mcgill.ecse211.project.Resources.WALL_DIST_THRESHOLD;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.navigation;
import static ca.mcgill.ecse211.project.Resources.odometer;
import static ca.mcgill.ecse211.project.Resources.rightMotor;
import static ca.mcgill.ecse211.project.Resources.usSensor;

/**
 * The UltrasonicLocalizer implements two different types of localization methods which
 * use an ultrasonic sensor.The two types of localizations include falling edge 
 * (when robot starts by facing away from the wall) and rising edge (when robot 
 * starts by facing the wall).
 * 
 * @author Dimitrios Sinodinos
 * @author Jacob St-Laurent
 */
public class UltrasonicLocalizer {
  
  /**
   * Angle of first edge detected in degrees.
   */
  private double alpha = 0.0;
  
  /**
   * Angle of second edge detected in degrees.
   */
  private double beta = 0.0;
  
  /**
   * Angle correction for localization in degrees.
   */
  private double deltaTheta = 0.0;
    
  /**
   * Distance reading from the ultrasonic sensor in centimeters.
   */
  private int distance;
  
  /**
   * Buffer (array) to store US samples. Declared as an instance variable to avoid creating a new
   * array each time {@code readUsSample()} is called.
   */
  private float[] usData = new float[usSensor.sampleSize()];
  
  /**
   * FallingEdgeLocalization is used when the robot starts facing away from the walls.
   * It applies a heading correction (deltaTheta) based on the difference between the
   * midpoint angle of alpha and beta to 225 degrees.
   */
  private void fallingEdgeLocalization() {
    
    // Flag to indicate if we have found the falling edge.
    boolean fallingEdgeDetected = false;
    // Minimum distance reading from the ultrasonic sensor.
    double minimum = WALL_DIST_THRESHOLD; // WALL_DIST_THRESHOLD is larger than the true minimum.
    
    // Rotate the robot clockwise.
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
    
    // Alpha and beta detection loop.
    while (leftMotor.isMoving() && rightMotor.isMoving()) {
      // Get the usSensor distance.
      distance = (int)getFilteredDistance();
      // Update minimum if smaller distance is encountered.
      if (distance < minimum) {
        minimum = distance;
      }
      // Condition for falling edge detection.
      if (!fallingEdgeDetected && distance <= (WALL_DIST_THRESHOLD - NOISE_MARGIN)) {
        // Update alpha.
        alpha = odometer.getXyt()[2];
        fallingEdgeDetected = true;
      // Condition for rising edge detection.
      } else if (fallingEdgeDetected && distance >= (WALL_DIST_THRESHOLD + NOISE_MARGIN) 
          && odometer.getXyt()[2] > alpha + MINIMUM_EDGE_SEPARATION) {
        // Update beta.
        beta = odometer.getXyt()[2];
        // Stop the motors and exit the loop.
        leftMotor.setSpeed(0);
        rightMotor.setSpeed(0);
      }
    }
    
    // Compute heading error.
    deltaTheta = 230 - (beta + alpha) / 2; //225
    
    // Update heading accordingly.
    if (odometer.getXyt()[2] + deltaTheta >= 360) {
      odometer.setTheta(odometer.getXyt()[2] + deltaTheta - 360);
    } else if (odometer.getXyt()[2] + deltaTheta <= -360) {
      odometer.setTheta(odometer.getXyt()[2] + deltaTheta + 360);
    } else {
      odometer.setTheta(odometer.getXyt()[2] + deltaTheta);
    }
    
    // Update X and Y coordinates.
    odometer.setY(minimum + SENSOR_LENGTH_OFFSET);
    odometer.setX(minimum + SENSOR_LENGTH_OFFSET);
    
    // Rotate to 0 degrees after heading correction.
    navigation.turnTo(0);   
  }
  
  /**
   * RisingEdgeLocalization is used when the robot starts facing the walls. It applies a heading
   * correction (deltaTheta) based on the difference between the midpoint angle of alpha and beta
   * to 45 degrees.
   */
  private void risingEdgeLocalization() {
    
    // Flag to indicate if we have found the rising edge.
    boolean risingEdgeDetected = false;
    // Minimum distance reading from the ultrasonic sensor.
    double minimum = WALL_DIST_THRESHOLD; // WALL_DIST_THRESHOLD is larger than the true minimum.
    
    // Rotate the robot clockwise.
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
    
    // Alpha and beta detection loop.
    while (leftMotor.isMoving() && rightMotor.isMoving()) {
      // Get the usSensor distance.
      usSensor.fetchSample(usData, 0);
      distance = (int) (usData[0] * 100.0);
      // Update minimum if smaller distance is encountered.
      if (distance < minimum) {
        minimum = distance;
      }
      // Condition for rising edge detection.
      if (!risingEdgeDetected && distance >= (WALL_DIST_THRESHOLD + NOISE_MARGIN)) {
        // Update alpha.
        alpha = odometer.getXyt()[2];
        risingEdgeDetected = true;
      // Condition for falling edge detection.
      } else if (risingEdgeDetected && distance < (WALL_DIST_THRESHOLD - NOISE_MARGIN) 
          && odometer.getXyt()[2] > alpha + MINIMUM_EDGE_SEPARATION) {
        // Update beta.
        beta = odometer.getXyt()[2];
        // Stop the motors and exit the loop.
        leftMotor.setSpeed(0);
        rightMotor.setSpeed(0);
      }
    }
    
    // Compute heading error.
    deltaTheta = 49 - (beta + alpha) / 2; //45
    
    // Update heading accordingly.
    if (odometer.getXyt()[2] + deltaTheta >= 360) {
      odometer.setTheta(odometer.getXyt()[2] + deltaTheta - 360);
    } else if (odometer.getXyt()[2] + deltaTheta <= -360) {
      odometer.setTheta(odometer.getXyt()[2] + deltaTheta + 360);
    } else {
      odometer.setTheta(odometer.getXyt()[2] + deltaTheta);
    }
    
    // Update X and Y coordinates.
    odometer.setY(minimum + SENSOR_LENGTH_OFFSET);
    odometer.setX(minimum + SENSOR_LENGTH_OFFSET);
    
    // Rotate to 0 degrees after heading correction.
    navigation.turnTo(0);
  }
  
  /**
   * Localize is used for the initial localization. It will call the appropriate localization
   * technique based on the starting orientation of the robot.
   */
  public void localize() {
    usSensor.fetchSample(usData, 0);
    distance = (int) (usData[0] * 100.0);
    
    if (distance < WALL_DIST_THRESHOLD - NOISE_MARGIN) {
      // Use rising edge localization if starting by facing a wall.
      risingEdgeLocalization();
    } else {
      // Use falling edge localization if starting by facing away from a wall.
      fallingEdgeLocalization();
    }
  }
  
  /**
   * Takes an average of a SAMPLE_SIZE number of samples from 
   * the ultrasonic sensor distance readings.
   * 
   * @return average of a SAMPLE_SIZE number of distance samples.
   */
  public double getFilteredDistance() {
    double sum = 0;
    for (int  i = 0; i < SAMPLING_SIZE; i++) {
      usSensor.fetchSample(usData, 0);
      distance = (int) (usData[0] * 100.0);
      sum += distance + SENSOR_LENGTH_OFFSET;
    }
    return sum / SAMPLING_SIZE;
  }
}
