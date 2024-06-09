package de.pirckheimer_gymnasium.engine_pi.resources;

/**
 *
 * @param <T> Die Ressource, z. B. BufferedImage, Sound, Color
 */
public interface Container<T>
{
    /**
     * Fügt die angegebene Ressource zu diesem Speicher hinzu.<br>
     * Das hinzugefügte Element kann später aus dem Speicher abgerufen werden,
     * indem {@code get(resourceName)} aufgerufen wird.
     * <p>
     * Verwenden Sie diese Methode, um eine Ressource während der Laufzeit über
     * diesen Speicher zugänglich zu machen.
     * </p>
     *
     * @param name     Der Name, unter dem die Ressource verwaltet wird.
     * @param resource Die Ressourceninstanz.
     */
    public T add(String name, T resource);

    /**
     * Leert den Ressourcenspeicher, indem alle zuvor geladenen Ressourcen
     * entfernt werden.
     *
     * @see ResourcesContainer#clear()
     */
    public void clear();

    /**
     * Ruft die Ressource mit dem angegebenen Namen ab.<br>
     * <p>
     * Dies ist die gängigste (und bevorzugte) Methode, um Ressourcen aus einem
     * Speicher abzurufen.
     * </p>
     *
     * <p>
     * Wenn die Ressource nicht zuvor geladen wurde, versucht diese Methode, sie
     * sofort zu laden, andernfalls wird sie aus dem Cache abgerufen.
     * </p>
     *
     * @param name Der Name, unter dem die Ressource verwaltet wird.
     *
     * @return Die Ressource mit dem angegebenen Namen oder null, wenn sie nicht
     *         gefunden wird.
     */
    public T get(String name);
}
