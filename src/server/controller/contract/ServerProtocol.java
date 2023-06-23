package server.controller.contract;

/**
 * This interface includes all relevant protocol codes, and methods that the networking.server of Uno Game will need to use in order to ensure
 * secure integration with the network protocol that was designed.
 * <p>
 * Relevant JavaDocs are added.
 */
public interface ServerProtocol {

    char DELIMITER = '|';

    /**
     * This enum class contains all the relevant protocol error codes and associated messages that will be used.
     * They are placed on the networking.server-side for better access management, but the Client class can make use of them with the public access modifier.
     */
    enum Errors {
        E001("Protocol violated"),
        E002("The player name [PlayerName] has been taken. Please enter another name."),
        E003("Parameters/arguments are missing or violate protocol. Please re-enter."),
        E004("The desired game is already in action. Please choose another game."),
        E005("The desired game is at maximum capacity. Please choose another game."),
        E006("Invalid input. Please type a valid input."),
        E007("Player [Name] has disconnected"),
        E008("Invalid Username. Please enter a valid username according to specifications."),
        E009("The message could not be sent."),
        E010("Please select a valid card for the game."),
        E011("You have requested more computer players than there are spaces available.");

        private final String message;

        Errors(String errorMessage) {
            this.message = errorMessage;
        }

        public String getMessage() {
            return this.message;
        }
    }

    /**
     * The following list contains the networking.server commands (commands that are sent from the networking.server to the networking.client).
     * <p>
     * The access modifier is public because the networking.client will need access to these in order to determine what appropriate course of action needs to be
     * taken with respect to each particular command sent by the networking.server. Further documentation for each command can be found in the protocol description table.
     */
    enum ServerCommands {
        AH("Accept handshake"),
        IAD("Inform Admin"),
        BPJ("Broadcast player joined"),
        GST("Broadcast game started"),
        RST("Broadcast round started"),
        BGI("Broadcast game information"),
        BCP("Broadcast card played"),
        BDC("Broadcast drew card"),
        BTS("Broadcast turn skipped"),
        BRS("Broadcast reverse"),
        BLG("Broadcast left game"),
        RP("Remind play"),
        RE("Round ended"),
        GE("Game ended"),
        ERR("Send error code"),
        DPC("Player drew a playable card"),
        AC("Ask for colour"),
        BCC("Broadcast colour change"),
        BGM("Broadcast game message"),
        /* This command is one that can be used by both, but to avoid duplication, it was placed inside the server protocol. */
        LOL("List of Lobbies"),
        BCL("Broadcast Created Lobby"),
        BJL("Player joined lobby"),
        BM("Broadcast Chat Message"),
        BUNO("Broadcast Say UNO");

        private final String action;

        ServerCommands(String action) {
            this.action = action;
        }

        public String getAction() {
            return this.action;
        }
    }


    /* Handlers */

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
    void handleHandshake(String playerName, String playerType);

    /**
     * This method handles the creation of a computerPlayer as requested by the networking.client (admin) (ACP).
     * It relates heavily with the game-logic.
     *
     * @param playerName of type {@code String} representing the name of the computer player
     * @param strategy   of type {@code String} representing the strategy for the computer player
     */
    void handleAddComputerPlayer(String playerName, String strategy);

    /**
     * This method handles the command from the networking.client (admin) to start the game (SG).
     * It relates heavily with the game-logic.
     *
     * @param gameMode of type {@code String} representing the type/mode of the game
     */
    void handleStartGame(String gameMode);

    /**
     * This method handles the response from a networking.client regarding the card that they chose to play (PC).
     * It relates heavily with the game-logic.
     *
     * @param card of type {@code String} representing the card that the networking.client wants to play
     */
    void handlePlayCard(String card);

    /**
     * This method handles the response from a networking.client regarding the fact that they chose to draw a card (DC).
     * It relates heavily with the game-logic.
     */
    void handleDrawCard();

