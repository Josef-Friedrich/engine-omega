/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha/src/main/java/ea/internal/physics/PhysicsHandler.java
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

import java.util.List;
import java.util.function.Supplier;

import de.pirckheimer_gymnasium.jbox2d.dynamics.Body;

import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Actor;
import de.pirckheimer_gymnasium.engine_pi.actor.BodyType;
import de.pirckheimer_gymnasium.engine_pi.annotations.Internal;
import de.pirckheimer_gymnasium.engine_pi.event.CollisionEvent;

/**
 * Beschreibt allgemein ein Objekt, dass die physikalischen Eigenschaften eines
 * {@link Actor}-Objektes kontrollieren kann. Dazu gehört:
 * <ul>
 * <li>Das {@link Actor}-Objekt <b>bewegen</b>.</li>
 * <li><b>Physikalische Eigenschaften</b> des Objektes verändern (wie Masse,
 * Reibungskoeffizient etc.)</li>
 * <li><b>Einflüsse</b> auf das {@link Actor}-Objekt ausüben (wie anwenden von
 * Impulsen / Kräften)</li>
 * </ul>
 * Created by andonie on 16.02.15.
 */
@Internal
public interface PhysicsHandler
{
    /**
     * Verschiebt das Ziel-Objekt um einen spezifischen Wert auf der
     * Zeichenebene. Die Ausführung hat <b>erst (ggf.) im kommenden Frame</b>
     * einfluss auf die Physics und <b>ändert keine physikalischen
     * Eigenschaften</b> des Ziel-Objekts (außer dessen Ort).
     *
     * @param v Ein Vector, um den das Ziel-Objekt verschoben werden soll. Dies
     *          ändert seine Position, jedoch sonst keine weiteren
     *          Eigenschaften.
     */
    @Internal
    void moveBy(Vector v);

    /**
     * Gibt den <b>Gewichtsmittelpunkt</b> dieses {@link Actor}-Objekts aus.
     *
     * @return der aktuelle <b>Gewichtsmittelpunkt</b> des Ziel-Objekts als
     *         <i>Point auf der Zeichenebene</i>.
     */
    @Internal
    Vector getCenter();

    /**
     * Gibt an, ob ein bestimmter Punkt auf der Zeichenebene innerhalb des
     * Ziel-Objekts liegt.
     *
     * @param p Ein Punkt auf der Zeichenebene.
     *
     * @return <code>true</code>, wenn der übergebene Punkt innerhalb des
     *         Ziel-Objekts liegt, sonst <code>false</code>. Das Ergebnis kann
     *         (abhängig von der implementierenden Klasse) verschieden sicher
     *         richtige Ergebnisse liefern.
     */
    @Internal
    boolean contains(Vector p);

    /**
     * Gibt die aktuelle Position des Ziel-Objekts an.
     *
     * @return Die aktuelle Position des Ziel-Objekts. Diese ist bei Erstellung
     *         des Objekts zunächst immer <code>(0|0)</code> und wird mit
     *         Rotation und Verschiebung verändert.
     */
    @Internal
    Vector getPosition();

    /**
     * Gibt die aktuelle Rotation des Ziel-Objekts in <i>Grad</i> an. Bei
     * Erstellung eines {@link Actor}-Objekts ist seine Rotation stets 0.
     *
     * @return die aktuelle Rotation des Ziel-Objekts in <i>Grad</i>.
     */
    @Internal
    double getRotation();

    /**
     * Rotiert das Ziel-Objekt um einen festen Winkel.
     *
     * @param degree Der Winkel, um den das Ziel-Objekt gedreht werden soll (in
     *               <i>Grad</i>).
     *               <ul>
     *               <li>Werte &gt; 0 : Drehung gegen Uhrzeigersinn</li>
     *               <li>Werte &lt; 0 : Drehung im Uhrzeigersinn</li>
     *               </ul>
     */
    @Internal
    void rotateBy(double degree);

    @Internal
    void setRotation(double degree);

    @Internal
    void setDensity(double density);

    @Internal
    double getDensity();

    @Internal
    void setGravityScale(double factor);

    @Internal
    double getGravityScale();

    @Internal
    void setFriction(double friction);

    @Internal
    double getFriction();

    @Internal
    void setRestitution(double restitution);

    @Internal
    double getRestitution();

    @Internal
    void setLinearDamping(double damping);

    @Internal
    double getLinearDamping();

    @Internal
    void setAngularDamping(double damping);

    @Internal
    double getAngularDamping();

    /**
     * Gibt die Masse des Ziel-Objekts aus.
     *
     * @return Die Masse des Ziel-Objekts in [kg].
     */
    @Internal
    double getMass();

