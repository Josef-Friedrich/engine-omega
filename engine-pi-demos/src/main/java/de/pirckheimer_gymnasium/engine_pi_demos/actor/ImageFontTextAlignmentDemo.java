/*
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
package de.pirckheimer_gymnasium.engine_pi_demos.actor;

import static de.pirckheimer_gymnasium.engine_pi.util.TextAlignment.CENTER;
import static de.pirckheimer_gymnasium.engine_pi.util.TextAlignment.LEFT;
import static de.pirckheimer_gymnasium.engine_pi.util.TextAlignment.RIGHT;

import java.awt.Color;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.actor.ImageFont;
import de.pirckheimer_gymnasium.engine_pi.actor.ImageFontCaseSensitivity;
import de.pirckheimer_gymnasium.engine_pi.actor.ImageFontText;
import de.pirckheimer_gymnasium.engine_pi.util.TextAlignment;

/**
 * @author Josef Friedrich
 */
public class ImageFontTextAlignmentDemo extends Scene
{
    public ImageFontTextAlignmentDemo()
    {
        setBackgroundColor(Color.GRAY);
        createTextLine(3, LEFT);
        createTextLine(0, CENTER);
        createTextLine(-3, RIGHT);
    }

    private void createTextLine(int y, TextAlignment alignment)
    {
        ImageFont font = new ImageFont("pixel-text",
                ImageFontCaseSensitivity.TO_UPPER);
        ImageFontText line = new ImageFontText(font, "Hello, World.", 18,
                alignment);
        line.setPosition(-9, y);
        add(line);
    }

    public static void main(String[] args)
    {
        Game.start(new ImageFontTextAlignmentDemo(), 1020, 520);
        Game.setTitle("Text Example");
    }
}
