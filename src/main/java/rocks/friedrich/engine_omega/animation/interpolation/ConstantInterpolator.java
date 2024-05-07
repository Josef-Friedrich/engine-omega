package rocks.friedrich.engine_omega.animation.interpolation;

import rocks.friedrich.engine_omega.animation.Interpolator;
import rocks.friedrich.engine_omega.internal.annotations.API;
import rocks.friedrich.engine_omega.internal.annotations.Internal;

/**
 * Ein Interpolator, der eine konstante Funktion darstellt.
 *
 * @param <Value> Ein beliebiger Typ zum Interpolieren
 */
public class ConstantInterpolator<Value> implements Interpolator<Value> {

    private final Value value;

    /**
     * Erstellt einen konstanten Interpolator
     *
     * @param value Der stets auszugebende Wert
     */
    @API
    public ConstantInterpolator(Value value) {
        this.value = value;
    }

    @Internal
    @Override
    public Value interpolate(double progress) {
        return value;
    }
}
