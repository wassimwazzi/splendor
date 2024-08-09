package splendor.controller.helper;

import javax.naming.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatorTest {
  TokenHelper tokenHelper = Mockito.mock(TokenHelper.class);
  Authenticator authenticator = new Authenticator(tokenHelper);

  @Before
  public void setUp() {
    when(tokenHelper.validate(anyString(), anyString())).thenReturn(true);
    when(tokenHelper.isPlayer(anyString())).thenReturn(true);
  }

  @Test
  public void testAuthenticate() throws AuthenticationException {
    authenticator.authenticate("token", "username");
  }

  @Test
  public void testAuthenticateInvalidToken() throws AuthenticationException {
    when(tokenHelper.validate(anyString(), anyString())).thenReturn(false);
    assertThrows(AuthenticationException.class, () -> {
      authenticator.authenticate("token", "username");
    });
  }

  @Test
  public void testAuthenticateInvalidPlayer() throws AuthenticationException {
    when(tokenHelper.isPlayer(anyString())).thenReturn(false);
    assertThrows(AuthenticationException.class, () -> {
      authenticator.authenticate("token", "username");
    });
  }
}
