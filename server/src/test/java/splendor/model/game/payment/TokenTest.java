package splendor.model.game.payment;

import org.junit.jupiter.api.*;
import splendor.model.game.Color;

public class TokenTest {
	static Token blueToken;
	static Token goldToken;
	
	@BeforeAll
	static void setUp() {
		blueToken = Token.of(Color.BLUE);
		goldToken = Token.of(Color.GOLD);
	}
	
	@Test
	void validateBlueColor() {
		Assertions.assertEquals(blueToken.getColor(), Color.BLUE);
	}
	
	@Test
	void validateMaxBlueTokens() {
		Assertions.assertEquals(blueToken.maxAmount(),7);
	}
	
	@Test
	void validateGoldColor() {
		Assertions.assertEquals(goldToken.getColor(), Color.GOLD);
	}
	
	@Test
	void validateMaxGoldTokens() {
		Assertions.assertEquals(goldToken.maxAmount(),5);
	}
}
