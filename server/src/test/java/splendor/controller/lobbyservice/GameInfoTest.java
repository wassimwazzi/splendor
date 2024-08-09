package splendor.controller.lobbyservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import splendor.model.game.player.Player;

public class GameInfoTest {
  private final String gameServer = "gameServer";
  private final String creator = "creator";
  private final Player[] players = new Player[0];
  private final String savegame = "savegame";

  private final GameInfo gameInfo = new GameInfo(gameServer, creator, players, savegame);

  @Test
  public void testGetPlayers() {
    assertEquals(players, gameInfo.getPlayers());
  }

  @Test
  public void testToString() {
    String expected = "GameInfo{"
            + "gameServer='" + gameServer + '\''
            + ", creator=" + creator
            + ", players=" + java.util.Arrays.toString(players)
            + ", savegame='" + savegame + '\''
            + '}';
    assertEquals(expected, gameInfo.toString());
  }
}
