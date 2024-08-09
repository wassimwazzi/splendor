package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.player.Player;

public class ReturnTokensActionTest {
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

  /***
  private void clearPlayerTokens(Player player) throws NoSuchFieldException {
    Field inventory = player.getClass().getDeclaredField("inventory");
    inventory.setAccessible(true);
    try {
      inventory.set(player, new Inventory());
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }***/

  private void clearPlayerTokens(Player player) throws NoSuchFieldException {
    for (Color key : player.getTokens().keySet()) {
      player.getTokens().put(key, 0);
    }
  }

  /***
   * The player has 10 red tokens, so the player can take one token
   * then the player has to return 1 token
   * ***/
  @Test
  public void performReturnOneTokenAction(){
    long gameId = 1;
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clearPlayerTokens(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    HashMap<Color, Integer> tenBlueTokens = new HashMap<Color, Integer>();
    tenBlueTokens.put(Color.BLUE, 10);
    player1.addTokens(tenBlueTokens);


    HashMap<Color, Integer> sixBlueToken = new HashMap<Color, Integer>();
    sixBlueToken.put(Color.BLUE, 6);
    HashMap<Color, Integer>  allTokensFromBoard = game.getBoard().getTokens();
    game.getBoard().removeTokens(allTokensFromBoard);
    game.getBoard().addTokens(sixBlueToken);

    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    for (Action a : actions) {
//      System.out.println(a.getActionType());
//   }
    Action action = actions.get(0);
    action.performAction(player1, game.getBoard());


    actionGenerator.removeActions(gameId);

    List<Action> nextactions = actionGenerator.generateActions(game, gameId, player1);
//    for (Action a : nextactions) {
//      System.out.println(a.getActionType());
//    }

    action = nextactions.get(0);
    action.performAction(player1, game.getBoard());
    System.out.println(player1.getTokens());
    assertEquals(10, player1.getTokens().get(Color.BLUE));
  }

  /***
   * There is six blue tokens in the board
   * The player have no tokens
   * The player should take two blue tokens
   ***/
  @org.junit.jupiter.api.Test
  public void performReturnTwoTokenAction(){
    long gameId = 1;
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clearPlayerTokens(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    HashMap<Color, Integer> tenRedTokens = new HashMap<Color, Integer>();
    tenRedTokens.put(Color.RED, 10);
    player1.addTokens(tenRedTokens);


    HashMap<Color, Integer> sixBlueToken = new HashMap<Color, Integer>();
    sixBlueToken.put(Color.BLUE, 6);
    HashMap<Color, Integer>  allTokensFromBoard = game.getBoard().getTokens();
    game.getBoard().removeTokens(allTokensFromBoard);
    game.getBoard().addTokens(sixBlueToken);

    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    for (Action a : actions) {
      System.out.println(a.getActionType());
   }
    Action action = actions.get(1);
    action.performAction(player1, game.getBoard());
    System.out.println(player1.getTokens());

    actionGenerator.removeActions(gameId);

    List<Action> nextactions = actionGenerator.generateActions(game, gameId, player1);
    for (Action a : nextactions) {
      System.out.println(a.getActionType());
    }

    action = nextactions.get(2);
    action.performAction(player1, game.getBoard());
    System.out.println(player1.getTokens());
    assertEquals(true, (player1.getTokens().get(Color.RED) == 8 && (player1.getTokens().get(Color.BLUE) == 2)));
  }

  /***
   * There is one blue token, one red token, and one green token
   * The player have no tokens
   * The player should take one blue token, one red token, and one green token
   ***/
  @Test
  public void performReturnThreeTokenAction(){
    long gameId = 1;
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clearPlayerTokens(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    HashMap<Color, Integer> tenRedTokens = new HashMap<Color, Integer>();
    tenRedTokens.put(Color.RED, 10);
    player1.addTokens(tenRedTokens);

    HashMap<Color, Integer> tokensWithDiffColor = new HashMap<Color, Integer>();
    tokensWithDiffColor.put(Color.BLUE, 1);
    tokensWithDiffColor.put(Color.RED, 1);
    tokensWithDiffColor.put(Color.GREEN, 1);
    HashMap<Color, Integer>  allTokensFromBoard = game.getBoard().getTokens();
    game.getBoard().removeTokens(allTokensFromBoard);
    game.getBoard().addTokens(tokensWithDiffColor);

    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    for (Action a : actions) {
//      System.out.println(a.getActionType());
//    }

    Action action = actions.get(0);
    action.performAction(player1, game.getBoard());
    System.out.println(player1.getTokens());

    actionGenerator.removeActions(gameId);


    List<Action> nextactions = actionGenerator.generateActions(game, gameId, player1);
//    for (Action a : nextactions) {
//      System.out.println(a.getActionType());
//    }
//
    action = nextactions.get(9);
    action.performAction(player1, game.getBoard());
    System.out.println(player1.getTokens());
    assertEquals(true, (player1.getTokens().get(Color.RED) == 8 && (player1.getTokens().get(Color.BLUE) == 1) && (player1.getTokens().get(Color.GREEN) == 1)));

  }


//  @Test
//  public void GenerateActions() {
//    long gameId = 1;
//    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    try {
//      clearPlayerTokens(player1);
//    } catch (NoSuchFieldException e) {
//      throw new RuntimeException(e);
//    }
//    actions.get(0).performAction(player1, game.getBoard());
//    game.getBoard().nextTurn();
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player1);
//    actions.get(0).preformAction(player1, game.getBoard());
//    game.getBoard().nextTurn();
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player1);
//    actions.get(0).preformAction(player1, game.getBoard());
//    game.getBoard().nextTurn();
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player1);
//    actions.get(0).preformAction(player1, game.getBoard());
//    game.getBoard().nextTurn();
//    game.getBoard().nextTurn();
//    player1.addNextAction(ActionType.RETURN_TOKENS);
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player2);
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player1);
//    System.out.println(player1.nextAction());
//    for (Action a: actions){
//      System.out.println(a);
//    }
//    assertEquals(51, actions.size());
//  }

  @Test
  public void preformAction() {

  }
}
