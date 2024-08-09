package splendor.model.game.player;

/**
 * Copied from BoardGamePlatform.
 * Read only interface for properties of a player, as provided by the LobbyService.
 *
 * @Author Maximilian Schiedermeier
 * @Date December 2020
 */
public interface PlayerReadOnly {

  /**
   * Getter for the name of a player.
   *
   * @return the name of the player
   */
  String getName();

  /**
   * Getter for the preferred colour of a player.
   *
   * @return the preferred colour of the player
   */
  String getPreferredColour();

  /**
   * Equals comparison. Only matches names. Ignores upper/lower cases.
   *
   * @param other as the player object to compare with.
   * @return a boolean that indicates whether the names of the provided player objects match.
   */
  boolean equals(Object other);
}
