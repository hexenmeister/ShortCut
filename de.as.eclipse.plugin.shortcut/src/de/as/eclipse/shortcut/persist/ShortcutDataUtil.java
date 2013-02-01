package de.as.eclipse.shortcut.persist;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Utilities zur Manipulationen mit den Shortcuts.
 *
 * @author Alexander Schulz
 */
public class ShortcutDataUtil {

    /**
     * Dupliziert einen Shortcut im eigenen Parent-Container.
     * @param shortcutStore Store wird benötigt, um den Parent-Container zu ermitteln.
     * @param shortcut Shortcut-Objekt
     * @return true, wenn erfolgreich
     */
    public static boolean duplicateShortcut(ShortcutStore shortcutStore, Shortcut shortcut) {
        if ((shortcutStore != null)) {
            ShortcutContainer parentContainer = shortcutStore.getParentContainer(shortcut);
            return ShortcutDataUtil.copyOrMoveShortcut(shortcut, parentContainer, parentContainer, false);
        }
        return false;
    }

    /**
     * Kopiert einen Shortcut in ein gegebenen Container.
     * Es wird geprüft, ob das Ziel-Container dem Parent-Container entspricht und sichergestellt, dass diese verschieden sind.
     * Wenn diese gleich sind, liefert die Methode <code>false</code> und tut gar nichts.
     * @param shortcutStore Store wird benötigt, um den Parent-Container zu ermitteln.
     * @param shortcut Shortcut-Objekt
     * @param target Ziel-Container.
     * @return true, wenn erfolgreich
     */
    public static boolean copyShortcut(ShortcutStore shortcutStore, Shortcut shortcut, ShortcutContainer target) {
        if ((shortcutStore != null)) {
            ShortcutContainer parentContainer = shortcutStore.getParentContainer(shortcut);
            if (target != parentContainer) {
                return ShortcutDataUtil.copyOrMoveShortcut(shortcut, parentContainer, target, false);
            }
        }
        return false;
    }

    /**
     * Verschiebt einen Shortcut in ein anderes Container.
     * Es wird geprüft, ob das Ziel-Container dem Parent-Container entspricht und sichergestellt, dass diese verschieden sind.
     * Wenn diese gleich sind, liefert die Methode <code>false</code> und tut gar nichts.
     * @param shortcutStore Store wird benötigt, um den Parent-Container zu ermitteln.
     * @param shortcut Shortcut-Objekt
     * @param target Ziel-Container.
     * @return true, wenn erfolgreich
     */
    public static boolean moveShortcut(ShortcutStore shortcutStore, Shortcut shortcut, ShortcutContainer target) {
        if ((shortcutStore != null)) {
            ShortcutContainer parentContainer = shortcutStore.getParentContainer(shortcut);
            if (target != parentContainer) {
                return ShortcutDataUtil.copyOrMoveShortcut(shortcut, parentContainer, target, true);
            }
        }
        return false;
    }

    /**
     * Kopiert oder verschiebt den Shortcut-Objekt in ein anderes Container.
     * @param shortcut Shrtcut-Objekt
     * @param parentContainer Parent-Container
     * @param target Ziel-Container
     * @param move true fürs Verscheieben, false fürs Kopieren
     * @return true, wenn erfolgreich
     */
    private static boolean copyOrMoveShortcut(Shortcut shortcut, ShortcutContainer parentContainer, ShortcutContainer target, boolean move) {
        if ((parentContainer != null) && (target != null) && (shortcut != null)) {
            try {
                ShortcutDataUtil.addShortcut(shortcut, target);
                if (move) {
                    parentContainer.removeShortcut(shortcut);
                }
                return true;
            } catch (DAOException e) {
                // TODO: Log
            }
        }
        return false;
    }

    /**
     * Klonnt einen Shortcut-Objekt un fügt diesen dem Ziel-Container hinzu.
     * @param shortcut Shortcut-Objekt
     * @param target Ziel-Container
     * @throws DAOException kann bei 'add' (also beim Speichern) auftretten
     */
    private static void addShortcut(Shortcut shortcut, ShortcutContainer target) throws DAOException {
        Shortcut newShortcut = target.createNewShortcut(); // Das neue Shortcut-Objekt muss von dem Ziel-Container erstellt werden!
        // Shortcut muss neu engelegt werden, damit innere Strukturen an das neue Container angepast werden.
        newShortcut.copyFrom(shortcut);
        target.addShortcut(newShortcut);
    }

}
