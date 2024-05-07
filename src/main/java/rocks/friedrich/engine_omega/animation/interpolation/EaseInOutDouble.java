package rocks.friedrich.engine_omega.animation.interpolation;

import rocks.friedrich.engine_omega.animation.Interpolator;
import rocks.friedrich.engine_omega.internal.annotations.API;
import rocks.friedrich.engine_omega.internal.annotations.Internal;

public class EaseInOutDouble implements Interpolator<Double> {

    /**
     * Startpunkt. Interpolationswert bei t=0
     */
    private final double start;

    /**
     * Endpunkt. Interpolationswert bei t=1
     */
    private final double end;

    /**
     * Erstellt einen EaseInOut-Interpolator.
     * Interpoliert "smooth" zwischen den beiden Werten, beginnt also langsam (erste Ableitung = 0) und endet langsam
     * (erste Ableitung = 0). Dazwischen wächst und schrumpft die Änderungsrate dynamisch.
     *
     * @param start Der Startpunkt der Interpolation.
     * @param end   Der Endpunkt der Interpolation.
     */
    @API
    public EaseInOutDouble(double start, double end) {
        this.start = start;
        this.end = end;
    }

    @Internal
    @Override
    public Double interpolate(double progress) {
        return (double) ((Math.sin((double) progress * Math.PI - Math.PI / 2) + 1) / 2) * (this.end - this.start) + this.start;
    }
}
