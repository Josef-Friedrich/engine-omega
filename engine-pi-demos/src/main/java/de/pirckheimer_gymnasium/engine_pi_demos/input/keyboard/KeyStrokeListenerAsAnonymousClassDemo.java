package de.pirckheimer_gymnasium.engine_pi_demos.input.keyboard;

import java.awt.Color;
import java.awt.event.KeyEvent;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.actor.Circle;
import de.pirckheimer_gymnasium.engine_pi.event.KeyStrokeListener;

public class KeyStrokeListenerAsAnonymousClassDemo extends Scene
{
    public KeyStrokeListenerAsAnonymousClassDemo()
    {
        Circle circle = new Circle(2);
        circle.setColor(Color.RED);
        circle.addKeyStrokeListener(new KeyStrokeListener()
        {
            @Override
            public void onKeyDown(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                case KeyEvent.VK_UP:
                    circle.moveBy(0, 1);
                    break;

                case KeyEvent.VK_RIGHT:
                    circle.moveBy(1, 0);
                    break;

                case KeyEvent.VK_DOWN:
                    circle.moveBy(0, -1);
                    break;

                case KeyEvent.VK_LEFT:
                    circle.moveBy(-1, 0);
                    break;
                }
            }
        });
        add(circle);
    }

    public static void main(String[] args)
    {
        Game.start(new KeyStrokeListenerAsAnonymousClassDemo(), 600, 400);
    }
}
