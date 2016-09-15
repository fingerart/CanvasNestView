package me.fingerart.android.library;

/**
 * Created by FingerArt on 2016/8/9.
 */
public class Point {
    public double x;
    public double y;
    public double vx;
    public double vy;

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Point) {
            Point p = (Point) obj;
            return x == p.x && y == p.y;
        }
        return false;
    }
}
