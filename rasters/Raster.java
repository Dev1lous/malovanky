package rasters;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Raster {

    /**
     * Vymazání plátna
     */
    void clear();

    /**
     * Nastavení barvy pro vymazání
     *
     * @param color
     *            barva pro vymazání
     */
    void setClearColor(int color);

    /**
     * Získání horizontální velikosti
     *
     * @return šířka
     */
    int getWidth();

    /**
     * Získání vertikální velikosti
     *
     * @return výška
     */
    int getHeight();

    /**
     * Získání barvy pixelu na pozici [x,y]
     *
     * @param x
     *            horizontální souřadnice
     * @param y
     *            vertikální souřadnice
     * @return    barva pixelu
     */
    int getPixel(int x, int y);

    /**
     * Nastavení barvy pixelu na pozici [x,y]
     *
     * @param x
     *            horizontální souřadnice
     * @param y
     *            vertikální souřadnice
     * @param color
     *            barva pixelu
     */
    void setPixel(int x, int y, int color);

    Graphics getGraphics();

    void repaint(Graphics graphics);

    BufferedImage getImg();

}