    /**
     * This method handles the command from the networking.client to leave the game (LG).
     */
    void handleLeaveGame();


    /* Handlers for additional features */

    /**
     * This method handles the networking.client-side request for the creation of a lobby, and responds in an appropriate manner (CL).
     *
     * @param lobbyName of type String, representing the name of the lobby.
     */
    void handleCreateLobby(String lobbyName);

    /**
     * This method handles the networking.client-side request for joining a lobby, and responds in an appropriate manner (JL).
     *
     * @param lobbyName of type String, representing the name of the lobby.
     */
    void handleJoinLobby(String lobbyName);

    /**
     * The method processes the message and forwards it to all other clients within the chat (SM).
     *
     * @param message of type String, representing the message.
     */
    void handleSendMessage(String message);

    /**
     * The method processes the networking.client saying Uno, which then needs to be processed (UNO).
     */
    void handleSayUno();

    /**
     * This method is intended to handle the client's choice whether to play the picked card or not.
     *
     * @param choice of type String, representing false if they do not want to play, true if they want to play it.
     */
    void handleRetainCard(String choice);

    /**
     * This method is intended to handle the client's choice in changing the color (ONLY IN THE INSTANCE THAT THE FIRST CARD DRAWN FROM THE DECK TO THE PLAYING SPACE IS A WILD).
     *
     * @param color of type String, representing the color.
     */
    void handleColorChoice(String color);

    /**
     * This method is intended to handle the client's choice for the player they want to swap hands with.
     *
     * @param playerName of type String, representing the name of the player.
     * @param card       of type String, representing the SEVEN that was played.
     */
    void handleMakeChoiceSeven(String playerName, String card);


    /* Sending Methods */

    /**
     * This method creates the appropriate tag and message corresponding to the player being informed that they are the admin (IAD).
     * Once the data packet is produced, it is sent.
     */
    void doInformAdmin();

    /**
     * This method creates the appropriate tag and message corresponding to a new player joining the lobby (BPJ).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName Refers to the name of the player that joined the lobby.
     */
    void doBroadcastPlayerJoined(String playerName);

    /**
     * This method creates the appropriate tag and message corresponding to the game commencing (GST).
     * Once the data packet is produced, it is sent.
     *
     * @param gameMode of type GameMode, referring to the gameMode of this particular game (normal, progressive, seven_o, jump)in).
     */
    void doGameStarted(String gameMode);

    /**
     * This method creates the appropriate tag and message corresponding to the round beginning (RST).
     * Once the data packet is produced, it is sent.
     */
    void doRoundStarted();

    /**
     * This method creates the appropriate tag and message corresponding to the game information being sent to clients (BGI).
     * Once the data packet is produced, it is sent.
     *
     * @param topCard     of type String, representing the card.
     * @param playerHand  of type String, representing this particular network networking.client player's hand.
     * @param playersList of type {@code String} representing the list of players of the game sorted by the order of turn
     * @param isYourTurn  of type {@code String} indicates if it is the player’s turn
     */
    void doBroadcastGameInformation(String topCard, String playerHand, String playersList, String isYourTurn);

    /**
     * This method creates the appropriate tag and message corresponding to a card being played in the game (BCP).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type String, representing the name of the player who played the card.
     * @param playedCard of type {@code String} representing the card played
     */
    void doBroadcastCardPlayed(String playerName, String playedCard);

    /**
     * This method creates the appropriate tag and message corresponding to a player drawing a card in the game (BDC).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type String, representing the name of the player who played the card.
     */
    void doBroadcastDrewCard(String playerName);

    /**
     * This method creates the appropriate tag and message corresponding to a player's turn being skipped in the game (BTS).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type String, representing the name of the player who played the card.
     */
    void doBroadcastTurnSkipped(String playerName);

