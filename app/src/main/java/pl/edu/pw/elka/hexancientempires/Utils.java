package pl.edu.pw.elka.hexancientempires;

import android.graphics.PointF;

import java.util.List;
import java.util.ArrayList;

/**
 * Some utilities
 */
public class Utils {
    public static PointF toPointF(Point p) {
        return new PointF(p.x, p.y);
    }

    public static List<PointF> pathToPixels(Iterable<Point> xs) {
        ArrayList<PointF> result = new ArrayList<>();
        for(Point x: xs) {
            result.add(toPointF(TileMath.tileLocation(x)));
        }
        return result;
    }

    public static String join(String separator, String[] tokens) {
        StringBuilder result = new StringBuilder();
        for(String tok: tokens) {
            if(result.length() > 0)
                result.append(' ');
            result.append(tok);
        }
        return result.toString();
    }
}
