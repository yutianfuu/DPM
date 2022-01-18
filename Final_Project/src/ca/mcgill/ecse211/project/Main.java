package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.navigation;
import static ca.mcgill.ecse211.project.Resources.odometer;

import lejos.hardware.Button;

/**
 * The main driver class for the competition. Once user input is received, the
 * odometer and navigation threads start. Pressing escape at any moment will stop
 * the robot.
 * 
 * @author Dimitrios Sinodinos
 * @author Jacob St-Laurent
 */
public class Main {
  
  /**
   * The main entry point. The main function simply waits for the user to press the "Enter" key
   * after map parameters have been loaded into the resources file. This method will then start 
   * the odometer and navigation threads. The navigation thread will be the principal driver
   * for the rest of the gameplay processes.
   * 
   * @param args not used
   */
  public static void main(String[] args) {
    
    while (Button.waitForAnyPress() != Button.ID_ENTER) {
    }
    
    new Thread(odometer).start();
    new Thread(navigation).start();
    
    while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
    } // do nothing
    
    System.exit(0);
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
