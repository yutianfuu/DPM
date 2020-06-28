package ca.mcgill.ecse211.project;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;
import static ca.mcgill.ecse211.project.Resources.*;

public class UltrasonicLocalizer {

	private double deltaTheta;

	private OdometerLocalize odo;
	private SampleProvider usSensor;
	private float[] usData;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private NavigationLocalize nav;

	// Constructor.
	public UltrasonicLocalizer(OdometerLocalize odo, SampleProvider usSensor, float[] usData, NavigationLocalize nav) {

		// Set all variables from constructor input to be the variables defined above.
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.nav = nav;

		// Get access to motors.
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// Set acceleration.
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);

		// Set speed.
		leftMotor.setSpeed((int) ROTATE_SPEED2);
		rightMotor.setSpeed((int) ROTATE_SPEED2);
	}

	/* US localization method. */
	public void doLocalization() {
		double angleA, angleB;
		// Rotate the robot until it sees no wall (~40cm).
		// The + 2 acts as a buffer to avoid confusion between going to and leaving a wall.
		// While the robot does not see wall, turn right.
		while (getFilteredData() < 42) {
			leftMotor.forward();
			rightMotor.backward();
		}

		// Keep rotating until the robot sees a wall, then latch the current
		// odometer angle; set it to angleA.
		while (getFilteredData() > 40) {
			leftMotor.forward();
			rightMotor.backward();
		}
		
		Sound.beep();
		angleA = odo.getAng();

		// Switch direction and turn until it sees no wall.
		while (getFilteredData() < 40 + 2) {
			leftMotor.backward();
			rightMotor.forward();
		}

		// Keep rotating until the robot sees a wall, then latch the current
		// odometer angle; set it to AngleB
		while (getFilteredData() > 40) {
			leftMotor.backward();
			rightMotor.forward();
		}
		
		Sound.beep();
		angleB = odo.getAng();

		// Stop both motors
		leftMotor.stop(true);
		rightMotor.stop(true);

		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'

		// Set deltaTheta depending on how angles worked out.
		//rising_edge case
		if (angleA < angleB) {				
			deltaTheta = 55 - (angleA + angleB) / 2 - 90;
		//falling edge case
		} else if (angleA > angleB) {		
			deltaTheta = 193 - (angleA + angleB) / 2 - 45;
		}

		// Get total new angle.
		double newAng = deltaTheta + odo.getAng();
		finaldistance = getFilteredData();

		Sound.buzz();

		// update the odometer position (example to follow:)
		odo.setPosition(new double[] { 0.0, 0.0, newAng }, new boolean[] { true, true, true });

		// Turn to 0, so that it faces the right direction.
		nav.turnTo(0, true);
	}

	/* Get US data. */
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		// usdata contains the distance data from the US sensor
		float distance = 100 * usData[0];
		return distance;
	}

}