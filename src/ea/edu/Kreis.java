package ea.edu;

import ea.actor.Circle;
import ea.internal.annotations.API;

/**
 * EDU-Variante von {@link Circle}.
 *
 * @author Michael Andonie
 */
@API
public class Kreis extends EduGeometrie<Circle> {
    /**
     * Konstruktor. Erstellt einen Kreis.
     *
     * @param radius Durchmesser des Kreises
     */
    @API
    public Kreis(float radius) {
        super(new Circle(radius * 2));
    }
}
