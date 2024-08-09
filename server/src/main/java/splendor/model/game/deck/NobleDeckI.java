package splendor.model.game.deck;

import splendor.model.game.card.Noble;

/**
 * Interface for a deck of nobles.
 */
public interface NobleDeckI {

  /**
   * Get all nobles in the deck.
   *
   * @return Array of nobles
   */
  Noble[] getNobles();

  /**
   * Take a noble from deck.
   *
   * @param noble noble to remove
   * @throws IllegalArgumentException if noble is not in deck
   */
  void removeNoble(Noble noble);

}