    /**
     * Übt eine Kraft auf das Ziel-Objekt (im Massenschwerpunkt) aus (sofern
     * möglich).
     *
     * @param force Die Kraft, die auf den Massenschwerpunkt angewandt werden
     *              soll. <b>Nicht in [px]</b>, sondern in [N] = [m / s^2].
     */
    @Internal
    void applyForce(Vector force);

    /**
     * Wirkt einen Drehmoment auf das Ziel-Objekt.
     *
     * @param torque der Drehmoment, der auf das Ziel-Objekt wirken soll. In
     *               [N*m]
     */
    @Internal
    void applyTorque(double torque);

    /**
     * Wirkt einen Drehimpuls auf das Ziel-Objekt.
     *
     * @param rotationImpulse der Drehimpuls, der auf das Ziel-Objekt wirken
     *                        soll. in [kg*m*m/s]
     */
    @Internal
    void applyRotationImpulse(double rotationImpulse);

    /**
     * Macht ein Type-Update für diesen Handler.
     *
     * @param type Der neue Type.
     */
    @Internal
    void setType(BodyType type);

    @Internal
    BodyType getType();

    @Internal
    void applyForce(Vector kraftInN, Vector globalLocation);

    /**
     * Wirkt einen Impuls auf einem Welt-Point.
     *
     * @param impulsInNS     Ein Impuls (in [Ns]).
     * @param globalLocation TODO
     */
    @Internal
    void applyImpulse(Vector impulsInNS, Vector globalLocation);

    /**
     * Gibt den WorldHandler aus, der die Welt handelt, in der sich der Klient
     * befindet.
     *
     * @return Der World-Handler, der zu diesem Physics-Handler gehört.
     */
    @Internal
    WorldHandler getWorldHandler();

    /**
     * Wird intern zum Debuggen benutzt. Gibt den korrespondierenden Body aus.
     *
     * @return Der korrespondierende Body.
     */
    @Internal
    Body getBody();

    /**
     * Setzt die Wirkung aller physikalischer Bewegungen (Geschwindigkeit und
     * Drehung) zurück. Hiernach ist das Objekt in Ruhe.
     */
    @Internal
    void resetMovement();

    /**
     * Setzt die Geschwindigkeit für das Handler-Objekt.
     *
     * @param metersPerSecond Setzt die Geschwindigkeit, mit der sich das
     *                        Zielobjekt bewegen soll.
     */
    @Internal
    void setVelocity(Vector metersPerSecond);

    /**
     * Gibt die aktuelle Geschwindigkeit aus.
     *
     * @return Die aktuelle Geschwindigkeit.
     */
    @Internal
    Vector getVelocity();

    /**
     * Setzt die Drehgeschwindigkeit für das Handler-Objekt.
     *
     * @param rotationsPerSecond Setzt die Drehgeschwindigkeit, mit der sich das
     *                           Zielobjekt bewegen soll.
     */
    @Internal
    void setAngularVelocity(double rotationsPerSecond);

    /**
     * Gibt die aktuelle Drehgeschwindigkeit aus.
     *
     * @return Die aktuelle Drehgeschwindigkeit.
     */
    @Internal
    double getAngularVelocity();

    /**
     * Setzt, ob die Rotation blockiert sein soll.
     */
    @Internal
    void setRotationLocked(boolean locked);

    /**
     * @return Ob die Rotation des Objekts blockiert ist.
     */
    @Internal
    boolean isRotationLocked();

    /**
     * Testet, ob das Objekt unter sich festen Boden hat. Dies ist der Fall,
     * wenn direkt unter dem Objekt ein passives Objekt liegt.<br>
     * Diese Methode geht bei <b>unten</b> explizit von "unterhalb der Y-Achse"
     * aus. Ein Objekt hat also Boden sich, wenn am "unteren" Ende seines Bodies
     * (=höchster Y-Wert) in unmittelbarer Nähe (heuristisch getestet) ein
     * passives Objekt anliegt.
     *
     * @return <code>true</code>, wenn direkt unter dem Objekt ein passives
     *         Objekt ist. Sonst <code>false</code>.
     */
    @Internal
    boolean isGrounded();

    /**
     * Entfernt alle Fixtures/Collider am Actor und setzt alle Fixturs für
     * dieses Objekt neu.
     *
     * @param fixtures Die neuen Fixtures als Supplier, der die Liste der
     *                 Fixtures ausgibt.
     */
    @Internal
    void setFixtures(Supplier<List<FixtureData>> fixtures);

    /**
     * Gibt die Proxy-Daten des Actors aus.
     *
     * @return der gegenwärtige physikalische Zustand des Raum-Objekts in
     *         Proxy-Daten.
     */
    @Internal
    PhysicsData getPhysicsData();

    void applyMountCallbacks(PhysicsHandler otherHandler);

    List<CollisionEvent<Actor>> getCollisions();

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
    void setAwake(boolean value);
}
