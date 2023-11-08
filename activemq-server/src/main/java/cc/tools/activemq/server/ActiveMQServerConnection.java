package cc.tools.activemq.server;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * This class implements an MQ writer interface for writing to Active MQ bus.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class ActiveMQServerConnection {

  /**
   * Constructor.
   * @param config Configuration object.
   */
  public ActiveMQServerConnection(ActiveMQServerConfig config) {
    
    _config = config;
    
  }
  
  /**
   * Method performs {@link ActiveMQServerConnection} initialization.
   * @return boolean true if success, false otherwise.
   */
  public boolean init() {

    try {
      
      InitialContext initCtx = new InitialContext();
      
      _context = (Context) initCtx.lookup(_config.getInitialContext());

      _isValid = true;
      
      return true;

    } catch (Exception e) {
    
      _logger.exception(e);
    
    }
    
    return false;
  }
  
  /**
   * Method to publish message to Broker.
   * 
   * @param id client call ID.
   * @param message text to be sent to MQ.
   * @param channel channel to be delivered to.
   * @param code StringBuffer to return result status code.
   * @param mqid StringBuffer to return MQ transaction id (if any).
   * @return boolean true if success, false otherwise.
   */
  public boolean publish(String id, 
      String message,
      String channel,
      StringBuilder code, 
      StringBuilder mqid) {
    
    _logger.info("publish id ",
        id);
    
    for (int i = 0; i < _config.getPublishLimit(); i++) {

      _logger.info("publish id ",
          id,
          " attempt ",
          Integer.toString(i+1));

      Map<String, ActiveMQServerAdaptor> adaptors1 = _adaptors;
      
      ActiveMQServerAdaptor adaptor1 = adaptors1.get(channel);
      
      if (!isConnected(adaptor1)) {
        
        _logger.severe("publish id ",
            id,
            " not connected");

        if (!connect(id, channel)) {
          
          _logger.severe("publish id ",
              id,
              " could not connect");

          if (i + 1 < _config.getPublishLimit()) {
          
            ActiveMQServerTime.sleep(_config.getPublishInterval());
          
          }
          
          continue;
        
        }        
        
      }

      Map<String, ActiveMQServerAdaptor> adaptors2 = _adaptors;
      
      ActiveMQServerAdaptor adaptor2 = adaptors2.get(channel);

      if (adaptor2 == null ||
          !adaptor2.publish(id, message, code, mqid)) {
        
        _logger.severe("publish id ",
            id,
            " could not publish");
        
        if (i + 1 < _config.getPublishLimit()) {

          ActiveMQServerTime.sleep(_config.getPublishInterval());

        }
        
        continue;  
      }

      _logger.severe("publish id ",
          id,
          " published");

      return true;
      
    }

    _logger.info("publish id ",
        id,
        " could not publish");
    
    return false;
  }

  /**
   * Method to create or recreate connection to MQ Broker.
   * @param id client call ID
   * @param channel channel id to be used.
   * @return boolean true for success, false otherwise.
   */
  synchronized private boolean connect(String id, String channel) {

    _logger.info("connecting ...",
        id);

    Map<String, ActiveMQServerAdaptor> adaptors1 = _adaptors;
    
    ActiveMQServerAdaptor adaptor1 = adaptors1.get(channel);

    if (isConnected(adaptor1)) {

      return true;
    
    }

    for (int i = 0; i < _config.getConnectLimit(); i++) {
      
      try {

        ActiveMQServerAdaptor adaptor2 = new ActiveMQServerAdaptor(_context, 
            _config.getFactoryName(),
            channel);

        if (adaptor2.getIsValid()) {

          HashMap<String, ActiveMQServerAdaptor> adaptors2 = new HashMap<String, ActiveMQServerAdaptor>();

          adaptors2.putAll(adaptors1);
          
          adaptors2.put(channel, adaptor2);
          
          _adaptors = adaptors2;
          
          _logger.info("connect id ",
              id,
              " got valid adaptor");

          return true;
          
        }

      } catch (Exception e) {

        _logger.exception(e);

      }

      ActiveMQServerTime.sleep(_config.getConnectInterval());
      
    }
    
    _logger.severe("connect id ",
        id,
        " could not get valid adaptor");
    
    _logger.severe("connect failed ",
        id);
    
    return false;
  }

  /**
  * Method to test if an indexed adaptor exists and if it is valid.
  * @param adaptor to be checked.
  * @return boolean indicating whether valid adaptor was found or not.
  */
  private boolean isConnected(ActiveMQServerAdaptor adaptor) {
    
    if (adaptor == null) {
      
      return false;
 
    }
    
    return adaptor.getIsValid();
  }

  /**
   * Method to check whether connection object is valid.
   * @return boolean indicating validity.  
   */
  public boolean getIsValid() {
    return _isValid;
  }
  
  /**
   * boolean indicating whether this {@link ActiveMQServerConnection} object is in a valid
   * state.
   */
  private boolean _isValid = false;
    
  /**
   * Local logger reference for logging operations.
   */
  final private ActiveMQServerLogger _logger = new ActiveMQServerLogger(ActiveMQServerConnection.class.getName());
  
  /**
   * JMS Context.
   */
  private Context _context = null;
  
  /**
   * MQ Adaptor to MQ JMS Api
   */  
  private Map<String, ActiveMQServerAdaptor> _adaptors = new HashMap<String, ActiveMQServerAdaptor>();

  /**
   * Configuration object. 
   */
  private ActiveMQServerConfig _config = null;

}


