package server.model.table.gameModes.factory;

import server.model.table.gameModes.contract.Mode;


public abstract class PlayingMode implements Mode {
    private int forwardCount = 0;

    public int getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
    }

}
