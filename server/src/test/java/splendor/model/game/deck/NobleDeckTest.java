package splendor.model.game.deck;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import splendor.model.game.card.Noble;

public class NobleDeckTest {
  private NobleDeck deck;

  @BeforeEach
  public void setUp() throws Exception {
    deck = new NobleDeck(2);
  }

  @Test
  void validateNoblesAreDistinct() {
    Noble[] nobles = deck.getNobles();
    for (int i = 0; i < nobles.length; i++) {
      for (int j = i + 1; j < nobles.length; j++) {
        Assertions.assertNotEquals(nobles[i], nobles[j]);
      }
    }
  }

  @Test
  void validateGetNoblesReturnsCopy() {
    Noble[] nobles1 = deck.getNobles();
    Noble[] nobles2 = deck.getNobles();
    Assertions.assertNotEquals(nobles1, nobles2);
  }

  @Test
  void validateRemoveNoble() {
    Noble[] nobles = deck.getNobles();
    deck.removeNoble(nobles[0]);
    Assertions.assertNull(deck.getNobles()[0]);
  }

  @Test
  void validateRemoveNobleThrowsException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      deck.removeNoble(null);
    });
  }

  @Test
  void validateNoblesAreRandom() {
    int sampleSize = 10;
    NobleDeck[] decks = new NobleDeck[sampleSize];
    for (int i = 0; i < sampleSize; i++) {
      decks[i] = new NobleDeck(2);
    }
    boolean same = true;
    for (int i = 0; i < sampleSize; i++) {
      for (int j = i + 1; j < sampleSize; j++) {
        same = decksHaveSameNobles(decks[i], decks[j]);
      }
    }
    Assertions.assertFalse(same);
  }

  private boolean decksHaveSameNobles(NobleDeck deck, NobleDeck deck1) {
    Noble[] nobles1 = deck.getNobles();
    Noble[] nobles2 = deck1.getNobles();
    for (int i = 0; i < nobles1.length; i++) {
      if (nobles1[i] != nobles2[i]) {
        return false;
      }
    }
    return true;
  }
}

