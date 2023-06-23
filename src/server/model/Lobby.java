package server.model;

import server.controller.UNO;
import server.model.player.factory.Player;

import java.util.ArrayList;

public class Lobby {
    public Lobby(String name) {
        this.PLAYERS = new ArrayList<>();
        this.NAME = name;
        this.GAME = new UNO();
        this.gameInProgress = false;
    }

    private final ArrayList<Player> PLAYERS;
    private final String NAME;
    private final UNO GAME;
    private boolean gameInProgress;

    public void addPlayer(Player p) {
        this.PLAYERS.add(p);
    }

    public ArrayList<Player> getPlayers() {
        return this.PLAYERS;
    }

    public String getName() {
        return NAME;
    }

    public UNO getGame() {
        return GAME;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }
}
