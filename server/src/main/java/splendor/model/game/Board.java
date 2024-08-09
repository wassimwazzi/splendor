package splendor.model.game;

import eu.kartoffelquadrat.asyncrestlib.BroadcastContent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.naming.InsufficientResourcesException;
import splendor.model.game.card.City;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.FacedDownCardTypes;
import splendor.model.game.card.Noble;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.deck.CityDeck;
import splendor.model.game.deck.Deck;
import splendor.model.game.deck.NobleDeck;
import splendor.model.game.deck.SplendorDeck;
import splendor.model.game.payment.Token;
import splendor.model.game.player.Player;
import splendor.model.game.player.SplendorPlayer;

/**
 * The board of the game. Contains the players, the nobles, the decks, and the tokens
 */
public class Board implements BroadcastContent {
  private final Player[] players;
  private int currentTurn;
  private final SplendorDeck[] decks = new SplendorDeck[6];
  private GameType gameType;
  private NobleDeck nobleDeck;
  private CityDeck cityDeck;

  private final TokenBank bank = new TokenBank(true);

  private List<String> winners = null; // can have multiple winners in case of a tie

  /**
   * Creates a new board.
   *
   * @param players the players. Only 2-4 players are allowed.
   */
  public Board(Player... players) {
    if (players.length < 2 || players.length > 4) {
      throw new IllegalArgumentException(
              String.format("Only 2-4 players are allowed, not %d", players.length));
    }
    this.players = players;
    // make sure there are no duplicate players
    Set<Player> playerSet = new HashSet<>(Arrays.asList(players));
    if (playerSet.size() != players.length) {
      throw new IllegalArgumentException("Duplicate players are not allowed");
    }
    this.nobleDeck = new NobleDeck(players.length); // create nobleDeck based on # players
    this.cityDeck = new CityDeck();
    this.gameType = GameType.ORIENT; // this way it won't be null
    currentTurn = 0;
    decks[0] = new Deck(Color.GREEN);
    decks[1] = new Deck(Color.YELLOW);
    decks[2] = new Deck(Color.BLUE);
    decks[3] = new Deck(Color.RED, 1);
    decks[4] = new Deck(Color.RED, 2);
    decks[5] = new Deck(Color.RED, 3);
  }

  /**
   * Creates a new board.
   *
   * @param players the players. Only 2-4 players are allowed.
   * @param gameType the type of game (extension)
   */
  public Board(String gameType, Player... players) {
    if (players.length < 2 || players.length > 4) {
      throw new IllegalArgumentException(
          String.format("Only 2-4 players are allowed, not %d", players.length));
    }
    this.players = players;
    // make sure there are no duplicate players
    Set<Player> playerSet = new HashSet<>(Arrays.asList(players));
    if (playerSet.size() != players.length) {
      throw new IllegalArgumentException("Duplicate players are not allowed");
    }
    if (gameType.equals("Splendor") || gameType.equals("SplendorTraderoutes")) {
      this.nobleDeck = new NobleDeck(players.length); // create nobleDeck based on # players
      if (gameType.equals("Splendor")) {
        this.gameType = GameType.ORIENT;
      } else {
        this.gameType = GameType.TRADEROUTES;
      }
    } else {
      this.cityDeck = new CityDeck();
      this.gameType = GameType.CITIES;

    }
    currentTurn = 0;
    decks[0] = new Deck(Color.GREEN);
    decks[1] = new Deck(Color.YELLOW);
    decks[2] = new Deck(Color.BLUE);
    decks[3] = new Deck(Color.RED, 1);
    decks[4] = new Deck(Color.RED, 2);
    decks[5] = new Deck(Color.RED, 3);
  }

  /**
   * Returns the decks.
   *
   * @return the decks
   */
  public SplendorDeck[] getDecks() {
    return decks;
  }

  /**
   * Buys a card from the deck.
   *
   * @param player the player buying the card
   * @param card the card to buy
   * @throws InsufficientResourcesException if not enough resources
   */
  public void buyCard(SplendorPlayer player, SplendorCard card)
      throws InsufficientResourcesException {
    if (card instanceof Noble) {
      throw new IllegalArgumentException("Cannot buy a noble yet");
    }
    if (card instanceof City) {
      throw new IllegalArgumentException("Cannot buy a city yet");
    }
    buyDevelopmentCard(player, (DevelopmentCardI) card);
  }


