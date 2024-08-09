package splendor.model.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import splendor.controller.action.ActionType;
import splendor.model.game.card.DevelopmentCard;
import splendor.model.game.payment.CoatOfArms;
import splendor.model.game.player.Player;

public class PlayerTest {
    static Player player1 = new Player("Wassim", "Blue");
    static Player player2 = new Player("Youssef", "Red");
    static Player player3 = new Player("Felicia", "Green");
    static Player player4 = new Player("Jessie", "Brown");
    static Player player5 = new Player("Kevin", "White");
    static Player player6 = new Player("Rui", "Yellow");
    static Board testBoard;

    @Test
    void getPerferredColorTest(){
        Assertions.assertEquals("Blue",player1.getPreferredColour());
    }

    @Test
    void addCardTest(){
        DevelopmentCard card1 = DevelopmentCard.get(1);;
        player2.addCard(card1);
        Assertions.assertTrue(player2.getCardsBought().contains(card1));
    }

    //TODO : test for addNoble
    @Test
    void removeTokenTest(){
        player3.removeTokens(player3.getTokens());
        int num_red_token_after_removal = player3.getTokens().getOrDefault(Color.RED, 0);
        Assertions.assertTrue(  num_red_token_after_removal == 0);
    }

    @Test
    void addUnlockedCoatOfArmsTest(){
        player4.addUnlockedCoatOfArms(CoatOfArms.get(1));
        Assertions.assertTrue(player4.getCoatOfArms().contains(CoatOfArms.get(1)));
    }

    @Test
    void addUnlockedCoatOfArmsTestAppliesThePower(){
        int ppBefore = player4.getPrestigePoints();
        player4.addUnlockedCoatOfArms(CoatOfArms.get(5));
        Assertions.assertEquals(ppBefore + 1, player4.getPrestigePoints());
    }

    @Test
    void  removeNextAction() {
        player1.addNextAction(ActionType.CLONE_CARD);
        player1.removeNextAction(ActionType.CLONE_CARD);
        Assertions.assertEquals(null, player1.nextAction());
    }

}
