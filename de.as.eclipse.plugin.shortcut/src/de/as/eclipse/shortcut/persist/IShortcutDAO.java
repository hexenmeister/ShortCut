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
     * Liefert Liste aller gespeicherten Eintr�ge.
     * @return Liste der Eintr�ge
     */
    public abstract List<Shortcut> getShortcuts();

    /**
     * F�gt ein neuen Eintrag hinzu.
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void addShortcut(Shortcut shortcut);

    /**
     * Entfernt (l�scht) den �bergeben Shortcut.
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void removeShortcut(Shortcut shortcut);

    /**
     * Aktualisiert die Daten f�r ein gegebenen Eintrag.
     * @param shortcut Shortcut-Eintrag
     */
    public abstract void updateShortcut(Shortcut shortcut);

    /**
     * F�gt alle Eintr�ge aus der gegebenen Liste zu den gespeicheren Eintr�gen hinzu.
     * @param newList Liste der Eintr�ge
     */
    public abstract void mergeShortcuts(List<Shortcut> newList);

    /**
     * Entfernt alle gespeicherten Eintr�ge.
     */
    public abstract void removeAllShortcuts();

    /**
     * Gibt an, ob der Container beschreibbar ist (d.h. Elemente k�nnen hinzugef�gt, ge�ndert oder gel�scht werden).
     * @return true, wenn der Container nicht �nderbar ist, false sonst
     */
    public abstract boolean isReadOnly();

    /**
     * Initialisiert das DAO mit einer passenden Fabrik.
     * Wird zu internen Zwecken gebraucht.
     * @param factory ShortcutFactory
     */
    public abstract void init(ShortcutFactory factory);


}