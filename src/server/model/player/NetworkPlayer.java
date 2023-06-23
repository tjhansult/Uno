package server.model.player;

import server.model.card.Card;
import server.model.player.factory.Player;
import server.controller.ServerHandler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkPlayer extends Player {
    private final ServerHandler SH;
    private String translation = "";
    private final ReentrantLock LOCK = new ReentrantLock();
    private final Condition canGet = LOCK.newCondition();
    private final Condition canSet = LOCK.newCondition();
    private boolean addUno = false;

    //________________________________CONSTRUCTOR__________________________________
    public NetworkPlayer(String nickname, ServerHandler serverHandler) {
        super(nickname);
        this.SH = serverHandler;
    }


    //________________________________METHODS__________________________________

    /**
     * The `translate` method is a synchronized method that sets the translation of a given card.
     * If the card is "draw", "skip", or "proceed", it sets the translation to be the same as the input.
     * If the card is in the player's hand, the translation is set to the index of the card in the player's hand.
     * If the player says "uno", the `addUno` flag is set to false.
     *
     * @param card the card to be translated
     */
    public synchronized void translate(String card) {
        switch (card) {
            case "draw":
                this.setTranslation("draw");
                break;
            case "skip":
                this.setTranslation("skip");
                break;
            case "proceed":
                this.setTranslation("proceed");
                break;
        }

        int ind = 0;
        String[] spl = card.split(" ");
        for (Card c : super.getHand()) {
            if (spl[0].equals(c.getColor().toString()) && spl[1].equals(c.getValue().toString())) {

                String s = Integer.toString(ind);
                if (addUno) {
                    s += " uno";
                    this.addUno = false;
                }
                this.setTranslation(s);
                break;
            }
            ind++;
        }
    }

    /**
     * The `broadcastTurn` method broadcasts the game information to all players in the game.
     * This includes the current top card, the player's hand, a list of all players in the game,
     * and a boolean indicating if it is the current player's turn.
     */
    public void broadcastTurn() {
        String topCard = super.getTable().getCurrentCard().getColor().toString() + " " + super.getTable().getCurrentCard().getValue().toString();
        String playerHand = "";
        for (Card c : super.getHand()) {
            playerHand += c.getColor().toString() + " " + c.getValue().toString();
            playerHand += ";";
        }
        String playersList = "";
        boolean yourTurn = false;
        for (Player p : super.getTable().getPlayers()) {
            playersList += p.getNickname() + ":" + p.getHand().size() + ":" + p.getTable().getScoreBoard().get(p);
            playersList += ";";
        }
        if (super.getTable().getCurrentPlayer().getNickname().equals(this.getNickname())) {
            yourTurn = true;
        }
        SH.doBroadcastGameInformation(topCard, playerHand, playersList, String.valueOf(yourTurn));
    }

    /**
     * The `pickColor` method is an implementation of the abstract method in the parent class.
     * It invokes the `doAskColour` method in the `SH` object to ask the player to pick a color.
     */
    @Override
    public void pickColor() {
        SH.doAskColour();
    }

    /**
     * The `chooseSwitchHands` method is an implementation of the abstract method in the parent class.
     * It invokes the `doAskChoiceSeven` method in the `SH` object to ask the player to choose a player to switch hands with.
     */
    @Override
    public void chooseSwitchHands() {
        this.getSh().doAskChoiceSeven();
    }

    /**
     * The `pickColor` method sets the indicated color on the table based on the input color.
     * If the input color is invalid, the `doAskColour` method in the `SH` object is invoked to ask the player to pick a color again.
     *
     * @param color the color to be set on the table
     */
    public void pickColor(String color) {
        switch (color) {
            case "BLUE":
                super.getTable().setIndicatedColor(Card.Color.BLUE);
                break;
            case "RED":
                super.getTable().setIndicatedColor(Card.Color.RED);
                break;
            case "GREEN":
                super.getTable().setIndicatedColor(Card.Color.GREEN);
                break;
            case "YELLOW":
                super.getTable().setIndicatedColor(Card.Color.YELLOW);
                break;
            default:
                this.SH.doAskColour();
        }
    }


    public String getTranslation() {
        LOCK.lock();
        try {
            while ((this.translation == null) || this.translation.isEmpty()) {
                canGet.await();
            }
            canSet.signal();
            LOCK.unlock();
            return this.translation;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void setTranslation(String translation) {
        LOCK.lock();
        try {
            while (!(this.translation == null) && !this.translation.isEmpty()) {
                canSet.await();
            }
            this.translation = translation;

            canGet.signal();
            LOCK.unlock();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetTranslation() {
        this.translation = null;
    }


    //_______________________________________GETTERS AND SETTERS_______________________________

    public ServerHandler getSh() {
        return SH;
    }

    public void setAddUno(boolean addUno) {
        this.addUno = addUno;
    }
}
