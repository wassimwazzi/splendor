package splendor.controller.action;

/**
 * All actions that can be performed on a development card or noble by a player.
 */
public enum ActionType {
  /**
   * For buying card.
   */
  BUY,
  /**
   * For reserving card.
   */
  RESERVE,
  /**
   * For taking a noble.
   */
  TAKE_NOBLE,
  /**
   * For taking tokens.
   */
  TAKE_TOKENS,
  /**
   * For cascade level 1.
   */
  TAKE_CARD_1,
  /**
   * For cascade level 2.
   */
  TAKE_CARD_2,
  /**
   * For returning tokens.
   */
  RETURN_TOKENS,
  /**
   * For cloning card.
   */
  CLONE_CARD,
  /**
   * For reserving a noble.
   */
  RESERVE_NOBLE,
  /**
   * For buying a reserved card.
   */
  BUY_RESERVED_CARD,
  /**
   * For coat of arms (taking 1 token).
   */
  TAKE_ONE_TOKEN
}
