package de.pirckheimer_gymnasium.engine_pi_demos.game;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.Vector;
import de.pirckheimer_gymnasium.engine_pi.actor.Actor;
import de.pirckheimer_gymnasium.engine_pi.actor.Image;
import de.pirckheimer_gymnasium.engine_pi.actor.Text;
import de.pirckheimer_gymnasium.engine_pi.event.FrameUpdateListener;
import de.pirckheimer_gymnasium.engine_pi.util.TextUtil;

public class SetPixelMultiplicationDemo extends Scene
{
    static
    {
        Game.setPixelMultiplication(2);
    }

    public SetPixelMultiplicationDemo()
    {
        addText("Text", 0, 0).setPosition(0, -1);
        Actor image = addImage(
                "Pixel-Adventure-1/Main Characters/Pink Man/Jump (32x32).png",
                1, 1);
        getCamera().setFocus(image);
        setBackgroundColor("white");
    }

    public static void main(String[] args)
    {
        Game.start(125, 200, new SetPixelMultiplicationDemo());
    }
}
