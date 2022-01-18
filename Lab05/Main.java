package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.navigation;
import static ca.mcgill.ecse211.project.Resources.odometer;

import lejos.hardware.Button;

/**
 * The main driver class for the lab.
 * 
 * @author Dimitrios Sinodinos
 * @author Jacob St-Laurent
 */
public class Main {

  public static Map map;
  
  /**
   * The main entry point.
   * 
   * @param args not used
   */
  public static void main(String[] args) {
    int buttonChoice;
    new Thread(odometer).start();
    
    // Choose the type of demo.
    buttonChoice = chooseTest();
    if (buttonChoice == Button.ID_LEFT) {
      ColorClassifier.colorTest();
    }
    
    // Choose the type of Map.
    buttonChoice = chooseMap();
    if (buttonChoice == Button.ID_UP) {
      map = new Map(1);
    } else if (buttonChoice == Button.ID_RIGHT) {
      map = new Map(2);
    } else if (buttonChoice == Button.ID_DOWN) {
      map = new Map(3);
    } else if (buttonChoice == Button.ID_LEFT) {
      map = new Map(4);
    }
    
    navigation.setMap(map);
    
    new Thread(new Display()).start();
    new Thread(navigation).start();
    while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
    } // do nothing
    
    System.exit(0);
  }
  
  /**
   * Asks the user for the type of Map.
   * 
   * @return the user choice.
   */
  private static int chooseTest() {
    int buttonChoice;
    Display.showText("< Left  |  Right >",
                     "        |         ",
                     " Color  |  Field  ",
                     "  Test  |  Test   ");

    do {
      buttonChoice = Button.waitForAnyPress(); // left or right press
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
    return buttonChoice;
  }
  
  /**
   * Asks the user for the type of Map.
   * 
   * @return the user choice.
   */
  private static int chooseMap() {
    int buttonChoice;
    Display.showText("Choose a Map:",
                     "Map 1 -> Up",
                     "Map 2 -> Right",
                     "Map 3 -> Down",
                     "Map 4 -> Left");
    
    do {
      buttonChoice = Button.waitForAnyPress(); // left or right press
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT
        && buttonChoice != Button.ID_UP && buttonChoice != Button.ID_DOWN);
    return buttonChoice;
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
