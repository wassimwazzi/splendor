package splendor.controller.action;

/**
 * Exception that is thrown when an action is invalid.
 */
public class InvalidAction extends Exception {
  /**
   * Exception.
   *
   * @param message to send.
   */
  public InvalidAction(String message) {
    super(message);
  }
}
