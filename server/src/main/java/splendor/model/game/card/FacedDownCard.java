package splendor.model.game.card;

import splendor.model.game.payment.Bonus;
import splendor.model.game.payment.Cost;

/**
 * Class that acts as a faced down card used for reserving a card.
 */
public class FacedDownCard implements SplendorCard {

  private FacedDownCardTypes type;

  /**
   * Creates the FacedDownCard.
   *
   * @param type of card.
   */
  public FacedDownCard(FacedDownCardTypes type) {
    this.type = type;
  }

  /**
   * Getter for type.
   *
   * @return type
   */
  public FacedDownCardTypes getType() {
    return this.type;
  }

  @Override
  public Cost getCost() {
    return null;
  }

  @Override
  public int getPrestigePoints() {
    return 0;
  }

  @Override
  public int getCardId() {
    return 0;
  }

  @Override
  public Bonus getBonus() {
    return null;
  }
}
