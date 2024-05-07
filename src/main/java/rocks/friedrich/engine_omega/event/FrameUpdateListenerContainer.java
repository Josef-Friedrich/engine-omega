/*
 * Engine Omega ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2019 Michael Andonie and contributors.
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

package rocks.friedrich.engine_omega.event;

import rocks.friedrich.engine_omega.internal.PeriodicTask;
import rocks.friedrich.engine_omega.internal.SingleTask;
import rocks.friedrich.engine_omega.internal.annotations.API;
import rocks.friedrich.engine_omega.FrameUpdateListener;

/**
 * Die Schnittstelle {@link FrameUpdateListenerContainer} definiert Methoden zur Zeitsteuerung.
 * Sie wird von den Klassen {@link rocks.friedrich.engine_omega.actor.Actor Actor},
 * {@link rocks.friedrich.engine_omega.Scene Scene} und
 * {@link rocks.friedrich.engine_omega.Layer Layer} implementieren
 */
@API
public interface FrameUpdateListenerContainer {
    /**
     * @return Liste der {@link FrameUpdateListener}
     */
    EventListeners<FrameUpdateListener> getFrameUpdateListeners();

    /**
     * Fügt einen neuen {@link FrameUpdateListener} hinzu.
     */
    @API
    default void addFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        getFrameUpdateListeners().add(frameUpdateListener);
    }

    /**
     * Entfernt einen {@link FrameUpdateListener}.
     */
    @API
    default void removeFrameUpdateListener(FrameUpdateListener frameUpdateListener) {
        getFrameUpdateListeners().remove(frameUpdateListener);
    }

    /**
     * Führt das übergebene Runnable mit Verzögerung aus.
     *
     * @param runnable Wird im nächsten Frame ausgeführt.
     */
    @API
    default void defer(Runnable runnable) {
        FrameUpdateListener frameUpdateListener = new FrameUpdateListener() {
            @Override
            public void onFrameUpdate(double time) {
                removeFrameUpdateListener(this);
                runnable.run();
            }
        };

        addFrameUpdateListener(frameUpdateListener);
    }

    /**
     * Führt das übergebene Runnable mit einer vorgegebenen Verzögerung aus.
     *
     * @param timeInSeconds Verzögerung
     * @param runnable      Wird nach Ablauf der Verzögerung ausgeführt
     *
     * @return Listener, der manuell abgemeldet werden kann, falls die Ausführung abgebrochen werden soll.
     */
    @API
    default FrameUpdateListener delay(double timeInSeconds, Runnable runnable) {
        // Später können wir den Return-Type auf SingleTask ändern, falls das notwendig werden sollte
        FrameUpdateListener singleTask = new SingleTask(timeInSeconds, runnable, this);
        addFrameUpdateListener(singleTask);
        return singleTask;
    }

    /**
     * Führt das übergebene Runnable mit Verzögerung wiederholend aus.
     *
     * @param intervalInSeconds Verzögerung
     * @param runnable          Wird immer wieder nach Ablauf der Verzögerung ausgeführt
     *
     * @return Listener, der manuell abgemeldet werden kann, falls die Ausführung abgebrochen werden soll.
     */
    @API
    default FrameUpdateListener repeat(double intervalInSeconds, Runnable runnable) {
        // Später können wir den Return-Type auf PeriodicTask ändern, falls das notwendig werden sollte
        FrameUpdateListener periodicTask = new PeriodicTask(intervalInSeconds, runnable);
        addFrameUpdateListener(periodicTask);
        return periodicTask;
    }
}
