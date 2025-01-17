/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha/src/main/java/ea/actor/TileContainer.java
 *
 * Engine Pi ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2024 Michael Andonie and contributors.
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
package de.pirckheimer_gymnasium.engine_pi.actor;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import de.pirckheimer_gymnasium.engine_pi.annotations.API;
import de.pirckheimer_gymnasium.engine_pi.annotations.Internal;
import de.pirckheimer_gymnasium.engine_pi.physics.FixtureBuilder;

/**
 * Ein <code>TileContainer</code> ist eine schachbrettartige Anordnung
 * rechteckiger
 * <a href="https://de.wikipedia.org/wiki/Tiling_(Computer)">Tiles</a>.
 *
 * @author Michael Andonie
 */
public class TileRegistration extends Actor implements TileMap
{
    /**
     * Die IDs der aktuellen Tiles des Containers.
     */
    private final Tile[][] tiles;

    /**
     * Die Breite eines Tiles (original) in px.
     */
    private final double tileWidth;

    /**
     * Die Höhe eines Tiles (original) in px.
     */
    private final double tileHeight;

    /**
     * Erstellt einen <b>leeren</b> Tile-Container. Er ist erst "sichtbar", wenn
     * Tiles gesetzt werden.
     *
     * @param numX Die Anzahl an Tiles in X-Richtung.
     * @param numY Die Anzahl an Tiles in Y-Richtung.
     * @param tileWidth Die Breite eines Tiles in Meter.
     * @param tileHeight Die Höhe eines Tiles in Meter.
     *
     * @see #setTile(int, int, Tile)
     */
    @API
    public TileRegistration(int numX, int numY, double tileWidth,
            double tileHeight)
    {
        super(() -> FixtureBuilder.rectangle(tileWidth * numX,
                tileHeight * numY));
        if (numX <= 0 || numY <= 0)
        {
            throw new IllegalArgumentException(
                    "numX und numY müssen jeweils > 0 sein.");
        }
        if (tileWidth <= 0 || tileHeight <= 0)
        {
            throw new IllegalArgumentException(
                    "Breite und Höhe der Tiles müssen jeweils > 0 sein.");
        }
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new Tile[numX][numY];
    }

    public int getTileCountX()
    {
        return tiles.length;
    }

    public int getTileCountY()
    {
        return tiles[0].length;
    }

    /**
     * Erstellt einen <b>leeren</b> Tile-Container für quadratische Tiles. Er
     * ist erst "sichtbar", wenn Tiles gesetzt werden.
     *
     * @param numX Die Anzahl an Tiles in X-Richtung.
     * @param numY Die Anzahl an Tiles in Y-Richtung.
     * @param tileSize Die Höhe <b>und</b> Breite eines Tiles in Pixel.
     *
     * @see #setTile(int, int, Tile)
     */
    @API
    public TileRegistration(int numX, int numY, double tileSize)
    {
        this(numX, numY, tileSize, tileSize);
    }

    /**
     * Setzt das Tile an einer festen Position durch eine klare Bilddatei.
     *
     * @param x Der X-Index für das neu zu setzende Tile.
     * @param y Der Y-Index für das neu zu setzende Tile.
     * @param tile Das neue Tile. Bei <code>null</code> wird das entsprechende
     *     Tile leer.
     */
    @API
    public void setTile(int x, int y, Tile tile)
    {
        tiles[x][y] = tile;
    }

    /**
     * Zeichnet die Figur an der Position {@code (0|0)} mit der Rotation
     * {@code 0}.
     *
     * @param g Das {@link Graphics2D}-Objekt, in das gezeichnet werden soll.
     * @param pixelPerMeter Gibt an, wie viele Pixel ein Meter misst.
     */
    @Internal
    @Override
    public void render(Graphics2D g, double pixelPerMeter)
    {
        final AffineTransform ore = g.getTransform();
        double offset = tiles[0].length * tileHeight * pixelPerMeter;
        g.translate(0, -offset);
        for (int x = 0; x < tiles.length; x++)
        {
            for (int y = 0; y < tiles[x].length; y++)
            {
                if (tiles[x][y] == null)
                {
                    continue;
                }
                double tx = tileWidth * x * pixelPerMeter;
                double ty = tileHeight * y * pixelPerMeter;
                g.translate(tx, ty);
                tiles[x][y].render(g, tileWidth * pixelPerMeter,
                        tileHeight * pixelPerMeter);
                g.translate(-tx, -ty);
            }
        }
        g.setTransform(ore);
    }

    @Override
    public Tile getTile(int x, int y)
    {
        return tiles[x][y];
    }
}
