package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.controller.UNO;
import server.model.card.Card;
import server.model.player.HumanPlayer;
import server.model.player.factory.Player;
import server.model.table.Table;
import server.model.table.gameModes.SevenZero;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SevenZeroTest {
    /**
     * test variables
     */
    private ArrayList<Player> players;
    private Table table;

    /**
     * Initializes the players to three new HumanPlayers with creative names. Creates a new table with these players and a
     * new SevenZero object. The table properties of the players are then set to the created table.
     */
    @BeforeEach
    public void setUp() {
        players = new ArrayList<Player>();
        players.add(new HumanPlayer("a"));
        players.add(new HumanPlayer("b"));
        players.add(new HumanPlayer("c"));
        table = new Table(players, new SevenZero(), new UNO());
        for (Player player: players) {
            player.setTable(table);
        }
    }

    /**
     * tests swapping the hands of a player with another player. (called when a seven is played in sevenZero gameMode) by calling
     * the swapHands method of a player, instead of calling the chooseSwitchHands method which would invoke a scanner asking for whom
     * to switch hands with, which in a unit test should not be the case.
     */
    @Test
    public void testSwapHands() {
        ArrayList<Card> hand1 = players.get(0).getHand();
        ArrayList<Card> hand2 = players.get(1).getHand();
        players.get(0).swapHands(players.get(1));
        assertEquals(hand1, players.get(1).getHand());
        assertEquals(hand2, players.get(0).getHand());
    }

    /**
     * tests passing down the players hands in the order of play. (called when a zero is played in sevenZero gameMode) If the current order
     * of play is clockwise p1 should now have p3's hand, p2 should have p1's hand and p3 should have p2's hand. If the order of play is
     * counterClockwise, p1 should have p2's hand, p2 should have p3's hand and p3 should have p1's hand after a zero has been played.
     */
    @Test
    public void testPassDownHands() {
        ArrayList<Card> hand1 = players.get(0).getHand();
        ArrayList<Card> hand2 = players.get(1).getHand();
        ArrayList<Card> hand3 = players.get(2).getHand();
        table.setCurrentCard(new Card(Card.Color.GREEN, Card.Value.FOUR));
        players.get(0).playCard(new Card(Card.Color.GREEN, Card.Value.ZERO));
        assertEquals(hand3, players.get(0).getHand());
        assertEquals(hand1, players.get(1).getHand());
        assertEquals(hand2, players.get(2).getHand());

        // !clockwise
        table.reversePlayers();
        hand1 = players.get(0).getHand();
        hand2 = players.get(1).getHand();
        hand3 = players.get(2).getHand();
        table.setCurrentCard(new Card(Card.Color.GREEN, Card.Value.FOUR));
        players.get(0).playCard(new Card(Card.Color.GREEN, Card.Value.ZERO));
        assertEquals(hand2, players.get(0).getHand());
        assertEquals(hand3, players.get(1).getHand());
        assertEquals(hand1, players.get(2).getHand());
    }

}
