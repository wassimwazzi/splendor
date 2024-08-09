package splendor.controller.game;

import eu.kartoffelquadrat.asyncrestlib.BroadcastContentManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import javax.naming.InsufficientResourcesException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import splendor.controller.action.Action;
import splendor.controller.action.ActionData;
import splendor.controller.action.ActionGenerator;
import splendor.controller.action.InvalidAction;
import splendor.controller.action.ReserveCardAction;
import splendor.controller.lobbyservice.GameInfo;
import splendor.controller.lobbyservice.Registrator;
import splendor.model.game.Board;
import splendor.model.game.SaveGameManager;
import splendor.model.game.SplendorGame;
import splendor.model.game.TokenBank;
import splendor.model.game.card.DevelopmentCard;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.player.Player;

public class GameManagerTest {
  ActionGenerator actionGenerator = mock(ActionGenerator.class);
  Registrator registrator = mock(Registrator.class);
  SaveGameManager saveGameManager = mock(SaveGameManager.class);

  ActionData actionData = mock(ActionData.class);
  private final GameManager gameManager = new GameManager(actionGenerator, saveGameManager,
      registrator);
  private final long gameId = 1;
  private final Player[] players = new Player[4];
  private final GameInfo gameInfo;

  public GameManagerTest() {
    players[0] = new Player("player1", "blue");
    players[1] = new Player("player2", "red");
    players[2] = new Player("player3", "green");
    players[3] = new Player("player4", "yellow");
    gameInfo = new GameInfo("gameServer", players[0].getName(), players,
        "");
  }

  private static void fillPlayerInventory(Player player) throws NoSuchFieldException,
      IllegalAccessException {
    // use reflection to fill the inventory
    Field inventory = player.getClass().getDeclaredField("inventory");
    Field tokens = inventory.getType().getDeclaredField("tokens");
    tokens.setAccessible(true);
    tokens.set(inventory, new TokenBank(true));
  }

  private static SplendorGame getGame(GameManager gameManager, long gameId) throws
      NoSuchFieldException, IllegalAccessException {
    Field games = gameManager.getClass().getDeclaredField("games");
    games.setAccessible(true);
    return ((HashMap<Long, SplendorGame>) games.get(gameManager)).get(gameId);
  }

