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
     * @return Liste der Einträge
     */
    public abstract List<Shortcut> getShortcuts();

    /**
     * Fügt ein neuen Eintrag hinzu.
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void addShortcut(Shortcut shortcut);

    /**
     * Entfernt (löscht) den übergeben Shortcut.
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void removeShortcut(Shortcut shortcut);

    /**
     * Aktualisiert die Daten für ein gegebenen Eintrag.
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void updateShortcut(Shortcut shortcut);

    /**
     * Fügt alle Einträge aus der gegebenen Liste zu den gespeicheren Einträgen hinzu.
     * @param newList Liste der Einträge
     */
    public abstract void mergeShortcuts(List<Shortcut> newList);

    /**
     * Entfernt alle gespeicherten Einträge.
     */
    public abstract void removeAllShortcuts();

    /**
     * Gibt an, ob der Container beschreibbar ist (d.h. Elemente können hinzugefügt, geändert oder gelöscht werden).
     * @return true, wenn der Container nicht änderbar ist, false sonst
     */
    public abstract boolean isReadOnly();

    /**
     * Initialisiert das DAO mit einer passenden Fabrik.
     * Wird zu internen Zwecken gebraucht.
     * @param factory ShortcutFactory
     */
    public abstract void init(ShortcutFactory factory);


}