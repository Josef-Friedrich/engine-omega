/*
 * Source: https://github.com/engine-alpha/tutorials/blob/master/src/eatutorials/userinput/MovingRectangle.java
 *
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
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
package de.pirckheimer_gymnasium.engine_pi_demos.input.keyboard;

import java.awt.event.KeyEvent;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.actor.Actor;
import de.pirckheimer_gymnasium.engine_pi.actor.Rectangle;
import de.pirckheimer_gymnasium.engine_pi.event.KeyStrokeListener;

public class KeyEventDemo extends Scene implements KeyStrokeListener
{
    Actor rectangle;

    public KeyEventDemo()
    {
        rectangle = new Rectangle(2, 2);
        add(rectangle);
    }

    @Override
    public void onKeyDown(KeyEvent keyEvent)
    {
        switch (keyEvent.getKeyCode())
        {
        case KeyEvent.VK_UP:
            rectangle.moveBy(0, 1);
            break;

        case KeyEvent.VK_RIGHT:
            rectangle.moveBy(1, 0);
            break;

        case KeyEvent.VK_DOWN:
            rectangle.moveBy(0, -1);
            break;

        case KeyEvent.VK_LEFT:
            rectangle.moveBy(-1, 0);
            break;
        }
    }

    public static void main(String[] args)
    {
        Game.start(new KeyEventDemo(), 600, 400);
    }
}
