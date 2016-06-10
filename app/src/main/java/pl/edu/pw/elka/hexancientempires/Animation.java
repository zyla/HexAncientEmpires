package pl.edu.pw.elka.hexancientempires;

import java.util.List;

import android.graphics.PointF;

/**
 * Class representing animation of a moving unit.
 */
public class Animation {
    private long totalTime;
    private List<PointF> points;
    private int fromPointIndex;

    private Unit unit;
    private long elapsedTime;

    /** Returns thue if the animation is not yet finished. */
    public boolean isRunning() {
        return unit != null && fromPointIndex < points.size() - 1;
    }

    /**
     * Begins the animation.
     *
     * @param unit the unit to move
     * @param totalTime time to move between each two tiles
     * @param points path to follow (coordinates in pixels)
     */
    public void start(Unit unit, long totalTime, List<PointF> points) {
        this.unit = unit;
        this.totalTime = totalTime;
        this.points = points;

        fromPointIndex = 0;
        elapsedTime = 0;
    }

    /** Stops the animation */
    public void stop() {
        totalTime = 0;
        unit = null;
    }

    /**
     * Updates animation state.
     * @param frameTime time elapsed since last update
     */
    public void update(long frameTime) {
        if(!isRunning())
            return;

        elapsedTime += frameTime;

        while(isRunning() && elapsedTime >= totalTime) {
            elapsedTime -= totalTime;
            fromPointIndex++;
        }
    }

    private float interpolate(float from, float to) {
        return from + (to - from) * elapsedTime / totalTime;
    }

    /**
     * @return current position of the animated unit
     */
    public PointF getCurrentPoint() {
        assertRunning();

        PointF from = points.get(fromPointIndex);
        PointF to = points.get(fromPointIndex + 1);

        return new PointF(
            interpolate(from.x, to.x),
            interpolate(from.y, to.y)
        );
    }

    /**
     * @return currently animated unit
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * @returns true if the given unit is currently being animated
     */
    public boolean isAnimating(Unit unit) {
        return isRunning() && this.unit == unit;
    }

    private void assertRunning() {
        if(!isRunning())
            throw new IllegalStateException("Animation not running");
    }
}
