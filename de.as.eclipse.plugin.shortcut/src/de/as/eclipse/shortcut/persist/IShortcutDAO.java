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
     * Prolog-Tag: Container-Description.
     */
    public static final String CONTAINER_DESCRIPTION_TAG = "description";

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
     * Prolog-Tag: Modus der Zugriffbeschränkung.
     */
    public static final String CONTAINER_ACCES_MODE_TAG = "accessMode";

    /**
     * Zugriffsbeschränkung: Schreibzugriff nur Ersteller.
     */
    public static final String CONTAINER_ACCES_MODE_WRITE_CREATOR_ONLY = "rw_creator";

    /**
     * Zugriffsbeschränkung: Zugriff nur Ersteller.
     */
    public static final String CONTAINER_ACCES_MODE_CREATOR_ONLY = "creator";

    /**
     * Zugriffsbeschränkung: Schreibzugriff für alle.
     */
    public static final String CONTAINER_ACCES_MODE_RW = "rw";

    /**
     * Zugriffsbeschränkung: Schreibzugriff niemand.
     */
    public static final String CONTAINER_ACCES_MODE_RO = "ro";

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
     * @param prolog Map mit Meta-Daten (Name, Description)
     * @param shortcut Shortcut-Eintrag
     * @throws DAOException Persistenz-Probleme
     */
    public abstract void addShortcut(Map<String, String> prolog, Shortcut shortcut) throws DAOException;

    /**
     * Entfernt (löscht) den übergeben Shortcut.
     * @param prolog Map mit Meta-Daten (Name, Description)
     * @param shortcut Shortcut-Eintrag
     * @throws DAOException Persistenz-Probleme
     */
    public abstract void removeShortcut(Map<String, String> prolog, Shortcut shortcut) throws DAOException;

    /**
     * Aktualisiert die Daten für ein gegebenen Eintrag.
     * @param prolog Map mit Meta-Daten (Name, Description)
     * @param shortcut Shortcut-Eintrag
     * @throws DAOException Persistenz-Probleme
     */
    public abstract void updateShortcut(Map<String, String> prolog, Shortcut shortcut) throws DAOException;

    /**
     * Fügt alle Einträge aus der gegebenen Liste zu den gespeicheren Einträgen hinzu.
     * @param prolog Map mit Meta-Daten (Name, Description)
     * @param newList Liste der Einträge
     * @throws DAOException Persistenz-Probleme
     */
    public abstract void mergeShortcuts(Map<String, String> prolog, List<Shortcut> newList) throws DAOException;

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
     * @return Liefert Prolog-Map (falls Container bereits existiert)
     * @throws DAOException Persistenz-Probleme
     */
    public abstract Map<String, String> init(ShortcutFactory factory) throws DAOException;

    /**
     * Liest die Kopf-Parameter des Containers (Name, Version etc.).
     * @throws DAOException Persistenz-Probleme
     * @return Tabelle mit den Kopf-Parametern.
     */
    public Map<String, String> readProlog() throws DAOException;

    /**
     * Schreibt die Datensätze neu.
     * @param prolog Map mit Meta-Daten (Name, Description)
     * @throws DAOException Persistenz-Probleme
     */
    public void saveShortcuts(Map<String, String> prolog) throws DAOException;
}
