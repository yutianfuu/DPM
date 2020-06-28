package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.FORWARD_SPEED;
import static ca.mcgill.ecse211.project.Resources.ROTATE_SPEED;
import static ca.mcgill.ecse211.project.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.navigation;
import static ca.mcgill.ecse211.project.Resources.odometer;
import static ca.mcgill.ecse211.project.Resources.rightMotor;
import static ca.mcgill.ecse211.project.Resources.sideColorSensor1;
import static ca.mcgill.ecse211.project.Resources.sideColorSensor2;

import lejos.hardware.sensor.SensorMode;

public class LightLocalization {

  // Light sensor wheel base offset.
  public static final double LIGHT_SENSOR_OFFSET = 5.5;
  //blue room
  public static double BLK_VAL = 0.32;
  public static double TILE_VAL = 0.48;

  //normal room
  public static double BLK_VAL2 = 0.3;
  public static double TILE_VAL2 = 0.58;

  // Array to store the color values
  public static SensorMode color = sideColorSensor1.getRedMode();
  public static float[] colorData = new float[color.sampleSize()];

  public static SensorMode color2 = sideColorSensor2.getRedMode();
  public static float[] colorData2 = new float[color2.sampleSize()];

  //Colors indicated by a certain value from the sensor
  public static float color_indicator;
  public static float color_indicator2;

  public static int error = 15;
  
  public static boolean isInitialLocalization = true;
  
  /**
   * Initial localization routine. Once ultrasonic localization terminates, the robot
   * will proceed to move forward to the front-facing perpendicular line until both sensors 
   * touch the line. If one touches before, the the motor to the side of that color sensor
   * will turn off, allowing the other color sensor to align as well. Next, the robot will
   * move forward a fixed distance to align the wheel base with the line. It will then rotate
   * to 90 degrees, move to the next line, then turn to 0 degrees. This will ensure a perfect 
   * localization to (1,1).
   */
  public static void localize() {
    // Move forward at half speed.
    leftMotor.setSpeed(FORWARD_SPEED / 2);
    rightMotor.setSpeed(FORWARD_SPEED / 2);
    leftMotor.forward();
    rightMotor.forward();
    
    // Detect black lines.
    detection();
    
    // Adjust theta.
    odometer.setTheta(0);
    
    // Align wheelbase to line.
    leftMotor.setSpeed(FORWARD_SPEED / 2);
    rightMotor.setSpeed(FORWARD_SPEED / 2);
    leftMotor.rotate(Navigation.convertDistance(LIGHT_SENSOR_OFFSET), true);
    rightMotor.rotate(Navigation.convertDistance(LIGHT_SENSOR_OFFSET), false);
    
    navigation.turnTo(90);
    
    // Move forward at half speed.
    leftMotor.setSpeed(FORWARD_SPEED / 2);
    rightMotor.setSpeed(FORWARD_SPEED / 2);
    leftMotor.forward();
    rightMotor.forward();
    
    // Detect the black lines.
    detection();
        
    // Align wheelbase to line.
    leftMotor.setSpeed(FORWARD_SPEED / 2);
    rightMotor.setSpeed(FORWARD_SPEED / 2);
    leftMotor.rotate(Navigation.convertDistance(LIGHT_SENSOR_OFFSET), true);
    rightMotor.rotate(Navigation.convertDistance(LIGHT_SENSOR_OFFSET), false);
    
    // Correct odometer values
    navigation.turnTo(0);
    odometer.setTheta(0);
    odometer.setX(TILE_SIZE);
    odometer.setX(TILE_SIZE);
    
    // Indicate the initial localization is complete.
    isInitialLocalization = false;
  }

