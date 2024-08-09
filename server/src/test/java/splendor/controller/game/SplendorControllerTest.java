package splendor.controller.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.util.Collections;
import javax.naming.AuthenticationException;
import javax.naming.InsufficientResourcesException;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import splendor.controller.action.Action;
import splendor.controller.action.BuyCardAction;
import splendor.controller.action.InvalidAction;
import splendor.controller.helper.Authenticator;
import splendor.controller.lobbyservice.Registrator;
import splendor.model.game.Board;
import splendor.model.game.player.Player;

public class SplendorControllerTest {
  private GameManager gameManager = mock(GameManager.class);
  private Authenticator authenticator = mock(Authenticator.class);
  private Registrator registrator = mock(Registrator.class);
  private SplendorController splendorController = new SplendorController(gameManager,
      authenticator, registrator);
  private long gameId = 1;
  Player[] players = new Player[2];
  private Board board;

  private Action action;
  private HttpServletRequest request = mock(HttpServletRequest.class);

  @Before
  public void setUp() throws AuthenticationException, InvalidAction,
      InsufficientResourcesException {
    players[0] = new Player("player1", "blue");
    players[1] = new Player("player2", "red");
    board = new Board(players);
    action = getAction();
    doNothing().when(authenticator).authenticate(anyString(), anyString());
    when(gameManager.exists(gameId)).thenReturn(true);
    when(gameManager.getBoard(anyLong())).thenReturn(board);
    when(gameManager.generateActions(anyLong(), anyString())).thenReturn(Collections.singletonList(action));
    when(gameManager.playerInGame(anyLong(), anyString())).thenReturn(true);
    doNothing().when(gameManager).performAction(anyLong(), anyString(), anyString(), any());
    when(request.getRequestURI()).thenReturn("http://localhost:8080");
  }

  private Action getAction() {
    return Mockito.mock(BuyCardAction.class);
  }

  @Test
  public void testGetBoard() {
    ResponseEntity response = splendorController.getBoard(
        gameId, request, "wassim", "abc123");
    assertEquals(response.getStatusCodeValue(), 200);
    assertEquals(response.getBody(), new Gson().toJson(board));
  }

