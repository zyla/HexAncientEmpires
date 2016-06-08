package pl.edu.pw.elka.hexancientempires;

import java.util.Arrays;

public abstract class Event {
    public static class Move extends Event {
        public final Point from;
        public final Point to;

        public Move(Point from, Point to) {
            this.from = from;
            this.to = to;
        }

        public <A> A accept(Visitor<A> visitor) {
            return visitor.move(this);
        }
    }

    public static interface Visitor<A> {
        public A move(Move event);
    }

    public abstract <A> A accept(Visitor<A> visitor);

    public String[] serialize() {
        return accept(new Visitor<String[]>() {
            public String[] move(Move event) {
                return new String[] {
                    "move", 
                    Integer.toString(event.from.x),
                    Integer.toString(event.from.y),
                    Integer.toString(event.to.x),
                    Integer.toString(event.to.y),
                };
            }
        });
    }

    public static Event deserialize(String[] input) {
        if(input.length < 1)
            throw invalidEvent(input, "no command");

        String command = input[0];

        try {
            switch(command) {

                case "move":
                    requireNumArguments(input, 4);
                    return new Move(parsePoint(input[1], input[2]), parsePoint(input[3], input[4]));

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
