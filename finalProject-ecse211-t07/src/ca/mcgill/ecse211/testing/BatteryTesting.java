package ca.mcgill.ecse211.testing;

import ca.mcgill.ecse211.testing.SquareDriverTesting;
import lejos.hardware.Button;


/**
 * Designed for Batter Test which continuously run the robot in 7 * 7 board.
 * 
 * @author Yutian Fu
 */
public class BatteryTesting {
  /**
   * Main entry point of the test.
   * 
   * @param args not used.
   */
  public static void main(String[] args) {
    while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
      while (true) {
        SquareDriverTesting.drive();
      }
    }
  }

}
