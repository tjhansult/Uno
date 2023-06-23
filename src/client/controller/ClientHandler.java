package client.controller;

import client.controller.contract.ClientProtocol;
import client.view.ClientTUI;
import server.controller.contract.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ClientHandler implements ClientProtocol, Runnable {
    private boolean isAdmin;
    private boolean gameStarted;
    private final Socket CONNECTION;
    private final BufferedReader IN;
    private final PrintWriter OUT;
    private boolean flag;
    private final ClientTUI CT;

    public ClientHandler(Socket connection) throws IOException {
        this.CONNECTION = connection;
        this.isAdmin = false;
        IN = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        OUT = new PrintWriter(connection.getOutputStream());
        this.flag = true;
        this.CT = new ClientTUI(this);
    }

    private synchronized void seperateAndCall(String input) {
        String[] splitted = input.split("[|]");
        try {
            switch (splitted[0]) {
                case "AH":
                    handleAcceptHandshake();
                    break;
                case "IAD":
                    handleInformAdmin();
                    break;
                case "BPJ":
                    handleBroadcastPlayerJoined(splitted[1]);
                    break;
                case "GST":
                    handleGameStarted(splitted[1]);
                    break;
                case "RST":
                    handleRoundStarted();
                    break;
                case "BGI":
                    handleBroadcastGameInformation(splitted[1], splitted[2], splitted[3], splitted[4]);
                    break;
                case "BCP":
                    handleBroadcastCardPlayed(splitted[1], splitted[2]);
                    break;
                case "BDC":
                    handleBroadcastDrewCard(splitted[1]);
                    break;
                case "BTS":
                    handleBroadcastTurnSkipped(splitted[1]);
                    break;
                case "BRS":
                    handleBroadcastReverse(splitted[1]);
                    break;
                case "BLG":
                    handleBroadcastLeftGame(splitted[1]);
                    break;
                case "RP":
                    handleRemindPlay(splitted[1]);
                    break;
                case "RE":
                    handleRoundEnded(splitted[1]);
                    break;
                case "GE":
                    handleGameEnded(splitted[1]);
                    break;
                case "ERR":
                    handleSendErrorCode(splitted[1]);
                    break;
                case "LOL":
                    handleBroadcastListOfLobbies(splitted[1]);
                    break;
                case "BCL":
                    handleBroadcastCreatedLobby(splitted[1]);
                    break;
                case "BJL":
                    handleBroadcastPlayerJoinedLobby(splitted[1]);
                    break;
                case "AC":
                    handleAskColor();
                    break;
                case "DPC":
                    handleDrewPlayableCard(splitted[1]);
                    break;
                case "BCC":
                    handleBroadcastColorChange(splitted[1]);
                    break;
                case "BUNO":
                    handleBroadcastSayUNO();
                    break;
                case "BGM":
                    handleBroadcastGameMessage(splitted[1]);
                    break;
                case "AC7":
                    handleAskChoiceSeven();
                    break;
                case "BM":
                    handleBroadcastMessage(splitted[1]);
                    break;
                default:
                    CT.printCustomMessage(ServerProtocol.Errors.E001.getMessage());
            }
        } catch (IndexOutOfBoundsException e) {
            CT.printCustomMessage("Command not recognized");
        }
    }

    public void sendMessage(String messageOut) {
        OUT.println(messageOut);
        OUT.flush();
        if (OUT.checkError()) {
            CT.printCustomMessage("An error occured during transmission.");
        }
    }

    public void receiveMessage() throws IOException {
        String messageIn = "";
        try {
            messageIn = IN.readLine();
            if (messageIn == null) {
                closeConnection();
            }
        } catch (IOException e) {
            sendMessage(ServerProtocol.Errors.E001.getMessage());
        }
        seperateAndCall(messageIn);
    }

    public void closeConnection() throws IOException {
        OUT.println("Closing the connection.");
        OUT.flush();
        CONNECTION.close();
        CT.printCustomMessage("Connection Closed.");
        System.exit(1);
    }

    /**
     * This method informs the networking.client that the handshake was accepted (AH).
     */
    @Override
    public void handleAcceptHandshake() {
        CT.printCustomMessage("Handshake accepted.");
    }

    /**
     * This method handles the message being sent by the networking.server regarding informing the networking.client that they are the admin (IAD).
     */
    @Override
    public void handleInformAdmin() {
        this.isAdmin = true;
        CT.printCustomMessage("You're the admin");
    }

    /**
     * This method handles the message being sent by the broadcast player joined (BPJ).
     *
     * @param playerName of type {@code String} representing the unique name of the player
     */
    @Override
    public void handleBroadcastPlayerJoined(String playerName) {
        CT.printPlayerJoined(playerName);
    }

    /**
     * This method handles the message being sent by the game started (GST).
     *
     * @param gameMode of type {@code String} representing the type/mode of the game
     */
    @Override
    public void handleGameStarted(String gameMode) {
        gameStarted = true;
        CT.printGameStarted(gameMode);
    }

    /**
     * This method handles the message being sent by the round started (RST).
     */
    @Override
    public void handleRoundStarted() {
        CT.printCustomMessage("New round has started!");
    }

    private boolean isInRange(String str, String[] splittedHand) {
        if (str == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(str);
            if (i < splittedHand.length) {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    private String askInput() {
        String ind = CT.printMakeMove();
        return ind;
    }

    /**
     * This method handles the message being sent by the broadcast game information method (BGI).
     *
     * @param topCard     of type {@code String} representing the top card on the pile visible to players
     * @param playerHand  of type {@code String} representing the corresponding playre's hand
     * @param playersList of type {@code String} representing the list of players of the game sorted by the order of turn
     * @param isYourTurn  of type {@code String} indicates if it is the player’s turn
     */
    @Override
    public void handleBroadcastGameInformation(String topCard, String playerHand, String playersList, String isYourTurn) {
        if (isYourTurn.equals("true")) {
            CT.printYourTurn(topCard, playerHand, playersList);
            String ind = askInput();
            String[] split = ind.split(" ");
            String[] splitDel = ind.split("[|]");
            String[] splittedHand = playerHand.split(";");
            if (splitDel[0].equals("sm") && splitDel.length > 1) {
                doSendMessage(splitDel[1]);
                handleBroadcastGameInformation(topCard, playerHand, playersList, isYourTurn);
            } else if (split.length > 1 && split[1].equals("uno") && splittedHand.length == 2) {
                doSayUno();
                if (isInRange(split[0], splittedHand)) {
                    String card = splittedHand[Integer.parseInt(split[0])];
                    doPlayCard(card);
                } else {
                    handleBroadcastGameInformation(topCard, playerHand, playersList, isYourTurn);
                }
            } else if (ind.equals("draw")) {
                doDrawCard();
            } else {
                if (isInRange(ind, splittedHand)) {
                    String card = splittedHand[Integer.parseInt(ind)];
                    doPlayCard(card);
                } else {
                    handleBroadcastGameInformation(topCard, playerHand, playersList, isYourTurn);
                }
            }

        } else {
            CT.printNewTurn(topCard, playerHand, playersList);
        }
    }

    /**
     * This method handles the message being sent by the broadcast card played (BCP).
     *
     * @param playerName of type {@code String} representing the unique name of the player
     * @param playedCard of type {@code String} representing the card played
     */
    @Override
    public void handleBroadcastCardPlayed(String playerName, String playedCard) {
        CT.printCardPlayed(playerName, playedCard);
    }

    /**
     * This method handles the message being set by the broadcast drew card (BDC).
     *
     * @param playerName of type {@code String} representing the unique name of the player
     */
    @Override
    public void handleBroadcastDrewCard(String playerName) {
        CT.printDrewCard(playerName);
    }

    /**
     * This method handles the message being sent by the broadcast turn skipped(BTS).
     *
     * @param playerName of type {@code String} representing the unique name of the player
     */
    @Override
    public void handleBroadcastTurnSkipped(String playerName) {
        CT.printTurnSkipped(playerName);
    }

    /**
     * This method handles the message being sent by the broadcast reverse (BRS).
     *
     * @param direction of type {@code String} representing the direction of the game
     */
    @Override
    public void handleBroadcastReverse(String direction) {
        if (direction.equals("true")) {
            CT.printCustomMessage("Direction was changed. Now it's clockwise");
        } else {
            CT.printCustomMessage("Direction was changed. Now it's counter clockwise");
        }
    }

    /**
     * This method handles the message being sent by the broadcast left game (BLG).
     *
     * @param playerName of type {@code String} representing the unique name of the player
     */
    @Override
    public void handleBroadcastLeftGame(String playerName) {
        CT.printLeftGame(playerName);
    }

    /**
     * This method handles the message being sent by the networking.server, reminding the networking.client to play (RP).
     *
     * @param timeLeft of type {@code String} representing the time left to play
     */
    @Override
    public void handleRemindPlay(String timeLeft) {
    }

    /**
     * This method handles the message being sent by the round ended (RE).
     *
     * @param playerName of type {@code String} representing the unique name of the winner of the round
     */
    @Override
    public void handleRoundEnded(String playerName) {
        CT.printRoundEnded(playerName);
    }

    /**
     * This method handles the message being sent by the game ended (GE).
     *
     * @param playerName of type {@code String} representing the unique name of the winner of the game
     */
    @Override
    public void handleGameEnded(String playerName) {
        this.flag = false;
        CT.printGameEnded(playerName);
    }

    /**
     * This method handles the message being sent by send error code (E***).
     *
     * @param errorCode of type {@code String} containing the error code
     */
    @Override
    public void handleSendErrorCode(String errorCode) {
        switch (errorCode) {
            case "E001":
                CT.printCustomMessage("Please enter a valid command.");
                break;
            case "E002":
                askStartInput();
                CT.printCustomMessage(ServerProtocol.Errors.E002.getMessage());
                break;
            case "E003":
                CT.printCustomMessage("Please enter a valid command!");
                break;
            case "E006":
                CT.printCustomMessage("Please type a valid input.");
                break;
            case "E007":
                CT.printCustomMessage("Please type a valid input. ");
                break;
        }
    }

    /**
     * This method handles the message being sent by the broadcast list of lobbies (LOL).
     *
     * @param lobbiesList of type String, representing the list of existing lobbies.
     */
    @Override
    public void handleBroadcastListOfLobbies(String lobbiesList) {
        CT.printListOfLobbies(lobbiesList);
    }

    /**
     * This method handles the message being sent by the broadcast created lobby (BCL).
     *
     * @param lobbyName of type {@code String} representing the unique name of the lobby
     */
    @Override
    public void handleBroadcastCreatedLobby(String lobbyName) {
        CT.printCreateLobby(lobbyName);
    }

    /**
     * This method handles the message being sent by the networking.server about a player joining the lobby (BJL).
     *
     * @param playerName of type {@code String} representing the unique name of the winner of the game
     */
    @Override
    public void handleBroadcastPlayerJoinedLobby(String playerName) {
        CT.printPlayerJoinedLobby(playerName);
    }

    /**
     * This method handles the message being sent by the broadcast message (BM).
     *
     * @param message of type String, representing the chat message.
     */
    @Override
    public void handleBroadcastMessage(String message) {
        CT.printMessage(message);
    }

    /**
     * This method handles the message being sent by the networking.server after a player says UNO (BUNO).
     */
    @Override
    public void handleBroadcastSayUNO() {

    }

    /**
     * This method is intended to handle the possibility when a player picks up a playable card, and is requested whether they want to play it.
     *
     * @param playableCard of type String, representing the playable card.
     */
    @Override
    public void handleDrewPlayableCard(String playableCard) {
        String a = CT.printDrewPlayableCard(playableCard);
        if (a.equals("yes")) {
            doRetainCard("true");
        } else if (a.equals("no")) {
            doRetainCard("false");
        } else {
            CT.printCustomMessage("Please try again. Only 'yes' and 'no' are accepted.");
            handleDrewPlayableCard(playableCard);
        }
    }

    /**
     * This method is intended to handle the request for the player to the left of the dealer for the color of the card.
     * THIS METHOD IS ONLY HANDLED IN THE EVENT THAT THE FIRST CARD DRAWN FROM THE PILE ONTO THE PLAYING AREA (FROM THE DECK) IS A WILD!
     */
    @Override
    public void handleAskColor() {
        String c = CT.printAskColor();
        switch (c) {
            case "red":
                doColorChoice("RED");
                break;
            case "green":
                doColorChoice("GREEN");
                break;
            case "yellow":
                doColorChoice("YELLOW");
                break;
            case "blue":
                doColorChoice("BLUE");
                break;
            default:
                CT.printCustomMessage("Color not recognized! Available are (case sensitive): yellow, red, green, blue");
                handleAskColor();
        }
    }

    public void handleAskChoiceSeven() {
        String p = CT.printAskChoiceSeven();
        doMakeChoiceSeven(p, "");
    }

    /**
     * This method is intended to display to the client when a color is changed.
     *
     * @param color of type String, representing the new color.
     */
    @Override
    public void handleBroadcastColorChange(String color) {
        CT.printColorChange(color);
    }

    /**
     * This is a free method: use it to your advantage to display specific information from the server as you would like.
     * This method will handle the displaying to the client.
     *
     * @param args of type String, representing multiple arguments of your choice.
     */
    @Override
    public void handleBroadcastGameMessage(String... args) {
        CT.printGameMessage(Arrays.toString(args));
    }

    /**
     * This method creates the appropriate tag and message corresponding to the make handshake (MH)..
     * The method initializes the handshake of the networking.client and the networking.server with the parameters provided.
     * Once the data packet is produced, the sender() method is invoked.
     *
     * @param playerName of type {@code String} representing the unique name of the player
     * @param playerType of type {@code String} representing computer_player ot human_player
     */
    @Override
    public void doMakeHandshake(String playerName, String playerType) {
        String hs = "MH|" + playerName + "|" + playerType;
        sendMessage(hs);
    }

    /**
     * This method creates the appropriate tag and message corresponding to the add computer player (ACP).
     * The method adds a computer player to the created game with the provided name and strategy
     * Once the data packet is produced, the sender() method is invoked.
     *
     * @param playerName of type {@code String} representing the name of the computer player
     * @param strategy   of type {@code String} representing the strategy for the computer player
     */
    @Override
    public void doAddComputerPlayer(String playerName, String strategy) {
        if (isAdmin) {
            String result = "ACP|" + playerName;
            sendMessage(result);
        }
    }

    /**
     * This method creates the appropriate tag and message corresponding to the start game (SG).
     * The method initializes the game
     * Once the data packet is produced, the sender() method is invoked.
     *
     * @param gameMode of type {@code String} representing the type/mode of the game
     */
    @Override
    public void doStartGame(String gameMode) {
        if (isAdmin) {
            String result = "SG|" + gameMode;
            sendMessage(result);
        } else {
            CT.printStartGame(gameMode);
        }
    }

    /**
     * This method creates the appropriate tag and message corresponding to a card being played (PC).
     * The method is being used when it is the networking.client's turn, and he needs to play a card. The chosen card is passed as a parameter to the method.
     * Once the data packet is produced, the sender() method is invoked.
     *
     * @param card of type {@code String} representing the card that the networking.client wants to play
     */
    @Override
    public void doPlayCard(String card) {
        String result = "PC|" + card;
        sendMessage(result);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a card being drawn (DC).
     * The method is being used when it is the networking.client's turn, and he wants to draw a card.
     * Once the data packet is produced, the sender() method is invoked.
     */
    @Override
    public void doDrawCard() {
        sendMessage("DC");
    }

    /**
     * This method creates the appropriate tag and message corresponding to a networking.client leaving the game (LG).
     * The method is being used when the networking.client wants to leave the game.
     * Once the data packet is produced, the sender() method is invoked.
     */
    @Override
    public void doLeaveGame() {
        this.flag = false;
        sendMessage("LG");
    }

    /**
     * This method creates the appropriate tag and message corresponding to a networking.client creating a lobby (CL).
     * The method is being used when the networking.client wants to create a lobby.
     * Once the data packet is produced, the sender() method is invoked.
     *
     * @param lobbyName of type String, representing the name of the lobby.
     */
    @Override
    public void doCreateLobby(String lobbyName) {
        sendMessage("CL|" + lobbyName);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a networking.client joining a lobby (JL).
     * The method is being used when the networking.client wants to join a lobby.
     * Once the data packet is produced, the sender() method is invoked.
     *
     * @param lobbyName of type String, representing the name of the lobby.
     */
    @Override
    public void doJoinLobby(String lobbyName) {
        sendMessage("JL|" + lobbyName);

    }

    /**
     * This method creates the appropriate tag and message corresponding to a networking.client sending a message in the chat (SM).
     * The method is being used when the networking.client wants to send a message in the chat.
     * Once the data packet is produced, the sender() method is invoked.
     *
     * @param message of type String, representing the message.
     */
    @Override
    public void doSendMessage(String message) {
        sendMessage("SM|" + message);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a networking.client saying UNO to avoid punishment(UNO).
     * The method is being used when the networking.client wants to say UNO.
     * Once the data packet is produced, the sender() method is invoked.
     */
    @Override
    public void doSayUno() {
        sendMessage("UNO");
    }

    /**
     * This method creates an appropriate tag and message corresponding to the choice made by a player whether to retain the Card that they picked.
     *
     * @param choice of type String, true if they want to play, false if they do not want to play the card.
     */
    @Override
    public void doRetainCard(String choice) {
        String msg = "RC|" + choice;
        sendMessage(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to the choice made by the player to the left of the dealer about what color to be played.
     * This happens under the event that the first card of play (pulled from the deck onto the playing space) is a WILD, meaning the player to the left of the dealer chooses the color.
     *
     * @param color of type String, representation of color.
     */
    @Override
    public void doColorChoice(String color) {
        String msg = "CC|" + color;
        sendMessage(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to the choice regarding the player to switch cards with in the Seven-0 game-mode.
     *
     * @param playerName of type String, representing the player with whom they want to change cards with.
     * @param card       of type String, representing the seven that was played and needs to be sent to the server.
     */
    @Override
    public void doMakeChoiceSeven(String playerName, String card) {
        String msg = "MC7|" + playerName;
        sendMessage(msg);
    }

    /**
     * The method `askStartInput` prompts the user to enter the name and playerType.
     * The user input is split based on the space character and processed to determine the player type.
     * If the last word in the input is "c", it is a computer player, otherwise it is a human player.
     * The processed input is then passed to the `doMakeHandshake` method along with the corresponding player type.
     */
    private void askStartInput() {
        String s = CT.printStartInput();
        String[] splitted = s.split(" ");
        StringBuilder sb = new StringBuilder(s);
        if (splitted[splitted.length - 1].equals("c")) {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            doMakeHandshake(sb.toString(), "computer_player");
        } else {
            doMakeHandshake(sb.toString(), "human_player");
        }
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        askStartInput();
        Scanner scan = new Scanner(System.in);

        InputListener il = new InputListener(this);
        Thread t = new Thread(il);
        t.start();

        while (!gameStarted) {
            try {
                receiveMessage();

            } catch (IOException e) {
                CT.printCustomMessage("An error occured during data transmission");
            }
        }

        il.stop();
        t.stop();

        while (flag) {
            try {
                receiveMessage();
            } catch (IOException e) {
                CT.printCustomMessage("An error occured during data transmission");
            }
        }
    }
}
