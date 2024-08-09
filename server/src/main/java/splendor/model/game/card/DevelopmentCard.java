package splendor.model.game.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import splendor.controller.action.ActionType;
import splendor.model.game.Color;

/**
 * Flyweight class for a development card.
 */
public class DevelopmentCard extends AbstractCard implements DevelopmentCardI {
  private final Color color;
  private static final int NUMBER_OF_CARDS = 120;
  private static final DevelopmentCard[] CARDS = new DevelopmentCard[NUMBER_OF_CARDS];

  private final ArrayList<ActionType> specialActions = new ArrayList<>();
  private static final HashMap<String, ActionType> ACTION_TYPE_MAP = new HashMap<>();

  static {
    ACTION_TYPE_MAP.put("cascade_1", ActionType.TAKE_CARD_1);
    ACTION_TYPE_MAP.put("cascade_2", ActionType.TAKE_CARD_2);
    ACTION_TYPE_MAP.put("clone", ActionType.CLONE_CARD);
    ACTION_TYPE_MAP.put("reserve_noble", ActionType.RESERVE_NOBLE);
  }

  /**
   * Creates a new card. The Card values are based on the cardId.
   *
   * @param cardId the card id.
   */
  private DevelopmentCard(int cardId) {
    super(cardId);
    color = getColorFromJson(cardId);
    setSpecialAbilityFromJson();
  }

  /**
   * Getter for color.
   *
   * @return color
   */
  @Override
  public Color getColor() {
    return color;
  }

  /**
   * Returns the special actions of the card.
   *
   * @return special actions of the card.
   */
  @Override
  public List<ActionType> getSpecialActions() {
    return Collections.unmodifiableList(specialActions);
  }

  /**
   * Returns the development card with the given id.
   *
   * @param cardId the id of the development card.
   * @return the development card with the given id.
   */
  public static DevelopmentCard get(int cardId) {
    if (cardId < 1 || cardId > NUMBER_OF_CARDS) {
      throw new IllegalArgumentException("The card id must be between 1 and " + NUMBER_OF_CARDS);
    }
    int index = cardId - 1; // to get the index of the array
    if (CARDS[index] == null) {
      CARDS[index] = new DevelopmentCard(cardId);
    }
    return CARDS[index];
  }

  /**
   * Retrieves color of a card using the cardid.
   *
   * @param cardId check color of this card
   * @return Color object of card
   */
  private Color getColorFromJson(int cardId) {
    JSONObject map = getCardsJson().getJSONObject("color_map");
    for (String key : map.keySet()) {
      if (map.getJSONArray(key).getInt(0) <= cardId
              && map.getJSONArray(key).getInt(1) >= cardId) {
        if (key.contains("red")) {
          key = "red";
        }
        return Color.valueOf(key.toUpperCase());
      }
    }
    throw new IllegalArgumentException("The card id must be between 1 and " + NUMBER_OF_CARDS);
  }

  /**
   * Add special abilities from json.
   *
   */
  private void setSpecialAbilityFromJson() {
    JSONObject cardJson = super.getCardJson();
    if (cardJson.has("special")) {
      cardJson.getJSONArray("special").forEach((action) -> {
        specialActions.add(ACTION_TYPE_MAP.get(action));
      });
    }
  }
}
