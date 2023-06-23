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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class UnoTest {
    /**
     * test variables
     */
    private ArrayList<Player> players;
    private Table table;
    private UNO uno;
    /**
     * Expected Values
     */
    private final int MAX_PLAYER_INDEX = 4;
    /**
     * initializes the players to five new HumanPlayers, initializes the table with these players and in normal gameMode. Then creates an UNO
     * object, sets its players and table properties and sets the table and uno property for each player.
     * Corresponds to the uno.start() method, as it sets the necessary parameters to start a game, which would otherwise be archieved by asking the user for input.
     */
    @BeforeEach
    public void setUp() {
        players = new ArrayList<Player>();
        players.add(new HumanPlayer("a"));
        players.add(new HumanPlayer("b"));
        players.add(new HumanPlayer("c"));
        players.add(new HumanPlayer("d"));
        players.add(new HumanPlayer("e"));
        uno = new UNO();
        table = new Table(players, new Normal(), uno);
        uno.setPlayers(players);
        uno.setTable(table);
        for (Player player: players) {
            player.setTable(table);
            player.setUNO(uno);
        }
    }

    /**
     * tests finding the Dealer at the beginning of the round, whoever draws the highest card will be the dealer. Since c5 is the highest
     * card in this scenario we expect the findDealer method to return the index four (corresponding to c5)
     */
    @Test
    public void testFindDealer() {
        Card c1 = new Card(Card.Color.BLUE, Card.Value.TWO);
        Card c2 = new Card(Card.Color.RED, Card.Value.THREE);
        Card c3 = new Card(Card.Color.BLUE, Card.Value.FOUR);
        Card c4 = new Card(Card.Color.RED, Card.Value.FIVE);
        Card c5 = new Card(Card.Color.YELLOW, Card.Value.SIX);
        ArrayList<Card> d = new ArrayList<>();
        d.add(c1);
        d.add(c2);
        d.add(c3);
        d.add(c4);
        d.add(c5);
        int mpi = uno.findDealer(d);
        assertEquals(MAX_PLAYER_INDEX, mpi);
    }

    /**
     * tests the handle move method, which is responsible for evaluating the input and calling proper subMethods based on that.
     * It should return true in case the current Players move should be terminated and it should be proceeded to the next player and
     * false otherwise. 0 in this case is the only valid move, the other two moves would be invalid and handleMove should return false.
     * The sequence is 0,2,3 because the size of the hand changes, whenever inputCard returns true, as that means the card was played.
     */
    @Test
    public void testHandleMove() {
        // current player is a, ind 0 in players
        table.setCurrentCard(new Card(Card.Color.BLUE, Card.Value.SEVEN));
        Card c1 = new Card(Card.Color.BLUE, Card.Value.TWO);
        Card c2 = new Card(Card.Color.RED, Card.Value.THREE);
        Card c3 = new Card(Card.Color.BLUE, Card.Value.FOUR);
        Card c4 = new Card(Card.Color.RED, Card.Value.FIVE);
        Card c5 = new Card(Card.Color.YELLOW, Card.Value.SIX);
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        hand.add(c4);
        hand.add(c5);
        this.players.get(0).setHand(hand);
        assertTrue(uno.handleMove("0"));
        assertFalse(uno.handleMove("2"));
        assertFalse(uno.handleMove("3"));
    }

    /**
     * tests the method that is called by handlemove, when the input is a number that corresponds to an index in the players hand. It will then
     * check if this is a valid move and if so play the card an return true, if it is not it will indicate that this move was invalid and return false.
     * The sequence is 0,0,1 because the size of the hand changes, whenever inputCard returns true, as that means the card was played.
     */
    @Test
    public void inputCard() {
        table.setCurrentCard(new Card(Card.Color.YELLOW, Card.Value.FIVE));
        Card c1 = new Card(Card.Color.BLUE, Card.Value.FIVE);
        Card c2 = new Card(Card.Color.RED, Card.Value.THREE);
        Card c3 = new Card(Card.Color.BLUE, Card.Value.FOUR);
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        this.players.get(0).setHand(hand);
        assertTrue(uno.inputCard("0"));
        assertFalse(uno.inputCard("0"));
        assertTrue(uno.inputCard("1"));
    }


    /**
     * tests the gameOver function, that should return Null if the game is not over (no player has more than 500 points) and return
     * the player with >= 500 points if the game is over. We simply set the score for each player and gameOver should return the Player
     * with over 500 points, which in this case would be "b".
     */
    @Test
    public void testGameOver() {
        Player w = players.get(3);
        table.getScoreBoard().put(w, 320);
        w = players.get(2);
        table.getScoreBoard().put(w, 420);
        assertNull(uno.gameOver());
        w = players.get(1);
        table.getScoreBoard().put(w, 520);
        assertEquals(w.getNickname(), uno.gameOver().getNickname());
    }

    /**
     * tests the roundOver function, it should return true when there is a winner of this round (a player has 0 cards) and false otherwise.
     * We call the isWinner function of the player after setting his hand to an empty arraylist, following which it should return true.
     * The uno roundOver function then evaluates if any of the players isWinner methods return true and if so toggles the roundOver variable in
     * uno, therefore we assert this property to be true
     */
    @Test
    public void testRoundOver() {
        uno.roundOver();
        assertFalse(uno.isRoundOver());
        players.get(0).setHand(new ArrayList<>());
        assertTrue(players.get(0).isWinner());
        uno.roundOver();
        assertTrue( uno.isRoundOver());
    }

}

