package splendor.model.game.deck;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.json.JSONObject;
import splendor.model.game.JsonParameters;
import splendor.model.game.card.Noble;

/**
 * Implementation of a Noble Deck.
 */
public class NobleDeck implements NobleDeckI {
  private static JSONObject noblesJson;

  private final int deckSize; // = getNobleJson().getInt("deckSize"); // TODO: remove

  private final Noble[] nobles;

  /**
   * Constructor for the noble deck. The deckSize is based on the number of players + 1.
   *
   * @param numPlayers number of players in the game.
   */
  public NobleDeck(int numPlayers) {
    deckSize = numPlayers + 1;
    nobles = new Noble[deckSize];
    addNoblesToDeck();
  }

  /**
   * Get all nobles in the deck.
   *
   * @return copy of Array of nobles
   */
  @Override
  public Noble[] getNobles() {
    return nobles.clone();
  }

  /**
   * Take a noble from deck.
   *
   * @param noble noble to remove
   * @throws IllegalArgumentException if noble is not in deck
   */
  @Override
  public void removeNoble(Noble noble) {
    for (int i = 0; i < nobles.length; i++) {
      if (nobles[i] == noble) {
        nobles[i] = null;
        return;
      }
    }
    throw new IllegalArgumentException("Noble not in deck");
  }

  /**
   * Add nobles to deck.
   */
  private void addNoblesToDeck() {
    int deckIndex = 0;
    for (int index : getNobleIndices()) {
      nobles[deckIndex] = Noble.get(index);
      deckIndex++;
    }
  }

  /**
   * Get a random set of indices of size deckSize from 0 to length of nobles array.
   *
   * @return set of indices
   */
  private Set<Integer> getNobleIndices() {
    int totalSize = getNobleJson().getJSONArray("nobles").length();
    Random rand = new Random();
    // Create a hash set to store distinct indices
    Set<Integer> indexSet = new HashSet<>();

    // Generate deckSize distinct indices
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
   * Get Json of nobles.
   */
  private JSONObject getNobleJson() {
    if (noblesJson == null) {
      noblesJson = JsonParameters.getNoblesJson();
    }
    return noblesJson;
  }
}
