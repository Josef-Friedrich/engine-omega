/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha-examples/src/main/java/ea/example/showcase/car/CarDemo.java
 *
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2020 Michael Andonie and contributors.
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
package de.pirckheimer_gymnasium.engine_pi_demos.small_games;

import static de.pirckheimer_gymnasium.engine_pi.Random.range;
import static de.pirckheimer_gymnasium.engine_pi.Vector.vector;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Objects;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Layer;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Actor;
import de.pirckheimer_gymnasium.engine_pi.actor.BodyType;
import de.pirckheimer_gymnasium.engine_pi.actor.Circle;
import de.pirckheimer_gymnasium.engine_pi.actor.Image;
import de.pirckheimer_gymnasium.engine_pi.actor.Polygon;
import de.pirckheimer_gymnasium.engine_pi.actor.PrismaticJoint;
import de.pirckheimer_gymnasium.engine_pi.actor.Rectangle;
import de.pirckheimer_gymnasium.engine_pi.actor.RevoluteJoint;
import de.pirckheimer_gymnasium.engine_pi.actor.TileRegistration;
import de.pirckheimer_gymnasium.engine_pi.actor.TileMap;
import de.pirckheimer_gymnasium.engine_pi.event.CollisionEvent;
import de.pirckheimer_gymnasium.engine_pi.event.FrameUpdateListener;
import de.pirckheimer_gymnasium.engine_pi.event.PeriodicTask;
import de.pirckheimer_gymnasium.engine_pi.physics.FixtureBuilder;

public class CarDemo extends Scene implements FrameUpdateListener
{
    private static final int WIDTH = 1240;

    private static final int HEIGHT = 812;

    private static final double GROUND_FRICTION = 0.6;

    private static final double GROUND_RESTITUTION = 0.3;

    private static final int MOTOR_SPEED = 80;

    private static final Color GROUND_COLOR = new Color(85, 86, 81);

    private static final int ZOOM = 60;

    private final CarBody carBody;

    private final Wheel wheelFront;

    private final Wheel wheelBack;

    public CarDemo()
    {
        setBackgroundColor(new Color(207, 239, 252));
        Layer blend = new Layer();
        Rectangle blender = new Rectangle((double) WIDTH / ZOOM,
                (double) HEIGHT / ZOOM);
        blender.setColor(Color.BLACK);
        blend.add(blender);
        blend.setParallaxRotation(0);
        blend.setParallaxPosition(0, 0);
        blend.setLayerPosition(10);
        addLayer(blend);
        delay(.2, () -> blender.animateOpacity(.3, 0));
        Layer background = new Layer();
        background.setLayerPosition(-2);
        background.setParallaxPosition(.5, -.025);
        Rectangle backgroundColor = new Rectangle(400, 100);
        backgroundColor.setPosition(-200, -105);
        backgroundColor.setColor(new Color(0, 194, 111));
        background.add(backgroundColor);
        for (int i = -200; i < 200; i += 10)
        {
            background.add(createBackgroundTile(i));
        }
        addLayer(background);
        createGround(-70, -49).setColor(new Color(200, 104, 73));
        Actor left = createGround(-50, -20);
        Actor middle = createGround(-10, 70);
        Actor right = createGround(85, 170);
        createGround(169, 200).setColor(new Color(200, 104, 73));
        createRope(-20, -10, left, middle);
        createRope(70, 85, middle, right);
        createHill(5, range(1, 2));
        createHill(25, range(1, 2));
        createHill(45, range(1, 2));
        Layer decoration = new Layer();
        decoration.setLayerPosition(-1);
        var tiles = new TileRegistration(27, 1, .5);
        tiles.setPosition(-9, -10);
        tiles.makeStatic();
        for (int i = 0; i < tiles.getTileCountX(); i++)
        {
            tiles.setTile(i, 0, TileMap.createFromImage("car/tile01.png"));
        }
        decoration.add(tiles);
        addLayer(decoration);
        carBody = new CarBody(0, -8f);
        wheelFront = new Wheel(1.36, -8.75, new Axle(1.36, -8.6, carBody));
        wheelBack = new Wheel(-1, -8.75, new Axle(-1, -8.6, carBody));
        // Wheels automatically add axes, and axes add the car body
        add(wheelFront, wheelBack);
        setGravity(vector(0, -9.81));
        getCamera().setMeter(ZOOM);
        setFocus(carBody);
        getCamera().setOffset(vector(0, 3));
    }

    private Actor createBackgroundTile(int x)
    {
        Image image = new Image("car/background-color-grass.png", 10, 10);
        image.setPosition(x, -7);
        image.makeStatic();
        return image;
    }

