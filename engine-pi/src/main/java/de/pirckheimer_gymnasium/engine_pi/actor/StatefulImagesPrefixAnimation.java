/*
 * Engine Pi ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2024 Josef Friedrich and contributors.
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
package de.pirckheimer_gymnasium.engine_pi.actor;

/**
 * Lädt alle Bilddateien mit einem bestimmten <b>Präfix</b> in einem bestimmten
 * Verzeichnis in eine Animation.
 *
 * @param <State> Typ der Zustände, zwischen denen in der Animation gewechselt
 *     werden soll.
 *
 * @author Josef Friedrich
 *
 * @since 0.27.0
 */
public class StatefulImagesPrefixAnimation<State>
        extends StatefulAnimation<State>
{
    /**
     * @param width Die Breite in Meter der animierten Figur.
     * @param height Die Höhe in Meter der animierten Figur.
     * @param frameDuration Die Dauer in Sekunden, die die Einzelbilder aktiv
     *     bleiben.
     */
    public StatefulImagesPrefixAnimation(double width, double height,
            double frameDuration)
    {
        super(width, height, frameDuration);
    }

    /**
     * Lädt alle Bilddateien mit einem bestimmten <b>Präfix</b> in einem
     * bestimmten Verzeichnis in eine Animation.
     *
     * @param state Der Zustand, unter dem die Animation gespeichert wird.
     * @param frameDuration Die Dauer in Sekunden, die die Einzelbilder aktiv
     *     bleiben.
     * @param directoryPath Der Pfad zum Verzeichnis, in dem die einzuladenden
     *     Bilder liegen.
     * @param prefix Das Pfad-Präfix. Diese Funktion sucht <a>alle Dateien mit
     *     dem gegebenen Präfix</a> (im angegebenen Ordner) und fügt sie in
     *     aufsteigender Reihenfolge der Animation hinzu.
     */
    public void addState(State state, double frameDuration,
            String directoryPath, String prefix)
    {
        addState(state, Animation.createFromImagesPrefix(frameDuration, width,
                height, directoryPath, prefix));
    }

    /**
     * Lädt alle Bilddateien mit einem bestimmten <b>Präfix</b> in einem
     * bestimmten Verzeichnis in eine Animation.
     *
     * @param state Der Zustand, unter dem die Animation gespeichert wird.
     * @param directoryPath Der Pfad zum Verzeichnis, in dem die einzuladenden
     *     Bilder liegen.
     * @param prefix Das Pfad-Präfix. Diese Funktion sucht <a>alle Dateien mit
     *     dem gegebenen Präfix</a> (im angegebenen Ordner) und fügt sie in
     *     aufsteigender Reihenfolge der Animation hinzu.
     */
    public void addState(State state, String directoryPath, String prefix)
    {
        addState(state, Animation.createFromImagesPrefix(frameDuration, width,
                height, directoryPath, prefix));
    }
}
