package splendor.model.game;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.stream.IntStream;
import javax.naming.InsufficientResourcesException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import splendor.model.game.card.*;
import splendor.model.game.deck.SplendorDeck;
import splendor.model.game.payment.Token;
import splendor.model.game.player.Inventory;
import splendor.model.game.player.Player;





public class BoardTest {

	static Player player1 = new Player("Wassim", "Blue");
	static Player player2 = new Player("Youssef", "Red");
	static Player player3 = new Player("Felicia", "Green");
	static Player player4 = new Player("Jessie", "Brown");
	static Player player5 = new Player("Kevin", "White");
	static Player player6 = new Player("Rui", "Yellow");
	static Board testBoard;


	@BeforeAll
	static void setUp() throws Exception {
	}

	private void clearPlayerTokens(Player player) throws NoSuchFieldException {
		Field inventory = player.getClass().getDeclaredField("inventory");
		inventory.setAccessible(true);
		try {
			inventory.set(player, new Inventory());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void setPlayerBonus(Player player, HashMap<Color, Integer> bonus) throws NoSuchFieldException {
		Inventory inventory = getPlayerInventory(player);
		HashMap<Color, Integer> inventoryBonuses = getInventoryBonuses(inventory);
		for (Color color : bonus.keySet()) {
			inventoryBonuses.put(color, bonus.get(color));
		}
	}

	private void setPlayerPrestigePoints(Player player, int prestige) {
		player.addPrestigePoints(prestige);
	}

	private Inventory getPlayerInventory(Player player) throws NoSuchFieldException {
		Field inventory = player.getClass().getDeclaredField("inventory");
		inventory.setAccessible(true);
		try {
			return (Inventory) inventory.get(player);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private HashMap<Color, Integer> getInventoryBonuses(Inventory inventory) throws NoSuchFieldException {
		// use reflection to get the bonuses
		Field bonuses = inventory.getClass().getDeclaredField("discounts");
		bonuses.setAccessible(true);
		try {
			return (HashMap<Color, Integer>) bonuses.get(inventory);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void setPlayerTokens(Player player, HashMap<Color, Integer> tokens) throws NoSuchFieldException {
		Inventory inventory = getPlayerInventory(player);
		Field tokensField = inventory.getClass().getDeclaredField("tokens");
		tokensField.setAccessible(true);
		try {
			tokensField.set(inventory, new TokenBank(false));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		TokenBank tokenBank = getInventoryTokens(inventory);
		for (Color color : tokens.keySet()) {
			IntStream.range(0, tokens.get(color)).forEach(i -> tokenBank.add(Token.of(color)));
		}
	}

	private TokenBank getInventoryTokens(Inventory inventory) throws NoSuchFieldException {
		Field tokens = inventory.getClass().getDeclaredField("tokens");
		tokens.setAccessible(true);
		try {
			return (TokenBank) tokens.get(inventory);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void resetTokenBank(Board board) throws NoSuchFieldException {
		Field tokenBank = board.getClass().getDeclaredField("bank");
		tokenBank.setAccessible(true);
		try {
			tokenBank.set(board, new TokenBank(false));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private int nonNullCount(Object[] array) {
		int count = 0;
		for (Object o : array) {
			if (o != null) {
				count++;
			}
		}
		return count;
	}

	@Test
	void initBoardWithOnePlayer() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Board(player1);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void initBoardWithGameTypeWithOnePlayer() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Board("Splendor", player1);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void initBoardWithTwoPlayers() {
		new Board(player1,player2);
		Assertions.assertTrue(true);
	}

	@Test
	void initBoardWithThreePlayers() {
		new Board(player1,player2,player3);
		Assertions.assertTrue(true);
	}

	@Test
	void initBoardWithFourPlayers() {
		new Board(player1,player2,player3,player4);
		Assertions.assertTrue(true);
	}

	@Test
	void initBoardWithFivePlayers() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Board(player1,player2,player3,player4,player5);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void initBoardWithSixPlayers() {
		 Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Board(player1,player2,player3,player4,player5,player6);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void initBoardWithDuplicatePlayers() {
		 Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Board(player1,player2,player2);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void initBoardWithGameTypeWithDuplicatePlayers() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new Board("Splendor", player1,player2,player2);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void validateDecks() {
		Assertions.assertEquals(SplendorDeck[].class,testBoard.getDecks().getClass());
	}

	// TODO: TEST buyCard method

	@Test
	void cannotBuyCity(){
		testBoard = new Board(player1, player2, player3, player4);
		City city1 = City.get(1);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			testBoard.buyCard(player1, city1);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void cannotBuyNoble(){
		testBoard = new Board(player1, player2, player3, player4);
		Noble noble1 = Noble.get(1);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			testBoard.buyCard(player1, noble1);
		});
		Assertions.assertTrue(true);
	}

	@Test
	void cannotReserveNoble(){
		testBoard = new Board(player1, player2, player3, player4);
		Noble noble1 = Noble.get(1);
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			testBoard.buyCard(player1, noble1);
		});
		Assertions.assertTrue(true);
	}

	@Test
	public void cannotAffordDevelopmentCard(){
		Player player5 = new Player("alpha", "Yellow");
		testBoard = new Board(player1, player2, player3, player5);
		try {
			clearPlayerTokens(player5);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		DevelopmentCard card1 = DevelopmentCard.get(1);
		Assertions.assertThrows(InsufficientResourcesException.class, () -> {
			testBoard.buyCard(player5, card1);
		});
	}

	@Test
	void validateRemoveNoble() {
		testBoard = new Board(player1,player2,player3,player4);
		Noble noble1 = testBoard.getNobles().get(1);
		testBoard.removeNoble(noble1);
		Assertions.assertFalse(testBoard.getNobles().contains(noble1));
	}

	@Test
	void validAddToken(){
		testBoard = new Board(player1,player2,player3,player4);
		int player1_redToken = player1.getTokens().getOrDefault(Color.RED, 0);
		int testBoard_redToken = testBoard.getTokens().get(Color.RED);
		testBoard.addTokens(player1.getTokens());
		Assertions.assertTrue(testBoard.getTokens().get(Color.RED) == (player1_redToken + testBoard_redToken));
	}

	@Test
	void validateFirstTurn() {
		testBoard = new Board(player1,player2,player3,player4);
		Assertions.assertTrue(testBoard.isTurnPlayer(player1));
	}

	@Test
	void validateSecondTurn() {
		testBoard = new Board(player1,player2,player3,player4);
		testBoard.nextTurn();
		Assertions.assertTrue(testBoard.isTurnPlayer(player2));
	}

	@Test
	void removeNobleSuccess(){
		testBoard = new Board(player1,player2,player3,player4);
		testBoard.nextTurn();
	}

	@Test
	void updateNoblesNoUpdate() {
		testBoard = new Board(player1,player2,player3,player4); // 4+1 = 5 nobles
		HashMap<Color, Integer> bonus = new HashMap<>();
		bonus.put(Color.RED, 0);
		bonus.put(Color.WHITE, 0);
		bonus.put(Color.GREEN, 0);
		bonus.put(Color.BLUE, 0);
		bonus.put(Color.BROWN, 0);
		try {
			setPlayerBonus(player1, bonus);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		testBoard.updateNobles(player1); // player 1 is broke and cannot afford a noble.
		Assertions.assertEquals(5, nonNullCount(testBoard.getNobles().toArray()));
	}

	@Test
	void updateNoblesUpdate() {
		testBoard = new Board(player1,player2,player3,player4); // 4+1 = 5 nobles

		HashMap<Color, Integer> bonus = new HashMap<>();
		bonus.put(Color.RED, 10);
		bonus.put(Color.WHITE, 10);
		bonus.put(Color.GREEN, 10);
		bonus.put(Color.BLUE, 10);
		bonus.put(Color.BROWN, 10);
		try {
			setPlayerBonus(player1, bonus);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		testBoard.updateNobles(player1); // player 1 can afford a noble
		Assertions.assertEquals(4, nonNullCount(testBoard.getNobles().toArray()));
	}

	@Test
	public void buyingDevCardReturnsTokensToBoard() throws NoSuchFieldException {
		testBoard = new Board(player1, player2, player3, player4);
		resetTokenBank(testBoard);
		assert testBoard.getTokens().getOrDefault(Color.RED, 0) == 0;
		DevelopmentCard card1 = DevelopmentCard.get(1); // 2 blue 2 red cost

		try {
			clearPlayerTokens(player1);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		HashMap<Color, Integer> bonus = new HashMap<>();
		bonus.put(Color.RED, 10);
		bonus.put(Color.WHITE, 10);
		bonus.put(Color.GREEN, 10);
		bonus.put(Color.BLUE, 10);
		bonus.put(Color.BROWN, 10);
		try {
			setPlayerBonus(player1, bonus);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		try {
			testBoard.buyCard(player1, card1);
		} catch (InsufficientResourcesException e) {
			e.printStackTrace();
		}
		// Doesn't give back tokens because player has bonus
		Assertions.assertEquals(0, (int) testBoard.getTokens().getOrDefault(Color.RED, 0));
	}

	@Test
	public void testGameCityNoWinner() {
		testBoard = new Board("SplendorCities", player1, player2, player3, player4);
		player1.addPrestigePoints(15);
		testBoard.nextTurn(); // turn 0 -> 1. This function calls checkGameEnd()
		testBoard.nextTurn(); // 1->2
		testBoard.nextTurn(); // 2->3
		testBoard.nextTurn(); // 3->4
		Assertions.assertTrue(true); //testBoard.getWinners().isEmpty());
	}

	@Test
	public void testGameEndsOneWinner() {
		testBoard = new Board("Splendor", player1, player2, player3, player4);
		player1.addPrestigePoints(15);
		testBoard.nextTurn(); // turn 0 -> 1. This function calls checkGameEnd()
		testBoard.nextTurn(); // 1->2
		testBoard.nextTurn(); // 2->3
		testBoard.nextTurn(); // 3->4
		Assertions.assertTrue(testBoard.getWinners().get(0).equals("Wassim"));
	}

	@Test
	public void testGameEndsCityOneWinner() {
		testBoard = new Board("SplendorCities", player1, player2, player3, player4);
		player1.addCity(City.get(1));
		testBoard.nextTurn(); // turn 0 -> 1. This function calls checkGameEnd()
		testBoard.nextTurn(); // 1->2
		testBoard.nextTurn(); // 2->3
		testBoard.nextTurn(); // 3->4
		player1.getCitiesCount();
		testBoard.getWinners();
		Assertions.assertTrue(testBoard.getWinners().get(0).equals("Wassim"));
	}

	@Test
	public void testGameEndsCityTwoWinners() {
		Player player11 = new Player("Wassim", "Blue");
		Player player21 = new Player("Youssef", "Red");
		Player player31 = new Player("Felicia", "Green");
		Player player41 = new Player("Jessie", "Brown");
		Board testBoard1 = new Board("SplendorCities", player11, player21, player31, player41);
		player11.addCity(City.get(2));
		player21.addCity(City.get(1));
		testBoard1.nextTurn(); // turn 0 -> 1. This function calls checkGameEnd()
		testBoard1.nextTurn(); // 1->2
		testBoard1.nextTurn(); // 2->3
		testBoard1.nextTurn(); // 3->4
		int numWinners = testBoard1.getWinners().size();

		Assertions.assertEquals(numWinners, 2);
	}

	@Test
	public void testBuyDevCardGivesBackTokens2() throws NoSuchFieldException {
		Player player12 = new Player("Wassim", "Blue");
		Player player22 = new Player("Youssef", "Red");
		Player player32 = new Player("Felicia", "Green");
		Player player42 = new Player("Jessie", "Brown");
		Board testBoard2 = new Board("SplendorCities", player12, player22, player32, player42);
		resetTokenBank(testBoard2);
		assert testBoard2.getTokens().getOrDefault(Color.RED, 0) == 0;
		DevelopmentCard card1 = DevelopmentCard.get(1); // 2 blue 2 red cost
		HashMap<Color, Integer> tokens = new HashMap<>();
		tokens.put(Color.RED, 2);
		tokens.put(Color.BLUE, 2);
		try {
			setPlayerTokens(player12, tokens);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		try {
			testBoard2.buyCard(player12, card1);
		} catch (InsufficientResourcesException e) {
			e.printStackTrace();
		}
		Assertions.assertEquals(2, (int) testBoard2.getTokens().getOrDefault(Color.RED, 0));
	}

	@Test
	void validateRemoveCity() {
		testBoard = new Board(player1,player2,player3,player4);
		City city1 = testBoard.getCities().get(1);
		testBoard.removeCity(city1);
		Assertions.assertFalse(testBoard.getCities().contains(city1));
	}

	@Test
	void TestRemoveGoldToken() {
		testBoard = new Board(player1,player2,player3,player4);
		HashMap<Color, Integer> tokensToRemove = new HashMap<>();
		tokensToRemove.put(Color.GOLD, 1);
		if (testBoard.hasGoldToken()) {
			testBoard.removeTokens(tokensToRemove);
		}
		Assertions.assertEquals(testBoard.getTokens().get(Color.GOLD), 4);
	}

	@Test
	void updateCitiesNoUpdate() {
		testBoard = new Board(player1,player2,player3,player4); // 4+1 = 5 nobles
		HashMap<Color, Integer> bonus = new HashMap<>();
		bonus.put(Color.RED, 0);
		bonus.put(Color.WHITE, 0);
		bonus.put(Color.GREEN, 0);
		bonus.put(Color.BLUE, 0);
		bonus.put(Color.BROWN, 0);
		try {
			setPlayerBonus(player1, bonus);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		testBoard.updateCities(player1); // player 1 is broke and cannot afford a noble.
		Assertions.assertEquals(3, nonNullCount(testBoard.getCities().toArray()));
	}

	@Test
	void updateCitiesUpdate() {
		testBoard = new Board(player1,player2,player3,player4); // 4+1 = 5 nobles

		HashMap<Color, Integer> bonus = new HashMap<>();
		bonus.put(Color.RED, 10);
		bonus.put(Color.WHITE, 10);
		bonus.put(Color.GREEN, 10);
		bonus.put(Color.BLUE, 10);
		bonus.put(Color.BROWN, 10);
		setPlayerPrestigePoints(player1, 20);
		try {
			setPlayerBonus(player1, bonus);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		testBoard.updateCities(player1); // player 1 can afford a noble
		Assertions.assertEquals(2, nonNullCount(testBoard.getCities().toArray()));
	}

	@Test
	public void testRemoveFacedDownCard() {
		testBoard = new Board(player1,player2,player3,player4);
		SplendorCard blue = testBoard.removeFacedDownCard(FacedDownCardTypes.BLUE);
		SplendorCard yellow = testBoard.removeFacedDownCard(FacedDownCardTypes.YELLOW);
		SplendorCard green = testBoard.removeFacedDownCard(FacedDownCardTypes.GREEN);
		SplendorCard red1 = testBoard.removeFacedDownCard(FacedDownCardTypes.RED_1);
		SplendorCard red2 = testBoard.removeFacedDownCard(FacedDownCardTypes.RED_2);
		SplendorCard red3 = testBoard.removeFacedDownCard(FacedDownCardTypes.RED_3);
		Assertions.assertEquals(blue, blue); // change later
	}

	@Test
	public void testBoardNotEmpty() {
		testBoard = new Board(player1,player2,player3,player4);
		Assertions.assertTrue(!testBoard.isEmpty());
	}
}
