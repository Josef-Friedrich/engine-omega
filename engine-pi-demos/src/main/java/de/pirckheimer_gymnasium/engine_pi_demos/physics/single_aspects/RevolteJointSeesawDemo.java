/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha-examples/src/main/java/ea/example/showcase/JointDemo.java
 *
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
package de.pirckheimer_gymnasium.engine_pi_demos.physics.single_aspects;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Circle;
import de.pirckheimer_gymnasium.engine_pi.actor.Polygon;
import de.pirckheimer_gymnasium.engine_pi.actor.Rectangle;
import de.pirckheimer_gymnasium.engine_pi.actor.RevoluteJoint;

import static de.pirckheimer_gymnasium.engine_pi.Vector.v;

/**
 * Demonstriert die Klasse
 * {@link de.pirckheimer_gymnasium.engine_pi.actor.RevoluteJoint} und die
 * Methode
 * {@link de.pirckheimer_gymnasium.engine_pi.actor.Actor#createRevoluteJoint(de.pirckheimer_gymnasium.engine_pi.actor.Actor, Vector)}
 * anhand einer Wippe.
 */
public class RevolteJointSeesawDemo extends Scene
{
    public RevolteJointSeesawDemo()
    {
        Polygon base = new Polygon(v(0, 0), v(1, 0), v(0.5, 1));
        base.makeStatic();
        base.setColor("white");
        add(base);
        Rectangle seesaw = new Rectangle(5, 0.4);
        seesaw.makeDynamic();
        seesaw.setCenter(0.5, 1);
        seesaw.setColor("gray");
        seesaw.createRevoluteJoint(base, v(2.5, 0.2));
        add(seesaw);
        Circle circle = new Circle();
        circle.setPosition(-2, 2);
        circle.makeDynamic();
        add(circle);
        Circle circle2 = new Circle();
        circle2.setPosition(2, 2.2);
        circle2.makeDynamic();
        add(circle2);
        setGravityOfEarth();
    }

    public static void main(String[] args)
    {
        Game.debug();
        Game.start(new RevolteJointSeesawDemo());
    }
}
