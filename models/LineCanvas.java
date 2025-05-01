package models;

import java.util.ArrayList;

public class LineCanvas {

    private ArrayList<Line> lines;
    private ArrayList<Circle> circles;
    private ArrayList<Rectangle> rectangles;
    private ArrayList<Square> squares;
    private ArrayList<FilledArea> filledAreas;

    public LineCanvas(
            ArrayList<Line> lines,
            ArrayList<Circle> circles
    ) {
        this.lines = lines != null ? lines : new ArrayList<>();
        this.circles = circles != null ? circles : new ArrayList<>();
        this.rectangles = new ArrayList<>();
        this.squares = new ArrayList<>();
        this.filledAreas = new ArrayList<>();
    }

    public LineCanvas() {
        this.lines = new ArrayList<>();
        this.circles = new ArrayList<>();
        this.rectangles = new ArrayList<>();
        this.squares = new ArrayList<>();
        this.filledAreas = new ArrayList<>();
    }

    public ArrayList<Line> getLines() {
        return lines;
    }

    public ArrayList<Circle> getCircles() {
        return circles;
    }

    public ArrayList<Rectangle> getRectangles() {
        return rectangles;
    }

    public ArrayList<Square> getSquares() {
        return squares;
    }

    public ArrayList<FilledArea> getFilledAreas() {
        return filledAreas;
    }

    public void add(Line line) {
        lines.add(line);
    }

    public void addCircle(Circle circle) {
        circles.add(circle);
    }

    public void addRectangle(Rectangle rect) {
        rectangles.add(rect);
    }

    public void addSquare(Square square) {
        squares.add(square);
    }

    public void addFilledArea(FilledArea area) {
        filledAreas.add(area);
    }

    public void clear() {
        lines.clear();
        circles.clear();
        rectangles.clear();
        squares.clear();
        filledAreas.clear();
    }
}