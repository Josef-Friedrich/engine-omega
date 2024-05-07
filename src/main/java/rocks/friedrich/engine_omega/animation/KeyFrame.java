package rocks.friedrich.engine_omega.animation;

import rocks.friedrich.engine_omega.animation.interpolation.EaseInOutDouble;
import rocks.friedrich.engine_omega.animation.interpolation.LinearDouble;
import rocks.friedrich.engine_omega.internal.annotations.API;
import rocks.friedrich.engine_omega.internal.annotations.Internal;

/**
 * Beschreibt einen Keyframe.
 *
 * @param <Value> Werttyp, der animiert werden soll.
 *
 * @author Michael Andonie
 */
public class KeyFrame<Value extends Number> implements Comparable<KeyFrame<Value>> {

    /**
     * Der Typ des Keyframes. Bestimmt die Interpolationsart <b>hin zum nächsten Keyframe</b>.
     */
    private Type type;

    /**
     * Der Zeitpunkt, zu dem der Keyframe den angegebenen Wert markiert.
     */
    private double timecode;

    /**
     * Der Wert, den dieser Keyframe markiert.
     */
    private Value value;

    /**
     * Referenz auf den Nachfolgenden Keyframe, falls vorhanden.
     */
    private KeyFrame<Value> next = null;

    /**
     * Erstellt einen Keyframe
     *
     * @param type     Der Typ des Keyframes
     * @param timecode Der Timecode des Keyframes
     */
    @API
    public KeyFrame(Value value, Type type, double timecode) {
        this.value = value;
        this.type = type;
        setTimecode(timecode);
    }

    @API
    public void setValue(Value value) {
        this.value = value;
    }

    @API
    public Value getValue() {
        return value;
    }

    @API
    public void setType(Type type) {
        this.type = type;
    }

    @API
    public Type getType() {
        return type;
    }

    @API
    public void setTimecode(double timecode) {
        if (timecode < 0) {
            throw new IllegalArgumentException("Der Timecode eines Keyframes kann nicht kleiner als 0 sein. Er war: " + timecode);
        }
        this.timecode = timecode;
    }

    @API
    public double getTimecode() {
        return timecode;
    }

    @Internal
    void setNext(KeyFrame<Value> next) {
        this.next = next;
    }

    @Override
    public int compareTo(KeyFrame<Value> o) {
        return (int) ((this.getTimecode() - o.getTimecode()) * 1000);
    }

    @Internal
    Interpolator<Double> generateInterpolator(Value destinationValue) {
        switch (type) {
            case LINEAR:
                return new LinearDouble(value.doubleValue(), destinationValue.doubleValue());
            case SMOOTHED_SIN:
                return new EaseInOutDouble(value.doubleValue(), destinationValue.doubleValue());
        }
        throw new RuntimeException("The impossible happened.");
    }

    @Internal
    boolean hasNext() {
        return next != null;
    }

    @Internal
    KeyFrame<Value> getNext() {
        return next;
    }

    /**
     * Aufzählung der verschiedenen Typen von Keyframes.
     * <ul>
     * <li>linear: Der Keyframe interpoliert linear zum nachfolgenden Keyframe</li>
     * <li>smooth: Der Keyframe interpoliert mit einer Sinuskurve zum nächsten Keyframe</li>
     * </ul>
     */
    @API
    public enum Type {
        SMOOTHED_SIN, LINEAR;
    }
}
