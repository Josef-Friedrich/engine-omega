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
package de.pirckheimer_gymnasium.engine_pi_demos.actor;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.actor.Text;
import de.pirckheimer_gymnasium.engine_pi.event.KeyStrokeListener;

/**
 * Demonstiert <b>alle Schriftarten</b>, die auf dem System installiert sind.
 *
 * <p>
 * Alle Schriftarten auf einmal zu laden ist sehr langsam.
 * </p>
 */
public class TextAllSystemFonts extends Scene implements KeyStrokeListener
{
    /**
     * Wie viele Seiten angezeigt werden können.
     */
    private int pageCount;

    /**
     * Die aktuelle Seite.
     */
    private int page;

    public TextAllSystemFonts()
    {
        Text überschrift = new Text("Alle System-Schriftarten", 2f);
        überschrift.setPosition(-12, 3);
        überschrift.setColor("black");
        add(überschrift);
        loadFonts();
        setBackgroundColor("white");
    }

    private void loadFonts()
    {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        String[] systemFonts = ge.getAvailableFontFamilyNames();
        pageCount = systemFonts.length / 10;
        int startIndex = page * 10;
        for (int i = startIndex; i < startIndex + 10; i++)
        {
            String fontName = systemFonts[i];
            Text text = new Text(fontName, 1, fontName);
            text.setPosition(-12, -1 * i);
            text.setColor("black");
            add(text);
        }
    }

    @Override
    public void onKeyDown(KeyEvent keyEvent)
    {
        if (keyEvent.getKeyCode() == KeyEvent.VK_UP)
        {
            if (page == 0)
            {
                page = pageCount - 1;
            }
            else
            {
                page--;
            }
        }
        else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN)
        {
            if (page == pageCount - 1)
            {
                page = 0;
            }
            else
            {
                page++;
            }
        }
    }

    public static void main(String[] args)
    {
        Game.start(new TextAllSystemFonts(), 1020, 520);
        Game.setTitle("Alle Schriftarten");
    }
}
