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
import rocks.friedrich.engine_omega.FrameUpdateListener;

/**
 * Aggregiert mehrere {@link FrameUpdateListener}, um sie gemeinsam pausieren zu können.
 *
 * @author Niklas Keller
 */
public abstract class AggregateFrameUpdateListener implements FrameUpdateListener, FrameUpdateListenerContainer {

    private final EventListeners<FrameUpdateListener> listeners = new EventListeners<>();

    private boolean paused = false;

    @API
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @API
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void onFrameUpdate(double deltaSeconds) {
        if (!paused) {
            listeners.invoke(listener -> listener.onFrameUpdate(deltaSeconds));
        }
    }

    @Override
    public EventListeners<FrameUpdateListener> getFrameUpdateListeners() {
        return listeners;
    }
}
