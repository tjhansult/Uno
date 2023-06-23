package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.controller.UNO;
import server.model.card.Card;
import server.model.player.HumanPlayer;
import server.model.player.factory.Player;
import server.model.table.Table;
import server.model.table.gameModes.Progressive;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class ProgressiveTest {
    /**
     * test variables
     */
    private ArrayList<Player> players;
    private Table table;
    private UNO uno;

    /**
     * Initializes the tests variables and sets the necessary properties.
     */
    @BeforeEach
    public void setUp() {
        players = new ArrayList<Player>();
        players.add(new HumanPlayer("a"));
        players.add(new HumanPlayer("b"));
        players.add(new HumanPlayer("c"));
        uno = new UNO();
        table = new Table(players, new Progressive(), uno);
        uno.setPlayers(players);
        uno.setTable(table);
        for (Player player: players) {
            player.setTable(table);
            player.setUNO(uno);
        }
    }

    /**
     * tests the forwardCount (the amount of times a draw2 has been forwarded * 2) for 3 players playing a  Draw2 card
     * in a row. Firstly, we add a draw 2 card to each players hand to ensure that the last index in the hand always corresponds
     * to playing a draw 2 card, then we play the card and assert the forwardCount after every card placement. Which should increase
     * by two everytime.
     */
    @Test
    public void testForwardCount() {
        table.setCurrentCard(new Card(Card.Color.GREEN, Card.Value.ZERO));
        for (Player p: players) {
            p.getHand().add(new Card(Card.Color.GREEN, Card.Value.DRAW_TWO));
        }
        int lastIndex = players.get(0).getHand().size()-1;
        players.get(0).playCard(players.get(0).getHand().get(lastIndex));
        assertEquals(2, table.getPlayingMode().getForwardCount());
        players.get(1).playCard(new Card(Card.Color.BLUE, Card.Value.DRAW_TWO));
        assertEquals(4, table.getPlayingMode().getForwardCount());
        players.get(2).playCard(players.get(2).getHand().get(lastIndex));
        assertEquals(6, table.getPlayingMode().getForwardCount());
    }

    /**
     * tests the valid move function in case a draw2 is played, it should only return true for a draw2 card and false otherwise,
     * since if you cannot or do not want to forward the draw2 card you are not allowed to play any other card since your turn is skipped.
     */
    @Test
    public void testValidMoveAfterDrawTwo() {
        table.setCurrentCard(new Card(Card.Color.GREEN, Card.Value.DRAW_TWO));
        table.getPlayingMode().setForwardCount(2);
        Card c1 = new Card(Card.Color.GREEN, Card.Value.THREE);
        assertFalse(table.getPlayingMode().validMove(c1, table));
        Card c2 = new Card(Card.Color.BLUE, Card.Value.DRAW_TWO);
        assertTrue(table.getPlayingMode().validMove(c2, table));
    }

    /**
     * tests if your turn is indeed skipped and the amount of cards indicated in the forwardCount have been added to your hand, if
     * you do not have a Draw 2 card in your hand. Firstly, every draw 2 card is removed from the players hand, to ensure that he
     * indeed is unable to play a Draw 2 card. Then the player before him places a draw 2 card and nextTurn is called, Then the handsize
     * of the player is asserted and the currentTurnIndex should be equal to one,as his turn should be skipped.
     */
    @Test
    public void testCurrentPlayerAfterDraw2() {
        ArrayList<Card> newHand = new ArrayList<>();
        for (Card c: players.get(0).getHand()) {
            if (c.getValue() != Card.Value.DRAW_TWO) {
                newHand.add(c);
            }
        }
        int handSize = newHand.size();
        players.get(0).setHand(newHand);
        table.setCurrentTurnIndex(2);
        table.getCurrentPlayer().playCard(new Card(Card.Color.GREEN, Card.Value.DRAW_TWO));
        table.nextTurn();
        assertEquals(handSize+2, players.get(0).getHand().size());
        assertEquals(players.get(1).getNickname(), table.getCurrentPlayer().getNickname());

    }
}
