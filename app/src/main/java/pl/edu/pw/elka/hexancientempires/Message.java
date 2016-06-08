package pl.edu.pw.elka.hexancientempires;

import android.graphics.PointF;

public class Message {
    private String text;
    private long timeLeft;

    public void show(String text, long time) {
        this.text = text;
        this.timeLeft = time;
    }

    public void update(long frameTime) {
        timeLeft = Math.max(0, timeLeft - frameTime);
    }

    public boolean isDisplaying() {
        return timeLeft > 0;
    }

    public String getText() {
        return text;
    }

    public long getTimeLeft() {
        return timeLeft;
    }
}
