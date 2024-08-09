package splendor.model.game.deck;

import splendor.model.game.Color;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.SplendorCard;

/**
 * This interface separates the deck from a card source. It is used to
 * represent the decks on the board.
 */
public interface SplendorDeck {
  /**
   * Take a card face up card.
   * Card is replaced with a new card from the deck.
   *
   * @param pos position.
   * @return the card.
   */
  DevelopmentCardI takeCard(int pos);

  /**
   * Check if the deck contains a card.
   *
   * @param card the card.
   * @return if the card is face up, return the position of the card, otherwise return -1.
   */
  int isFaceUp(DevelopmentCardI card);

  /**
   * Returns the face up cards.
   *
   * @return the face up cards
   */
  DevelopmentCardI[] getFaceUpCards();

  /**
   * The number of cards in the deck.
   *
   * @return the number of cards in the deck.
   */
  int getCardCount();

  /**
   * The color of the deck.
   *
   * @return color.
   */
  Color getColor();

  /**
   * The level of the deck.
   *
   * @return level.
   */
  int getLevel();

  /**
   * get the next faced down card.
   *
   * @return the next faced down card
   */
  SplendorCard getNextFacedDownCard();
}
