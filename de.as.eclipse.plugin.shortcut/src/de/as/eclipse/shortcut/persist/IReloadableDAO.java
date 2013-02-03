package de.as.eclipse.shortcut.persist;

/**
 * Dient als Marker für DAO-Klassen, die ihre Parameter (als ein String) ausgeben können
 * und mit diesem eine neue, gleichwertige Instanz erstellt werden kann.
 * Für die Wiederinitialisierung wird der Config-String an einen entsprechender Constuctor
 * übergeben (dieser muss vorhanden sein).
 *
 * @author Alexander Schulz
 * Date: 21.11.2012
 */
public interface IReloadableDAO {

    /**
     * Liefert String mit Konfigurationsparametern, mit denen eine gleichwertige Instanz (wieder-) erstellt werden kann.
     * @return Konfigurationsparameter.
     */
    public abstract String getConfigString();

}
