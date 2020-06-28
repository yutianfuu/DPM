package ca.mcgill.ecse211.project;

import lejos.robotics.SampleProvider;
import static ca.mcgill.ecse211.project.Resources.*;

import lejos.hardware.Button;

/**
 * The main driver class for the lab.
 */
public class Main {

	/**
	 * The main entry point.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];
		OdometerLocalize odo = new OdometerLocalize(leftMotor, rightMotor, 30, true);
		
		// Navigation object
		NavigationLocalize nav = new NavigationLocalize(odo);
		
		// perform the ultrasonic localization
		UltrasonicLocalizer usl = new UltrasonicLocalizer(odo, usValue, usData, nav);
		usl.doLocalization();
		
		//let the car drives automatically
		DriveLocalizer.drive();
		Button.waitForAnyPress();
		
		// start navigation
		Resources.navigation = new Navigation(odometer, leftMotor, rightMotor);
		new Thread(odometer).start();
		odometer.setXyt(TILE_SIZE, TILE_SIZE, 0);
		new Thread(navigation).start();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
		} // do nothing
		System.exit(0);
	}

	/**
	 * Sleeps current thread for the specified duration.
	 * 
	 * @param duration sleep duration in milliseconds
	 */
	public static void sleepFor(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

}