package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.payment.CoatOfArms;
import splendor.model.game.player.Inventory;
import splendor.model.game.player.Player;
 import splendor.model.game.Color;
public class TakeTokensActionTest {
  ActionGenerator actionGenerator = new ActionGenerator();
  SplendorGame game;
  Player player1 = new Player("Wassim", "Blue");
  Player player2 = new Player("Youssef", "Red");

//  @Before
//  public void setUp() {
//    Player[] testPlayers = {player1,player2};
//    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
//    game = new SplendorGame(testGameInfo);
//  }

  private void clearPlayerTokens(Player player) throws NoSuchFieldException {
    for (Color key : player.getTokens().keySet()) {
      player.getTokens().put(key, 0);
    }
    }


  @Test
  public void preformAction(){
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clearPlayerTokens(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    Action action = actions.get(0);
    action.performAction(player1, game.getBoard());

    int numOfTokens = 3;
    for (Color c : player1.getTokens().keySet()){
      numOfTokens -= player1.getTokens().get(c);
    }
    assertTrue(numOfTokens == 1 || numOfTokens == 0);
  }

  @Test
  public void performTakeOneTokenAction(){
    long gameId = 1;
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clearPlayerTokens(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    HashMap<Color, Integer> sixBlueToken = new HashMap<Color, Integer>();
    sixBlueToken.put(Color.BLUE, 6);

    HashMap<Color, Integer>  allTokensFromBoard = game.getBoard().getTokens();
    game.getBoard().removeTokens(allTokensFromBoard);
    game.getBoard().addTokens(sixBlueToken);

    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);

    Action action = actions.get(0);
    action.performAction(player1, game.getBoard());
    assertEquals(true, player1.getTokens().get(Color.BLUE) == 1);
  }

  /***
   * There is six blue tokens in the board
   * The player have no tokens
   * The player should take two blue tokens
   ***/
  @Test
  public void performTakeTwoTokenAction(){
    long gameId = 1;
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clearPlayerTokens(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    HashMap<Color, Integer> sixBlueToken = new HashMap<Color, Integer>();
    sixBlueToken.put(Color.BLUE, 6);

    HashMap<Color, Integer>  allTokensFromBoard = game.getBoard().getTokens();
    game.getBoard().removeTokens(allTokensFromBoard);
    game.getBoard().addTokens(sixBlueToken);

    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    for (Action a : actions) {
//      System.out.println(a.getActionType());
//    }

    Action action = actions.get(1);
    action.performAction(player1, game.getBoard());
    assertEquals(true, player1.getTokens().get(Color.BLUE) == 2);
  }

  /***
   * There is one blue token, one red token, and one green token
   * The player have no tokens
   * The player should take one blue token, one red token, and one green token
   ***/
  @Test
  public void performTakeThreeTokenAction(){
    long gameId = 1;
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clearPlayerTokens(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    HashMap<Color, Integer> tokensWithDiffColor = new HashMap<Color, Integer>();
    tokensWithDiffColor.put(Color.BLUE, 1);
    tokensWithDiffColor.put(Color.RED, 1);
    tokensWithDiffColor.put(Color.GREEN, 1);

    HashMap<Color, Integer>  allTokensFromBoard = game.getBoard().getTokens();
    game.getBoard().removeTokens(allTokensFromBoard);
    game.getBoard().addTokens(tokensWithDiffColor);

    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    Action action = actions.get(0);
    action.performAction(player1, game.getBoard());
    assertEquals(true, (player1.getTokens().get(Color.BLUE) == 1) && (player1.getTokens().get(Color.RED) == 1) && (player1.getTokens().get(Color.GREEN) == 1));
  }

  @Test
  public void performTakeTwoTokenActionWithSpecialPower(){
    long gameId = 1;
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);

    try {
      clearPlayerTokens(player1);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    HashMap<Color, Integer> sixBlueToken = new HashMap<Color, Integer>();
    sixBlueToken.put(Color.BLUE, 6);
    sixBlueToken.put(Color.GREEN, 1);
    HashMap<Color, Integer>  allTokensFromBoard = game.getBoard().getTokens();
    game.getBoard().removeTokens(allTokensFromBoard);
    game.getBoard().addTokens(sixBlueToken);

    HashMap<Color, Integer> twoWhiteToken = new HashMap<Color, Integer>();
    twoWhiteToken.put(Color.WHITE, 2);
    player1.addTokens(twoWhiteToken);
    player1.addUnlockedCoatOfArms(CoatOfArms.get(2));

    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    for (Action a : actions) {
//      System.out.println(a.getActionType());
//    }

    Action action = actions.get(1);
    action.performAction(player1, game.getBoard());
    assertEquals(true, (player1.getTokens().get(Color.BLUE) == 2) && (player1.getTokens().get(Color.GREEN) == 1));
  }

  @Test
  public void getActionType(){
    HashMap<Color, Integer> token = new HashMap<Color, Integer>();
    TakeOneTokenAction tester = new TakeOneTokenAction(ActionType.TAKE_ONE_TOKEN,token);
    assertEquals(true, tester.getActionType() == ActionType.TAKE_ONE_TOKEN);
  }

  @Test
  public void getActionId(){
    HashMap<Color, Integer> token = new HashMap<Color, Integer>();
    TakeOneTokenAction tester = new TakeOneTokenAction(ActionType.TAKE_ONE_TOKEN,token);
    assertTrue(tester.getId() > 0);
  }

//  @Test // not working for some reason
//  public void generatingTake2TokensAndTake1Token(){
//    Player[] testPlayers = {player1,player2};
//    GameInfo testGameInfo = new GameInfo("testServer","SplendorGameTest",testPlayers,"testSave");
//    game = new SplendorGame(testGameInfo);
//
//    long gameId = 1;
//    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    actions.get(0).preformAction(player1, game.getBoard());
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player2);
//    actions.get(0).preformAction(player2, game.getBoard());
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player1);
//    actions.get(0).preformAction(player1, game.getBoard());
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player2);
//    actions.get(0).preformAction(player2, game.getBoard());
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player1);
//    actions.get(0).preformAction(player1, game.getBoard());
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player2);
//    actions.get(0).preformAction(player2, game.getBoard());
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player1);
//    actions.get(0).preformAction(player1, game.getBoard());
//    game.getBoard().nextTurn();
//    actions = actionGenerator.generateActions(game, gameId, player2);
////    System.out.println(actions.size());
//    System.out.println(game.getBoard().getTokens());
////    actions.get(0).preformAction(player2, game.getBoard());
//  }
}
