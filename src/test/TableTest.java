package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.controller.UNO;
import server.model.card.Card;
import server.model.player.HumanPlayer;
import server.model.player.factory.Player;
import server.model.table.Table;
import server.model.table.gameModes.Normal;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
public class TableTest {
    /**
     * test variables
     */
    private ArrayList<Player> players = new ArrayList<>();
    private Table table;
    private Player player;
    /**
     * Expected values for the test cases.
     */
    private final int HAND_SIZE = 7;
    private final int FINAL_SCORE = 300;
    /**
     * Initializes players to four human players with cool nicknames, also stores the first created player in the player property,
     * creates a new table with these players and a new Normal playing mode and then sets the table properties of the players.
     */
    @BeforeEach
    public void setUp() {
        players = new ArrayList<Player>();
        player = new HumanPlayer("KD");
        players.add(player);
        players.add(new HumanPlayer("MJ"));
        players.add(new HumanPlayer("Lebron"));
        players.add(new HumanPlayer("Luca"));
        table = new Table(players, new Normal(), new UNO());
        for (Player player: players) {
            player.setTable(table);
        }
    }

    /**
     * tests whether the table has correctly initialized all the properties it should after being constructed, its values should now be initialized
     * in such a way that a game of uno can be started. No further adjustment need to be made here before asserting, as the setUp round function is
     * called in the constructor of table and therefore already in the setUp method of this test class.
     */
    @Test
    public void testSetUpRound() {
        assertNotNull(table.getCurrentCard());
        assertNotNull(table.getDeck().getUsedCards().get(0));
        for (Player p: players) {
            assertEquals(HAND_SIZE, p.getHand().size());
        }
        assertNull(table.getIndicatedColor());
    }

    /**
     * tests reversing the order of play by checking if the table's currentPlayer changes accordingly after calling nextTurn. If we reverse the order
     * and then call nextTurn our currentPlayer should be the last player we added to the players array (nickname: "Luca"), calling nextTurn again the player
     * that we added second last should now be our current player (nickname: Lebron) if we then call nextTurn again the player that we added to the players array
     * second should now be our current player (nickname: MJ)
     */
    @Test
    public void testReversePlayers() {
        table.reversePlayers();
        table.nextTurn();
        assertEquals("Luca",table.getCurrentPlayer().getNickname());
        table.nextTurn();
        assertEquals("Lebron", table.getCurrentPlayer().getNickname());
        table.nextTurn();
        assertEquals("MJ", table.getCurrentPlayer().getNickname());
    }

    /**
     * tests whether get-previous- and -nextPlayer indeed returns the previous and the next player in the order of play.
     */
    @Test
    public void testPreviousNextPlayer() {
        assertEquals("Luca", table.getPreviousPlayer().getNickname());
        assertEquals("MJ", table.getNextPlayer().getNickname());
    }

    /**
     * tests reversing the order of play by playing a change direction card. We call next turn twice and change the order of play in between
     * those calls by playing a change direction card. Therefore, the currentPlayer should now be KD who was added first to the players array.
     */
    @Test
    public void testChangeDirectionsAfterPlay() {
        Card card = new Card(Card.Color.BLUE, Card.Value.FIVE);
        table.getCurrentPlayer().playCard(card);
        table.nextTurn();
        card = new Card(Card.Color.BLUE, Card.Value.CHANGE_DIRECTION);
        table.getCurrentPlayer().playCard(card);
        table.nextTurn();
        assertEquals("KD", table.getCurrentPlayer().getNickname());
    }

    /**
     * tests if the scores are calculated correctly, after a player has won a round. The score of the cards added to the hand of each player sum
     * up to exactly 100 points. As the calculateScores method is only called when the winner does not have cards any more, we substract 100 from
     * the score calculated before comparing it to the expected value.
     */
    @Test
    public void testCalculateScores() {
        ArrayList<Card> hand = new ArrayList<>();
        // cards are worth 100 points.
        hand.add(new Card(Card.Color.WILD, Card.Value.DRAW_FOUR));
        hand.add(new Card(Card.Color.GREEN, Card.Value.DRAW_TWO));
        hand.add(new Card(Card.Color.RED, Card.Value.SKIP));
        hand.add(new Card(Card.Color.YELLOW, Card.Value.SIX));
        hand.add(new Card(Card.Color.BLUE, Card.Value.FOUR));
        for (Player p: players) {
            p.setHand(hand);
        }
        player.setHand(hand);
        table.calculateScores(player);
        // assuming his hand should be empty
        int score = table.getScoreBoard().get(player) -100;
        assertEquals(FINAL_SCORE, score);
    }

}
