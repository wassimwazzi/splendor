package splendor.controller.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;
import splendor.model.game.SplendorGame;
import splendor.model.game.player.SplendorPlayer;

/**
 * Class responsible for generating all possible actions for a given game state.
 */
@Component
public class ActionGenerator {
  private final HashMap<Long, List<Action>> gameActions = new HashMap<>();

  /**
   * Returns all possible actions for a given game state.
   *
   * @param game the game
   * @param gameId the game id
   * @param player the player
   * @return all possible actions for a given game state
   */
  public List<Action> generateActions(SplendorGame game, long gameId,  SplendorPlayer player) {
    List<Action> actions = new ArrayList<>();
    if (!game.isTurnPlayer(player)) {
      return actions;
    }
    if (game.getBoard().isFinished()) {
      return actions;
    }
    if (gameActions.containsKey(gameId)) {
      // To check if the action was already generated for the player
      return gameActions.get(gameId);
    }

    if (player.nextAction() == null) {
      actions.addAll(TakeTokensAction.getLegalActions(game, player));
      actions.addAll(ReserveCardAction.getLegalActions(game, player));
      actions.addAll(BuyCardAction.getLegalActions(game, player));
      actions.addAll(BuyReservedCardAction.getLegalActions(player));
    } else if (player.nextAction() == ActionType.TAKE_CARD_2) {
      actions.addAll(TakeCardAction.getLegalActions(game, 2));
    } else if (player.nextAction() == ActionType.TAKE_CARD_1) {
      actions.addAll(TakeCardAction.getLegalActions(game, 1));
    } else if (player.nextAction() == ActionType.TAKE_NOBLE) {
      actions.addAll(TakeNobleAction.getLegalActions(game, player, false));
    } else if (player.nextAction() == ActionType.RETURN_TOKENS) {
      actions.addAll(ReturnTokensAction.getLegalActions(player));
    } else if (player.nextAction() == ActionType.RESERVE_NOBLE) {
      actions.addAll(TakeNobleAction.getLegalActions(game, player, true));
    } else if (player.nextAction() == ActionType.CLONE_CARD) {
      actions.addAll(CloneCardAction.getLegalActions(player));
    } else if (player.nextAction() == ActionType.TAKE_ONE_TOKEN) {
      actions.addAll(TakeOneTokenAction.getLegalActions(game));
    }
    player.removeOldestNextAction();
    gameActions.put(gameId, actions); // save the actions for later validation
    return actions;
  }

  /**
   * Returns a previously generated action.
   *
   * @param gameId the id of the game
   * @param actionId the id of the action
   * @return the action
   * @throws InvalidAction if the action does not exist
   */
  public Action getGeneratedAction(long gameId, long actionId) throws InvalidAction {
    if (!gameActions.containsKey(gameId)) {
      throw new InvalidAction("Game id does not exist");
    }
    return gameActions.get(gameId).stream()
        .filter(action -> action.getId() == actionId)
        .findFirst()
        .orElseThrow(() -> new InvalidAction("Action not found"));
  }

  /**
   * Removes the action that has been executed.
   *
   * @param gameId the id of the game
   */
  public void removeActions(long gameId) {
    gameActions.remove(gameId);
  }
}
