package splendor.model.game.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import splendor.model.game.Color;
import splendor.model.game.TokenBank;
import splendor.model.game.card.City;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.Noble;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.payment.Bonus;
import splendor.model.game.payment.Cost;
import splendor.model.game.payment.Token;


/**
 * A splendor player's Inventory during a game. Can hold cards, nobles, and tokens.
 */
public class Inventory {
  private final TokenBank tokens;
  private final List<DevelopmentCardI> boughtCards = new ArrayList<>();
  private final List<DevelopmentCardI> reservedCards = new ArrayList<>();
  private final HashMap<Color, Integer> discounts = new HashMap<>();
  private List<Noble> nobles = new ArrayList<>();
  private List<City> cities = new ArrayList<>();
  // TODO: Change implementation of cities, bc there is max 1 city/player.

  private int numGoldCards = 0;

  /**
   * Creates a new inventory.
   */
  public Inventory() {
    tokens = new TokenBank(false);
  }

  /**
   * Adds a development card to the inventory.
   *
   * @param card the card to add
   */
  public void addBoughtCard(DevelopmentCardI card) {
    this.boughtCards.add(card);
    int cardId = card.getCardId();
    if ((cardId == 92) || (cardId == 94) || (cardId == 95) || (cardId == 96) || (cardId == 100)) {
      addTokens(Token.of(Color.GOLD), 2); // those IDs are gold cards (they count as 2 gold tokens)
      addOneGoldCard();
    }
    addBonus(Collections.singletonList(card), this.discounts);
  }

  /**
   * Adds a development card to the inventory as a reserved card.
   *
   * @param card the card to add
   */
  public void addReservedCard(DevelopmentCardI card) {
    this.reservedCards.add(card);
  }

  /**
   * Adds a noble to the inventory.
   *
   * @param noble the noble to add
   */
  public void addNoble(Noble noble) {
    nobles.add(noble);
  }

  /**
   * Adds a city to the inventory.
   *
   * @param city the city to add
   */
  public void addCity(City city) {
    cities.add(city);
  }

  /**
   * Adds tokens to the inventory.
   *
   * @param token the tokens to add
   * @param amount the number of tokens to add
   */
  public void addTokens(Token token, int amount) {
    for (int i = 0; i < amount; i++) {
      this.tokens.add(token);
    }
  }

  /**
   * Gets all the resources in the inventory.
   * This includes discounts earned from cards and nobles.
   *
   * @return the resources
   */
  public HashMap<Color, Integer> getResources() {
    HashMap<Color, Integer> tokens = this.tokens.getTokens();
    HashMap<Color, Integer> bonuses = getBonuses();
    // return the merge of the two maps
    HashMap<Color, Integer> resources = new HashMap<>();
    tokens.forEach((k, v) -> resources.merge(k, v, Integer::sum));
    bonuses.forEach((k, v) -> resources.merge(k, v, Integer::sum));
    return resources;
  }

  /**
   * Adds the bonus points and resources of a list of cards to the provided hash map (resources).
   *
   * @param cards     the list of cards to process
   * @param resources the HashMap of resources to which the bonus points and resources will be added
   */
  private void addBonus(List<? extends SplendorCard> cards, HashMap<Color, Integer> resources) {
    for (SplendorCard card : cards) {
      Bonus bonus = card.getBonus();
      for (Color color : card.getBonus()) {
        resources.put(color, resources.getOrDefault(color, 0) + bonus.getBonus(color));
      }
    }
  }

  /**
   * For the demo.
   *
   * @return inventory
   */
  public static Inventory getDemoInventory() {
    Inventory inventory = new Inventory();
    for (Color color : Color.tokenColors()) {
      inventory.addTokens(Token.of(color), 0);
    }
    return inventory;
  }

  /**
   * Make a payment for a cost.
   *
   * @param cost the cost to pay
   * @pre player has enough resources
   * @return the tokens used for the payment
   */
  public HashMap<Color, Integer> payFor(Cost cost) {
    // apply discounts
    HashMap<Color, Integer> discountedCosts = getDiscountedCosts(cost);
    HashMap<Color, Integer> tokensUsed = new HashMap<>();

    for (Color color : discountedCosts.keySet()) {
      int amount = discountedCosts.get(color);
      if (amount <= 0) {
        continue;
      }
      int difference = amount - tokens.count(Token.of(color));
      int remaining;
      if (difference > 0) { // pre is that player has enough resources
        // use gold tokens to pay for the difference
        IntStream.range(0, difference).forEach(i -> tokens.remove(Token.of(Color.GOLD)));
        remaining = amount - difference;
        tokensUsed.put(Color.GOLD, tokensUsed.getOrDefault(Color.GOLD, 0) + difference);
      } else {
        remaining = amount;
      }
      IntStream.range(0, remaining).forEach(i -> tokens.remove(Token.of(color)));
      tokensUsed.put(color, remaining);
    }
    return tokensUsed;
  }

