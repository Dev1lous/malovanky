package models;

import java.awt.Color;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents an area filled with a specific color, identified by its set of pixels.
 * Can also represent an erased area.
 */
public class FilledArea {

    private Point seedPoint; // Keep seedPoint for potential future use, though not strictly needed for drawing
    private Color fillColor;
    private Set<Point> filledPixels = new HashSet<>(); // Stores all pixels belonging to this area
    private boolean isEraser;

    // Constructor for regular fill
    public FilledArea(Point seedPoint, Color fillColor) {
        this(seedPoint, fillColor, false);
    }

    // Constructor for eraser or specifying type
    public FilledArea(Point seedPoint, Color fillColor, boolean isEraser) {
        this.seedPoint = seedPoint;
        this.fillColor = fillColor;
        this.isEraser = isEraser;
        // filledPixels will be set later using setFilledPixels or addPixels
    }

    // Getters
    public Point getSeedPoint() {
        return seedPoint;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Set<Point> getFilledPixels() {
        return filledPixels;
    }

    public boolean isEraser() {
        return isEraser;
    }

    // Setter
    public void setFilledPixels(Set<Point> pixels) {
        this.filledPixels = (pixels != null) ? pixels : new HashSet<>();
    }
    
    // Method to add multiple pixels
    public void addPixels(Set<Point> pixels) {
        if (pixels != null) {
            this.filledPixels.addAll(pixels);
        }
    }
} 