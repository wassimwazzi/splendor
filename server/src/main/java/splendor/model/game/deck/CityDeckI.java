package splendor.model.game.deck;

import splendor.model.game.card.City;

/**
 * Interface for a deck of cities.
 */
public interface CityDeckI {


  /**
  * Get all cities in the deck.
  *
  * @return Array of cities
  */
  City[] getCities();

  /**
   * Take a city from deck.
   *
   * @param city city to remove
   * @throws IllegalArgumentException if city is not in deck
   */
  void removeCity(City city);

}
