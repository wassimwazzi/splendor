package splendor.model.game;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A class that loads the json files.
 */
public class JsonParameters {

  /**
   * Get the cards json.
   *
   * @return the cards json JSONObject
   */
  public static JSONObject getCardsJson() {
    InputStream cardsIs = JsonParameters.class.getClassLoader()
        .getResourceAsStream("cards.json");
    if (cardsIs == null) {
      throw new IllegalStateException("cards.json not found");
    }
    return new JSONObject(new JSONTokener(cardsIs));
  }

  /**
   * Get the nobles json.
   *
   * @return the nobles json JSONObject
   */
  public static JSONObject getNoblesJson() {
    InputStream noblesIs = JsonParameters.class.getClassLoader()
        .getResourceAsStream("nobles.json");
    if (noblesIs == null) {
      throw new IllegalStateException("nobles.json not found");
    }
    return new JSONObject(new JSONTokener(noblesIs));
  }

  /**
   * Get the cities json.
   *
   * @return the cities json JSONObject
   */
  public static JSONObject getCitiesJson() {
    InputStream citiesIs = JsonParameters.class.getClassLoader()
        .getResourceAsStream("cities.json");
    if (citiesIs == null) {
      throw new IllegalStateException("cities.json not found");
    }
    return new JSONObject(new JSONTokener(citiesIs));
  }
}
