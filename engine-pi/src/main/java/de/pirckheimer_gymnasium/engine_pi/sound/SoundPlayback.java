/*
 * Source: https://github.com/gurkenlabs/litiengine/blob/main/litiengine/src/main/java/de/gurkenlabs/litiengine/sound/SoundPlayback.java
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
package de.pirckheimer_gymnasium.engine_pi.sound;

import javax.sound.sampled.LineUnavailableException;

import de.pirckheimer_gymnasium.engine_pi.Jukebox;

/**
 * A {@code SoundPlayback} implementation for the playback of sound effects.
 */
public class SoundPlayback extends Playback
{
    private final Sound sound;

    private final boolean loop;

    public SoundPlayback(Sound sound, boolean loop) throws LineUnavailableException
    {
        super(sound.getFormat());
        this.loop = loop;
        this.sound = sound;
    }

    @Override
    public void run()
    {
        try
        {
            do
            {
                if (this.play(this.sound))
                {
                    return;
                }
            }
            while (this.loop);
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }
        finally
        {
            this.finish();
        }
    }

    @Override
    protected void play()
    {
        super.play();
        Jukebox.addSound(this);
    }
}
