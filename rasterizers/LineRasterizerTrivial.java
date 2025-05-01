package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;
import java.util.ArrayList;

public class LineRasterizerTrivial implements Rasterizer {

    private Raster raster;

    public LineRasterizerTrivial(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void setColor(Color color) {
        // Barva není v tomto rasterizeru použita, protože je převzata z objektu Line
    }

    public void rasterize(Line line) {
        int x1 = line.getPoint1().getX();
        int y1 = line.getPoint1().getY();
        int x2 = line.getPoint2().getX();
        int y2 = line.getPoint2().getY();
        int lineWidth = line.getLineWidth(); // Získání šířky čáry z objektu Line

        // Kontrola hranic, aby nedošlo k chybě
        if(x1 < 0 || x1 >= raster.getWidth() || x2 < 0 || x2 >= raster.getWidth() || y1 < 0 || y1 >= raster.getHeight() || y2 < 0 || y2 >= raster.getHeight()) {
            return;
        }

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            // Vykreslení pixelů v čtverci kolem aktuálního bodu pro tloušťku čáry
            for (int i = -lineWidth/2; i <= lineWidth/2; i++) {
                for (int j = -lineWidth/2; j <= lineWidth/2; j++) {
                    int px = x1 + i;
                    int py = y1 + j;
                    if (px >= 0 && px < raster.getWidth() && py >= 0 && py < raster.getHeight()) {
                        raster.setPixel(px, py, line.getColor().getRGB());
                    }
                }
            }

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

        raster.repaint(raster.getGraphics());
    }
}