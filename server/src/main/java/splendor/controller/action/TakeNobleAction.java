package splendor.controller.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import splendor.model.game.Board;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.Noble;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.payment.Bonus;
import splendor.model.game.payment.Cost;
import splendor.model.game.player.Player;
import splendor.model.game.player.SplendorPlayer;

/**
 * Take noble action class.
 */
public class TakeNobleAction extends CardAction {
  private static CardType cardType = CardType.Noble;

  /**
   * Creates a new card action.
   *
   * @param card the card
   * @param actionType the action type
   */
  protected TakeNobleAction(ActionType actionType, SplendorCard card) {

    super(actionType, card);
  }

  /**
   * Performs TakeNoble action.
   *
   * @param player the player that is taking the noble.
   * @param board  the board where this action takes place.
   */
  @Override
  public void performAction(Player player, Board board) {
    if (TakeNobleAction.canAfford(player.getCardsBought(), this.getCard().getCost())) {
      player.addNoble((Noble) this.getCard());
      board.removeNoble((Noble) this.getCard());
    } else {
      player.addReserveNoble((Noble) this.getCard());
      board.removeNoble((Noble) this.getCard());
    }
  }

  /**
   * Generates the list of actions for the player.
   *
   * @param game   the current game that is being played.
   * @param player the player we are generating actions for.
   * @param all boolean which represents if it is a reserve noble or picking
   *            one noble from different options.
   * @return all legal actions for the given player in the given game state.
   */
  public static List<Action> getLegalActions(SplendorGame game,
                                             SplendorPlayer player, boolean all) {
    List<Action> actions = new ArrayList<>();
    List<Noble> nobles =  game.getBoard().getNobles();
    List<DevelopmentCardI> cards = player.getCardsBought();

    if (all) {
      for (Noble n : nobles) {
        actions.add(new TakeNobleAction(ActionType.TAKE_NOBLE, n));
      }
    } else {
      for (Noble n : nobles) {
        if (TakeNobleAction.canAfford(cards, n.getCost())) {
          actions.add(new TakeNobleAction(ActionType.RESERVE_NOBLE, n));
        }
      }
    }

    return actions;
  }

  /**
   * Checks if the player can afford the noble.
   *
   * @param cards the player's cards, used to calculate all the player's bonuses.
   * @param cost the cost of the noble
   * @return whether the bonuses in the cards reach the cost of the noble
   */
  private static boolean canAfford(List<DevelopmentCardI> cards, Cost cost) {
    HashMap<Color, Integer> costCopy = new HashMap<>();
    for (Color c : cost) {
      costCopy.putIfAbsent(c, 0);
      costCopy.put(c, costCopy.get(c) + 1);
    }

    for (SplendorCard card : cards) {
      Bonus bonus = card.getBonus();
      for (Color color : card.getBonus()) {
        costCopy.put(color, costCopy.getOrDefault(color, 0) - bonus.getBonus(color));
      }
    }

    for (Color c : costCopy.keySet()) {
      if (costCopy.get(c) > 0) {
        return false;
      }
    }

    return true;
  }

}