  @Test
  public void testGetBoardAuthenticationFailure() throws AuthenticationException {
    doThrow(new AuthenticationException("Failed")).when(authenticator).authenticate(anyString(),
        anyString());
    ResponseEntity response = splendorController.getBoard(
        gameId, request, "wassim", "abc123");
    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void testGetBoardLongPoll() {
    ResponseEntity response = splendorController.getBoard(
        gameId, request, "wassim", "abc123");
    assertEquals(response.getStatusCodeValue(), 200);
    assertEquals(response.getBody(), new Gson().toJson(board));
  }

  @Test
  public void testGetBoardLongPollAuthenticationFailure() throws AuthenticationException {
    doThrow(new AuthenticationException("Failed")).when(authenticator).authenticate(anyString(),
        anyString());
    ResponseEntity response = splendorController.getBoard(
        gameId, request, "wassim", "abc123");
    assertEquals(response.getStatusCodeValue(), 401);
  }

  @Test
  public void testGetBoardLongPollInvalidGameId() {
    when(gameManager.exists(gameId)).thenReturn(false);
    ResponseEntity response = splendorController.getBoard(
        gameId, request, "wassim", "abc123");
    assertEquals(response.getStatusCodeValue(), 400);
  }

  @Test
  public void testGetBoardInvalidGameId() {
    when(gameManager.exists(gameId)).thenReturn(false);
    ResponseEntity response = splendorController.getBoard(gameId,
        request, "wassim", "abc123");
    assertEquals(response.getStatusCodeValue(), 400);
  }

  @Test
  public void testGetActions() {
    try {
      ResponseEntity response = splendorController.getActions(gameId, "player1",
      "player1", "abc123", request);
      assertEquals(response.getStatusCodeValue(), 200);
      assertEquals(response.getBody(), new Gson().toJson(Collections.singletonList(action)));
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGetActionsInvalidGameId() {
    when(gameManager.exists(gameId)).thenReturn(false);
    ResponseEntity response = splendorController.getActions(gameId, "player1",
        "player1", "abc123", request);
    assertEquals(response.getStatusCodeValue(), 400);
  }

  @Test
  public void testGetActionsInvalidPlayer() {
    when(gameManager.playerInGame(gameId, "player5")).thenReturn(false);
    ResponseEntity response = splendorController.getActions(gameId, "player1",
        "player5", "abc123", request);
    assertEquals(response.getStatusCodeValue(), 401);
  }

//  @Test
//  public void testAuthenticate() {
//    HttpServletRequest request = mock(HttpServletRequest.class);
//    HttpServletResponse httpResponse = mock(HttpServletResponse.class);
//    Object handler = mock(Object.class);
//    when(request.getParameter(anyString())).thenReturn("String");
//    when(request.getRequestURI()).thenReturn("http://fake.com");
//    try {
//      boolean response = splendorController.authenticate(
//          request, httpResponse, handler);
//      assertTrue(response);
//    } catch (Exception e) {
//      fail();
//    }
//  }

//  @Test
//  public void testPreHandleFailure() throws Exception {
//    HttpServletRequest request = mock(HttpServletRequest.class);
//    HttpServletResponse httpResponse = mock(HttpServletResponse.class);
//    Object handler = mock(Object.class);
//    when(request.getParameter(anyString())).thenReturn("String");
//    when(request.getRequestURI()).thenReturn("http://fake.com");
//    doThrow(new AuthenticationException("Failed")).when(authenticator).authenticate(anyString(),
//        anyString());
//    assertFalse(splendorController.preHandle(request, httpResponse, handler));
//  }

  @Test
  public void testPerformAction() {
    ResponseEntity response = splendorController.
        performAction(gameId, String.valueOf(action.getId()),
            "player1", "player1", "abc123", request);
    assertEquals(response.getStatusCodeValue(), 200);
  }

  @Test
  public void testPerformActionInvalidGameId() {
    when(gameManager.exists(gameId)).thenReturn(false);
    ResponseEntity response = splendorController.
        performAction(gameId, String.valueOf(action.getId()),
            "player1", "player1", "abc123", request);
    assertEquals(response.getStatusCodeValue(), 400);
  }

  @Test
  public void testPerformActionInvalidPlayer() {
    when(gameManager.playerInGame(gameId, "player5")).thenReturn(false);
    ResponseEntity response = splendorController.
        performAction(gameId, String.valueOf(action.getId()),
        "player5", "player5", "abc123", request);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void testPerformActionInsufficientResources() throws InvalidAction, InsufficientResourcesException {
    doThrow(new InsufficientResourcesException("Failed")).when(gameManager)
        .performAction(anyLong(), anyString(), anyString(), any());
    ResponseEntity response = splendorController.
        performAction(gameId, String.valueOf(action.getId()),
            "player1", "player1", "abc123", request);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void testSaveGame() {
    doNothing().when(gameManager).saveGame(anyLong());
    ResponseEntity response = splendorController.
        saveGame(gameId, "player1", "abc123", request);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void testSaveGameInvalidGameId() {
    when(gameManager.exists(gameId)).thenReturn(false);
    ResponseEntity response = splendorController.
        saveGame(gameId, "player1", "abc123", request);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void testSaveGameInvalidPlayer() {
    when(gameManager.playerInGame(gameId, "player5")).thenReturn(false);
    ResponseEntity response = splendorController.
        saveGame(gameId, "player5", "abc123", request);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void testSaveGameAuthenticationFailure() throws AuthenticationException {
    doThrow(new AuthenticationException("Failed")).when(authenticator).authenticate(anyString(),
        anyString());
    ResponseEntity response = splendorController.
        saveGame(gameId, "player1", "abc123", request);
    assertEquals(401, response.getStatusCodeValue());
  }

  @Test
  public void deregisterTest() throws AuthenticationException {
    doNothing().when(authenticator).authenticateAdmin(anyString(), anyString());
    doNothing().when(registrator).deregisterFromLobbyService();
    ResponseEntity response = splendorController.
        deregister("player1", "abc123", request);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void deregisterTestAuthenticationFailure() throws AuthenticationException {
    doThrow(new AuthenticationException("Failed")).when(authenticator).authenticateAdmin(anyString(),
        anyString());
    ResponseEntity response = splendorController.
        deregister("player1", "abc123", request);
    assertEquals(401, response.getStatusCodeValue());
  }
}
