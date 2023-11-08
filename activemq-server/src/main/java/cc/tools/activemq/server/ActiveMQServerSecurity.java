package cc.tools.activemq.server;

import java.security.*;
import java.security.spec.*;
import java.util.*;
import java.lang.reflect.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.servlet.ServletContext;

/**
 * This class implements logging.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class ActiveMQServerSecurity {

  /**
   * Constructor.
   * @param config Configuration object.
   */
  public ActiveMQServerSecurity(ActiveMQServerConfig config) {
    
    _config = config;
    
  }

  /**
   * Initialize security object.
   * @param context Servlet context.
   */
  public void init(ServletContext context) {
    
    try {
          
      KeyFactory keyFactory = KeyFactory.getInstance(_SECURITY_KEY_ALGORITHM_RSA);

      if (keyFactory == null) {
        
        _logger.severe("key factory could not be created.");

        return;
      }

      _keyFactory = keyFactory;
      
      if (!getKeyServerPrivate(context)) {
        
        _logger.severe("could not create server key.");
        
        return;
      }
      
      if (!getUserCredentials(context)) {
        
        _logger.severe("could not load credentials.");
        
        return;
      }
      
      _isValid = true;

    } catch (Exception e) {
      
      _logger.exception(e);

    }
  }
  
  /**
   * Method initializes the server's {@link java.security.PrivateKey}.
   * 
   * @param context Servlet context.
   * @return boolean indicating whether the {@link java.security.PrivateKey} was
   *         successfully initialized.
   */
  private boolean getKeyServerPrivate(ServletContext context) {
    
    StringBuilder buffer = new StringBuilder();

    if (!ActiveMQServerResource.loadResource(context, _config.getPrivateKeyLocation(), buffer)) {
      
      _logger.severe("unable to load key location ",
          _config.getPrivateKeyLocation(),
          ".");
      
      return false;
    }

    Key privateKey = getKeyFromText(buffer.toString(), 
        PKCS8EncodedKeySpec.class, 
        "PRIVATE", 
        "generatePrivate");

    if (privateKey != null && 
        privateKey instanceof PrivateKey) {
    
      _serverPrivateKey = (PrivateKey) privateKey;
      
      return true;
    }

    _logger.severe("could not init private key.");
    
    return false;
  }
  
  /**
   * Method initializes the server's client user/password sets.
   * Only for this simplistic authorization.
   * 
   * @param context Servlet context.
   * @return boolean indicating whether the user/password was loaded.
   */
  private boolean getUserCredentials(ServletContext context) {
    
    String location = _config.getCredentialsLocation();

    if (location == null) {
      
      _logger.severe("could not load resource key ",
          _config.getCredentialsLocation(),
          ". check servlet configuration.");
      
      return false;
    }

    StringBuilder buffer = new StringBuilder();

    if (!ActiveMQServerResource.loadResource(context, location, buffer)) {
      
      _logger.severe("unable to load key location ",
          location,
          ".");
      
      return false;
    }

    if (!initCredentialsFromText(buffer.toString())) {
      _logger.severe("security failed credentials public key");
      
      return false;
    }
    
    return true;
  }
  
  /**
   * Method sets client's server login credentials from value in text parameter.
   * 
   * @param text location of file containing credentials.
   * @return boolean indicating success or failure.
   */
  private boolean initCredentialsFromText(String text) {

    if (text == null) {
      return false;
    }

    String data = text.trim();

    if (data.isBlank()) {
      _logger.severe("credentials data is empty");
      
      return false;
    }

    String credentials = decryptData(data);

    if (credentials == null) {
      _logger.severe("could not decrypt credentials");
      
      return false;
    }

    String[] lines = credentials.trim().split("\n");

    if (lines.length != 2) {
      _logger.severe("wrong number of lines in credentials");
      
      return false;
    }

    String user = lines[0].trim();

    if (user.isBlank()) {
      _logger.severe("user credential invalid");
      
      return false;
    }

    String password = lines[1].trim();

    if (password.isBlank()) {
      _logger.severe("password credential empty");
      
      return false;
    }

    _user = user;

    _password = password;

    return true;
  }
  
  /**
   * Method loads a {@link java.security.PrivateKey} or
   * {@link java.security.PublicKey} from a {@link javax.servlet.http.HttpServlet}
   * Uri path.
   * 
   * @param text     {@link java.security.Key} data base64 encoded.
   * @param keyClass class that should be used to create {@link java.security.Key}
   *                 object.
   * @param pattern  pattern for removal from Key base64 text header and footer.
   * @param function {@link java.security.KeyFactory} key generation method name..
   * @return {@link java.security.Key} created if success, else null.
   **/
  private Key getKeyFromText(String text, Class<?> keyClass, String pattern, String function) {

    byte[] decoded = Base64.getDecoder()
        .decode(text.replaceAll("\\n", "").replaceAll("-----BEGIN " + pattern + " KEY-----", "")
            .replaceAll("-----END " + pattern + " KEY-----", "").trim());

    try {

      Constructor<?> constructor = keyClass.getDeclaredConstructor(decoded.getClass());

      Object object = constructor.newInstance(new Object[] { decoded });

      if (object instanceof KeySpec) {

        KeySpec keySpec = (KeySpec) object;

        Method method = _keyFactory.getClass().getDeclaredMethod(function, KeySpec.class);

        object = method.invoke(_keyFactory, keySpec);

        if (object instanceof Key) {
          return (Key) object;
        }
        
      }

    } catch (Exception e) {

      _logger.exception(e);

    }
    
    return null;
  }
  
  /**
   * Performs decryption of encryptedData parameter and returns plain-text.
   * 
   * @param data encrypted base64 data to be decrypted.
   * @return String containing plain text result of decryption if success, else
   *         null.
   */
  public String decryptData(String data) {

    try {

      Cipher cipher = Cipher.getInstance(_SECURITY_KEY_CYPHER_TRANSFORMATION);

      cipher.init(Cipher.DECRYPT_MODE, _serverPrivateKey);

      return new String(cipher.doFinal(Base64.getDecoder().decode(data)));

    } catch (Exception e) {
      
      _logger.exception(e);
    
    }

    return null;
  }
  
  /**
   * Method Url encodes data.
   * 
   * @param data name value pairs in Map to be encoded
   * @return String of encoded data.
   */
  public String encodeData(Map<String, String> data) {

    StringBuilder buffer = new StringBuilder();

    for (Map.Entry<String, String> entry : data.entrySet()) {

      if (!buffer.isEmpty()) {
        buffer.append("&");
      }

      buffer.append(URLEncoder.encode(entry.getKey().toString(), 
          StandardCharsets.UTF_8));
      
      buffer.append("=");
      
      buffer.append(URLEncoder.encode(entry.getValue().toString(), 
          StandardCharsets.UTF_8));
    }

    return buffer.toString();
  }
  
  /**
   * Method checks credentials.
   *  
   * @param user user ID
   * @param password User password
   * @return boolean true if user credentials are correct, false otherwise.
   */
  public boolean validateCredentials(String user, String password) {
    return user.equalsIgnoreCase(_user) &&
        _password.equalsIgnoreCase(_password);
  }

  /**
   * {@link java.security.KeyFactory} algorithm '{@value _SECURITY_KEY_ALGORITHM_RSA}'.
   */
  final public static String _SECURITY_KEY_ALGORITHM_RSA = "RSA";

  /**
   * {@link javax.crypto.Cipher} transformation type ActiveMQServerConfig
   * '{@value _SECURITY_KEY_CYPHER_TRANSFORMATION}'.
   */
  final public static String _SECURITY_KEY_CYPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

  /**
   * KeyFactory used to generate RSA security keys.
   */
  private KeyFactory _keyFactory = null;
 
  /**
   * Server {@link java.security.PrivateKey}
   */
  private PrivateKey _serverPrivateKey = null;

  /**
   * Client's user name on server
   */
  private String _user = null;

  /**
   * Client's password on server
   */
  private String _password = null;
  
  /**
   * Method to check whether security object is valid.
   * @return boolean indicating validity.  
   */
  public boolean getIsValid() {
    return _isValid;
  }
  
  /**
   * boolean indicating whether this {@link ActiveMQServerSecurity} object is in a valid
   * state.
   */
  private boolean _isValid = false;

  /**
   * Local logger reference for logging operations.
   */
  final private static ActiveMQServerLogger _logger = new ActiveMQServerLogger(ActiveMQServerSecurity.class.getName());
 
  /**
   * Configuration object. 
   */
  private ActiveMQServerConfig _config = null;

}
