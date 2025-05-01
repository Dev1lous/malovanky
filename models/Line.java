package models;

import java.awt.*;

/**
 * Třída reprezentující úsečku v 2D prostoru
 */
public class Line {

    private Point point1;
    private Point point2;
    private Color color;
    private int lineWidth;
    private LineStyle lineStyle;

    /**
     * Vytvoří novou úsečku s výchozí tloušťkou 1 a plným stylem
     */
    public Line(Point point1, Point point2, Color color) {
        this(point1, point2, color, 1, LineStyle.SOLID);
    }

    /**
     * Vytvoří novou úsečku s plným stylem
     */
    public Line(Point point1, Point point2, Color color, int lineWidth) {
        this(point1, point2, color, lineWidth, LineStyle.SOLID);
    }

    /**
     * Vytvoří novou úsečku se specifikovanými vlastnostmi
     */
    public Line(Point point1, Point point2, Color color, int lineWidth, LineStyle style) {
        this.point1 = point1;
        this.point2 = point2;
        this.color = color;
        this.lineWidth = lineWidth > 0 ? lineWidth : 1;
        this.lineStyle = style != null ? style : LineStyle.SOLID;
    }

    public Color getColor() {
        return color;
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    /**
     * Vypočítá vzdálenost bodu od úsečky
     * @param p Bod, od kterého měříme vzdálenost
     * @return Vzdálenost bodu od úsečky
     */
    public double distanceToPoint(Point p) {
        double x = p.getX();
        double y = p.getY();
        double x1 = point1.getX();
        double y1 = point1.getY();
        double x2 = point2.getX();
        double y2 = point2.getY();

        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;

        if (len_sq != 0) {
            param = dot / len_sq;
        }

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = x - xx;
        double dy = y - yy;

        return Math.sqrt(dx * dx + dy * dy);
    }
}