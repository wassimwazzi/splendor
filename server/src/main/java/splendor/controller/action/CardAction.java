package splendor.controller.action;


import splendor.model.game.card.SplendorCard;

/**
 * All actions that can be performed on a development card or noble by a player.
 */
public abstract class CardAction implements Action {

  private final SplendorCard card;
  private final ActionType actionType;

  private final long actionId;


  /**
   * Creates a new card action.
   *
   * @param actionType the type of the action
   * @param card the card
   */
  protected CardAction(ActionType actionType, SplendorCard card) {
    this.actionType = actionType;
    this.card = card;
    // generate a random id for the action
    this.actionId = (long) (Math.random() * Long.MAX_VALUE);
  }

  /**
   * Returns the index of the card.
   *
   * @return the index of the card
   */
  public SplendorCard getCard() {
    return card;
  }

  /**
   * Returns the id of the action.
   *
   * @return the id of the action
   */
  @Override
  public long getId() {
    return actionId;
  }

  /**
   * Returns the type of the action.
   *
   * @return the type of the action
   */
  @Override
  public ActionType getActionType() {
    return actionType;
  }
}
