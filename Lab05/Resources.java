package ca.mcgill.ecse211.project;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>{@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 * 
 * @author Dimitrios Sinodinos
 * @author Jacob St-Laurent
 */
public class Resources {
  
  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.102;//2.13;
  
  /**
   * The robot width in centimeters.
   */
  public static final double BASE_WIDTH = 14.75;//15.1; //localization
  
  /**
   * The speed at which the robot moves forward in degrees per second.
   */
  public static final int FORWARD_SPEED = 180;
  
  /**
   * The speed at which the robot rotates in degrees per second.
   */
  public static final int ROTATE_SPEED = 120;
  
  /**
   * The distance in centimeters used to identify rising and falling edge angles (alpha and beta).
   */
  public static final int WALL_DIST_THRESHOLD = 25;
  
  /**
   * The margin in centimeters used to assert the presence of a rising or falling edge.
   */
  public static final int NOISE_MARGIN = 4;
  
  /**
   * The distance in centimeters from the ultrasonic sensor and the axis of rotation of the
   * robot's wheel base.
   */
  public static final double SENSOR_LENGTH_OFFSET = 4.5;//4.5;
  
  /**
   * The tile size in centimeters. Note that 30.48 cm = 1 ft.
   */
  public static final double TILE_SIZE = 30.48;
  
  /**
   * The minimum angle separation between edge detection angles in degrees.
   */
  public static final double MINIMUM_EDGE_SEPARATION = 85;
  
  /**
   * The number of samples to poll when taking an average US reading.
   */
  public static final int SAMPLING_SIZE = 10;
  
  /**
   * The distance to detect incoming objects.
   */
  public static final int OBJECT_DETECTION_THRESHOLD = 9;
  
  // Hardware resources
  /**
   * The LCD screen used for displaying text.
   */
  public static final TextLCD lcd = LocalEV3.get().getTextLCD();
  
  /**
   * The ultrasonic sensor.
   */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
  
  /**
   * The front color sensor.
   */
  public static EV3ColorSensor frontColorSensor = new EV3ColorSensor(SensorPort.S2);
  
  /**
   * The left color sensor.
   */
  public static EV3ColorSensor sideColorSensor1 = new EV3ColorSensor(SensorPort.S3);
  
  /**
   * The right color sensor.
   */
  public static EV3ColorSensor sideColorSensor2 = new EV3ColorSensor(SensorPort.S4);
  
  /**
   * The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);

  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);
  
  /**
   * The odometer.
   */
  public static Odometer odometer = Odometer.getOdometer();
  
  /**
   * The navigator.
   */
  public static Navigation navigation = new Navigation();
  
  /**
   * The ultrasonic localizer.
   */
  public static UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer();
}
