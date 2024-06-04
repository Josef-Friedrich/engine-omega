/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha/src/main/java/ea/Scene.java
 *
 * Engine Omega ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2023 Michael Andonie and contributors.
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
package rocks.friedrich.engine_omega;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RopeJoint;

import rocks.friedrich.engine_omega.actor.Actor;
import rocks.friedrich.engine_omega.actor.ActorCreator;
import rocks.friedrich.engine_omega.annotations.API;
import rocks.friedrich.engine_omega.annotations.Internal;
import rocks.friedrich.engine_omega.event.EventListenerHelper;
import rocks.friedrich.engine_omega.event.EventListeners;
import rocks.friedrich.engine_omega.event.FrameUpdateListener;
import rocks.friedrich.engine_omega.event.FrameUpdateListenerContainer;
import rocks.friedrich.engine_omega.event.KeyListener;
import rocks.friedrich.engine_omega.event.KeyListenerContainer;
import rocks.friedrich.engine_omega.event.MouseButton;
import rocks.friedrich.engine_omega.event.MouseClickListener;
import rocks.friedrich.engine_omega.event.MouseClickListenerContainer;
import rocks.friedrich.engine_omega.event.MouseWheelEvent;
import rocks.friedrich.engine_omega.event.MouseWheelListener;
import rocks.friedrich.engine_omega.event.MouseWheelListenerContainer;
import rocks.friedrich.engine_omega.physics.WorldHandler;

/**
 * Mit Hilfe von Szenen können verschiedene Ansichten eines Spiels erstellt
 * werden, ohne beim Szenenwechsel alle grafischen Objekte entfernen und wieder
 * neu erzeugen zu müssen.
 */
