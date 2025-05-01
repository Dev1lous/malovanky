import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

public class Canvas {
    private Raster raster;

    public Canvas(int width, int height) {
        this.raster = new Raster(width, height);
    }
    
    /**
     * Flood fill implementation similar to paint bucket tool
     * @param x Starting x coordinate
     * @param y Starting y coordinate
     * @param fillColor Color to fill with
     */
    public void floodFill(int x, int y, int fillColor) {
        if (x < 0 || x >= raster.getWidth() || y < 0 || y >= raster.getHeight()) {
            return;
        }
        
        int targetColor = raster.getPixel(x, y);
        if (targetColor == fillColor) {
            return; // Already filled with this color
        }
        
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));
        
        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int px = p.x;
            int py = p.y;
            
            if (px < 0 || px >= raster.getWidth() || py < 0 || py >= raster.getHeight()) {
                continue;
            }
            
            if (raster.getPixel(px, py) != targetColor) {
                continue;
            }
            
            raster.setPixel(px, py, fillColor);
            
            // Add adjacent pixels to the queue
            queue.add(new Point(px + 1, py));
            queue.add(new Point(px - 1, py));
            queue.add(new Point(px, py + 1));
            queue.add(new Point(px, py - 1));
        }
    }
} 