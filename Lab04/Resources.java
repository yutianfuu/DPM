package ca.mcgill.ecse211.project;


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 */
public class Resources {

	/**
	 * The wheel radius in centimeters.
	 */
	public static final double WHEEL_RAD = 2.130;

	/**
	 * The robot width in centimeters. 13.375. 12.68
	 */
	public static final double BASE_WIDTH = 12.68;  

	/**
	 * Distance between the sensor and the wall (cm).
	 */
	public static final int WALL_DIST = 30;

	/**
	 * The speed at which the robot moves forward in degrees per second.
	 */
	public static final int FORWARD_SPEED = 120;
	public static final int DL_FORWARD_SPEED = 250;

	/**
	 * The speed at which the robot rotates in degrees per second.
	 */
	public static final int ROTATE_SPEED = 60;
	public static final int DL_ROTATE_SPEED = 150;

	/**
	 * The motor acceleration in degrees per second squared.
	 */
	public static final int ACCELERATION = 3000;

	/**
	 * The tile size in centimeters. Note that 30.48 cm = 1 ft.
	 */
	public static final double TILE_SIZE = 30.48;

	/**
	 * The sample size for the ultrasonic sensor.
	 */
	public static final int  SAMPLE_SIZE = 10;

	/**
	 * The colorID constant to distinguish the color. 
	 */
	public static final int colorID_constant = 13;

	/**
	 * Offset used to adjust the angle in radians.
	 */
	public static double offset = 0.95;

	/**
	 * Odometer sampling period.
	 */
	public static final long ODOMETER_PERIOD = 25;
	public static final int DEFAULT_TIMEOUT_PERIOD = 20;

	/**
	 * The left motor.
	 */
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);

	/**
	 * The right motor.
	 */
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);

	/**
	 * The ultrasonic sensor.
	 */
	public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
	public static final Port usPort = LocalEV3.get().getPort("S1");

	/**
	 * The odometer.
	 */
	public static Odometer odometer = Odometer.getOdometer();

	/**
	 * The UltrasonicLocalizer.
	 */
	public static UltrasonicLocalizer usLocalizer;
	public static double finaldistance = 0;

	/**
	 * The navigation.
	 */
	public static Navigation navigation;
	final static int FAST = 200, SLOW = 100, N_ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	public static final int ROTATE_SPEED2 = 50;
}