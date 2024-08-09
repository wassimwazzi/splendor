package splendor.model.game.payment;

import splendor.model.game.Color;

/**
 * Flyweight for tokens.
 */
public class Token implements PaymentItem {
  private static final int MAX_TOKENS = 7;
  private static final int MAX_GOLD_TOKENS = 5;
  private final Color color;
  private static final Token[] tokens = new Token[Color.tokenColors().length];

  /**
   * Creates a new token.
   *
   * @param color the color of the token
   */
  private Token(Color color) {
    this.color = color;
  }

  /**
   * Returns the color of the token.
   *
   * @return the color
   */
  public Color getColor() {
    return color;
  }

  /**
   * Returns the maximum number of tokens of this color.
   *
   * @return the maximum number of tokens
   */
  public int maxAmount() {
    return color == Color.GOLD ? MAX_GOLD_TOKENS : MAX_TOKENS;
  }

  /**
   * Returns the token of the given color.
   *
   * @param color the color of the token
   * @return the token
   */
  public static Token of(Color color) {
    if (tokens[color.ordinal()] == null) {
      tokens[color.ordinal()] = new Token(color);
    }
    return tokens[color.ordinal()];
  }
}
