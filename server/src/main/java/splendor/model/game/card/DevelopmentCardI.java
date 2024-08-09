package splendor.model.game.card;

import java.util.List;
import splendor.controller.action.ActionType;
import splendor.model.game.Color;

/**
 * Interface for a development card.
 */
public interface DevelopmentCardI extends SplendorCard {
  /**
   * Get the colour of the card.
   *
   * @return colour
   */
  Color getColor();

  /**
   * Get any special actions of the card.
   *
   * @return special actions
   */
  List<ActionType> getSpecialActions();
}
