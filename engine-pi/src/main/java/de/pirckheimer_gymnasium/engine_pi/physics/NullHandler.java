/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha/src/main/java/ea/internal/physics/NullHandler.java
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
package de.pirckheimer_gymnasium.engine_pi.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.pirckheimer_gymnasium.jbox2d.collision.AABB;
import de.pirckheimer_gymnasium.jbox2d.common.Transform;
import de.pirckheimer_gymnasium.jbox2d.dynamics.Body;

import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Actor;
import de.pirckheimer_gymnasium.engine_pi.actor.BodyType;
import de.pirckheimer_gymnasium.engine_pi.event.CollisionEvent;

/**
 * Eine Steuerungsklasse für Operationen an {@link Actor}-Objekten, die an
 * keiner Szene angehängt sind. Die Klasse führt alle Operationen rein numerisch
 * durch und gibt Fehler aus, wenn Operationen ausgeführt werden, die nur mit
 * einer Verbindung zur Physics-Engine funktionieren können.
 */
public class NullHandler implements PhysicsHandler
{
    private final PhysicsData physicsData;

    private final Collection<Consumer<PhysicsHandler>> mountCallbacks = new ArrayList<>();

    public NullHandler(PhysicsData physicsData)
    {
        this.physicsData = physicsData;
    }

    @Override
    public void moveBy(Vector v)
    {
        this.physicsData.setX(this.physicsData.getX() + v.getX());
        this.physicsData.setY(this.physicsData.getY() + v.getY());
    }

    @Override
    public Vector getCenter()
    {
        AABB bounds = null;
        AABB shapeBounds = new AABB();
        Transform transform = new Transform();
        for (FixtureData fixtureData : physicsData.getFixtures().get())
        {
            transform.set(getPosition().toVec2(),
                    (float) Math.toRadians(getRotation()));
            fixtureData.getShape().computeAABB(shapeBounds, transform, 0);
            if (bounds != null)
            {
                bounds.combine(shapeBounds);
            }
            else
            {
                bounds = new AABB();
                bounds.set(shapeBounds);
            }
        }
        return Vector.of(bounds.getCenter());
    }

    /**
     * Ein Objekt ohne Physik enthält keinen Punkt.
     *
     * @param p Ein Punkt auf der Zeichenebene.
     *
     * @return false
     */
    @Override
    public boolean contains(Vector p)
    {
        return false;
    }

    @Override
    public Vector getPosition()
    {
        return new Vector(this.physicsData.getX(), this.physicsData.getY());
    }

    @Override
    public double getRotation()
    {
        return this.physicsData.getRotation();
    }

    @Override
    public void rotateBy(double degree)
    {
        this.physicsData.setRotation(this.physicsData.getRotation() + degree);
    }

    @Override
    public void setRotation(double degree)
    {
        this.physicsData.setRotation(degree);
    }

    @Override
    public void setDensity(double density)
    {
        if (density <= 0)
        {
            throw new IllegalArgumentException(
                    "Dichte kann nicht kleiner als 0 sein. Eingabe war "
                            + density + ".");
        }
        this.physicsData.setGlobalDensity(density);
    }

    @Override
    public double getDensity()
    {
        return this.physicsData.getGlobalDensity();
    }

    @Override
    public void setGravityScale(double factor)
    {
        this.physicsData.setGravityScale(factor);
    }

    @Override
    public double getGravityScale()
    {
        return this.physicsData.getGravityScale();
    }

    @Override
    public void setFriction(double friction)
    {
        this.physicsData.setGlobalFriction(friction);
    }

    @Override
    public double getFriction()
    {
        return this.physicsData.getGlobalFriction();
    }

    @Override
    public void setRestitution(double elasticity)
    {
        this.physicsData.setGlobalRestitution(elasticity);
    }

    @Override
    public double getRestitution()
    {
        return this.physicsData.getGlobalRestitution();
    }

