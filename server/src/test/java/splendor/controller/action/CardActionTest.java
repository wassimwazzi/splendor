//package splendor.controller.action;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import org.junit.Before;
//import org.junit.Test;
//import splendor.model.game.card.DevelopmentCard;
//import splendor.model.game.card.SplendorCard;
//
//public class CardActionTest {
//  SplendorCard card = DevelopmentCard.get(1);
//  ActionType actionType = ActionType.BUY;
//  CardAction cardAction;
//
//  @Before
//  public void setUp() {
//    cardAction = getNewCardAction(card);
//  }
//
//  private CardAction getNewCardAction(SplendorCard card) {
//    try {
//      Constructor<BuyCardAction> constructor = BuyCardAction.class
//          .getDeclaredConstructor(SplendorCard.class);
//      constructor.setAccessible(true);
//      return constructor.newInstance(card);
//    } catch (NoSuchMethodException | InstantiationException | InvocationTargetException |
//             IllegalAccessException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  @Test
//  public void testGetType() {
//    assertEquals(actionType, cardAction.getActionType());
//  }
//
//  @Test
//  public void testGetCard() {
//    assertEquals(card, cardAction.getCard());
//  }
//
//  @Test
//  public void testIdsAreUnique() {
//    assertNotEquals(getNewCardAction(card, actionType).getId(),
//        getNewCardAction(card, actionType).getId());
//  }
//}
