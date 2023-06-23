package server.controller;

import server.model.card.Card;
import server.model.player.ComputerPlayer;
import server.model.player.NetworkPlayer;
import server.model.player.factory.Player;
import server.model.table.gameModes.Normal;
import server.model.table.gameModes.Progressive;
import server.model.table.gameModes.SevenZero;
import server.model.table.gameModes.factory.PlayingMode;
import server.model.Lobby;
import server.controller.contract.ServerProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;

public class ServerHandler implements ServerProtocol, Runnable{
    private final Socket CONNECTION;
    private final BufferedReader IN;
    private final PrintWriter OUT;
    private final Server SERVER;
    private final int LOBBY_CAPACITY = 10;
    private Player correspondingPlayer;
    private Lobby lobby;
    private boolean flag;

    public ServerHandler(Socket connection, Server server) throws IOException {
        this.CONNECTION = connection;
        IN = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        OUT = new PrintWriter(connection.getOutputStream());
        this.SERVER = server;
        this.flag = true;
    }

    private void seperateAndCall(String input) {
        String[] splitted = input.split("[|]");
        System.out.println(Arrays.toString(splitted));
        try {
        switch (splitted[0]) {
            case "MH":
                handleHandshake(splitted[1], splitted[2]);
                break;
            case "ACP":
                handleAddComputerPlayer(splitted[1], " ");
                break;
            case "SG":
                handleStartGame(splitted[1]);
                break;
            case "PC":
                handlePlayCard(splitted[1]);
                break;
            case "DC":
                handleDrawCard();
                break;
            case "LG":
                handleLeaveGame();
                break;
            case "CL":
                handleCreateLobby(splitted[1]);
                break;
            case "JL":
                handleJoinLobby(splitted[1]);
                break;
            case "CC":
                handleColorChoice(splitted[1]);
                break;
            case "RC":
                handleRetainCard(splitted[1]);
                break;
            case "UNO":
                handleSayUno();
                break;
            case "LOL":
                doBroadcastListOfLobbies("");
                break;
            case "MC7":
                handleMakeChoiceSeven(splitted[1], "");
                break;
            case "SM":
                handleSendMessage(splitted[1]);
                break;
            default:
                sendMessage(Errors.E001.getMessage()+Arrays.toString(splitted));
                System.out.println(input);
                break;
        }
        }catch (IndexOutOfBoundsException e) {
            sendMessage(Errors.E001.getMessage());
        }
    }



    public void sendMessage(String message) {
        System.out.println("SEND to "+ this.correspondingPlayer.getNickname() + ": " + message);
        OUT.println(message);
        OUT.flush();
        if(OUT.checkError()) {
            System.out.println("An error occured during transmission.");
        }
    }
    public void sendMessageToAll(String message) {
        for (ServerHandler s: SERVER.getHandlers()) {
            s.sendMessage(message);
        }
    }
    public void sendMessageToLobby(String message) {
        for (Player p: this.lobby.getPlayers()) {
            if (p instanceof NetworkPlayer) {
                ((NetworkPlayer)p).getSh().sendMessage(message);
            }
        }
    }
    public void receiveMessage()  {
        System.out.println("WAITING...");
        String messageIn = "";
        try {
            messageIn = IN.readLine();
            if (messageIn==null) {
                handleLeaveGame();
                doHandleClientDisconnected();
                return;
            }

        } catch (IOException e) {
            sendMessage(Errors.E001.getMessage());
        }
        System.out.println("RECEIVED: " + messageIn);
        seperateAndCall(messageIn);
    }

    /**
     * This method is called when a connection is first made, and the networking.client performs a "potentially valid" handshake.
     * <p>
     * The method assesses whether the networking.client has performed a valid handshake, and if this is the case, the method returns an
     * appropriate correspondence containing relevant information that the verified UnoClient needs to know in the form of a
     * welcome message (AH).
     * Once the data packet is produced, it is sent.
     * <p>
     * If the handshake is not valid, the method itself invokes the sendErrorCode() method to send the appropriate error code.
     *
     * @param playerName of type {@code String} representing the unique name of the player
     * @param playerType of type {@code String} representing computer_player ot human_player
     */
    @Override
    public void handleHandshake(String playerName, String playerType) {
        for (Lobby l: SERVER.getLobbies()) {
            for (Player p : l.getPlayers()) {
                if (p.getNickname().equals(playerName)) {
                    sendMessage("ERR|E002");
                    break;
                }
            }
        }

        if (playerType.equals("human_player")) {
            this.correspondingPlayer = new NetworkPlayer(playerName, this);
        }else {
            sendMessage("ERR|E003");
            System.out.println(Errors.E003.getMessage());
        }
        OUT.println("AH");
        OUT.flush();
        System.out.println(playerName + " connected successfully.");

        }


