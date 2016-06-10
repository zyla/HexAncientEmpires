package pl.edu.pw.elka.hexancientempires;

/**
 * We can't use Android's Rect class in local tests. Meh.
 */
public class Rect {
    public final int left, top, right, bottom;

    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        return String.format("Rect[%d, %d, %d, %d]", left, top, right, bottom);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Rect))
            return false;
        Rect r = (Rect) o;
        return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
    }
}
