package splendor.controller.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import splendor.controller.lobbyservice.GameInfo;
import splendor.model.game.SplendorGame;
import splendor.model.game.player.Player;

public class TakeNobleActionTest {

    ActionGenerator actionGenerator = new ActionGenerator();
    SplendorGame game;
    Player player1 = new Player("Wassim", "Blue");
    Player player2 = new Player("Youssef", "Red");

    @Before
    public void setUp() {
        Player[] testPlayers = {player1,player2};
        GameInfo testGameInfo = new GameInfo("Splendor","SplendorGameTest",testPlayers,"testSave");
        game = new SplendorGame(testGameInfo);
    }


    @Test
    public void ReserveNobleAction(){
        long gameId = 1;
        player1.addNextAction(ActionType.RESERVE_NOBLE);
        List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
        System.out.println(actions.get(actions.size()-1).getActionType());
        actions.get(actions.size()-1).performAction(player1, game.getBoard());
        assertEquals(1, player1.getNoblesCount());

        player1.addCard(game.getBoard().getDecks()[0].takeCard(0));
        player1.addCard(game.getBoard().getDecks()[0].takeCard(1));
        player1.updateReserveNobles();
    }


    @Test
    public void checkingTakeNoblesAction() {
        long gameId = 1;
        player1.addNextAction(ActionType.TAKE_NOBLE);
        player1.addCard(game.getBoard().getDecks()[0].takeCard(0));
        List<Action> actions = actionGenerator.generateActions(game, gameId, player1);
        assertEquals(0, actions.size());
    }

}


