package splendor.model.game.payment;

/**
 * Interface for payment items. Used for tokens
 */
public interface PaymentItem {
  /**
   * the maximum amount allowed.
   *
   * @return the maximum amount.
   */
  public int maxAmount();
}
