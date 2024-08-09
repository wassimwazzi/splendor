package splendor.model.game.card;

/**
 * Flyweight for a city card.
 */
public class City extends AbstractCard {
  private static final int NUMBER_OF_CITIES = 12;
  private static final City[] CITIES = new City[NUMBER_OF_CITIES];

  /**
   * Creates a new card. The Card values are based on the cardId.
   *
   * @param cardId the card id.
   */
  private City(int cardId) {
    super(cardId);
    // add prestige points to the cost
    this.getCost().updatePrestigePoints(this.getPrestigePoints());
  }

  /**
   * Returns the city with the given id.
   *
   * @param cardId the id of the city.
   * @return the city with the given id.
   */
  public static City get(int cardId) {
    if (cardId < 1 || cardId > NUMBER_OF_CITIES) {
      throw new IllegalArgumentException(
              "The card id must be between 1 and " + NUMBER_OF_CITIES + " got " + cardId
      );
    }
    int index = cardId - 1; // to get the index of the array
    if (CITIES[index] == null) {
      CITIES[index] = new City(cardId);
    }
    return CITIES[index];
  }
}

