package ca.mcgill.ecse211.project;

/**
 * This class is used to create the map used by the Navigation class.
 * 
 * @author Dimitrios Sinodinos
 * @author Jacob St-Laurent
 */
public class Map {
  
  /**
   * Array of five waypoints.
   */
  private Waypoint[] waypoints = new Waypoint[5];
  
  /**
   * Constructor to initialize one of the four bonus or one of the four regular set of waypoints.
   * 
   * @param index integer to select one of the predefined maps (1-4).
   */
  public Map(int index) {
    switch (index) {
      case 1:
        waypoints[0] = new Waypoint(1,7);
        waypoints[1] = new Waypoint(3,4);
        waypoints[2] = new Waypoint(7,7);
        waypoints[3] = new Waypoint(7,4);
        waypoints[4] = new Waypoint(4,1);
        break;
      case 2:
        waypoints[0] = new Waypoint(5,4);
        waypoints[1] = new Waypoint(1,7);
        waypoints[2] = new Waypoint(7,7);
        waypoints[3] = new Waypoint(7,4);
        waypoints[4] = new Waypoint(4,1);
        break;
      case 3:
        waypoints[0] = new Waypoint(3,1);
        waypoints[1] = new Waypoint(7,4);
        waypoints[2] = new Waypoint(7,7);
        waypoints[3] = new Waypoint(1,7);
        waypoints[4] = new Waypoint(1,4);
        break;
      case 4:
        waypoints[0] = new Waypoint(1,4);
        waypoints[1] = new Waypoint(3,7);
        waypoints[2] = new Waypoint(3,1);
        waypoints[3] = new Waypoint(7,4);
        waypoints[4] = new Waypoint(7,7);
        break;
      default:
        break;
    }
  }
  
  /**
   * Getter for the set of waypoints in the map.
   * 
   * @return Waypoint[] an array of waypoints specific to this map.
   */
  public Waypoint[] getMap() {
    return this.waypoints;
  }
  
  /**
   * Public nested class serving as a data structure representing a waypoint.
   * 
   * @author Dimitrios Sinodinos
   * @author Jacob St-Laurent
   */
  public class Waypoint {
    private int x;
    private int y;
    
    /**
     * Constructor for a waypoint.
     * 
     * @param x is the x coordinate as a multiple of TILE_SIZE.
     * @param y is the y coordinate as a multiple of TILE_SIZE.
     */
    public Waypoint(int x, int y) {
      this.x = x;
      this.y = y;
    }
    
    /**
     * Getter for the x coordinate of a waypoint.
     * 
     * @return x
     */
    public int getX() {
      return this.x;
    }
    
    /**
     * Getter for the y coordinate of a waypoint.
     * 
     * @return y
     */
    public int getY() {
      return this.y;
    }
  }
}
