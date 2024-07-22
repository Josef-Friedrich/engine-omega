/*
 * Engine Pi ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2024 Josef Friedrich and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.pirckheimer_gymnasium.engine_pi.debug;

import de.pirckheimer_gymnasium.engine_pi.Camera;
import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.annotations.Internal;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Zeichnet das <b>Koordinatensystem</b>.
 */
public final class CoordinateSystemDrawer
{
    private static final int GRID_SIZE_IN_PIXELS = 160;

    private static final int GRID_SIZE_METER_LIMIT = 100000;

    private static final int DEBUG_TEXT_SIZE = 12;

    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN,
            DEBUG_TEXT_SIZE);

    private static final Color COLOR = new Color(255, 255, 255, 150);

    /**
     * Das {@link Graphics2D}-Objekt, in das gezeichnet werden soll.
     */
    private final Graphics2D g;

    AffineTransform pre;

    /**
     * Die Kameraposition.
     */
    private final Vector position;

    /**
     * Wie viele Pixel ein Meter misst.
     */
    private final double pixelPerMeter;

    /**
     * Wie viele Meter ein Kästchen im Gitter groß sein soll.
     */
    private final int gridSizeInMeters;

    /**
     * Wie viele Pixel ein Kästchen im Gitter groß sein soll.
     */
    private final double gridSizeInPixels;

    private final double gridSizeFactor;

    /**
     * Die maximale Ausdehnung des Spielfelds in Pixel.
     */
    private final int windowSizeInPixels;

    /**
     * Zeichnet das <b>Koordinatensystem</b>.
     *
     * @param g      Das {@link Graphics2D}-Objekt, in das gezeichnet werden
     *               soll.
     * @param scene  Die Szene, über die das Koordinatensystem gezeichnet werden
     *               soll.
     * @param width  Die Breite des Spielfelds in Pixel.
     * @param height Die Höhe des Spielfelds in Pixel.
     */
    public CoordinateSystemDrawer(Graphics2D g, Scene scene, int width,
            int height)
    {
        this.g = g;
        pre = g.getTransform();
        Camera camera = scene.getCamera();
        position = camera.getPosition();
        double rotation = -camera.getRotation();
        g.setClip(0, 0, width, height);
        g.translate(width / 2, height / 2);
        pixelPerMeter = camera.getMeter();
        g.rotate(Math.toRadians(rotation), 0, 0);
        g.translate(-position.getX() * pixelPerMeter,
                position.getY() * pixelPerMeter);
        gridSizeInMeters = (int) Math
                .round(GRID_SIZE_IN_PIXELS / pixelPerMeter);
        gridSizeInPixels = gridSizeInMeters * pixelPerMeter;
        gridSizeFactor = gridSizeInPixels / gridSizeInMeters;
        windowSizeInPixels = Math.max(width, height);
    }

    /**
     * @param value Der x- oder y-Wert
     */
    private int getLineThickness(int value)
    {
        return value == 0 ? 3 : 1;
    }

    /**
     * Zeichnet die horizontalen Linien.
     */
    private void drawHorizontalLines(int startY, int stopY, int startX)
    {
        for (int y = startY; y <= stopY; y += gridSizeInMeters)
        {
            g.fillRect((int) ((startX - 1) * gridSizeFactor),
                    (int) (y * gridSizeFactor - 1),
                    (int) (windowSizeInPixels + 3 * gridSizeInPixels),
                    getLineThickness(y));
        }
    }

    /**
     * Zeichnet die vertikalen Linien.
     */
    private void drawVerticalLines(int startX, int stopX, int startY)
    {
        for (int x = startX; x <= stopX; x += gridSizeInMeters)
        {
            g.fillRect((int) (x * gridSizeFactor) - 1,
                    (int) ((startY - 1) * gridSizeFactor), getLineThickness(x),
                    (int) (windowSizeInPixels + 3 * gridSizeInPixels));
        }
    }

    /**
     * Zeichnet das <b>Koordinatensystem</b>.
     */
    @Internal
    public void draw()
    {
        if (gridSizeInMeters > 0 && gridSizeInMeters < GRID_SIZE_METER_LIMIT)
        {
            int startX = (int) (position.getX()
                    - windowSizeInPixels / 2.0 / pixelPerMeter);
            int startY = (int) ((-1 * position.getY())
                    - windowSizeInPixels / 2.0 / pixelPerMeter);
            startX -= (startX % gridSizeInMeters) + gridSizeInMeters;
            startY -= (startY % gridSizeInMeters) + gridSizeInMeters;
            startX -= gridSizeInMeters;
            int stopX = (int) (startX + windowSizeInPixels / pixelPerMeter
                    + gridSizeInMeters * 2);
            int stopY = (int) (startY + windowSizeInPixels / pixelPerMeter
                    + gridSizeInMeters * 2);
            g.setFont(FONT);
            // Setzen der Gitterfarbe
            g.setColor(COLOR);
            drawVerticalLines(startX, stopX, startY);
            drawHorizontalLines(startY, stopY, startX);
            for (int y = startY; y <= stopY; y += gridSizeInMeters)
            {
                g.drawString(-y + "", (int) (0 * gridSizeFactor + 5),
                        (int) (y * gridSizeFactor - 5));
            }
            for (int x = startX; x <= stopX; x += gridSizeInMeters)
            {
                for (int y = startY; y <= stopY; y += gridSizeInMeters)
                {
                    g.drawString(x + " | " + -y, (int) (x * gridSizeFactor + 5),
                            (int) (y * gridSizeFactor - 5));
                }
            }
        }
        g.setTransform(pre);
    }

    public static void main(String[] args)
    {
        Game.debug();
        Game.start();
    }
}
