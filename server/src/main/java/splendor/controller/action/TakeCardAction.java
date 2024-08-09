package splendor.controller.action;

import java.util.ArrayList;
import java.util.List;
import splendor.model.game.Board;
import splendor.model.game.SplendorGame;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.deck.SplendorDeck;
import splendor.model.game.player.Player;

/**
 * Taking card action without payment class.
 */
public class TakeCardAction extends CardAction {
  private static CardType cardType = CardType.DevelopmentCard;


  /**
   * Creates a new card action.
   *
   * @param actionType  type of action
   * @param card the card
   */
  protected TakeCardAction(ActionType actionType, SplendorCard card) {
    super(actionType, card);
  }

  /**
   * Performs the TakeCard action.
   *
   * @param player the player that takes the card.
   * @param board  the board where this action takes place.
   */
  @Override
  public void performAction(Player player, Board board) {
    DevelopmentCardI card = (DevelopmentCardI) this.getCard();
    player.addCard(card);
    board.removeCard(this.getCard());
    card.getSpecialActions().forEach(player::addNextAction); // add special actions to player
    if (board.getNobles() == null) {
      if (player.containsNextAction(ActionType.TAKE_NOBLE)) {
        player.removeNextAction(ActionType.TAKE_NOBLE);
      } else if (player.containsNextAction(ActionType.RESERVE_NOBLE)) {
        player.removeNextAction(ActionType.RESERVE_NOBLE);
      }
    }

    player.updateReserveNobles();
    player.updateCoatOfArms();
  }

  /**
   * Generates the list of taking card actions for the player.
   *
   * @param game   the current game that is being played.
   * @param level  the level for cascade.
   * @return all legal taking card actions for the given player in the given game state.
   */
  public static List<Action> getLegalActions(SplendorGame game, int level) {
    List<Action> actions = new ArrayList<>();
    SplendorDeck[] decks = game.getBoard().getDecks();
    if (level == 2) {
      SplendorDeck yellowCards = decks[1];
      SplendorDeck red2Cards = decks[4];

      for (DevelopmentCardI card : yellowCards.getFaceUpCards()) {
        if (card != null) {
          actions.add(new TakeCardAction(ActionType.TAKE_CARD_1, card));
        }
      }

      for (DevelopmentCardI card : red2Cards.getFaceUpCards()) {
        if (card != null) {
          actions.add(new TakeCardAction(ActionType.TAKE_CARD_1, card));
        }
      }

    } else if (level == 1) {
      SplendorDeck greenCards = decks[0];
      SplendorDeck red1Cards = decks[3];

      for (DevelopmentCardI card : greenCards.getFaceUpCards()) {
        if (card != null) {
          actions.add(new TakeCardAction(ActionType.TAKE_CARD_2, card));

        }
      }
      for (DevelopmentCardI card : red1Cards.getFaceUpCards()) {
        if (card != null) {
          actions.add(new TakeCardAction(ActionType.TAKE_CARD_2, card));
        }
      }

    }
    return actions;
  }
}
