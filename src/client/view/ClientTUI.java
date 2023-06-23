package client.view;

import client.controller.ClientHandler;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ClientTUI {
    private final ClientHandler CH;

    /**
     * Instantiates a new Client tui.
     *
     * @param ch the ch
     */
    public ClientTUI(ClientHandler ch) {
        this.CH = ch;
    }

    /**
     * Print player joined.
     *
     * @param playerName the player name
     */
    public void printPlayerJoined (String playerName){
        System.out.println(playerName + " connected to the networking.server");
    }

    /**
     * Print game started.
     *
     * @param gameMode the game mode
     */
    public void printGameStarted (String gameMode){
        System.out.println("Game has started in mode: " + gameMode);
    }

    /**
     * Print card played.
     *
     * @param playerName the player name
     * @param playedCard the played card
     */
    public void printCardPlayed (String playerName, String playedCard){
        System.out.println(playerName + " played " + playedCard);
    }

    /**
     * Print drew card.
     *
     * @param playerName the player name
     */
    public void printDrewCard(String playerName){
        System.out.println(playerName + " drew a card!");
    }

    /**
     * Print turn skipped.
     *
     * @param playerName the player name
     */
    public void printTurnSkipped(String playerName){
        System.out.println(playerName + "'s turn was skipped!");
    }

    /**
     * Print left game.
     *
     * @param playerName the player name
     */
    public void printLeftGame(String playerName){
        System.out.println(playerName + " left the game!");
    }

    /**
     * Print round ended.
     *
     * @param playerName the player name
     */
    public void printRoundEnded(String playerName){
        System.out.println("The round has ended! " + playerName + " was a winner!");
    }

    /**
     * Print list of lobbies.
     *
     * @param lobbiesList the lobbies list
     */
    public void printListOfLobbies(String lobbiesList){
        String[] spl = lobbiesList.split(";");
        for (String s : spl) {
            String[] l = s.split(":");
            System.out.println(l[0] + ": " + l[1] + " players waiting");
        }
    }

    /**
     * Print game ended.
     *
     * @param playerName the player name
     */
    public void printGameEnded(String playerName){
        System.out.println("The game has ended! " + playerName + " was an ultimate winner!");
    }

    /**
     * Print create lobby.
     *
     * @param lobbyName the lobby name
     */
    public void printCreateLobby(String lobbyName){
        System.out.println("Lobby " + lobbyName + " has been created.");
    }

    /**
     * Print player joined lobby.
     *
     * @param playerName the player name
     */
    public void printPlayerJoinedLobby(String playerName){
        System.out.println(playerName + " has joined the lobby.");
    }

    /**
     * Print message.
     *
     * @param message the message
     */
    public void printMessage(String message){
        String[] msg = message.split(":");
        System.out.println(msg[0] + ": " + msg[1]);
    }

    /**
     * Print color change.
     *
     * @param color the color
     */
    public void printColorChange(String color){
        System.out.println("The color was changed to " + color);
    }

    /**
     * Print game message.
     *
     * @param msg the msg
     */
    public void printGameMessage(String msg){
        System.out.println(msg);
    }

    /**
     * Print make move string.
     *
     * @return the string
     */
    public String printMakeMove(){
        System.out.println(">> Make your move: ");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CH.doLeaveGame();
            }
        };
        timer.schedule(task, 45000);
        Scanner scan = new Scanner(System.in);
        String ind = scan.nextLine();
        timer.cancel();
        return ind;
    }

    /**
     * Print start input string.
     *
     * @return the string
     */
    public String printStartInput(){
        System.out.print(">> Please enter name: ");
        Scanner scan = new Scanner(System.in);
        String nextLn = scan.nextLine();
        return nextLn;
    }

    /**
     * Print ask choice seven string.
     *
     * @return the string
     */
    public String printAskChoiceSeven(){
        Scanner scan = new Scanner(System.in);
        System.out.println(">> Who would you like to swap cards with? ");
        String p = scan.next();
        return p;
    }

    /**
     * Print ask color string.
     *
     * @return the string
     */
    public String printAskColor(){
        Scanner scan = new Scanner(System.in);
        System.out.print(">> Please pick a color ");
        String c = scan.nextLine();
        return c;
    }

    /**
     * Print drew playable card string.
     *
     * @param playableCard the playable card
     * @return the string
     */
    public String printDrewPlayableCard(String playableCard){
        System.out.println("You drew " + playableCard + ". Do you want to play this card now?");
        Scanner scan = new Scanner(System.in);
        String a = scan.next();
        return a;
    }

    /**
     * Print your turn.
     *
     * @param topCard     the top card
     * @param playerHand  the player hand
     * @param playersList the players list
     */
    public void printYourTurn(String topCard, String playerHand, String playersList){
        System.out.println("========================================YOUR TURN=================================================");
        System.out.println("| " + topCard + " |");
        String[] splittedHand = playerHand.split(";");
        for (int i = 0; i < splittedHand.length; i++) {
            System.out.print(i + "| " + splittedHand[i] + " |        ");
        }
        System.out.println();
        String[] splittedPlayers = playersList.split(";");
        for (String splittedPlayer : splittedPlayers) {
            String[] split = splittedPlayer.split(":");
            System.out.println(split[0] + " has " + split[1] + " cards and " + split[2] + " points!");
        }
    }

    /**
     * Print new turn.
     *
     * @param topCard     the top card
     * @param playerHand  the player hand
     * @param playersList the players list
     */
    public void printNewTurn(String topCard, String playerHand, String playersList){
        System.out.println("========================================NEW TURN==================================================");
        System.out.println("| " + topCard + " |");
        String[] splittedHand = playerHand.split(";");
        for (int i = 0; i < splittedHand.length; i++) {
            System.out.print(i + "| " + splittedHand[i] + " |        ");
        }
        System.out.println();
        String[] splittedPlayers = playersList.split(";");
        for (String splittedPlayer : splittedPlayers) {
            String[] split = splittedPlayer.split(":");
            System.out.println(split[0] + " has " + split[1] + " cards and " + split[2] + " points!");
        }
        System.out.println("Wait for your turn!\n\n");
    }

    /**
     * Print start game.
     *
     * @param gameMode the game mode
     */
    public void printStartGame(String gameMode){
        System.out.println("Game is being setup in mode " + gameMode);
    }

    /**
     * Print custom message.
     *
     * @param msg the msg
     */
    public void printCustomMessage(String msg){
        System.out.println(msg);
    }
}
