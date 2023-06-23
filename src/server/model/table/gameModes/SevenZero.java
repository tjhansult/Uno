package server.model.table.gameModes;

import server.model.card.Card;
import server.model.player.NetworkPlayer;
import server.model.player.factory.Player;
import server.model.table.Table;
import server.model.table.gameModes.factory.PlayingMode;

import java.util.ArrayList;

public class SevenZero extends PlayingMode {
    /**
     * Overridden method from the PlayingMode interface.
     * This method performs the action to be taken when a wild card is played by a player.
     * The actions taken depend on the value of the card played (Draw Two, Draw Four, Skip, Pick Color, or Change Direction).
     *
     * @param card       The card that has been played.
     * @param player     The player who has played the card.
     * @param nextPlayer The next player who will play.
     */
    @Override
    public void performWildCardAction(Card card, Player player, Player nextPlayer) {
        switch (card.getValue()) {
            case ZERO:
                for (Player p : player.getTable().getPlayers()) {
                    if (p instanceof NetworkPlayer) {
                        ((NetworkPlayer) p).getSh().doBroadcastGameMessage("Hands have been passed in the order of play.");
                    }
                }
                player.getTable().getUno().getTUI().printCustomMessage("Hands have been passed in the order of play");
                passDownHands(player.getTable());
                break;
            case SEVEN:
                player.chooseSwitchHands();
                break;
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
     * Passes down hands of players on a table.
     * If the table is not set to play in a clockwise direction, the hand of the first player is passed to the second player,
     * the hand of the second player is passed to the third player, and so on, until the hand of the last player is passed to the first player.
     * If the table is set to play in a clockwise direction, the hand of the last player is passed to the player before it,
     * the hand of the second to last player is passed to the player before it, and so on, until the hand of the first player is passed to the last player.
     *
     * @param table The table object representing the game state.
     */
    public void passDownHands(Table table) {
        if (!table.isClockWise()) {
            ArrayList<Card> temp = table.getPlayers().get(0).getHand();
            for (int i = 0; i < table.getPlayers().size() - 1; i++) {
                table.getPlayers().get(i).setHand(table.getPlayers().get(i + 1).getHand());
            }
            table.getPlayers().get(table.getPlayers().size() - 1).setHand(temp);
        } else {
            ArrayList<Card> temp = table.getPlayers().get(table.getPlayers().size() - 1).getHand();
            for (int i = table.getPlayers().size() - 1; i > 0; i--) {
                table.getPlayers().get(i).setHand(table.getPlayers().get(i - 1).getHand());
            }
            table.getPlayers().get(0).setHand(temp);
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
            if (indicatedColor.equals(cardToPlay.getColor())) {
                return true;
            }
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
            case ZERO:
                for (Player p : table.getPlayers()) {
                    if (p instanceof NetworkPlayer) {
                        ((NetworkPlayer) p).getSh().doBroadcastGameMessage("Hands have been passed in the order of play.");
                    }
                }
                table.getUno().getTUI().printCustomMessage("Hands have been passed in the order of play");
                passDownHands(table);
                break;
            case SEVEN:
                table.getCurrentPlayer().chooseSwitchHands();
                break;
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
