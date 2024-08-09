package splendor.model.game.payment;

import java.util.HashMap;
import java.util.Iterator;
import splendor.model.game.Color;

/**
 * A set of bonuses mapping colors to integers.
 * Bonuses are immutable.
 */
public class Bonus implements Iterable<Color> {
  private final HashMap<Color, Integer> bonusMap;

  /**
   * Creates a new bonus.
   *
   * @param bonusMap map of colors to amounts
   */
  public Bonus(HashMap<Color, Integer> bonusMap) {
    this.bonusMap = new HashMap<>(bonusMap);
  }

  /**
   * Empty Constructor.
   */
  public Bonus() {
    this.bonusMap = new HashMap<>();
  }

  /**
   * Returns the bonus for the given color. If no bonus exists for the given color,
   * 0 is returned.
   *
   * @param color the color
   * @return the bonus
   */
  public int getBonus(Color color) {
    return bonusMap.getOrDefault(color, 0);
  }

  /**
   * Iterator function.
   *
   * @return iterator for bonus map
   */
  @Override
  public Iterator<Color> iterator() {
    return bonusMap.keySet().iterator();
  }
}