  /**
   * Gets the discounted costs of a cost.
   *
   * @param cost the cost to pay
   * @return the discounted costs
   */
  public HashMap<Color, Integer> getDiscountedCosts(Cost cost) {
    HashMap<Color, Integer> discount = getBonuses();
    HashMap<Color, Integer> discountedCosts = new HashMap<>();
    for (Color color : cost) {
      int originalCost = cost.getValue(color);
      int discountForColor = discount.getOrDefault(color, 0);
      int discountedCost = Math.max(0, originalCost - discountForColor);
      discountedCosts.put(color, discountedCost);
    }
    return discountedCosts;
  }


  /**
   * Getter for the bought cards.
   *
   * @return bought cards of this inventory
   */
  public List<DevelopmentCardI> getBoughtCards() {
    return this.boughtCards;
  }

  /**
   * Getter for the tokens in this inventory.
   *
   * @return tokens in this inventory
   */
  public HashMap<Color, Integer> getTokens() {
    return tokens.getTokens();
  }

  /**
   * removes tokens from the inventory.
   *
   * @param tokens  a hashmap of the color and the tokens to remove.
   */
  public void removeTokens(HashMap<Color, Integer> tokens) {
    for (Color c : tokens.keySet()) {
      int value = tokens.get(c);
      for (int i = 0; i < value; i++) {
        this.tokens.remove(Token.of(c));
      }
    }
  }

  /**
   * get number of nobles in the inventory.
   *
   * @return the number of nobles
   */
  public int getNoblesCount() {
    return this.nobles.size();
  }

  /**
   * get number of cities in the inventory.
   * TODO: Maximum number of cities is 1. Consider different implementation.
   *
   * @return the number of cities
   */
  public int getCitiesCount() {
    return this.cities.size();
  }

  /**
   * get bonuses of a player from cards and nobles.
   *
   * @return the bonuses
   */
  public HashMap<Color, Integer> getBonuses() {
    return discounts;
  }

  /**
   * Get the reserved cards.
   *
   * @return list of reserved development cards
   */
  public List<DevelopmentCardI> getReservedCards() {
    return this.reservedCards;
  }

  /**
   * Get the number of gold cards in the player's inventory.
   * "Gold card" refers to the card worth 2 gold tokens when paying.
   *
   * @return the number of gold cards.
   */
  public int getNumGoldCards() {
    return numGoldCards;
  }

  /**
   * Add 1 to the number of gold cards.
   */
  public void addOneGoldCard() {
    numGoldCards += 1;
  }

  /**
   * We decide the number of gold cards to remove.
   * We calculate using the number of gold tokens used in the payment.
   * By default, a gold card will be used instead of 2 gold tokens.
   *
   * @param numGoldTokensRemoved number of gold tokens used in the payment.
   */
  public void removeGoldCards(Integer numGoldTokensRemoved) {
    if ((numGoldCards <= 0) || (numGoldTokensRemoved < 2)) { // no gold cards will be used
      // if there are none or if the number of gold tokens to spend is less than 2.
      return;
    }
    while (numGoldCards >= 1 && numGoldTokensRemoved >= 2) {
      // while we have at least 1 gold card, and the number of gold tokens to remove is 2 or more
      numGoldTokensRemoved -= 2;
      numGoldCards -= 1;
    }

  }

  /**
   * remove the reserved card from inventory.
   *
   * @param card to remove.
   */
  public void removeReservedCard(DevelopmentCardI card) {
    this.reservedCards.remove(card);
  }

  /**
   * Get rid of the card bought and associated bonuses.
   *
   * @param card the card
   */
  public void removeCardBought(DevelopmentCardI card) {
    this.boughtCards.remove(card);
    for (Color c : Color.tokenColors()) {
      this.discounts.replace(c, this.discounts.getOrDefault(c, 0) - card.getBonus().getBonus(c));
    }
  }

  public void addReservedNoble(Noble noble) {
    this.nobles.add(noble);
  }
}
