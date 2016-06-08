package pl.edu.pw.elka.hexancientempires;

import java.util.Arrays;

public abstract class Event {
    public static enum Type {
        MOVE
    }

    public static class Move extends Event {
        public final int unitId;
        public final Point to;

        public Move(int unitId, Point to) {
            this.unitId = unitId;
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
                    Integer.toString(event.unitId),
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
                    requireNumArguments(input, 3);
                    return new Move(parseUnitId(input[1]), parsePoint(input[2], input[3]));

                default:
                    throw invalidEvent(input, "Invalid command");

            }
        } catch (NumberFormatException e) {
            throw invalidEvent(input, "Invalid integer: " + e.getMessage());
        }
    }

    private static int parseUnitId(String str) {
        return Integer.parseInt(str);
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
