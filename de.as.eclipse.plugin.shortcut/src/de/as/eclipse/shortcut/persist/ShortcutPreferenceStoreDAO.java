package de.as.eclipse.shortcut.persist;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Diese ShortcutDAO-Implementierung benutzt  eine IPreferenceStore als Daten-Container (im XML-Format).
 *
 * @author Alexander Schulz
 * Date: 19.11.2012
 */
public class ShortcutPreferenceStoreDAO extends AbstractShortcutXmlDAO {

    // Root-Tag
    private static final String ROOT_TAG = "shortcuts-data";

    // Eintrag-Tag
    private static final String SHORTCUTS_TAG = "shortcuts";

    private IPreferenceStore store;

    /**
     * Constructor.
     * @param store IPreferenceStore als Daten-Container
     */
    public ShortcutPreferenceStoreDAO(IPreferenceStore store) {
        super();
        this.store = store;
    }

    private IPreferenceStore getStore() {
        return this.store;
    }

    @Override
    protected Map<Integer, Shortcut> getShortcutsMap() throws DAOException {
        String stringData = this.getStore().getString(ShortcutPreferenceStoreDAO.ROOT_TAG);
        Map<Integer, Shortcut> m = AbstractShortcutXmlDAO.readShortcutsFromString(stringData, this.getFactory());
        return m;
    }

    /**
     * {@inheritDoc}
     * Auchtung! Die Daten werden an eine PreferenceStore übergeben,
     * wann diese wirklich dauerhaft gespeichert werden, entscheidet die Store!
     * @see de.as.eclipse.shortcut.persist.AbstractShortcutDAO#saveShortcuts(java.util.Map)
     */
    @Override
    protected void saveShortcuts(Map<Integer, Shortcut> shortcuts) throws DAOException {
        this.getStore().setValue(ShortcutPreferenceStoreDAO.ROOT_TAG, AbstractShortcutXmlDAO.writeShortcutsToString(shortcuts, ShortcutPreferenceStoreDAO.SHORTCUTS_TAG, this.getContainerName()));
    }

    @Override
    public Map<String, String> readProlog() throws DAOException {
        String stringData = this.getStore().getString(ShortcutPreferenceStoreDAO.ROOT_TAG);
        Map<String, String> m = AbstractShortcutXmlDAO.readPrologFromString(stringData);
        return m;
    }

}
