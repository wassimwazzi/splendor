package splendor.model.game.card;

import org.junit.jupiter.api.*;

import splendor.model.game.Color;
import splendor.model.game.payment.Bonus;
import splendor.model.game.payment.Cost;

public class NobleTest {

	@Test
	void validateGetNobleClass() {
		Assertions.assertEquals(Noble.class, Noble.get(1).getClass());
	}
	
	@Test
	void tryGetIllegalNoble() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			Noble.get(-1);
		});
		Assertions.assertTrue(true);
	}
	
	@Test
	void validateGetCost() {
		Cost testCost = Noble.get(4).getCost();
		Assertions.assertEquals(3, testCost.getValue(Color.BROWN));
	}
	
	@Test
	void validateGetBonus() {
		Bonus testBonus = Noble.get(4).getBonus();
		Assertions.assertEquals(0, testBonus.getBonus(Color.GREEN));
	}
	
	// TODO: Test non-zero prestige points when JSON file is complete
	@Test
	void validateGetPrestigePoints() {
		int testPrestigePoints = Noble.get(1).getPrestigePoints();
		Assertions.assertEquals(3, testPrestigePoints);
	}
	
	@Test
	void validateGetId() {
		int testId = Noble.get(3).getCardId();
		Assertions.assertEquals(testId, 3);
	}
}