    /**
     * This method handles the creation of a computerPlayer as requested by the networking.client (admin) (ACP).
     * It relates heavily with the game-logic.
     *
     * @param playerName of type {@code String} representing the name of the computer player
     * @param strategy   of type {@code String} representing the strategy for the computer player
     */

    @Override
    public void handleAddComputerPlayer(String playerName, String strategy) {
        for (Player p: this.lobby.getPlayers()){
            if (p.getNickname().equals(playerName)){
                doSendErrorCode(Errors.E002);
                return;
            }
        }
        Player c = new ComputerPlayer(playerName);
        if (this.lobby!=null) {
            if (this.SERVER.getLobby(this.lobby.getName()).getPlayers().size() == LOBBY_CAPACITY){
                doSendErrorCode(Errors.E005);
                return;
            }
            this.lobby.addPlayer(c);
        } else {
            if (this.SERVER.getMainLobby().getPlayers().size() == LOBBY_CAPACITY){
                doSendErrorCode(Errors.E005);
                return;
            }
            SERVER.getMainLobby().addPlayer(c);
        }
    }

    /**
     * This method handles the command from the networking.client (admin) to start the game (SG).
     * It relates heavily with the game-logic.
     *
     * @param gameMode of type {@code String} representing the type/mode of the game
     */
    @Override
    public void handleStartGame(String gameMode) {
        PlayingMode playingMode;
        switch (gameMode) {
            case "normal":
                playingMode = new Normal();
                break;
            case "progressive":
                playingMode = new Progressive();
                break;
            case "sevenZero":
                playingMode = new SevenZero();
                break;
            default:
                doSendErrorCode(Errors.E006);
                return;
        }
        doGameStarted(gameMode);
        SERVER.getUno(correspondingPlayer).setup(this.SERVER.getPlayersInLobby(correspondingPlayer), playingMode);
        SERVER.getCurrentGames().add(SERVER.getUno(correspondingPlayer));
        Thread myUno = new Thread(SERVER.getUno(correspondingPlayer));
        myUno.start();
    }

    /**
     * This method handles the response from a networking.client regarding the card that they chose to play (PC).
     * It relates heavily with the game-logic.
     *
     * @param card of type {@code String} representing the card that the networking.client wants to play
     */
    @Override
    public void handlePlayCard(String card) {
        ((NetworkPlayer)correspondingPlayer).translate(card);
    }

    /**
     * This method handles the response from a networking.Client regarding the fact that they chose to draw a card (DC).
     * It relates heavily with the game-logic.
     */
    @Override
    public void handleDrawCard() {
        ((NetworkPlayer)correspondingPlayer).translate("draw");
        doBroadcastDrewCard(correspondingPlayer.getNickname());
    }

    /**
     * This method handles the command from the networking.client to leave the game (LG).
     */
    @Override
    public void handleLeaveGame() {
        if (this.SERVER.getLobbyIndex(correspondingPlayer)==-1) {
            System.out.println("The player has not yet joined a lobby.");
        }

        else if (this.lobby.isGameInProgress()) {
            for (Card c : this.correspondingPlayer.getHand()) {
                this.SERVER.getUno(correspondingPlayer).getTable().getDeck().getPlayingCards().add(c);
            }
            Collections.shuffle(this.SERVER.getUno(correspondingPlayer).getTable().getDeck().getPlayingCards());
            if (this.lobby.getPlayers().size() >= 2) {
                doBroadcastLeftGame(correspondingPlayer.getNickname());
                if (correspondingPlayer.getNickname().equals(this.lobby.getGame().getTable().getCurrentPlayer().getNickname())) {
                    ((NetworkPlayer) correspondingPlayer).translate("skip");
                }
                removePlayer(correspondingPlayer);
                this.SERVER.getHandlers().remove(this);
            }
        } else {
            this.SERVER.getLobby(correspondingPlayer).getPlayers().remove(correspondingPlayer);

        }
        doBroadcastLeftGame(correspondingPlayer.getNickname());
    }

