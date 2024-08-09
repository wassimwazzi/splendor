package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.Board;
import splendor.model.game.SplendorGame;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.deck.SplendorDeck;
import splendor.model.game.player.Inventory;
import splendor.model.game.player.Player;

public class ActionGeneratorTest {
  ActionGenerator actionGenerator = new ActionGenerator();
  SplendorGame game;
  Player player1 = new Player("Wassim", "Blue");
  Player player2 = new Player("Youssef", "Red");

  @Before
  public void setUp() {
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("Splendor","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);
    try {
      setSingleCardOnly(game);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
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

  private void setSingleCardOnly(SplendorGame game) throws NoSuchFieldException,
      IllegalAccessException {
    // This makes it easier to test the action generator
    Board board = game.getBoard();
    Field decks = board.getClass().getDeclaredField("decks");
    decks.setAccessible(true);
    SplendorDeck[] gameDeck = (SplendorDeck[]) decks.get(board);
    boolean keepOne = true;
    for (SplendorDeck deck : gameDeck) {
      clearCards(deck, keepOne);
      keepOne = false;
    }
  }

  private void clearCards(SplendorDeck deck, boolean keepOne) throws NoSuchFieldException,
      IllegalAccessException {
    Field cards = deck.getClass().getDeclaredField("faceUpCards");
    cards.setAccessible(true);
    // make cards not final
    SplendorCard[] cardArray = (SplendorCard[]) cards.get(deck);
    for (int i = 0; i < cardArray.length; i++) {
      if (keepOne && i == 0) {
        continue;
      }
      cardArray[i] = null;
    }
  }

//  @Test
//  public void testGenerateActions() {
//    long gameId = 1;
//    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    assertEquals(17, actions.size()); // buy and reserve and takeTokens
//  }

//  @Test
//  public void testGenerateActionsEmptyPlayerInventory() {
//    long gameId = 1;
//    try {
//      clearPlayerTokens(player1);
//    } catch (NoSuchFieldException e) {
//      throw new RuntimeException(e);
//    }
//    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    assertEquals(16, actions.size()); // reserve + takeTokens
//  }

  @Test
  public void testGenerateActionsNotPlayerTurn() {
  long gameId = 1;
  List<Action> actions = actionGenerator.generateActions(game, gameId, player2);
  assertEquals(0, actions.size()); // no actions
  }

  @Test
  public void testGenerateActionsTwiceAreEqual() {
    long gameId = 1;
    List<Action> actions1 = actionGenerator.generateActions(game, gameId, player1);
    List<Action> actions2 = actionGenerator.generateActions(game, gameId, player1);
    assertEquals(actions1, actions2);
  }

  @Test
  public void testGetGeneratedAction() {
    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    Action action = actions.get(0);
    long actionId = action.getId();
    try {
      assertEquals(action, actionGenerator.getGeneratedAction(gameId, actionId));
    } catch (InvalidAction e) {
      fail(e);
    }
  }

  @Test
  public void testGetGeneratedActionInvalidActionId() {
    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    Action action = actions.get(0);
    long actionId = action.getId();
    try {
      actionGenerator.getGeneratedAction(gameId, actionId + 1);
      fail("InvalidAction should have been thrown");
    } catch (InvalidAction e) {
      assertTrue(true);
    }
  }

  @Test
  public void testRemoveAction() {
    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    Action action = actions.get(0);
    long actionId = action.getId();
    actionGenerator.removeActions(gameId);
    try {
      actionGenerator.getGeneratedAction(gameId, actionId);
      fail("InvalidAction should have been thrown");
    } catch (InvalidAction e) {
      assertTrue(true);
    }
  }

  @Test
  public void takeCardLevel2(){
    long gameId = 1;
    player1.addNextAction(ActionType.TAKE_CARD_2);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    assertEquals(0, actions.size()); // take level 2 card
  }

  @Test
  public void takeCardLevel1(){
    long gameId = 1;
    player1.addNextAction(ActionType.TAKE_CARD_1);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    assertEquals(1, actions.size()); // take level 1 card does not exist since there is only one dev card
  }

  @Test
  public void takeNoble(){
    long gameId = 1;
    player1.addNextAction(ActionType.TAKE_NOBLE);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    assertEquals(0, actions.size()); // take Noble
  }

  @Test
  public void reserveNoble(){ // should work once we have nobles in the model.
    long gameId = 1;
    player1.addNextAction(ActionType.RESERVE_NOBLE);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    assertEquals(3, actions.size()); // reserve Noble
  }

  @Test
  public void returnTokens(){ // need to check after we took tokens
    long gameId = 1;
    player1.addNextAction(ActionType.RETURN_TOKENS);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    assertEquals(0, actions.size()); // no tokens to return
  }

  @Test
  public void cloneCard(){ // need to check after we get a card
    long gameId = 1;
    player1.addNextAction(ActionType.CLONE_CARD);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    assertEquals(0, actions.size()); // no card to clone
  }
}
