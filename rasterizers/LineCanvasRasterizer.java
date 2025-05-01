package rasterizers;

import models.Line;
import models.LineCanvas;
import models.Circle;
import models.Point;
import models.Rectangle;
import models.Square;
import models.LineStyle;
import models.FilledArea;
import rasters.Raster;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Rasterizer pro vykreslování objektů na plátno
 * VYSVĚTLIVKA: Tato třída se stará o vykreslování všech tvarů na plátno
 * - Vykresluje objekty v pořadí: výplně, obrysy tvarů, oblasti mazání
 * - Podporuje různé styly čar (plná, přerušovaná, tečkovaná)
 * - Implementuje algoritmy pro vykreslování čar a tvarů
 */
public class LineCanvasRasterizer {

    private Raster raster;

    private Rasterizer solidRasterizer;
    private Rasterizer dashRasterizer;
    private Rasterizer dotRasterizer;

    public LineCanvasRasterizer(Raster raster) {
        this.raster = raster;

        solidRasterizer = new LineRasterizerTrivial(raster);
        dashRasterizer = new DashRasterizer(raster);
        dotRasterizer = new DotRasterizer(raster);
    }
    
    /**
     * Vykreslí celé plátno na rastr
     * VYSVĚTLIVKA: Pořadí vykreslování:
     * 1. Nejprve se vykreslí výplně
     * 2. Pak se vykreslí obrysy tvarů
     * 3. Nakonec se vykreslí oblasti mazání
     */
    public void rasterizeCanvas(LineCanvas canvas) {
        raster.clear(); // CLEAR the raster before drawing everything from the model
        
        // 1. Draw NORMAL filled areas from stored pixels
        for (FilledArea area : canvas.getFilledAreas()) {
            if (!area.isEraser()) {
                int fillColor = area.getFillColor().getRGB();
                for (Point p : area.getFilledPixels()) {
                    int px = p.getX(); 
                    int py = p.getY();
                    if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                       raster.setPixel(px, py, fillColor);
                    } 
                }
            }
        }
        
        // 2. THEN Rasterize outlines (lines, shapes) over the normal fill
        for (Line line : canvas.getLines()) {
            getRasterizerForStyle(line.getLineStyle()).rasterize(line);
        }
        
        // Rasterize circles
        for (Circle circle : canvas.getCircles()) {
            rasterizeCircle(circle); 
        }

        // Rasterize rectangles
        for (Rectangle rect : canvas.getRectangles()) {
            rasterizeRectangle(rect);
        }

        // Rasterize squares
        for (Square square : canvas.getSquares()) {
            rasterizeSquare(square);
        }
        
