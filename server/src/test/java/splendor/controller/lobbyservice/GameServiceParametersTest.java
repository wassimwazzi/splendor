package splendor.controller.lobbyservice;

import org.junit.Before;
import org.junit.Test;

public class GameServiceParametersTest {
  private final GameServiceParameters gameServiceParameters = new GameServiceParameters();
  private final String lobbyServiceLocation = "http://localhost:4242";

  private final String gameServiceDisplayName = "Splendor Display Name";

  private final String gameServiceName = "Splendor";

  private final String oauth2Name = "oauthname";

  private final String oauth2Password = "oauthpassword";

  private final String gameServiceLocation = "http://localhost:8080";

  private final int minPlayers = 2;

  private final int maxPlayers = 4;

  private final boolean webSupport = false;

  @Before
  public void setUp() {
    // use reflection to set the values of the private fields
    setPrivateField("lobbyServiceLocation", lobbyServiceLocation);
    setPrivateField("gameServiceDisplayName", gameServiceDisplayName);
    setPrivateField("gameServiceName", gameServiceName);
    setPrivateField("oauth2Name", oauth2Name);
    setPrivateField("oauth2Password", oauth2Password);
    setPrivateField("gameServiceLocation", gameServiceLocation);
    setPrivateField("minPlayers", minPlayers);
    setPrivateField("maxPlayers", maxPlayers);
    setPrivateField("webSupport", webSupport);
  }

  private void setPrivateField(String fieldName, Object value) {
    try {
      java.lang.reflect.Field field = gameServiceParameters.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(gameServiceParameters, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetLobbyServiceLocation() {
    assert lobbyServiceLocation.equals(gameServiceParameters.getLobbyServiceLocation());
  }

  @Test
  public void testGetGameServiceDisplayName() {
    assert gameServiceDisplayName.equals(gameServiceParameters.getGameServiceDisplayName());
  }

  @Test
  public void testGetGameServiceName() {
    assert gameServiceName.equals(gameServiceParameters.getGameServiceName());
  }

  @Test
  public void testGetOauth2Name() {
    assert oauth2Name.equals(gameServiceParameters.getOauth2Name());
  }

  @Test
  public void testGetOauth2Password() {
    assert oauth2Password.equals(gameServiceParameters.getOauth2Password());
  }

  @Test
  public void testGetName() {
    assert gameServiceName.equals(gameServiceParameters.getName());
  }

  @Test
  public void testToJson() {
    String gameServiceParametersString = gameServiceParameters.toJson();
    assert gameServiceParametersString.contains("location\":\"" + gameServiceLocation);
    assert gameServiceParametersString.contains("maxSessionPlayers\":\"" + maxPlayers);
    assert gameServiceParametersString.contains("minSessionPlayers\":\"" + minPlayers);
    assert gameServiceParametersString.contains("name\":\"" + gameServiceName);
    assert gameServiceParametersString.contains("displayName\":\"" + gameServiceDisplayName);
    assert gameServiceParametersString.contains("webSupport\":\"" + webSupport);
  }
}
