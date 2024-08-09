package splendor.controller.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import splendor.model.game.Board;
import splendor.model.game.Color;
import splendor.model.game.SplendorGame;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.deck.SplendorDeck;
import splendor.model.game.payment.Bonus;
import splendor.model.game.payment.CoatOfArms;
import splendor.model.game.player.Player;
import splendor.model.game.player.SplendorPlayer;

/**
 * Buying a card action class.
 */
public class BuyCardAction extends CardAction {
  private static CardType cardType = CardType.DevelopmentCard;
  private HashMap<Color, Integer> tokenPayment;
  private List<DevelopmentCardI> cardPayment;

  /**
   * Creates a new card action.
   *
   * @param card the card.
   * @param tokenPayment tokens you use to pay.
   * @param cardPayment cards you use to pay.
   */
  protected BuyCardAction(SplendorCard card, HashMap<Color, Integer> tokenPayment,
                          List<DevelopmentCardI> cardPayment) {
    super(ActionType.BUY, card);
    this.tokenPayment = tokenPayment;
    this.cardPayment = cardPayment;
  }

  private static HashMap<Color, Integer> payForCardWithCoatOfArms(HashMap<Color, Integer> cost,
                                                                  SplendorPlayer player) {
    HashMap<Color, Integer> payment = new HashMap<>();
    HashMap<Color, Integer> playerTokens = player.getTokens();
    double numOfGoldTokensPlayerHas = playerTokens.getOrDefault(Color.GOLD, 0);
    payment.put(Color.GOLD, 0);

    for (Color c : cost.keySet()) {
      double tokensLeft = playerTokens.getOrDefault(c, 0) - cost.get(c);
      if (tokensLeft < 0) {
        numOfGoldTokensPlayerHas -= Math.ceil(tokensLeft / 2);
        payment.put(c, (int) (cost.get(c) - Math.ceil(tokensLeft / 2)));
        payment.replace(Color.GOLD, (int) (payment.get(Color.GOLD) + Math.ceil(tokensLeft / 2)));
        if (numOfGoldTokensPlayerHas < 0) {
          return null;
        }
      } else {
        payment.put(c, cost.get(c));
      }
    }

    return payment;
  }

  private static HashMap<Color, Integer> newCardCost(HashMap<Color, Integer> playerBonuses,
                                                     HashMap<Color, Integer> cardCost) {
    HashMap<Color, Integer> newCost = new HashMap<>();
    for (Color c : cardCost.keySet()) {
      int value = cardCost.get(c) - playerBonuses.getOrDefault(c, 0);
      if (value >= 0) {
        newCost.put(c, value);
      }
    }
    return newCost;
  }

  // assumes the payment with cards is only with 2 cards
  private static List<List<DevelopmentCardI>> differentWaysToPayWithCards(
                                              List<DevelopmentCardI> playerCards,
                                              HashMap<Color, Integer> cardCost) {
    List<List<DevelopmentCardI>> differentWaysToPay = new ArrayList<>();
    Color color = null;
    for (Color c : cardCost.keySet()) {
      if (cardCost.getOrDefault(c, 0) != 0) {
        color = c;
        break;
      }
    }
    List<DevelopmentCardI> cardsOfSameColor = new ArrayList<>();
    for (DevelopmentCardI c : playerCards) {
      Bonus bonus = c.getBonus();
      if (c.getBonus().getBonus(color) != 0) {
        cardsOfSameColor.add(c);
      }
    }
    // create different ways to pay for the card and send them back.
    for (int i = 0; i < cardsOfSameColor.size(); i++) {
      for (int j = i + 1; j < cardsOfSameColor.size(); j++) {
        List<DevelopmentCardI> cards = new ArrayList<>();
        cards.add(cardsOfSameColor.get(i));
        cards.add(cardsOfSameColor.get(j));
        differentWaysToPay.add(cards);
      }
    }

    return differentWaysToPay;
  }

