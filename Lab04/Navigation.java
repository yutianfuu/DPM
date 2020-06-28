package ca.mcgill.ecse211.project;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

import static ca.mcgill.ecse211.project.Resources.*;

/**
 * Navigation the robot on the map
 */
public class Navigation implements Runnable {

	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static Odometer odo;

	/**
	 * Constructor.
	 * 
	 * @param odo odometer
	 * @param leftMotor left motor of the robot
	 * @param rightMotor right motor of the robot
	 */
	public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		Navigation.odo = Odometer.getOdometer();
		Navigation.leftMotor = leftMotor;
		Navigation.rightMotor = rightMotor;
	}

	/**
	 * Maps of the navigation.
	 */
	public void run() {
		travelTo(4 * TILE_SIZE, 1 * TILE_SIZE);
		travelTo(7 * TILE_SIZE, 4 * TILE_SIZE);
		travelTo(7 * TILE_SIZE, 7 * TILE_SIZE);
		travelTo(1 * TILE_SIZE, 7 * TILE_SIZE);
		travelTo(4 * TILE_SIZE, 4 * TILE_SIZE);
	}

	/**
	 * This method causes the robot to travel to the  field location (x,y), specified in tile points.
	 * This method should continuously call turnTo(double theta), set the motor speed to forward.
	 * This will make sure that your heading is updated until you reach your exact goal. 
	 * This method will use the odometer.
	 * @param x x position 
	 * @param y y position
	 */
	public static void travelTo(double x, double y) {
		double dx = x - odo.getXyt()[0];
		double dy = y - odo.getXyt()[1];
		double dt = Math.atan2(dx, dy);
		turnTo(dt);
		// The distance should travel based on arc-tangent
		double dist = Math.hypot(dx, dy);
		//set the forward speed
		setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(dist), true);
		rightMotor.rotate(convertDistance(dist), false);
		stopMotors();
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading theta. 
	 * This method should turn a MINIMAL angle to its target.
	 * @param theta angle theta in radians
	 */
	public static void turnTo(double theta) {
		setSpeed(ROTATE_SPEED);
		// get the minimum turning angle 
		double turningAngle = getMinAngle(theta - Math.toRadians(odo.getXyt()[2]));
		leftMotor.rotate(convertAngle(Math.toDegrees(turningAngle)), true);
		rightMotor.rotate(-convertAngle(Math.toDegrees(turningAngle)), false);
	}

	/**
	 * This method calculate the minimum turning angle to suit the lab description.
	 * @param theta angle in radians
	 * @return
	 */
	public static double getMinAngle(double theta) {
		// angle > 180, decrease by 360
		if (theta > Math.PI) {
			theta -= 2 * Math.PI;
		}
		// angle < 180, increase by 360
		if (theta < - Math.PI) {
			theta += 2 * Math.PI;
		}
		return theta;
	}

	/**
	 * the distance that the wheel should  rotate
	 * Code from lab 2 Odometer
	 * 
	 * @param  the distance that the robot should travel
	 * @return the wheel rotations to cover that distance
	 */
	public static int convertDistance(double distance) {
		return (int) ((180.0 * distance) / (Math.PI * (WHEEL_RAD)));
	}

	/**
	 * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
	 * angle.
	 * 
	 * @param angle the input angle
	 * @return the wheel rotations necessary to rotate the robot by the angle
	 */
	public static int convertAngle(double angle) {
		return convertDistance(Math.PI *(BASE_WIDTH) * angle / 360.0);
	}

	/**
	 * Stop motors
	 */
	public static void stopMotors() {
		leftMotor.stop();
		rightMotor.stop();
	}

	/**
	 * Sets the speed of both motors to the same values
	 * 
	 * @param speed the speed in degrees per second
	 */
	public static void setSpeed(int speed) {
		setSpeeds(speed, speed);
	}

	/**
	 * Sets the speed of both motors to different values.
	 *
	 */
	public static void setSpeeds(int leftSpeed, int rightSpeed) {
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}

	/**
	 * Sets the acceleration of both motors
	 * 
	 * @param acceleration the acceleration in degrees per second squared
	 */
	public static void setAcceleration(int acceleration) {
		leftMotor.setAcceleration(acceleration);
		rightMotor.setAcceleration(acceleration);
	}
}