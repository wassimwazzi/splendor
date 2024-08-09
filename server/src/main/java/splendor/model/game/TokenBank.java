package splendor.model.game;

import java.util.HashMap;
import splendor.model.game.payment.Token;

/**
 * The bank of tokens on the board.
 */
public class TokenBank implements Bank<Token> {

  private final HashMap<Color, Integer> tokens = new HashMap<>();

  /**
   * Creates a new token bank.
   *
   * @param fill if true, the bank is filled with max number of tokens of each color.
   */
  public TokenBank(boolean fill) {
    if (!fill) {
      return;
    }
    for (Color color : Color.tokenColors()) {
      tokens.put(color, Token.of(color).maxAmount());
    }
  }

  /**
   * adds a token to the player's inventory.
   *
   * @param element the element to add
   */
  @Override
  public void add(Token element) {
    // FIXME: Uncomment below after demo
    // assert tokens.getOrDefault(element.getColor(), 0) < element.maxAmount();
    tokens.put(element.getColor(), tokens.getOrDefault(element.getColor(), 0) + 1);
  }

  /**
   * removes a type of token from the inventory.
   *
   * @param element the element to remove
   */
  @Override
  public void remove(Token element) {
    if (contains(element)) {
      tokens.replace(element.getColor(), tokens.get(element.getColor()) - 1);
    }
  }

  /**
   * Checks if the token exists in the players inverntory.
   *
   * @param element the element to check
   * @return  boolean of the token exists.
   */
  @Override
  public boolean contains(Token element) {
    return tokens.getOrDefault(element.getColor(), 0) > 0;
  }

  /**
   * returns the number of the tokens a player has of that type.
   *
   * @param element the element to count
   * @return  integer the number of tokens.
   */
  @Override
  public int count(Token element) {
    return tokens.getOrDefault(element.getColor(), 0);
  }

  /**
   * Get a Hashmap of the tokens of the player.
   *
   * @return a Hashmap of the tokens of the player.
   */
  public HashMap<Color, Integer> getTokens() {
    return tokens;
  }
}
