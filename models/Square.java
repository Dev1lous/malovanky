package models;

import java.awt.*;

/**
 * Třída reprezentující čtverec v 2D prostoru
 */
public class Square {
    private Point p1; // První roh
    private Point p2; // Protilehlý roh
    private Color color;
    private int lineWidth;
    private LineStyle lineStyle;

    /**
     * Vytvoří nový čtverec se specifikovanými vlastnostmi
     */
    public Square(Point p1, Point p2, Color color, int lineWidth, LineStyle style) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
        this.lineWidth = lineWidth > 0 ? lineWidth : 1;
        this.lineStyle = style != null ? style : LineStyle.SOLID;
    }

    // Gettery
    public Point getP1() { return p1; }
    public Point getP2() { return p2; }
    public Color getColor() { return color; }
    public int getLineWidth() { return lineWidth; }
    public LineStyle getLineStyle() { return lineStyle; }

    // Settery (pro úpravy)
    public void setP1(Point p1) { this.p1 = p1; }
    public void setP2(Point p2) { this.p2 = p2; }

    /**
     * Pomocná metoda pro získání všech čtyř rohů
     */
    public Point[] getCorners() {
        int size = Math.max(Math.abs(p2.getX() - p1.getX()), Math.abs(p2.getY() - p1.getY()));
        int signX = (p2.getX() - p1.getX() >= 0) ? 1 : -1;
        int signY = (p2.getY() - p1.getY() >= 0) ? 1 : -1;
        
        Point p3 = new Point(p1.getX() + size * signX, p1.getY());
        Point p4 = new Point(p1.getX() + size * signX, p1.getY() + size * signY);
        Point p5 = new Point(p1.getX(), p1.getY() + size * signY);
        
        return new Point[]{p1, p3, p4, p5};
    }

    /**
     * Pomocná metoda pro kontrolu, zda je bod blízko některé hrany
     */
    public boolean isNearEdge(Point clickPoint, double threshold) {
        Point[] corners = getCorners();
        for (int i = 0; i < corners.length; i++) {
            Point c1 = corners[i];
            Point c2 = corners[(i + 1) % corners.length];
            // Kontrola vzdálenosti od bodu k úsečce
            if (distanceToLineSegment(clickPoint, c1, c2) <= threshold) {
                return true;
            }
        }
        return false;
    }

    /**
     * Pomocná metoda: Vzdálenost od bodu p k úsečce c1-c2
     */
    private double distanceToLineSegment(Point p, Point c1, Point c2) {
        double x = p.getX();
        double y = p.getY();
        double x1 = c1.getX();
        double y1 = c1.getY();
        double x2 = c2.getX();
        double y2 = c2.getY();

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