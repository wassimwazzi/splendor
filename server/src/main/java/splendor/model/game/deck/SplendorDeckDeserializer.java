package splendor.model.game.deck;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import splendor.model.game.Color;

/**
 * Deserializer for SplendorDeck. Instantiates to Deck.
 */
public class SplendorDeckDeserializer implements JsonDeserializer<SplendorDeck> {

  /**
   * Deserialize a SplendorDeck.
   *
   * @param json    the json
   * @param typeOfT the type of t
   * @param context the context
   * @return the splendor deck
   * @throws JsonParseException the json parse exception
   */
  @Override
  public SplendorDeck deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    JsonElement colorElement = jsonObject.get("color");
    JsonElement levelElement = jsonObject.get("level");

    if (colorElement == null || levelElement == null) {
      throw new JsonParseException("Invalid SplendorDeck JSON: " + json);
    }

    Color color = context.deserialize(colorElement, Color.class);
    int level = levelElement.getAsInt();

    if (color == Color.RED) {
      return new Deck(color, level);
    }
    return new Deck(color);
  }
}

