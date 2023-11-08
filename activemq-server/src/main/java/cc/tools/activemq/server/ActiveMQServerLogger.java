package cc.tools.activemq.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

import javax.servlet.http.HttpServletResponse;

/**
 * This class implements logging.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class ActiveMQServerLogger {

  /**
   * Constructor {@link ActiveMQServerLogger}.
   * @param name Logger name. Usually expected to be the name of the owning class.
   */
  public ActiveMQServerLogger(String name) {
    _logger = Logger.getLogger(name);
  }
  
  /**
   * Method to report {@link java.util.logging.Level#INFO} message to server log
   * 
   * @param message {@link String} containing message to be logged.
   */
  public void info(String message) {
    log(Level.INFO, _info + message);
  }

  /**
   * Method to report {@link java.util.logging.Level#INFO} message to server log
   * 
   * @param messages {@link String} containing messages to be logged.
   */
  public void info(String ... messages) {
    StringBuilder buffer = new StringBuilder();
    
    for (String message : messages) {
   
      buffer.append(message);
    
    }
    
    log(Level.INFO, _info + buffer.toString());
  }

  /**
   * Method to report {@link java.util.logging.Level#SEVERE} message to server log
   * 
   * @param message {@link String} containing message to be logged.
   */
  public void severe(String message) {
    log(Level.SEVERE, _severe + message);
  }

  /**
   * Method to report {@link java.util.logging.Level#SEVERE} message to server log
   * 
   * @param messages {@link String} containing messages to be logged.
   */
  public void severe(String ... messages) {
    StringBuilder buffer = new StringBuilder();
    
    for (String message : messages) {
    
      buffer.append(message);
    
    }
    
    log(Level.SEVERE, 
        _severe + buffer.toString());
  }

  /**
   * Method to report {@link java.util.logging.Level#SEVERE} messages to server log
   * and Servlet response.
   * 
   * @param response Servlet response.
   * @param messages {@link String} containing messages to be logged.
   */
  public void severe_response(HttpServletResponse response, String ... messages) {
    StringBuilder buffer = new StringBuilder();
    
    for (String message : messages) {
      
      buffer.append(message);
    
    }
    
    severe_response(response, 
        buffer.toString());
  }
  
  /**
   * Method to report {@link java.util.logging.Level#SEVERE} message to server log
   * and Servlet response.
   * 
   * @param response Servlet response.
   * @param message {@link String} containing message to be logged.
   */
  public void severe_response(HttpServletResponse response, String message) {

    try {
      
      response.getWriter().append(message);

      severe(message);
    
    } catch (Exception e) {
    
      exception(e);
      
    }
  }
  
  /**
   * Method output exception level text.
   * @param e Exception to be output.
   */
  public void exception(Exception e) {
    StringBuilder buffer = new StringBuilder();

    buffer.append(e.toString());
    buffer.append(" ");
    buffer.append(getStackTraceAsString(e));
    
    log(Level.SEVERE, 
        _exception + buffer.toString());
  }

  /**
   * Method to log fully constructed message to log object.
   * @param level log level.
   * @param message String text to be output.
   */
  private void log(Level level, String message) {
    _logger.log(level, 
        prefix() + 
        " " +
        _logger.getName() +
        " " +
        message);
  }
  
  /**
   * Method output log line prefix.
   * @return String containing output prefix.
   */
  private String prefix() {
    return ActiveMQServerTime.now();
  }

  /**
   * Method returns a String containing stack trace from Throwable parameter t.
   * 
   * @param t {@link java.lang.Throwable} object containing stack trace
   * @return String containing stack trace of throwable parameter
   */
  private String getStackTraceAsString(Throwable t) {

    StringWriter stringWriter = new StringWriter();
    
    PrintWriter printWriter = new PrintWriter(stringWriter, true);

    t.printStackTrace(printWriter);

    return stringWriter.getBuffer().toString();
  }

  /**
   * Container logger.
   */
  private Logger _logger = null;
 
  /**
   * Output level text for error.
   */
  private String _severe = "severe: ";
  
  /**
   * Output level text for info.
   */
  private String _info = "info: ";

  /**
   * Output level text for exception.
   */
  private String _exception = "exception: ";
}

