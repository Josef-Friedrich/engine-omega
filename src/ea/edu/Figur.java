package ea.edu;

import ea.actor.Actor;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.internal.ano.NoExternalUse;
import ea.internal.gra.Frame;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * EDU-Variante der {@link StatefulAnimation}.
 * @author Michael Andonie
 */
public class Figur
extends StatefulAnimation
implements EduActor {

    private final float scale;

    /**
     * Einführungskonstruktor. Erstellt eine Figur mit einem ersten Zustand.
     * @param zustandsName  Der Name für den ersten Zustand.
     * @param gifBildPfad   Pfad zu einem <b>GIF Bild</b>.
     */
    public Figur(float scale, String zustandsName, String gifBildPfad) {
        this.scale = scale;
        zustandHinzufuegenVonGIF(zustandsName, gifBildPfad);
        eduSetup();
    }

    public Figur(String zustandsName, String gifBildPfad) {
        this(1f, zustandsName, gifBildPfad);
    }

    /**
     * Einführungskonstruktor. Erstellt eine Figur mit einem ersten Zustand.
     * @param zustandsName  Der Name für den ersten Zustand.
     * @param spriteSheetPfad   Pfad zu einem <b>Spritesheet</b>.
     * @param anzahlX       Anzahl der Spritesheet-Kacheln in die X-Richtung.
     * @param anzahlY       Anzahl der Spritesheet-Kacheln in die Y-Richtung.
     */
    public Figur(float scale, String zustandsName, String spriteSheetPfad, int anzahlX, int anzahlY) {
        this.scale = scale;
        zustandHinzufuegenVonSpritesheet(zustandsName, spriteSheetPfad, anzahlX, anzahlY);
        eduSetup();
    }

    public Figur(String zustandsName, String spriteSheetPfad, int anzahlX, int anzahlY) {
        this(1f, zustandsName, spriteSheetPfad, anzahlX, anzahlY);
    }

    /**
     * Erstellt eine Figur mit einem ersten Zustand. Lädt dazu alle Bilder in einem Verzeichnis ein, die zu einem
     * bestimmten Präfix passen.
     * @param zustandName       Name für den ersten Zustand.
     * @param verzeichnisPfad   Pfad zum Verzeichnis, in dem alle einzuladenden Bilder liegen.
     * @param praefix           Das Präfix, das alle einzuladenden Bilder haben müssen.
     */
    public Figur(float scale, String zustandName, String verzeichnisPfad, String praefix) {
        this.scale = scale;
        zustandHinzufuegenNachPraefix(zustandName, verzeichnisPfad, praefix);
        eduSetup();
    }

    public Figur(String zustandName, String verzeichnisPfad, String praefix) {
        this(1f, zustandName, verzeichnisPfad, praefix);
    }

    /**
     * Fügt einen Zustand mit GIF-Visualisierung ein.
     * @param zustandsName  Name des Zustands.
     * @param bildpfad      Pfad zum GIF, das zu diesem Zustand animiert wird.
     */
    public void zustandHinzufuegenVonGIF(String zustandsName, String bildpfad) {
        if(!bildpfad.toLowerCase().endsWith(".gif")) {
            throw new RuntimeException("Der agegebene Bildpfad muss eine GIF-Datei sein und auf \".gif\" enden. "
                    + "Der angegebene Bildpfad war " + bildpfad);
        }
        Animation animation = Animation.createFromAnimatedGif(bildpfad);
        super.addState(zustandsName, animation);
    }

    /**
     * Fügt Zustand mit Spritesheet-Animation ein. Das Spritesheet muss <b>aus Kacheln gleicher Größe</b> bestehen.
     * "leere" Kacheln werden als leere Animationsframes mit einbezogen.
     * @param zustandsName  Der Name des Zustands.
     * @param bildpfad      Pfad zum Spritesheet.
     * @param anzahlX       Anzahl der Spritesheet-Kacheln in die X-Richtung.
     * @param anzahlY       Anzahl der Spritesheet-Kacheln in die Y-Richtung.
     */
    public void zustandHinzufuegenVonSpritesheet(String zustandsName, String bildpfad, int anzahlX, int anzahlY) {
        Animation animation = Animation.createFromSpritesheet(250, bildpfad, anzahlX, anzahlY);
        super.addState(zustandsName, animation);
    }

    /**
     * Fügt einen Zustand über Einzelframes als Bilder ein.
     * @param zustandsName  Der Name des Zustands.
     * @param bildpfade     Die Pfade der Animationsframes in korrekter Reihenfolge.
     */
    public void zustandHinzufuegenVonBildern(String zustandsName, String... bildpfade) {
        Animation animation = Animation.createFromImages(250, bildpfade);
        super.addState(zustandsName, animation);
    }

    /**
     * Fügt einen Zustand hinzu. Lädt dazu alle Bilder in einem Verzeichnis ein, die zu einem
     * bestimmten Präfix passen.
     * @param zustandName       Name für den ersten Zustand.
     * @param verzeichnisPfad   Pfad zum Verzeichnis, in dem alle einzuladenden Bilder liegen.
     * @param praefix           Das Präfix, das alle einzuladenden Bilder haben müssen.
     */
    public void zustandHinzufuegenNachPraefix(String zustandName, String verzeichnisPfad, String praefix) {
        Animation animation = Animation.createFromImagesPrefix(250, verzeichnisPfad, praefix);
        super.addState(zustandName, animation);
    }

    /**
     * Setzt den Zustand der Figur neu. In jedem Fall wird dabei der Animationsloop zurückgesetzt.
     * @param zustandsName  Der Name des zu setzenden Zustands. Unter diesem Namen muss ein Zustand in dieser
     *                      Figur existieren.
     */
    public void zustandSetzen(String zustandsName) {
        super.setState(zustandsName);
    }

    /**
     * Setzt einen automatischen Übergang von einem Zustand zu einem anderen.
     * @param zustandVon    Der Von-Zustand.
     * @param zustandNach   Der Zustand, zu dem die Figur automatisch übergehen soll, nachdem der Von-Zustand einmal
     *                      bis zum Ende durchgelaufen ist.
     */
    public void automatischenUebergangSetzen(String zustandVon, String zustandNach) {
        super.setStateTransition(zustandVon, zustandNach);
    }

    /**
     * Gibt den aktuellen Zustand aus.
     * @return  Der Name des aktuellen Zustands.
     */
    public String nenneAktuellenZustand() {
        return super.getCurrentState();
    }

    public void setzeAnimationsGeschwindigkeitVon(String zustandName, int frameDauerInMS) {
        super.setFrameDurationsOf(zustandName, frameDauerInMS);
    }

    @NoExternalUse
    @Override
    public Actor getActor() {
        return this;
    }

    private void addStateWithScaling(String stateName, Animation animation) {
        if(scale != 1f) {
            Frame[] standardFrames = animation.getFrames();
            Frame[] resizedFrames = new Frame[standardFrames.length];
            for(int i = 0; i < standardFrames.length; i++) {
                resizedFrames[i] = new Frame(
                        getScaledImage(standardFrames[i].getImage(), scale),
                        standardFrames[i].getDuration());
            }
            super.addStateRaw(stateName, resizedFrames);
        } else {
            super.addState(stateName, animation);
        }
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param scale     scaling factor
     * @return - the new resized image
     */
    private static BufferedImage getScaledImage(BufferedImage srcImg, float scale){
        int w = (int) (srcImg.getWidth()*scale), h = (int) (srcImg.getHeight()*scale);
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
}