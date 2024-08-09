package splendor.controller.game;

import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import splendor.controller.action.Action;
import splendor.controller.action.ActionGenerator;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.player.Player;

public class SplendorGameTest {
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
}