    private void createRope(int startX, int endX, Actor left, Actor right)
    {
        int length = (endX - startX);
        RopeSegment[] rope = new RopeSegment[length];
        for (int i = 0; i < length; i++)
        {
            rope[i] = new RopeSegment(.8, 0.2);
            rope[i].setPosition(startX + i + 0.1, -10.2);
            rope[i].setColor(new Color(175, 90, 30));
            rope[i].makeDynamic();
            rope[i].setDensity(150);
            rope[i].setFriction(GROUND_FRICTION);
            rope[i].setElasticity(GROUND_RESTITUTION);
            rope[i].setBorderRadius(.5);
            if (i == 0)
            {
                rope[0].createRevoluteJoint(left, vector(-.1, .2)).setLimits(0,
                        0.1);
            }
            else
            {
                if (i == length - 1)
                {
                    rope[length - 1].createRevoluteJoint(right, vector(.9, .2))
                            .setLimits(0, 0.1);
                }
                rope[i - 1].createRevoluteJoint(rope[i], vector(.9, .2))
                        .setLimits(0, 0.1);
            }
        }
        add(rope);
    }

    private Ground createGround(double startX, double endX)
    {
        Ground ground = new Ground(startX, endX);
        add(ground);
        return ground;
    }

    private void createHill(double x, double height)
    {
        double offset = 180;
        for (int j = 0; j < 40 - 1; j += 1)
        {
            Polygon ground = new HillSegment(
                    vector(x + j / 2f, -10), vector(x + j / 2f
                            + 1, -10),
                    vector(x + (j + 1) / 2f,
                            -10 + Math
                                    .cos(Math.toRadians(
                                            ((j + 1) / 2f) * 18 + offset))
                                    * height + height),
                    vector(x + j / 2f,
                            -10 + Math.cos(Math.toRadians(j / 2f * 18 + offset))
                                    * height + height));
            ground.moveBy(0, -0.01);
            ground.makeStatic();
            ground.setColor(GROUND_COLOR);
            ground.setFriction(GROUND_FRICTION);
            ground.setElasticity(GROUND_RESTITUTION);
            ground.setDensity(50);
            add(ground);
        }
    }

    @Override
    public void onFrameUpdate(double pastTime)
    {
        boolean left = Game.isKeyPressed(KeyEvent.VK_J);
        boolean right = Game.isKeyPressed(KeyEvent.VK_L);
        // Antriebssteuerung
        if (left ^ right)
        {
            wheelFront.setMotorSpeed(right ? MOTOR_SPEED : -MOTOR_SPEED);
            wheelBack.setMotorSpeed(right ? MOTOR_SPEED : -MOTOR_SPEED);
        }
        else if (Game.isKeyPressed(KeyEvent.VK_SPACE))
        {
            wheelFront.setMotorSpeed(0);
            wheelBack.setMotorSpeed(0);
        }
        else
        {
            wheelFront.setMotorEnabled(false);
            wheelBack.setMotorEnabled(false);
        }
        if (carBody.getCenter().getX() < -65)
        {
            carBody.applyForce(
                    vector(10000 * (-65 - carBody.getCenter().getX()), 0),
                    carBody.getCenter());
        }
        else if (carBody.getCenter().getX() > 195)
        {
            carBody.applyForce(
                    vector(10000 * (195 - carBody.getCenter().getX()), 0),
                    carBody.getCenter());
        }
        if (carBody.getCenter().getY() < -20)
        {
            Game.transitionToScene(new CarDemo());
        }
    }

    private static Actor createParticle(double size, Vector center,
            Color initialColor, Vector impulse)
    {
        Circle particle = new Circle(size);
        particle.setBodyType(BodyType.PARTICLE);
        particle.setLayerPosition(2);
        particle.setColor(initialColor);
        particle.setCenter(center);
        particle.animateParticle(range(.1, 3f));
        particle.animateColor(range(.3, .6), Color.BLACK);
        particle.applyImpulse(impulse);
        particle.setGravityScale(1);
        particle.setLinearDamping(range(18, 22));
        particle.setLayerPosition(-1);
        return particle;
    }

    private static Actor createSplitter(Vector center)
    {
        Polygon splitter = new Polygon(vector(0, 0), vector(0.15, 0),
                vector(0.15, 0.05));
        splitter.setBodyType(BodyType.PARTICLE);
        splitter.rotateBy(range(0, 360));
        splitter.setLayerPosition(2);
        splitter.setColor(new Color(119, 82, 54));
        splitter.setCenter(center.add(range(-.2, .2), range(-.2, .2)));
        splitter.animateParticle(range(.1, 3f));
        splitter.setGravityScale(1);
        splitter.setLinearDamping(range(18, 22));
        splitter.setLayerPosition(-1);
        return splitter;
    }

    private static PeriodicTask createSplitterEmitter(Actor actor)
    {
        return (counter) -> {
            for (CollisionEvent<Actor> collision : actor.getCollisions())
            {
                if (collision.getColliding() instanceof Wood
                        && actor.getVelocity().getLength() > 1)
                {
                    for (Vector point : collision.getPoints())
                    {
                        Objects.requireNonNull(actor.getLayer())
                                .add(createSplitter(point));
                    }
                }
            }
        };
    }

    private static class Ground extends Rectangle implements Mud
    {
        public Ground(double startX, double endX)
        {
            super(endX - startX, 10);
            setPosition(startX, -20);
            setColor(GROUND_COLOR);
            makeStatic();
            setFriction(GROUND_FRICTION);
            setElasticity(GROUND_RESTITUTION);
            setDensity(150);
            setBorderRadius(.1);
        }
    }

