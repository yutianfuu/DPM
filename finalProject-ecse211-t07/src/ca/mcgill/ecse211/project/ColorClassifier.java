package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.OBJECT_DETECTION_THRESHOLD;
import static ca.mcgill.ecse211.project.Resources.frontColorSensor;
import static ca.mcgill.ecse211.project.Resources.lcd;
import static ca.mcgill.ecse211.project.Resources.usLocalizer;

import lejos.hardware.sensor.SensorMode;

/**
 * Class to identify colors which will be used to find team specific color hitch.
 * This class will be called upone when and object is detected by ObjectDetetction
 * while we are in the search zone.
 * 
 * @author Dimitrios Sinodinos
 * @author Minh Quan Hoang
 *
 */
public class ColorClassifier {

  public static SensorMode color = frontColorSensor.getRGBMode();
  public static float[] colorSample = new float[color.sampleSize()];
  public static boolean objectDetected = false;

  private static double objectDistance = usLocalizer.getFilteredDistance();  

  /**
   * Enumerated color values. Values subject to change depending on
   * ambient lighting.
   */
  public enum RingColor {
    GREEN, BLUE, YELLOW, ORANGE, UNKNOWN
  }

  private static final double BLUE_R_MEAN = 0.02723144;
  private static final double BLUE_R_STD = 0.00857729;
  private static final double GREEN_R_MEAN = 0.05636523;
  private static final double GREEN_R_STD = 0.01520633;
  private static final double YELLOW_R_MEAN = 0.15095626;
  private static final double YELLOW_R_STD = 0.0200428;
  private static final double ORANGE_R_MEAN = 0.095048288;
  private static final double ORANGE_R_STD = 0.02006;
  
  /**
   * Fetches the current color sample and stores the value in colorSample.
   * @return colorSample array of float color samples.
   */
  public float[] getColorSample() {
    color.fetchSample(colorSample, 0);
    return colorSample;
  }

  /**
   * Classifies the color detected as of the known color values.
   * @return Color the color of the object.
   */
  public static RingColor classifyColor() {

    double red = colorSample[0];
    
    if (red >= GREEN_R_MEAN - GREEN_R_STD && red <= GREEN_R_MEAN + GREEN_R_STD) {
      return RingColor.GREEN;
    } else if (red >= BLUE_R_MEAN - BLUE_R_STD && red <= BLUE_R_MEAN + BLUE_R_STD) {
      return RingColor.BLUE;
    } else if (red >= YELLOW_R_MEAN - YELLOW_R_STD && red <= YELLOW_R_MEAN + YELLOW_R_STD) {
      return RingColor.YELLOW;
    } else if (red >= ORANGE_R_MEAN - ORANGE_R_STD && red <= ORANGE_R_MEAN + ORANGE_R_STD) {
      return RingColor.ORANGE;
    } else {
      return RingColor.UNKNOWN;
    }
  }

  /**
   * Method to detect the color once an object breaks the detection threshold.
   * 
   * @return RingColor color of the detected object.
   */
  public static RingColor colorDetection() {
    objectDetected = false;
    while (!objectDetected) {
      
      color.fetchSample(colorSample, 0);
      objectDistance = usLocalizer.getFilteredDistance();
      RingColor ringColor = classifyColor();
      if (ringColor != RingColor.UNKNOWN && objectDistance <= OBJECT_DETECTION_THRESHOLD) {  
        
        lcd.clear();
        objectDetected = true;

        lcd.drawString("Object Detected", 0, 0);
        lcd.drawString("" + colorSample[0], 0, 5);

        switch (ringColor) {
          case GREEN:
            lcd.drawString("GREEN", 0, 1);
            break;
          case BLUE:
            lcd.drawString("BLUE", 0, 1);
            break;
          case ORANGE:
            lcd.drawString("ORANGE", 0, 1);
            break;
          case YELLOW:
            lcd.drawString("YELLOW", 0, 1);
            break;
          case UNKNOWN:
            lcd.drawString("UNKNOWN", 0, 1);
            break;
          default:
            break;
        }
        return ringColor;
      }
    }
    return RingColor.UNKNOWN;
  }
}
