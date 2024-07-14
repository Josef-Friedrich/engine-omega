/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha-examples/src/main/java/ea/example/showcase/ForceKlickEnvironment.java
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
package de.pirckheimer_gymnasium.engine_pi_demos;

import java.awt.Color;
import java.awt.event.KeyEvent;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Actor;
import de.pirckheimer_gymnasium.engine_pi.actor.BodyType;
import de.pirckheimer_gymnasium.engine_pi.actor.Circle;
import de.pirckheimer_gymnasium.engine_pi.actor.Geometry;
import de.pirckheimer_gymnasium.engine_pi.actor.Rectangle;
import de.pirckheimer_gymnasium.engine_pi.event.CollisionEvent;
import de.pirckheimer_gymnasium.engine_pi.event.CollisionListener;
import de.pirckheimer_gymnasium.engine_pi.event.FrameUpdateListener;
import de.pirckheimer_gymnasium.engine_pi.event.MouseButton;
import de.pirckheimer_gymnasium.engine_pi.event.MouseClickListener;

/**
 * Eine kleine Sandbox, in der man ein paar Grundfunktionen der EA-Physik (4.0)
 * ausprobieren kann.
 *
 * <h2>Nutzung der Simulation</h2>
 * <p>
 * Die Simulation wird mit der Maus beeinflusst. Klicken setzt einen
 * Angriffspunkt. Ein weiteres Klicken wirkt an dem Angriffspunkt einen Impuls.
 * Stärke und Richtung hängen von der Position der Maus relativ zum ersten Point
 * ab. Der entsprechende Vector ist sichtbar.
 * </p>
 * <h2>Funktionen</h2>
 * <ul>
 * <li>R Setzt die gesamte Simulation zurück. Alle Objekte verharren wieder in
 * Ruhe an ihrer AusgangssetPosition(</li>
 * <li>S Aktiviert/Deaktiviert Schwerkraft in der Simulation.</li>
 * <li>E Aktiviert/Deaktiviert Wände</li>
 * <li>D Aktiviert/Deaktiviert den Debug-Modus (und stellt damit ein Raster, FPS
 * etc. dar)</li>
 * <li>I Aktiviert/Deaktiviert die Info-Box mit Infos zu den physikalischen
 * Eigenschaften des zuletzt angeklickten Objekts.</li>
 * <li>U und J erhöhen/reduzieren die Masse des zuletzt angeklickten
 * Objekts.</li>
 * <li>W und Q erhöhen/reduzieren die Elastizität der Wände.</li>
 * <li>1 und 2 zoomen rein/raus</li>
 * </ul>
 * <p>
 * Created by andonie on 05.09.15.
 */
