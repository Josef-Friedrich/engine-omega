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
package de.pirckheimer_gymnasium.engine_pi_demos.event;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;

/**
 * Demonstriert die Methode
 * {@link de.pirckheimer_gymnasium.engine_pi.event.FrameUpdateListenerRegistration#repeat(double, int, de.pirckheimer_gymnasium.engine_pi.event.PeriodicTask, de.pirckheimer_gymnasium.engine_pi.event.PeriodicTask)}.
 *
 * <p>
 * In der Konsole erscheint viermal {@code task} und einmal {@code final task}
 * </p>
 */
public class RepeatFinalTaskDemo
{
    public static void main(String[] args)
    {
        Game.start(new Scene()
        {
            {
                repeat(0.2, 5, (counter) -> System.out.println("task"),
                        (counter) -> System.out.println("final task"));
            }
        });
    }
}
