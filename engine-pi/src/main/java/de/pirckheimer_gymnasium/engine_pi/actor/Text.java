/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha/src/main/java/ea/actor/Text.java
 *
 * Engine Pi ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2019 Michael Andonie and contributors.
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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.annotations.API;
import de.pirckheimer_gymnasium.engine_pi.annotations.Internal;
import de.pirckheimer_gymnasium.engine_pi.physics.FixtureBuilder;
import de.pirckheimer_gymnasium.engine_pi.physics.FixtureData;
import de.pirckheimer_gymnasium.engine_pi.util.FontMetrics;
import de.pirckheimer_gymnasium.engine_pi.Resources;

/**
 * Zur Darstellung von Texten im Programmbildschirm.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public class Text extends Geometry
{
    // Needs to be large enough, so we don't have rounding errors due to integers
    // in font metrics
    private static final int SIZE = 1000;

    @Internal
    private static FixtureData createShape(String content, double height,
            Font font)
    {
        Vector sizeInPixels = FontMetrics.getSize(content, font);
        return FixtureBuilder.rectangle(
                sizeInPixels.getX() * height / sizeInPixels.getY(), height);
    }

    /**
     * Höhe des Textes.
     */
    private double height;

    /**
     * Die Schriftart (<b>fett, kursiv, oder fett & kursiv</b>).<br>
     * Dies wird dargestellt als int. Wert:<br>
     * 0: Normaler Text<br>
     * 1: Fett<br>
     * 2: Kursiv<br>
     * 3: Fett & Kursiv
     */
    private int fontStyle;

    /**
     * Der Wert des Textes
     */
    private String content;

    /**
     * Der Font der Darstellung
     */
    private Font font;

    private transient int cachedDescent;

    private transient double cachedScaleFactor;

    /**
     * Konstruktor für Objekte der Klasse Text<br>
     * Möglich ist es auch, Fonts zu laden, die im Projektordner sind. Diese
     * werden zu Anfang einmalig geladen und stehen dauerhaft zur Verfügung.
     *
     * @param content  Die Zeichenkette, die dargestellt werden soll
     * @param fontName Der Name des zu verwendenden Fonts.<br>
     *                 Wird hierfür ein Font verwendet, der in dem Projektordner
     *                 vorhanden sein soll, <b>und dies ist immer und in jedem
     *                 Fall zu empfehlen</b>, muss der Name der Schriftart hier
     *                 ebenfalls einfach nur eingegeben werden, <b>nicht der
     *                 Name der schriftart-Datei!</b>
     * @param height   Die Breite
     * @param style    Die Schriftart dieses Textes. Folgende Werte entsprechen
     *                 folgendem:<br>
     *                 0: Normaler Text<br>
     *                 1: Fett<br>
     *                 2: Kursiv<br>
     *                 3: Fett &amp; Kursiv <br>
     *                 <br>
     *                 Alles andere sorgt nur für einen normalen Text.
     */
    @API
    public Text(String content, double height, String fontName, int style)
    {
        super(() -> createShape(content == null ? "" : content, height,
                Resources.FONTS.get(fontName).deriveFont(style, SIZE)));
        this.content = content == null ? "" : content;
        this.height = height;
        setStyle(style);
        setFont(fontName);
        setColor("black");
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und Font. Der Text ist in
     * Schriftgröße 12, nicht fett, nicht kursiv.
     *
     * @param content  Der Inhalt, der dargestellt wird
     * @param height   Die Höhe in Meter.
     * @param fontName Der Font, in dem der Text dargestellt werden soll.
     */
    @API
    public Text(String content, double height, String fontName)
    {
        this(content, height, fontName, 0);
    }

    /**
     * Erstellt einen Text mit spezifischem Inhalt und spezifischer Größe. Die
     * Schriftart ist ein Standard-Font (Serifenfrei), nicht fett, nicht kursiv.
     *
     * @param content Der Inhalt, der dargestellt wird
     * @param height  Die Höhe in Meter.
     */
    @API
    public Text(String content, double height)
    {
        this(content, height, Font.SANS_SERIF, 0);
    }

    /**
     * Setzt eine neue Schriftart für den Text.
     *
     * @param fontName Name des neuen Fonts für den Text
     */
    @API
    public void setFont(String fontName)
    {
        this.setFont(Resources.FONTS.get(fontName));
    }

    @API
    public void setFont(Font font)
    {
        this.font = font.deriveFont(fontStyle, SIZE);
        this.update();
    }

    @API
    public Font getFont()
    {
        return font;
    }

    /**
     * Setzt den Inhalt des Textes.
     *
     * @param content Der neue Inhalt des Textes.
     */
    @API
    public void setContent(String content)
    {
        String normalizedContent = content;
        if (normalizedContent == null)
        {
            normalizedContent = "";
        }
        if (!this.content.equals(normalizedContent))
        {
            this.content = normalizedContent;
            update();
        }
    }

    /**
     * Setzt den Inhalt des Textes durch Angabe eines beliebigen Datentyps.
     *
     * @param content Der neue Inhalt des Textes in einem beliebigen Datentyp.
     */
    @API
    public void setContent(Object content)
    {
        setContent(String.valueOf(content));
    }

    @API
    public String getContent()
    {
        return content;
    }

    /**
     * Setzt den Stil der Schriftart (Fett/Kursiv/Fett&amp;Kursiv/Normal).
     *
     * @param style Die Repräsentation der Schriftart als Zahl:<br>
     *              0: Normaler Text<br>
     *              1: Fett<br>
     *              2: Kursiv<br>
     *              3: Fett &amp; Kursiv<br>
     *              <br>
     *              Ist die Eingabe nicht eine dieser 4 Zahlen, so wird nichts
     *              geändert.
     */
    @API
    public void setStyle(int style)
    {
        if (style >= 0 && style <= 3 && style != fontStyle)
        {
            fontStyle = style;
            font = font.deriveFont(style, SIZE);
            update();
        }
    }

    @API
    public int getStyle()
    {
        return fontStyle;
    }

    @API
    public void setHeight(double height)
    {
        if (this.height != height)
        {
            this.height = height;
            update();
        }
    }

    @API
    public double getHeight()
    {
        return height;
    }

    @API
    public double getWidth()
    {
        Vector sizeInPixels = FontMetrics.getSize(content, font);
        return sizeInPixels.getX() * height / sizeInPixels.getY();
    }

    @API
    public void setWidth(double width)
    {
        Vector sizeInPixels = FontMetrics.getSize(content, font);
        setHeight(width / sizeInPixels.getX() * sizeInPixels.getY());
    }

    @Internal
    private void update()
    {
        Vector size = FontMetrics.getSize(content, font);
        cachedScaleFactor = height / size.getY();
        cachedDescent = FontMetrics.getDescent(font);
        setFixture(() -> createShape(content, height, font));
    }

    /**
     * Zeichnet die Figur an der Position {@code (0|0)} mit der Rotation
     * {@code 0}.
     *
     * @param g             Das {@link Graphics2D}-Objekt, in das gezeichnet
     *                      werden soll.
     * @param pixelPerMeter Gibt an, wie viele Pixel ein Meter misst.
     */
    @Override
    @Internal
    public void render(Graphics2D g, double pixelPerMeter)
    {
        AffineTransform pre = g.getTransform();
        Font preFont = g.getFont();
        g.setColor(getColor());
        g.scale(cachedScaleFactor * pixelPerMeter,
                cachedScaleFactor * pixelPerMeter);
        g.setFont(font);
        g.drawString(content, 0, -cachedDescent);
        g.setFont(preFont);
        g.setTransform(pre);
    }
}
