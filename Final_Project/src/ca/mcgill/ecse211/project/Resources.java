package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.playingfield.Point;
import ca.mcgill.ecse211.playingfield.Region;
import ca.mcgill.ecse211.wificlient.WifiConnection;
import java.math.BigDecimal;
import java.util.Map;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>{@code import static ca.mcgill.ecse211.project.Resources.*;}
 * 
 * @author Younes Boubekeur
 * @author Dimitrios Sinodinos
 */
public class Resources {
  
  // Set these as appropriate for your team and current situation
  /**
   * The default server IP used by the profs and TA's.
   */
  public static final String DEFAULT_SERVER_IP = "192.168.2.3";
  
  /**
   * The IP address of the server that transmits data to the robot. For the beta demo and
   * competition, replace this line with
   * 
   * <p>{@code public static final String SERVER_IP = DEFAULT_SERVER_IP;}
   */
  public static final String SERVER_IP = "192.168.2.3"; // = DEFAULT_SERVER_IP;
  
  /**
   * Your team number.
   */
  public static final int TEAM_NUMBER = 7;
  
  /** 
   * Enables printing of debug info from the WiFi class. 
   */
  public static final boolean ENABLE_DEBUG_WIFI_PRINT = true;
  
  /**
   * Enable this to attempt to receive Wi-Fi parameters at the start of the program.
   */
  public static final boolean RECEIVE_WIFI_PARAMS = true;
  
  
  // ----------------------------- DECLARE YOUR CURRENT RESOURCES HERE -----------------------------
  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.102;
  
  /**
   * The robot width in centimeters.
   */
  public static final double BASE_WIDTH = 14.75;
  
  /**
   * The speed at which the robot moves forward in degrees per second.
   */
  public static final int FORWARD_SPEED = 180;
  
  /**
   * The speed at which the robot rotates in degrees per second.
   */
  public static final int ROTATE_SPEED = 120;
  
  /**
   * The distance in centimeters used to identify rising and falling edge angles (alpha and beta).
   */
  public static final int WALL_DIST_THRESHOLD = 25;
  
  /**
   * The margin in centimeters used to assert the presence of a rising or falling edge.
   */
  public static final int NOISE_MARGIN = 4;
  
  /**
   * The distance in centimeters from the ultrasonic sensor and the axis of rotation of the
   * robot's wheel base.
   */
  public static final double SENSOR_LENGTH_OFFSET = 4.5;
  
  /**
   * The distance in centimeters require to align the towing motor hook to the hitch of the trailer.
   */
  public static final double COLLECTION_OFFSET = 4.0;
  
  /**
   * The tile size in centimeters. Note that 30.48 cm = 1 ft.
   */
  public static final double TILE_SIZE = 30.48;
  
  /**
   * The minimum angle separation between edge detection angles in degrees.
   */
  public static final double MINIMUM_EDGE_SEPARATION = 85;
  
  /**
   * The number of samples to poll when taking an average US reading.
   */
  public static final int SAMPLING_SIZE = 10;
  
  /**
   * The distance to detect incoming objects.
   */
  public static final int OBJECT_DETECTION_THRESHOLD = 9;
  
  // Hardware resources
  /**
   * The LCD screen used for displaying text.
   */
  public static final TextLCD lcd = LocalEV3.get().getTextLCD();
  
  /**
   * The ultrasonic sensor.
   */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
  
  /**
   * The front color sensor.
   */
  public static EV3ColorSensor frontColorSensor = new EV3ColorSensor(SensorPort.S2);
  
  /**
   * The left color sensor.
   */
  public static EV3ColorSensor sideColorSensor1 = new EV3ColorSensor(SensorPort.S3);
  
  /**
   * The right color sensor.
   */
  public static EV3ColorSensor sideColorSensor2 = new EV3ColorSensor(SensorPort.S4);
  
  /**
   * The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);

  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);
  
  /**
   * The towing motor.
   */
  public static final EV3LargeRegulatedMotor backMotor = new EV3LargeRegulatedMotor(MotorPort.B);
  
  /**
   * The odometer.
   */
  public static Odometer odometer = Odometer.getOdometer();
  
  /**
   * The navigator.
   */
  public static Navigation navigation = new Navigation();
  
  /**
   * The object detector.
   */
  public static ObjectDetection objectDetector = new ObjectDetection();
  
  /**
   * The ultrasonic localizer.
   */
  public static UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer();
  
  // ----------------------------- eg, motors, sensors, constants, etc -----------------------------
  
  
  /**
   * Container for the Wi-Fi parameters.
   */
  public static Map<String, Object> wifiParameters;
  
  // This static initializer MUST be declared before any Wi-Fi parameters.
  static {
    receiveWifiParameters();
  }
  
  /** Red team number. */
  public static int redTeam = getWP("RedTeam");

  /** Red team's starting corner. */
  public static int redCorner = getWP("RedCorner");

  /** Green team number. */
  public static int greenTeam = getWP("GreenTeam");

  /** Green team's starting corner. */
  public static int greenCorner = getWP("GreenCorner");

  /** The Red Zone. */
  public static Region red = makeRegion("Red");

  /** The Green Zone. */
  public static Region green = makeRegion("Green");

  /** The Island. */
  public static Region island = makeRegion("Island");

  /** The red tunnel footprint. */
  public static Region tnr = makeRegion("TNR");

  /** The green tunnel footprint. */
  public static Region tng = makeRegion("TNG");

  /** The red search zone. */
  public static Region szr = makeRegion("SZR");

  /** The green search zone. */
  public static Region szg = makeRegion("SZG");
  
  /**
   * Receives Wi-Fi parameters from the server program.
   */
  public static void receiveWifiParameters() {
    // Only initialize the parameters if needed
    if (!RECEIVE_WIFI_PARAMS || wifiParameters != null) {
      return;
    }
    System.out.println("Waiting to receive Wi-Fi parameters.");

    // Connect to server and get the data, catching any errors that might occur
    try (WifiConnection conn =
        new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT)) {
      /*
       * getData() will connect to the server and wait until the user/TA presses the "Start" button
       * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
       * pressing the back/escape button on the EV3. getData() will throw exceptions if something
       * goes wrong.
       */
      wifiParameters = conn.getData();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
  
  /**
   * Returns the Wi-Fi parameter int value associated with the given key.
   * 
   * @param key the Wi-Fi parameter key
   * @return the Wi-Fi parameter int value associated with the given key
   */
  public static int getWP(String key) {
    if (wifiParameters != null) {
      return ((BigDecimal) wifiParameters.get(key)).intValue();
    } else {
      return 0;
    }
  }
  
  /** 
   * Makes a point given a Wi-Fi parameter prefix.
   */
  public static Point makePoint(String paramPrefix) {
    return new Point(getWP(paramPrefix + "_x"), getWP(paramPrefix + "_y"));
  }
  
  /**
   * Makes a region given a Wi-Fi parameter prefix.
   */
  public static Region makeRegion(String paramPrefix) {
    return new Region(makePoint(paramPrefix + "_LL"), makePoint(paramPrefix + "_UR"));
  }
  
}
