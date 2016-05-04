package pl.edu.pw.elka.hexancientempires;

/**
 * We can't use Android's Point class in local tests. Meh.
 */
public class Point {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Point))
            return false;
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }
}
