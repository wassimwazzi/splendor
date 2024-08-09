package splendor.controller.action;

import java.util.HashMap;
import splendor.model.game.Color;

/**
 * Class to store all relevant data for an action.
 */
public class ActionData {
  private final HashMap<Color, Integer> payment;

  /**
   * Creates a new action data.
   *
   * @param payment the payment
   */
  public ActionData(HashMap<Color, Integer> payment) {
    this.payment = payment;
  }

  /**
   * Empty constructor.
   *
   */
  public ActionData() {
    this.payment = new HashMap<>();
  }

  /**
   * Gets the payment.
   *
   * @return the payment
   */
  public HashMap<Color, Integer> getPayment() {
    return payment;
  }
}
