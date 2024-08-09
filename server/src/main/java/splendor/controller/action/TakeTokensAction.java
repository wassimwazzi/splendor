package splendor.controller.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import splendor.model.game.Board;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.payment.CoatOfArms;
import splendor.model.game.player.Player;
import splendor.model.game.player.SplendorPlayer;

/**
 * take tokens action class.
 */
public class TakeTokensAction implements Action {
  private HashMap<Color, Integer> tokens;
  private final ActionType actionType;
  private final long actionId;

  /**
   * Creates a take token action.
   *
   * @param actionType to know what type of action it is
   * @param tokens  tokens that can be taken.
   */
  public TakeTokensAction(ActionType actionType, HashMap<Color, Integer> tokens) {
    this.actionType = actionType;
    this.tokens = tokens;
    this.actionId = (long) (Math.random() * Long.MAX_VALUE);
  }

  /**
   * Retrieves the possible combinations to take 3 tokens of different colours.
   *
   * @param tokens the tokens to calculate the combinations from
   * @return the possible combinations of 3 tokens of different colours
   */
  private static List<HashMap<Color, Integer>> get3Tokens(HashMap<Color, Integer> tokens) {
    List<HashMap<Color, Integer>> combinations = new ArrayList<>();
    Color[] colors = Color.tokenColors();

    for (int i = 0; i < colors.length - 1; i++) {
      Color color1 = colors[i];
      int count1 = tokens.getOrDefault(color1, 0);
      if (count1 == 0) {
        continue; // skip colors with no tokens
      }

      for (int j = i + 1; j < colors.length - 1; j++) {
        Color color2 = colors[j];
        int count2 = tokens.getOrDefault(color2, 0);
        if (count2 == 0) {
          continue; // skip colors with no tokens
        }

        for (int k = j + 1; k < colors.length - 1; k++) {
          Color color3 = colors[k];
          int count3 = tokens.getOrDefault(color3, 0);
          if (count3 == 0) {
            continue; // skip colors with no tokens
          }

          HashMap<Color, Integer> combination = new HashMap<>();
          combination.put(color1, 1);
          combination.put(color2, 1);
          combination.put(color3, 1);
          combinations.add(combination);

        }
      }
    }

    // Handle case where we only take 2 tokens
    if (combinations.size() == 0) {
      for (int i = 0; i < colors.length - 1; i++) {
        Color color1 = colors[i];
        int count1 = tokens.getOrDefault(color1, 0);
        if (count1 == 0) {
          continue; // skip colors with no tokens
        }

        for (int j = i + 1; j < colors.length - 1; j++) {
          Color color2 = colors[j];
          int count2 = tokens.getOrDefault(color2, 0);
          if (count2 == 0) {
            continue; // skip colors with no tokens
          }

          HashMap<Color, Integer> combination = new HashMap<>();
          combination.put(color1, 1);
          combination.put(color2, 1);
          combinations.add(combination);

        }
      }
    }

    // Handle case where we only take 1 token
    if (combinations.size() == 0) {
      for (int i = 0; i < colors.length - 1; i++) {
        Color color1 = colors[i];
        int count1 = tokens.getOrDefault(color1, 0);
        if (count1 == 0) {
          continue; // skip colors with no tokens
        }

        HashMap<Color, Integer> combination = new HashMap<>();
        combination.put(color1, 1);
        combinations.add(combination);
      }
    }

    return combinations;
  }

  /**
   * Retrieves the possible combinations to take 2 tokens of the same colour.
   *
   * @param tokens the tokens to calculate the combinations from
   * @param limit maximum number of tokens of a given colour used for a combination
   * @param specialPower modifier from the coat of arms (tradingroutes extension)
   * @return the possible combinations of 2 tokens of the same colour
   */
  // need to add logic for the special power.
  private static List<HashMap<Color, Integer>> get2Tokens(HashMap<Color, Integer> tokens,
                                                   int limit,
                                                   boolean specialPower) {
    List<HashMap<Color, Integer>> combinations = new ArrayList<>();
    Color[] colors = Color.tokenColors();

    for (int i = 0; i < colors.length - 1; i++) {
      Color color1 = colors[i];
      int count1 = tokens.getOrDefault(color1, 0);
      if (count1 <= (2 + limit)) {
        continue; // skip colors
      }

      if (specialPower) {
        for (int j = 0; j < colors.length - 1; j++) {
          Color color2 = colors[j];
          if (color1 != color2) {
            int count2 = tokens.getOrDefault(color2, 0);
            if (count2 > 0) {
              HashMap<Color, Integer> combination = new HashMap<>();
              combination.put(color1, 2);
              combination.put(color2, 1);
              combinations.add(combination);
            }
          }
        }
      } else {
        HashMap<Color, Integer> combination = new HashMap<>();
        combination.put(color1, 2);
        combinations.add(combination);
      }
    }

    return combinations;
  }

  /**
   * generates a list of actions that the player can make.
   *
   * @param game The game that the player is playing in.
   * @param player the player that we are getting legal actions for.
   * @return  a list of possible action that the player can take.
   */
  public static List<Action> getLegalActions(SplendorGame game, SplendorPlayer player) {
    List<Action> actions = new ArrayList<>();
    List<HashMap<Color, Integer>> taking3Tokens = get3Tokens(game.getBoard().getTokens());
    // need to replace it depending on the number of players in the game
    boolean specialPower = false;
    if (player.getCoatOfArms().contains(CoatOfArms.get(2))) {
      specialPower = true;
    }
    List<HashMap<Color, Integer>> taking2Tokens = get2Tokens(game.getBoard().getTokens(),
                                                        2,
                                                  specialPower);
    for (HashMap<Color, Integer> tk : taking3Tokens) {
      actions.add(new TakeTokensAction(ActionType.TAKE_TOKENS, tk));
    }

    for (HashMap<Color, Integer> tk : taking2Tokens) {
      actions.add(new TakeTokensAction(ActionType.TAKE_TOKENS, tk));
    }

    return actions;
  }

  /**
   * Returns the id of this TakeTokens action.
   *
   * @return the id of this TakeTokens action
   */
  @Override
  public long getId() {
    return this.actionId;
  }

  /**
   * Performs the TakeTokens action.
   *
   * @param player the player who takes tokens.
   * @param board  the board from which tokens are removed to "give" to the player.
   */
  @Override
  public void performAction(Player player, Board board) {
    player.addTokens(this.tokens);
    board.removeTokens(this.tokens);
  }

  /**
   * Returns the type of the action.
   *
   * @return the type of the action
   */
  @Override
  public ActionType getActionType() {
    return actionType;
  }
}
