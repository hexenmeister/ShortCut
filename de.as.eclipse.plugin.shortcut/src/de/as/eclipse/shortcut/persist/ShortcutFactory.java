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
     * Prüft, ob der gegebene Shortcut mit dem dieser Factory zugeordnetem Container verknüpft.
     * @param shortcut zu untersuchende Shortcut-Objekt
     * @return true, falls Shortcut bereits dem richtigen Contianer zugeordnet ist, false sonst.
     */
    boolean checkContainer(Shortcut shortcut) {
        if (shortcut instanceof ShortcutDecorator) {
            if (((ShortcutDecorator) shortcut).getParentContainer() == this.container) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stellt sicher, dass Shortcut-Instanz zu dieser Factory gehörenden Container enthält.
     * Bei Bedarf wird die Instanz geklonnt und angepasst. Geklonnte Instanzen bekommen null als ID.
     * @param shortcut anzupassende Shortcut-Instanz
     * @return shortcut, falls Container korrekt war, eine neue Instanz mit korrektem Container sonst.
     */
    Shortcut ensureCorrectContainer(Shortcut shortcut) {
        if (this.checkContainer(shortcut)) {
            // passt, nichts zu tun
            return shortcut;
        }
        return this.clone(shortcut);
    }

    /**
     * Die übergeben Instanz wird geklonnt. Geklonnte Instanzen bekommen null als ID.
     * @param shortcut zu kopierende Instanz
     * @return neue Instanz
     */
    Shortcut clone(Shortcut shortcut) {
        if (shortcut instanceof ShortcutDecorator) {
            ShortcutDecorator decorator = (ShortcutDecorator) shortcut;
            decorator = decorator.clone(this.container);
            return decorator;
        } else {
            // TODO: Exception
            throw new RuntimeException("unexpected Shortcut class");
        }
    }

    /**
     * Erweitert die Klasse Shortcut um die Eigenschaft parentContainer (ShortcutContainer).
     * Wird zu internen Zwecken benötigt und soll nicht anderweitig genutzt werden.
     *
     * @author Alexander Schulz
     * Date: 18.11.2012
     */
    static class ShortcutDecorator extends Shortcut implements Cloneable {
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

        private void setParentContainer(ShortcutContainer container) {
            this.parentContainer = container;
        }

        /**
         * Erstellt eine Kopie des Objektes, ersetzt den ParentContainer, löscht ID (diese ist ja DAO-bezogen).
         * @param parentContainer neue ParentContainer
         * @return neue Instanz
         */
        ShortcutDecorator clone(ShortcutContainer parentContainer) {
            try {
                ShortcutDecorator shortcut = (ShortcutDecorator) super.clone();
                shortcut.setParentContainer(parentContainer);
                shortcut.setId(null);
                return shortcut;
            } catch (CloneNotSupportedException e) {
                // ignore, da unmöglich
                return null;
            }
        }
    }
}
