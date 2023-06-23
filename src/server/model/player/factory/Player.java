package server.model.player.factory;

import server.controller.UNO;
import server.model.card.Card;
import server.model.player.contract.PlayerActions;
import server.model.table.Table;

import java.util.ArrayList;

public abstract class Player implements PlayerActions {
    private final String NICKNAME;
    private ArrayList<Card> hand;
    private UNO UNO;
    private Table table;


    /**
     * The draw method is used to draw a specified number of cards from the deck and add it to the player's hand.
     * The method first checks if the requested number of cards can be drawn from the deck, and if so, removes the
     * specified number of cards from the deck and adds it to the player's hand.
     *
     * @param amount An int representing the number of cards to be drawn.
     */
    @Override
    public void draw(int amount) {
        if (checkDrawPossibility(amount)) {
            for (int i = 0; i < amount; i++) {
                getHand().add(getTable().getDeck().getPlayingCards().get(0));
                getTable().getDeck().getPlayingCards().remove(0);
            }
        }
    }

    /**
     * The playCard method is used to play a card from the player's hand onto the table.
     * The method removes the card from the player's hand, sets it as the current card, and adds it to the used cards.
     * The method then calls the performWildCardAction method to perform any specific actions related to the played card,
     * and checks if the player is a winner.
     *
     * @param card An instance of the Card class representing the card to be played.
     * @requires card is not null
     */
    @Override
    public void playCard(Card card) {
        assert card != null;
        placeCard(card);
        getTable().resetIndicatedColor();
        Player nextPlayer = this.getTable().getNextPlayer();
        this.getTable().getPlayingMode().performWildCardAction(card, this, nextPlayer);

    }

    /**
     * The placeCard method is used to place a card from the player's hand onto the table.
     * The method removes the card from the player's hand, sets the current card on the table, and adds the card
     * to the used cards.
     *
     * @param card An instance of the Card class representing the card to be placed.
     */
    @Override
    public void placeCard(Card card) {
        getTable().setDrawFourPlayable(true);
        getHand().remove(card);
        isWinner();
        getTable().setCurrentCard(card);
        getTable().getDeck().getUsedCards().add(card);
    }

    /**
     * The checkDrawPossibility method is used to check if the requested number of cards can be drawn from the deck.
     * The method returns true if the size of the deck's playing cards is greater than or equal to the requested
     * amount, and returns false otherwise.
     *
     * @param amount An int representing the number of cards to possibly draw.
     * @return A boolean value indicating whether the requested number of cards can be drawn from the deck.
     */
    @Override
    public boolean checkDrawPossibility(int amount) {
        if (amount > getTable().getDeck().getPlayingCards().size()) {
            getTable().getDeck().reShuffle();
        }

        if (getTable().getDeck().getPlayingCards().size() >= amount) {
            return true;
        } else {
            this.getTable().getUno().getTUI().printCustomMessage("You have drown all the cards");
            return false;
        }
    }

    /**
     * The swapHands method is used to swap the hands of the player with another player.
     * The method takes an instance of the Player class as a parameter and swaps the hands of the current player
     * and the player passed as a parameter.
     *
     * @param other An instance of the Player class representing the other player.
     */
    public void swapHands(Player other) {
        ArrayList<Card> tempHand1 = this.getHand();
        ArrayList<Card> tempHand2 = other.getHand();
        this.setHand(tempHand2);
        other.setHand(tempHand1);
    }

    /**
     * The isWinner method is used to determine if the player has won the game.
     * The method checks if the player's hand is empty, and if so, calculates the player's score, sets the game as having a winner,
     * and displays the score board.
     *
     * @return A boolean value indicating whether the player is a winner (true) or not (false).
     */
    @Override
    public boolean isWinner() {
        if (getHand().size() == 0) {
            getTable().calculateScores(this);
            table.setHasWinner(true);
            this.getTable().getUno().getTUI().printScoreboard(this);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return getNickname();
    }

    //--------------------------CONSTRUCTOR--------------------------

    public Player(String nickname) {
        this.NICKNAME = nickname;
    }

    //--------------------------GETTERS--------------------------

    public String getNickname() {
        return NICKNAME;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public UNO getUNO() {
        return UNO;
    }

    public Table getTable() {
        return table;
    }


    //--------------------------SETTERS--------------------------
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public void setUNO(UNO UNO) {
        this.UNO = UNO;
    }

    public void setTable(Table table) {
        this.table = table;
    }

}
