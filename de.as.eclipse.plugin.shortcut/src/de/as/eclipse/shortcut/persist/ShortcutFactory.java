package de.as.eclipse.shortcut.persist;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Fabrikklasse zum erstellen von Shortcut-Instanzen.
 *
 * @author Alexander Schulz
 * Date: 19.11.2012
 */
public class ShortcutFactory {
    private ShortcutContainer container;

    /**
     * Constructor.
     * @param container ShortcutContainer
     */
    ShortcutFactory(ShortcutContainer container) {
        this.container = container;
    }

    /**
     * Erstellt eine neue Instanz der Klasse Shortcut.
     * @return ein neues Shortcut-Objekt
     */
    public Shortcut createNewShortcut() {
        return new ShortcutDecorator(this.container);
    }

    /**
     * Erweitert die Klasse Shortcut um die Eigenschaft parentContainer (ShortcutContainer).
     * Wird zu internen Zwecken benötigt und soll nicht anderweitig genutzt werden.
     *
     * @author Alexander Schulz
     * Date: 18.11.2012
     */
    static class ShortcutDecorator extends Shortcut {
        private ShortcutContainer parentContainer;

        /**
         * Erstellt eine neue Instanz und setzt die Eigenschaft parentContainer
         * @param parentContainer ParentContainer
         */
        private ShortcutDecorator(ShortcutContainer parentContainer) {
            this.parentContainer = parentContainer;
        }

        /**
         * Liefert den ParentContainer eines Shortcut-Objektes.
         * @return ParentContainer
         */
        public ShortcutContainer getParentContainer() {
            return this.parentContainer;
        }
    }
}
