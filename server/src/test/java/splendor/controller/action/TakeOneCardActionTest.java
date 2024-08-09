package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.SplendorGame;
import splendor.model.game.player.Player;

public class TakeOneCardActionTest {

  ActionGenerator actionGenerator = new ActionGenerator();
  SplendorGame game;
  Player player1 = new Player("Wassim", "Blue");
  Player player2 = new Player("Youssef", "Red");

  @Before
  public void setUp() {
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("Cities","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);
  }


  @Test
  public void takeOneCardAction(){
    long gameId = 1;
    player1.addNextAction(ActionType.TAKE_CARD_1);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    actions.get(actions.size()-1).performAction(player1, game.getBoard());
    assertEquals(1, player1.getCardsBought().size());
  }


  @Test
  public void takeOneCardAction2(){
    long gameId = 1;
    player1.addNextAction(ActionType.TAKE_CARD_2);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    actions.get(actions.size()-1).performAction(player1, game.getBoard());
    assertEquals(1, player1.getCardsBought().size());
  }
}
