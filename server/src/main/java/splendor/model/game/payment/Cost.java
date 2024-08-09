package splendor.model.game.payment;

import java.util.HashMap;
import java.util.Iterator;
import splendor.model.game.Color;

/**
 * Cost is a map of token colors to integers. It represents the cost of a card.
 */
public class Cost implements Iterable<Color> {
  private final HashMap<Color, Integer> costMap;
  private int prestigePoints;

  /**
   * Creates a new cost.
   *
   * @param costMap the map with cost per colour.
   */
  public Cost(HashMap<Color, Integer> costMap) {
    this.costMap = new HashMap<>(costMap);
    this.prestigePoints = 0;
  }

  /**
   * Creates a new cost.
   */
  public Cost(HashMap<Color, Integer> costMap, int prestigePoints) {
    this.costMap = new HashMap<>(costMap);
    this.prestigePoints = prestigePoints;
  }

  /**
   * Update prestige points.
   */
  public void updatePrestigePoints(int prestigePoints) {
    this.prestigePoints = prestigePoints;
  }

  /**
   * Returns the value of the cost for the given color. If no cost exists for the given color,
   * 0 is returned.
   *
   * @param color the color
   * @return the cost
   */
  public int getValue(Color color) {
    return costMap.getOrDefault(color, 0);
  }

  /**
   * Checks if the given resources are sufficient to pay this cost.
   *
   * @param resources a Cost to add
   * @return true if the given resources are sufficient to pay this cost
   */
  public boolean isAffordable(HashMap<Color, Integer> resources) {
    HashMap<Color, Integer> remainingCosts = new HashMap<>(costMap);
    int goldCount = resources.getOrDefault(Color.GOLD, 0);

    for (Color color : costMap.keySet()) {
      int requiredCount = remainingCosts.getOrDefault(color, 0);
      int availableCount = resources.getOrDefault(color, 0);

      if (color == Color.GOLD) {
        continue; // skip gold cost
      }

      if (availableCount < requiredCount) {
        int goldNeeded = requiredCount - availableCount;
        if (goldCount < goldNeeded) {
          return false;
        }
        goldCount -= goldNeeded;
        remainingCosts.put(color, 0);
      } else {
        remainingCosts.put(color, 0);
      }
    }

    return true;
  }

  /**
   * Checks isAffordable with prestige points.
   *
   * @param resources a Cost to add
   */
  public boolean isAffordable(HashMap<Color, Integer> resources, int prestigePoints) {
    return prestigePoints >= this.prestigePoints && isAffordable(resources);
  }

  /**
   * Iterator function for cost map.
   *
   * @return iterator
   */
  @Override
  public Iterator<Color> iterator() {
    return costMap.keySet().iterator();
  }

  /**
   * Getter for the cost.
   *
   * @return the cost map
   */
  public HashMap<Color, Integer> getCost() {
    return this.costMap;
  }
}
