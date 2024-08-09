package splendor.controller.lobbyservice;

import java.util.Arrays;
import java.util.stream.Collectors;
import splendor.model.game.Color;
import splendor.model.game.player.Player;

/**
 * Class responsible for storing all information about a game received from LS.
 */
public class GameInfo {

  private final String gameServer;
  private final String creator;
  private final Player[] players;
  private String savegame;

  /**
   * Instantiates a new Game info.
   *
   * @param gameServer the game server name, just for verification
   * @param creator    the creator of the session
   * @param players    the players
   * @param savegame   whether to save the game
   */
  public GameInfo(String gameServer, String creator, Player[] players, String savegame) {
    this.gameServer = gameServer;
    this.creator = creator;
    this.players = players;
    this.savegame = savegame;
  }

  /**
   * Getter for players.
   *
   * @return array of Players
   */
  public Player[] getPlayers() {
    return players;
  }

  /**
   * Nicely formats the game info.
   *
   * @return String representation of the game info
   */
  @Override
  public String toString() {
    return "GameInfo{"
            + "gameServer='" + gameServer + '\''
            + ", creator=" + creator
            + ", players=" + Arrays.toString(players)
            + ", savegame='" + savegame + '\''
            + '}';
  }

  /**
   * Get the gameserver name.
   *
   * @return the gameserver name
   */
  public String getGameServer() {
    return gameServer;
  }

  /**
   * Sets the savegame.
   *
   * @param savegame the savegame
   */
  public void setSavegame(String savegame) {
    this.savegame = savegame;
  }

  /**
   * Returns a JSON representation of the game info. Used for saving games with LS.
   *
   * @return JSON representation of the game info
   */
  public String toJson() {
    String playerList = Arrays.stream(players)
        .map(name -> "\"" + name + "\"")
        .collect(Collectors.joining(", "));
    return "{"
        + "\"gamename\":\"" + gameServer + "\""
        + ", \"players\":[" + playerList + "]"
        + ", \"savegameid\":\"" + savegame + "\""
        + '}';
  }

  /**
   * Get Json for save game resource.
   * Example: {
   *     "creator": "maex",
   *     "game": "DummyGame1",
   *     "savegame": "funnysavegameid42"
   * }
   *
   * @return Json for save game resource
   */
  public String toJsonForSaveGameResource() {
    return "{"
        + "\"creator\":\"" + creator + "\""
        + ", \"game\":\"" + gameServer + "\""
        + ", \"savegame\":\"" + savegame + "\""
        + '}';
  }

  /**
   * Getter for savegame.
   *
   * @return savegame
   */
  public String getSavegame() {
    return savegame;
  }
}
