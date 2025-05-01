package rasterizers;

import models.Line;
import java.awt.*;
import java.util.ArrayList;

public interface Rasterizer {

    void setColor(Color color);

    void rasterize(Line line);

    void rasterizeArray(ArrayList<Line> arrayList);

}