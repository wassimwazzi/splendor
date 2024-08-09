package splendor.model.game.deck;

import org.junit.jupiter.api.*;
import splendor.model.game.Color;
import splendor.model.game.card.DevelopmentCardI;

public class DeckTest {
	private Deck greenDeck;
	private Deck redDeck;
	
	// TODO: Initialize Red Deck (any valid level)
	@BeforeEach
	public void setUp() throws Exception {
		greenDeck = new Deck(Color.GREEN);
		//redDeck = new Deck(Color.RED,1);
	}
	
	@Test
	void initIllegalRedDeckLevel() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Deck(Color.RED,4);
		});
		Assertions.assertTrue(true);
	}
	
	@Test
	void initNonRedDeckWithLevel() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Deck(Color.GREEN,3);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void validateFacedUpCard() {
		Assertions.assertEquals(greenDeck.getFaceUpCards().length,3);
	}
	
	@Test
	void validateCardIsFacedUp() {
		DevelopmentCardI[] facedUpCards = greenDeck.getFaceUpCards();
		Assertions.assertEquals(greenDeck.isFaceUp(facedUpCards[0]), 0);
	}
	
	@Test
	void validateCardIsNotFacedUp() {
		Assertions.assertEquals(greenDeck.isFaceUp(null), -1);
	}
	
	@Test
	void validateInitialFacedDownCount() {
		Assertions.assertEquals(37, greenDeck.getCardCount());
	}
	
	@Test
	void validateTakeCardUntilEmpty() {
		while (greenDeck.getCardCount() > 0) {
			greenDeck.takeCard(0);
		}
		Assertions.assertEquals(greenDeck.getCardCount(), 0);
		greenDeck.takeCard(0);
		Assertions.assertEquals(null,greenDeck.takeCard(0));
	}
	
	@Test
	void validateColor() {
		Assertions.assertEquals(greenDeck.getColor(), Color.GREEN);
	}
	
	@Test
	void validateLevel() {
		Assertions.assertEquals(greenDeck.getLevel(), 1);
	}
	
	@Test
	void validateToStringOnRegularDeck() {
		Assertions.assertEquals(greenDeck.toString(), "green");
	}
	
	// TODO: Uncomment this for more line coverage when Red is initialized
	@Test
	void validateToStringOnRedDeck() {
		//Assertions.assertEquals(redDeck.toString(), "red1");
	}
}
