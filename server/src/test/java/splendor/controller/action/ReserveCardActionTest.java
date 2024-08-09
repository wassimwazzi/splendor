package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.player.Inventory;
import splendor.model.game.player.Player;

public class ReserveCardActionTest {

  ActionGenerator actionGenerator = new ActionGenerator();
  SplendorGame game;
  Player player1 = new Player("Wassim", "Blue");
  Player player2 = new Player("Youssef", "Red");

  @Before
  public void setUp() {
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);
  }

  private void clearPlayerTokens(Player player) throws NoSuchFieldException {
    Field inventory = player.getClass().getDeclaredField("inventory");
    inventory.setAccessible(true);
    try {
      inventory.set(player, new Inventory());
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private void clear(Player player) throws NoSuchFieldException {
    for (Color key : player.getTokens().keySet()) {
      player.getTokens().put(key, 0);
    }
  }

  @Test
  public void preformBuyAReserveCardAction(){
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clear(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);

    Action action = actions.get(15); // should be reserve card
    action.performAction(player1, game.getBoard());

    HashMap<Color, Integer> addTokensToPlayer = new HashMap<Color, Integer>();
    addTokensToPlayer.put(Color.GOLD, 9); //It is only used for testing
    player1.addTokens(addTokensToPlayer);

    actionGenerator.removeActions(gameId);
    List<Action> nextactions = actionGenerator.generateActions(game, gameId, player1);
    for (Action a : nextactions) {
      System.out.println(a.getActionType());
    }

    action = nextactions.get(nextactions.size() -1);
    action.performAction(player1, game.getBoard());
    assertEquals(1, player1.getCardsBought().size());
//    assertTrue(player1.getCardsReserved().size()  == 1); // should work when we have a method for it.
  }

  @Test
  public void preformAction(){
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clear(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    Action action = actions.get(17); // should be reserve card
    action.performAction(player1, game.getBoard());
    assertEquals(1, (int) player1.getTokens().get(Color.GOLD));
//    assertTrue(player1.getCardsReserved().size()  == 1); // should work when we have a method for it.
  }
}