    private static class HillSegment extends Polygon implements Mud
    {
        public HillSegment(Vector... vectors)
        {
            super(vectors);
        }
    }

    private static class Axle extends Rectangle implements FrameUpdateListener
    {
        private final PrismaticJoint spring;

        private final CarBody carBody;

        public Axle(double cx, double cy, CarBody carBody)
        {
            super(.2, .9);
            setCenter(cx, cy);
            makeDynamic();
            setColor(new Color(255, 255, 255, 0));
            setDensity(50);
            this.carBody = carBody;
            spring = createPrismaticJoint(carBody,
                    getCenterRelative().add(0, getHeight() / 2), 90);
            spring.setLimits(-.15, .15);
            addMountListener(
                    () -> Objects.requireNonNull(getLayer()).add(carBody));
        }

        @Override
        public void onFrameUpdate(double pastTime)
        {
            // Federeffekt für die Achsen
            double translation = spring.getTranslation();
            spring.setMotorSpeed(Math
                    .sin(min(max(-0.15, translation), 0.15) / .15 * Math.PI / 2)
                    * -.3);
            spring.setMaximumMotorForce(5000);
        }

        public CarBody getCarBody()
        {
            return carBody;
        }
    }

    private static class Wheel extends Image
    {
        private final RevoluteJoint motor;

        public Wheel(double cx, double cy, Axle axle)
        {
            super("car/wheel-back.png", 1.4, 1.4);
            setFixture(() -> FixtureBuilder.circle(.7, .7, .7));
            setCenter(cx, cy);
            setDensity(100);
            makeDynamic();
            setFriction(.5);
            setElasticity(.2);
            setDensity(150);
            setAngularDamping(1);
            setLayerPosition(2);
            motor = createRevoluteJoint(axle, getCenterRelative());
            motor.setMaximumMotorTorque(5000);
            addMountListener(
                    () -> Objects.requireNonNull(getLayer()).add(axle));
            addCollisionListener(axle.getCarBody(),
                    CollisionEvent::ignoreCollision);
            repeat(.025, (counter) -> {
                for (CollisionEvent<Actor> collision : getCollisions())
                {
                    if (collision.getColliding() instanceof Mud)
                    {
                        double velocity = getVelocity().getLength();
                        double overTwist = abs(
                                getAngularVelocity() * Math.PI * 2 * 0.7)
                                / velocity;
                        boolean slowMoving = abs(getVelocity().getX()) < 0.5
                                && abs(getAngularVelocity()) < 0.3;
                        if (overTwist > 0.95 && overTwist < 1.05 || slowMoving)
                        {
                            continue;
                        }
                        Vector impulse = collision.getTangentNormal() //
                                .rotate(90) //
                                .multiply(min(max(-1, overTwist - 1), 1));
                        for (Vector point : collision.getPoints())
                        {
                            double size = range(0.05, .15);
                            Vector center = point.add(point
                                    .getDistance(getCenter()).multiply(size));
                            Color color = ((Mud) collision.getColliding())
                                    .getColor();
                            Objects.requireNonNull(getLayer())
                                    .add(createParticle(size, center, color,
                                            impulse.rotate(range(-15, 15))));
                        }
                    }
                }
            });
            repeat(.25, createSplitterEmitter(this));
        }

        public void setMotorSpeed(int speed)
        {
            motor.setMotorSpeed(speed);
        }

        public void setMotorEnabled(boolean enabled)
        {
            motor.setMotorEnabled(enabled);
        }
    }

    private static class CarBody extends Image
    {
        public CarBody(double cx, double cy)
        {
            super("car/truck-240px.png", 4, 1.2);
            setCenter(cx, cy);
            makeDynamic();
            setDensity(100);
            setAngularDamping(0.3);
            setFriction(0.5);
            setFixtures(
                    "R0,.45,2,.45&P2,1.2,2.6,1.15,3.8,0.8,3.95,0.45,2,0.45&R1,0,2,0.6");
            repeat(.05, (counter) -> {
                if (getVelocity().getLength() < 0.1)
                {
                    return;
                }
                for (CollisionEvent<Actor> collision : getCollisions())
                {
                    if (collision.getColliding() instanceof Mud)
                    {
                        for (Vector point : collision.getPoints())
                        {
                            double size = range(0.05, .15);
                            Vector impulse = vector(range(-1f, 1f),
                                    range(-1f, 1f));
                            Objects.requireNonNull(getLayer())
                                    .add(createParticle(size, point,
                                            Color.YELLOW, impulse));
                        }
                    }
                }
            });
            repeat(.25, createSplitterEmitter(this));
        }
    }

    private static class RopeSegment extends Rectangle implements Wood
    {
        public RopeSegment(double width, double height)
        {
            super(width, height);
        }
    }

    private interface Mud
    {
        Color getColor();
    }

    private interface Wood
    {
        // marker
    }

    public static void main(String[] args)
    {
        Game.start(new CarDemo(), WIDTH, HEIGHT);
    }
}
