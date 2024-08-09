package splendor.controller.helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javax.naming.AuthenticationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import splendor.controller.lobbyservice.LogicException;
import splendor.controller.lobbyservice.Role;

/**
 * Inspired from BoardGamePlatform TokenResolver class. This class is used to facilitate the
 * validation of authentication tokens.
 */
@Component
public class TokenHelper {

  private static final String ROLE_RESOURCE = "/oauth/role";
  private static final String NAME_RESOURCE = "/oauth/username";

  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TokenHelper.class);

  @Value("${lobbyservice.location}")
  private String lobbyServiceLocation;

  /**
   * Gets the name of the player associated with the provided token.
   *
   * @param token OAuth2 access token
   * @return the name of the user associated with the provided token
   * @throws LogicException   in case the lobby service throws an exception
   * @throws UnirestException in case the request to the lobby service failed
   */
  public String name(String token) throws LogicException, UnirestException {
    HttpResponse<String> response = Unirest
            .get(lobbyServiceLocation + NAME_RESOURCE)
            .header("Authorization", "Bearer " + token)
            .asString();
    if (response.getStatus() != 200) {
      throw new LogicException("Could not resolve token to name.");
    }
    return response.getBody();
  }

  /**
   * Gets the role of the player associated with the provided token.
   *
   * @param token OAuth2 access token
   * @return the role of the user associated with the provided token
   * @throws AuthenticationException   in case the lobby service throws an exception
   * @throws UnirestException in case the request to the lobby service failed
   */
  public String role(String token) throws AuthenticationException, UnirestException {
    HttpResponse<String> response = Unirest
            .get(lobbyServiceLocation + ROLE_RESOURCE)
            .header("Authorization", "Bearer " + token)
            .asString();
    if (response.getStatus() != 200) {
      logger.error("Could not resolve token to role.");
      throw new AuthenticationException("Could not resolve token to role.");
    }
    return response.getBody();
  }

  /**
   * Checks whether the provided token has the desired role.
   *
   * @param role the role of the user
   * @param desiredRole the role to check for
   * @return true if the token has the desired role, false otherwise
   */
  private boolean hasRole(String role, Role desiredRole) {
    return role.toLowerCase().contains(desiredRole.toString().toLowerCase());
  }

  /**
   * Checks whether the provided token is valid for the provided username.
   *
   * @param token OAuth2 access token
   * @param username the username of the user
   * @return true if the token is valid for the provided username, false otherwise
   */
  public boolean validate(String token, String username) {
    try {
      boolean result = name(token).equalsIgnoreCase(username);
      logger.info("Token validation result: " + result);
      return result;
    } catch (LogicException | UnirestException e) {
      return false;
    }
  }

  /**
   * Checks whether the provided token is valid for the provided username AND that the user has
   * the desired role.
   *
   * @param token OAuth2 access token
   * @param username the username of the user
   * @param role the role of the user to check for
   * @return true if the token is valid for the provided username, false otherwise
   */
  public boolean validate(String token, String username, Role role) {
    try {
      boolean result = validate(token, username) && hasRole(role(token), role);
      logger.info("Token validation result: " + result);
      return result;
    } catch (AuthenticationException | UnirestException e) {
      return false;
    }
  }

  /**
   * Checks whether the provided token belongs to a player.
   *
   * @param token OAuth2 access token
   * @return true if the token belongs to a player, false otherwise
   */
  public boolean isPlayer(String token) {
    try {
      String role = role(token);
      return hasRole(role, Role.PLAYER) || hasRole(role, Role.ADMIN);
    } catch (AuthenticationException | UnirestException e) {
      return false;
    }
  }

  /**
   * Gets access token from the Lobby Service.
   *
   * @param username the username of the user
   * @param password the password of the user
   * @return the access token
   * @throws UnirestException in case the request to the lobby service failed
   * @throws AuthenticationException in case the provided credentials are invalid
   */
  public String get(String username, String password) throws UnirestException,
          AuthenticationException {
    logger.info("Fetching token for {} at {}", username, lobbyServiceLocation + "/oauth/token");
    HttpResponse<String> response = Unirest
            .post(lobbyServiceLocation + "/oauth/token")
            .header("Authorization", "Basic YmdwLWNsaWVudC1uYW1lOmJncC1jbGllbnQtcHc=")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .field("grant_type", "password")
            .field("username", username)
            .field("password", password)
            .asString();
    if (response.getStatus() != 200) {
      throw new AuthenticationException(String.format("Could not authenticate user %s with "
              + "provided credentials.", username));
    }
    // To escape special characters in the token
    JsonObject responseJson = JsonParser.parseString(response.getBody()).getAsJsonObject();
    String token =  responseJson.get("access_token").toString().replaceAll("\"", "");
    logger.info("Fetched token {} for user {}", token, username);
    return token;
  }
}