public class ForceKlickEnvironment extends Scene implements
        CollisionListener<Actor>, MouseClickListener, FrameUpdateListener
{
    public static final float FIELD_WIDTH = 85;

    public static final float FIELD_DEPTH = 50;

    @Override
    public void onCollision(CollisionEvent<Actor> event)
    {
        attackedLast = event.getColliding();
    }

    @Override
    public void onCollisionEnd(CollisionEvent<Actor> colliding)
    {
        if (attackedLast == colliding.getColliding())
        {
            attackedLast = null;
        }
    }

    /**
     * Beschreibt die Zustände, in denen sich die Sandbox in Bezug auf
     * Mausklick-Funktion befinden kann.
     */
    private enum KlickMode
    {
        ATTACK_POINT, DIRECTION_INTENSITY
    }

    private final Actor attack;

    private final Geometry[] walls = new Geometry[4];

    private Actor attackedLast = null;

    private final Rectangle stange;

    private KlickMode klickMode = KlickMode.ATTACK_POINT;

    private Vector lastAttack;

    /**
     * Startet ein Sandbox-Fenster.
     */
    public ForceKlickEnvironment()
    {
        // Info-Message
        // fenster.nachrichtSchicken("Elastizität +[W]/-[Q] | Masse +[U] / -[J]
        // | [R]eset | [S]chwerkraft | [E]insperren");
        // Boden
        Rectangle boden = new Rectangle(FIELD_WIDTH, 1);
        boden.setPosition(0, FIELD_DEPTH);
        boden.setColor(Color.WHITE);
        boden.makeStatic();
        // Der Rest der Wände
        Rectangle links = new Rectangle(1, FIELD_DEPTH);
        Rectangle rechts = new Rectangle(1, FIELD_DEPTH);
        rechts.setPosition(FIELD_WIDTH - 1, 0);
        Rectangle oben = new Rectangle(FIELD_WIDTH, 1);
        walls[1] = links;
        walls[2] = rechts;
        walls[3] = oben;
        for (int i = 1; i <= 3; i++)
        {
            walls[i].setColor(Color.WHITE);
            walls[i].setVisible(false);
            walls[i].makeSensor();
        }
        // Vector-Visualisierung
        Rectangle stab = new Rectangle(1, 0.5);
        stab.setColor(new Color(200, 50, 50));
        stange = stab;
        stange.setLayerPosition(-10);
        // Attack-Visualisierung
        Circle atv = new Circle(0.5);
        atv.setColor(Color.RED);
        attack = atv;
        attack.setLayerPosition(-10);
        // Maus erstellen, Listener Anmelden.
        attack.addCollisionListener(this);
        getKeyStrokeListeners().add(e -> {
            if (e.getKeyCode() == KeyEvent.VK_E)
            {
                boolean wasActive = walls[1].isVisible();
                BodyType newType = wasActive ? BodyType.SENSOR
                        : BodyType.STATIC;
                for (int i = 0; i <= 3; i++)
                {
                    walls[i].setVisible(!wasActive);
                    walls[i].setBodyType(newType);
                }
            }
        });
        add(boden);
        add(links, rechts, oben);
        add(stab);
        add(atv);
        getCamera().setMeter(1);
    }

    /**
     * Wird bei jedem Mausklick aufgerufen.
     *
     * @param p Point des Mausklicks auf der Zeichenebene.
     */
    @Override
    public void onMouseDown(Vector p, MouseButton mouseButton)
    {
        switch (klickMode)
        {
        case ATTACK_POINT:
            lastAttack = p;
            // Visualize Attack Point
            attack.setCenter(p);
            attack.setVisible(true);
            // Prepare Vector Stick
            stange.setVisible(true);
            stange.setPosition(p);
            klickMode = KlickMode.DIRECTION_INTENSITY;
            break;

        case DIRECTION_INTENSITY:
            if (lastAttack == null)
            {
                klickMode = KlickMode.ATTACK_POINT;
                return;
            }
            attack.setVisible(false);
            stange.setVisible(false);
            Vector distance = lastAttack.negate().add(p);
            if (attackedLast != null
                    && attackedLast.getBodyType() == BodyType.DYNAMIC)
            {
                attackedLast.applyImpulse(distance.multiply(1), lastAttack);
                attackedLast = null;
            }
            klickMode = KlickMode.ATTACK_POINT;
            break;
        }
    }

    @Override
    public void onMouseUp(Vector point, MouseButton mouseButton)
    {
        // Ignore
    }

    @Override
    public void onFrameUpdate(double pastTime)
    {
        // Visualisiere ggf. die Vectorstange
        if (klickMode == KlickMode.DIRECTION_INTENSITY)
        {
            Vector currentMousePos = getMousePosition();
            if (lastAttack == null)
            {
                return;
            }
            double vectorLength = new Vector(lastAttack, currentMousePos)
                    .getLength();
            if (vectorLength <= 0)
            {
                return;
            }
            stange.setSize(vectorLength, stange.getHeight());
            double rot = Vector.RIGHT
                    .getAngle(lastAttack.negate().add(currentMousePos));
            if (Double.isNaN(rot))
            {
                return;
            }
            if (currentMousePos.getY() < lastAttack.getY())
            {
                rot = 360 - rot;
            }
            stange.setRotation(rot);
        }
    }

    public static void main(String[] args)
    {
        Game.start(new ForceKlickEnvironment(), 1000, 800);
    }
}
