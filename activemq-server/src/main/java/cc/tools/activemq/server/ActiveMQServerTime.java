package cc.tools.activemq.server;

import java.util.Date;

/**
 * This class implements logging.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class ActiveMQServerTime {

  /**
   * Constructor {@link ActiveMQServerTime}.
   */
  private ActiveMQServerTime() {
  }
  
  /**
   * Method to return the current time as a formatted String.
   * @return String containing formatted current time.
   */
  public static String now() {
    return new Date().toString();
  }
 
  /**
   * Method to cause the current thread to sleep.
   * @param ms sleep interval in milliseconds
   * @return boolean indicating whether sleep was NOT interrupted by an exception.
   */
  public static boolean sleep(int ms) {
    
    try {
    
      Thread.sleep(ms);
    
    } catch (Exception e) {
    
      return false;
    
    }
    
    return true;
  }
}