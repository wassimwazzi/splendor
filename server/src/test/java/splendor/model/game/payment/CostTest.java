package splendor.model.game.payment;

import org.junit.jupiter.api.*;
import java.util.HashMap;
import java.util.Iterator;

import splendor.model.game.Color;

public class CostTest {
	HashMap<Color, Integer> testCardCost;
	Cost testCost;
	
	@BeforeEach
	void setUp() {
		testCardCost = new HashMap<Color, Integer>();
		testCardCost.put(Color.BLUE, 5);
		testCost = new Cost(testCardCost);
	}
	
	@AfterEach
	void cleanUp() {
		testCardCost.clear();
	}
	
	@Test
	void validateCostValue() {
		int blueCost = testCost.getValue(Color.BLUE);
		Assertions.assertEquals(blueCost, 5);
	}
	
	@Test
	void validateMultiCostValue() {
		testCardCost.put(Color.GREEN, 2);
		testCardCost.put(Color.RED, 7);
		testCost = new Cost(testCardCost);
		
		int blueCost = testCost.getValue(Color.BLUE);
		Assertions.assertEquals(blueCost, 5);
		
		int greenCost = testCost.getValue(Color.GREEN);
		Assertions.assertEquals(greenCost, 2);
		
		int redCost = testCost.getValue(Color.RED);
		Assertions.assertEquals(redCost, 7);
	}
	
	@Test
	void validateDefaultValue() {
		int goldCost = testCost.getValue(Color.GOLD);
		Assertions.assertEquals(goldCost, 0);
	}
	
	@Test
	void checkUnaffordableCostGivenResources() {
		HashMap<Color, Integer> unaffordableResources = new HashMap<Color, Integer>();
		unaffordableResources.put(Color.BLUE, 4);
		
		Assertions.assertFalse(testCost.isAffordable(unaffordableResources));
	}
	
	@Test
	void checkAffordableCostGivenResources() {
		HashMap<Color, Integer> affordableResources = new HashMap<Color, Integer>();
		affordableResources.put(Color.BLUE, 5);
		
		Assertions.assertTrue(testCost.isAffordable(affordableResources));
	}
	
	@Test
	void validateIterator() {
		Iterator<Color> testIterator = testCost.iterator();
		testIterator.next();
		Assertions.assertFalse(testIterator.hasNext());
	}

	@Test
	void testUsingGoldToPay() {
		HashMap<Color, Integer> resources = new HashMap<Color, Integer>();
		resources.put(Color.BLUE, 3);
		resources.put(Color.GOLD, 2);

		Assertions.assertTrue(testCost.isAffordable(resources));
	}

	@Test
	void testUsingGoldToPayNotEnough() {
		HashMap<Color, Integer> resources = new HashMap<Color, Integer>();
		resources.put(Color.BLUE, 3);
		resources.put(Color.GOLD, 1);

		Assertions.assertFalse(testCost.isAffordable(resources));
	}

	@Test
	void testUsingGoldToPayMultiple() {
		HashMap<Color, Integer> resources = new HashMap<Color, Integer>();
		resources.put(Color.BLUE, 3);
		resources.put(Color.GREEN, 3);
		resources.put(Color.GOLD, 2);

		HashMap<Color, Integer> cost = new HashMap<Color, Integer>();
		testCardCost.put(Color.BLUE, 4);
		testCardCost.put(Color.GREEN, 4);
		Cost testCost = new Cost(cost);

		Assertions.assertTrue(testCost.isAffordable(resources));
	}
}
