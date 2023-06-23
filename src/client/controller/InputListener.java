package client.controller;

import java.util.Scanner;

public class InputListener implements Runnable {
    private boolean flag = true;
    private final ClientHandler CH;

    public InputListener(ClientHandler ch) {
        this.CH = ch;
    }

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while (flag) {
            System.out.println(">> Enter command: ");
            String input = scanner.nextLine();
            evaluateInput(input);
        }
    }

    public void stop() {
        System.out.println("Enter the same command twice to confirm you are ready");
        this.setFlag(false);
    }

    /**
     * The method `evaluateInput` evaluates the input provided by the user.
     * The input is split based on the pipe character (|) and the length of the resulting array is used to determine the type of query.
     * If the length of the array is 1, the input is checked against the string "lol".
     * If it matches, the message "LOL" is sent.
     * If the length of the array is 2, the first element is used to determine the type of query.
     * It can be "start", "cl", or "jl".
     * If it is "start", the game is started by calling `doStartGame` with the second element as an argument.
     * If it is "cl", a lobby is created by calling `doCreateLobby` with the second element as an argument.
     * If it is "jl", a player joins a lobby by calling `doJoinLobby` with the second element as an argument.
     * If the type of query is not recognized, an error message is printed.
     * If the length of the array is not 1 or 2, an error message is printed.
     *
     * @param input The input provided by the user
     */
    public void evaluateInput(String input) {
        String[] spl = input.split("[|]");
        if (spl.length == 1) {
            if (input.equals("lol")) {
                CH.sendMessage("LOL");
            } else {
                System.out.println("Invalid command. Try again!");
            }
        } else if (spl.length == 2) {
            switch (spl[0]) {
                case "start":
                    CH.doStartGame(spl[1]);
                    break;
                case "cl":
                    CH.doCreateLobby(spl[1]);
                    break;
                case "jl":
                    CH.doJoinLobby(spl[1]);
                    break;
                case "acp":
                    CH.doAddComputerPlayer(spl[1], "");
                    break;
                default:
                    System.out.println("Query not recognized. Please try one of the listed methods: start, lol, cl|[lobbyname], jl|[lobbyname]");
            }
        } else {
            System.out.println("Invalid command. Try again!");
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }


}
