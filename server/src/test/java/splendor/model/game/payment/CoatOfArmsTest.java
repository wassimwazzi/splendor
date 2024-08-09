package splendor.model.game.payment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import splendor.model.game.Color;
import splendor.model.game.player.Inventory;
import splendor.model.game.player.Player;

public class CoatOfArmsTest {
  Player player = new Player("Wassim", "Blue");

  private void addBonusToPlayer(Player player, HashMap<Color, Integer> bonus) throws NoSuchFieldException {
    Inventory inventory = getPlayerInventory(player);
    HashMap<Color, Integer> inventoryBonuses = getInventoryBonuses(inventory);
    for (Color color : bonus.keySet()) {
      inventoryBonuses.put(color, bonus.get(color));
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
    // use reflection to get the bonuses
    Field bonuses = inventory.getClass().getDeclaredField("discounts");
    bonuses.setAccessible(true);
    try {
      return (HashMap<Color, Integer>) bonuses.get(inventory);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testCanUnlock() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // make canUnlock public so we can test it with reflection
    Method method = CoatOfArms.class.getDeclaredMethod("canUnlock", Player.class);
    method.setAccessible(true);
    CoatOfArms coatOfArms = CoatOfArms.get(1);
    // add bonuses to player
    HashMap<Color, Integer> bonus = new HashMap<>();
    bonus.put(Color.RED, 3);
    bonus.put(Color.WHITE, 1);
    try {
      addBonusToPlayer(player, bonus);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    assertTrue((boolean) method.invoke(coatOfArms, player));
  }

  @Test
  public void testAddUnlockedCoatOfArms() {
    HashMap<Color, Integer> bonus = new HashMap<>();
    bonus.put(Color.RED, 3);
    bonus.put(Color.WHITE, 1);
    try {
      addBonusToPlayer(player, bonus);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    CoatOfArms.addUnlockedCoatOfArms(player);
    assertTrue(player.getCoatOfArms().contains(CoatOfArms.get(1)));
  }

  @Test
  public void testGetter() {
    CoatOfArms coat = CoatOfArms.get(1);
    Assertions.assertEquals(1, coat.getId());
  }

}
