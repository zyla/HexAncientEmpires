package pl.edu.pw.elka.hexancientempires;

import java.util.Arrays;

/**
 * An event send through network connection.
 */
public abstract class Event {
    public static class Action extends Event {
        public final Point from;
        public final Point to;

        /**
         * Event: do a game action
         *
         * @param from tile to start from
         * @param to tile to act upon
         */
        public Action(Point from, Point to) {
            this.from = from;
            this.to = to;
        }

        public <A> A accept(Visitor<A> visitor) {
            return visitor.action(this);
        }
    }

    /**
     * Event: finish current turn
     */
    public static class FinishTurn extends Event {
        public <A> A accept(Visitor<A> visitor) {
            return visitor.finishTurn(this);
        }
    }

    public static interface Visitor<A> {
        public A action(Action event);
        public A finishTurn(FinishTurn event);
    }

    public abstract <A> A accept(Visitor<A> visitor);

    /** Encode this Event as a String */
    public String serialize() {
        String[] tokens = accept(new Visitor<String[]>() {
            public String[] action(Action event) {
                return new String[] {
                    "action", 
                    Integer.toString(event.from.x),
                    Integer.toString(event.from.y),
                    Integer.toString(event.to.x),
                    Integer.toString(event.to.y),
                };
            }

            public String[] finishTurn(FinishTurn event) {
                return new String[] {
                    "finish_turn", 
                };
            }
        });
        return Utils.join(" ", tokens) + "\n";
    }

    /**
     * Decode an Event from a String.
     * @throws IllegalArgumentException when input is invalid
     */
    public static Event deserialize(String str) {
        String[] input = str.split("\\s+");

        if(input.length < 1)
            throw invalidEvent(input, "no command");

        String command = input[0];

        try {
            switch(command) {

                case "action":
                    requireNumArguments(input, 4);
                    return new Action(parsePoint(input[1], input[2]), parsePoint(input[3], input[4]));

                case "finish_turn":
                    requireNumArguments(input, 0);
                    return new FinishTurn();

                default:
                    throw invalidEvent(input, "Invalid command");

            }
        } catch (NumberFormatException e) {
            throw invalidEvent(input, "Invalid integer: " + e.getMessage());
        }
    }

    private static Point parsePoint(String xStr, String yStr) {
        return new Point(Integer.parseInt(xStr), Integer.parseInt(yStr));
    }

    private static void requireNumArguments(String[] input, int requiredNumArgs) {
        int numArgs = input.length - 1; // first is the command

        if(numArgs != requiredNumArgs) {
            throw invalidEvent(input, "Wrong number of arguments: " + numArgs + " (required: " + requiredNumArgs + ")");
        }
    }

    private static IllegalArgumentException invalidEvent(String[] input, String errorMsg) {
        return new IllegalArgumentException("Invalid Event: " + Arrays.toString(input) + ":  " + errorMsg);
    }
}
