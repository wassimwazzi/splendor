package splendor.controller.lobbyservice;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import javax.naming.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import splendor.controller.helper.TokenHelper;

@RunWith(MockitoJUnitRunner.class)
public class RegistratorTest {
  TokenHelper tokenHelper = Mockito.mock(TokenHelper.class);
  GameServiceParameters gameServiceParameters = Mockito.mock(GameServiceParameters.class);
  Unirest unirest = Mockito.mock(Unirest.class);

  Registrator registrator = new Registrator(gameServiceParameters, tokenHelper);

  @Before
  public void setUp() throws AuthenticationException, UnirestException {
    when(gameServiceParameters.getOauth2Name()).thenReturn("oauth2Name");
    when(gameServiceParameters.getOauth2Password()).thenReturn("oauth2Password");
    when(tokenHelper.get(anyString(), anyString())).thenReturn("token");
    when(tokenHelper.get(anyString(), anyString())).thenReturn("token");
//    HttpRequestWithBody request = Mockito.mock(HttpRequestWithBody.class);
//    HttpResponse response = Mockito.mock(HttpResponse.class);
//    when(unirest.delete(anyString())).thenReturn(request);
//    when(unirest.put(anyString())).thenReturn(request);
//    when(request.header(anyString(), anyString())).thenReturn(request);
//    when(request.asString()).thenReturn(response);
//    when(response.getStatus()).thenReturn(200);

  }

  @Test
  public void testRegister() throws AuthenticationException, UnirestException {
    registrator.register();
    //    verify(tokenHelper).get(anyString(), anyString());
    // TODO: How can I test the REST call to the LS with the token?
  }

  @Test
  public void testDeregister() {
//    registrator.deregisterFromLobbyService();
  }
}
