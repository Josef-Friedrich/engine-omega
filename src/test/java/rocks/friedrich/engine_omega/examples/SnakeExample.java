package rocks.friedrich.engine_omega.examples;

import java.awt.Color;
import java.awt.event.KeyEvent;

import rocks.friedrich.engine_omega.FrameUpdateListener;
import rocks.friedrich.engine_omega.Game;
import rocks.friedrich.engine_omega.Random;
import rocks.friedrich.engine_omega.Scene;
import rocks.friedrich.engine_omega.Vector;
import rocks.friedrich.engine_omega.actor.Circle;
import rocks.friedrich.engine_omega.actor.Text;
import rocks.friedrich.engine_omega.collision.CollisionEvent;
import rocks.friedrich.engine_omega.collision.CollisionListener;
import rocks.friedrich.engine_omega.event.KeyListener;

/**
 * @see https://engine-alpha.org/wiki/v4.x/Game_Loop
 */
public class SnakeExample extends Scene
{
    private Text scoreText = new Text("Score: 0", 1.4f);

    private int score = 0;

    private Snake snake = new Snake();

    public SnakeExample()
    {
        add(snake);
        scoreText.setPosition(-9, 5);
        add(scoreText);
        placeRandomGoodie();
    }

    public void setScore(int score)
    {
        this.score = score;
        this.scoreText.setContent("Score: " + score);
    }

    public void increaseScore()
    {
        setScore(score + 1);
    }

    public void placeRandomGoodie()
    {
        double x = Random.range() * 10 - 5;
        double y = Random.range() * 10 - 5;
        Goodie goodie = new Goodie();
        goodie.setCenter(x, y);
        add(goodie);
        goodie.addCollisionListener(snake, goodie);
    }

    private class Snake extends Circle
            implements FrameUpdateListener, KeyListener
    {
        private Vector v_per_s = new Vector(0, 0);

        public Snake()
        {
            super(1);
            setColor(Color.GREEN);
        }

        @Override
        public void onFrameUpdate(double timeInS)
        {
            this.moveBy(v_per_s.multiply(timeInS));
        }

        @Override
        public void onKeyDown(KeyEvent keyEvent)
        {
            switch (keyEvent.getKeyCode())
            {
            case KeyEvent.VK_W:
                v_per_s = new Vector(0, 5);
                break;

            case KeyEvent.VK_A:
                v_per_s = new Vector(-5, 0);
                break;

            case KeyEvent.VK_S:
                v_per_s = new Vector(0, -5);
                break;

            case KeyEvent.VK_D:
                v_per_s = new Vector(5, 0);
                break;
            }
        }
    }

    private class Goodie extends Text implements CollisionListener<Snake>
    {
        public Goodie()
        {
            super("Eat Me!", 1);
            setColor(Color.RED);
        }

        @Override
        public void onCollision(CollisionEvent<Snake> collisionEvent)
        {
            increaseScore();
            this.remove();
            placeRandomGoodie();
        }
    }

    public static void main(String[] args)
    {
        Game.start(600, 400, new SnakeExample());
        // Game.setDebug(true);
    }
}