  /**
   * Generates the list of buy actions for the player.
   *
   * @param game   the current game that is being played.
   * @param player the player we are generating actions for.
   * @return all legal buy actions for the given player in the given game state.
   */
  public static List<Action> getLegalActions(SplendorGame game, SplendorPlayer player) {
    List<Action> actions = new ArrayList<>();
    for (SplendorDeck deck : game.getBoard().getDecks()) {
      for (DevelopmentCardI card : deck.getFaceUpCards()) {
        if (card != null) {
          int cid = card.getCardId();
          // placeholder for now
          if (cid == 115 || cid == 116 || cid == 117 || cid == 119 || cid == 120) {
            List<List<DevelopmentCardI>> waysToPayWithCards = differentWaysToPayWithCards(
                                                        player.getCardsBought(), card.getCost()
                .getCost());
            for (List<DevelopmentCardI> cards : waysToPayWithCards) {
              actions.add(new BuyCardAction(card, null, cards));
            }

          } else {
            HashMap<Color, Integer> newCost = newCardCost(player.getBonuses(),
                                                        card.getCost().getCost());
            if (player.getCoatOfArms().contains(CoatOfArms.get(3))) {
              HashMap<Color, Integer> payment = payForCardWithCoatOfArms(newCost, player);
              if (payment != null) {
                actions.add(new BuyCardAction(card, payment, null));
              }
            } else if (player.canAfford(card)) {
              HashMap<Color, Integer> newPayment = getCardCostWithGold(newCost, player);
              actions.add(new BuyCardAction(card, newCost, null));
            }

          }
        }
      }
    }
    return actions;
  }

  /**
   * Returns the cost of the card with gold tokens.
   *
   * @param newCost the cost of the card
   * @param player the player we are performing an action for.
   * @return the cost of the card with gold tokens.
   */
  private static HashMap<Color, Integer> getCardCostWithGold(HashMap<Color, Integer> newCost,
                                                             SplendorPlayer player) {
    HashMap<Color, Integer> playerTokens = player.getTokens();
    HashMap<Color, Integer> newPayment = new HashMap<>();

    for (Color c : newCost.keySet()) {
      if (playerTokens.getOrDefault(c, 0) >= newCost.get(c)) {
        newPayment.put(c, newCost.get(c));
      } else {
        newPayment.put(c, playerTokens.getOrDefault(c, 0));
        newPayment.put(Color.GOLD, newCost.get(c) - playerTokens.getOrDefault(c, 0));
      }
    }

    return newPayment;
  }

  /**
   * Performs the BuyCard action for a player.
   *
   * @param player the player we are performing an action for.
   * @param board the board where we are performing the action.
   */
  @Override
  public void performAction(Player player, Board board) {
    DevelopmentCardI card = (DevelopmentCardI) this.getCard();
    if (!card.getSpecialActions().contains(ActionType.CLONE_CARD)) {
      player.addCard(card);
    }
    board.removeCard(card);
    card.getSpecialActions().forEach(player::addNextAction); // add special actions to player
    if (player.getCardsBought().size() == 0 && player.containsNextAction(ActionType.CLONE_CARD)) {
      player.removeNextAction(ActionType.CLONE_CARD);
    }
    if (board.getNobles() == null) {
      if (player.containsNextAction(ActionType.TAKE_NOBLE)) {
        player.removeNextAction(ActionType.TAKE_NOBLE);
      } else if (player.containsNextAction(ActionType.RESERVE_NOBLE)) {
        player.removeNextAction(ActionType.RESERVE_NOBLE);
      }
    }
    if (this.tokenPayment != null) {
      player.removeTokens(this.tokenPayment);
      board.addTokens(this.tokenPayment);
    } else {
      for (DevelopmentCardI c : cardPayment) {
        player.removeCardBought(c);
      }
    }
    // coat of arms special power
    if (player.getCoatOfArms().contains(CoatOfArms.get(1))) {
      player.addNextAction(ActionType.TAKE_ONE_TOKEN);
    }

    player.updateReserveNobles();
    player.updateCoatOfArms();
  }
}
