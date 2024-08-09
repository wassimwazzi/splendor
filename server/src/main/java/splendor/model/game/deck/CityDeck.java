package splendor.model.game.deck;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.json.JSONObject;
import splendor.model.game.JsonParameters;
import splendor.model.game.card.City;

/**
 * Implementation of a City Deck.
 */
public class CityDeck implements CityDeckI {
  private static JSONObject citiesJson;

  private final int deckSize = getCityJson().getInt("deckSize");

  private final City[] cities = new City[deckSize];

  /**
   * create a city deck with cities.
   */
  public CityDeck() {
    addCitiesToDeck();
  }

  /**
   * Get all cities in the deck.
   *
   * @return copy of Array of cities
   */
  @Override
  public City[] getCities() {
    return cities.clone();
  }

  /**
   * Take a city from deck.
   *
   * @param city city to remove
   * @throws IllegalArgumentException if city is not in deck
   */
  @Override
  public void removeCity(City city) {
    for (int i = 0; i < cities.length; i++) {
      if (cities[i] == city) {
        cities[i] = null;
        return;
      }
    }
    throw new IllegalArgumentException("City not in deck");
  }

  /**
   * Add cities to deck.
   */
  private void addCitiesToDeck() {
    int deckIndex = 0;
    for (int index : getCityIndices()) {
      cities[deckIndex] = City.get(index);
      deckIndex++;
    }
  }

  /**
   * Get a random set of indices of size deckSize from 0 to length of cities array.
   *
   * @return set of indices
   */
  private Set<Integer> getCityIndices() {
    int totalSize = getCityJson().getJSONArray("cities").length();
    Random rand = new Random();
    // Create a hash set to store distinct indices
    Set<Integer> indexSet = new HashSet<>();

    // Generate deckSize distinct indices+
    while (indexSet.size() < deckSize) {
      int index = rand.nextInt(totalSize + 1);
      if (index == 0) {
        continue;
      }
      indexSet.add(index);
    }

    return indexSet;
  }

  /**
   * Get Json of cities.
   */
  private JSONObject getCityJson() {
    if (citiesJson == null) {
      citiesJson = JsonParameters.getCitiesJson();
    }
    return citiesJson;
  }
}
