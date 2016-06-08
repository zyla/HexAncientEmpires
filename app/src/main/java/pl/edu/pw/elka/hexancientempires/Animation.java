package pl.edu.pw.elka.hexancientempires;

import java.util.List;

import android.graphics.PointF;

public class Animation {
    private long totalTime;
    private List<PointF> points;
    private int fromPointIndex;

    private Unit unit;
    private long elapsedTime;

    public Animation() {}

    public boolean isRunning() {
        return unit != null && fromPointIndex < points.size() - 1;
    }

    public void start(Unit unit, long totalTime, List<PointF> points) {
        this.unit = unit;
        this.totalTime = totalTime;
        this.points = points;

        fromPointIndex = 0;
        elapsedTime = 0;
    }

    public void stop() {
        totalTime = 0;
        unit = null;
    }

    public void update(long frameTime) {
        elapsedTime += frameTime;

        while(elapsedTime >= totalTime) {
            elapsedTime -= totalTime;
            fromPointIndex++;
        }
    }

    private float interpolate(float from, float to) {
        return from + (to - from) * elapsedTime / totalTime;
    }

    public PointF getCurrentPoint() {
        assertRunning();

        PointF from = points.get(fromPointIndex);
        PointF to = points.get(fromPointIndex + 1);

        return new PointF(
            interpolate(from.x, to.x),
            interpolate(from.y, to.y)
        );
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
