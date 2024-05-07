package rocks.friedrich.engine_omega.internal.graphics;

//import com.sun.corba.se.impl.orbutil.graph.Graph;

import rocks.friedrich.engine_omega.internal.annotations.Internal;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Beschreibt einen Frame einer {@link rocks.friedrich.engine_omega.actor.Animation}.
 * @author Niklas Keller
 */
@Internal
public final class AnimationFrame {
    /**
     * Das Bild, das zu diesem Frame gehört.
     */
    private final BufferedImage image;
    /**
     * Die Dauer in Sekunden, die dieser Frame aktiv bleibt.
     */
    private double duration;

    /**
     * Erstellt einen Frame.
     * @param image     Das Bild für den Frame.
     * @param duration  Die Dauer, die dieser Frame aktiv bleibt.
     */
    @Internal
    public AnimationFrame(BufferedImage image, double duration) {
        this.image = image;
        this.duration = duration;
    }

    @Internal
    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Internal
    public BufferedImage getImage() {
        return image;
    }

    @Internal
    public double getDuration() {
        return duration;
    }

    /**
     * Rendert den Frame (an der entsprechenden Position des Graphics Objekts)
     * @param g   Das Graphics Objekt
     */
    @Internal
    public void render(Graphics2D g, double width, double height, boolean flipHorizontal, boolean flipVertical) {
        AffineTransform pre = g.getTransform();
        g.scale(width / this.image.getWidth(), height / this.image.getHeight());
        g.drawImage(image,
                flipHorizontal ? image.getWidth() : 0,
                -image.getHeight() + (flipVertical ? image.getHeight() : 0),
                (flipHorizontal ? -1 : 1)*image.getWidth(),
                (flipVertical ? -1 : 1)*image.getHeight(),
                null);
        g.setTransform(pre);
    }
}
