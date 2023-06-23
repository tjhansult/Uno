package server.model.table.gameModes.contract;

import server.model.card.Card;
import server.model.player.factory.Player;
import server.model.table.Table;


public interface Mode {

    //-----------------------------INITIALIZERS---------------------------------------
    void performWildCardAction(Card card, Player player, Player nextPlayer);

    boolean validMove(Card cardToPlay, Table table);

    void adjustToFirstCard(Table table);


}