    /**
     * This method creates the appropriate tag and message corresponding to the direction of the game reversing (BRS).
     * Once the data packet is produced, it is sent.
     *
     * @param direction of type String, representing the direction (clockwise, anti-clockwise).
     */
    void doBroadcastReverse(String direction);

    /**
     * This method creates the appropriate tag and message to inform other players that a network player has left/forfeited (BLG).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type String, representing the name of the player who left.
     */
    void doBroadcastLeftGame(String playerName);

    /**
     * This method creates the appropriate tag and message corresponding to reminding a player that it is his turn(RP).
     * Once the data packet is produced, it is sent.
     *
     * @param timeLeft of type int, representing the seconds that the player has left to make a move.
     */
    void doRemindPlay(String timeLeft);

    /**
     * This method creates the appropriate tag and message corresponding to when a round has ended (RE).
     * Once the data packet is produced, it is sent.
     *
     * @param winnerName of type String, representing the name of the player who won that round.
     */
    void doRoundEnded(String winnerName);

    /**
     * This method creates the appropriate tag and message corresponding to a player winning the game (GE).
     * Once the data packet is produced, it is sent.
     *
     * @param winnerName of type String, representing the name of the player who won the game.
     */
    void doGameEnded(String winnerName);

    /**
     * This method creates the appropriate tag and message corresponding to an error code (E***).
     * Once the data packet is produced, it is sent.
     *
     * @param errorCode of type Errors, representing the error code.
     */
    void doSendErrorCode(Errors errorCode);

    /**
     * This method creates the appropriate tag and message corresponding to a player drawing a card that can be played directly (DPC).
     * Once the data packet is produced, it is sent.
     *
     * @param card of type String, representing the card that the player drew.
     */
    void doDrewPlayableCard(String card);

    /**
     * This method creates the appropriate tag and message corresponding to a player playing a wild card (AC).
     * Once the data packet is produced, it is sent.
     */
    void doAskColour();

    /**
     * This method creates the appropriate tag and message corresponding to a player changing the colour that is current played (BCC).
     * Once the data packet is produced, it is sent.
     *
     * @param colour of type String, representing the colour that the player chose to switch to.
     */
    void doBroadcastColourChange(String colour);

    /**
     * This method creates the appropriate tag and message corresponding to the server sending messages relating to the game (BGM).
     * Once the data packet is produced, it is sent.
     *
     * @param message of type String, representing the message that needs to be sent.
     */
    void doBroadcastGameMessage(String... message);


    /* Network-Related Handling */

    /**
     * This method exists so that the networking.server can implement a mechanism to handle an inactive player (RP can be used).
     */
    void doHandleInactivePlayer();

    /**
     * This method exists so that the networking.server can handle a networking.client that disconnected (by terminating the socket and adjusting the game).
     */
    void doHandleClientDisconnected();


    /* Additional features */

    /**
     * This method creates the appropriate tag and message corresponding to listing the available lobbies (LOL).
     * The method lists the available lobbies, that clients can join.
     * Once the data packet is produced, it is sent.
     */
    void doBroadcastListOfLobbies(String lobbiesList);

    /**
     * This method creates the appropriate tag and message corresponding to a user creating a lobby (BCL).
     * The method returns a message if the creation of the lobby was successful.
     * Once the data packet is produced, it is sent.
     *
     * @param lobbyName of type {@code String} representing the unique name of the lobby
     */
    void doBroadcastCreatedLobby(String lobbyName);

    /**
     * This method creates the appropriate tag and message corresponding to player joining a lobby (BJL).
     * Once the data packet is produced, it is sent.
     *
     * @param playerName of type {@code String} representing the unique name of the winner of the game
     */
    void doBroadcastPlayerJoinedLobby(String playerName);

    /**
     * This method creates the appropriate tag and message corresponding to player sending a message (BM).
     * The method broadcasts a message sent my a networking.client to the other clients.
     * Once the data packet is produced, it is sent.
     *
     * @param message of type String, representing the chat message.
     */
    void doBroadcastMessage(String message);

}
