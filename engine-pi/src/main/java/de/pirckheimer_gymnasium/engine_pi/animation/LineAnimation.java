/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha/src/main/java/ea/animation/LineAnimation.java
 *
 * Engine Pi ist eine anfängerorientierte 2D-Gaming Engine.
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
package de.pirckheimer_gymnasium.engine_pi.animation;

import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Actor;
import de.pirckheimer_gymnasium.engine_pi.animation.interpolation.LinearDouble;
import de.pirckheimer_gymnasium.engine_pi.event.AggregateFrameUpdateListener;

/**
 * Eine Animation, die ein {@link Actor}-Objekt in einer Linie animiert.
 */
public class LineAnimation extends AggregateFrameUpdateListener
{
    /**
     * Erstellt eine neue Linien-Animation.
     *
     * @param actor Der Actor, der zwischen seinem aktuellen Mittelpunkt und
     *     einem Endpunkt bewegt werden soll.
     * @param endPoint Der Endpunkt. Die Bewegung des Aktors endet mit seinem
     *     Mittelpunkt auf dem <code>endPoint</code>.
     * @param durationInSeconds Die Zeit in Sekunden, in der der Actor von
     *     seiner Ausgangsposition bis zum Zielpunkt benötigt.
     * @param pingpong <code>false</code>: Die Animation endet, wenn der Actor
     *     den Zielpunkt erreicht hat. <code>true</code>: Der Actor bewegt sich
     *     zwischen seinem Ausgangspunkt und dem Zielpunkt hin und her. Jede
     *     Strecke in eine Richtung dauert <code>durationInMS</code>. Die
     *     Animation endet nicht von sich aus.
     */
    public LineAnimation(Actor actor, Vector endPoint, double durationInSeconds,
            boolean pingpong)
    {
        Vector center = actor.getCenter();
        addFrameUpdateListener(new ValueAnimator<>(durationInSeconds,
                x -> actor.setCenter(x, actor.getCenter().getY()),
                new LinearDouble(center.getX(), endPoint.getX()),
                pingpong ? AnimationMode.PINGPONG : AnimationMode.SINGLE,
                this));
        addFrameUpdateListener(new ValueAnimator<>(durationInSeconds,
                y -> actor.setCenter(actor.getCenter().getX(), y),
                new LinearDouble(center.getY(), endPoint.getY()),
                pingpong ? AnimationMode.PINGPONG : AnimationMode.SINGLE,
                this));
    }
}
