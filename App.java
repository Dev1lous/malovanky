import models.Line;
import models.LineCanvas;
import models.Point;
import models.Circle;
import models.Rectangle;
import models.Square;
import models.FilledArea;
import models.LineStyle;
import rasterizers.LineCanvasRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Hlavní třída aplikace pro kreslení
 * Obsahuje grafické rozhraní a implementuje následující funkce:
 * 1. Kreslení čar - plné, přerušované, nastavení barvy a tloušťky
 * 2. Základní tvary - čára, kružnice, čtverec, obdélník, polygon
 * 3. Úpravy tvarů - možnost přesunu bodů (vrcholů) pomocí pravého tlačítka myši
 * 4. Další nástroje - guma, vyplnění polygonu
 * 5. Uživatelské rozhraní - nástrojová lišta s ovládacími prvky
 */
public class App {

    private final JPanel panel;                  // Panel pro vykreslování grafiky
    private final Raster raster;                // Rastr pro uchovávání pixelů
    private MouseAdapter mouseAdapter;          // Zpracování událostí myši
    private KeyAdapter keyAdapter;              // Zpracování událostí klávesnice
    private Point point;                        // Aktuální bod (při kreslení)
    private Point pointNearest;                 // Nejbližší bod (při editaci)
    private LineCanvasRasterizer rasterizer;    // Rasterizer pro vykreslování čar
    private LineCanvas canvas;                  // Plátno obsahující všechny čáry
    private boolean shiftMode = false;          // Režim zarovnání na 45° (SHIFT)
    private LineStyle currentLineStyle = LineStyle.SOLID;  // VYSVĚTLIVKA: Typ čáry (SOLID=plná, DASHED=přerušovaná, DOTTED=tečkovaná)
    
    // VYSVĚTLIVKA: Nastavení vlastností nástrojů
    private Color currentColor = Color.RED;     // Aktuální vybraná barva
    private int currentLineWidth = 1;           // VYSVĚTLIVKA: Tloušťka čáry (1-20 pixelů)
    private int currentShapeType = 0;           // VYSVĚTLIVKA: Typ vybraného tvaru (0=čára, 1=kružnice, 2=čtverec, 3=obdélník, 4=polygon)
    private ArrayList<Point> polygonPoints = new ArrayList<>(); // Body pro kreslení polygonu
    private boolean isDrawingPolygon = false;   // Příznak kreslení polygonu
    private JToolBar toolBar;                   // Nástrojová lišta
    private Circle selectedCircle = null;       
    private Rectangle selectedRectangle = null;
    private Square selectedSquare = null;
    private Point fixedCorner = null;
    private static final int SHAPE_FILL = 5;    // VYSVĚTLIVKA: Index pro nástroj výplně
    private static final int SHAPE_ERASER = 6;  // VYSVĚTLIVKA: Index pro nástroj gumy
    private FilledArea currentEraserArea = null; // Pro průběžné mazání

    /**
     * Vstupní bod aplikace
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    /**
     * Vyčistí plátno zadanou barvou
     */
    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    /**
     * Překreslí rastr na grafický kontext
     */
    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    /**
     * Inicializuje aplikaci a spustí ji
     */
    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    /**
     * Konstruktor aplikace - vytvoření okna a inicializace komponent
     */
    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("Gidegadao");
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Inicializace rastrů
        raster = new RasterBufferedImage(width, height);