  @Before
  public void setUp() throws InvalidAction, InvocationTargetException, InstantiationException, IllegalAccessException {
    // use reflection to make DevelopmentCardAction constructor public
    Constructor<ReserveCardAction> constructor;
    try {
      constructor = ReserveCardAction.class
          .getDeclaredConstructor(SplendorCard.class);
      constructor.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    Action action = constructor.newInstance(DevelopmentCard.get(1));
    when(actionGenerator.generateActions(Mockito.any(), Mockito.anyLong(), Mockito.any()))
        .thenReturn(Collections.singletonList(action));
    when(actionGenerator.getGeneratedAction(Mockito.anyLong(), Mockito.anyLong()))
        .thenReturn(action);
    doNothing().when(saveGameManager).saveGame(any());
  }

  @Test
  public void testCreateGameSuccess() {
    gameManager.createGame(gameInfo, gameId);
    assertTrue(gameManager.exists(gameId));
  }

  @Test
  public void testCreateGameFailureNullGameInfo() {
    assertThrows(IllegalArgumentException.class, () -> {
      gameManager.createGame(null, gameId);
    });
  }

  @Test
  public void testCreateGameFailureGameExists() {
    gameManager.createGame(gameInfo, gameId);
    assertThrows(IllegalArgumentException.class, () -> {
      gameManager.createGame(gameInfo, gameId);
    });
  }

  @Test
  public void testDeleteGameSuccess() {
    gameManager.createGame(gameInfo, gameId);
    gameManager.deleteGame(gameId);
    assertFalse(gameManager.exists(gameId));
  }

  @Test
  public void testDeleteGameFailureGameDoesNotExist() {
    assertThrows(IllegalArgumentException.class, () -> {
      gameManager.deleteGame(gameId);
    });
  }

  @Test
  public void testExistsSuccess() {
    gameManager.createGame(gameInfo, gameId);
    assertTrue(gameManager.exists(gameId));
  }

  @Test
  public void testExistsFailure() {
    assertFalse(gameManager.exists(gameId));
  }

  @Test
  public void testGetBoardSuccess() throws NoSuchFieldException, IllegalAccessException {
    gameManager.createGame(gameInfo, gameId);
    SplendorGame game = getGame(gameManager, gameId);
    assertEquals(gameManager.getBoard(gameId), game.getBoard());
  }

  @Test
  public void testGenerateActionsSuccess() throws NoSuchFieldException, IllegalAccessException {
    gameManager.createGame(gameInfo, gameId);
    SplendorGame game = getGame(gameManager, gameId);
    gameManager.generateActions(gameId, players[0].getName());
    verify(actionGenerator).generateActions(game, gameId, players[0]);
  }

  @Test
  public void testPlayerInGameTrue() throws NoSuchFieldException, IllegalAccessException {
    gameManager.createGame(gameInfo, gameId);
    SplendorGame game = getGame(gameManager, gameId);
    assertTrue(gameManager.playerInGame(gameId, players[0].getName()));
  }

  @Test
  public void testPlayerInGameFalse() throws NoSuchFieldException, IllegalAccessException {
    gameManager.createGame(gameInfo, gameId);
    SplendorGame game = getGame(gameManager, gameId);
    assertFalse(gameManager.playerInGame(gameId, "nonExistent"));
  }

  @Test
  public void testPerformActionSuccess() throws InsufficientResourcesException, InvalidAction, NoSuchFieldException, IllegalAccessException {
    SplendorGame game = mock(SplendorGame.class);
    ActionGenerator actionGenerator = mock(ActionGenerator.class);
    GameManager gameManager = new GameManager(actionGenerator, saveGameManager, registrator);
    addGameToGameManager(game, gameManager);
    doNothing().when(game).performAction(Mockito.any(), Mockito.anyString(), Mockito.any());
    gameManager.performAction(gameId, players[0].getName(), "1", actionData);
    verify(game).performAction(Mockito.any(), Mockito.anyString(), Mockito.any());
  }

  @Test
  public void testPerformActionInvalidAction() throws NoSuchFieldException, IllegalAccessException,
      InvalidAction {
    SplendorGame game = mock(SplendorGame.class);
    ActionGenerator actionGenerator = mock(ActionGenerator.class);
    GameManager gameManager = new GameManager(actionGenerator, saveGameManager, registrator);
    addGameToGameManager(game, gameManager);
    when(actionGenerator.getGeneratedAction(Mockito.anyLong(), Mockito.anyLong())).thenThrow(InvalidAction.class);
    assertThrows(InvalidAction.class, () -> {
      gameManager.performAction(gameId, players[0].getName(), "1", actionData);
    });
  }

  @Test
  public void testPerformActionRemovesActionAfter() throws InsufficientResourcesException,
      InvalidAction, NoSuchFieldException, IllegalAccessException {
    SplendorGame game = mock(SplendorGame.class);
    ActionGenerator actionGenerator = mock(ActionGenerator.class);
    GameManager gameManager = new GameManager(actionGenerator, saveGameManager, registrator);
    addGameToGameManager(game, gameManager);
    doNothing().when(game).performAction(Mockito.any(), Mockito.anyString(), Mockito.any());
    doNothing().when(actionGenerator).removeActions(Mockito.anyLong());
    gameManager.performAction(gameId, players[0].getName(), "1", actionData);
    verify(actionGenerator).removeActions(Mockito.anyLong());
  }

  private void addGameToGameManager(SplendorGame game, GameManager gameManager) throws NoSuchFieldException,
      IllegalAccessException {
    Field games = gameManager.getClass().getDeclaredField("games");
    games.setAccessible(true);
    Field boardManagers = gameManager.getClass().getDeclaredField("boardManagers");
    boardManagers.setAccessible(true);
    Board board = Mockito.mock(Board.class);
    when(game.getBoard()).thenReturn(board);
    when(board.isEmpty()).thenReturn(false);
    BroadcastContentManager<Board> boardManager =
        Mockito.mock(BroadcastContentManager.class);
    doNothing().when(boardManager).updateBroadcastContent(Mockito.any(Board.class));
    ((HashMap<Long, BroadcastContentManager<Board>>) boardManagers.get(gameManager))
        .put(gameId, boardManager);
    ((HashMap<Long, SplendorGame>) games.get(gameManager)).put(gameId, game);
    ((HashMap<Long, BroadcastContentManager<Board>>) boardManagers.get(gameManager))
        .put(gameId, boardManager);
    ((HashMap<Long, SplendorGame>) games.get(gameManager)).put(gameId, game);
  }

  @Test
  public void testSaveGameSuccess() throws NoSuchFieldException, IllegalAccessException {
    SplendorGame game = mock(SplendorGame.class);
    gameInfo.setSavegame("savegameId");
    setGameInfo(game);
    addGameToGameManager(game, gameManager);
    gameManager.saveGame(gameId);
  }

  @Test
  public void testSaveGameDoesNotExists() {
    SplendorGame game = mock(SplendorGame.class);
    gameInfo.setSavegame("savegameId");
    setGameInfo(game);
    assertThrows(IllegalArgumentException.class, () -> {
      gameManager.saveGame(gameId);
    });
  }

  private void setGameInfo(SplendorGame game) {
    when(game.getGameInfo()).thenReturn(gameInfo);
  }

  private void addSavedGameToGameManager(SplendorGame game, GameManager gameManager)
      throws IllegalAccessException, NoSuchFieldException {
    String saveGameId = game.getGameInfo().getSavegame();
    Field savedGames = gameManager.getClass().getDeclaredField("savedGames");
    savedGames.setAccessible(true);
    ((HashMap<String, SplendorGame>) savedGames.get(gameManager)).put(saveGameId, game);
  }
}
