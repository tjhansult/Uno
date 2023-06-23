package server.model.player.contract;

import server.model.card.Card;

public interface PlayerActions {

    //-------------------------------------------INITIALIZERS----------------------------------------------
    void playCard(Card card);

    void placeCard(Card card);

    void draw(int amount);

    boolean checkDrawPossibility(int amount);

    void pickColor();

    boolean isWinner();

    void chooseSwitchHands();
}
