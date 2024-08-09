package splendor.model.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.deck.SplendorDeck;
import splendor.model.game.deck.SplendorDeckDeserializer;

public class SaveGameManagerTest {

  String saveGamePath = "savegames";
  SaveGameManager saveGameManager;
  SplendorGame splendorGame;
  GameInfo gameInfo;

  public SaveGameManagerTest() {
    this.splendorGame = Mockito.mock(SplendorGame.class);
    this.gameInfo = Mockito.mock(GameInfo.class);
    when(splendorGame.getGameInfo()).thenReturn(gameInfo);
    when(gameInfo.getSavegame()).thenReturn("testSaveGame");
    saveGameManager = new SaveGameManager(saveGamePath);
  }

  @Test
  void testLoadAllGames() {
    saveGameManager.loadAllGames();
  }
}
