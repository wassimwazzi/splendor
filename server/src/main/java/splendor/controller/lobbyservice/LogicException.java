package splendor.controller.lobbyservice;

/**
 * Copied from BoardGamePlatform.
 * Custom Exception that is fired whenever the logic is instructed to handle parameters that
 * semantically are not applicable.
 *
 * @Author Maximilian Schiedermeier
 * @Date December 2020
 */
public class LogicException extends Exception {

  /**
   * Logic Exception.
   *
   * @param cause custom exception
   */
  public LogicException(String cause) {
    super(cause);
  }
}

