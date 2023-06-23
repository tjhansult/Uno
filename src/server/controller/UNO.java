package server.controller;

import server.model.card.Card;
import server.model.deck.Deck;
import server.model.player.ComputerPlayer;
import server.model.player.HumanPlayer;
import server.model.player.NetworkPlayer;
import server.model.player.factory.Player;
import server.model.table.Table;
import server.model.table.gameModes.Normal;
import server.model.table.gameModes.Progressive;
import server.model.table.gameModes.SevenZero;
import server.model.table.gameModes.factory.PlayingMode;
import server.view.TUI;

import java.util.*;

public class UNO implements Runnable {
    private final TUI TUI = new TUI();
    private ArrayList<Player> players;
    private Table table;
    private boolean roundOver = false;
    private PlayingMode gameMode;

    /**
     * The main method that starts the game by creating a UNO object and calling its start() and play() methods.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        UNO uno = new UNO();
        uno.start();
        uno.setup(uno.players, uno.gameMode);
        uno.play();
    }

    /**
     * A method that asks the user to enter the names of all players. The names are stored in the players list.
     *
     * @param playersAmount the number of players in the game
     */
    public void nameInput(int playersAmount) {
        this.players.clear();
        for (int i = 0; i < playersAmount; i++) {
            String name = TUI.askNameInput(i);
            String[] spl = name.split(" ");
            if (spl[spl.length - 1].equals("c")) {
                StringBuilder sb = new StringBuilder(name);
                sb.deleteCharAt(sb.length() - 1);
                sb.deleteCharAt(sb.length() - 1);
                players.add(new ComputerPlayer(sb.toString()));
            } else {
                players.add(new HumanPlayer(name));
            }
        }
    }

    /**
     * A method that asks the user to choose the type of game to play.
     *
     * @ensures playingMode is equal to normal, progressive or sevenZero
     */
    public void modeInput() {
        String playingMode = TUI.askModeInput();

        switch (playingMode) {
            case "normal":
                this.gameMode = new Normal();
                break;
            case "progressive":
                this.gameMode = new Progressive();
                break;
            case "sevenZero":
                this.gameMode = new SevenZero();
                break;
            default:
                TUI.printInvalidInput();
                modeInput();
                break;
        }
    }

    /**
     * Starts the game asking user for basic input to set up a game
     *
     * @ensures playersAmount to be between 2 and 10
     */
    public void start() {
        int playersAmount = TUI.askNumberOfPlayersInput();
        if (playersAmount > 10 || playersAmount < 2) {
            this.TUI.printCustomMessage("Enter number from 2 to 10");
            start();
            return;
        }
        this.players = new ArrayList<>();
        nameInput(playersAmount);
        modeInput();
    }

    /**
     * Broadcasts BGI for every player
     *
     * @ensures that every network player is informed
     */
    public void informAll() {
        for (Player player : this.players) {
            if (player instanceof NetworkPlayer) {
                ((NetworkPlayer) player).broadcastTurn();
            }
        }
    }

    /**
     * Plays a game until not finished
     */
    public void play() {
        while (gameOver() == null) {
            this.roundOver = false;
            while (!this.roundOver) {
                tablePrinter();
                informAll();
                String input1 = createInput();
                while (!handleMove(input1)) {
                    if (table.getCurrentPlayer() instanceof NetworkPlayer) {
                        NetworkPlayer np = ((NetworkPlayer) table.getCurrentPlayer());
                        np.broadcastTurn();
                    } else {
                        tablePrinter();
                    }
                    input1 = createInput();
                }

                table.nextTurn();

                if (gameOver() != null) {
                    TUI.announceWinner(gameOver().getNickname());
                    for (Player p : players) {
                        if (p instanceof NetworkPlayer) {
                            ((NetworkPlayer) p).getSh().doGameEnded(gameOver().getNickname());
                        }
                    }
                    break;
                }
                this.roundOver();
            }
        }
    }

