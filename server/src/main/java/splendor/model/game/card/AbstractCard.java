package splendor.model.game.card;

import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import splendor.model.game.Color;
import splendor.model.game.JsonParameters;
import splendor.model.game.payment.Bonus;
import splendor.model.game.payment.Cost;

/**
 * A splendor card.
 * Cards are immutable.
 */
public abstract class AbstractCard implements SplendorCard {

  private static JSONObject cardsJson;
  private static JSONObject noblesJson;
  private static JSONObject citiesJson;
  private final int cardId; // 1 indexed
  private final Cost cost;
  private final int prestigePoints;
  private final Bonus bonus;

  /**
   * Creates a new card. The Card values are based on the cardId.
   *
   * @param cardId the card id.
   */
  protected AbstractCard(int cardId) {
    this.cardId = cardId;
    this.cost = getCostFromJson();
    this.prestigePoints = getPrestigePointsFromJson();
    this.bonus = getBonusFromJson();
  }

  /**
   * Getter for cost.
   *
   * @return cost
   */
  @Override
  public Cost getCost() {
    return cost;
  }

  /**
   * Getter for prestige points.
   *
   * @return prestige points
   */
  @Override
  public int getPrestigePoints() {
    return prestigePoints;
  }

  /**
   * Getter for card id.
   *
   * @return card id
   */
  @Override
  public int getCardId() {
    return cardId;
  }

  /**
   * Getter for bonus.
   *
   * @return bonus
   */
  @Override
  public Bonus getBonus() {
    // Creates a copy to keep cards immutable.
    return bonus;
  }

  /**
   * Returns the cost of the card based on the cardId.
   *
   * @return the cost of the card.
   */
  protected Cost getCostFromJson() {
    JSONObject cost = getCardJson().getJSONObject("cost");
    HashMap<Color, Integer> costMap = getMapFromJson(cost);
    return new Cost(costMap);
  }

  /**
   * Returns the prestige points of the card based on the cardId.
   *
   * @return the prestige points of the card.
   */
  protected int getPrestigePointsFromJson() {
    if (getCardJson().has("prestige_points")) {
      return getCardJson().getInt("prestige_points");
    }
    return 0;
  }

  /**
   * Returns the bonus of the card based on the cardId.
   *
   * @return the bonus of the card.
   */
  protected Bonus getBonusFromJson() {
    if (!getCardJson().has("bonus")) {
      return new Bonus();
    }
    JSONObject bonus = getCardJson().getJSONObject("bonus");
    HashMap<Color, Integer> bonusMap = getMapFromJson(bonus);
    return new Bonus(bonusMap);
  }

  /**
   * Returns hash map of bonuses from the json input.
   *
   * @param bonus json
   * @return bonuses
   */
  private HashMap<Color, Integer> getMapFromJson(JSONObject bonus) {
    HashMap<Color, Integer> bonusMap = new HashMap<>();
    for (Color color : Color.values()) {
      String colorString = color.toString().toLowerCase();
      if (bonus.has(colorString)) {
        bonusMap.put(color, bonus.getInt(colorString));
      }
    }
    return bonusMap;
  }

  /**
   * Returns the card json object.
   *
   * @return the card json object.
   */
  protected JSONObject getCardJson() {
    JSONArray cards;
    if (this instanceof Noble) {
      cards = getNoblesJson().getJSONArray("nobles");
    } else if (this instanceof City) {
      cards = getCitiesJson().getJSONArray("cities");
    } else {
      cards = getCardsJson().getJSONArray("cards");
    }
    return cards.getJSONObject(cardId - 1); // -1 because cardId is 1 indexed
  }

  /**
   * Returns the json object of the cities file.
   *
   * @return the json object.
   */
  protected static JSONObject getCitiesJson() {
    if (citiesJson == null) {
      citiesJson = JsonParameters.getCitiesJson();
    }
    return citiesJson;
  }

  /**
   * Returns the json object of the cards file.
   *
   * @return the json object.
   */
  protected static JSONObject getCardsJson() {
    if (cardsJson == null) {
      cardsJson = JsonParameters.getCardsJson();
    }
    return cardsJson;
  }

  /**
   * Returns the json object of the nobles file.
   *
   * @return the json object.
   */
  protected static JSONObject getNoblesJson() {
    if (noblesJson == null) {
      noblesJson = JsonParameters.getNoblesJson();
    }
    return noblesJson;
  }
}