        // 3. FINALLY, draw ERASER areas from stored pixels on top of everything else
         for (FilledArea area : canvas.getFilledAreas()) {
            if (area.isEraser()) {
                int eraseColor = area.getFillColor().getRGB(); // Should be black
                for (Point p : area.getFilledPixels()) {
                    int px = p.getX(); 
                    int py = p.getY();
                    if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                       raster.setPixel(px, py, eraseColor);
                    } 
                }
            }
        }
        
        // Repaint should be handled by the caller (App)
    }

    // Method to draw only a single shape onto the current raster
    public void rasterizeSingleShape(Object shape) {
        if (shape instanceof Line) {
            Line line = (Line) shape;
            getRasterizerForStyle(line.getLineStyle()).rasterize(line);
        } else if (shape instanceof Circle) {
            rasterizeCircle((Circle) shape);
        } else if (shape instanceof Square) {
            rasterizeSquare((Square) shape);
        } else if (shape instanceof Rectangle) {
            rasterizeRectangle((Rectangle) shape);
        }
        // No repaint here, caller handles it
    }

    /**
     * Vykreslí náhled čáry na rastr
     * VYSVĚTLIVKA: Používá se pro zobrazení náhledu při kreslení
     * - Podporuje různé styly čar
     * - Vykresluje se přímo na rastr
     */
    public void rasterizePreview(Line line) {
        getRasterizerForStyle(line.getLineStyle()).rasterize(line);
    }

    /**
     * Vrátí vhodný rasterizer pro daný styl čáry
     * VYSVĚTLIVKA: Dostupné styly čar:
     * - SOLID: plná čára
     * - DASHED: přerušovaná čára (můžete upravit délku čáry a mezery)
     * - DOTTED: tečkovaná čára (můžete upravit velikost teček a mezery)
     */
    private Rasterizer getRasterizerForStyle(LineStyle style) {
        switch (style) {
            case DASHED:
                return dashRasterizer;
            case DOTTED:
                return dotRasterizer;
            case SOLID:
            default:
                return solidRasterizer;
        }
    }

    /**
     * Vykreslí obdélník na rastr
     * VYSVĚTLIVKA: Obdélník je definován dvěma rohy
     * - Vykresluje se pomocí čtyř čar
     * - Podporuje různé styly čar a tloušťku
     */
    private void rasterizeRectangle(Rectangle rect) {
        Point[] corners = rect.getCorners();
        Color color = rect.getColor();
        int width = rect.getLineWidth();
        LineStyle style = rect.getLineStyle();
        Rasterizer r = getRasterizerForStyle(style);

        r.rasterize(new Line(corners[0], corners[1], color, width, style));
        r.rasterize(new Line(corners[1], corners[2], color, width, style));
        r.rasterize(new Line(corners[2], corners[3], color, width, style));
        r.rasterize(new Line(corners[3], corners[0], color, width, style));
    }

    /**
     * Vykreslí čtverec na rastr
     * VYSVĚTLIVKA: Čtverec je definován jedním rohem a délkou strany
     * - Vykresluje se pomocí čtyř čar stejné délky
     * - Podporuje různé styly čar a tloušťku
     */
    private void rasterizeSquare(Square square) {
        Point[] corners = square.getCorners();
        Color color = square.getColor();
        int width = square.getLineWidth();
        LineStyle style = square.getLineStyle();
        Rasterizer r = getRasterizerForStyle(style);

        r.rasterize(new Line(corners[0], corners[1], color, width, style));
        r.rasterize(new Line(corners[1], corners[2], color, width, style));
        r.rasterize(new Line(corners[2], corners[3], color, width, style));
        r.rasterize(new Line(corners[3], corners[0], color, width, style));
    }

    /**
     * Vykreslí kružnici na rastr
     * VYSVĚTLIVKA: Používá Bresenhamův algoritmus pro vykreslení kružnice
     * - Kružnice je definována středem a poloměrem
     * - Vykresluje se pomocí osmi symetrických bodů
     */
    private void rasterizeCircle(Circle circle) {
        Point center = circle.getCenter();
        int radius = circle.getRadius();
        Color color = circle.getColor();
        int lineWidth = circle.getLineWidth();

        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;

        while (y >= x) {
            drawThickPixel(center.getX() + x, center.getY() + y, color.getRGB(), lineWidth);
            drawThickPixel(center.getX() - x, center.getY() + y, color.getRGB(), lineWidth);
            drawThickPixel(center.getX() + x, center.getY() - y, color.getRGB(), lineWidth);
            drawThickPixel(center.getX() - x, center.getY() - y, color.getRGB(), lineWidth);
            drawThickPixel(center.getX() + y, center.getY() + x, color.getRGB(), lineWidth);
            drawThickPixel(center.getX() - y, center.getY() + x, color.getRGB(), lineWidth);
            drawThickPixel(center.getX() + y, center.getY() - x, color.getRGB(), lineWidth);
            drawThickPixel(center.getX() - y, center.getY() - x, color.getRGB(), lineWidth);

            x++;

            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
        }
    }

    /**
     * Vykreslí tlustý pixel na rastr
     * VYSVĚTLIVKA: Vykresluje čtverec pixelů kolem zadaného bodu
     * - Velikost čtverce je určena parametrem lineWidth
     * - Používá se pro vykreslování tlustých čar a tvarů
     */
    private void drawThickPixel(int x, int y, int color, int lineWidth) {
        for (int i = -lineWidth/2; i <= lineWidth/2; i++) {
            for (int j = -lineWidth/2; j <= lineWidth/2; j++) {
                int px = x + i;
                int py = y + j;
                if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                    raster.setPixel(px, py, color);
                }
            }
        }
    }

    /**
     * Implementace výplně pomocí algoritmu flood fill
     * VYSVĚTLIVKA: Vyplní souvislou oblast stejné barvy
     * - Používá zásobník pro ukládání bodů k vyplnění
     * - Vrací množinu všech vyplněných bodů
     */
    public Set<Point> floodFill(int startX, int startY, int fillColor) {
        Set<Point> filledPixels = new HashSet<>();
        Stack<Point> stack = new Stack<>();
        int targetColor = raster.getPixel(startX, startY);
        
        if (targetColor == fillColor) {
            return filledPixels;
        }

        stack.push(new Point(startX, startY));

        while (!stack.isEmpty()) {
            Point p = stack.pop();
            int x = p.getX();
            int y = p.getY();

            if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) {
                continue;
            }

            if (raster.getPixel(x, y) != targetColor) {
                continue;
            }

            raster.setPixel(x, y, fillColor);
            filledPixels.add(new Point(x, y));

            stack.push(new Point(x + 1, y));
            stack.push(new Point(x - 1, y));
            stack.push(new Point(x, y + 1));
            stack.push(new Point(x, y - 1));
        }

        return filledPixels;
    }
}