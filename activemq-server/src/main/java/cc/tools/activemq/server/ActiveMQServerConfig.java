package cc.tools.activemq.server;

import javax.servlet.ServletContext;

/**
 * This class implements a configuration class.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class ActiveMQServerConfig {

  /**
   * Constructor.
   */
  public ActiveMQServerConfig() {
    
  }
  
  /**
   * Initialize config object.
   * @param context servlet context.
   */
  public void init(ServletContext context) {
    
    try {
      
      if (!setConnectLimit(context)       | 
          !setConnectInterval(context)    |
          !setPublishLimit(context)       |
          !setPublishInterval(context)    |
          !setInitialContextKey(context)  |
          !setFactoryName(context)        |
          !setPrivateKeyLocation(context) |
          !setCredentialsLocation(context)) {
        
        _logger.severe("setter failed");
        
        return;
      }
               
    } catch (Exception e) {
      
      _logger.exception(e);

      return;
    }
    
    _isValid = true;
  }

  /** 
   * Method to set connect retry limit.
   * 
   * @param context servlet context.
   * @return boolean true if success, false otherwise.
   */
  private boolean setConnectLimit(ServletContext context) {

    String number = context.getInitParameter(_CONTEXT_CONNECT_LIMIT_KEY);

    try {
          
      _connectLimit = Integer.parseInt(number);
    
      return true;
      
    } catch (Exception e) {

      _logger.exception(e);

    }

    return false;
  
  }

  /** 
   * Method to set connection retry pause.
   * 
   * @param context servlet context.
   * @return boolean true if success, false otherwise.
   */
  private boolean setConnectInterval(ServletContext context) {

    String number = context.getInitParameter(_CONTEXT_CONNECT_INTERVAL_KEY);

    try {
          
      _connectInterval = Integer.parseInt(number);
    
      return true;
      
    } catch (Exception e) {

      _logger.exception(e);

    }

    return false;
  
  }

  /** 
   * Method to set publish try limit.
   * 
   * @param context servlet context.
   * @return boolean true if success, false otherwise.
   */
  private boolean setPublishLimit(ServletContext context) {

    String number = context.getInitParameter(_CONTEXT_PUBLISH_LIMIT_KEY);

    try {
          
      _publishLimit = Integer.parseInt(number);
    
      return true;
      
    } catch (Exception e) {

      _logger.exception(e);

    }

    return false;
  
  }

  /** 
   * Method to set publish retry pause.
   * 
   * @param context servlet context.
   * @return boolean true if success, false otherwise.
   */
  private boolean setPublishInterval(ServletContext context) {

    String number = context.getInitParameter(_CONTEXT_PUBLISH_INTERVAL_KEY);

    try {
          
      _publishInterval = Integer.parseInt(number);
    
      return true;
      
    } catch (Exception e) {

      _logger.exception(e);

    }

    return false;
  
  }

  /** 
   * Method to set JMS initial context key.
   * 
   * @param context servlet context.
   * @return boolean true if success, false otherwise.
   */
  private boolean setInitialContextKey(ServletContext context) {

    String name = context.getInitParameter(_CONTEXT_INITIAL_CONTEXT_KEY);

    if (name != null &&
        !name.isBlank()) {
      
      _initialContext = name;
      
      return true;
    }

    _logger.severe("could not load resource key ",
        _CONTEXT_INITIAL_CONTEXT_KEY,
        ". check servlet configuration.");
    
    return false;
  
  }

  /** 
   * Method to set JMS factory name.
   * 
   * @param context servlet context.
   * @return boolean true if success, false otherwise.
   */
  private boolean setFactoryName(ServletContext context) {

    String name = context.getInitParameter(_CONTEXT_FACTORY_NAME_KEY);

    if (name != null &&
        !name.isBlank()) {
      
      _factoryName = name;
      
      return true;
    }

    _logger.severe("could not load resource key ",
        _CONTEXT_FACTORY_NAME_KEY,
        ". check servlet configuration.");

    return false;
  
  }

  /** 
   * Method to set security private key file location.
   * 
   * @return boolean true if success, false otherwise.
   * @param context servlet context.
   */
  private boolean setPrivateKeyLocation(ServletContext context) {

    String location = context.getInitParameter(_CONTEXT_SERVER_PRIVATE_LOCATION_KEY);
    
    if (location != null &&
        !location.isBlank()) {
      
      _privateKeyLocation = location;
      
      return true;
    }

    _logger.severe("could not load resource key ",
        _CONTEXT_SERVER_PRIVATE_LOCATION_KEY,
        ". check servlet configuration.");
    
    return false;
  
  }

  /** 
   * Method to set security credentials file location.
   * 
   * @param context servlet context.
   * @return boolean true if success, false otherwise.
   */
  private boolean setCredentialsLocation(ServletContext context) {

    String location = context.getInitParameter(_CONTEXT_CREDENTIALS_FILE_LOCATION_KEY);
    
    if (location != null &&
        !location.isBlank()) {
      
      _credentialsLocation = location;
      
      return true;
    }

    _logger.severe("could not load resource key ",
        _CONTEXT_CREDENTIALS_FILE_LOCATION_KEY,
        ". check servlet configuration.");
    
    return false;
  
  }

  /**
   * Method to get max number of connection attempts in a row.
   *
   * @return int limit.
   */
  public int getConnectLimit() {
    return _connectLimit;
  }

  /**
   * Method to get pause interval between connect tries.
   * 
   * @return int pause.
   */
  public int getConnectInterval() {
    return _connectInterval;
  }

  /**
   * Method to get max number of publish attempts in a row.
   * 
   * @return int limit.
   */
  public int getPublishLimit() {
    return _publishLimit;
  }

  /**
   * Method to get pause interval between publish tries.
   * 
   * @return int pause.
   */
  public int getPublishInterval() {
    return _publishInterval;
  }

  /**
   * Method to get initial context value.
   * 
   * @return String containing initial context.
   */
  public String getInitialContext() {
    return _initialContext;
  }

  /**
   * Method to get factory lookup value.
   * 
   * @return String containing factory lookup.
   */
  public String getFactoryName() {
    return _factoryName;
  }

  /**
   * Method to get private key location file.
   * 
   * @return String containing private key file location.
   */
  public String getPrivateKeyLocation() {
    return _privateKeyLocation;
  }

  /**
   * Method to get credentials file location..
   * 
   * @return String credentials file location.
   */
  public String getCredentialsLocation() {    
    return _credentialsLocation;
  }
  
  /**
   * Connection retry limit.
   */
  private int _connectLimit = -1;

  /**
   * Message publish retry sleep interval (ms).
   */
  private int _connectInterval = -1;

  /**
   * Message publish retry limit.
   */
  private int _publishLimit = -1;

  /**
   * Message publish retry sleep interval (ms).
   */
  private int _publishInterval = -1;

  /**
   * MQ initial context.
   */
  private String _initialContext = null;

  /**
   * MQ Factory name
   */
  private String _factoryName = null;

  /**
   * MQ Factory name
   */
  private String _privateKeyLocation = null;

  /**
   * MQ Factory name
   */
  private String _credentialsLocation = null;

  /**
   * Parameter constant '{@value _CONTEXT_CONNECT_LIMIT_KEY}'.
   */
  final public static String _CONTEXT_CONNECT_LIMIT_KEY = "connect-limit";

  /**
   * Parameter constant '{@value _CONTEXT_CONNECT_INTERVAL_KEY}'.
   */
  final public static String _CONTEXT_CONNECT_INTERVAL_KEY = "connect-interval";

  /**
   * Parameter constant '{@value _CONTEXT_PUBLISH_LIMIT_KEY}'.
   */
  final public static String _CONTEXT_PUBLISH_LIMIT_KEY = "publish-limit";

  /**
   * Parameter constant '{@value _CONTEXT_PUBLISH_INTERVAL_KEY}'.
   */
  final public static String _CONTEXT_PUBLISH_INTERVAL_KEY = "publish-interval";

  /**
   * Parameter constant '{@value _CONTEXT_INITIAL_CONTEXT_KEY}'.
   */
  final public static String _CONTEXT_INITIAL_CONTEXT_KEY = "initial-context";

  /**
   * Parameter constant '{@value _CONTEXT_FACTORY_NAME_KEY}'.
   */
  final public static String _CONTEXT_FACTORY_NAME_KEY = "factory-name";

  /**
   * Parameter constant '{@value _CONTEXT_SERVER_PRIVATE_LOCATION_KEY}'.
   */
  final public static String _CONTEXT_SERVER_PRIVATE_LOCATION_KEY = "server-private-key";

  /**
   * Parameter constant '{@value _CONTEXT_CREDENTIALS_FILE_LOCATION_KEY}'.
   */
  final public static String _CONTEXT_CREDENTIALS_FILE_LOCATION_KEY = "credentials";

  /**
   * Method to check whether security object is valid.
   * @return boolean indicating validity.  
   */
  public boolean getIsValid() {
    return _isValid;
  }
    
  /**
   * boolean indicating whether this {@link ActiveMQServerConfig} object is in a valid
   * state.
   */
  private boolean _isValid = false;
  
  /**
   * Local logger reference for logging operations.
   */
  final private static ActiveMQServerLogger _logger = new ActiveMQServerLogger(ActiveMQServerConfig.class.getName());

}
