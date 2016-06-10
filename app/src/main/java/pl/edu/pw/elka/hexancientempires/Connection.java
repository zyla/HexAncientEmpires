package pl.edu.pw.elka.hexancientempires;

/**
 * Interface representing a connection.
 */
public interface Connection {
    /**
     * @return false if this side initiated the connection; false otherwise
     */
    public boolean isServer();

    /**
     * Sends an event to the other side.
     */
    public void sendEvent(Event event);
}
