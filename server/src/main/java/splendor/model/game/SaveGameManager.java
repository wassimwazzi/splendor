package splendor.model.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import splendor.model.game.card.DevelopmentCardDeserializer;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.deck.SplendorDeck;
import splendor.model.game.deck.SplendorDeckDeserializer;

/**
 * This class is responsible for saving games into json files, and loading them.
 */
@Component
public class SaveGameManager {
  private final String saveGamePath;
  private final Logger logger = Logger.getLogger(SaveGameManager.class.getName());
  private final Gson gson;

  /**
   * Constructor.
   *
   * @param saveGamePath the save game path.
   */
  @Autowired
  public SaveGameManager(@Value("${savegame.location}") String saveGamePath) {
    gson = new GsonBuilder()
        .registerTypeAdapter(SplendorDeck.class, new SplendorDeckDeserializer())
        .registerTypeAdapter(DevelopmentCardI.class, new DevelopmentCardDeserializer())
        .create();
    this.saveGamePath = saveGamePath;
    // Create the savegame directory if it does not exist
    File directory = new File(saveGamePath);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  /**
  * Save a game into a json file.
  *
  * @param game the game to save
  */
  public void saveGame(SplendorGame game) {
    String saveGameId = game.getGameInfo().getSavegame();
    logger.info("Saving game " + saveGameId);
    String json = new Gson().toJson(game);
    writeToFile(json, saveGameId);
  }

  /**
   * Loads all games from the savegame directory.
   *
   * @return a HashMap of all loaded games, keyed by their game IDs
   */
  public HashMap<String, SplendorGame> loadAllGames() {
    logger.info("Loading all games from " + saveGamePath);
    File directory = new File(saveGamePath);
    File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
    if (files == null) {
      logger.info("No saved games found in " + saveGamePath);
      return new HashMap<>();
    }
    HashMap<String, SplendorGame> games = new HashMap<>();
    for (File file : files) {
      String fileName = file.getName();
      String saveGameId = fileName.substring(0, fileName.lastIndexOf("."));
      SplendorGame game = readFromFile(file.getAbsolutePath());
      games.put(saveGameId, game);
    }
    return games;
  }

  /**
   * Write a json string to a file.
   *
   * @param json the json string to write
   * @param saveGameId the id of the game
   * @throws RuntimeException if the file could not be written
   */
  private void writeToFile(String json, String saveGameId) throws RuntimeException {
    String fileName = saveGamePath + "/" + saveGameId + ".json";
    try (FileWriter file = new FileWriter(fileName)) {
      file.write(json);
      file.flush();
      logger.info("Game saved to file " + fileName);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Could not write to file " + fileName);
    }
  }

  /**
   * Read a json string from a file.
   *
   * @param fileName the name of the file to read from
   * @return the board read from the file
   */
  private SplendorGame readFromFile(String fileName) {
    try (FileReader reader = new FileReader(fileName)) {
      SplendorGame game = gson.fromJson(reader, SplendorGame.class);
      logger.info("Game loaded from file " + fileName);
      logger.info("Game Info: " + game.getGameInfo());
      return game;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Could not read file");
    }
  }
}