  /**
   * Method to detect the black lines on the board.
   */
  public static void detection() {
    boolean leftMotorDetected = false;
    boolean rightMotorDetected = false;
    boolean inReverse = false;
    
    // Get initial position.
    double xi = odometer.getXyt()[0];
    double yi = odometer.getXyt()[1];
    
    // Loop to detect and align to black lines.
    while (leftMotor.isMoving() || rightMotor.isMoving()) {
      double xf = odometer.getXyt()[0];
      double yf = odometer.getXyt()[1];
      double distanceTraveled = Math.sqrt(Math.pow((xf - xi), 2) + Math.pow((yf - yi), 2));
      
      // Move backward if moved forward too much.
      if (distanceTraveled >= TILE_SIZE / 2) {
        if (leftMotor.isMoving() && rightMotor.isMoving()) {
          leftMotor.setSpeed(FORWARD_SPEED / 2);
          rightMotor.setSpeed(FORWARD_SPEED / 2);
          leftMotor.backward();
          rightMotor.backward();
          inReverse = true;
        }
      }
      
      // Get color samples.
      color.fetchSample(colorData, 0);
      float sensorColor1 = colorData[0];

      color2.fetchSample(colorData2, 0);
      float sensorColor2 = colorData2[0];
      
      // Check if perpendicular when both sensors detect lines.
      if ((sensorColor1 <= BLK_VAL && rightMotorDetected) 
          || (sensorColor2 <= BLK_VAL && leftMotorDetected)
          || sensorColor1 <= BLK_VAL && sensorColor2 <= BLK_VAL) {

        if (isPerpendicular(odometer.getXyt()[2])) {
          leftMotor.setSpeed(0);
          rightMotor.setSpeed(0);
        }
      }
      
      // Stop left motor, rotate right motor.
      if (sensorColor1 <= BLK_VAL && leftMotor.isMoving()) {
        leftMotorDetected = true;
        leftMotor.setSpeed(0);
        if (!rightMotorDetected) {
          rightMotor.setSpeed(ROTATE_SPEED / 2);
          if (inReverse) {
            rightMotor.backward();
          } else {
            rightMotor.forward();
          }
        }
      }
      
      // Stop right motor, rotate left motor.
      if (sensorColor2 <= BLK_VAL && rightMotor.isMoving()) {
        rightMotorDetected = true;
        rightMotor.setSpeed(0);
        if (!leftMotorDetected) {
          leftMotor.setSpeed(ROTATE_SPEED / 2);
          if (inReverse) {
            leftMotor.backward();
          } else {
            leftMotor.forward();
          }
        }
      }
    }
  }

  /**
   * Method to orient accordingly to the nearest perpendicular orientation. 
   * This serves as a relocalization method.
   */
  public static void relocalize() {

    // Get current angle and turn to nearest perpendicular angle.
    double theta = odometer.getXyt()[2];
    if (theta >= 315 && theta < 45) {
      navigation.turnTo(0);
    } else if (theta >= 45 && theta < 135) {
      navigation.turnTo(90);
    } else if (theta >= 135 && theta < 225) {
      navigation.turnTo(180);
    } else if (theta >= 225 && theta < 315) {
      navigation.turnTo(270);
    }
    
    // Move motors forward.
    leftMotor.setSpeed(FORWARD_SPEED / 2);
    rightMotor.setSpeed(FORWARD_SPEED / 2);
    leftMotor.forward();
    rightMotor.forward();
    
    // Detect black lines.
    detection();
    
    // Move forward again.
    leftMotor.setSpeed(FORWARD_SPEED / 2);
    rightMotor.setSpeed(FORWARD_SPEED / 2);
    
    // Move forward by light sensor offset.
    leftMotor.rotate(Navigation.convertDistance(LIGHT_SENSOR_OFFSET), true);
    rightMotor.rotate(Navigation.convertDistance(LIGHT_SENSOR_OFFSET), false);
    
    // Adjust theta.
    theta = odometer.getXyt()[2];
    if (theta > 90 - error && theta < 90 + error) {
      odometer.setTheta(90);
    } else if (theta > 180 - error && theta < 180 + error) {
      odometer.setTheta(180);
    } else if (theta > 270 - error && theta < 270 + error) {
      odometer.setTheta(270);
    } else if (theta > 360 - error && theta < 0 + error) {
      odometer.setTheta(0);
    }

  }

  /**
   * Method to verify if the current orientation is at a perpendicular angle.
   * @param theta current angle.
   * @return isPerpendicular boolean if current angle is within tolerable error 
   *     of a perpendicular angle.
   */
  private static boolean isPerpendicular(double theta) {
    if ((theta > 90 - error && theta < 90 + error) || (theta > 180 - error && theta < 180 + error)
        || (theta > 270 - error && theta < 270 + error) 
        || (theta > 360 - error && theta < 0 + error)) {
      return true;
    }
    return false;
  }
}
