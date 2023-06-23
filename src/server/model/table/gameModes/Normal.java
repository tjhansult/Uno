package server.model.table.gameModes;

import server.model.card.Card;
import server.model.player.NetworkPlayer;
import server.model.table.Table;
import server.model.table.gameModes.factory.PlayingMode;
import server.model.player.factory.Player;

public class Normal extends PlayingMode {

    /**
     * Perform wild card action based on the card played by the player
     *
     * @param card       the wild card played by the player
     * @param player     the current player playing the card
     * @param nextPlayer the next player in the game
     */
    @Override
    public void performWildCardAction(Card card, Player player, Player nextPlayer) {
        switch (card.getValue()) {
            case DRAW_TWO:
                nextPlayer.draw(2);
                player.getTable().skip();
                break;
            case DRAW_FOUR:
                if (player.getTable().isHasWinner()) {
                    break;
                }
                if (player instanceof NetworkPlayer) {
                    NetworkPlayer np = (NetworkPlayer) player;
                    np.getSh().doAskColour();
                } else {
                    player.pickColor();
                }
                nextPlayer.draw(4);
                player.getTable().drawFourEligibility();
                break;
            case SKIP:
                player.getTable().skip();
                for (Player p : player.getTable().getPlayers()) {
                    if (p instanceof NetworkPlayer) {
                        ((NetworkPlayer) p).getSh().doBroadcastTurnSkipped(nextPlayer.getNickname());
                    }
                }
                break;
            case PICK_COLOR:
                if (player.getTable().isHasWinner()) {
                    break;
                }
                if (player instanceof NetworkPlayer) {
                    NetworkPlayer np = (NetworkPlayer) player;
                    np.getSh().doAskColour();
                } else {
                    player.pickColor();
                }
                break;
            case CHANGE_DIRECTION:
                for (Player p : player.getTable().getPlayers()) {
                    if (p instanceof NetworkPlayer) {
                        ((NetworkPlayer) p).getSh().doBroadcastReverse(String.valueOf(player.getTable().isClockWise()));
                    }
                }
                player.getTable().reversePlayers();
                break;
        }
    }


    /**
     * Determines if the specified card is a valid move.
     *
     * @param cardToPlay the card to be played
     * @param table      the current state of the table
     * @return true if the card is a valid move, false otherwise
     */
    @Override
    public boolean validMove(Card cardToPlay, Table table) {
        Card.Color color = table.getCurrentCard().getColor();
        Card.Value value = table.getCurrentCard().getValue();
        Card.Color indicatedColor = table.getIndicatedColor();
        if (indicatedColor == null) {
            if (cardToPlay.getColor() == Card.Color.WILD && color == Card.Color.WILD) {
                return false;
            } else if (cardToPlay.getColor() == Card.Color.WILD) {
                return true;
            }
            if (color == cardToPlay.getColor()) {
                return true;
            }
            if (value == cardToPlay.getValue()) {
                return true;
            }
        } else {
            return indicatedColor.equals(cardToPlay.getColor());
        }
        return false;
    }

    /**
     * Performs actions for the first card in the game according to UNO rules.
     * Depending on the value of the card, it could draw two cards, choose a color, reverse direction, or skip a turn.
     */
    @Override
    public void adjustToFirstCard(Table table) {
        switch (table.getCurrentCard().getValue()) {
            case DRAW_TWO:
                table.getUno().getTUI().printCustomMessage("Unfortunately you've been punished with two cards at very beginning");
                table.getCurrentPlayer().draw(2);
                if (table.getCurrentPlayer() instanceof NetworkPlayer) {
                    ((NetworkPlayer) table.getCurrentPlayer()).getSh().doBroadcastGameMessage("Unfortunately you've been punished with two cards at very beginning");
                }
                break;
            case DRAW_FOUR:
                Card card = table.getCurrentCard();
                table.setCurrentCard(table.getDeck().getPlayingCards().get(0));
                table.getDeck().getPlayingCards().remove(0);
                table.getDeck().getPlayingCards().add(card);
                table.getDeck().getUsedCards().add(table.getCurrentCard());
                if (table.getCurrentCard().getColor() == Card.Color.WILD) {
                    adjustToFirstCard(table);
                }
                break;
            case SKIP:
                table.getUno().getTUI().printPlayerSkipped(table.getCurrentPlayer().getNickname());
                if (table.getCurrentPlayer() instanceof NetworkPlayer) {
                    ((NetworkPlayer) table.getCurrentPlayer()).getSh().doBroadcastGameMessage(">> You have been skipped.");
                }
                table.skip();
                break;
            case PICK_COLOR:
                table.getUno().getTUI().printCustomMessage(">> WILD PICK was the first card so player has to choose the color");
                table.getUno().getTUI().printPlayerColorPick(table.getCurrentPlayer().getNickname());
                if (table.getCurrentPlayer() instanceof NetworkPlayer) {
                    ((NetworkPlayer) table.getCurrentPlayer()).getSh().doAskColour();
                } else {
                    table.getCurrentPlayer().pickColor();
                }
                break;
            case CHANGE_DIRECTION:
                table.getUno().getTUI().printCustomMessage("First card was 'change direction' therefore dealer starts and direction is changed");
                table.reversePlayers();
                table.skip();
                break;
        }

    }


}
