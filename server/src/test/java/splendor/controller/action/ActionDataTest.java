package splendor.controller.action;

import java.util.HashMap;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import splendor.controller.action.ActionData;
import splendor.model.game.Color;

public class ActionDataTest {

  @Test
  public void creatingActionDataObjects() {
    HashMap<Color, Integer> payment = new HashMap<>();
    payment.put(Color.RED,3);
    ActionData actionData1 = new ActionData(payment);
    ActionData actionData2 = new ActionData();

    Assertions.assertEquals(actionData1.getPayment(), payment);
    Assertions.assertEquals(actionData2.getPayment(), new HashMap<>());
    Assertions.assertEquals(actionData1.getPayment().get(Color.RED), 3);
  }

}
