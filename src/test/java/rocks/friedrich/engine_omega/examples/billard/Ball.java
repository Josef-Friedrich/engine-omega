/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha-examples/src/main/java/ea/example/showcase/billard/Ball.java
 *
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2018 Michael Andonie and contributors.
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
package rocks.friedrich.engine_omega.examples.billard;

import java.awt.Color;

import rocks.friedrich.engine_omega.Vector;
import rocks.friedrich.engine_omega.actor.BodyType;
import rocks.friedrich.engine_omega.actor.Circle;
import rocks.friedrich.engine_omega.event.FrameUpdateListener;

public class Ball extends Circle implements FrameUpdateListener
{
    public static final double DIAMETER = 24;

    public Ball()
    {
        super(DIAMETER);
        setColor(Color.YELLOW);
        setBodyType(BodyType.DYNAMIC);
        setFriction(0);
        setRestitution(0.9);
    }

    @Override
    public void onFrameUpdate(double deltaSeconds)
    {
        if (getVelocity().getLength() < 0.2)
        {
            setVelocity(Vector.NULL);
        }
        else
        {
            applyForce(getVelocity().negate().multiply(10));
            // TODO torque
        }
    }
}
