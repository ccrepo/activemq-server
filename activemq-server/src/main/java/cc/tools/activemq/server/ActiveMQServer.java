package cc.tools.activemq.server;

import javax.servlet.*;

import javax.servlet.http.*;
import java.io.*;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class implements a dynamic {@link javax.servlet.Servlet} service for
 * accessing Active MQ bus.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */

public class ActiveMQServer extends HttpServlet {

  /**
   * Auto-generated serialization ID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor for {@link ActiveMQServer}.
   * 
   * This method creates the JNI objects used within the class.
   */
  public ActiveMQServer() {

    super();
  
  }

  /**
   * Method performs servlet initialization.
   * 
   * @param config {@link javax.servlet.ServletConfig} provided by
   *               {@link javax.servlet.http.HttpServlet} container.
   * @throws ServletException .
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    
    super.init(config);

    _config.init(getServletContext());
    
    if (!_config.getIsValid()) {
      
      _logger.severe("config is not valid");
      
      return;
    }

    _logger.info("config is valid");

    _security.init(getServletContext());
    
    if (!_security.getIsValid()) {
      
      _logger.severe("security is not valid");
      
      return;
    }

    _logger.info("security is valid");

    if (!_connection.init()) {
      
      _logger.severe("connection is not valid");
      
      return;
    }

    _logger.info("connection is valid");

    _isValid = true;
  }

  /**
   * Override of 'service' {@link javax.servlet.http.HttpServlet} life cycle
   * method
   * {@link javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)}.
   * This method returns code {@value HttpURLConnection#HTTP_INTERNAL_ERROR} to
   * clients if {@link ActiveMQServer#_isValid} is false. Otherwise it calls the
   * overridden superclass method.
   * 
   * @param request  client {@link javax.servlet.http.HttpServletRequest} object.
   * @param response client {@link javax.servlet.http.HttpServletResponse} object.
   * @throws IOException      .
   * @throws ServletException .
   */
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    if (!_isValid) {

      _logger.severe_response(response, "servlet disabled as invalid.");

      response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);

