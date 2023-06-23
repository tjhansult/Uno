package server.model.player;

import server.model.card.Card;
import server.model.player.factory.Player;

import java.util.Scanner;

public class HumanPlayer extends Player {
    //--------------------------CONSTRUCTOR--------------------------

    public HumanPlayer(String nickname) {
        super(nickname);
    }

    //--------------------------METHODS--------------------------

    /**
     * pickColor method is a method that allows the player to pick a color.
     * It prints out the message ">> Please pick a color: " and receives user input
     * The input is then converted to a Card.Color and set as the indicated color of the table.
     * If the input is not one of the valid colors, the method will call itself until a valid color is picked.
     */
    @Override
    public void pickColor() {
        super.getTable().getUno().getTUI().printCustomMessage(">> Please pick a color: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        switch (input) {
            case "blue":
                super.getTable().setIndicatedColor(Card.Color.BLUE);
                break;
            case "red":
                super.getTable().setIndicatedColor(Card.Color.RED);
                break;
            case "green":
                super.getTable().setIndicatedColor(Card.Color.GREEN);
                break;
            case "yellow":
                super.getTable().setIndicatedColor(Card.Color.YELLOW);
                break;
            default:
                super.getTable().getUno().getTUI().printCustomMessage(">> Invalid input: please try to type one of valid colors in small letters");
                pickColor();
        }
    }

    /**
     * chooseSwitchHands method is a method that allows the player to switch hands with another player.
     * It prints out the message ">> Please pick a player to switch hands with." and receives user input
     * The input is then checked against the nicknames of all players in the table.
     * If the input is a valid player nickname, the hands of the current player and the selected player will be swapped.
     * If the input is not a valid player nickname, the method will call itself until a valid nickname is entered.
     */
    @Override
    public void chooseSwitchHands() {
        System.out.println(">> Please pick a player to switch hands with.");
        Scanner scan = new Scanner(System.in);
        String input = scan.next();
        Player p = null;
        boolean flag = false;
        for (Player player : super.getTable().getPlayers()) {
            if (input.equals(player.getNickname())) {
                p = player;
                flag = true;
            }
        }
        if (flag) {
            super.swapHands(p);
        } else {
            super.getTable().getUno().getTUI().printCustomMessage(">> Invalid input: please type an existing player nickname");
            chooseSwitchHands();
        }
    }

}
