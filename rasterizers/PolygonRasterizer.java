package rasterizers;

import models.Line;
import models.Point;
import rasters.Raster;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PolygonRasterizer implements Rasterizer {
    private final Raster raster;
    private final LineRasterizerTrivial lineRasterizer;
    private Color currentColor;

    public PolygonRasterizer(Raster raster) {
        this.raster = raster;
        this.lineRasterizer = new LineRasterizerTrivial(raster);
        this.currentColor = Color.BLACK;
    }

    @Override
    public void setColor(Color color) {
        this.currentColor = color;
        lineRasterizer.setColor(color);
    }

    @Override
    public void rasterize(Line line) {
        lineRasterizer.rasterize(line);
    }

    @Override
    public void rasterizeArray(ArrayList<Line> arrayList) {
        for (Line line : arrayList) {
            rasterize(line);
        }
    }

    public void rasterizePolygon(List<Point> points) {
        if (points.size() < 3) return;

        ArrayList<Line> lines = new ArrayList<>();
        // Vytvoření čar mezi po sobě jdoucími body
        for (int i = 0; i < points.size() - 1; i++) {
            lines.add(new Line(points.get(i), points.get(i + 1), currentColor));
        }
        // Spojení posledního bodu s prvním bodem pro uzavření polygonu
        lines.add(new Line(points.get(points.size() - 1), points.get(0), currentColor));
        
        rasterizeArray(lines);
    }

    public void fillPolygon(List<Point> points) {
        if (points.size() < 3) return;

        // Nalezení ohraničujícího obdélníku
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Point p : points) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
        }

        // Pro každý řádek v ohraničujícím obdélníku
        for (int y = minY; y <= maxY; y++) {
            List<Integer> intersections = new ArrayList<>();
            
            // Nalezení průsečíků s hranami polygonu
            for (int i = 0; i < points.size(); i++) {
                Point p1 = points.get(i);
                Point p2 = points.get((i + 1) % points.size());

                if ((p1.getY() > y) != (p2.getY() > y)) {
                    int x = (int) (p1.getX() + (p2.getX() - p1.getX()) * (y - p1.getY()) / (p2.getY() - p1.getY()));
                    intersections.add(x);
                }
            }

            // Seřazení průsečíků
            intersections.sort(Integer::compareTo);

            // Vyplnění mezi páry průsečíků
            for (int i = 0; i < intersections.size() - 1; i += 2) {
                int x1 = intersections.get(i);
                int x2 = intersections.get(i + 1);
                for (int x = x1; x <= x2; x++) {
                    if (x >= 0 && x < raster.getWidth() && y >= 0 && y < raster.getHeight()) {
                        raster.setPixel(x, y, currentColor.getRGB());
                    }
                }
            }
        }
    }
} 