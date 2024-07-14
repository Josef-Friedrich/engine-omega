/*
 * Source: https://github.com/engine-alpha/tutorials/blob/master/src/eatutorials/physics/Dominoes.java
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
package de.pirckheimer_gymnasium.engine_pi_demos.physics;

import java.awt.Color;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Circle;
import de.pirckheimer_gymnasium.engine_pi.actor.Rectangle;
import de.pirckheimer_gymnasium.engine_pi.event.FrameUpdateListener;
import de.pirckheimer_gymnasium.engine_pi.event.MouseButton;
import de.pirckheimer_gymnasium.engine_pi.event.MouseClickListener;

public class DominoesDemo extends Scene
        implements FrameUpdateListener, MouseClickListener
{
    private Rectangle ground;

    private Rectangle wall;

    private Circle ball;

    private Rectangle angle;

    public DominoesDemo()
    {
        setupBasicObjects();
        setupPhysics();
        setupAngle();
        makeDominoes();
    }

    private void setupBasicObjects()
    {
        // Boden auf dem die Dominosteine stehen
        ground = new Rectangle(200, 2);
        ground.setCenter(0, -5);
        ground.setColor("white");
        add(ground);
        // Der Ball, der die Dominosteine umwerfen soll.
        ball = new Circle(0.5);
        ball.setColor("red");
        ball.setPosition(-10, -2);
        add(ball);
        // Eine senkrechte Wand links der Simulation
        wall = new Rectangle(1, 40);
        wall.setPosition(-14, -4);
    }

    private void setupAngle()
    {
        angle = new Rectangle(1, 0.1);
        angle.setColor(Color.GREEN);
        add(angle);
    }

    private void setupPhysics()
    {
        ground.makeStatic();
        wall.makeDynamic();
        ball.makeDynamic();
        setGravityOfEarth();
    }

    private void makeDominoes()
    {
        for (int i = 0; i < 20; i++)
        {
            Rectangle domino = new Rectangle(0.4, 3);
            domino.setPosition(i * 3 * 0.4, -4);
            domino.makeDynamic();
            domino.setColor("blue");
            add(domino);
        }
    }

    @Override
    public void onFrameUpdate(double pastTime)
    {
        Vector mousePosition = getMousePosition();
        Vector ballCenter = ball.getCenter();
        Vector distance = ballCenter.getDistance(mousePosition);
        angle.setPosition(ball.getCenter());
        angle.setWidth(distance.getLength());
        double rot = Vector.RIGHT.getAngle(distance);
        angle.setRotation(rot);
    }

    @Override
    public void onMouseDown(Vector position, MouseButton button)
    {
        Vector impulse = ball.getCenter().getDistance(position).multiply(5);
        ball.applyImpulse(impulse);
    }

    public static void main(String[] args)
    {
        Game.start(new DominoesDemo(), 800, 300);
    }
}
