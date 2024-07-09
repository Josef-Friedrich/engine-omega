/*
 * Source: https://github.com/gurkenlabs/litiengine/blob/main/litiengine/src/main/java/de/gurkenlabs/litiengine/resources/Resources.java
 *
 * MIT License
 *
 * Copyright (c) 2016 - 2024 Gurkenlabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.pirckheimer_gymnasium.engine_pi;

import java.awt.Color;

import de.pirckheimer_gymnasium.engine_pi.resources.ColorContainer;
import de.pirckheimer_gymnasium.engine_pi.resources.ColorScheme;
import de.pirckheimer_gymnasium.engine_pi.resources.ColorSchemeSelection;
import de.pirckheimer_gymnasium.engine_pi.resources.FontContainer;
import de.pirckheimer_gymnasium.engine_pi.resources.ImageContainer;
import de.pirckheimer_gymnasium.engine_pi.resources.Resource;
import de.pirckheimer_gymnasium.engine_pi.resources.ResourcesContainer;
import de.pirckheimer_gymnasium.engine_pi.resources.SoundContainer;

/**
 * Zur <b>Aufbewahrung</b> und <b>Verwaltung</b> verschiedener
 * <b>Ressourcen</b>.
 *
 * <p>
 * Diese Klasse ist der Einstiegspunkt für den Zugriff auf alle Arten von
 * {@link Resource}n. Eine Ressource ist jede nicht-ausführbare Datei, die mit
 * dem Spiel bereitgestellt wird. Die {@link Resources} Klasse bietet Zugriff
 * auf verschiedene Spezialisierungen von {@link ResourcesContainer} und wird
 * von verschiedenen (Lade-)Mechanismen verwendet, um Ressourcen während der
 * Laufzeit verfügbar zu machen.
 * </p>
 *
 * @see ResourcesContainer
 */
public final class Resources
{
    public static final ColorContainer COLORS = new ColorContainer();

    public static final FontContainer FONTS = new FontContainer();

    public static final ImageContainer IMAGES = new ImageContainer();

    public static final SoundContainer SOUNDS = new SoundContainer();

    public static ColorScheme colorScheme;
    static
    {
        setColorSchemeToPredefined(ColorSchemeSelection.GNOME);
        COLORS.addScheme(colorScheme);
    }

    private Resources()
    {
        throw new UnsupportedOperationException();
    }

    public static void setColorScheme(ColorScheme scheme)
    {
        colorScheme = scheme;
        COLORS.addScheme(colorScheme);
    }

    public static void setColorSchemeToPredefined(
            ColorSchemeSelection selection)
    {
        setColorScheme(selection.getScheme());
    }

    public static Color getColor(String name)
    {
        return COLORS.get(name);
    }

    /**
     * Stellt den Zugriff auf den {@link ImageContainer Zwischenspeicher für
     * Bild-Resourcen} vom Datentyp {@link java.awt.image.BufferedImage} bereit.
     *
     * @author Josef Friedrich
     *
     * @return Ein Zwischenspeicher für Bild-Ressourcen vom Datentyp
     *         {@link java.awt.image.BufferedImage}.
     */
    public static ImageContainer getImages()
    {
        return IMAGES;
    }

    /**
     * Stellt den Zugriff auf den {@link SoundContainer Zwischenspeicher für
     * Audio-Resourcen} vom Datentyp
     * {@link de.pirckheimer_gymnasium.engine_pi.sound.Sound Sound} bereit.
     *
     * @author Josef Friedrich
     *
     * @return Ein {@link SoundContainer Zwischenspeicher für Audio-Ressourcen}
     *         vom Datentyp
     *         {@link de.pirckheimer_gymnasium.engine_pi.sound.Sound Sound}.
     */
    public static SoundContainer getSounds()
    {
        return SOUNDS;
    }

    /**
     * Clears the all resource containers by removing previously loaded
     * resources.
     */
    public static void clearAll()
    {
        COLORS.clear();
        FONTS.clear();
        IMAGES.clear();
        SOUNDS.clear();
    }
}