        // Vytvoření panelu pro vykreslování s překrytou metodou paintComponent
        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g); // Zobrazit aktuální obsah rastru
                
                // Odstranit vykreslování náhledu polygonu odsud
                /*
                if (isDrawingPolygon && polygonPoints.size() > 0) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(currentColor);
                    g2d.setStroke(new BasicStroke(currentLineWidth));
                    
                    // Vykreslení již nakreslených čar polygonu
                    for (int i = 0; i < polygonPoints.size() - 1; i++) {
                        Point p1 = polygonPoints.get(i);
                        Point p2 = polygonPoints.get(i + 1);
                        g2d.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                    }
                    
                    // Propojení posledního bodu s kurzorem pro náhled
                    if (polygonPoints.size() > 0 && point != null) {
                        Point lastPoint = polygonPoints.get(polygonPoints.size() - 1);
                        g2d.drawLine(lastPoint.getX(), lastPoint.getY(), point.getX(), point.getY());
                    }
                }
                */
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        // Vytvoření nástrojové lišty s ovládacími prvky
        createToolBar();
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // Inicializace rasterizerů a plátna
        rasterizer = new LineCanvasRasterizer(raster);
        canvas = new LineCanvas();

        // Připojení posluchačů událostí
        createAdapters();
        panel.addMouseMotionListener(mouseAdapter);
        panel.addMouseListener(mouseAdapter);
        panel.addKeyListener(keyAdapter);

        // Nastavení zaměření panelu pro příjem událostí klávesnice
        panel.setFocusable(true);
        panel.requestFocus();
        panel.requestFocusInWindow();
    }
    
    /**
     * Vytvoří nástrojovou lištu s ovládacími prvky
     * VYSVĚTLIVKA: Zde můžete upravit:
     * - Typ čáry (plná, přerušovaná, tečkovaná)
     * - Tloušťku čáry (1-20 pixelů)
     * - Barvu
     * - Typ tvaru (čára, kružnice, čtverec, obdélník, polygon)
     */
    private void createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Přidat výběr tvaru
        String[] shapes = {"Čára", "Kružnice", "Čtverec", "Obdélník", "Polygon", "Výpln", "Guma"};
        JComboBox<String> shapeCombo = new JComboBox<>(shapes);
        shapeCombo.addActionListener(e -> {
            switch (shapeCombo.getSelectedIndex()) {
                case 0: currentShapeType = 0; break; // Čára
                case 1: currentShapeType = 1; break; // Kružnice
                case 2: currentShapeType = 2; break; // Čtverec
                case 3: currentShapeType = 3; break; // Obdélník
                case 4: currentShapeType = 4; break; // Polygon
                case 5: currentShapeType = SHAPE_FILL; break; // Výpln
                case 6: currentShapeType = SHAPE_ERASER; break; // Guma
            }
        });
        toolBar.add(new JLabel("Tvar:"));
        toolBar.add(shapeCombo);
        
        // Přidat výběr barvy
        JButton colorButton = new JButton("Barva");
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Vyber barvu", currentColor);
            if (newColor != null) {
                currentColor = newColor;
            }
        });
        toolBar.add(colorButton);
        
        // Přidat nastavení tloušťky čáry
        SpinnerNumberModel widthModel = new SpinnerNumberModel(1, 1, 20, 1);
        JSpinner widthSpinner = new JSpinner(widthModel);
        widthSpinner.addChangeListener(e -> currentLineWidth = (Integer) widthSpinner.getValue());
        toolBar.add(new JLabel("Tloušťka:"));
        toolBar.add(widthSpinner);
        
        // Přidat výběr stylu čáry
        String[] styles = {"Plná", "Přerušovaná", "Tečkovaná"};
        JComboBox<String> styleCombo = new JComboBox<>(styles);
        styleCombo.addActionListener(e -> {
            switch (styleCombo.getSelectedIndex()) {
                case 0: currentLineStyle = LineStyle.SOLID; break;
                case 1: currentLineStyle = LineStyle.DASHED; break;
                case 2: currentLineStyle = LineStyle.DOTTED; break;
            }
        });
        toolBar.add(new JLabel("Styl:"));
        toolBar.add(styleCombo);

        // Přidat tlačítko pro vymazání
        JButton clearButton = new JButton("Vymazat");
        clearButton.addActionListener(e -> {
            canvas.clear();
            raster.clear();
            polygonPoints.clear();
            isDrawingPolygon = false;
            panel.repaint();
        });
        toolBar.add(clearButton);
    }

    /**
     * Vytvoří posluchače událostí myši a klávesnice
     */
    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                point = new Point(e.getX(), e.getY());
                
                // Zpracování nástroje gumy - začít vytvářet trvalou oblast mazání
                if (currentShapeType == SHAPE_ERASER) {
                    currentEraserArea = new FilledArea(point, Color.BLACK, true); // Vytvořit novou oblast mazání
                    Set<Point> initialPixels = getPixelsForEraserSquare(point);
                    currentEraserArea.addPixels(initialPixels);
                    
                    // Vykreslit počáteční bod mazání pro okamžitou zpětnou vazbu
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas); // Vykreslit existující objekty
                    // Dočasně vykreslit aktuální stav oblasti mazání
                    int eraseColor = currentEraserArea.getFillColor().getRGB();
                    for(Point p : currentEraserArea.getFilledPixels()) {
                        if (p.getX() >= 0 && p.getX() < raster.getWidth() && 
                            p.getY() >= 0 && p.getY() < raster.getHeight()) {
                            raster.setPixel(p.getX(), p.getY(), eraseColor);
                        }
                    }
                    panel.repaint();
                    return;
                }
                
                // Zpracování nástroje výplně
                if (currentShapeType == SHAPE_FILL) {
                    FilledArea filledArea = new FilledArea(point, currentColor, false);
                    Set<Point> filledPixels = rasterizer.floodFill(point.getX(), point.getY(), currentColor.getRGB());
                    filledArea.addPixels(filledPixels);
                    canvas.addFilledArea(filledArea);
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                    panel.repaint();
                    return;
                }
                
                // Zpracování polygonu
                if (currentShapeType == 4) { // Polygon
                    if (!isDrawingPolygon) {
                        isDrawingPolygon = true;
                        polygonPoints.clear();
                    }
                    polygonPoints.add(point);
                    if (polygonPoints.size() > 2 && e.getButton() == MouseEvent.BUTTON3) {
                        finishPolygon();
                    }
                    return;
                }
                
                // Zpracování ostatních tvarů
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Hledání nejbližšího bodu pro editaci
                    pointNearest = findNearbyLineEndpoint(point, 10);
                    if (pointNearest != null) {
                        return;
                    }
                    
                    // Hledání tvarů pro editaci
                    selectedCircle = findNearbyCircle(point, 10);
                    if (selectedCircle != null) {
                        fixedCorner = findOppositeCorner(selectedCircle, point);
                        return;
                    }
                    
                    selectedSquare = findNearbySquare(point, 10);
                    if (selectedSquare != null) {
                        fixedCorner = findOppositeCorner(selectedSquare, point);
                        return;
                    }
                    
                    selectedRectangle = findNearbyRectangle(point, 10);
                    if (selectedRectangle != null) {
                        fixedCorner = findOppositeCorner(selectedRectangle, point);
                        return;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (point == null) return;
                
                // Dokončení oblasti mazání
                if (currentShapeType == SHAPE_ERASER && currentEraserArea != null) {
                    canvas.addFilledArea(currentEraserArea);
                    currentEraserArea = null;
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                    panel.repaint();
                    return;
                }
                
                // Dokončení tvaru
                if (currentShapeType < 4 && e.getButton() == MouseEvent.BUTTON1) {
                    Point p2 = new Point(e.getX(), e.getY());
                    if (shiftMode) {
                        p2 = snap45deg(point, p2);
                    }
                    drawShape(point, p2);
                }
                
                point = null;
                pointNearest = null;
                selectedCircle = null;
                selectedSquare = null;
                selectedRectangle = null;
                fixedCorner = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (point == null) return;
                
                // Zpracování oblasti mazání
                if (currentShapeType == SHAPE_ERASER && currentEraserArea != null) {
                    Point currentPoint = new Point(e.getX(), e.getY());
                    Set<Point> newPixels = getPixelsForEraserLine(point, currentPoint);
                    currentEraserArea.addPixels(newPixels);
                    
                    // Aktualizace zobrazení
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                    int eraseColor = currentEraserArea.getFillColor().getRGB();
                    for(Point p : currentEraserArea.getFilledPixels()) {
                        if (p.getX() >= 0 && p.getX() < raster.getWidth() && 
                            p.getY() >= 0 && p.getY() < raster.getHeight()) {
                            raster.setPixel(p.getX(), p.getY(), eraseColor);
                        }
                    }
                    panel.repaint();
                    point = currentPoint;
                    return;
                }
                
                // Zpracování náhledu tvaru
                if (currentShapeType < 4) {
                    Point p2 = new Point(e.getX(), e.getY());
                    if (shiftMode) {
                        p2 = snap45deg(point, p2);
                    }
                    previewShape(point, p2);
                }
                
                // Zpracování náhledu polygonu
                if (currentShapeType == 4 && isDrawingPolygon) {
                    drawPolygonPreview(new Point(e.getX(), e.getY()));
                }
                
                // Zpracování přesunu bodů
                if (pointNearest != null) {
                    pointNearest.setX(e.getX());
                    pointNearest.setY(e.getY());
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                    panel.repaint();
                }
                
                // Zpracování přesunu tvarů
                if (selectedCircle != null && fixedCorner != null) {
                    selectedCircle.setCenter(new Point(e.getX(), e.getY()));
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                    panel.repaint();
                }
                
                if (selectedSquare != null && fixedCorner != null) {
                    selectedSquare.setP2(new Point(e.getX(), e.getY()));
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                    panel.repaint();
                }
                
                if (selectedRectangle != null && fixedCorner != null) {
                    selectedRectangle.setP2(new Point(e.getX(), e.getY()));
                    raster.clear();
                    rasterizer.rasterizeCanvas(canvas);
                    panel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (isDrawingPolygon) {
                    drawPolygonPreview(new Point(e.getX(), e.getY()));
                }
            }
        };

        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftMode = true;
                    if (point != null) {
                        Point currentPoint = new Point(point.getX(), point.getY());
                        Point snappedPoint = snap45deg(point, currentPoint);
                        previewShape(point, snappedPoint);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftMode = false;
                    if (point != null) {
                        Point currentPoint = new Point(point.getX(), point.getY());
                        previewShape(point, currentPoint);
                    }
                }
            }
        };
    }

    /**
     * Vykreslí tvar mezi dvěma body
     */
    private void drawShape(Point p1, Point p2) {
        switch (currentShapeType) {
            case 0: // Čára
                canvas.add(new Line(p1, p2, currentColor, currentLineWidth, currentLineStyle));
                break;
            case 1: // Kružnice
                int radius = (int) Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
                canvas.addCircle(new Circle(p1, radius, currentColor, currentLineWidth));
                break;
            case 2: // Čtverec
                int size = Math.max(Math.abs(p2.getX() - p1.getX()), Math.abs(p2.getY() - p1.getY()));
                int signX = (p2.getX() - p1.getX() >= 0) ? 1 : -1;
                int signY = (p2.getY() - p1.getY() >= 0) ? 1 : -1;
                Point p2Square = new Point(p1.getX() + size * signX, p1.getY() + size * signY);
                canvas.addSquare(new Square(p1, p2Square, currentColor, currentLineWidth, currentLineStyle));
                break;
            case 3: // Obdélník
                canvas.addRectangle(new Rectangle(p1, p2, currentColor, currentLineWidth, currentLineStyle));
                break;
        }
        raster.clear();
        rasterizer.rasterizeCanvas(canvas);
        panel.repaint();
    }

    /**
     * Zobrazí náhled tvaru mezi dvěma body
     */
    private void previewShape(Point p1, Point p2) {
        raster.clear();
        rasterizer.rasterizeCanvas(canvas);
        
        switch (currentShapeType) {
            case 0: // Čára
                rasterizer.rasterizePreview(new Line(p1, p2, currentColor, currentLineWidth, currentLineStyle));
                break;
            case 1: // Kružnice
                int radius = (int) Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
                Circle circle = new Circle(p1, radius, currentColor, currentLineWidth);
                rasterizer.rasterizeSingleShape(circle);
                break;
            case 2: // Čtverec
                int size = Math.max(Math.abs(p2.getX() - p1.getX()), Math.abs(p2.getY() - p1.getY()));
                int signX = (p2.getX() - p1.getX() >= 0) ? 1 : -1;
                int signY = (p2.getY() - p1.getY() >= 0) ? 1 : -1;
                Point p2Square = new Point(p1.getX() + size * signX, p1.getY() + size * signY);
                Square square = new Square(p1, p2Square, currentColor, currentLineWidth, currentLineStyle);
                rasterizer.rasterizeSingleShape(square);
                break;
            case 3: // Obdélník
                Rectangle rect = new Rectangle(p1, p2, currentColor, currentLineWidth, currentLineStyle);
                rasterizer.rasterizeSingleShape(rect);
                break;
        }
        panel.repaint();
    }

    /**
     * Dokončí kreslení polygonu
     */
    private void finishPolygon() {
        if (polygonPoints.size() < 3) return;
        
        // Vytvoření čar pro polygon
        for (int i = 0; i < polygonPoints.size(); i++) {
            Point p1 = polygonPoints.get(i);
            Point p2 = polygonPoints.get((i + 1) % polygonPoints.size());
            canvas.add(new Line(p1, p2, currentColor, currentLineWidth, currentLineStyle));
        }
        
        isDrawingPolygon = false;
        polygonPoints.clear();
        raster.clear();
        rasterizer.rasterizeCanvas(canvas);
        panel.repaint();
    }

    /**
     * Najde nejbližší kružnici k danému bodu
     */
    private Circle findNearbyCircle(Point clickPoint, double threshold) {
        for (Circle circle : canvas.getCircles()) {
            double distance = Math.sqrt(Math.pow(clickPoint.getX() - circle.getCenter().getX(), 2) + 
                                      Math.pow(clickPoint.getY() - circle.getCenter().getY(), 2));
            if (distance <= threshold) {
                return circle;
            }
        }
        return null;
    }

    /**
     * Najde nejbližší koncový bod čáry k danému bodu
     */
    private Point findNearbyLineEndpoint(Point clickPoint, double threshold) {
        for (Line line : canvas.getLines()) {
            double distance1 = Math.sqrt(Math.pow(clickPoint.getX() - line.getPoint1().getX(), 2) + 
                                       Math.pow(clickPoint.getY() - line.getPoint1().getY(), 2));
            double distance2 = Math.sqrt(Math.pow(clickPoint.getX() - line.getPoint2().getX(), 2) + 
                                       Math.pow(clickPoint.getY() - line.getPoint2().getY(), 2));
            if (distance1 <= threshold) return line.getPoint1();
            if (distance2 <= threshold) return line.getPoint2();
        }
        return null;
    }

    /**
     * Najde nejbližší čtverec k danému bodu
     */
    private Square findNearbySquare(Point clickPoint, double threshold) {
        for (Square square : canvas.getSquares()) {
            Point p1 = square.getP1();
            double distance = Math.sqrt(Math.pow(clickPoint.getX() - p1.getX(), 2) + 
                                      Math.pow(clickPoint.getY() - p1.getY(), 2));
            if (distance <= threshold) {
                return square;
            }
        }
        return null;
    }

    /**
     * Najde nejbližší obdélník k danému bodu
     */
    private Rectangle findNearbyRectangle(Point clickPoint, double threshold) {
        for (Rectangle rect : canvas.getRectangles()) {
            Point p1 = rect.getP1();
            double distance = Math.sqrt(Math.pow(clickPoint.getX() - p1.getX(), 2) + 
                                      Math.pow(clickPoint.getY() - p1.getY(), 2));
            if (distance <= threshold) {
                return rect;
            }
        }
        return null;
    }

    /**
     * Najde protilehlý roh tvaru k danému bodu
     */
    private Point findOppositeCorner(Object shape, Point clickPoint) {
        if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            return circle.getCenter();
        } else if (shape instanceof Square) {
            Square square = (Square) shape;
            return square.getP1();
        } else if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;
            return rect.getP1();
        }
        return null;
    }

    /**
     * Zarovná bod na úhel 45° vůči druhému bodu
     */
    public static Point snap45deg(Point point1, Point point2) {
        int dx = point2.getX() - point1.getX();
        int dy = point2.getY() - point1.getY();
        double angle = Math.atan2(dy, dx);
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Zaokrouhlení na nejbližší násobek 45°
        angle = Math.round(angle / (Math.PI / 4)) * (Math.PI / 4);
        
        int newX = point1.getX() + (int)(distance * Math.cos(angle));
        int newY = point1.getY() + (int)(distance * Math.sin(angle));
        
        return new Point(newX, newY);
    }

    /**
     * Zobrazí náhled polygonu
     */
    private void drawPolygonPreview(Point currentMousePos) {
        raster.clear();
        rasterizer.rasterizeCanvas(canvas);
        
        if (polygonPoints.size() > 0) {
            // Vykreslení již nakreslených čar
            for (int i = 0; i < polygonPoints.size() - 1; i++) {
                Line line = new Line(polygonPoints.get(i), polygonPoints.get(i + 1), 
                                   currentColor, currentLineWidth, currentLineStyle);
                rasterizer.rasterizePreview(line);
            }
            
            // Vykreslení náhledu poslední čáry
            Line previewLine = new Line(polygonPoints.get(polygonPoints.size() - 1), 
                                      currentMousePos, currentColor, currentLineWidth, currentLineStyle);
            rasterizer.rasterizePreview(previewLine);
        }
        
        panel.repaint();
    }

    /**
     * Vrátí pixely pro čtvercovou gumu
     */
    private Set<Point> getPixelsForEraserSquare(Point center) {
        Set<Point> pixels = new HashSet<>();
        int size = currentLineWidth * 2 + 1;
        int halfSize = size / 2;
        
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                pixels.add(new Point(center.getX() + x, center.getY() + y));
            }
        }
        
        return pixels;
    }

    /**
     * Vrátí pixely pro čáru gumy
     */
    private Set<Point> getPixelsForEraserLine(Point p1, Point p2) {
        Set<Point> pixels = new HashSet<>();
        int dx = Math.abs(p2.getX() - p1.getX());
        int dy = Math.abs(p2.getY() - p1.getY());
        int sx = p1.getX() < p2.getX() ? 1 : -1;
        int sy = p1.getY() < p2.getY() ? 1 : -1;
        int err = dx - dy;
        
        while (true) {
            pixels.addAll(getPixelsForEraserSquare(new Point(p1.getX(), p1.getY())));
            
            if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) break;
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                p1.setX(p1.getX() + sx);
            }
            if (e2 < dx) {
                err += dx;
                p1.setY(p1.getY() + sy);
            }
        }
        
        return pixels;
    }
}