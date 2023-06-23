package server.model.table;

import server.controller.UNO;
import server.model.card.Card;
import server.model.deck.Deck;
import server.model.player.NetworkPlayer;
import server.model.player.factory.Player;
import server.model.table.gameModes.factory.PlayingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class Table {
    private ArrayList<Player> players;
    private final HashMap<Player, Integer> SCOREBOARD;
    private Deck deck;
    private int currentTurnIndex;
    private final PlayingMode PLAYINGMODE;
    private Card currentCard;
    private Card.Color indicatedColor;
    private boolean drawFourPlayable;
    private boolean hasWinner;
    private boolean clockWise;
    private final UNO UNO;
    private final int DISTRUBUTED_CARDS = 7;

    public Table(ArrayList<Player> players, PlayingMode playingMode, UNO uno) {
        this.players = players;
        this.PLAYINGMODE = playingMode;
        setUpRound(new Deck());
        this.SCOREBOARD = new HashMap<>();
        for (Player player : this.players) {
            SCOREBOARD.put(player, 0);
        }
        clockWise = true;
        this.UNO = uno;
    }

    public void setUpRound(Deck deckArg) {
        this.deck = deckArg;
        this.currentCard = this.deck.getPlayingCards().get(0);
        this.deck.getPlayingCards().remove(0);
        this.deck.getUsedCards().add(this.currentCard);
        this.indicatedColor = null;
        this.distributeHands();
        this.drawFourPlayable = true;
    }

    //--------------------------METHODS--------------------------

    /**
     * Distributes hands to each player in the game by removing the cards from the deck.
     * The number of cards to be distributed to each player is specified by the constant DISTRIBUTED_CARDS.
     */
    public void distributeHands() {
        ArrayList<Card> tempDeck = deck.getPlayingCards();
        for (Player player : players) {
            ArrayList<Card> tempHand = new ArrayList<>();
            for (int i = 0; i < DISTRUBUTED_CARDS; i++) {
                tempHand.add(tempDeck.get(0));
                tempDeck.remove(0);
            }
            player.setHand(tempHand);
        }
        deck.setPlayingCards(tempDeck);
    }

    /**
     * Reverses order of a players if there is more than 2 players, otherwise acts like a skip
     */
    public void reversePlayers() {
        if (this.players.size() == 2) {
            nextTurn();
        }
        clockWise = !clockWise;
    }

    /**
     * Determines the next player's turn.
     * The turn order is determined by the clockWise variable and the currentTurnIndex.
     */
    public void nextTurn() {
        if (clockWise) {
            if (currentTurnIndex < players.size() - 1) {
                currentTurnIndex++;
            } else {
                currentTurnIndex = 0;
            }
        } else {
            if (currentTurnIndex > 0) {
                currentTurnIndex--;
            } else {
                currentTurnIndex = players.size() - 1;
            }
        }
    }

    /**
     * Sets indicatedcolor to null
     */
    public void resetIndicatedColor() {
        this.indicatedColor = null;
    }

    /**
     * Determines if draw four is eligible to be played.
     * This is determined by checking the current player's hand against the last used card in the deck.
     */
    public void drawFourEligibility() {
        for (Card card : this.getCurrentPlayer().getHand()) {
            if (card.getColor().equals(this.getDeck().getUsedCards().get(this.getDeck().getUsedCards().size() - 2).getColor())) {
                this.drawFourPlayable = false;
                break;
            }
        }
    }

    /**
     * Skips the current turn to the next player.
     */
    public void skip() {
        nextTurn();
    }

    /**
     * Calculates the scores for each player in the game based on the cards in their hands.
     * The winner is determined by the player passed as an argument.
     * @ensures winner != 0
     */
    public void calculateScores(Player winner) {
        assert winner != null;
        int score = 0;
        for (Player player : players) {
            if (player.getHand().size() > 0) {
                for (Card card : player.getHand()) {
                    if (card.getValue() == Card.Value.DRAW_FOUR || card.getValue() == Card.Value.PICK_COLOR) {
                        score += 50;
                    } else if (card.getValue() == Card.Value.DRAW_TWO || card.getValue() == Card.Value.SKIP || card.getValue() == Card.Value.CHANGE_DIRECTION) {
                        score += 20;
                    } else {
                        score += card.getValue().ordinal() - 5;
                    }
                }
            }
        }
        int i = this.SCOREBOARD.get(winner) + score;
        this.SCOREBOARD.put(winner, i);
    }


    //--------------------------GETTERS--------------------------

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Deck getDeck() {
        return deck;
    }

    public Player getPreviousPlayer() {
        if (clockWise) {
            if (currentTurnIndex == 0) {
                return this.players.get(players.size() - 1);
            }
            return this.players.get(currentTurnIndex - 1);
        } else {
            if (currentTurnIndex == players.size() - 1) {
                return this.players.get(0);
            } else {
                return this.players.get(currentTurnIndex + 1);
            }
        }
    }

    public Player getNextPlayer() {
        if (clockWise) {
            if (currentTurnIndex == players.size() - 1) {
                return this.players.get(0);
            }
            return this.players.get(currentTurnIndex + 1);
        } else {
            if (currentTurnIndex == 0) {
                return this.players.get(players.size() - 1);
            } else {
                return this.players.get(currentTurnIndex - 1);
            }
        }
    }


    public Player getCurrentPlayer() {
        return this.players.get(currentTurnIndex);
    }

    public PlayingMode getPlayingMode() {
        return PLAYINGMODE;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public Card.Color getIndicatedColor() {
        return indicatedColor;
    }

    public HashMap<Player, Integer> getScoreBoard() {
        return this.SCOREBOARD;
    }

    public boolean isDrawFourPlayable() {
        return drawFourPlayable;
    }

    public boolean isHasWinner() {
        return hasWinner;
    }

    public boolean isClockWise() {
        return clockWise;
    }

    public UNO getUno() {
        return UNO;
    }

    //--------------------------SETTERS--------------------------

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    public void setIndicatedColor(Card.Color indicatedColor) {
        this.indicatedColor = indicatedColor;
        if (this.indicatedColor != null){
            for (Player p : this.players){
                if (p instanceof NetworkPlayer){
                    ((NetworkPlayer)p).getSh().doBroadcastGameMessage("The color has been changed to " + indicatedColor);
                }
            }
        }
    }

    public void setDrawFourPlayable(boolean drawFourPlayable) {
        this.drawFourPlayable = drawFourPlayable;
    }

    public void setCurrentTurnIndex(int currentTurnIndex) {
        this.currentTurnIndex = currentTurnIndex;
    }

    public void setHasWinner(boolean hasWinner) {
        this.hasWinner = hasWinner;
    }
}
