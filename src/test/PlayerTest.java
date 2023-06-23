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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {
    /**
     * test variables
     */
    private Table table;
    private ArrayList<Player> players;

    /**
     * initializes the player array, with three HumanPlayers. Then initializes the table with the players and the normal playingMode.
     * It also sets the table property for the players.
     */
    @BeforeEach
    public void setUp() {
        players = new ArrayList<Player>();
        players.add(new HumanPlayer("KD"));
        players.add(new HumanPlayer("MJ"));
        players.add(new HumanPlayer("Luca Doncic"));
        table = new Table(players, new Normal(),new UNO());
        for (Player player: players) {
            player.setTable(table);
        }
    }

    /**
     * tests playing a card, and asserting if the current card has indeed changed properly.
     */
    @Test
    public void testPlayCard() {
        table.setCurrentCard(new Card(Card.Color.BLUE, Card.Value.THREE));
        Card card = new Card(Card.Color.BLUE, Card.Value.SEVEN);
        players.get(0).playCard(card);
        assertEquals(card, table.getCurrentCard());
    }

    /**
     * tests the valid move function after the indicatedColor has been changed(simulating playing a pickColor card) and
     * furthermore checking, if after playing a card the indicatedColor is correctly reset to null.
     */
    @Test
    public void testValidMoveAfterPickColor() {
        Card card = new Card(Card.Color.YELLOW, Card.Value.SEVEN);
        table.getCurrentPlayer().playCard(card);
        table.setIndicatedColor(Card.Color.BLUE);
        assertEquals(table.getIndicatedColor(), Card.Color.BLUE);
        card = new Card(Card.Color.BLUE, Card.Value.EIGHT);
        assertTrue(table.getPlayingMode().validMove(card, table));
        table.getCurrentPlayer().playCard(card);
        assertEquals(null, table.getIndicatedColor());
    }

    /**
     * tests playing a wild card on top of another wild card, in which case valid move should return false.
     * During the course of the game, when the indicatedColor is set to a value != null, that means that the
     * current card must be a wild card and validMove should return accordingly.
     */
    @Test
    public void testPlayTwoWildCards() {
        Card card = new Card(Card.Color.WILD, Card.Value.PICK_COLOR);
        table.setIndicatedColor(Card.Color.BLUE);
        assertFalse(table.getPlayingMode().validMove(card, table));
    }

    /**
     * tests the drawing method of a player and asserts the new size of the hand and the new size of the playingCards.
     * The expected size is always the total cards drawn to this point plus the original size for the hand and minus the
     * original size for the playingCards
     */
    @Test
    public void testDrawCards() {
        Player p = players.get(0);
        int handSize = p.getHand().size();
        int playingCardsSize = table.getDeck().getPlayingCards().size();
        p.draw(1);
        assertEquals(handSize+1,p.getHand().size());
        assertEquals(playingCardsSize-1, table.getDeck().getPlayingCards().size());
        p.draw(4);
        assertEquals(handSize+5,p.getHand().size());
        assertEquals(playingCardsSize-5, table.getDeck().getPlayingCards().size());
        p.draw(2);
        assertEquals(handSize+7,p.getHand().size());
        assertEquals(playingCardsSize-7, table.getDeck().getPlayingCards().size());
    }
    /**
     * tests player.isWinner, which refers to being a winner of a round, so in case the players hand is empty.
     * Firstly it is asserted that this returns false, then if it returns true when we set the players hand to
     * an empty arrayList.
     */
    @Test
    public void testIsWinner() {
        assertFalse(players.get(0).isWinner());
        players.get(0).setHand(new ArrayList<>());
        assertTrue(players.get(0).isWinner());
    }
}
