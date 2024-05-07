package rocks.friedrich.engine_omega.animation.interpolation;

import rocks.friedrich.engine_omega.animation.Interpolator;
import rocks.friedrich.engine_omega.internal.annotations.API;
import rocks.friedrich.engine_omega.internal.annotations.Internal;

/**
 * Interpoliert auf einer kompletten Sinuskurve.
 *
 * @author Michael Andonie
 */
public class SinusDouble implements Interpolator<Double> {

    /**
     * Der Startwert (und Endwert)
     */
    private final double start;

    /**
     * Die Amplitude der Sinuskurve
     */
    private final double amplitude;

    /**
     * Erstellt einen <code>SinusDouble</code>-Interpolator.
     * @param start     Der Startpunkt der Sinuskurve, die dieser Interpolator interpoliert. Dieser Punkt wird also
     *                  erreicht zu Beginn, bei Ablauf der halben Zeit sowie zum Ende der Interpolation.
     * @param amplitude Die Amplitude der Sinuskurve. Bei 1/4 der Zeit ist der Wert der Interpolation also
     *                  <code>start+amplitude</code> und bei 3/4 ist der Wert <code>start-amplitude</code>.<br>
     *                  Negative Werte sind natürlich auch möglich.
     */
    @API
    public SinusDouble(double start, double amplitude) {
        this.start = start;
        this.amplitude = amplitude;
    }

    @Internal
    @Override
    public Double interpolate(double progress) {
        return (double)Math.sin(Math.PI * progress * 2) * amplitude + start;
    }
}
