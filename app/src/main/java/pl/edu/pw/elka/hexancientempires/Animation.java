package pl.edu.pw.elka.hexancientempires;

import android.graphics.PointF;

public class Animation {
    private long totalTime;
    private PointF from;
    private PointF to;

    private Unit unit;
    private long elapsedTime;

    public Animation() {}

    public boolean isRunning() {
        return unit != null && elapsedTime < totalTime;
    }

    public void start(Unit unit, long totalTime, PointF from, PointF to) {
        this.totalTime = totalTime;
        this.from = from;
        this.to = to;
        this.unit = unit;

        elapsedTime = 0;
    }

    public void stop() {
        totalTime = 0;
        unit = null;
    }

    public void update(long frameTime) {
        elapsedTime += frameTime;
    }

    private float interpolate(float from, float to) {
        return from + (to - from) * elapsedTime / totalTime;
    }

    public float getCurrentX() {
        assertRunning();
        return interpolate(from.x, to.x);
    }

    public float getCurrentY() {
        assertRunning();
        return interpolate(from.y, to.y);
    }

    public Unit getUnit() {
        assertRunning();
        return unit;
    }

    public boolean isAnimating(Unit unit) {
        return isRunning() && this.unit == unit;
    }

    private void assertRunning() {
        if(!isRunning())
            throw new IllegalStateException("Animation not running");
    }
}