public class Scene implements KeyListenerContainer, MouseClickListenerContainer,
        MouseWheelListenerContainer, FrameUpdateListenerContainer, ActorCreator
{
    private static final Color REVOLUTE_JOINT_COLOR = Color.BLUE;

    private static final Color ROPE_JOINT_COLOR = Color.CYAN;

    private static final Color DISTANCE_JOINT_COLOR = Color.ORANGE;

    private static final Color PRISMATIC_JOINT_COLOR = Color.GREEN;

    /**
     * Die Kamera des Spiels. Hiermit kann der sichtbare Ausschnitt der
     * Zeichenebene bestimmt und manipuliert werden.
     */
    private final Camera camera;

    private final EventListeners<KeyListener> keyListeners = new EventListeners<>();

    private final EventListeners<MouseClickListener> mouseClickListeners = new EventListeners<>();

    private final EventListeners<MouseWheelListener> mouseWheelListeners = new EventListeners<>();

    private final EventListeners<FrameUpdateListener> frameUpdateListeners = new EventListeners<>();

    /**
     * Die Layer dieser Szene.
     */
    private final List<Layer> layers = new ArrayList<>();

    private Color backgroundColor = Color.BLACK;

    /**
     * Die Hauptebene (default-additions)
     */
    private final Layer mainLayer;

    private static final int JOINT_CIRCLE_RADIUS = 10;

    /**
     * (Basis-)Breite für die Visualisierung von Rechtecken
     */
    private static final int JOINT_RECTANGLE_SIDE = 12;

    public Scene()
    {
        camera = new Camera();
        mainLayer = new Layer();
        mainLayer.setLayerPosition(0);
        addLayer(mainLayer);
        EventListenerHelper.autoRegisterListeners(this);
    }

    public Scene getScene()
    {
        return this;
    }

    /**
     * Gibt die Hauptebene dieser Szene aus.
     *
     * @return Die Hauptebene dieser Szene.
     */
    @API
    public Layer getMainLayer()
    {
        return mainLayer;
    }

    /**
     * Führt auf allen Ebenen <b>parallelisiert</b> den World-Step aus.
     *
     * @param deltaSeconds Die Echtzeit, die seit dem letzten World-Step
     *                     vergangen ist.
     */
    @Internal
    public final void step(double deltaSeconds,
            Function<Runnable, Future<?>> invoker) throws InterruptedException
    {
        synchronized (layers)
        {
            Collection<Future<?>> layerFutures = new ArrayList<>(layers.size());
            for (Layer layer : layers)
            {
                Future<?> future = invoker
                        .apply(() -> layer.step(deltaSeconds));
                layerFutures.add(future);
            }
            for (Future<?> layerFuture : layerFutures)
            {
                try
                {
                    layerFuture.get();
                }
                catch (ExecutionException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Internal
    public final void render(Graphics2D g, int width, int height)
    {
        final AffineTransform base = g.getTransform();
        synchronized (layers)
        {
            for (Layer layer : layers)
            {
                layer.render(g, camera, width, height);
                g.setTransform(base);
            }
        }
        if (Game.isDebug())
        {
            renderJoints(g);
        }
    }

    /**
     * Wird aufgerufen, wann immer ein Layerzustand innerhalb dieser Scene
     * geändert wurde. Stellt sicher, dass die Layer-Liste korrekt sortiert ist
     * und aller Layer in der richtigen Reihenfolge gerendert werden.
     */
    @Internal
    final void sortLayers()
    {
        this.layers.sort(Comparator.comparingInt(Layer::getLayerPosition));
    }

    @API
    public final void addLayer(Layer layer)
    {
        synchronized (this.layers)
        {
            layer.setParent(this);
            this.layers.add(layer);
            sortLayers();
        }
    }

    @API
    public final void removeLayer(Layer layer)
    {
        synchronized (this.layers)
        {
            this.layers.remove(layer);
            layer.setParent(null);
        }
    }

    /**
     * Gibt die sichtbare Fläche auf dem <b>Hauptebene</b> aus.
     *
     * @return Die sichtbare Fläche auf der Hauptebene
     *
     * @see Game#getFrameSizeInPixels()
     */
    @API
    public Bounds getVisibleArea(Vector gameSizeInPixels)
    {
        return mainLayer.getVisibleArea(gameSizeInPixels);
    }

    @API
    public final Camera getCamera()
    {
        return camera;
    }

    @Internal
    private void renderJoints(Graphics2D g)
    {
        // Display Joints
        for (Layer layer : layers)
        {
            Joint j = layer.getWorldHandler().getWorld().getJointList();
            while (j != null)
            {
                renderJoint(j, g, layer);
                j = j.getNext();
            }
        }
    }

    @Internal
    private static void renderJoint(Joint j, Graphics2D g, Layer layer)
    {
        Vec2 anchorA = new Vec2(), anchorB = new Vec2();
        j.getAnchorA(anchorA);
        j.getAnchorB(anchorB);
        Vector aInPx = layer
                .translateWorldPointToFramePxCoordinates(Vector.of(anchorA));
        Vector bInPx = layer
                .translateWorldPointToFramePxCoordinates(Vector.of(anchorB));
        if (j instanceof RevoluteJoint)
        {
            g.setColor(REVOLUTE_JOINT_COLOR);
            g.drawOval((int) aInPx.getX() - (JOINT_CIRCLE_RADIUS / 2),
                    (int) aInPx.getY() - (JOINT_CIRCLE_RADIUS / 2),
                    JOINT_CIRCLE_RADIUS, JOINT_CIRCLE_RADIUS);
        }
        else if (j instanceof RopeJoint)
        {
            renderJointRectangle(g, ROPE_JOINT_COLOR, aInPx, bInPx,
                    layer.calculatePixelPerMeter());
        }
        else if (j instanceof DistanceJoint)
        {
            renderJointRectangle(g, DISTANCE_JOINT_COLOR, aInPx, bInPx,
                    layer.calculatePixelPerMeter());
        }
        else if (j instanceof PrismaticJoint)
        {
            renderJointRectangle(g, PRISMATIC_JOINT_COLOR, aInPx, bInPx,
                    layer.calculatePixelPerMeter());
        }
    }

    @Internal
    private static void renderJointRectangle(Graphics2D g, Color color,
            Vector a, Vector b, double pixelPerMeter)
    {
        g.setColor(color);
        g.drawRect((int) a.getX() - (JOINT_CIRCLE_RADIUS / 2),
                (int) a.getY() - (JOINT_CIRCLE_RADIUS / 2),
                JOINT_RECTANGLE_SIDE, JOINT_RECTANGLE_SIDE);
        g.drawRect((int) b.getX() - (JOINT_CIRCLE_RADIUS / 2),
                (int) b.getY() - (JOINT_CIRCLE_RADIUS / 2),
                JOINT_RECTANGLE_SIDE, JOINT_RECTANGLE_SIDE);
        g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(),
                (int) b.getY());
        Vector middle = a.add(b).divide(2);
        g.drawString(
                String.valueOf(
                        a.getDistance(b).divide(pixelPerMeter).getLength()),
                (int) middle.getX(), (int) middle.getY());
    }

    /**
     * Gibt den WorldHandler der Hauptebene aus.
     *
     * @return WorldHandler der Hauptebene.
     */
    @Internal
    public final WorldHandler getWorldHandler()
    {
        return mainLayer.getWorldHandler();
    }

    /**
     * Setzt die Schwerkraft als Vektor, die auf <b>alle Objekte innerhalb der
     * Hauptebene der Szene</b> wirkt.
     *
     * @param gravity Die neue Schwerkraft als {@link Vector}. Die Einheit ist
     *                <b>[N]</b>.
     *
     * @see #setGravity(double, double)
     * @see Layer#setGravity(Vector)
     * @see Layer#setGravity(double, double)
     *
     * @jbox2d <a href=
     *         "https://github.com/jbox2d/jbox2d/blob/94bb3e4a706a6d1a5d8728a722bf0af9924dde84/jbox2d-library/src/main/java/org/jbox2d/dynamics/World.java#L997-L1004">dynamics/World.java#L997-L1004</a>
     * @box2d <a href=
     *        "https://github.com/erincatto/box2d/blob/411acc32eb6d4f2e96fc70ddbdf01fe5f9b16230/include/box2d/b2_world.h#L312-L315">b2_world.h#L312-L315</a>
     */
    @API
    public void setGravity(Vector gravity)
    {
        mainLayer.setGravity(gravity);
    }

    /**
     * Setzt die Schwerkraft durch zwei Eingabeparameter für die x- und
     * y-Richtung, die auf <b>alle Objekte innerhalb der Hauptebene der
     * Szene</b> wirkt.
     *
     * @param gravityX Die neue Schwerkraft, die in X-Richtung wirken soll. Die
     *                 Einheit ist <b>[N]</b>.
     * @param gravityY Die neue Schwerkraft, die in Y-Richtung wirken soll. Die
     *                 Einheit ist <b>[N]</b>.
     *
     * @see #setGravity(Vector)
     * @see Layer#setGravity(Vector)
     * @see Layer#setGravity(double, double)
     *
     * @jbox2d <a href=
     *         "https://github.com/jbox2d/jbox2d/blob/94bb3e4a706a6d1a5d8728a722bf0af9924dde84/jbox2d-library/src/main/java/org/jbox2d/dynamics/World.java#L997-L1004">dynamics/World.java#L997-L1004</a>
     * @box2d <a href=
     *        "https://github.com/erincatto/box2d/blob/411acc32eb6d4f2e96fc70ddbdf01fe5f9b16230/include/box2d/b2_world.h#L312-L315">b2_world.h#L312-L315</a>
     */
    @API
    public void setGravity(double gravityX, double gravityY)
    {
        setGravity(new Vector(gravityX, gravityY));
    }

    /**
     * Setzt die Schwerkraft die auf der Erde wirkt: 9.81 <b>[N]</b> bzw.
     * <b>[m/s^2]</b> nach unten (x: 0, y: -9.81).
     *
     * @see Layer#setGravityOfEarth
     */
    @API
    public void setGravityOfEarth()
    {
        setGravity(0, -9.81);
    }

    /**
     * Setzt, ob die Engine-Physics für diese Szene pausiert sein soll.
     *
     * @param worldPaused <code>false</code>: Die Engine-Physik läuft normal.
     *                    <code>true</code>: Die Engine-Physik läuft
     *                    <b>nicht</b>. Das bedeutet u.A. keine
     *                    Collision-Detection, keine Physik-Simulation etc., bis
     *                    die Physik wieder mit
     *                    <code>setPhysicsPaused(true)</code> aktiviert wird.
     *
     * @see #isPhysicsPaused()
     */
    @API
    public void setPhysicsPaused(boolean worldPaused)
    {
        mainLayer.getWorldHandler().setWorldPaused(worldPaused);
    }

    /**
     * Gibt an, ob die Physik dieser Szene pausiert ist.
     *
     * @return <code>true</code>: Die Physik ist pausiert. <code>false</code>:
     *         Die Physik ist nicht pausiert.
     *
     * @see #setPhysicsPaused(boolean)
     */
    @API
    public boolean isPhysicsPaused()
    {
        return mainLayer.getWorldHandler().isWorldPaused();
    }

    /**
     * Fügt einen oder mehrere {@link Actor}-Objekte der Szene hinzu.
     *
     * @param actors Ein oder mehrere {@link Actor}-Objekte.
     */
    @API
    final public void add(Actor... actors)
    {
        for (Actor actor : actors)
        {
            mainLayer.add(actor);
        }
    }

    /**
     * Entferne einen oder mehrere {@link Actor}-Objekte aus der Szene.
     *
     * @param actors Ein oder mehrere {@link Actor}-Objekte.
     */
    @API
    final public void remove(Actor... actors)
    {
        for (Actor actor : actors)
        {
            mainLayer.remove(actor);
        }
    }

    @API
    public EventListeners<KeyListener> getKeyListeners()
    {
        return keyListeners;
    }

    @API
    public EventListeners<MouseClickListener> getMouseClickListeners()
    {
        return mouseClickListeners;
    }

    @API
    public EventListeners<MouseWheelListener> getMouseWheelListeners()
    {
        return mouseWheelListeners;
    }

    @API
    public EventListeners<FrameUpdateListener> getFrameUpdateListeners()
    {
        return frameUpdateListeners;
    }

    @Internal
    public final void invokeFrameUpdateListeners(double deltaSeconds)
    {
        frameUpdateListeners.invoke(frameUpdateListener -> frameUpdateListener
                .onFrameUpdate(deltaSeconds));
        synchronized (layers)
        {
            for (Layer layer : layers)
            {
                layer.invokeFrameUpdateListeners(deltaSeconds);
            }
        }
    }

    @Internal
    final void invokeKeyDownListeners(KeyEvent e)
    {
        keyListeners.invoke(keyListener -> keyListener.onKeyDown(e));
    }

    @Internal
    final void invokeKeyUpListeners(KeyEvent e)
    {
        keyListeners.invoke(keyListener -> keyListener.onKeyUp(e));
    }

    @Internal
    final void invokeMouseDownListeners(Vector position, MouseButton button)
    {
        mouseClickListeners.invoke(mouseClickListener -> mouseClickListener
                .onMouseDown(position, button));
    }

    @Internal
    final void invokeMouseUpListeners(Vector position, MouseButton button)
    {
        mouseClickListeners.invoke(mouseClickListener -> mouseClickListener
                .onMouseUp(position, button));
    }

    @Internal
    final void invokeMouseWheelMoveListeners(MouseWheelEvent mouseWheelEvent)
    {
        mouseWheelListeners.invoke(mouseWheelListener -> mouseWheelListener
                .onMouseWheelMove(mouseWheelEvent));
    }

    @API
    public final Vector getMousePosition()
    {
        return Game.convertMousePosition(this, Game.getMousePositionInFrame());
    }

    /**
     * Gibt die Hintergrundfarbe zurück.
     *
     * @return Die Hintergrundfarbe.
     */
    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * Setzt die Hintergrundfarbe.
     *
     * @param backgroundColor Die Hintergrundfarbe.
     */
    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }
}
