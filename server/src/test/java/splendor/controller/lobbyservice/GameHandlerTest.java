package splendor.controller.lobbyservice;

import org.junit.Before;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import splendor.controller.game.GameManager;
import splendor.controller.action.ActionGenerator;
import splendor.model.game.SaveGameManager;
import splendor.model.game.player.Player;

@ExtendWith(MockitoExtension.class)
public class GameHandlerTest {
  private final GameManager gameManager = Mockito.mock(GameManager.class);
  private final GameHandler gameHandler;
  private final long gameId = 1;
  private final GameInfo gameInfo;

  public GameHandlerTest() {
    Player[] players = new Player[4];
    players[0] = new Player("player1", "blue");
    players[1] = new Player("player2", "red");
    players[2] = new Player("player3", "green");
    players[3] = new Player("player4", "yellow");
    gameInfo = new GameInfo("gameServer", players[0].getName(), players,
        null);
    this.gameHandler = new GameHandler(gameManager);
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    doNothing().when(gameManager).createGame(any(), any());
  }

  @Test
  public void testStartGameSuccess() {
    gameHandler.startGame(gameId, gameInfo);
    verify(gameManager).createGame(gameInfo, gameId);
  }

  @Test
  public void testStartGameFailureNullGameInfo() {
    doThrow(new IllegalArgumentException()).when(gameManager).createGame(eq(null),
        any(Long.class));
    ResponseEntity response = gameHandler.startGame(gameId, null);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void testDeleteGameSuccess() {
    gameHandler.startGame(gameId, gameInfo);
    gameHandler.deleteGame(gameId);
    verify(gameManager).deleteGame(gameId);
  }

  @Test
  public void testDeleteGameWhenGameDoesNotExist() {
    doThrow(new IllegalArgumentException()).when(gameManager).deleteGame(any(Long.class));
    ResponseEntity response = gameHandler.deleteGame(gameId);
    assertEquals(400, response.getStatusCodeValue());
  }
}
