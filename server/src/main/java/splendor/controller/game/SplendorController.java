package splendor.controller.game;

import com.google.gson.Gson;
import eu.kartoffelquadrat.asyncrestlib.BroadcastContentManager;
import eu.kartoffelquadrat.asyncrestlib.ResponseGenerator;
import java.util.logging.Logger;
import javax.naming.AuthenticationException;
import javax.naming.InsufficientResourcesException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import splendor.controller.action.ActionData;
import splendor.controller.action.InvalidAction;
import splendor.controller.helper.Authenticator;
import splendor.controller.lobbyservice.Registrator;
import splendor.model.game.Board;

/**
 * Controller responsible for all HTTP requests specific to a game.
 */
@RestController
public class SplendorController {
  private static final Logger LOGGER = Logger.getLogger(SplendorController.class.getName());
  private final GameManager gameManager;
  private final Authenticator authenticator;
  private final Registrator registrator;
  private final int longPollTimeout = 60000;

  /**
   * Constructor.
   *
   * @param gameManager the game manager.
   * @param authenticator the authenticator.
   * @param registrator the registrator.
   */
  public SplendorController(@Autowired GameManager gameManager,
                            @Autowired Authenticator authenticator,
                            @Autowired Registrator registrator) {
    this.gameManager = gameManager;
    this.authenticator = authenticator;
    this.registrator = registrator;
  }

