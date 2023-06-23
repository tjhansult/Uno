package server.model.player;

import server.model.card.Card;
import server.model.player.factory.Player;

import java.util.*;

public class ComputerPlayer extends Player {
    public ComputerPlayer(String nickname) {
        super(nickname);
    }

    private final ArrayList<Integer> POSSIBLE_MOVES = new ArrayList<>();

    /**
     * The translator method returns the next move that the computer player should make.
     * It first calls the method getValidMoves() which determines the valid moves that the computer player can make.
     * If there are no possible moves, the method returns "draw".
     * If the player has only two cards in hand, it returns the best move along with "uno".
     * Otherwise, it returns the best move.
     *
     * @return String representing the next move for the computer player.
     */
    public String translator() {
        getValidMoves();
        String s = "";
        if (POSSIBLE_MOVES.isEmpty()) {
            s += "draw";
        } else if (super.getHand().size() == 2) {
            s += determineBestMove();
            s += " uno";
        } else {
            s += determineBestMove();
        }
        POSSIBLE_MOVES.clear();
        return s;
    }

    /**
     * This method is used to select the color of the card when a Wild card is played.
     * The color is selected by counting the most frequent color in the computer's hand.
     */
    @Override
    public void pickColor() {
        Card.Color color = cardColors();
        switch (color) {
            case BLUE:
                super.getTable().getUno().getTUI().printCustomMessage("computer chose color blue");
                super.getTable().setIndicatedColor(Card.Color.BLUE);
                break;
            case RED:
                super.getTable().getUno().getTUI().printCustomMessage("computer chose color red");
                super.getTable().setIndicatedColor(Card.Color.RED);
                break;
            case GREEN:
                super.getTable().getUno().getTUI().printCustomMessage("computer chose color green");
                super.getTable().setIndicatedColor(Card.Color.GREEN);
                break;
            case YELLOW:
                super.getTable().getUno().getTUI().printCustomMessage("computer chose color yellow");
                super.getTable().setIndicatedColor(Card.Color.YELLOW);
                break;
            default:
                pickColor();
        }
    }

    /**
     * This method is used to choose another player to switch hands with.
     * The computer chooses the first player it finds that is not itself.
     */
    @Override
    public void chooseSwitchHands() {
        for (Player p : super.getTable().getPlayers()) {
            if (!p.getNickname().equals(super.getNickname())) {
                super.swapHands(p);
                break;
            }
        }
    }

    /**
     * This method is used to determine the color of the cards in the computer's hand.
     * It returns the color with the maximum number of cards in the hand.
     *
     * @return color The color with the most number of cards in the computer's hand.
     */
    public Card.Color cardColors() {
        HashMap<Card.Color, Integer> map = new HashMap<>();
        for (Card card : super.getHand()) {
            if (map.containsKey(card.getColor())) {
                int i = map.get(card.getColor()) + 1;
                map.put(card.getColor(), i);
            } else {
                map.put(card.getColor(), 1);
            }
        }

        map.remove(Card.Color.WILD);

        int maxOccurances = 0;
        Card.Color max = Card.Color.YELLOW;
        for (Map.Entry<Card.Color, Integer> entry : map.entrySet()) {
            if (entry.getValue() > maxOccurances) {
                maxOccurances = entry.getValue();
                max = entry.getKey();
            }
        }
        System.out.println(max);
        return max;
    }

    /**
     * This method is used to get the valid moves in the computer's hand.
     */
    public void getValidMoves() {
        for (int i = 0; i < super.getHand().size(); i++) {
            if (super.getTable().getPlayingMode().validMove(super.getHand().get(i), super.getTable())) {
                POSSIBLE_MOVES.add(i);
            }
        }
        System.out.println("VALID MOVES:" + POSSIBLE_MOVES);
    }

    /**
     * This method is used to determine the best move for the computer.
     * It returns the index of the best card to be played.
     *
     * @return index The index of the best card to be played.
     */
    public int determineBestMove() {
        int best = 0;
        for (int i = 0; i < POSSIBLE_MOVES.size(); i++) {
            if (getHand().get(POSSIBLE_MOVES.get(i)).getValue() != Card.Value.DRAW_FOUR && getHand().get(POSSIBLE_MOVES.get(i)).getValue() != Card.Value.PICK_COLOR) {
                best = i;
            }
        }
        return POSSIBLE_MOVES.get(best);
    }
}
