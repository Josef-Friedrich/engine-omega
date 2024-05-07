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

import rocks.friedrich.engine_omega.internal.annotations.API;
import rocks.friedrich.engine_omega.internal.annotations.Internal;

/**
 * Beschreibt eine Bewegung des Mausrads. Wird vom {@link MouseWheelListener} genutzt.
 *
 * @see MouseWheelListener
 *
 * @author Michael Andonie
 */
@API
public class MouseWheelEvent {

    /**
     * Die Rotation des Mausrades. Bei Mäusen mit Präzession auch in Bruchteilen eines "Clicks"
     */
    private final double wheelRotation;


    @Internal
    public MouseWheelEvent(double wheelRotation) {
        this.wheelRotation = wheelRotation;
    }

    /**
     * Gibt die Anzahl an "Clicks" aus, die das Mausrad bewegt wurde.
     * @return  Die Anzahl an "Clicks", die das Mausrad bewegt wurde.<br>
     *          <b>Negative Werte:</b> Das Rad wurde "rauf" gedreht (weg vom Benutzer).
     *          <b>Positive Werte:</b> Das Rad wurde "runter" gedreht (hin zum Benutzer).
     * @see #getPreciseWheelRotation()
     */
    @API
    public int getWheelRotation() {
        return (int)wheelRotation;
    }

    /**
     * Gibt die Anzahl an "Clicks" aus, die das Mausrad bewegt wurde. Wenn die benutzte Maus auch Zwischenschritte
     * erlaubt, werden auch "Click-Bruchteile" mit eingerechnet.
     * @return  Die Anzahl an "Clicks", die das Mausrad bewegt wurde.<br>
     *          <b>Negative Werte:</b> Das Rad wurde "rauf" gedreht (weg vom Benutzer).
     *          <b>Positive Werte:</b> Das Rad wurde "runter" gedreht (hin zum Benutzer).
     * @see #getWheelRotation()
     */
    @API
    public double getPreciseWheelRotation() {
        return (double)wheelRotation;
    }

}
