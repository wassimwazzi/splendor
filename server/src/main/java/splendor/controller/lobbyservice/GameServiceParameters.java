package splendor.controller.lobbyservice;

import com.google.gson.Gson;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Bean to encapsulate the parameters of registration of a game service with the lobby service.
 */
@Component
public class GameServiceParameters {
  @Value("${lobbyservice.location}")
  private String lobbyServiceLocation;

  @Value("${gameservice.display.name}")
  private String gameServiceDisplayName;

  @Value("${gameservice.name}")
  private String gameServiceName;

  @Value("${gameservice.oauth2.name}")
  private String oauth2Name;

  @Value("${gameservice.oauth2.password}")
  private String oauth2Password;

  @Value("${gameservice.location}")
  private String gameServiceLocation;

  @Value("${gameservice.min.players}")
  private int minPlayers;

  @Value("${gameservice.max.players}")
  private int maxPlayers;

  @Value("${gameservice.web.support}")
  private boolean webSupport;

  /**
   * Formats the parameters as a JSON string.
   * The required params by the LS are:
   * "location": "http://127.0.0.1:4243/DummyGameService",
   * "maxSessionPlayers": 5,
   * "minSessionPlayers": 3,
   * "name": "DummyGame1",
   * "displayName": "Dummy Game 1",
   * "webSupport": "true"
   *
   * @return the parameters as a JSON string
   */
  public String toJson() {
    HashMap<String, String> map = new HashMap<>();
    map.put("location", gameServiceLocation);
    map.put("maxSessionPlayers", Integer.toString(maxPlayers));
    map.put("minSessionPlayers", Integer.toString(minPlayers));
    map.put("name", gameServiceName);
    map.put("displayName", gameServiceDisplayName);
    map.put("webSupport", Boolean.toString(webSupport));
    return new Gson().toJson(map);
  }

  /**
   * Getter for game service name.
   *
   * @return the name
   */
  public String getName() {
    return gameServiceName;
  }

  /**
   * Change the name to handle multiple game modes.
   *
   * @param name the name to give to the game service and game service display
   */
  public void setName(String name) {
    this.gameServiceName = name;
    this.gameServiceDisplayName = name;
  }

  /**
   * Getter for the lobby service url and port.
   *
   * @return The lobby service location
   */
  public String getLobbyServiceLocation() {
    return lobbyServiceLocation;
  }

  /**
   * Getter for game service display name.
   *
   * @return The name
   */
  public String getGameServiceDisplayName() {
    return gameServiceDisplayName;
  }

  /**
   * Getter for game service name.
   *
   * @return The name
   */
  public String getGameServiceName() {
    return gameServiceName;
  }

  /**
   * Getter for oauth2 name.
   *
   * @return The name
   */
  public String getOauth2Name() {
    return oauth2Name;
  }

  /**
   * Getter for oauth2 password.
   *
   * @return The password
   */
  public String getOauth2Password() {
    return oauth2Password;
  }
}
