package de.as.eclipse.shortcut.persist;

import java.util.List;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Definiert eine Schnittstelle zum Persistieren von Shortcuts.
 *
 * @author Alexander Schulz
 * Date: 18.11.2012
 */
public interface IShortcutDAO {

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


}