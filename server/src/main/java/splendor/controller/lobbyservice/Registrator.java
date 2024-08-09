package splendor.controller.lobbyservice;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.AuthenticationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import splendor.controller.helper.TokenHelper;

/**
 * This class is used to register game server with the LS on startup.
 */
@Component
public class Registrator {
  private static final String REGISTRATION_RESOURCE = "/api/gameservices";
  private static final String SESSION_RESOURCE = "/api/sessions";
  private static final String SAVEGAME_RESOURCE = "/savegames";
  private static final String[] GAME_MODES = {"Splendor", "SplendorCities", "SplendorTraderoutes"};

  private final Logger logger = org.slf4j.LoggerFactory.getLogger(Registrator.class);

  private GameServiceParameters gameServiceParameters;

  private TokenHelper tokenHelper;

  private boolean registered = false;

  /**
   * Constructor.
   *
   * @param gameServiceParameters the game service parameters used to register with LS
   * @param tokenHelper helper to resolve tokens
   */
  public Registrator(@Autowired GameServiceParameters gameServiceParameters,
                     @Autowired TokenHelper tokenHelper) {
    this.gameServiceParameters = gameServiceParameters;
    this.tokenHelper = tokenHelper;
    logger.info("Instantiated with gameServiceParameters: " + gameServiceParameters.toJson());
  }

  /**
   * This method is called after the bean is created and all the properties are set.
   * It registers the game service with the lobby service.
   */
  @PostConstruct
  public void register() {
    // Register on a separate thread to avoid blocking the main thread.
    logger.info("Registering at lobby service.");
    new Thread(() -> {
      try {
        String token = tokenHelper.get(gameServiceParameters.getOauth2Name(),
                gameServiceParameters.getOauth2Password());
        registerGameServices(token);
        registered = true;
      } catch (UnirestException | AuthenticationException e) {
        logger.error("Failed to register with LS", e);
        System.exit(1);
      }
    }).start();
  }

  /**
   * Registers all game services with the LS.
   *
   * @param token the token to use for authentication
   */
  private void registerGameServices(String token) {
    try {
      for (String gameService : GAME_MODES) {
        gameServiceParameters.setName(gameService);
        logger.info("Registering game service: " + gameServiceParameters.getName());
        registerGameService(token);
      }
    } catch (UnirestException e) {
      logger.error("Could not register at lobby service. Shutting down.", e);
      System.exit(1);
    }
  }

  /**
   * Registers the game service with the lobby service.
   *
   * @param token the token to use for authentication
   * @throws UnirestException in case the request fails
   */
  private void registerGameService(String token) throws UnirestException {
    if (registered(token, gameServiceParameters.getName())) {
      logger.info("Already registered with LS. Skipping registration.");
      return;
    }
    String url =
            gameServiceParameters.getLobbyServiceLocation() + REGISTRATION_RESOURCE
            + "/" + gameServiceParameters.getName();
    HttpResponse<String> response = Unirest
            .put(url)
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .body(gameServiceParameters.toJson())
            .asString();
    if (response.getStatus() != 200) {
      logger.error("Could not register at lobby service.\n Status: {}\n Body: {}",
              response.getStatus(), response.getBody());
      throw new UnirestException("Could not register at lobby service.");
    }
    logger.info(
        "Successfully registered at lobby service game service: " + gameServiceParameters.getName()
    );
  }

  private boolean registered(String token, String name) {
    String url = gameServiceParameters.getLobbyServiceLocation() + REGISTRATION_RESOURCE
        + "/" + name;
    try {
      HttpResponse<String> response = Unirest
          .get(url)
          .header("Authorization", "Bearer " + token)
          .asString();
      if (response.getStatus() == 200) {
        logger.info("{} is already registered with LS.", name);
        return true;
      }
      return false;
    } catch (UnirestException e) {
      logger.error("Could not check registration status for {}.", name, e);
      throw new RuntimeException(e);
    }
  }

  /**
   * De-registers the game service from the lobby service. Called before the application exits.
   */
  @PreDestroy
  public void deregisterFromLobbyService() {
    if (!registered) {
      logger.info("Not registered with LS. Skipping de-registration.");
      return;
    }
    logger.info("De-registering from lobby service.");
    for (String gameService : GAME_MODES) {
      gameServiceParameters.setName(gameService);
      String url = gameServiceParameters.getLobbyServiceLocation() + REGISTRATION_RESOURCE
            + "/" + gameServiceParameters.getName();
      try {
        String token = tokenHelper.get(gameServiceParameters.getOauth2Name(),
            gameServiceParameters.getOauth2Password());
        HttpResponse<String> response = Unirest
            .delete(url)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + token)
            .asString();
        if (response.getStatus() != 200) {
          logger.error("Could not deregister from lobby service.\n Status: {}\n Body: {}",
              response.getStatus(), response.getBody());
          return;
        }
        logger.info(
            "Successfully deregistered {} from lobby service.", gameServiceParameters.getName());
        registered = false;
      } catch (UnirestException | AuthenticationException e) {
        logger.error("Could not deregister {} from lobby service.", gameServiceParameters.getName(),
            e);
      }
    }
  }

  /**
   * Saves game on LS.
   *
   * @param gameInfo game info
   * @throws RuntimeException in case the request fails
   */
  public void saveGame(GameInfo gameInfo) throws RuntimeException {
    String url =
        gameServiceParameters.getLobbyServiceLocation() + REGISTRATION_RESOURCE
            + "/" + gameInfo.getGameServer() + "/" + SAVEGAME_RESOURCE + "/"
            + gameInfo.getSavegame();
    try {
      String token = tokenHelper.get(gameServiceParameters.getOauth2Name(),
          gameServiceParameters.getOauth2Password());
      String gameInfoJson = gameInfo.toJson();
      logger.info("Saving game with LS. Game info: " + gameInfoJson);
      HttpResponse<String> response = Unirest
          .put(url)
          .header("Content-Type", "application/json")
          .header("Authorization", "Bearer " + token)
          .body(gameInfoJson)
          .asString();
      if (response.getStatus() != 200) {
        throw new UnirestException("Could not save game with LS." + response.getBody());
      }
      logger.info("Successfully saved game.");
    } catch (UnirestException | AuthenticationException e) {
      throw new RuntimeException("Could not save game with LS." + e);
    }
  }
}