    public void removePlayer(Player p) {
        UNO u = this.lobby.getGame();
        u.getTable().getPlayers().remove(p);
        u.getPlayers().remove(p);
        lobby.getPlayers().remove(p);
    }

    /**
     * This method handles the networking.client-side request for the creation of a lobby, and responds in an appropriate manner (CL).
     *
     * @param lobbyName of type String, representing the name of the lobby.
     */
    @Override
    public void handleCreateLobby(String lobbyName) {
        if (this.lobby != null){
            doSendErrorCode(Errors.E003);
            return;
        }
        for (Lobby lob: this.SERVER.getLobbies()){
            if (lobbyName.equals(lob.getName())){
                doSendErrorCode(Errors.E006);
                return;
            }
        }
        Lobby lobby = new Lobby(lobbyName);
        lobby.addPlayer(correspondingPlayer);
        this.SERVER.addLobby(lobby);
        this.lobby = lobby;
        doInformAdmin();
        doBroadcastCreatedLobby(lobbyName);
    }

    /**
     * This method handles the networking.client-side request for joining a lobby, and responds in an appropriate manner (JL).
     *
     * @param lobbyName of type String, representing the name of the lobby.
     */
    @Override
    public void handleJoinLobby(String lobbyName) {
        if (this.SERVER.getLobby(lobbyName) == null){
            doSendErrorCode(Errors.E003);
            return;
        }
        if (this.SERVER.getLobby(lobbyName).isGameInProgress()){
            doSendErrorCode(Errors.E004);
            return;
        }
        if (this.SERVER.getLobby(lobbyName).getPlayers().size() == LOBBY_CAPACITY){
            doSendErrorCode(Errors.E005);
            return;
        }
        if (this.lobby != null){
            doSendErrorCode(Errors.E003);
            return;
        }
        if (lobbyName.equals("main")&&this.SERVER.getMainLobby().getPlayers().isEmpty()) {
            doInformAdmin();
        }
        this.SERVER.getLobby(lobbyName).addPlayer(correspondingPlayer);
        this.lobby = this.SERVER.getLobby(lobbyName);
        doBroadcastPlayerJoinedLobby(correspondingPlayer.getNickname());
    }

    /**
     * The method processes the message and forwards it to all other clients within the chat (SM).
     *
     * @param message of type String, representing the message.
     */
    @Override
    public void handleSendMessage(String message) {
        sendMessageToLobby("BM|" + correspondingPlayer.getNickname() + ":" + message);
    }

    /**
     * The method processes the networking.client saying Uno, which then needs to be processed (UNO).
     */
    @Override
    public void handleSayUno() {
        if (correspondingPlayer instanceof NetworkPlayer) {
            ((NetworkPlayer)correspondingPlayer).setAddUno(true);
        }
        sendMessageToLobby("BUNO|"+correspondingPlayer.getNickname());
    }

    /**
     * This method is intended to handle the client's choice whether to play the drawn card or not.
     *
     * @param choice of type String, representing false if they do not want to play, true if they want to play it.
     */
    @Override
    public void handleRetainCard(String choice) {
        if (choice.equals("true")){
            if (correspondingPlayer instanceof NetworkPlayer){
                ((NetworkPlayer) correspondingPlayer).translate("proceed");
            }
        }
        else {
            if (correspondingPlayer instanceof NetworkPlayer) {
                ((NetworkPlayer) correspondingPlayer).translate("skip");
            }
        }
    }

