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

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.actor.Triangle;

/**
 * Demonstriert die Figur <b>Dreieck</b> ({@link Triangle}).
 *
 * @author Josef Friedrich
 */
public class TriangleDemo extends ActorBaseScene
{
    public TriangleDemo()
    {
        // Kippt beim Aufprall
        Triangle triangle = new Triangle(5, 2);
        triangle.makeDynamic();
        triangle.rotateBy(45);
        add(triangle);
        // Der Anker ist links unten.
        Triangle triangle2 = new Triangle(5, 5);
        triangle2.makeStatic();
        triangle2.setPosition(5, 0);
        add(triangle2);
    }

    public static void main(String[] args)
    {
        Game.setDebug(true);
        Game.start(new TriangleDemo());
    }
}
