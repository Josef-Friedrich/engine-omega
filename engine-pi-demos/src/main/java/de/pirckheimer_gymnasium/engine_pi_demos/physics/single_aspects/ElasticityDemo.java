package de.pirckheimer_gymnasium.engine_pi_demos.physics.single_aspects;

import java.text.DecimalFormat;

import de.pirckheimer_gymnasium.engine_pi.Game;
import de.pirckheimer_gymnasium.engine_pi.Scene;
import de.pirckheimer_gymnasium.engine_pi.actor.Circle;
import de.pirckheimer_gymnasium.engine_pi.actor.Rectangle;
import de.pirckheimer_gymnasium.engine_pi.actor.Text;

/**
 * Demonstriert die Methode
 * {@link de.pirckheimer_gymnasium.engine_pi.actor.Actor#setElasticity(double)}
 */
public class ElasticityDemo extends Scene
{
    private final Rectangle ground;

    public ElasticityDemo()
    {
        getCamera().setMeter(20);
        // Ein Reckteck als Boden, auf dem die Kreise abprallen.
        ground = new Rectangle(24, 1);
        ground.setPosition(-12, -16);
        // Wir setzen die Elastizität auf 0, damit beim ersten Kreis mit der
        // Stoßzahl 0 demonstriert werden kann, dass dieser nicht abprallt.
        ground.setElasticity(0);
        ground.makeStatic();
        setGravity(0, -9.81);
        add(ground);
        double elasticity = 0;
        for (double x = -11.5; x < 12; x += 2)
        {
            createCircleWithElasticity(x, elasticity);
            elasticity += 0.1;
        }
    }

    private void createCircleWithElasticity(double x, double elasticity)
    {
        Circle circle = new Circle(1);
        add(circle);
        circle.setElasticity(elasticity);
        circle.setPosition(x, 5);
        circle.makeDynamic();
        // Eine Beschriftung mit der Stoßzahl unterhalb des Kollisionsrechtecks
        DecimalFormat df = new DecimalFormat("0.00");
        Text label = new Text(df.format(elasticity), 0.8);
        label.setPosition(x, -17);
        label.makeStatic();
        add(label);
    }

    public static void main(String[] args)
    {
        Game.start(new ElasticityDemo(), 600, 800);
    }
}
