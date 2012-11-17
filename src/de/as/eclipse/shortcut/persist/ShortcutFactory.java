package de.as.eclipse.shortcut.persist;

import de.as.eclipse.shortcut.business.Shortcut;

public class ShortcutFactory {
    private ShortcutContainer container;

    ShortcutFactory(ShortcutContainer container) {
        this.container = container;
    }

    public Shortcut createNewShortcut() {
        return new ShortcutDecorator(this.container);
    }

    static class ShortcutDecorator extends Shortcut {
        private ShortcutContainer parentContainer;

        private ShortcutDecorator(ShortcutContainer parentContainer) {
            this.parentContainer = parentContainer;
        }

        public ShortcutContainer getParentContainer() {
            return this.parentContainer;
        }
    }
}
