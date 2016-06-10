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


    /**
     * calculate path measured with pixels for unit movement animation
     * @param xs list of points in map geometry
     * @return List of offsets as a path
     */
    public static List<PointF> pathToPixels(Iterable<Point> xs) {
        ArrayList<PointF> result = new ArrayList<>();
        for(Point x: xs) {
            result.add(toPointF(TileMath.tileLocation(x)));
        }
        return result;
    }

    /**
     * concatenate tokens into one string separating them with separator
     * @param separator
     * @param tokens
     * @return String with all tokens
     */
    public static String join(String separator, String[] tokens) {
        StringBuilder result = new StringBuilder();
        for(String tok: tokens) {
            if(result.length() > 0)
                result.append(separator);
            result.append(tok);
        }
        return result.toString();
    }
}
