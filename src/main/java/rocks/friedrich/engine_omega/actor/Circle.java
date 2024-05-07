/*
 * Engine Omega ist eine anfängerorientierte 2D-Gaming Engine.
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

package rocks.friedrich.engine_omega.actor;

import rocks.friedrich.engine_omega.internal.annotations.API;
import rocks.friedrich.engine_omega.internal.annotations.Internal;
import rocks.friedrich.engine_omega.internal.physics.FixtureData;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Beschreibt einen Kreis.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public class Circle extends Geometry {
    private double diameter;

    /**
     * Konstruktor.
     *
     * @param diameter Durchmesser des Kreises
     */
    public Circle(double diameter) {
        super(() -> new FixtureData(createCircleShape(diameter)));

        this.diameter = diameter;
        this.setColor(Color.WHITE);
    }

    /**
     * Gibt den Durchmesser des Kreises aus.
     *
     * @return Durchmesser des Kreises.
     */
    @API
    public double getDiameter() {
        return diameter;
    }

    /**
     * Gibt den Radius des Kreises aus.
     *
     * @return Radius des Kreises.
     */
    @API
    public double getRadius() {
        return diameter / 2;
    }

    @Override
    public void render(Graphics2D g, double pixelPerMeter) {
        g.setColor(getColor());
        g.fillOval(0, -(int) (diameter * pixelPerMeter), (int) (diameter * pixelPerMeter), (int) (diameter * pixelPerMeter));
    }

    /**
     * Setzt den Radius des Kreises neu.
     * Ändert damit die physikalischen Eigenschaften des Objekts.
     *
     * @param radius Der neue Radius des Kreises.
     */
    @API
    public void resetRadius(double radius) {
        this.diameter = 2 * radius;
        FixtureData[] fixtureData = this.getPhysicsHandler().getPhysicsData().generateFixtureData();
        FixtureData thatoneCircle = fixtureData[0];
        thatoneCircle.setShape(createCircleShape(this.diameter));
        this.setFixture(() -> thatoneCircle);
    }

    @Internal
    private static Shape createCircleShape(double diameter) {
        CircleShape shape = new CircleShape();
        shape.m_radius = (float) (diameter / 2);
        shape.m_p.set(shape.m_radius, shape.m_radius);
        return shape;
    }
}
