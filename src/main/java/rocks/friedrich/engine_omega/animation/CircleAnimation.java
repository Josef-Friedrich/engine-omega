package rocks.friedrich.engine_omega.animation;

import rocks.friedrich.engine_omega.actor.Actor;
import rocks.friedrich.engine_omega.animation.interpolation.CosinusDouble;
import rocks.friedrich.engine_omega.animation.interpolation.LinearDouble;
import rocks.friedrich.engine_omega.animation.interpolation.SinusDouble;
import rocks.friedrich.engine_omega.event.AggregateFrameUpdateListener;
import rocks.friedrich.engine_omega.internal.annotations.API;
import rocks.friedrich.engine_omega.Vector;

/**
 * Animiert einen Actor in einem Kreis.
 *
 * @author Michael Andonie
 */
public class CircleAnimation extends AggregateFrameUpdateListener {
    /**
     * Erstellt eine Circle-Animation. Animiert ein Actor-Objekt anhand seines Mittelpunkts
     * um einen Drehungsmittelpunkt.
     *
     * @param actor             Der zu animierende Actor.
     * @param rotationCenter    Das Zentrum der Drehung.
     * @param durationInSeconds Die Dauer einer ganzen Umdrehung in Millisekunden.
     * @param circleClockwise   <code>true</code>: Drehung im Uhrzeigersinn. <code>false</code>: Drehung entgegen
     *                          des Uhrzeigersinns.
     * @param rotateActor       <code>true</code>: Der Actor rotiert auch.
     *                          <code>false</code>: Die Rotation des Actors bleibt fix. Nur seine Position verändert
     *                          sich durch die Animation.
     */
    @API
    public CircleAnimation(Actor actor, Vector rotationCenter, double durationInSeconds, boolean circleClockwise, boolean rotateActor) {
        Vector currentActorCenter = actor.getCenter();
        double radius = new Vector(rotationCenter, currentActorCenter).getLength();
        Vector rightPoint = rotationCenter.add(new Vector(radius, 0));

        ValueAnimator<Double> aX = new ValueAnimator<>(durationInSeconds, x -> actor.setCenter(x, actor.getCenter().getY()), new CosinusDouble(rightPoint.getX(), radius), AnimationMode.REPEATED, this);
        ValueAnimator<Double> aY = new ValueAnimator<>(durationInSeconds, y -> actor.setCenter(actor.getCenter().getX(), y), new SinusDouble(rotationCenter.getY(), circleClockwise ? -radius : radius), AnimationMode.REPEATED, this);

        // Winkel zwischen gewünschtem Startpunkt und aktueller Actor-Position (immer in [0;PI])
        double angle = rotationCenter.negate().add(rightPoint).getAngle(rotationCenter.negate().add(currentActorCenter));

        if (circleClockwise && currentActorCenter.getY() > rotationCenter.getY() || !circleClockwise && currentActorCenter.getY() < rotationCenter.getY()) {
            // Gedrehter Winkel ist bereits über die Hälfte
            angle = 360 - angle;
        }

        double actualProgress = angle / 360;
        aX.setProgress(actualProgress);
        aY.setProgress(actualProgress);

        addFrameUpdateListener(aX);
        addFrameUpdateListener(aY);

        if (rotateActor) {
            double rotationAngle = circleClockwise ? angle : -angle;
            ValueAnimator<Double> aR = new ValueAnimator<>(durationInSeconds, actor::setRotation, new LinearDouble(-rotationAngle, -rotationAngle + 360 * (circleClockwise ? -1 : 1)), AnimationMode.REPEATED, actor);
            aR.setProgress(actualProgress);
            addFrameUpdateListener(aR);
        }
    }
}
