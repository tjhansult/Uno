package client.controller;

import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client implements Runnable {
    private int port = 5050;
    private String computer = "localhost";

    public static void main(String[] args) {
        Client client = new Client();
        Thread myThread = new Thread(client);
        myThread.start();
    }

    /**
     * This method asks the user for a desired connection (IP or localhost) and a desired port number.
     * It stores the inputted values in the `computer` and `port` fields of the class.
     * If the user inputs an invalid value for the port number, the method will prompt the user to try again.
     */
    public void askForConnection() {
        System.out.print(">> Please type desired connection (IP or localhost): ");
        Scanner scan1 = new Scanner(System.in);
        String nextLn1 = scan1.nextLine();
        this.computer = nextLn1;


        Scanner scanner = new Scanner(System.in);
        int p;
        while (true) {
            try {
                System.out.print(">> Please type desired PORT (compatible with server): ");
                p = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                scanner.nextLine();
            }
        }
        this.port = p;
    }

    @Override
    public void run() {
        Socket connection = null;

        try {
            askForConnection();
            System.out.println("Connecting to " + computer + " on port " + port);
            connection = new Socket(computer, port);
        } catch (IOException e) {
            try {
                System.out.println("Input invalid. You were connected to localhost on port 5050 on default");
                connection = new Socket("localhost", 5050);
            } catch (IOException ex) {
                System.out.println("Connecting failed");
            }
        }
        try {
            ClientHandler ch = new ClientHandler(connection);
            Thread cht = new Thread(ch);
            cht.start();

        } catch (IOException e) {
            System.out.println("Could not create clientHandler");
        }
    }
}
