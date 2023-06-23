package server.model.table.gameModes;

import server.model.card.Card;
import server.model.player.NetworkPlayer;
import server.model.player.factory.Player;
import server.model.table.Table;
import server.model.table.gameModes.factory.PlayingMode;

public class Progressive extends PlayingMode {
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
            case DRAW_TWO:
                if (hasDrawTwo(nextPlayer)) {
                    if (nextPlayer instanceof NetworkPlayer) {
                        NetworkPlayer np = (NetworkPlayer) nextPlayer;
                        np.getSh().doBroadcastGameMessage("You can forward drawing two cards, by placing your draw two card.");
                    } else {
                        player.getTable().getUno().getTUI().printCustomMessage("You can forward drawing two cards, by placing your draw two card.");
                        System.out.println("You can forward drawing two cards, by placing your draw two card.");
                    }
                    super.setForwardCount(super.getForwardCount() + 2);
                } else {
                    nextPlayer.draw(super.getForwardCount() + 2);
                    super.setForwardCount(0);
                    player.getTable().skip();
                }
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
     * Determines if a player has a Draw Two card in their hand.
     *
     * @param p - The player to check for a Draw Two card.
     * @return boolean - True if the player has a Draw Two card, False otherwise.
     */
    private boolean hasDrawTwo(Player p) {
        for (Card c : p.getHand()) {
            if (c.getValue() == Card.Value.DRAW_TWO) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a move is valid according to the current card on the table and the indicated color.
     *
     * @param cardToPlay - The card the player is trying to play.
     * @param table      - The current game table.
     * @return boolean - True if the move is valid, False otherwise.
     */
    @Override
    public boolean validMove(Card cardToPlay, Table table) {
        Card.Color color = table.getCurrentCard().getColor();
        Card.Value value = table.getCurrentCard().getValue();
        Card.Color indicatedColor = table.getIndicatedColor();

        if (indicatedColor == null && super.getForwardCount() == 0) {
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
        } else if (indicatedColor != null && super.getForwardCount() == 0) {
            if (indicatedColor.equals(cardToPlay.getColor())) {
                return true;
            }
        } else {
            if (cardToPlay.getValue() == Card.Value.DRAW_TWO) {
                return true;
            }
        }
        return false;
    }

    /**
     Performs actions for the first card in the game according to UNO rules.
     Depending on the value of the card, it could draw two cards, choose a color, reverse direction, or skip a turn.
     */
    @Override
    public void adjustToFirstCard(Table table) {
        switch (table.getCurrentCard().getValue()) {
            case DRAW_TWO:
                if (hasDrawTwo(table.getCurrentPlayer())) {
                    if (table.getCurrentPlayer() instanceof NetworkPlayer) {
                        NetworkPlayer np = (NetworkPlayer) table.getCurrentPlayer();
                        np.getSh().doBroadcastGameMessage("You can forward drawing two cards, by placing your draw two card.");
                    } else {
                        table.getCurrentPlayer().getTable().getUno().getTUI().printCustomMessage("You can forward drawing two cards, by placing your draw two card.");
                        System.out.println("You can forward drawing two cards, by placing your draw two card.");
                    }
                    super.setForwardCount(super.getForwardCount() + 2);
                } else {
                    table.getCurrentPlayer().draw(super.getForwardCount() + 2);
                    super.setForwardCount(0);
                    table.getCurrentPlayer().getTable().skip();
                }
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
