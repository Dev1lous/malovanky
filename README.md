# Grafický Editor

Tento program je grafický editor, který umožňuje kreslení různých tvarů s možností jejich úpravy. Program je napsán v Javě a využívá Swing pro grafické rozhraní.

## Hlavní funkce

### Kreslení tvarů
Program podporuje kreslení následujících tvarů:
- Čára
- Kružnice
- Čtverec
- Obdélník
- Polygon

### Úpravy tvarů
- Možnost přesunu bodů (vrcholů) pomocí pravého tlačítka myši
- Úprava velikosti tvarů tažením rohů
- Možnost mazání tvarů pomocí gumy

### Vlastnosti čar
- **Typ čáry:**
  - Plná čára (SOLID)
  - Přerušovaná čára (DASHED)
  - Tečkovaná čára (DOTTED)
- **Tloušťka čáry:** 1-20 pixelů
- **Barva:** Volitelná barva pro všechny tvary

### Speciální nástroje
- **Guma:** Nástroj pro mazání částí kresby
- **Výplň:** Nástroj pro vyplnění uzavřených oblastí vybranou barvou

## Návod k použití

1. **Výběr tvaru**
   - V horní liště vyberte požadovaný tvar (čára, kružnice, čtverec, obdélník, polygon)
   - Pro polygon klikněte levým tlačítkem myši pro vytvoření bodů
   - Pro dokončení polygonu klikněte pravým tlačítkem myši

2. **Nastavení vlastností**
   - Vyberte barvu pomocí tlačítka "Barva"
   - Nastavte tloušťku čáry pomocí číselníku
   - Zvolte styl čáry (plná, přerušovaná, tečkovaná)

3. **Kreslení**
   - Levým tlačítkem myši začněte kreslit tvar
   - Tažením myši upravte velikost tvaru
   - Pro zarovnání na 45° úhly podržte klávesu SHIFT

4. **Úpravy tvarů**
   - Pravým tlačítkem myši klikněte na bod tvaru
   - Tažením upravte pozici bodu
   - Pro úpravu velikosti tvaru tahejte za rohy

5. **Mazání**
   - Vyberte nástroj "Guma"
   - Tažením myši mažte části kresby
   - Pro výplň oblasti vyberte nástroj "Výpln" a klikněte do uzavřené oblasti

## Klávesové zkratky
- `C` - Vymazat celou kresbu
- `SHIFT` - Zarovnání na 45° úhly
- `ESC` - Zrušení kreslení polygonu

## Technické detaily

### Vykreslování
Program používá vlastní rasterizátory pro vykreslování tvarů:
- `LineRasterizerTrivial` - pro plné čáry
- `DashRasterizer` - pro přerušované čáry
- `DotRasterizer` - pro tečkované čáry

### Správa tvarů
Všechny tvary jsou uloženy v `LineCanvas`, který spravuje:
- Seznam čar
- Seznam kružnic
- Seznam čtverců
- Seznam obdélníků
- Seznam vyplněných oblastí

### Úpravy tvarů
Program umožňuje:
- Přesun bodů tvarů
- Změnu velikosti tvarů
- Úpravu vlastností čar (barva, tloušťka, styl) 

### Editování čar
Program poskytuje pokročilé možnosti editace čar:
- Přesun celé čáry tažením
- Změna délky čáry tažením koncových bodů
- Rotace čáry kolem středového bodu
- Změna úhlu čáry s možností zarovnání na 45° úhly (při podržení SHIFT)

### Vlastnosti čar
Podrobnější informace o vlastnostech čar:
- **Barva čáry:**
  - Výběr z palety barev
  - Možnost nastavení průhlednosti (alpha kanál)
  - Uložení vlastních barev
- **Tloušťka čáry:**
  - Plynulá změna tloušťky od 1 do 20 pixelů
  - Okamžitá aktualizace při změně
  - Zachování tloušťky při změně velikosti tvaru
