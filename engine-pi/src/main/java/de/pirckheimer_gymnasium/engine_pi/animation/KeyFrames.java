/*
 * Source: https://github.com/engine-alpha/engine-alpha/blob/4.x/engine-alpha/src/main/java/ea/animation/KeyFrames.java
 *
 * Engine Pi ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2024 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.pirckheimer_gymnasium.engine_pi.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

import de.pirckheimer_gymnasium.engine_pi.animation.interpolation.ConstantInterpolator;
import de.pirckheimer_gymnasium.engine_pi.annotations.API;
import de.pirckheimer_gymnasium.engine_pi.event.FrameUpdateListener;

/**
 * Utility-Klasse
 *
 * @author Michael Andonie
 */
public class KeyFrames implements FrameUpdateListener
{
    /**
     * Der Consumer, der durch dieses Set an Keyframes animiert wird.
     */
    private final Consumer<Double> toAnimate;

    /**
     * Locked-Flag. Wird true gesetzt, sobald die Keyframes animieren.
     */
    private boolean isLocked = false;

    private double currentAnimationTime;

    private double currentInterpolationEndpoint;

    private KeyFrame<Double> currentKeyframe;

    private Interpolator<Double> currentInterpolator;

    /**
     * Gibt an, ob dieses Set an Keyframes unbegrenzt weitergeht.
     * <code>false</code>: das Set wird nicht weiter animiert nach Ablauf des
     * letzten Keyframes. <code>true</code>: das Set animiert nach Ablauf des
     * letzten Keyframes die Konstante Funktion mit dem Wert des letzten
     * Keyframes.
     */
    private boolean infinite = true;

    private boolean paused = false;

    /**
     * Erstellt ein leeres Set an Keyframes
     *
     * @param toAnimate Die Funktion, die durch dieses Set an Keyframes
     *     interpoliert wird.
     */
    @API
    public KeyFrames(Consumer<Double> toAnimate)
    {
        this.toAnimate = toAnimate;
    }

    @API
    public void addKeyframe(KeyFrame<Double> keyFrame)
    {
        if (isLocked)
        {
            throw new RuntimeException(
                    "Keyframes können nach Beginn der Animation nicht mehr hinzugefügt werden.");
        }
        keyFrames.add(keyFrame);
        Collections.sort(keyFrames);
    }

    @API
    public void setPaused(boolean paused)
    {
        this.paused = paused;
    }

    @API
    public boolean isPaused()
    {
        return this.paused;
    }

    /**
     * Setzt, ob diese Animation unendlich lange gehen soll.
     *
     * @param infinite Ist dieser Wert <code>true</code>, so wird nach Ablauf
     *     des letzten Keyframes jeden weiteren Frame der letzte Wert
     *     interpoliert. Ist dieser Wert <code>false</code>, so wird nach dem
     *     letzten Keyframe keine weitere Interpolation ausgeführt.
     *
     * @see #isInfinite()
     */
    @API
    public void setInifinite(boolean infinite)
    {
        this.infinite = infinite;
    }

    /**
     * Gibt an, ob dieses Keyframe-Set unendlich animiert wird.
     *
     * @return Ob das Keyframe-Set unendlich animiert wird.
     *
     * @see #setInifinite(boolean)
     */
    @API
    public boolean isInfinite()
    {
        return this.infinite;
    }

    /**
     * Das Set an Keyframes
     */
    private final ArrayList<KeyFrame<Double>> keyFrames = new ArrayList<>();

    @Override
    public void onFrameUpdate(double pastTime)
    {
        if (paused)
        {
            return;
        }
        if (!isLocked)
        {
            prepForAnimation();
        }
        if (currentInterpolationEndpoint == -1)
        {
            // End State:
            if (infinite)
            {
                currentInterpolator.interpolate(0);
            }
            else
            {
                // TODO cleanup!
            }
            return;
        }
        if (currentAnimationTime < currentInterpolationEndpoint)
        {
            // Business as usual: Interpolation
            // Progres = [time since last key frame] / [time between current and
            // next key frame]
            toAnimate.accept(currentInterpolator.interpolate(
                    (currentAnimationTime - currentKeyframe.getTimecode())
                            / (currentInterpolationEndpoint
                                    - currentKeyframe.getTimecode())));
        }
        else
        {
            // Key Frame Update
            setupKeyframeForInterpolation(currentKeyframe.getNext());
        }
        // Time Update
        currentAnimationTime += pastTime;
    }

    private void prepForAnimation()
    {
        if (keyFrames.isEmpty())
        {
            throw new RuntimeException(
                    "Ein leeres Keyframe-Set sollte animiert werden.");
        }
        currentAnimationTime = 0;
        KeyFrame<Double> first = keyFrames.get(0);
        if (first.getTimecode() != 0)
        {
            // Add Keyframe at t=0 with value of previously first keyframe.
            addKeyframe(
                    new KeyFrame<>(first.getValue(), KeyFrame.Type.LINEAR, 0));
        }
        for (int i = 0; i < keyFrames.size() - 1; i++)
        {
            keyFrames.get(i).setNext(keyFrames.get(i + 1));
        }
        setupKeyframeForInterpolation(keyFrames.get(0));
        isLocked = true;
    }

    private void setupKeyframeForInterpolation(KeyFrame<Double> keyFrame)
    {
        currentKeyframe = keyFrame;
        if (keyFrame.hasNext())
        {
            currentInterpolator = keyFrame
                    .generateInterpolator(keyFrame.getNext().getValue());
            currentInterpolationEndpoint = keyFrame.getNext().getTimecode();
        }
        else
        {
            currentInterpolator = new ConstantInterpolator<>(
                    keyFrame.getValue());
            currentInterpolationEndpoint = -1;
        }
    }
}
