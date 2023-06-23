package server.model.card;

public class Card {
    private final Color COLOR;
    private final Value VALUE;

    //--------------------------ENUMS--------------------------
    public enum Color {
        BLUE, GREEN, YELLOW, RED, WILD
    }

    public enum Value {
        DRAW_TWO, SKIP, CHANGE_DIRECTION, DRAW_FOUR, PICK_COLOR, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

        @Override
        public String toString() {
            String result;
            switch (this) {
                case DRAW_TWO:
                    result = "DRAW_2";
                    break;
                case SKIP:
                    result = "SKIP";
                    break;
                case CHANGE_DIRECTION:
                    result = "REVERSE";
                    break;
                case DRAW_FOUR:
                    result = "DRAW_FOUR";
                    break;
                case PICK_COLOR:
                    result = "PICK";
                    break;
                case ZERO:
                    result = "0";
                    break;
                case ONE:
                    result = "1";
                    break;
                case TWO:
                    result = "2";
                    break;
                case THREE:
                    result = "3";
                    break;
                case FOUR:
                    result = "4";
                    break;
                case FIVE:
                    result = "5";
                    break;
                case SIX:
                    result = "6";
                    break;
                case SEVEN:
                    result = "7";
                    break;
                case EIGHT:
                    result = "8";
                    break;
                case NINE:
                    result = "9";
                    break;
                default:
                    result = "NULL";
                    break;
            }
            return result;
        }
    }

    //--------------------------CONSTRUCTOR--------------------------
    public Card(Card.Color color, Card.Value value) {
        this.COLOR = color;
        this.VALUE = value;
    }

    //--------------------------GETTERS--------------------------
    public Color getColor() {
        return COLOR;
    }

    public Value getValue() {
        return VALUE;
    }

    //--------------------------toString--------------------------
    public String toString() {
        return this.COLOR.toString() + " " + this.VALUE.toString();
    }

}
