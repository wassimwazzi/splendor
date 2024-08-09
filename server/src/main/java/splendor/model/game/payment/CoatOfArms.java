package splendor.model.game.payment;

import java.util.HashMap;
import splendor.model.game.Color;
import splendor.model.game.player.Player;

/**
 * Flyweight class of coat of arms.
 */
public class CoatOfArms {
  private final int id; // the id matches the order in the documentation
  private static final HashMap<Integer, CoatOfArms> COATS_OF_ARMS = new HashMap<>();

  static {
    for (int i = 1; i <= 5; i++) {
      COATS_OF_ARMS.put(i, new CoatOfArms(i));
    }
  }

  /**
   * Creates a new coat of arms.
   *
   * @param id the id of the coat of arms
   * @throws IllegalArgumentException if the id is not between 1 and 5
   */
  private CoatOfArms(int id) {
    if (id < 1 || id > 5) {
      throw new IllegalArgumentException("Coat of arms id must be between 1 and 5");
    }
    this.id = id;
  }

  /**
   * Returns the id of the coat of arms.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the coat of arms with the given id.
   *
   * @param id the id of the coat of arms
   * @return the coat of arms
   */
  public static CoatOfArms get(int id) {
    if (!COATS_OF_ARMS.containsKey(id)) {
      COATS_OF_ARMS.put(id, new CoatOfArms(id));
    }
    return COATS_OF_ARMS.get(id);
  }

  /**
   * Checks if player can unlock a coat of arms.
   *
   * @param player that we are checking
   * @return true if the player can unlock
   */
  private boolean canUnlock(Player player) {
    HashMap<Color, Integer> bonusMap = player.getBonuses();
    switch (id) {
      case 1:
        return bonusMap.getOrDefault(Color.RED, 0) >= 3
            && bonusMap.getOrDefault(Color.WHITE, 0) >= 1;
      case 2:
        return bonusMap.getOrDefault(Color.WHITE, 0) >= 2;
      case 3:
        return bonusMap.getOrDefault(Color.BLUE, 0) >= 3
            && bonusMap.getOrDefault(Color.BROWN, 0) >= 1;
      case 4:
        return bonusMap.getOrDefault(Color.GREEN, 0) >= 5
            && player.getNoblesCount() >= 1;
      case 5:
        return bonusMap.getOrDefault(Color.BROWN, 0) >= 3;
      default:
        return false;
    }
  }

  /**
   * Adds all unlocked coats of arms to the player.
   *
   * @param player the player
   */
  public static void addUnlockedCoatOfArms(Player player) {
    COATS_OF_ARMS.values().stream()
        .filter(coatOfArms -> coatOfArms.canUnlock(player))
        .forEach(player::addUnlockedCoatOfArms);
  }

  /**
   * Returns true if the coat of arms can only be used once.
   *
   * @return true if the coat of arms can only be used once
   */
  public boolean appliesOnce() {
    return id == 4 || id == 5;
  }

  /**
   * Applies the coat of arms to the player.
   *
   * @param player the player
   */
  public void apply(Player player) {
    if (!appliesOnce()) {
      return;
    }
    switch (id) {
      case 4:
        player.addPrestigePoints(5);
        break;
      case 5:
        int pp = player.getCoatOfArms().size();
        player.addPrestigePoints(pp);
        break;
      default:
        break;
    }
  }
}
