package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.model.card.Card;
import server.model.deck.Deck;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckTest {
    /**
     * Test variable deck
     */
    private Deck deck;

    /**
     * Expected values for the test cases, the total card values are based on the UNO rules. After reshuffling a deck,
     * the last two cards should be kept on the discard pile and the rest should be reshuffled to the playing Cards (Drawing pile).
     */
    private final int TOTAL_NUMERICAL_CARDS = 76;
    private final int TOTAL_CARDS = 108;
    private final int TOTAL_WILD_CARDS = 8;
    private final int TOTAL_SPECIAL_CARDS = 24;
    private final int PLAYING_CARDS_AFTER_SHUFFLE = 106;
    private final int USED_CARDS_AFTER_SHUFFLE = 2;

    /**
     * initializes the test variable.
     */
    @BeforeEach
    public void setUp() {
        deck = new Deck();
    }

    /**
     * tests if the deck is properly generated and has the correct amount of numerical cards, since in Card.Value we start listing the values
     * by special cards, and there are 5 special values, if the ordinal of the value is >= 5 we know that it is a numericCard.
     */
    @Test
    public void testGenerateNumericalCards() {
        int numericalCount = 0;
        for (Card c: deck.getPlayingCards()) {
            if(c.getValue().ordinal()>=5) {
                numericalCount++;
            }
        }
        assertEquals(TOTAL_NUMERICAL_CARDS, numericalCount);
    }

    /**
     * tests if the deck is properly generated and has the correct amount of total cards, wild cards and special cards, by looping through the entire deck
     * and increasing the corresponding count in case it falls under one of the mentioned categories. The expected values are defined as constants and according
     * to a uno deck.
     */
    @Test
    public void testGenerateDeck() {
        assertEquals(TOTAL_CARDS, deck.getPlayingCards().size());
        int wCount = 0;
        int specialCount = 0;
        for (Card c: deck.getPlayingCards()) {
            if (c.getColor()== Card.Color.WILD) {
                wCount++;
            } else if (c.getValue()== Card.Value.DRAW_TWO||c.getValue()== Card.Value.CHANGE_DIRECTION||c.getValue()== Card.Value.SKIP) {
                specialCount++;
            }
        }
        assertEquals(TOTAL_WILD_CARDS, wCount);
        assertEquals(TOTAL_SPECIAL_CARDS, specialCount);
    }

    /**
     * tests the reshuffling method of the deck, the reshuffle method of the deck will only be called when there is no more cards on the drawing pile to draw from.
     * The test was designed taking this into account, as when you reshuffle before the playingCards are 0, all remaining cards in the playingCards will be lost.
     * It also stores the size of the playingCards before looping through them, because inside the loop we remove from playingCards and would thereby change the size.
     */
    @Test
    public void testReShuffle() {
        int j = deck.getPlayingCards().size();
        for (int i=0; i < j; i++) {
            deck.getUsedCards().add(deck.getPlayingCards().get(0));
            deck.getPlayingCards().remove(0);
        }
        deck.reShuffle();
        assertEquals(PLAYING_CARDS_AFTER_SHUFFLE, deck.getPlayingCards().size());
        assertEquals(USED_CARDS_AFTER_SHUFFLE, deck.getUsedCards().size());
    }
    }

