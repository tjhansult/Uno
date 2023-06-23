package server.model.deck;

import server.model.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

public class Deck {
    private ArrayList<Card> playingCards;
    private ArrayList<Card> usedCards;

    private final int NUMERICAL_CARDS_EACH = 2;
    private final int WILD_CARDS_EACH = 4;

    //--------------------------CONSTRUCTOR--------------------------

    public Deck() {
        this.playingCards = new ArrayList<>();
        this.usedCards = new ArrayList<>();
        generateCards();
    }

    //--------------------------METHODS--------------------------

    /**
     * Wrapper method which performs relevant tasks
     */
    public void generateCards() {
        generateNumericalCards();
        generateZeroCards();
        generateWildCards();
        shuffleFirstDeck();
    }

    /**
     * Method shuffles the first deck until it's valid
     */
    public void shuffleFirstDeck() {
        for (int i = 0; i < 4; i++) {
            Collections.shuffle(playingCards);
        }
    }

    /**
     * Generates numerical cards
     */
    public void generateNumericalCards() {
        EnumSet<Card.Color> colorsSet = EnumSet.of(Card.Color.WILD);
        EnumSet<Card.Value> valueSet = EnumSet.of(Card.Value.DRAW_FOUR, Card.Value.PICK_COLOR, Card.Value.ZERO);
        for (int i = 0; i < NUMERICAL_CARDS_EACH; i++) {
            for (Card.Color color : EnumSet.complementOf(colorsSet)) {
                for (Card.Value value : EnumSet.complementOf(valueSet)) {
                    playingCards.add(new Card(color, value));
                }
            }
        }
    }

    /**
     * Generates zero cards, since there are only 4 of them
     */
    public void generateZeroCards() {
        playingCards.add(new Card(Card.Color.BLUE, Card.Value.ZERO));
        playingCards.add(new Card(Card.Color.RED, Card.Value.ZERO));
        playingCards.add(new Card(Card.Color.YELLOW, Card.Value.ZERO));
        playingCards.add(new Card(Card.Color.GREEN, Card.Value.ZERO));
    }

    /**
     * Generates wild cards
     */
    public void generateWildCards() {
        for (int i = 0; i < WILD_CARDS_EACH; i++) {
            playingCards.add(new Card(Card.Color.WILD, Card.Value.DRAW_FOUR));
            playingCards.add(new Card(Card.Color.WILD, Card.Value.PICK_COLOR));
        }
    }

    /**
     * Shuffles all used cards and puts it into playing cards
     */
    public void reShuffle() {
        Card card1 = this.usedCards.get(this.usedCards.size() - 1);
        Card card2 = this.usedCards.get(this.usedCards.size() - 2);
        ArrayList<Card> tempArr = this.usedCards;
        tempArr.remove(card1);
        tempArr.remove(card2);
        this.playingCards = tempArr;
        this.usedCards = new ArrayList<>();

        this.usedCards.add(card2);
        this.usedCards.add(card1);

        Collections.shuffle(playingCards);
    }

    //--------------------------GETTERS--------------------------

    public ArrayList<Card> getPlayingCards() {
        return playingCards;
    }

    public ArrayList<Card> getUsedCards() {
        return usedCards;
    }

    //--------------------------SETTERS--------------------------

    public void setPlayingCards(ArrayList<Card> cards) {
        this.playingCards = cards;
    }

}
