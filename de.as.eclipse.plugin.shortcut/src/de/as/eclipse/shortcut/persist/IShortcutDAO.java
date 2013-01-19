package de.as.eclipse.shortcut.persist;

import java.util.List;
import java.util.Map;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Definiert eine Schnittstelle zum Persistieren von Shortcuts.
 *
 * @author Alexander Schulz
 * Date: 18.11.2012
 */
public interface IShortcutDAO {

    // Prolog-Tags.

    /**
     * Prolog-Tag: Ersteller-Programm.
     */
    public static final String CREATOR_TAG = "creator";

    /**
     * Prolog-Tag: Container-Name.
     */
    public static final String CONTAINER_NAME_TAG = "name";

    /**
     * Prolog-Tag: OS (Betriebsystem, unter dem das Container erstellt wurde.)
     */
    public static final String OS_TAG = "os";

    /**
     * Prolog-Tag: Angemeldeter OS-Benutzer (der das Container erstellt hat.)
     */
    public static final String USER_TAG = "user";

    /**
     * Prolog-Tag: Datum der letzten Änderung.
     */
    public static final String DATE_TAG = "date";

    /**
     * Prolog-Tag: Container-Format-Version.
     */
    public static final String VERSION_TAG = "version";

    /**
     * Liefert Liste aller gespeicherten Einträge.
     * @throws DAOException Persistenz-Probleme
     * @return Liste der Einträge
     */
    public abstract List<Shortcut> getShortcuts() throws DAOException;

    /**
     * Fügt ein neuen Eintrag hinzu.
     * @throws DAOException Persistenz-Probleme
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void addShortcut(Shortcut shortcut) throws DAOException;

    /**
     * Entfernt (löscht) den übergeben Shortcut.
     * @throws DAOException Persistenz-Probleme
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void removeShortcut(Shortcut shortcut) throws DAOException;

    /**
     * Aktualisiert die Daten für ein gegebenen Eintrag.
     * @throws DAOException Persistenz-Probleme
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void updateShortcut(Shortcut shortcut) throws DAOException;

    /**
     * Fügt alle Einträge aus der gegebenen Liste zu den gespeicheren Einträgen hinzu.
     * @throws DAOException Persistenz-Probleme
     * @param newList Liste der Einträge
     */
    public abstract void mergeShortcuts(List<Shortcut> newList) throws DAOException;

    /**
     * Entfernt alle gespeicherten Einträge.
     * @throws DAOException Persistenz-Probleme
     */
    public abstract void removeAllShortcuts() throws DAOException;

    /**
     * Gibt an, ob der Container beschreibbar ist (d.h. Elemente kännen hinzugefügt, geändert oder gelöscht werden).
     * @return true, wenn der Container nicht änderbar ist, false sonst
     */
    public abstract boolean isReadOnly();

    /**
     * Initialisiert das DAO mit einer passenden Fabrik.
     * Wird zu internen Zwecken gebraucht.
     * @param factory ShortcutFactory
     * @param containerName Name des Containers (kann ggf. mitgespeichert werden).
     * @throws DAOException Persistenz-Probleme
     */
    public abstract void init(ShortcutFactory factory, String containerName) throws DAOException;

    /**
     * Liest die Kopf-Parameter des Containers (Name, Version etc.).
     * @throws DAOException Persistenz-Probleme
     * @return Tabelle mit den Kopf-Parametern.
     */
    public Map<String, String> readProlog() throws DAOException;
}