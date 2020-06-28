package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.*;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/** Odometer class was used by lab 2,which is mainly used for get the angle when
 * the robot is on the map
 **/
public class Odometer implements Runnable {

	private volatile double x;
	private volatile double y;
	private volatile double theta;
	private volatile boolean isReset = false;
	private static Lock lock = new ReentrantLock(true);
	private Condition doneReset = lock.newCondition();
	private static Odometer odo;
	private static int nowTachoL;
	private static int nowTachoR;
	private static int lastTachoL = leftMotor.getTachoCount();
	private static int lastTachoR = rightMotor.getTachoCount();

	/**
	 * This is the default constructor of this class. It initiates all motors and variables once. 
	 */
	Odometer() {
		setXyt(0, 0, 0);
	}

	/**
	 * Returns the Odometer Object. Use this method to obtain an instance of Odometer.
	 * 
	 * @return the Odometer Object
	 */
	public static synchronized Odometer getOdometer() {
		if (odo == null) {
			odo = new Odometer();
		}
		return odo;
	}

	/**
	 * This method is where the logic for the odometer will run.
	 */
	public void run() {
		// variables used during the calculation of the displacement and heading
		long updateStart;
		long updateDuration;
		double distL;
		double distR;
		double deltaD;

		Resources.leftMotor.resetTachoCount();
		Resources.rightMotor.resetTachoCount();

		lastTachoL = Resources.leftMotor.getTachoCount();
		lastTachoR = Resources.rightMotor.getTachoCount();

		while (true) {
			updateStart = System.currentTimeMillis();

			nowTachoL = Resources.leftMotor.getTachoCount();
			nowTachoR = Resources.rightMotor.getTachoCount();

			// Calculate new robot position based on tachometer count
			distL = Math.toRadians(Resources.WHEEL_RAD * (nowTachoL - lastTachoL));
			distR = Math.toRadians(Resources.WHEEL_RAD * (nowTachoR - lastTachoR));

			lastTachoL = nowTachoL;
			lastTachoR = nowTachoR;

			// Update odometer values with new calculated values using update()
			deltaD = (distL + distR) / 2;
			theta += Math.toDegrees((distL - distR) / Resources.BASE_WIDTH);         

			// calculate the x/y displacement
			x += deltaD * Math.sin(Math.toRadians(theta));
			y += deltaD * Math.cos(Math.toRadians(theta));

			// this ensures that the odometer only runs once every period
			updateDuration = System.currentTimeMillis() - updateStart;
			if (updateDuration < Resources.ODOMETER_PERIOD) {
				sleepFor(Resources.ODOMETER_PERIOD - updateDuration);
			}
		}
	}

	// THE FOLLOWING CODES ARE FROM THE STARTER CODE GIVEN BY THE TA

	/**
	 * Returns the Odometer data.
	 * Writes the current position and orientation of the robot onto the odoData array.
	 * {@code odoData[0] = x, odoData[1] = y; odoData[2] = theta;}
	 * 
	 *
	 */
	public double[] getXyt() {
		double[] position = new double[3];
		lock.lock();
		try {
			while(isReset) {
				doneReset.await();
			}
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return position;
	}

	/**
	 * Updates current dx, dy and dtheta values
	 * 
	 * @param dx the change in x
	 * @param dy the change in y
	 * @param dtheta the change in theta
	 */
	public void update(double dx, double dy, double dtheta) {
		lock.lock();
		isReset= true;
		try {
			x += dx;
			y += dy;
			// keeps the updates within 360 degrees
			theta = (theta + (360 + dtheta) % 360) % 360;
			isReset = false;
			// Let the other threads know we are done resetting
			doneReset.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Overrides the values of x, y and theta. Use for odometry correction.
	 * 
	 * @param x the value of x
	 * @param y the value of y
	 * @param theta the value of theta in degrees
	 */
	public void setXyt(double x, double y, double theta) {
		lock.lock();
		isReset = true;
		try {
			this.x = x;
			this.y = y;
			this.theta = theta;
			isReset = false;
			doneReset.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Overwrites x.
	 * 
	 * @param x the value of x
	 */
	public void setX(double x) {
		lock.lock();
		isReset = true;
		try {
			this.x = x;
			isReset = false;
			doneReset.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Overwrites y. Use for odometry correction.
	 */
	public void setY(double y) {
		lock.lock();
		isReset = true;
		try {
			this.y = y;
			isReset = false;
			doneReset.signalAll();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Overwrites theta. Use for odometry correction.
	 * 
	 * @param theta the value of theta
	 */
	public void setT(double theta) {
		lock.lock();
		isReset = true;
		try {
			this.theta = theta;
			isReset = false;
			doneReset.signalAll();
		} finally {
			lock.unlock();
		}
	}

}