  /**
   * Validate all requests have correct access token.
   * Validate all requests have correct game id, if applicable.
   * This method is called before any request is processed.
   *
   * @param username the username.
   * @param accessToken the accessToken for the user.
   * @param url of the request. Used for logging only.
   * @return boolean if succeeded or not
   */
  public boolean authenticate(String username, String accessToken, String url) {
    LOGGER.info(String.format("Received request to %s with access token %s and username %s",
        url, accessToken, username));
    // Check if access token is valid
    try {
      authenticator.authenticate(accessToken, username);
    } catch (AuthenticationException e) {
      LOGGER.warning(e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Gets the board of a specific game. All information is shared with all players, so all
   * players get the same view of the board.
   *
   * @param gameId the id of the game.
   * @param request the HttpServletRequest
   * @param accessToken the access token of the player
   * @param username the username of the player
   * @return response entity
   */
  @GetMapping(value = "/api/games/{gameId}/board")
  public ResponseEntity getBoard(@PathVariable long gameId, HttpServletRequest request,
                                 @RequestParam("access_token") String accessToken,
                                 @RequestParam("username") String username) {
    LOGGER.info(String.format("Received request to get board of game with id %d", gameId));
    if (!authenticate(username, accessToken, request.getRequestURI())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "Invalid access token for user " + username);
    }
    if (!gameManager.exists(gameId)) {
      LOGGER.warning(String.format("Game with id %d does not exist", gameId));
      return ResponseEntity.badRequest().body(String.format("Game with id %d does not exist",
          gameId));
    }

    LOGGER.info(String.format("Returning board of game with id %d", gameId));
    String body = new Gson().toJson(gameManager.getBoard(gameId));
    return ResponseEntity.ok().body(body);
  }

  /**
   * Get the board of a specific game. Use long polling to wait for the board to change.
   *
   * @param gameId the id of the game.
   * @param hash hash
   * @param username username of player
   * @param accessToken access token of player
   * @param request HttpServletRequest of game
   * @return the board of the game.
   */
  @GetMapping(value = "/api/games/{gameId}/board/longpoll")
  public DeferredResult<ResponseEntity<String>>
      getBoardLongPoll(@PathVariable long gameId,
                      @RequestParam(required = false) String hash,
                      @RequestParam("username") String username,
                      @RequestParam("access_token") String accessToken,
                      HttpServletRequest request) {
    LOGGER.info(String.format("Received long poll request for board of game with id %d", gameId));
    DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
    if (!authenticate(username, accessToken, request.getRequestURI())) {
      result.setResult(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "Invalid access token for user " + username));
      return  result;
    }
    if (!gameManager.exists(gameId)) {
      LOGGER.warning(String.format("Game with id %d does not exist", gameId));
      result.setResult(ResponseEntity.badRequest().body(String.format(
          "Game with id %d does not exist", gameId)));
      return result;
    }
    BroadcastContentManager<Board> broadcastContentManager = gameManager.getBoardManager(gameId);
    if (hash == null || hash.isEmpty()) {
      result = ResponseGenerator.getAsyncUpdate(longPollTimeout, broadcastContentManager);
    } else {
      result = ResponseGenerator.getHashBasedUpdate(longPollTimeout, broadcastContentManager, hash);
    }
    LOGGER.info(String.format("Returning board of game with id %d", gameId));
    return result;
  }

  /**
   * Generates all the actions that can be performed by the player.
   * This is used by the client to generate the buttons that the player can click.
   *
   * @param gameId the id of the game.
   * @param username username of player
   * @param accessToken access token of player
   * @param request HttpServletRequest of game
   * @param usernameParam username
   * @return a list of actions that the player can perform.
   */
  @GetMapping("/api/games/{gameId}/players/{username}/actions")
  public ResponseEntity getActions(@PathVariable long gameId, @PathVariable String username,
                                   @RequestParam("username") String usernameParam,
                                   @RequestParam("access_token") String accessToken,
                                   HttpServletRequest request) {
    LOGGER.info(String.format("Received request to get actions of player %s in game with id %d",
        username, gameId));
    if (!usernameParam.equals(username)) {
      LOGGER.warning(String.format("Cannot get actions of a different user. Requested %s, got %s",
          username, usernameParam));
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "Cannot get actions of a different user"
      );
    }
    if (!authenticate(username, accessToken, request.getRequestURI())) {
      LOGGER.warning(String.format("Invalid access token for user %s", username));
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "Invalid access token for user " + username);
    }
    if (!gameManager.exists(gameId)) {
      LOGGER.warning(String.format("Game with id %d does not exist", gameId));
      return ResponseEntity.badRequest().body(String.format("Game with id %d does not exist",
          gameId));
    }
    if (!gameManager.playerInGame(gameId, username)) {
      LOGGER.warning(String.format("Player %s is not in game with id %d", username, gameId));
      return ResponseEntity.badRequest().body(String.format("Player %s is not in game with id %d",
          username, gameId));
    }
    String body = new Gson().toJson(gameManager.generateActions(gameId, username));
    LOGGER.info(String.format("Returning actions of player %s in game with id %d",
        username, gameId));
    return ResponseEntity.ok().body(body);
  }

  /**
   * Performs a previously generated action.
   *
   * @param gameId     the id of the game.
   * @param username   the username of the player.
   * @param actionId   the id of the action.
   * @param request    the HttpServletRequest
   * @param accessToken the access token of player
   * @param usernameParam the username as a parameter
   * @return response entity
   */
  @PostMapping("/api/games/{gameId}/players/{username}/actions/{actionId}")
  public ResponseEntity performAction(@PathVariable long gameId,
                                      @PathVariable String actionId,
                                      @PathVariable String username,
                                      @RequestParam("username") String usernameParam,
                                      @RequestParam("access_token") String accessToken,
                                      HttpServletRequest request) {
    LOGGER.info(String.format("Received request to perform action %s of player %s in game %d",
        actionId, username, gameId));
    if (!usernameParam.equals(username)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "Cannot perform actions of a different user"
      );
    }
    if (!authenticate(username, accessToken, request.getRequestURI())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "Invalid access token for user " + username);
    }
    if (!gameManager.exists(gameId)) {
      return ResponseEntity.badRequest().body(String.format("Game with id %d does not exist",
          gameId));
    }
    if (!gameManager.playerInGame(gameId, username)) {
      return ResponseEntity.badRequest().body(String.format("Player %s is not in game with id %d",
          username, gameId));
    }
    try {
      ActionData actionData = new ActionData(); // dummy for now
      gameManager.performAction(gameId, username, actionId, actionData);
      LOGGER.info(String.format("Performed action %s of player %s in game with id %d",
          actionId, username, gameId));
      return ResponseEntity.ok().build();
    } catch (InvalidAction | InsufficientResourcesException e) {
      LOGGER.warning(e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Saves the game.
   *
   * @param gameId the id of the game.
   * @param username username
   * @param accessToken access token
   * @param request request
   * @return response entity
   */
  @PostMapping("/api/games/{gameId}/save")
  public ResponseEntity saveGame(@PathVariable long gameId,
                                 @RequestParam("username") String username,
                                 @RequestParam("access_token") String accessToken,
                                 HttpServletRequest request) {
    LOGGER.info(String.format("Received request to save game with id %d", gameId));
    if (!authenticate(username, accessToken, request.getRequestURI())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
          "Invalid access token for user " + username);
    }
    if (!gameManager.exists(gameId)) {
      return ResponseEntity.badRequest().body(String.format("Game with id %d does not exist",
          gameId));
    }
    if (!gameManager.playerInGame(gameId, username)) {
      return ResponseEntity.badRequest().body(String.format("Player %s is not in game with id %d",
          username, gameId));
    }
    try {
      gameManager.saveGame(gameId);
      LOGGER.info(String.format("Saved game with id %d", gameId));
      return ResponseEntity.ok().build();
    } catch (RuntimeException e) {
      e.printStackTrace();
      LOGGER.warning(e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Deregister from LS.
   *
   * @param request the HttpServletRequest
   * @param accessToken the access token
   * @param username player username
   * @return the ResponseEntity
   */
  @PostMapping("/api/deregister")
  public ResponseEntity deregister(@RequestParam("username") String username,
                                   @RequestParam("access_token") String accessToken,
                                   HttpServletRequest request) {
    LOGGER.info(String.format("Received request to deregister by user %s", username));
    try {
      authenticator.authenticateAdmin(accessToken, username);
      registrator.deregisterFromLobbyService();
      LOGGER.info("Deregistered from LS");
      return ResponseEntity.ok().build();
    } catch (AuthenticationException e) {
      LOGGER.warning(e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
  }
}
