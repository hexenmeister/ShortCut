package de.as.eclipse.shortcut.persist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.as.eclipse.shortcut.business.Shortcut;
import de.as.eclipse.shortcut.persist.ShortcutFactory.ShortcutDecorator;

public class ShortcutStore {

    private List<ShortcutContainer> containerList;

    public ShortcutStore() {
        this.containerList = new ArrayList<ShortcutContainer>();
    }

    public void init() {
        // ggf. initialisieren
    }

    public void close() {
        // ggf. speichern oder so...
    }

    public List<ShortcutContainer> getContainers() {
        return Collections.unmodifiableList(this.containerList);
    }

    public ShortcutContainer createNewContainer(IShortcutDAO dao, String name) {
        return new ShortcutContainer(dao, name);
    }

    public void addContainer(ShortcutContainer container) {
        this.containerList.add(container);
    }

    public boolean removeContainer(ShortcutContainer container) {
        return this.containerList.remove(container);
    }

    public List<ShortcutContainer> getVisibleContainers() {
        // liste filtern: nur sichtbare
        List<ShortcutContainer> ret = new ArrayList<ShortcutContainer>();
        for (ShortcutContainer shortcutContainer : this.containerList) {
            if (shortcutContainer.isVisible()) {
                ret.add(shortcutContainer);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    public List<ShortcutContainer> getChangeableContainers() {
        // liste filtern: nur beschreibbare
        List<ShortcutContainer> ret = new ArrayList<ShortcutContainer>();
        for (ShortcutContainer shortcutContainer : this.containerList) {
            if (!shortcutContainer.isReadOnly()) {
                ret.add(shortcutContainer);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    public List<Shortcut> getVisibleShortcuts() {
        List<Shortcut> ret = new ArrayList<Shortcut>();
        List<ShortcutContainer> list = this.getVisibleContainers();
        for (ShortcutContainer container : list) {
            try {
                ret.addAll(container.getShortcuts());
            } catch (Exception e) {
                // ignore: wenn ein Container nicht zugreifbar ist, dann wird er ausgelassen und mit den restlichen fortgefahren
                // TODO: Vormerken, Meldung ausgeben
            }
        }
        return ret;
    }

    public void removeShortcut(Shortcut shortcut) throws DAOException {
        if (shortcut instanceof ShortcutDecorator) {
            ShortcutContainer container = ((ShortcutDecorator) shortcut).getParentContainer();
            container.removeShortcut(shortcut);
        } else if (shortcut != null) {
            throw new RuntimeException("unexpected subclass of Shortcut: " + shortcut.getClass().getName());
        }
    }

    public ShortcutContainer getContainer(Shortcut shortcut) {
        if (shortcut instanceof ShortcutDecorator) {
            ShortcutContainer container = ((ShortcutDecorator) shortcut).getParentContainer();
            return container;
        } else if (shortcut != null) {
            throw new RuntimeException("unexpected subclass of Shortcut: " + shortcut.getClass().getName());
        }

        return null;
    }

    // TODO: JavaDoc, Filter-Methoden (definieren, abfragen, ...)
}