      return;
    }

    super.service(request, response);
  }

  /**
   * Method implements this {@link javax.servlet.http.HttpServlet} handler for
   * Delete requests. This method overrides {@link javax.servlet.http.HttpServlet}
   * method
   * {@link javax.servlet.http.HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)}.
   * 
   * @param request  client {@link javax.servlet.http.HttpServletRequest} object.
   * @param response client {@link javax.servlet.http.HttpServletResponse} object.
   * @throws IOException      .
   * @throws ServletException .
   */
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    _logger.severe_response(response, 
        "http delete disabled.");

    response.setStatus(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
  }

  /**
   * Method implements this {@link javax.servlet.http.HttpServlet} handler for Get
   * requests. This method overrides {@link javax.servlet.http.HttpServlet} method
   * {@link javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)}.
   * 
   * @param request  client {@link javax.servlet.http.HttpServletRequest} object.
   * @param response client {@link javax.servlet.http.HttpServletResponse} object.
   * @throws IOException      .
   * @throws ServletException .
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {

    _logger.severe_response(response, 
        "http get disabled.");

    response.setStatus(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
  }

  /**
   * Method implements this {@link javax.servlet.http.HttpServlet} handler for Put
   * requests. This method overrides {@link javax.servlet.http.HttpServlet} method
   * {@link javax.servlet.http.HttpServlet#doPut(HttpServletRequest, HttpServletResponse)}.
   * 
   * @param request  client {@link javax.servlet.http.HttpServletRequest} object.
   * @param response client {@link javax.servlet.http.HttpServletResponse} object.
   * @throws IOException      .
   * @throws ServletException .
   */
  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {

    _logger.severe_response(response, 
        "http put disabled.");
    
    response.setStatus(HttpURLConnection.HTTP_NOT_IMPLEMENTED);
  }

  /**
   * Method implements this {@link javax.servlet.http.HttpServlet} handler for
   * Post requests. This method overrides {@link javax.servlet.http.HttpServlet}
   * method
   * {@link javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)}.
   * 
   * @param request  client {@link javax.servlet.http.HttpServletRequest} object.
   * @param response client {@link javax.servlet.http.HttpServletResponse} object.
   * @throws IOException      .
   * @throws ServletException .
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    
    String requestURI = request.getRequestURI();
    
    String requestContext = request.getContextPath();
    
    String clientIp = request.getRemoteAddr();

    if (isEndpointLogPost(requestContext, requestURI)) {

      if (doPostEndpointPost(request, response)) {
      
        _logger.info("doPost Post OK for client ",
            clientIp);
      
      } else {
       
        _logger.severe("doPost Post NOT ok for client ",
            clientIp);
      }

      return;
    }

    _logger.severe_response(response, 
        "invalid endpoint ",
        requestURI,
        " from ",
        clientIp);
            
    response.setStatus(HttpURLConnection.HTTP_BAD_METHOD);
  }

  /**
   * Method implements processing for Post Post endpoint. This method bounces the
   * client's header and ip back to the client. This checks that the client
   * credentials are valid.
   * 
   * @param request  client {@link javax.servlet.http.HttpServletRequest} object.
   * @param response client {@link javax.servlet.http.HttpServletResponse} object.
   * @return boolean true indicating success, false otherwise.
   * @throws IOException      .
   * @throws ServletException .
   */
  private boolean doPostEndpointPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String id = Long.toString(_id.getAndIncrement());

    Map<String, String> values = new HashMap<String, String>();

    if (!extractRequest(_HTTP_REQUEST_KEYS, request, values)) {

      response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      
      _logger.severe_response(response,
          "invalid fields in request from ",
          request.getRemoteAddr());

      return false;
    }

    if (!_security.validateCredentials(values.get(_HTTP_KEY_USER), values.get(_HTTP_KEY_PASSWORD))) {
      
      _logger.severe_response(response,
          "invalid credentials for user from client ",
          request.getRemoteAddr());
      
      return false;
    }
    
    if (!values.get(_HTTP_KEY_CHANNEL).matches(_pattern)) {
      
      response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      
      _logger.severe_response(response, "invalid channel name format.");
      
      return false;
    }
    
    Map<String, String> data = new HashMap<String, String>();

    StringBuilder code = new StringBuilder();
    
    StringBuilder mqid = new StringBuilder();
    
    if (!_connection.publish(id, 
        values.get(_HTTP_KEY_MESSAGE), 
        values.get(_HTTP_KEY_CHANNEL), 
        code, 
        mqid)) {
    
      data.put(_HTTP_KEY_CODE, (code.length() > 0 ? code.toString() : "-11"));
    
      data.put(_HTTP_KEY_MQID, (mqid.length() > 0 ? mqid.toString() : "0"));
    
    } else {
      
      data.put(_HTTP_KEY_CODE, "0");
      
      data.put(_HTTP_KEY_MQID, "0");      
    
    }
    
    data.put(_HTTP_KEY_HEADER, values.get(_HTTP_KEY_HEADER));
    
    data.put(_HTTP_KEY_ID, id);
    
    data.put(_HTTP_KEY_REMOTE, request.getRemoteAddr());

    String encoded = _security.encodeData(data);

    if (encoded == null ||
        encoded.isBlank()) {
      
      response.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      
      _logger.severe_response(response, "could not encode data.");
      
      return false;
    }

    response.setStatus(HttpURLConnection.HTTP_OK);

    response.getWriter().append(encoded);

    return true;
  }

  /**
   * Method checks if all fields in fields parameter are present in request
   * object and extracts values..
   * 
   * @param request client {@link javax.servlet.http.HttpServletRequest} object.
   * @param keys names of field values to be checked against request parameter.
   * @param values request data is returned to client in this collection.
   * @return boolean true indicating success, false otherwise.
   */
  private boolean extractRequest(String[] keys, HttpServletRequest request, Map<String, String> values) {
    
    boolean result = true;

    for (String key : keys) {

      String value = request.getParameter(key);
      
      if (value == null || 
          value.isBlank()) {

        _logger.severe("request missing field '",
            key,
            "' from client ",
            request.getRemoteAddr());
        
        result = false;
        
        continue;
      }
      
      try {
        
        String text = _security.decryptData(value);
        
        if (text == null || 
            text.isBlank()) {

          _logger.severe("request mangled value for key '",
              key,
              "' from client ",
              request.getRemoteAddr());

          result = false;
          
          continue;
        }
        
        values.put(key, text);
      
      } catch (Exception e) {
      
        _logger.exception(e);
        
        result = false;
      }
    }
        
    return result;
  }
  
  /**
   * Method returns boolean indicating whether Uri is the delete endpoint.
   * 
   * @param context contains request context.
   * @param uri     contains Uri path.
   * @return boolean indicating whether Uri is the log delete endpoint.
   **/
  @SuppressWarnings("unused")
  private boolean isEndpointLogDelete(String context, String uri) {
    return (context + _ENDPOINT_LOG_DELETE).compareToIgnoreCase(uri) == 0;
  }

  /**
   * Method returns boolean indicating whether Uri is the get endpoint.
   * 
   * @param context contains request context.
   * @param uri contains Uri path.
   * @return boolean indicating whether Uri is the log get endpoint.
   **/
  @SuppressWarnings("unused")
  private boolean isEndpointLogGet(String context, String uri) {
    return (context + _ENDPOINT_LOG_GET).compareToIgnoreCase(uri) == 0;
  }

  /**
   * Method returns boolean indicating whether Uri is the post endpoint.
   * 
   * @param context contains request context.
   * @param uri contains Uri path.
   * @return boolean indicating whether Uri is the log post endpoint.
   **/
  private boolean isEndpointLogPost(String context, String uri) {
    return (context + _ENDPOINT_LOG_POST).compareToIgnoreCase(uri) == 0;
  }

  /**
   * Method returns boolean indicating whether Uri is the put endpoint.
   * 
   * @param context contains request context.
   * @param uri contains Uri path.
   * @return boolean indicating whether Uri is the log put endpoint.
   **/
  @SuppressWarnings("unused")
  private boolean isEndpointLogPut(String context, String uri) {
    return (context + _ENDPOINT_LOG_PUT).compareToIgnoreCase(uri) == 0;
  }

  /**
   * Parameter constant for the {@link javax.servlet.http.HttpServlet} get log Uri
   * '{@value _ENDPOINT_LOG_DELETE}'.
   */
  final public static String _ENDPOINT_LOG_DELETE = "/server/logger/log/delete";

  /**
   * Parameter constant for the {@link javax.servlet.http.HttpServlet} get log Uri
   * '{@value _ENDPOINT_LOG_GET}'.
   */
  final public static String _ENDPOINT_LOG_GET = "/server/logger/log/get";

  /**
   * Parameter constant for the {@link javax.servlet.http.HttpServlet} get log Uri
   * '{@value _ENDPOINT_LOG_POST}'.
   */
  final public static String _ENDPOINT_LOG_POST = "/server/logger/log/post";

  /**
   * Parameter constant for the {@link javax.servlet.http.HttpServlet} get log Uri
   * '{@value _ENDPOINT_LOG_PUT}'.
   */
  final public static String _ENDPOINT_LOG_PUT = "/server/logger/log/put";

  /**
   * HTTP message key '{@value _HTTP_KEY_HEADER}'.
   */
  final public static String _HTTP_KEY_HEADER = "header";

  /**
   * HTTP message key '{@value _HTTP_KEY_CHANNEL}'
   * 
   */
  final public static String _HTTP_KEY_CHANNEL = "channel";

  /**
   * HTTP message key '{@value _HTTP_KEY_PID}'
   * 
   */
  final public static String _HTTP_KEY_PID = "pid";

  /**
   * HTTP message key '{@value _HTTP_KEY_PASSWORD}'
   * 
   */
  final public static String _HTTP_KEY_PASSWORD = "password";

  /**
   * HTTP message key '{@value _HTTP_KEY_USER}'.
   */ 
  final public static String _HTTP_KEY_USER = "user";

  /**
   * HTTP message key '{@value _HTTP_KEY_MESSAGE}'.
   */
  final public static String _HTTP_KEY_MESSAGE = "message";
  
  /**
   * Parameter array containing client request fields.
   */
  final public static String[] _HTTP_REQUEST_KEYS = new String[] { _HTTP_KEY_HEADER, 
      _HTTP_KEY_USER, 
      _HTTP_KEY_PASSWORD,
      _HTTP_KEY_CHANNEL,
      _HTTP_KEY_MESSAGE,
      _HTTP_KEY_PID };

  /**
   * HTTP message key '{@value _HTTP_KEY_ID}'.
   */
  final public static String _HTTP_KEY_ID = "id";

  /**
   * HTTP message key '{@value _HTTP_KEY_CODE}'.
   */
  final public static String _HTTP_KEY_CODE = "code";

  /**
   * HTTP message key '{@value _HTTP_KEY_REMOTE}'.
   */
  final public static String _HTTP_KEY_REMOTE = "remote";

  /**
   * HTTP message key '{@value _HTTP_KEY_MQID}'.
   */
  final public static String _HTTP_KEY_MQID = "mqid";

  /**
   * Parameter array containing client request fields.
   */
  final public static String[] _HTTP_RESPONSE_KEYS = new String[] { _HTTP_KEY_HEADER, 
      _HTTP_KEY_ID, 
      _HTTP_KEY_CODE,
      _HTTP_KEY_REMOTE,
      _HTTP_KEY_MQID };
  
  /**
   * boolean indicating whether this {@link ActiveMQServer} object is in a valid
   * state.
   */
  private boolean _isValid = false;

  /**
   * Variable counter to provide unique ID's for each operation.
   */
  private AtomicLong _id = new AtomicLong();

  /**
   * Logger object for logging operations.
   */
  final private static ActiveMQServerLogger _logger = new ActiveMQServerLogger(ActiveMQServer.class.getName());
  
  /**
   * Configuration object. 
   */
   final private static ActiveMQServerConfig _config = new ActiveMQServerConfig();
   
  /**
   * Security object for access to security functions.
   */
  final private static ActiveMQServerSecurity _security = new ActiveMQServerSecurity(_config);
  
  /**
   * MQ connection object.
   */
  private ActiveMQServerConnection _connection = new ActiveMQServerConnection(_config);
  
  /**
   * Regexp to validate channel name. 
   */
  private static final String _pattern = "^[/a-zA-Z0-9\\.]+$";
}
