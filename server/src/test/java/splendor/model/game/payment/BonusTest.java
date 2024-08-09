package splendor.model.game.payment;

import org.junit.jupiter.api.*;
import java.util.HashMap;
import java.util.Iterator;
import splendor.model.game.Color;

public class BonusTest {
	private Bonus emptyBonus;
	private Bonus filledBonus;
	
	@BeforeEach
	public void setUp() throws Exception {
		HashMap<Color,Integer> bonusMap = new HashMap<Color,Integer>();
		bonusMap.put(Color.BLUE, 2);
		bonusMap.put(Color.RED, 1);
		emptyBonus = new Bonus();
		filledBonus = new Bonus(bonusMap);
	}
	
	@Test
	void validateEmptyBonus() {
		Assertions.assertEquals(emptyBonus.getBonus(Color.BLUE),0);
	}
	
	@Test
	void validateNonEmptyBonus() {
		Assertions.assertEquals(filledBonus.getBonus(Color.BLUE),2);
	}
	
	@Test
	void validateIterator() {
		Iterator<Color> testIterator = filledBonus.iterator();
		testIterator.next();
		testIterator.next();
		Assertions.assertFalse(testIterator.hasNext());	
	}

}
