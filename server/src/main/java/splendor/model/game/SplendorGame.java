package splendor.model.game;

import java.util.Arrays;
import javax.naming.InsufficientResourcesException;
import splendor.controller.action.Action;
import splendor.controller.action.ActionData;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.payment.CoatOfArms;
import splendor.model.game.player.Player;
import splendor.model.game.player.SplendorPlayer;

/**
 * Class responsible for storing metadata about a game.
 */
public class SplendorGame {
  private final GameInfo gameInfo;
  private final Board board;

  /**
   * Creates a new game, with a fresh board.
   *
   * @param gameInfo the game info
   */
  public SplendorGame(GameInfo gameInfo) {
    this.gameInfo = gameInfo;
    this.board = new Board(gameInfo.getGameServer(), gameInfo.getPlayers());
  }

  /**
   * Creates a games from a previously saved game.
   *
   * @param splendorGame the saved game
   */
  public SplendorGame(SplendorGame splendorGame) {
    this.gameInfo = splendorGame.gameInfo;
    this.board = splendorGame.board;
  }

  /**
   * Returns the game board.
   *
   * @return the game board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Returns a player.
   *
   * @param name the player name.
   * @return a player.
   */
  public Player getPlayer(String name) {
    return Arrays.stream(gameInfo.getPlayers())
        .filter(player -> player.getName().equals(name))
        .findFirst()
        .orElse(null);
  }

  /**
   * Buy a card.
   *
   * @param player the player
   * @param card   the card
   * @throws InsufficientResourcesException if not enough resources
   */
  public void buyCard(SplendorPlayer player, SplendorCard card)
      throws InsufficientResourcesException {
    board.buyCard(player, card);
  }


  /**
   * Checks if it's a player's turn.
   *
   * @param player the player
   * @return true if it's the player's turn
   */
  public boolean isTurnPlayer(SplendorPlayer player) {
    return board.isTurnPlayer(player);
  }

  /**
   * Performs an action.
   * Checking for the turn must have been done before.
   *
   * @param action     the action
   * @param username   the username
   * @param actionData the action data
   * @post updates the turn
   * @throws InsufficientResourcesException if not enough resources
   */
  public void performAction(Action action, String username, ActionData actionData)
      throws InsufficientResourcesException {
    // No use of action data for now, system automatically decides tokens to use for payment
    Player player = getPlayer(username);
    if (!isTurnPlayer(player)) {
      return;
    }
    action.performAction(player, board);
    CoatOfArms.addUnlockedCoatOfArms(player);
    if (board.getNobles() == null) {
      board.updateCities(player);
    } else {
      board.updateNobles(player);
    }
    if (player.nextAction() == null) {
      board.nextTurn();
    }
  }

  /**
   * Returns the game info.
   *
   * @return the game info
   */
  public GameInfo getGameInfo() {
    return gameInfo;
  }
}
