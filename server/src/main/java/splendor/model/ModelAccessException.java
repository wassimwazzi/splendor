package splendor.model;

/**
 * Copied from BoardGamePlatform.
 * Custom Exception that is fired whenever model modifications are requested that would lead to an
 * inconsistent state.
 *
 * @Author Maximilian Schiedermeier
 * @Date December 2020
 */
public class ModelAccessException extends Exception {
  /**
   * ModelAccessException.
   *
   * @param cause string
   */
  public ModelAccessException(String cause) {
    super(cause);
  }
}
