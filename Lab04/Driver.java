package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

public class Driver {

  /**
   * This method turns the robot by a specified angle in radians.
   *
   * @param angle the angle the robot will turn.
   */
  public static void turnBy(double angle) {
    leftMotor.rotate(convertAngle(angle), true);
    rightMotor.rotate(-convertAngle(angle), false);
  }

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * Code from lab2 - Odometer, SquareDriver.java.
   * 
   * @param distance the input distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   * Code from lab2 - Odometer, SquareDriver.java.
   * 
   * @param angle the input angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  private static int convertAngle(double angle) {
    return convertDistance(Math.PI * BASE_WIDTH * angle / 360.0);
  }

  /**
   * Sets the speed of both motors to the same values.
   * Code from lab2 - Odometer, SquareDriver.java.
   * 
   * @param speed the speed in degrees per second
   */
  public static void setSpeed(int speed) {
    setSpeeds(speed, speed);
  }

  /**
   * Sets the speed of both motors to different values.
   * Code from lab2 - Odometer, SquareDriver.java.
   * 
   * @param leftSpeed the speed of the left motor in degrees per second
   * @param rightSpeed the speed of the right motor in degrees per second
   */
  private static void setSpeeds(int leftSpeed, int rightSpeed) {
    leftMotor.setSpeed(leftSpeed);
    rightMotor.setSpeed(rightSpeed);
  }
}