- **Styl čáry:**
  - Plná čára (SOLID) - standardní vykreslení
  - Přerušovaná čára (DASHED) - střídání úseků
  - Tečkovaná čára (DOTTED) - pravidelné tečky
  - Možnost nastavení délky úseků a mezer

### Správa kresby
- Automatické ukládání historie změn
- Možnost vrácení změn (undo)
- Export kresby do různých formátů
- Import existujících kreseb
- Vrstvení objektů s možností změny pořadí 

### Technické detaily implementace

#### Hlavní třídy a jejich metody

##### App.java
- **Hlavní metody:**
  - `main(String[] args)` - Vstupní bod aplikace, spouští GUI
  - `App(int width, int height)` - Konstruktor inicializující okno a komponenty
  - `createToolBar()` - Vytváří nástrojovou lištu s ovládacími prvky
  - `createAdapters()` - Nastavuje posluchače událostí myši a klávesnice
  - `drawShape(Point p1, Point p2)` - Vykresluje tvary na plátno
  - `previewShape(Point p1, Point p2)` - Zobrazuje náhled tvaru při kreslení
  - `finishPolygon()` - Dokončuje a vykresluje polygon
  - `findNearbyCircle/LineEndpoint/Square/Rectangle()` - Pomocné metody pro detekci tvarů při editaci

##### LineCanvas.java
- **Správa tvarů:**
  - `add(Line line)` - Přidá čáru do plátna
  - `addCircle(Circle circle)` - Přidá kružnici
  - `addRectangle(Rectangle rect)` - Přidá obdélník
  - `addSquare(Square square)` - Přidá čtverec
  - `addFilledArea(FilledArea area)` - Přidá vyplněnou oblast
  - `clear()` - Vymaže všechny tvary z plátna

##### LineCanvasRasterizer.java
- **Vykreslování:**
  - `rasterizeCanvas(LineCanvas canvas)` - Vykreslí celé plátno
  - `rasterizeSingleShape(Object shape)` - Vykreslí jednotlivý tvar
  - `rasterizePreview(Line line)` - Vykreslí náhled čáry
  - `rasterizeCircle/Rectangle/Square()` - Metody pro vykreslování specifických tvarů
  - `floodFill(int startX, int startY, int fillColor)` - Implementace výplně oblasti

##### Rasterizátory čar
- **LineRasterizerTrivial:**
  - `rasterize(Line line)` - Vykresluje plné čáry pomocí Bresenhamova algoritmu
  - Podporuje tloušťku čáry a barvu

- **DashedLineRasterizer:**
  - Implementuje přerušované čáry s nastavitelnou délkou úseků
  - Používá konstanty `DASH_LENGTH` a `GAP_LENGTH`

#### Implementace algoritmů

##### Bresenhamův algoritmus
- Použit pro vykreslování čar v `LineRasterizerTrivial`
- Optimalizovaná verze pro různé směry čar
- Podpora tloušťky čáry pomocí vyplňování čtverců kolem každého bodu

##### Flood Fill
- Implementace v `LineCanvasRasterizer`
- Používá zásobníkový přístup pro efektivní vyplňování
- Podporuje různé barvy a detekci hranic

##### Detekce tvarů
- Metody `findNearby*` v `App.java`
- Používají výpočet vzdálenosti pro detekci blízkých tvarů
- Optimalizované pro rychlé vyhledávání v kolekcích tvarů

#### Správa událostí
- **MouseAdapter:**
  - `mousePressed()` - Zpracování kliknutí pro začátek kreslení
  - `mouseReleased()` - Dokončení kreslení tvaru
  - `mouseDragged()` - Aktualizace náhledu při tažení
  - `mouseMoved()` - Aktualizace náhledu polygonu

- **KeyAdapter:**
  - `keyPressed()` - Zpracování klávesových zkratek
  - `keyReleased()` - Resetování stavu kláves (např. SHIFT)
