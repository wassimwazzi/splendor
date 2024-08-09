package splendor.model.game.card;

/**
 * Flyweight for a noble card.
 */
public class Noble extends AbstractCard {
  private static final int NUMBER_OF_NOBLES = 11;
  private static final Noble[] NOBLES = new Noble[NUMBER_OF_NOBLES];

  /**
   * Creates a new card. The Card values are based on the cardId.
   *
   * @param cardId the card id.
   */
  private Noble(int cardId) {
    super(cardId);
  }

  /**
   * Returns the noble with the given id.
   *
   * @param cardId the id of the noble.
   * @return the noble with the given id.
   */
  public static Noble get(int cardId) {
    if (cardId < 1 || cardId > NUMBER_OF_NOBLES) {
      throw new IllegalArgumentException(
          "The card id must be between 1 and " + NUMBER_OF_NOBLES + " got " + cardId
      );
    }
    int index = cardId - 1; // to get the index of the array
    if (NOBLES[index] == null) {
      NOBLES[index] = new Noble(cardId);
    }
    return NOBLES[index];
  }
}
