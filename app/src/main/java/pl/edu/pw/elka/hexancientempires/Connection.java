package pl.edu.pw.elka.hexancientempires;

public interface Connection {
    public boolean isServer();
    public void sendEvent(Event event);
}
