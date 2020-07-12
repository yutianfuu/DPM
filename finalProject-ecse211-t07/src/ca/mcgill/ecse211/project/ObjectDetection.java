package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.OBJECT_DETECTION_THRESHOLD;
import static ca.mcgill.ecse211.project.Resources.navigation;
import static ca.mcgill.ecse211.project.Resources.odometer;
import static ca.mcgill.ecse211.project.Resources.usLocalizer;

/**
 * Object Detection class polls the ultrasonic sensor to detect nearby objects and
 * executes the necessary logic to avoid walls or attach to the stranded vehicle.
 * This thread is started by the Navigation class.
 * 
 * @author Dimitrios Sinodinos
 * @author Minh Quan Hoang
 */
public class ObjectDetection implements Runnable {
  
  public static boolean objectDetected = false;
  public static boolean trailerAttached = false;
  
  /**
   * Polls the US sensor to detect nearby objects. If we detect an object while outside 
   * the search zone, then we call "circumvent" and if we are in the search zone, we call 
   * "collectVehicle".
   */
  public void run() {
    
    double distance = usLocalizer.getFilteredDistance();
    
    while (true) {
      distance = usLocalizer.getFilteredDistance();
      if (distance < OBJECT_DETECTION_THRESHOLD) {
        objectDetected = true;
        if (navigation.isInSearchZone()) {
          collectTailer();
          objectDetected = false;
          break;
        } else {
          circumvent();
          objectDetected = false;
        }
      }
    }
  }
  
  /**
   * Stops the current navigation by implementing a wall follower routine. Then resumes 
   * navigation to the search zone. The avoidance algorithm moves the robot two spaces parallel
   * to the obstacle and the turns perpendicularly to check if the obstacle is still obstructing
   * our path. If the obstacle is still present, we repeat the process. Before calling travelTo(),
   * we must validate that the next point is on the island. If it is not, we must perform this
   * process in the opposite direction (turn 180 degrees). IMPLIMENTATION REQUIRED.
   */
  public void circumvent() {
    
    /* TODO Implement code for obstacle avoidance. */
    
    // Relocalize after avoiding the obstacle.
    LightLocalization.lightRelocalize();
    
    // Resume travel to the search zone.
    if (navigation.searchZone.ll.y < odometer.getXyt()[1]) {
      navigation.travelToPerpendicular(navigation.searchZone.ur.x,navigation.searchZone.ur.y);
    } else {
      navigation.travelToPerpendicular(navigation.searchZone.ll.x,navigation.searchZone.ll.y);
    }
  }
  
  /**
   * Identifies the attachment point of the stranded vehicle and attaches the motorized
   * hook. It should get the details of the cart location and color by calling the ColorClassifier
   * class and then call the collect() method in the TrailerCollection class.
   * IMPLIMENTATION REQUIRED.
   */
  public void collectTailer() {
    /* TODO Get information on hook location and color from the ColorClassifier class. */
    /* TODO Call the collect() function from the TrailerCollection class. */
    trailerAttached = true;
  }
  
  /**
   * Used by other threads to communicate if an object has been detected.
   * 
   * @return boolean if an object was detected.
   */
  public static boolean isObjectDetected() {
    return objectDetected;
  }
  
  /**
   * Used by other threads to communicate if the trailer is connected to the robot.
   * 
   * @return boolean if trailer is connected.
   */
  public static boolean isTrailerAttached() {
    return trailerAttached;
  }
}
