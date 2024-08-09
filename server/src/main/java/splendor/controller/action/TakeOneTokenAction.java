package splendor.controller.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import splendor.model.game.Board;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.player.Player;

/**
 * Taking one token action if a player has an unlocked coat of arms.
 */
public class TakeOneTokenAction implements Action {
  private HashMap<Color, Integer> token;
  private final ActionType actionType;
  private final long actionId;

  /**
   * Creates a take token action.
   *
   * @param actionType to know what type of action it is
   * @param token  tokens that can be taken.
   */
  public TakeOneTokenAction(ActionType actionType, HashMap<Color, Integer> token) {
    this.actionType = actionType;
    this.token = token;
    this.actionId = (long) (Math.random() * Long.MAX_VALUE);
  }

  /**
   * generates a list of actions that the player can make.
   *
   * @param game The game that the player is playing in.
   * @return  a list of possible action that the player can take.
   */
  public static List<Action> getLegalActions(SplendorGame game) {
    List<Action> actions = new ArrayList<>();
    for (Color color : game.getBoard().getTokens().keySet()) {
      HashMap<Color, Integer> tk = new HashMap<>();
      tk.put(color, 1);
      actions.add(new TakeOneTokenAction(ActionType.TAKE_ONE_TOKEN, tk));
    }
    return actions;
  }

  @Override
  public long getId() {
    return this.actionId;
  }

  @Override
  public void performAction(Player player, Board board) {
    player.addTokens(this.token);
    board.removeTokens(this.token);
    player.removeOldestNextAction(); // needs to be remove the last one.
  }

  @Override
  public ActionType getActionType() {
    return this.actionType;
  }
}
