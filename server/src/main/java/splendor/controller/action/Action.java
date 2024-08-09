package splendor.controller.action;

import splendor.model.game.Board;
import splendor.model.game.player.Player;

/**
 * Interface for all actions that can be performed by a player.
 * All actions can be executed.
 */
public interface Action {

  /**
   * Getter for the action id.
   *
   * @return the Action id.
   */
  long getId();


  /**
   * Performs the action depending on the which action is being called.
   *
   * @param player the player that is performing the action.
   * @param board  the board that the player is playing on.
   */
  void performAction(Player player, Board board);

  /**
   * Returns the type of the action.
   *
   * @return the type of the action
   */
  ActionType getActionType();

}
