package splendor.model.game;

/**
 * The colors used in the game.
 */
public enum Color {
  /**
   * White token and colour.
   */
  WHITE,
  /**
   * Green token and colour.
   */
  GREEN,
  /**
   * Blue token and colour.
   */
  BLUE,
  /**
   * Red token and colour.
   */
  RED,
  /**
   * Brown token and colour.
   */
  BROWN,
  /**
   * Gold token and colour.
   */
  GOLD,
  /**
   * Not a token. Only used as a colour (for example, player's colour to be used in the game)
   */
  YELLOW;

  /**
   * The colors of tokens.
   *
   * @return the colors of tokens
   */
  public static Color[] tokenColors() {
    return new Color[] {WHITE, GREEN, BLUE, RED, BROWN, GOLD};
  }
}
