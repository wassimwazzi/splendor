package splendor.controller.action;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InsufficientResourcesException;
import splendor.model.game.Board;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.player.Player;
import splendor.model.game.player.SplendorPlayer;

/**
 * Buying a reserved card action.
 */
public class BuyReservedCardAction extends CardAction {
  private static CardType cardType = CardType.DevelopmentCard;

  /**
   * Creates a new card action.
   *
   * @param card       the card
   */
  protected BuyReservedCardAction(SplendorCard card) {
    super(ActionType.BUY_RESERVED_CARD, card);
  }

  /**
   * Generates the list of buy reserved card actions for the player.
   *
   * @param player the player we are generating actions for.
   * @return all legal buy reserved card actions for the given player in the given game state.
   */
  public static List<Action> getLegalActions(SplendorPlayer player) {
    List<Action> actions = new ArrayList<>();
    for (DevelopmentCardI c : player.getReservedCards()) {
      if (player.canAfford(c)) {
        actions.add(new BuyReservedCardAction(c));
      }
    }
    return actions;
  }

  @Override
  public void performAction(Player player, Board board) {
    player.removeReservedCard((DevelopmentCardI) this.getCard());
    try {
      player.buyCard((DevelopmentCardI) this.getCard());
    } catch (InsufficientResourcesException e) {
      System.out.println(e);
    }
  }
}
