package cc.tools.activemq.server;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

/**
 * This class implements Servlet resource management.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class ActiveMQServerResource {

  /**
   * Constructor.
   */
  private ActiveMQServerResource() {  
  }
  
  /**
   * Method loads {@link javax.servlet.http.HttpServlet} resource indicated by
   * parameter uri value.
   * 
   * @param context Servlet context.
   * @param location contains path to {@link javax.servlet.http.HttpServlet}
   *                 resource to be loaded.
   * @param buffer   URI contents will be returned to caller in this parameter.
   * @return boolean true for success, false otherwise.
   */
  public static boolean loadResource(ServletContext context, String location, StringBuilder buffer) {
    
    InputStream configStream = null;

    try {

      configStream = context.getResourceAsStream(location);

      if (configStream == null) {

        _logger.severe("could not load resource '",
            location,
            "'");

        return false;
      }

      buffer.append(new String(configStream.readAllBytes()));

      configStream.close();

      return true;

    } catch (Exception e) {

      _logger.exception(e);
      
    }

    try {

      if (configStream != null) {
        configStream.close();
      }
      
    } catch (IOException e) {
      
      _logger.exception(e);
    
    }

    return false;
  }

  /**
   * Local logger reference for logging operations.
   */
  final private static ActiveMQServerLogger _logger = new ActiveMQServerLogger(ActiveMQServerResource.class.getName());
}

