package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;

public class DotRasterizer implements Rasterizer {

    private Raster raster;

    public DotRasterizer(Raster raster) {
        this.raster = raster;
    }

    public void setColor(Color color) {
        // Není přímo použito
    }

    // Pomocná metoda pro vykreslení jednoho pixelu s ohledem na tloušťku čáry
    private void drawThickPixel(int x, int y, int color, int lineWidth) {
        for (int i = -lineWidth / 2; i <= lineWidth / 2; i++) {
            for (int j = -lineWidth / 2; j <= lineWidth / 2; j++) {
                int px = x + i;
                int py = y + j;
                if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                    raster.setPixel(px, py, color);
                }
            }
        }
    }

    public void rasterize(Line line) {
        int x1 = line.getPoint1().getX();
        int y1 = line.getPoint1().getY();
        int x2 = line.getPoint2().getX();
        int y2 = line.getPoint2().getY();
        int lineWidth = line.getLineWidth();
        int color = line.getColor().getRGB();

        // Použití Bresenhamova algoritmu pro lepší vykreslování čar
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        int dotCounter = 0;
        int dotSpacing = 5; // Mezera mezi tečkami

        while (true) {
            // Vykreslení tečky pouze pokud je čítač na začátku mezery
            if (dotCounter == 0) {
                drawThickPixel(x1, y1, color, lineWidth);
            }
            
            dotCounter = (dotCounter + 1) % dotSpacing;

            if (x1 == x2 && y1 == y2) {
                break;
            }
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    public void rasterizeArray(ArrayList<Line> arrayList) {
        for (Line line : arrayList) {
            rasterize(line);
        }
        // Překreslení je lepší řešit v App
        // raster.repaint(raster.getGraphics());
    }
} 