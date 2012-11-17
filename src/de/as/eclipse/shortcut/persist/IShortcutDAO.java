package de.as.eclipse.shortcut.persist;

import java.util.List;

import de.as.eclipse.shortcut.business.Shortcut;

public interface IShortcutDAO {

    public abstract List<Shortcut> getShortcuts();

    public abstract void addShortcut(Shortcut shortcut);

    public abstract void removeShortcut(Shortcut shortcut);

    public abstract void updateShortcut(Shortcut shortcut);

    public abstract void mergeShortcuts(List<Shortcut> newList);

    public abstract void removeAllShortcuts();

    public abstract void init(ShortcutFactory factory);

    public abstract boolean isReadOnly();

}