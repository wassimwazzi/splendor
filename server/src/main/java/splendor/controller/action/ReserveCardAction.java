package splendor.controller.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import splendor.model.game.Board;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.FacedDownCard;
import splendor.model.game.card.FacedDownCardTypes;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.deck.SplendorDeck;
import splendor.model.game.player.Player;
import splendor.model.game.player.SplendorPlayer;

/**
 * reserving card action.
 */
public class ReserveCardAction extends CardAction {
  private static CardType cardType = CardType.DevelopmentCard;


  /**
   * Creates a new card action.
   *
   * @param card the card
   */
  protected ReserveCardAction(SplendorCard card) {
    super(ActionType.RESERVE, card);
  }

  /**
   * Generates the list of actions for the player.
   *
   * @param game   the current game that is being played.
   * @param player the player we are generating actions for.
   * @return all legal actions for the given player in the given game state.
   */
  public static List<Action> getLegalActions(SplendorGame game, SplendorPlayer player) {
    List<Action> actions = new ArrayList<>();
    if (player.getReservedCards().size() == 3) {
      return actions;
    }
    for (SplendorDeck deck : game.getBoard().getDecks()) {
      for (DevelopmentCardI card : deck.getFaceUpCards()) {
        if (card != null) {
          actions.add(new ReserveCardAction(card));
        }
      }
    }

    for (FacedDownCardTypes t : FacedDownCardTypes.values()) {
      actions.add(new ReserveCardAction(new FacedDownCard(t)));
    }
    return actions;
  }

  /**
   * Performs the ReserveCard action for a player.
   *
   * @param player the player we are performing an action for.
   * @param board the board where we are performing the action.
   */
  @Override
  public void performAction(Player player, Board board) {
    SplendorCard card = this.getCard();
    if (card instanceof FacedDownCard) {
      card = board.removeFacedDownCard(((FacedDownCard) this.getCard()).getType());
    } else {
      board.removeCard(this.getCard());
    }
    boolean hasGoldToken = board.hasGoldToken();
    player.reserveCard((DevelopmentCardI) card, hasGoldToken);
    if (hasGoldToken) {
      HashMap<Color, Integer> tokens = new HashMap<>();
      tokens.put(Color.GOLD, 1);
      board.removeTokens(tokens);
    }
  }
}
