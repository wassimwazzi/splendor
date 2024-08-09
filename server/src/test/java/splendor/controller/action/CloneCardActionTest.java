package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.SplendorGame;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.player.Player;

public class CloneCardActionTest {

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
  public void CloneCardAction(){
    long gameId = 1;
    DevelopmentCardI card = game.getBoard().getDecks()[0].takeCard(0);
    player1.addCard(card);
    game.getBoard().removeCard(card);
    player1.addNextAction(ActionType.CLONE_CARD);
    List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
    System.out.println(actions.get(actions.size()-1).getActionType());
    actions.get(actions.size()-1).performAction(player1, game.getBoard());
    assertEquals(2, player1.getCardsBought().size());
  }

}
