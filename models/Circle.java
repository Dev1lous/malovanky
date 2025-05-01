package models;

import java.awt.*;

/**
 * Třída reprezentující kružnici v 2D prostoru
 */
public class Circle {

    private Point center;
    private int radius;
    private Color color;
    private int lineWidth;

    /**
     * Vytvoří novou kružnici se specifikovanými vlastnostmi
     */
    public Circle(Point center, int radius, Color color, int lineWidth) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.lineWidth = lineWidth > 0 ? lineWidth : 1;
    }

    // Getters
    public Point getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    // Setters
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setCenter(Point center) { 
        this.center = center;
    }

    /**
     * Vypočítá vzdálenost bodu od obvodu kružnice
     */
    public double distanceToCircumference(Point p) {
        double distanceToCenter = center.getDistance(p);
        return Math.abs(distanceToCenter - radius);
    }
} 