    @Override
    public void setLinearDamping(double damping)
    {
        this.physicsData.setLinearDamping(damping);
    }

    @Override
    public double getLinearDamping()
    {
        return physicsData.getLinearDamping();
    }

    @Override
    public void setAngularDamping(double damping)
    {
        physicsData.setAngularDamping(damping);
    }

    @Override
    public double getAngularDamping()
    {
        return physicsData.getAngularDamping();
    }

    @Override
    public double getMass()
    {
        Double mass = physicsData.getMass();
        return mass == null ? 0 : mass;
    }

    @Override
    public void applyForce(Vector force)
    {
        mountCallbacks.add(physicsHandler -> physicsHandler.applyForce(force));
    }

    @Override
    public void applyTorque(double torque)
    {
        mountCallbacks
                .add(physicsHandler -> physicsHandler.applyTorque(torque));
    }

    @Override
    public void applyRotationImpulse(double rotationImpulse)
    {
        mountCallbacks.add(physicsHandler -> physicsHandler
                .applyRotationImpulse(rotationImpulse));
    }

    @Override
    public void setType(BodyType type)
    {
        this.physicsData.setType(type);
    }

    @Override
    public BodyType getType()
    {
        return physicsData.getType();
    }

    @Override
    public void applyForce(Vector force, Vector globalLocation)
    {
        mountCallbacks.add(physicsHandler -> physicsHandler.applyForce(force,
                globalLocation));
    }

    @Override
    public void applyImpulse(Vector impulse, Vector globalLocation)
    {
        mountCallbacks.add(physicsHandler -> physicsHandler
                .applyImpulse(impulse, globalLocation));
    }

    @Override
    public WorldHandler getWorldHandler()
    {
        return null;
    }

    @Override
    public Body getBody()
    {
        return null;
    }

    @Override
    public void resetMovement()
    {
        physicsData.setVelocity(Vector.NULL);
        physicsData.setAngularVelocity(0);
    }

    @Override
    public void setVelocity(Vector metersPerSecond)
    {
        physicsData.setVelocity(metersPerSecond);
    }

    @Override
    public Vector getVelocity()
    {
        return physicsData.getVelocity();
    }

    @Override
    public void setAngularVelocity(double rotationsPerSecond)
    {
        physicsData.setAngularVelocity(
                (double) Math.toRadians(rotationsPerSecond * 360));
    }

    @Override
    public double getAngularVelocity()
    {
        return physicsData.getAngularVelocity();
    }

    @Override
    public void setRotationLocked(boolean locked)
    {
        this.physicsData.setRotationLocked(locked);
    }

    @Override
    public boolean isRotationLocked()
    {
        return this.physicsData.isRotationLocked();
    }

    @Override
    public boolean isGrounded()
    {
        return false;
    }

    @Override
    public void setFixtures(Supplier<List<FixtureData>> shapes)
    {
        physicsData.setFixtures(shapes);
    }

    @Override
    public PhysicsData getPhysicsData()
    {
        return this.physicsData;
    }

    @Override
    public void applyMountCallbacks(PhysicsHandler otherHandler)
    {
        for (Consumer<PhysicsHandler> mountCallback : mountCallbacks)
        {
            mountCallback.accept(otherHandler);
        }
        mountCallbacks.clear();
    }

    @Override
    public List<CollisionEvent<Actor>> getCollisions()
    {
        return Collections.emptyList();
    }

    /**
     * Legt den Schlafzustand des Körpers fest. Ein schlafender Körper hat sehr
     * geringe CPU-Kosten.
     *
     * <p>
     * Das Versetzen in den Schlafzustand setzt die Geschwindigkeit und den
     * Impuls eines Körpers auf null.
     * </p>
     *
     * @param value Der Schlafzustand des Körpers.
     */
    @Override
    public void setAwake(boolean value)
    {
        // Mache nichts
    }
}