  /**
   * Performs BuyDevelopmentCard action on board.
   *
   * @param player player buying the card
   * @param card card being bought
   * @throws InsufficientResourcesException when player cannot afford the card
   */
  private void buyDevelopmentCard(SplendorPlayer player, DevelopmentCardI card)
      throws InsufficientResourcesException {
    if (!player.canAfford(card)) {
      throw new InsufficientResourcesException("Player cannot afford card");
    }

    takeCardFromDeck(card);
    HashMap<Color, Integer> tokensToGiveBack = player.buyCard(card);
    giveBackTokens(tokensToGiveBack);
  }

  /**
   * Retrieves a card from the deck in the board.
   *
   * @param card taken from deck
   * @return true if position is not -1
   */
  private boolean takeCardFromDeck(DevelopmentCardI card) {
    for (SplendorDeck deck : decks) {
      int pos = deck.isFaceUp(card);
      if (pos != -1) {
        deck.takeCard(pos);
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if it's a player's turn.
   *
   * @param player the player
   * @return true if it's the player's turn
   */
  public boolean isTurnPlayer(SplendorPlayer player) {
    return players[currentTurn].equals(player);
  }

  /**
   * Updates the board to the next turn.
   *
   */
  public void nextTurn() {
    currentTurn = (currentTurn + 1) % players.length;
    if (currentTurn == 0) {
      checkGameEnd();
    }
  }

  /**
   * Used to compare Players by the number of prestige points.
   * Used for Collections.sort in checkGameEnd.
   */
  class SortByDescendingPrestigePoints implements Comparator<Player> {
    @Override
    public int compare(Player o1, Player o2) {
      return o2.getPrestigePoints() - o1.getPrestigePoints();
    }
  }

  private void checkGameEnd() {
    if (winners != null) {
      return;
    }
    if (nobleDeck == null) { // in cities extension, the nobleDeck will be null
      List<Player> playersWithCities = new ArrayList<>();
      boolean gameEnd = false;
      for (Player player : players) { // go through players and add any player that has a city
        if (player.getCitiesCount() > 0) {
          playersWithCities.add(player);
          gameEnd = true;
        }
      }
      // if at least one player had a city,
      // we sort them by prestige points (high to low) and declare the highest one the winner.
      // 1. Sort and add top player as a winner
      // 2. If the next player on the list has fewer prestige points, then we stop adding winners.
      // Otherwise, if they have the same amount of points, then we add them as a tied winner.
      if (gameEnd) {
        Collections.sort(playersWithCities, new SortByDescendingPrestigePoints());
        winners = new ArrayList<>(); // previously null
        for (int i = 0; i < playersWithCities.size(); i++) {
          winners.add(playersWithCities.get(i).getName()); // player with most prestige points
          if (i >= playersWithCities.size() - 1) {
            return;
          }
          if (playersWithCities.get(i).getPrestigePoints()
                  > playersWithCities.get(i + 1).getPrestigePoints()) {
            return;
          }
        }
      }
    } else { // not cities extension
      boolean gameEnd = false;
      for (Player player : players) {
        if (player.getPrestigePoints() >= 15) {
          if (winners == null) {
            winners = new ArrayList<>(); // initialize
          }
          winners.add(player.getName());
        }
      }
      return;
    }
  }

  /**
   * give back tokens to the bank.
   *
   * @param tokens the tokens to give back
   */
  public void giveBackTokens(HashMap<Color, Integer> tokens) {
    for (Color color : tokens.keySet()) {
      IntStream.range(0, tokens.get(color))
          .forEach(i -> bank.add(Token.of(color)));
    }
  }

  /**
   * Returns a list of the nobles. Removes nulls.
   *
   * @return a list of the nobles.
   */
  public List<Noble> getNobles() {
    if (nobleDeck == null) {
      return null;
    }
    Noble[] nobles = nobleDeck.getNobles();
    return Arrays.stream(nobles)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }


  /**
   * Removes noble from the nobleDeck on the board.
   *
   * @param noble being removed
   */
  public void removeNoble(Noble noble) {
    nobleDeck.removeNoble(noble);
  }

  /**
   * Returns a list of the cities. Removes nulls.
   *
   * @return a list of the cities.
   */
  public List<City> getCities() {
    if (cityDeck == null) {
      return null;
    }
    City[] cities = cityDeck.getCities();
    return Arrays.stream(cities)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  /**
   * Removes city from the cityDeck on the board.
   *
   * @param city being removed
   */
  public void removeCity(City city) {
    cityDeck.removeCity(city);
  }

  /**
   * removes the card from the deck.
   *
   * @param card the card that need to be removed.
   */
  public void removeCard(SplendorCard card) {
    for (SplendorDeck deck : decks) {
      int pos = deck.isFaceUp((DevelopmentCardI) card);
      if (pos != -1) {
        deck.takeCard(pos);
      }
    }
  }

  /**
   * Checks if the bank contains any gold tokens.
   *
   * @return true if bank contains at least 1 gold token, false otherwise
   */
  public boolean hasGoldToken() {
    return this.bank.contains(Token.of(Color.GOLD));
  }


  /**
   * adds tokens to the board.
   *
   * @param tokens  a hashmap of the color and the tokens to return to the bank.
   */
  public void addTokens(HashMap<Color, Integer> tokens) {
    for (Color c : tokens.keySet()) {
      for (int i = 0; i < tokens.get(c); i++) {
        this.bank.add(Token.of(c));
      }
    }
  }

  /**
   * Getter for tokens.
   *
   * @return tokens from bank on board
   */
  public HashMap<Color, Integer> getTokens() {
    return this.bank.getTokens();
  }

  /**
   * removes tokens form the board.
   *
   * @param tokens  a hashmap of the color and the tokens to remove from the bank.
   */
  public void removeTokens(HashMap<Color, Integer> tokens) {
    for (Color c : tokens.keySet()) {
      int numOfTokens = tokens.get(c);
      for (int i = 0; i < numOfTokens; i++) {
        this.bank.remove(Token.of(c));
      }
    }
  }

  /**
   * Checks if the player can unlock a noble, and if so, unlocks it.
   *
   * @param player the player
   */
  public void updateNobles(Player player) {
    for (Noble noble : getNobles()) {
      if (noble.getCost().isAffordable(player.getBonuses())) {
        player.addNoble(noble);
        removeNoble(noble);
        return; // only unlock one noble per turn?
      }
    }
  }

  /**
   * Checks if the player can unlock a city, and if so, unlocks it.
   *
   * @param player the player
   */
  public void updateCities(Player player) {
    for (City city : getCities()) {
      if (city.getCost().isAffordable(player.getBonuses(), player.getPrestigePoints())) {
        player.addCity(city);
        removeCity(city);
        return; // only unlock one city per turn?
      }
    }
  }

  /**
   * removes a faced down card. used for when the player is trying to reserve it.
   *
   * @param type  the type of card that is being reserved.
   * @return  a splendor card that is being reserved.
   */
  public SplendorCard removeFacedDownCard(FacedDownCardTypes type) {
    if (FacedDownCardTypes.BLUE == type) {
      return this.decks[2].getNextFacedDownCard();
    } else if (FacedDownCardTypes.GREEN == type) {
      return this.decks[0].getNextFacedDownCard();
    } else if (FacedDownCardTypes.YELLOW == type) {
      return this.decks[1].getNextFacedDownCard();
    } else if (FacedDownCardTypes.RED_1 == type) {
      return this.decks[3].getNextFacedDownCard();
    } else if (FacedDownCardTypes.RED_2 == type) {
      return this.decks[4].getNextFacedDownCard();
    } else if (FacedDownCardTypes.RED_3 == type) {
      return this.decks[5].getNextFacedDownCard();
    } else {
      return null;
    }
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  /**
   * Check if game is finished.
   *
   * @return finished if winners is not empty (if there are winners)
   */
  public boolean isFinished() {
    return winners != null;
  }

  /**
   * get the list of winners.
   *
   * @return list of winners.
   */
  public List<String> getWinners() {
    return winners;
  }


}
