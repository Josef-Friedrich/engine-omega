package de.pirckheimer_gymnasium.engine_pi.demos.resources;

import java.awt.Color;
import java.awt.event.KeyEvent;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Actor;
import de.pirckheimer_gymnasium.engine_pi.actor.Polygon;
import de.pirckheimer_gymnasium.engine_pi.actor.Text;
import de.pirckheimer_gymnasium.engine_pi.event.KeyStrokeListener;
import de.pirckheimer_gymnasium.engine_pi.resources.ColorSchemeSelection;

/**
 * https://commons.wikimedia.org/wiki/File:Farbkreis_Itten_1961.svg
 */
public class ColorWheelIttenDemo extends Scene implements KeyStrokeListener
{
    private final int NUMBER_SEGMENTS = 12;

    private final double SEGMENT_ANGLE = 360 / NUMBER_SEGMENTS;

    private final double HALF_SEGMENT_ANGLE = SEGMENT_ANGLE / 2.0;

    private final double OUTER_RADIUS = 7.0;

    private final double INNER_RADIUS = 5.0;

    private final Actor[] WHEEL_AREAS;

    private final Actor[] PRIMARY_AREAS;

    private final Actor[] SECONDARY_AREAS;

    private final ColorSchemeSelection[] COLOR_SCHEMES = ColorSchemeSelection
            .values();

    private int currentColorScheme = -1;

    private final Text NAME;

    public ColorWheelIttenDemo()
    {
        WHEEL_AREAS = drawWheelColors();
        // Zuerst Primär, denn die müssen übermalt werden.
        PRIMARY_AREAS = drawPrimaryColors();
        SECONDARY_AREAS = drawSecondaryColors();
        NAME = createText("", 5, 6);
        NAME.setColor("white");
        setNextColorScheme();
    }

    /**
     * Berechnet einen Punkt auf der Kreislinie.
     *
     * @param radius Der Radius des Kreises.
     * @param angle  Der Winkel, der die Lage des Punktes angibt (0 = rechts, 90
     *               = oben, 180 = links, 270 = unten).
     *
     * @return Ein Punkt, der auf der Kreislinie liegt.
     *
     */
    private Vector getCirclePoint(double radius, double angle)
    {
        return Vector.ofAngle(angle).multiply(radius);
    }

    /**
     * Ein farbiges Segment in der Form eines Trapezes mit einer der zwölf
     * Farben des Farbkreises von Itten. Zwölf Segmente ergeben einen Kreis.
     *
     * @param index Der Farbindex. 0 = gelb
     * @param angle Der Winkel deutet auf die Mitte des Segments.
     */
    private Actor createWheelArea(int index, double angle)
    {
        // Erster Winkel
        double start = angle - HALF_SEGMENT_ANGLE;
        // Zweiter Winkel
        double end = angle + HALF_SEGMENT_ANGLE;
        Polygon polygon = new Polygon(getCirclePoint(OUTER_RADIUS, start),
                getCirclePoint(INNER_RADIUS, start),
                getCirclePoint(INNER_RADIUS, end),
                getCirclePoint(OUTER_RADIUS, end));
        add(polygon);
        return polygon;
    }

    /**
     * alle 12 Farben
     */
    private Actor[] drawWheelColors()
    {
        Actor[] areas = new Actor[NUMBER_SEGMENTS];
        for (int i = 0; i < NUMBER_SEGMENTS; i++)
        {
            double angle = (i * SEGMENT_ANGLE * -1) + 90;
            Vector textPosition = getCirclePoint(7.5, angle);
            createText(i + "", 0.5, textPosition.getX(), textPosition.getY())
                    .setColor("weiß");
            areas[i] = createWheelArea(i, angle);
        }
        return areas;
    }

    /**
     * die 3 Sekundärfarben
     */
    private Actor[] drawSecondaryColors()
    {
        Actor[] areas = new Actor[3];
        // 90 Grad ist oben
        int START_ANGLE = 90;
        // 0, 4, 8 -> erste Ecke des Dreiecks
        for (int i = 0; i < NUMBER_SEGMENTS; i += 4)
        {
            double radius = INNER_RADIUS - 0.2;
            int angle = START_ANGLE - (i * 30);
            // Zeichnen des Dreiecks
            Polygon area = new Polygon(getCirclePoint(radius, angle),
                    getCirclePoint(radius, angle - 60),
                    getCirclePoint(radius, angle - 120));
            add(area);
            areas[i / 4] = area;
        }
        return areas;
    }

    /**
     * die 3 Pimärfarben
     */
    private Actor[] drawPrimaryColors()
    {
        Actor[] areas = new Actor[3];
        // 90 Grad ist oben
        int START_ANGLE = 90;
        // 0, 4, 8 -> Spitze
        for (int i = 0; i < NUMBER_SEGMENTS; i += 4)
        {
            double radius = INNER_RADIUS - 0.2;
            int angle = START_ANGLE - (i * 30);
            // Zeichnen eines Vierecks
            Polygon area = new Polygon(getCirclePoint(radius, angle + 60),
                    getCirclePoint(radius, angle),
                    getCirclePoint(radius, angle - 60), new Vector(0, 0));
            add(area);
            areas[i / 4] = area;
        }
        return areas;
    }

    private ColorSchemeSelection getNextColorScheme()
    {
        currentColorScheme++;
        if (currentColorScheme >= COLOR_SCHEMES.length)
        {
            currentColorScheme = 0;
        }
        return COLOR_SCHEMES[currentColorScheme];
    }

    private void setColorScheme(ColorSchemeSelection selection)
    {
        NAME.setContent(selection.name());
        var scheme = selection.getScheme();
        int i = 0;
        for (Color color : scheme.getWheelColors())
        {
            WHEEL_AREAS[i].setColor(color);
            i++;
        }
        i = 0;
        for (Color color : scheme.getPrimaryColors())
        {
            PRIMARY_AREAS[i].setColor(color);
            i++;
        }
        i = 0;
        for (Color color : scheme.getSecondaryColors())
        {
            SECONDARY_AREAS[i].setColor(color);
            i++;
        }
    }

    private void setNextColorScheme()
    {
        setColorScheme(getNextColorScheme());
    }

    @Override
    public void onKeyDown(KeyEvent event)
    {
        setNextColorScheme();
    }

    public static void main(String[] args)
    {
        Game.start(520, 520, new ColorWheelIttenDemo());
    }
}
