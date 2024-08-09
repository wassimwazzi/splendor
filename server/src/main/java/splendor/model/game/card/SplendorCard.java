package splendor.model.game.card;

import splendor.model.game.payment.Bonus;
import splendor.model.game.payment.Cost;

/**
 * This interface represents a splendor card. It can be a noble or a normal card.
 */
public interface SplendorCard {

  /**
   * The cost of the card.
   *
   * @return the cost of the card.
   */
  Cost getCost();

  /**
   * The prestige points of the card.
   *
   * @return the prestige points of the card.
   */
  int getPrestigePoints();

  /**
   * The card id.
   *
   * @return the card id.
   */
  int getCardId();

  /**
   * The bonus of the card.
   *
   * @return the bonus of the card.
   */
  Bonus getBonus();
}
