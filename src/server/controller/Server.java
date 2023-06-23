package server.controller;

import server.model.player.factory.Player;
import server.model.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {
    public Server() {
        LOBBIES = new ArrayList<>();
        Lobby l = new Lobby("main");
        LOBBIES.add(l);
        CURRENT_GAMES = new ArrayList<>();
    }

    static final int DEFAULT_PORT = 5050;
    private final static ArrayList<ServerHandler> HANDLERS = new ArrayList<>();
    private final ArrayList<Lobby> LOBBIES;
    private final ArrayList<UNO> CURRENT_GAMES;

    public static void main(String[] args) {
        Server server = new Server();
        Thread myServer = new Thread(server);
        myServer.start();
    }

    public Lobby getLobby(Player p) {
        return this.LOBBIES.get(getLobbyIndex(p));
    }

    public int getLobbyIndex(Player p) {
        for (int i = 0; i < LOBBIES.size(); i++) {
            for (Player player : LOBBIES.get(i).getPlayers()) {
                if (p.getNickname().equals(player.getNickname())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ArrayList<Player> getPlayersInLobby(Player p) {
        return this.LOBBIES.get(getLobbyIndex(p)).getPlayers();
    }

    public Lobby getMainLobby() {
        return this.LOBBIES.get(0);
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
        int port = DEFAULT_PORT;
        ServerSocket listen;
        Socket connection;

        try {
            while (true) {
                listen = new ServerSocket(port);
                System.out.println("Listening on port " + port);
                connection = listen.accept();
                listen.close();
                ServerHandler sh = new ServerHandler(connection, this);
                HANDLERS.add(sh);
                Thread sHThread = new Thread(sh, "Gracjan");
                sHThread.start();
            }
        } catch (IOException e) {
            System.out.println("Connection couldn't be established.");
        }

    }

    //-----------------------------GETTERS & SETTERS-----------------------------
    public void addLobby(Lobby lobby) {
        this.LOBBIES.add(lobby);
    }

    public Lobby getLobby(String lobbyName) {
        for (Lobby l : LOBBIES) {
            if (l.getName().equals(lobbyName)) {
                return l;
            }
        }
        return null;
    }

    public ArrayList<Lobby> getLobbies() {
        return LOBBIES;
    }

    public ArrayList<UNO> getCurrentGames() {
        return CURRENT_GAMES;
    }

    public UNO getUno(Player p) {
        Lobby l = this.LOBBIES.get(getLobbyIndex(p));
        return l.getGame();
    }

    public ArrayList<ServerHandler> getHandlers() {
        return HANDLERS;
    }

}