    /**
     * Creates an input for a player's turn.
     *
     * @return the input for the player's turn
     * @ensures input1 is always set
     */
    private String createInput() {
        String input1;
        if (table.getCurrentPlayer() instanceof HumanPlayer) {
            input1 = TUI.askForMove(table.getCurrentPlayer().getNickname());
        } else if (table.getCurrentPlayer() instanceof ComputerPlayer) {
            TUI.printMove(table.getCurrentPlayer().getNickname());
            ComputerPlayer cp = (ComputerPlayer) table.getCurrentPlayer();
            input1 = cp.translator();
        } else {
            NetworkPlayer np = (NetworkPlayer) table.getCurrentPlayer();
            input1 = np.getTranslation();
            np.resetTranslation();
        }
        return input1;
    }

    /**
     * Sets up a game of UNO.
     *
     * @param players  the players in the game
     * @param gameMode the mode of the game
     */
    public void setup(ArrayList<Player> players, PlayingMode gameMode) {
        this.players = players;
        ArrayList<Card> d = new Deck().getPlayingCards();
        Collections.shuffle(d);
        int mpi = findDealer(d);
        createTable(gameMode);
        setPlayingOrder(mpi);
        table.getPlayingMode().adjustToFirstCard(table);
    }

    /**
     * Finds the dealer of the game.
     * The method goes through the players and deals one card to each player.
     * The player with the highest value card becomes the dealer.
     * If there are no cards with value greater than 4, the method calls itself again to deal another set of cards.
     *
     * @param d the deck of cards
     * @return the index of the dealer player in the players list
     * @requires d not to be null
     * @ensures maxplayers is set, if it returns -1 we know that dealer was not found
     */
    public int findDealer(ArrayList<Card> d) {
        assert d != null;
        int maxValue = -1;
        int maxPlayerIndex = 0;

        for (int i = 0; i < players.size(); i++) {
            Card dealtCard = d.remove(0);
            TUI.findDealerPrinter(players.get(i).getNickname(), dealtCard.toString());
            if (dealtCard.getValue().ordinal() > maxValue && dealtCard.getValue().ordinal() > 4) {
                maxValue = dealtCard.getValue().ordinal();
                maxPlayerIndex = i;
            }
        }

        if (maxValue == -1) {
            TUI.findDealerPrinter();
            findDealer(d);
        }

        TUI.findDealerPrinter(players.get(maxPlayerIndex).getNickname());

        return maxPlayerIndex;
    }

    /**
     * Sets the playing order by determining the next player to play after the dealer.
     * The method sets the current turn index to the next player after the dealer.
     * If the dealer is the last player in the players list, the next player becomes the first player in the list.
     *
     * @param maxPlayerIndex the index of the dealer player in the players list
     * @ensures that maxPlayerIndex is >= 0
     */
    private void setPlayingOrder(int maxPlayerIndex) {
        assert maxPlayerIndex >= 0;
        if (maxPlayerIndex == players.size() - 1) {
            this.table.setCurrentTurnIndex(0);
        } else {
            this.table.setCurrentTurnIndex(maxPlayerIndex + 1);
        }
        TUI.announceStarter(table.getCurrentPlayer().getNickname());
    }

    /**
     * Creates the table and sets the reference to the table in each player object.
     * The method initializes a new table object with the players, game mode, and reference to the game manager.
     * It then sets the reference to the table in each player object.
     *
     * @param gameMode the game mode
     */
    private void createTable(PlayingMode gameMode) {
        table = new Table(players, gameMode, this);
        for (Player player : players) {
            player.setTable(table);
        }
    }

    /**
     * Prints current card and player's hand
     */
    public void tablePrinter() {
        TUI.printCurrentCard(table.getCurrentCard());
        TUI.printHand(table.getCurrentPlayer());
    }

    /**
     * inputDrawNetworkPlayer is a method that takes input from the NetworkPlayer in the game and handles its actions.
     * It takes the last card from the NetworkPlayer's hand, asks the NetworkPlayer if it wants to play or skip the card,
     * and based on the input, it plays the card or skips it.
     *
     * @ensures translation is reset
     */
    public void inputDrawNetworkPlayer() {
        NetworkPlayer np = (NetworkPlayer) table.getCurrentPlayer();
        Card c = np.getHand().get(np.getHand().size() - 1);
        String card = c.getColor() + " " + c.getValue().toString();
        np.getSh().doDrewPlayableCard(card);
        String choice = np.getTranslation();
        if (choice.equals("skip")) {
            np.resetTranslation();

        } else if (choice.equals("proceed")) {
            np.playCard(np.getHand().get(np.getHand().size() - 1));
            np.resetTranslation();

        }
        np.resetTranslation();
    }

