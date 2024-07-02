/*
 * Source: https://github.com/gurkenlabs/litiengine/blob/main/litiengine/src/main/java/de/gurkenlabs/litiengine/sound/SoundEngine.java
 *
 * MIT License
 *
 * Copyright (c) 2016 - 2024 Gurkenlabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.pirckheimer_gymnasium.engine_pi;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.LineUnavailableException;

import de.pirckheimer_gymnasium.engine_pi.resources.SoundContainer;
import de.pirckheimer_gymnasium.engine_pi.sound.IntroTrack;
import de.pirckheimer_gymnasium.engine_pi.sound.LoopedTrack;
import de.pirckheimer_gymnasium.engine_pi.sound.MusicPlayback;
import de.pirckheimer_gymnasium.engine_pi.sound.Playback;
import de.pirckheimer_gymnasium.engine_pi.sound.Sound;
import de.pirckheimer_gymnasium.engine_pi.sound.SoundPlayback;
import de.pirckheimer_gymnasium.engine_pi.sound.Track;

/**
 * Die {@link Jukebox} Klasse bietet Methoden an, um Klänge (Sounds) und Musik
 * (Musik) im Spiel wiederzugeben.
 *
 * <p>
 * Die {@link Jukebox} kann standardmäßig {@code .wav}, {@code .mp3} und
 * {@code .ogg} Dateien abspielen. Wenn Sie andere Dateierweiterungen benötigen,
 * müssen Sie eine eigene SPI-Implementierung schreiben und sie in Ihr Projekt
 * einbauen.
 */
public final class Jukebox
{
    public static final ExecutorService EXECUTOR = Executors
            .newCachedThreadPool(new ThreadFactory()
            {
                private int id = 0;

                @Override
                public Thread newThread(Runnable r)
                {
                    return new Thread(r, "Sound Playback Thread " + ++id);
                }
            });

    private static final Logger log = Logger.getLogger(Jukebox.class.getName());

    private static MusicPlayback music;

    private static final Collection<MusicPlayback> allMusic = ConcurrentHashMap
            .newKeySet();

    private static final Collection<SoundPlayback> allSounds = ConcurrentHashMap
            .newKeySet();

    private static SoundContainer soundsContainer = Resources.SOUNDS;

    /**
     * Sets the currently playing track to a {@code LoopedTrack} with the
     * specified music {@code Sound}. This has no effect if the specified track
     * is already playing.
     *
     * @param music The {@code Sound} to be played.
     * @return The playback of the music
     */
    public static MusicPlayback playMusic(Sound music)
    {
        return playMusic(new LoopedTrack(music));
    }

    /**
     * Sets the currently playing track to the specified track. This has no
     * effect if the specified track is already playing.
     *
     * @param track The track to play
     * @return The playback of the music
     */
    public static MusicPlayback playMusic(Track track)
    {
        return playMusic(track, null, false, true);
    }

    /**
     * Sets the currently playing track to a {@code LoopedTrack} with the
     * specified music {@code Sound}. This has no effect if the specified track
     * is already playing.
     *
     * @param music The {@code Sound} to be played.
     * @return The playback of the music
     */
    public static MusicPlayback playMusic(String music)
    {
        return playMusic(getSound(music));
    }

    /**
     * Sets the currently playing track to the specified track.
     *
     * @param track   The track to play
     * @param restart Whether to restart if the specified track is already
     *                playing, determined by {@link Object#equals(Object)}
     * @return The playback of the music
     */
    public static MusicPlayback playMusic(Track track, boolean restart)
    {
        return playMusic(track, null, restart, true);
    }

    /**
     * Plays the specified track.
     *
     * @param track   The track to play
     * @param restart Whether to restart if the specified track is already
     *                playing, determined by {@link Object#equals(Object)}
     * @param stop    Whether to stop an existing track if present
     * @return The playback of the music
     */
    public static MusicPlayback playMusic(Track track, boolean restart,
            boolean stop)
    {
        return playMusic(track, null, restart, stop);
    }

