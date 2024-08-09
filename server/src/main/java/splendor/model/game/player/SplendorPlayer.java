package splendor.model.game.player;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.naming.InsufficientResourcesException;
import splendor.controller.action.ActionType;
import splendor.model.game.Color;
import splendor.model.game.card.DevelopmentCardI;
import splendor.model.game.card.SplendorCard;
import splendor.model.game.payment.CoatOfArms;

/**
 * Interface for a splendor player. Can buy/reserve cards and nobles, and can take tokens.
 */
public interface SplendorPlayer {
  /**
   * buy a card.
   *
   * @param card to buy.
   * @return the color info.
   * @throws InsufficientResourcesException if not enough resources.
   */
  HashMap<Color, Integer> buyCard(DevelopmentCardI card) throws InsufficientResourcesException;

  /**
   * reserve a card.
   *
   * @param card to reserve.
   * @param addGoldToken to add.
   */
  void reserveCard(DevelopmentCardI card, boolean addGoldToken);

  /**
   * Checks if player has enough resources to buy a card.
   *
   * @param card the card to buy
   * @return true if player has enough resources to buy the card
   */
  boolean canAfford(SplendorCard card);

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  String getName();

  /**
   * gets the next action that needs to be done by the player.
   *
   * @return  a list of the next actions.
   */
  ActionType nextAction();

  /**
   * removes the action at the beginning of the list.
   */
  void removeOldestNextAction();

  /**
   * returns a lost of the cards bought by the player.
   *
   * @return  the cards bought by the player.
   */
  List<DevelopmentCardI> getCardsBought();

  /**
   * get a hashmap of the tokens of the player.
   *
   * @return  a hashmap of tokens.
   */
  HashMap<Color, Integer> getTokens();

  /**
   * get number of gold cards.
   *
   * @return num gold cards.
   */
  int getNumGoldCards();

  /**
   * get reserved cards.
   *
   * @return list of cards.
   */
  List<DevelopmentCardI> getReservedCards();

  /**
   * removes the card.
   *
   * @param card to remove.
   */
  void removeReservedCard(DevelopmentCardI card);

  /**
   * get the bonuses.
   *
   * @return bonuses map.
   */
  HashMap<Color, Integer> getBonuses();

  /**
   * Getter for coat of arms.
   *
   * @return the set of coat of arms.
   */
  Set<CoatOfArms> getCoatOfArms();
}
