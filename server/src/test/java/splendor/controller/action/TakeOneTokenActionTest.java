package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.player.Player;

public class TakeOneTokenActionTest {

  ActionGenerator actionGenerator = new ActionGenerator();
  SplendorGame game;
  Player player1 = new Player("Wassim", "Blue");
  Player player2 = new Player("Youssef", "Red");

  @Before
  public void setUp() {
    Player[] testPlayers = {player1,player2};
    GameInfo testGameInfo = new GameInfo("Splendor","SplendorGameTest",testPlayers,"testSave");
    game = new SplendorGame(testGameInfo);
  }


  @Test
  public void takeOneTokenAction(){
    long gameId = 1;
    player1.addNextAction(ActionType.TAKE_ONE_TOKEN);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    System.out.println(actions.get(actions.size()-1).getActionType());
    actions.get(actions.size()-1).performAction(player1, game.getBoard());
    int numOfTokensTaken = 0;
    for (Color c : player1.getTokens().keySet()) {
      numOfTokensTaken += player1.getTokens().get(c);
    }
    assertEquals(1, numOfTokensTaken);
  }

}