    public static MusicPlayback playIntroTrack(String track, String loop)
    {
        return playMusic(new IntroTrack(getSound(track), getSound(loop)));
    }

    /**
     * Plays the specified track, optionally configuring it before starting.
     *
     * @param track   The track to play
     * @param config  A call to configure the playback prior to starting, which
     *                can be {@code null}
     * @param restart Whether to restart if the specified track is already
     *                playing, determined by {@link Object#equals(Object)}
     * @param stop    Whether to stop an existing track if present
     * @return The playback of the music
     */
    public static synchronized MusicPlayback playMusic(Track track,
            Consumer<? super MusicPlayback> config, boolean restart,
            boolean stop)
    {
        if (!restart && music != null && music.isPlaying()
                && music.getTrack().equals(track))
        {
            return music;
        }
        try
        {
            MusicPlayback playback = new MusicPlayback(track);
            if (config != null)
            {
                config.accept(playback);
            }
            if (stop)
            {
                stopMusic();
            }
            allMusic.add(playback);
            playback.start();
            music = playback;
            return playback;
        }
        catch (LineUnavailableException | IllegalArgumentException e)
        {
            resourceFailure(e);
            return null;
        }
    }

    /**
     * Gets the "main" music that is playing. This usually means the last call
     * to {@code playMusic}, though if the music has been stopped it will be
     * {@code null}.
     *
     * @return The main music, which could be {@code null}.
     */
    public static synchronized MusicPlayback getMusic()
    {
        return music;
    }

    /**
     * Liefert eine Liste mit allen Musikwiedergaben.
     *
     * @return Eine Liste mit allen Musikwiedergaben.
     */
    public static synchronized Collection<MusicPlayback> getAllMusic()
    {
        return Collections.unmodifiableCollection(allMusic);
    }

    /**
     * Stoppt die Wiedergabe der aktuellen Hintergrundmusik.
     */
    public static synchronized void stopMusic()
    {
        for (MusicPlayback track : allMusic)
        {
            track.cancel();
        }
    }

    public static Sound getSound(String filePath)
    {
        return soundsContainer.get(filePath);
    }

    /**
     * Creates an {@code SoundPlayback} object that can be configured prior to
     * starting.
     *
     * <p>
     * Unlike the {@code playSound} methods, the {@code SoundPlayback} objects
     * returned by this method must be started using the
     * {@link Playback#start()} method. However, necessary resources are
     * acquired <em>immediately</em> upon calling this method, and will remain
     * in use until the playback is either cancelled or finalized.
     *
     * @param sound The sound to play
     * @param loop  Whether to loop the sound
     * @return An {@code SoundPlayback} object that can be configured prior to
     *         starting, but will need to be manually started.
     */
    public static SoundPlayback createSoundPlayback(Sound sound, boolean loop)
    {
        try
        {
            return new SoundPlayback(sound, loop);
        }
        catch (LineUnavailableException | IllegalArgumentException e)
        {
            resourceFailure(e);
            return null;
        }
    }

    public static SoundPlayback createSoundPlayback(String filePath,
            boolean loop)
    {
        return createSoundPlayback(getSound(filePath), loop);
    }

    public static void addSound(SoundPlayback playback)
    {
        allSounds.add(playback);
    }

    public static SoundPlayback playSound(Sound sound, boolean loop)
    {
        if (sound == null)
        {
            return null;
        }
        SoundPlayback playback = createSoundPlayback(sound, loop);
        if (playback == null)
        {
            return null;
        }
        playback.start();
        return playback;
    }

    public static SoundPlayback playSound(final String filePath, boolean loop)
    {
        return playSound(getSound(filePath), loop);
    }

    public static SoundPlayback playSound(final String filePath)
    {
        return playSound(filePath, false);
    }

    private static void resourceFailure(Throwable e)
    {
        log.log(Level.WARNING, "could not open a line", e);
    }
}