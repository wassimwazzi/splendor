package splendor.model.game.card;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Deserializer for DevelopmentCardI. Instantiates to DevelopmentCard.
 */
public class DevelopmentCardDeserializer implements JsonDeserializer<DevelopmentCardI> {

  /**
   * Deserialize a DevelopmentCardI.
   *
   * @param jsonElement the json
   * @param type the type of t
   * @param jsonDeserializationContext the context
   * @return the development card
   * @throws JsonParseException the json parse exception
   */
  @Override
  public DevelopmentCardI deserialize(JsonElement jsonElement, Type type,
                                      JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    JsonElement cardIdElement = jsonElement.getAsJsonObject().get("cardId");

    if (cardIdElement == null) {
      throw new JsonParseException("Invalid DevelopmentCard JSON: " + jsonElement);
    }

    int cardId = cardIdElement.getAsInt();
    return DevelopmentCard.get(cardId);
  }
}
