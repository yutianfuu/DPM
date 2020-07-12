package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.lcd;

/**
 * This class is used to facilitate writing to the display. It is used mostly for debugging purposes
 * or color identification, and has no direct use for the competition.
 * 
 * @author Dimitrios Sinodinos
 * @author Jacob St-Laurent
 */
public class Display {
  
  /**
   * Shows the text on the LCD, line by line.
   * 
   * @param strings comma-separated list of strings, one per line
   */
  public static void showText(String... strings) {
    lcd.clear();
    for (int i = 0; i < strings.length; i++) {
      lcd.drawString(strings[i], 0, i);
    }
  }

}