    /**
     * inputDrawHumanPlayer is a method that takes input from the HumanPlayer in the game and handles its actions.
     * It asks the HumanPlayer if it wants to play the last card from its hand and based on the input,
     * it either plays the card or continues asking for the input.
     */
    public void inputDrawHumanPlayer() {
        String input = TUI.askYesNo(table.getCurrentPlayer().getHand().get(table.getCurrentPlayer().getHand().size() - 1).toString());
        if (input.equals("yes")) {
            table.getCurrentPlayer().playCard(table.getCurrentPlayer().getHand().get(table.getCurrentPlayer().getHand().size() - 1));
        } else if (!input.equals("no")) {
            TUI.printInvalidInput();
            inputDraw();
        }
    }

    /**
     * inputDraw is a method that handles the draw action in the game.
     * It calls the draw method for the current player, sets the drawFourPlayable flag and based on the type of the current player,
     * calls the inputDrawNetworkPlayer or inputDrawHumanPlayer method for further actions.
     */
    public void inputDraw() {
        table.getCurrentPlayer().draw(1);
        table.setDrawFourPlayable(true);
        if (table.getPlayingMode().validMove(table.getCurrentPlayer().getHand().get(table.getCurrentPlayer().getHand().size() - 1), this.table)) {
            if (table.getCurrentPlayer() instanceof NetworkPlayer) {
                inputDrawNetworkPlayer();
                return;
            }
            if (!(table.getCurrentPlayer() instanceof ComputerPlayer)) {
                inputDrawHumanPlayer();
            }

        } else {
            TUI.printCardAdded(table.getCurrentPlayer().getNickname());
        }

    }

    /**
     * inputChallenge is a method that handles the challenge action in the game.
     * If the drawFourPlayable flag is false, it prints that the challenge is not valid, and the previous player draws 4 cards.
     * If the drawFourPlayable flag is true, it prints that the challenge is valid and the current player draws 2 cards.
     *
     * @return boolean - True if the challenge is valid, false otherwise.
     */
    public boolean inputChallenge() {
        if (!table.isDrawFourPlayable()) {
            TUI.printChallenge(false);
            table.getPreviousPlayer().draw(4);
            for (int i = 0; i < 4; i++) {
                table.getCurrentPlayer().getHand().remove(table.getCurrentPlayer().getHand().size() - 1);
                table.setDrawFourPlayable(true);
            }
            return false;
        } else {
            TUI.printChallenge(true);
            table.getCurrentPlayer().draw(2);
        }
        return true;
    }

    /**
     * inputCard is a method that takes an input from the player and plays the card if the input is valid.
     * It checks if the card is a valid move and if it is, it plays the card.
     * If the input is not valid, it prints an error message and for a NetworkPlayer, it also sends an error message.
     *
     * @param input - The input string from the player
     * @return boolean - True if the card was played, false otherwise.
     */
    public boolean inputCard(String input) {
        if (table.getPlayingMode().validMove(table.getCurrentPlayer().getHand().get(Integer.parseInt(input)), this.table)) {
            table.getCurrentPlayer().playCard(table.getCurrentPlayer().getHand().get(Integer.parseInt(input)));
            return true;
        }
        TUI.printInvalidInput();
        if (this.table.getCurrentPlayer() instanceof NetworkPlayer) {
            ((NetworkPlayer) this.table.getCurrentPlayer()).getSh().sendMessage("ERR|E006");
        }

        return false;

    }

