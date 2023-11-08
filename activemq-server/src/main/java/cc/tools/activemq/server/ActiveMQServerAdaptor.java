package cc.tools.activemq.server;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

/**
 * This class implements a wrapper for the Active MQ connection objects.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class ActiveMQServerAdaptor implements ExceptionListener {

  /**
   * Constructor.
   * @param context Initial Naming context.
   * @param factory MQ Factory name.
   * @param queue MQ Queue name.
   */
  public ActiveMQServerAdaptor(Context context, String factory, String queue) {
   
    try {

      _factory = (ConnectionFactory) context.lookup(factory);

      _connection = _factory.createConnection();

      _connection.setExceptionListener(this);

      _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      _producer = _session.createProducer((Destination) context.lookup(queue));

      _isValid.set(true);

    } catch (Exception e) {

      _logger.exception(e);

      shutdown();
    }
    
  }
  
  /**
   * Method to finalize state.
   */
  @Override
  @SuppressWarnings("removal")
  public void finalize() {

    shutdown();    
  
  }
  
  /** 
   * Method to publish message to message to MQ Broker.
   * 
   * @param id client call ID.
   * @param message text to be sent to MQ.
   * @param code StringBuffer to return result status code.
   * @param mqid StringBuffer to return MQ transaction id (if any).
   * @return boolean true if success, false otherwise.
   */
  public boolean publish(String id, String message, StringBuilder code, StringBuilder mqid) {
   
    try {
      
      TextMessage text = _session.createTextMessage(message);
      
      _producer.send(text);
     
      return true;
      
    } catch (Exception e) {
      
      _logger.exception(e);

      _isValid.set(false);
    }
    
    return false;  
  }
  
  /**
   * Method to close down MQ resources.
   */
  private void shutdown() {

    _producer = null;
    
    try {
    
      if (_session != null) {
        
        _session.close();
      
      }
    
    } catch (Exception e) {
      
      _logger.exception(e);
    }
    
    try {
      
      if (_connection != null) {

        _connection.close();
      
      }
    
    } catch (Exception e) {
      
      _logger.exception(e);
    }

    _factory = null;
  }

  /**
   * Method is the exception callback for implemented 
   * Interface {@link ExceptionListener} generated by error
   * in MQ publish framework.
   * @param e exception throw by MQ.
   */
  @Override
  public synchronized void onException(JMSException e) {
  
    _logger.exception(e);
  
    _isValid.set(false);
  }
  
  /**
   * Method to check whether adaptor object is valid.
   * @return boolean indicating validity.  
   */
  public boolean getIsValid() {

    return _isValid.get();
  
  }
  
  /**
   * boolean indicating whether this {@link ActiveMQServerAdaptor} object is in a valid
   * state.
   */
  private AtomicBoolean _isValid = new AtomicBoolean(false);
  
  /**
   * JMS Connection Factory.
   */
  private ConnectionFactory _factory = null;
  
  /**
   * JMS Connection.
   */
  private Connection _connection = null;
  
  /**
   * JMS Session.
   */
  private Session _session = null;
    
  /**
   * JMS Producer.
   */
  private MessageProducer _producer = null;
  
  /**
   * Local logger reference for logging operations.
   */
  final private ActiveMQServerLogger _logger = new ActiveMQServerLogger(ActiveMQServerAdaptor.class.getName());
}



