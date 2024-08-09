package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.card.DevelopmentCard;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.payment.CoatOfArms;
import splendor.model.game.player.Inventory;
import splendor.model.game.player.Player;

import javax.naming.InsufficientResourcesException;

public class BuyCardActionTest {
  ActionGenerator actionGenerator = new ActionGenerator();
  SplendorGame game;
  Player player1 = new Player("Wassim", "Blue");
  Player player2 = new Player("Youssef", "Red");

  Player player3 = new Player("Jessie", "Green");

  static DevelopmentCard redCardCascade1 = DevelopmentCard.get(103);

  private void clearPlayerTokens(Player player) throws NoSuchFieldException {
    Field inventory = player.getClass().getDeclaredField("inventory");
    inventory.setAccessible(true);
    try {
      inventory.set(player, new Inventory());
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Before
  public void setUp() {
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("Splendor","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);
  }

  private Player getTurnPlayer(SplendorGame game) {
    if(game.getBoard().isTurnPlayer(player1)) {
      return player1;
    } else {
      return player2;
    }
  }

  @Test
  public void preformAction(){
    long gameId = 1;
    HashMap<Color, Integer> tokens= new HashMap<>();
    tokens.put(Color.RED, 3);
    tokens.put(Color.BLUE, 3);
    tokens.put(Color.WHITE, 3);
    player1.addTokens(tokens);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    System.out.println(actions.get(actions.size()-1).getActionType());
    player1.getResources();
    if (actions.get(actions.size()-1).getActionType() == ActionType.BUY ){
      actions.get(actions.size()-1).performAction(player1, game.getBoard());
//      assertEquals(1, player1.getCardsBought().size());
    } else if (actions.get(actions.size()-1).getActionType() == ActionType.RESERVE) {
      actions.get(actions.size()-1).performAction(player1, game.getBoard());
//      assertEquals(1, player1.getReservedCards().size());
    }

  }

  @Test
  public void testPerformActionWithNullTokenPayment() throws InsufficientResourcesException {
    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player3);
    List<DevelopmentCardI> dummyPayment = new ArrayList<DevelopmentCardI>();
    dummyPayment.add(DevelopmentCard.get(2));
    player3.buyCard(DevelopmentCard.get(2));
    BuyCardAction tester1 = new BuyCardAction(DevelopmentCard.get(1), null, dummyPayment);
    tester1.performAction(player3, game.getBoard());
    assertEquals(false, player3.getCardsBought().contains(DevelopmentCard.get(2)));
  }

  @Test
  public void testPerformActionWithCoatOfArmsBonus() throws NoSuchFieldException, InsufficientResourcesException {
    long gameId = 1;
    player1.addUnlockedCoatOfArms(CoatOfArms.get(1));
    HashMap<Color, Integer> tokens= new HashMap<>();
    tokens.put(Color.RED, 3);
    tokens.put(Color.BLUE, 3);
    tokens.put(Color.WHITE, 3);
    player1.addTokens(tokens);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
//    System.out.println(actions.get(actions.size()-1).getActionType());
    actions.get(actions.size()-1).performAction(player1, game.getBoard());
    assertEquals(ActionType.TAKE_ONE_TOKEN, (player1.nextAction()));
  }

  @Test
  public void testGetLegalActions(){
    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    player1.addUnlockedCoatOfArms(CoatOfArms.get(3));
    HashMap<Color, Integer> tokenPayment = DevelopmentCard.get(1).getCost().getCost();
    BuyCardAction testBuyCardAction = new BuyCardAction(DevelopmentCard.get(1),tokenPayment, null);
    List<Action> legalAction = testBuyCardAction.getLegalActions(game, player1);
    assertEquals(true, legalAction.size()>0);
  }

  @Test
  public void testGetLegalActionsWithInsufficientTokens() throws NoSuchFieldException {
    long gameId = 1;
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    HashMap<Color, Integer> tokenPayment = DevelopmentCard.get(1).getCost().getCost();
    clearPlayerTokens(player1);
    player1.addTokens(tokenPayment);
    player1.addUnlockedCoatOfArms(CoatOfArms.get(3));
    BuyCardAction testBuyCardAction = new BuyCardAction(DevelopmentCard.get(1),tokenPayment, null);
    List<Action> legalAction = testBuyCardAction.getLegalActions(game, player1);
    assertEquals(true, legalAction.size()>0);
  }


//  @Test
//  public void performActionRedCardWithCascade() {
//    Action redCardAction = new BuyCardAction(redCardCascade1);
//    Player turnPlayer = getTurnPlayer(game);
//    redCardAction.performAction(turnPlayer, game.getBoard());
//    assertEquals(1, turnPlayer.getCardsBought().size()); // adds card to player
//    assertEquals(ActionType.TAKE_CARD_1, turnPlayer.nextAction()); // adds next action
//    assertEquals(turnPlayer, getTurnPlayer(game)); // does not change turn
//  }


}