    /**
     * Handles a player's turn by taking input and handling the move.
     *
     * @param input the input from the player
     * @return whether the turn was successful or not
     */
    public boolean handleMove(String input) {
        boolean b = false;
        if (input != null) {
            String[] splitted = input.split(" ");
            if (this.getTable().getPlayingMode().getForwardCount() == 0) {
                if (splitted[0].equals("draw")) {
                    inputDraw();
                    b = true;
                } else if (splitted[0].equals("skip")) {
                    b = true;
                } else if (splitted[0].equals("challenge")) {
                    b = inputChallenge();
                } else if (splitted.length == 2 && splitted[1].equals("uno")) {
                    b = inputCard(splitted[0]);
                } else if (table.getCurrentPlayer().getHand().size() == 2 && !(splitted.length == 2 && splitted[1].equals("uno"))) {
                    b = true;
                    if (table.getCurrentPlayer() instanceof NetworkPlayer) {
                        ((NetworkPlayer) table.getCurrentPlayer()).getSh().doBroadcastGameMessage("You didn't say uno. You were punished with 2 cards!");
                    }
                    TUI.noUno();
                    table.getCurrentPlayer().draw(2);
                } else if (isInRange(splitted[0])) {
                    b = inputCard(input);
                } else {
                    TUI.printInvalidInput();
                }
            } else {
                if (splitted[0].equals("skip")) {
                    b = true;
                    table.getCurrentPlayer().draw(table.getPlayingMode().getForwardCount());
                    table.getPlayingMode().setForwardCount(0);
                } else if (isInRange(splitted[0])) {
                    b = inputCard(splitted[0]);
                } else if (splitted.length == 2 && splitted[1].equals("uno")) {
                    b = inputCard(splitted[0]);
                } else if (table.getCurrentPlayer().getHand().size() == 2 && !(splitted.length == 2 && splitted[1].equals("uno"))) {
                    b = inputCard(splitted[0]);
                    if (table.getCurrentPlayer() instanceof NetworkPlayer) {
                        ((NetworkPlayer) table.getCurrentPlayer()).getSh().doBroadcastGameMessage("You didn't say uno. You were punished with 2 cards!");
                    }
                    TUI.noUno();
                    table.getCurrentPlayer().draw(2);
                }
            }
        }
        return b;
    }

    public boolean isInRange(String str) {
        if (str == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(str);
            if (i < table.getCurrentPlayer().getHand().size()) {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    /**
     * Returns the winning player, if there is one, otherwise returns null.
     *
     * @return the winning player
     */
    public Player gameOver() {
        if (this.players.size() == 1) {
            return this.players.get(0);
        }

        for (Player player : table.getScoreBoard().keySet()) {
            if (table.getScoreBoard().get(player) >= 500) {
                return player;
            }
        }

        return null;
    }

    /**
     * The roundOver method is used to end a round of the game and reset the game state.
     * If the game has a winner, it is determined by finding the player with the highest score in the score board.
     * The round end message is sent to all network players.
     * A new deck of cards is created, shuffled and the playing order is determined by the new dealer.
     * The table is set up for a new round and the first card is adjusted to match the game rules.
     * The roundOver variable is set to true and the hasWinner variable is set to false.
     */
    public void roundOver() {
        if (table.isHasWinner()) {
            String winner = Collections.max(table.getScoreBoard().entrySet(), Map.Entry.comparingByValue()).getKey().getNickname();
            for (Player p : players) {
                if (p instanceof NetworkPlayer) {
                    ((NetworkPlayer) p).getSh().doRoundEnded(winner);
                }
            }
            Deck d = new Deck();
            Collections.shuffle(d.getPlayingCards());
            int mpi = findDealer(d.getPlayingCards());
            setPlayingOrder(mpi);
            table.setPlayers(players);
            table.setCurrentTurnIndex(0);
            table.setUpRound(d);
            table.getPlayingMode().adjustToFirstCard(table);
            this.roundOver = true;
            table.setHasWinner(false);
        }
    }


    //____________________GETTERS AND SETTERS_______________________

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public server.view.TUI getTUI() {
        return TUI;
    }

    public boolean isRoundOver() {
        return roundOver;
    }
    //____________________RUN METHOD_______________________

    @Override
    public void run() {
        play();
    }
}