    /**
     * This method is intended to handle the client's choice in changing the color (ONLY IN THE INSTANCE THAT THE FIRST CARD DRAWN FROM THE DECK TO THE PLAYING SPACE IS A WILD).
     *
     * @param color of type String, representing the color.
     */
    @Override
    public void handleColorChoice(String color) {
        NetworkPlayer p = (NetworkPlayer) correspondingPlayer;
        p.pickColor(color);
        doBroadcastColourChange(color);
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * This method is intended to handle the client's choice for the player they want to swap hands with.
     *
     * @param playerName of type String, representing the name of the player.
     * @param card       of type String, representing the SEVEN that was played.
     */
    @Override
    public void handleMakeChoiceSeven(String playerName, String card) {
        boolean flag = false;
        for (Player p: SERVER.getPlayersInLobby(correspondingPlayer)) {
            if (p.getNickname().equals(playerName)) {
                correspondingPlayer.swapHands(p);
                flag = true;
                break;
            }
        }
        if (!flag) {
            doSendErrorCode(Errors.E006);
            doAskChoiceSeven();
        }
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * This method creates the appropriate tag and message corresponding to the player being informed that they are the admin (IAD).
     * Once the data packet is produced, it is sent.
     */
    @Override
    public void doInformAdmin() {
        String msg = "IAD";
        this.sendMessage(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a new player joining the lobby (BPJ).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName Refers to the name of the player that joined the lobby.
     */
    @Override
    public void doBroadcastPlayerJoined(String playerName) {
        String msg = "BPJ|" + playerName;
        sendMessage(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to the game commencing (GST).
     * Once the data packet is produced, it is sent.
     *
     * @param gameMode of type GameMode, referring to the gameMode of this particular game (normal, progressive, seven_o, jump)in).
     */
    @Override
    public void doGameStarted(String gameMode) {
        this.SERVER.getLobby(correspondingPlayer).setGameInProgress(true);
        String msg = "GST|" + gameMode;
        sendMessageToLobby(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to the round beginning (RST).
     * Once the data packet is produced, it is sent.
     */
    @Override
    public void doRoundStarted() {
        String msg = "RST";
        sendMessage(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to the game information being sent to clients (BGI).
     * Once the data packet is produced, it is sent.
     *
     * @param topCard     of type String, representing the card.
     * @param playerHand  of type String, representing this particular network networking.client player's hand.
     * @param playersList of type {@code String} representing the list of players of the game sorted by the order of turn
     * @param isYourTurn  of type {@code String} indicates if it is the player’s turn
     */
    @Override
    public void doBroadcastGameInformation(String topCard, String playerHand, String playersList, String isYourTurn) {
        String msg = "BGI|";
        msg += topCard + "|";
        msg += playerHand + "|";
        msg += playersList + "|";
        msg += isYourTurn;
        sendMessage(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a card being played in the game (BCP).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type String, representing the name of the player who played the card.
     * @param playedCard of type {@code String} representing the card played
     */
    @Override
    public void doBroadcastCardPlayed(String playerName, String playedCard) {
        String result = "BCP|" + playerName + "|" + playedCard;
        sendMessage(result);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a player drawing a card in the game (BDC).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type String, representing the name of the player who played the card.
     */
    @Override
    public void doBroadcastDrewCard(String playerName) {
        String result = "BDC|" + playerName;
        sendMessage(result);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a player's turn being skipped in the game (BTS).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type String, representing the name of the player who played the card.
     */
    @Override
    public void doBroadcastTurnSkipped(String playerName) {
        String result = "BTS|" + playerName;
        sendMessage(result);
    }

    /**
     * This method creates the appropriate tag and message corresponding to the direction of the game reversing (BRS).
     * Once the data packet is produced, it is sent.
     *
     * @param direction of type String, representing the direction (clockwise, anti-clockwise).
     */
    @Override
    public void doBroadcastReverse(String direction) {
        String result = "BRS|" + direction;
        sendMessage(result);
    }

    /**
     * This method creates the appropriate tag and message to inform other players that a network player has left/forfeited (BLG).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type String, representing the name of the player who left.
     */
    @Override
    public void doBroadcastLeftGame(String playerName) {
        String result = "BLG|" + playerName;
        sendMessageToLobby(result);
    }

    /**
     * This method creates the appropriate tag and message corresponding to reminding a player that it is his turn(RP).
     * Once the data packet is produced, it is sent.
     *
     * @param timeLeft of type int, representing the seconds that the player has left to make a move.
     */
    @Override
    public void doRemindPlay(String timeLeft) {
        String result = "RP|" + timeLeft;
        sendMessage(result);
    }

    /**
     * This method creates the appropriate tag and message corresponding to when a round has ended (RE).
     * Once the data packet is produced, it is sent.
     *
     * @param winnerName of type String, representing the name of the player who won that round.
     */
    @Override
    public void doRoundEnded(String winnerName) {
        String result = "RE|" + winnerName;
        sendMessage(result);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a player winning the game (GE).
     * Once the data packet is produced, it is sent.
     *
     * @param winnerName of type String, representing the name of the player who won the game.
     */
    @Override
    public void doGameEnded(String winnerName) {
        this.SERVER.getLobby(correspondingPlayer).setGameInProgress(false);
        String result = "GE|" + winnerName;
        sendMessage(result);
        this.SERVER.getCurrentGames().remove(this.SERVER.getLobbies().get(this.SERVER.getLobbyIndex(correspondingPlayer)).getGame());
    }

    /**
     * This method creates the appropriate tag and message corresponding to an error code (E***).
     * Once the data packet is produced, it is sent.
     *
     * @param errorCode of type Errors, representing the error code.
     */
    @Override
    public void doSendErrorCode(Errors errorCode) {
        String result = "ERR|" + errorCode;
        sendMessage(result);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a player drawing a card that can be played directly (DPC).
     * Once the data packet is produced, it is sent.
     *
     * @param card of type String, representing the card that the player drew.
     */
    @Override
    public void doDrewPlayableCard(String card) {
        String msg = "DPC|" + card;
        sendMessage(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to a player playing a wild card (AC).
     * Once the data packet is produced, it is sent.
     */
    @Override
    public void doAskColour() {
        String msg = "AC";
        sendMessage(msg);
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method creates the appropriate tag and message corresponding to a player changing the colour that is current played (BCC).
     * Once the data packet is produced, it is sent.
     *
     * @param colour of type String, representing the colour that the player chose to switch to.
     */
    @Override
    public void doBroadcastColourChange(String colour) {
        String msg = "BCC|" + colour;
        sendMessageToLobby(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to the server sending messages relating to the game (BGM).
     * Once the data packet is produced, it is sent.
     *
     * @param message of type String, representing the message that needs to be sent.
     */
    @Override
    public void doBroadcastGameMessage(String... message) {
        String msg = "BGM|" + Arrays.toString(message);
        sendMessage(msg);
    }

    /**
     * This method exists so that the networking.server can implement a mechanism to handle an inactive player (RP can be used).
     */
    @Override
    public void doHandleInactivePlayer() {
        // the client automatically sends a leave game message to the server, if he is inactive for more than 45s and it is his turn.
    }

    /**
     * This method exists so that the networking.server can handle a networking.client that disconnected (by terminating the socket and adjusting the game).
     */
    @Override
    public void doHandleClientDisconnected() {
        try {
            this.CONNECTION.close();
            this.flag = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method creates the appropriate tag and message corresponding to listing the available lobbies (LOL).
     * The method lists the available lobbies, that clients can join.
     * Once the data packet is produced, it is sent.
     * @param lobbiesList
     */
    @Override
    public void doBroadcastListOfLobbies(String lobbiesList) {
        String msg = "LOL|";
        for (Lobby l: this.SERVER.getLobbies()) {
            msg += l.getName()+":"+l.getPlayers().size()+";";
        }
        sendMessage(msg);
    }
    /**
     * This method creates the appropriate tag and message corresponding to a user creating a lobby (BCL).
     * The method returns a message if the creation of the lobby was successful.
     * Once the data packet is produced, it is sent.
     *
     * @param lobbyName of type {@code String} representing the unique name of the lobby
     */
    @Override
    public void doBroadcastCreatedLobby(String lobbyName) {
        String msg = "BCL|"+lobbyName;
        sendMessageToAll(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to player joining a lobby (BJL).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type {@code String} representing the unique name of the winner of the game
     */
    @Override
    public void doBroadcastPlayerJoinedLobby(String playerName) {
        String msg = "BJL|" + playerName;
        sendMessageToLobby(msg);
    }

    /**
     * This method creates the appropriate tag and message corresponding to player sending a message (BM).
     * The method broadcasts a message sent my a networking.client to the other clients.
     * Once the data packet is produced, it is sent.
     *
     * @param message of type String, representing the chat message.
     */
    @Override
    public void doBroadcastMessage(String message) {
        sendMessage("BM|" + message);
    }

    public void doAskChoiceSeven() {
        String msg = "AC7";
        sendMessage(msg);
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
        System.out.println("Connected.");
        while(flag) {
            this.receiveMessage();
        }
}
}
