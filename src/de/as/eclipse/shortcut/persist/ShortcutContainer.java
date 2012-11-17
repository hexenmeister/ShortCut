package de.as.eclipse.shortcut.persist;

import java.util.List;

import de.as.eclipse.shortcut.business.Shortcut;

public class ShortcutContainer {

    private String name;

    private IShortcutDAO dao;

    private ShortcutFactory shortcutFactory;

    private boolean visible = true;

    // Ermöglicht in der Anwendung die Schreibzugriffe zu sperren, auch wenn DAO sie erlaubt.
    // Andersherum kann der im DAO verweigerter Zugriff natürlich nicht freigegeben werden.
    private boolean readOnly = false;

    /**
     * Geschützter Konsruktor.
     * @param dao ShortcutDAO
     * @param name Name/Neschreibung
     */
    ShortcutContainer(IShortcutDAO dao, String name) {
        this.dao = dao;
        this.shortcutFactory = new ShortcutFactory(this);
        dao.init(this.shortcutFactory);
        this.name = name;
    }

    /**
     * Liefert den symbolischen Namen der Shortcut-Container.
     * Ein Container enthält eine Liste der Shortcuts, die an einem bestimmten Ort gespeichert werden.
     * Mehrere Container werden in einem Store zusammengefasst (s. ShortcutStore).
     * @return Container-Name
     */
    public String getName() {
        return this.name;
    }

    public ShortcutFactory getShortcutFactory() {
        return this.shortcutFactory;
    }

    protected IShortcutDAO getDAO() {
        return this.dao;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isReadOnly() {
        // nur wenn hier und im DAO Schreibzugriffe erlaubt
        return this.readOnly && this.getDAO().isReadOnly();
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public List<Shortcut> getShortcuts() {
        return this.getDAO().getShortcuts();
    }

    public boolean addShortcut(Shortcut shortcut) {
        if (!this.isReadOnly()) {
            this.getDAO().addShortcut(shortcut);
            return true;
        }
        return false;
    }

    public boolean removeShortcut(Shortcut shortcut) {
        if (!this.isReadOnly()) {
            this.getDAO().removeShortcut(shortcut);
            return true;
        }
        return false;
    }

    public boolean updateShortcut(Shortcut shortcut) {
        if (!this.isReadOnly()) {
            this.getDAO().updateShortcut(shortcut);
            return true;
        }
        return false;
    }

    public boolean mergeShortcuts(List<Shortcut> newList) {
        if (!this.isReadOnly()) {
            this.getDAO().mergeShortcuts(newList);
            return true;
        }
        return false;
    }

    public boolean removeAllShortcuts() {
        if (!this.isReadOnly()) {
            this.getDAO().removeAllShortcuts();
            return true;
        }
        return false;
    }
}
