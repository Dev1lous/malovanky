package models;

/**
 * Třída reprezentující bod v 2D prostoru
 */
public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Vypočítá euklidovskou vzdálenost mezi dvěma body
     */
    public double getDistance(Point p) {
        return Math.sqrt(getDistanceSq(p));
    }

    /**
     * Vypočítá druhou mocninu euklidovské vzdálenosti mezi dvěma body
     * (rychlejší než getDistance, protože nepočítá odmocninu)
     */
    public double getDistanceSq(Point p) {
        int dx = this.x - p.x;
        int dy = this.y - p.y;
        return dx * dx + dy * dy;
    }
}