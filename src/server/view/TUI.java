package server.view;

import server.model.card.Card;
import server.model.player.factory.Player;

import java.util.InputMismatchException;
import java.util.Scanner;

public class TUI {

    /**
     * Ask name input string.
     *
     * @param i the
     * @return the string
     */
    public String askNameInput(int i) {
        Scanner scanner1 = new Scanner(System.in);
        System.out.print(">> Enter name of Player " + (i + 1) + ": ");
        String name = scanner1.nextLine();
        return name;
    }

    /**
     * Ask mode input string.
     *
     * @return the string
     */
    public String askModeInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">> Please enter mode: ");
        String playingMode = scanner.next();
        return playingMode;
    }

    /**
     * Ask number of players input int.
     *
     * @return the int
     */
    public int askNumberOfPlayersInput() {
        Scanner scanner = new Scanner(System.in);
        int playersAmount;
        while (true) {
            try {
                System.out.print(">> Please enter number of players: ");
                playersAmount = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                scanner.nextLine();
            }
        }
        return playersAmount;
    }

    /**
     * Announce winner.
     *
     * @param winner the winner
     */
    public void announceWinner(String winner) {
        System.out.println(">> Player " + winner + " has ultimately won the game!");
        System.out.println(">> GAME OVER!!!");
    }

    /**
     * Ask for move string.
     *
     * @param playerName the player name
     * @return the string
     */
    public String askForMove(String playerName) {
        printMove(playerName);
        String msg = "";
        Scanner scan = new Scanner(System.in);
        msg = scan.nextLine();
        return msg;
    }

    /**
     * Find dealer printer.
     *
     * @param playerName the player name
     * @param card       the card
     */
    public void findDealerPrinter(String playerName, String card) {
        System.out.println("Player " + playerName + " drew " + card);
    }

    /**
     * Find dealer printer.
     *
     * @param playerName the player name
     */
    public void findDealerPrinter(String playerName) {
        System.out.println("\nPlayer " + playerName + " is a dealer!");
    }

    /**
     * Find dealer printer.
     */
    public void findDealerPrinter() {
        System.out.println("You drew only action cards!");
    }

    /**
     * Announce starter.
     *
     * @param playerName the player name
     */
    public void announceStarter(String playerName) {
        System.out.println(playerName + " starts.");
        System.out.println("\n\n");
    }

    /**
     * Ask yes no string.
     *
     * @param card the card
     * @return the string
     */
    public String askYesNo(String card) {
        System.out.println("You drew " + card + ". Would you like to play now? (yes/no)");
        Scanner s = new Scanner(System.in);
        String input = s.nextLine();
        return input;
    }

    /**
     * Print invalid input.
     */
    public void printInvalidInput() {
        System.out.println("Invalid input. Please try again! ");
    }

    /**
     * No uno.
     */
    public void noUno() {
        System.out.println("You didn't say UNO. You are punished with two cards");
    }

    /**
     * Print challenge.
     *
     * @param b the b
     */
    public void printChallenge(boolean b) {
        if (b) {
            System.out.println("Challenge unsuccessful! You're punished with additional 2 cards ");
        } else {
            System.out.println("Previous player was punished with drawing 4 cards, since he placed it illegally.");
        }
    }

    /**
     * Print card added.
     *
     * @param playerName the player name
     */
    public void printCardAdded(String playerName) {
        System.out.println("One card was added to " + playerName + "'s hand, it cannot be played.");

    }

    /**
     * Print scoreboard.
     *
     * @param pl the pl
     */
    public void printScoreboard(Player pl) {
        System.out.println("Player " + pl.getNickname() + " won this round.");
        for (Player p : pl.getTable().getScoreBoard().keySet()) {
            System.out.println(p.getNickname() + " : " + pl.getTable().getScoreBoard().get(p));
        }
    }

    /**
     * Print custom message.
     *
     * @param message the message
     */
    public void printCustomMessage(String message) {
        System.out.println(message);

    }

    /**
     * Print move.
     *
     * @param playerName the player name
     */
    public void printMove(String playerName) {
        System.out.print(">> " + playerName + " make your move: ");
    }

    /**
     * Print hand.
     *
     * @param player the player
     */
    public void printHand(Player player) {
        String s = "";
        int index = 0;
        for (Card card : player.getHand()) {
            s += index + " ";
            s += card.toString();
            s += "      |      ";
            index++;
        }
        System.out.println(s);
    }

    /**
     * Print current card.
     *
     * @param card the card
     */
    public void printCurrentCard(Card card) {
        System.out.println();
        System.out.println("========================================NEW TURN==================================================");
        System.out.println(card.toString());
    }

    /**
     * Print player skipped.
     *
     * @param playername the playername
     */
    public void printPlayerSkipped(String playername) {
        System.out.println(">> Player: " + playername + " was skipped! ");
    }

    /**
     * Print player color pick.
     *
     * @param playername the playername
     */
    public void printPlayerColorPick(String playername) {
        System.out.println(">> Player: " + playername + "picks a color!");
    }
}
