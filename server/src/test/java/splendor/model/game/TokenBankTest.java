package splendor.model.game;

import org.junit.jupiter.api.*;
import splendor.model.game.payment.Token;

public class TokenBankTest {
	private TokenBank emptyTokenBank;
	private TokenBank filledTokenBank;
	
	@BeforeEach
	public void setUp() throws Exception {
		emptyTokenBank = new TokenBank(false);
		filledTokenBank = new TokenBank(true);
	}
	
	@Test
	void validateEmptyCount() {
		Assertions.assertEquals(emptyTokenBank.count(Token.of(Color.BLUE)), 0);
		Assertions.assertEquals(emptyTokenBank.count(Token.of(Color.BROWN)), 0);
		Assertions.assertEquals(emptyTokenBank.count(Token.of(Color.GREEN)), 0);
		Assertions.assertEquals(emptyTokenBank.count(Token.of(Color.RED)), 0);
		Assertions.assertEquals(emptyTokenBank.count(Token.of(Color.WHITE)), 0);
		Assertions.assertEquals(emptyTokenBank.count(Token.of(Color.GOLD)), 0);
	}
	
	@Test
	void validateSevenRegularTokensFilled() {
		Assertions.assertEquals(filledTokenBank.count(Token.of(Color.BLUE)), 7);
		Assertions.assertEquals(filledTokenBank.count(Token.of(Color.BROWN)), 7);
		Assertions.assertEquals(filledTokenBank.count(Token.of(Color.GREEN)), 7);
		Assertions.assertEquals(filledTokenBank.count(Token.of(Color.RED)), 7);
		Assertions.assertEquals(filledTokenBank.count(Token.of(Color.WHITE)), 7);
	}
	
	@Test
	void validateFiveGoldTokensFilled() {
		Assertions.assertEquals(filledTokenBank.count(Token.of(Color.GOLD)), 5);
	}
	
	@Test
	void validateNotContains() {
		Assertions.assertFalse(emptyTokenBank.contains(Token.of(Color.BLUE)));
	}
	
	@Test
	void validateContains() {
		Assertions.assertTrue(filledTokenBank.contains(Token.of(Color.BLUE)));
	}
	
	@Test
	void validateAddToken() {
		emptyTokenBank.add(Token.of(Color.BLUE));
		Assertions.assertEquals(emptyTokenBank.count(Token.of(Color.BLUE)), 1);
	}
	
	@Test
	void validateRemoveToken() {
		filledTokenBank.remove(Token.of(Color.BLUE));
		Assertions.assertEquals(filledTokenBank.count(Token.of(Color.BLUE)), 6);
	}
}
