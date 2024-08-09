package splendor.model.game;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import splendor.model.game.payment.Cost;
import splendor.model.game.payment.Token;
import splendor.model.game.player.Inventory;
import splendor.model.game.player.Player;

public class InventoryTest {
  Player player = new Player("name", "color");
  Inventory inventory;

  @Before
  public void setUp() throws NoSuchFieldException {
    this.inventory = getPlayerInventory(player);
  }

  private void setPlayerBonus(Player player, HashMap<Color, Integer> bonus) throws NoSuchFieldException {
    Inventory inventory = getPlayerInventory(player);
    HashMap<Color, Integer> inventoryBonuses = getInventoryBonuses(inventory);
    for (Color color : bonus.keySet()) {
      inventoryBonuses.put(color, bonus.get(color));
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
    Field bonuses = inventory.getClass().getDeclaredField("discounts");
    bonuses.setAccessible(true);
    try {
      return (HashMap<Color, Integer>) bonuses.get(inventory);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
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

  @Test
  public void testPayForCost() {
    HashMap<Color, Integer> tokens = new HashMap<>();
    tokens.put(Color.BLUE, 1);
    tokens.put(Color.GREEN, 1);
    tokens.put(Color.RED, 1);
    tokens.put(Color.WHITE, 1);
    tokens.put(Color.BROWN, 1);
    try {
      setPlayerTokens(player, tokens);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    HashMap<Color, Integer> costMap = new HashMap<>();
    costMap.put(Color.BLUE, 1);
    costMap.put(Color.GREEN, 1);
    costMap.put(Color.RED, 1);
    costMap.put(Color.WHITE, 1);
    costMap.put(Color.BROWN, 1);
    Cost cost = new Cost(costMap);
    inventory.payFor(cost);
    for (Color color : tokens.keySet()) {
      assert (inventory.getTokens().get(color) == 0);
    }
  }

  @Test
  public void testPayForWithGoldTokens() {
    // it doesn't use gold tokens if it doesn't have to
    HashMap<Color, Integer> tokens = new HashMap<>();
    tokens.put(Color.BLUE, 1);
    tokens.put(Color.GREEN, 1);
    tokens.put(Color.RED, 1);
    tokens.put(Color.WHITE, 1);
    tokens.put(Color.BROWN, 1);
    tokens.put(Color.GOLD, 1);
    try {
      setPlayerTokens(player, tokens);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
      Assertions.fail();
    }
    HashMap<Color, Integer> costMap = new HashMap<>();
    costMap.put(Color.BLUE, 1);
    costMap.put(Color.GREEN, 1);
    costMap.put(Color.RED, 1);
    costMap.put(Color.WHITE, 1);
    costMap.put(Color.BROWN, 1);
    Cost cost = new Cost(costMap);
    inventory.payFor(cost);
    assertEquals(1, (int) inventory.getTokens().get(Color.GOLD));
  }

  @Test
  public void testPayForWithGoldTokens2() {
    // it uses gold tokens if it has to
    HashMap<Color, Integer> tokens = new HashMap<>();
    tokens.put(Color.BLUE, 1);
    tokens.put(Color.BROWN, 1);
    tokens.put(Color.GOLD, 1);
    try {
      setPlayerTokens(player, tokens);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
      Assertions.fail();
    }
    HashMap<Color, Integer> costMap = new HashMap<>();
    costMap.put(Color.BLUE, 1);
    costMap.put(Color.BROWN, 2);
    Cost cost = new Cost(costMap);
    inventory.payFor(cost);
    assertEquals(0, (int) inventory.getTokens().get(Color.GOLD));
  }

  @Test
  public void testPayForAppliesDiscounts() {
    HashMap<Color, Integer> tokens = new HashMap<>();
    tokens.put(Color.BLUE, 1);
    tokens.put(Color.GREEN, 1);
    tokens.put(Color.RED, 1);
    tokens.put(Color.WHITE, 1);
    tokens.put(Color.BROWN, 1);
    try {
      setPlayerTokens(player, tokens);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    HashMap<Color, Integer> costMap = new HashMap<>();
    costMap.put(Color.BLUE, 1);
    costMap.put(Color.GREEN, 1);
    costMap.put(Color.RED, 1);
    costMap.put(Color.WHITE, 1);
    costMap.put(Color.BROWN, 1);
    Cost cost = new Cost(costMap);
    HashMap<Color, Integer> bonus = new HashMap<>();
    bonus.put(Color.BLUE, 1);
    bonus.put(Color.GREEN, 1);
    bonus.put(Color.RED, 1);
    bonus.put(Color.WHITE, 1);
    bonus.put(Color.BROWN, 1);
    try {
      setPlayerBonus(player, bonus);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    inventory.payFor(cost);
    for (Color color : tokens.keySet()) {
      assertEquals(1, (int) inventory.getTokens().get(color));
    }
  }

  @Test
  public void testErrorPayFor() {
    HashMap<Color, Integer> tokens = new HashMap<>();
    tokens.put(Color.BLUE, 10);
    tokens.put(Color.GREEN, 10);
    tokens.put(Color.RED, 10);
    tokens.put(Color.WHITE, 10);
    tokens.put(Color.BROWN, 10);
    try {
      setPlayerTokens(player, tokens);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    HashMap<Color, Integer> costMap = new HashMap<>();
    costMap.put(Color.BLUE, 1);
    costMap.put(Color.GREEN, 1);
    costMap.put(Color.RED, 1);
    costMap.put(Color.WHITE, 1);
    costMap.put(Color.BROWN, 1);
    Cost cost = new Cost(costMap);
    inventory.payFor(cost);
    for (Color color : tokens.keySet()) {
      assertEquals(9, (int) inventory.getTokens().get(color));
    }
  }
}
