package de.as.eclipse.shortcut.persist;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import de.as.eclipse.shortcut.business.Shortcut;

public class ShortcutPreferenceStoreDAO implements IShortcutDAO {

    private static final String ROOT_TAG = "shortcuts-data";

    private static final String SHORTCUTS_TAG = "shortcuts";

    private static final String SHORTCUT_TAG = "shortcut";

    private static final String ID_TAG = "id";

    private static final String NAME_TAG = "name";

    private static final String GROUP_TAG = "group";

    private static final String LOCATION_TAG = "location";

    private static final String PRIORITY_TAG = "priority";

    private static final String CATEGORY_TAG = "category";

    private static final String CATEGORY2_TAG = "category2";

    private static final String WORKINGDIR_TAG = "workingdir";

    private static final String MCMDS_TAG = "more_cmds";

    private static final String GRABOUTPUT_TAG = "graboutput";

    private static final String RGB_TAG = "rgb";

    private IPreferenceStore store;

    private ShortcutFactory factory;

    public ShortcutPreferenceStoreDAO(IPreferenceStore store) {
        this.store = store;
    }

    public void init(ShortcutFactory factory) {
        this.factory = factory;
    }

    /* (non-Javadoc)
     * @see de.as.eclipse.shortcut.persist.IShortcutDAO#getShortcuts()
     */
    @Override
    public List<Shortcut> getShortcuts() {
        return Collections.unmodifiableList(new ArrayList<Shortcut>(this.getShortcutsMap().values()));
    }

    /* (non-Javadoc)
     * @see de.as.eclipse.shortcut.persist.IShortcutDAO#addShortcut(de.as.eclipse.shortcut.business.Shortcut)
     */
    @Override
    public void addShortcut(Shortcut shortcut) {
        if (shortcut.getId() != null) {
            throw new RuntimeException("DAO error: attempt to add a new shortcut with an existing id. please use update function.");
        }
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        shortcut.setId(this.getNewId(m));
        m.put(shortcut.getId(), shortcut);
        this.saveShortcuts(m);
    }

    /* (non-Javadoc)
     * @see de.as.eclipse.shortcut.persist.IShortcutDAO#removeShortcut(de.as.eclipse.shortcut.business.Shortcut)
     */
    @Override
    public void removeShortcut(Shortcut shortcut) {
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        m.remove(shortcut.getId());
        this.saveShortcuts(m);
    }

    /* (non-Javadoc)
     * @see de.as.eclipse.shortcut.persist.IShortcutDAO#updateShortcut(de.as.eclipse.shortcut.business.Shortcut)
     */
    @Override
    public void updateShortcut(Shortcut shortcut) {
        if (shortcut.getId() == null) {
            throw new RuntimeException("DAO error: attempt to update a shortcut without an existing id. please use add function.");
        }

        Map<Integer, Shortcut> m = this.getShortcutsMap();
        m.put(shortcut.getId(), shortcut);
        this.saveShortcuts(m);
    }

    /* (non-Javadoc)
     * @see de.as.eclipse.shortcut.persist.IShortcutDAO#mergeShortcuts(java.util.List)
     */
    @Override
    public void mergeShortcuts(List<Shortcut> newList) {
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        if (newList != null) {
            for (Shortcut newShortcut : newList) {
                // Prüfen, ob ein entsprechender Eintrag bereits vorliegt
                if (!m.containsValue(newShortcut)) {
                    // ID erstellen / erneuern
                    newShortcut.setId(this.getNewId(m));
                    m.put(newShortcut.getId(), newShortcut);
                }
            }
            this.saveShortcuts(m);
        }
    }

    /* (non-Javadoc)
     * @see de.as.eclipse.shortcut.persist.IShortcutDAO#removeAllShortcut()
     */
    @Override
    public void removeAllShortcuts() {
        this.saveShortcuts(null);
    }

    private Integer getNewId(Map<Integer, Shortcut> m) {
        int newId = m.size();

        // Wenn die Anzahl der Elemente als ID bereits existiert, dann haben wir eine ID-Lücke, die gefüllt werden kann.
        while (m.get(newId) != null) {
            newId--;
        }

        return newId;
    }

    private Map<Integer, Shortcut> getShortcutsMap() {
        String stringData = this.getStore().getString(ShortcutPreferenceStoreDAO.ROOT_TAG);
        //        System.out.println(stringData);
        Map<Integer, Shortcut> m = ShortcutPreferenceStoreDAO.convertShortcutFromString(stringData, this.factory);
        return m;
    }

    private static Map<Integer, Shortcut> convertShortcutFromString(String stringData, ShortcutFactory factory) {
        Map<Integer, Shortcut> m = new HashMap<Integer, Shortcut>();

        if (stringData.length() == 0) {
            return m;
        }

        try {
            XMLMemento rootMemento = XMLMemento.createReadRoot(new StringReader(stringData));
            IMemento[] mementos = rootMemento.getChildren(ShortcutPreferenceStoreDAO.SHORTCUT_TAG);
            for (int i = 0; i < mementos.length; i++) {
                IMemento memento = mementos[i];

                Shortcut shortcut = factory.createNewShortcut();
                shortcut.setId(memento.getInteger(ShortcutPreferenceStoreDAO.ID_TAG));
                shortcut.setName(memento.getString(ShortcutPreferenceStoreDAO.NAME_TAG));
                shortcut.setGroup(memento.getString(ShortcutPreferenceStoreDAO.GROUP_TAG));
                shortcut.setLocation(memento.getString(ShortcutPreferenceStoreDAO.LOCATION_TAG));
                shortcut.setPriority(memento.getString(ShortcutPreferenceStoreDAO.PRIORITY_TAG));
                shortcut.setCategory1(memento.getString(ShortcutPreferenceStoreDAO.CATEGORY_TAG));
                shortcut.setCategory2(memento.getString(ShortcutPreferenceStoreDAO.CATEGORY2_TAG));
                shortcut.setWorkingDir(memento.getString(ShortcutPreferenceStoreDAO.WORKINGDIR_TAG));
                shortcut.setMoreCommands(memento.getString(ShortcutPreferenceStoreDAO.MCMDS_TAG));
                shortcut.setRgb(memento.getString(ShortcutPreferenceStoreDAO.RGB_TAG));
                Boolean grabOutput = memento.getBoolean(ShortcutPreferenceStoreDAO.GRABOUTPUT_TAG);
                shortcut.setGrabOutput(grabOutput != null ? grabOutput : false);

                // Wegen  Migration der Daten der Vorversion: ggf. eine neue ID hier erstellen
                if (shortcut.getId() == null) {
                    // Wenn Einträge ohne ID gefunden werden, dann dürften alle ohne ID sein (Daten der Vorversion).
                    // Einzelne Einträge ohne ID sind in der Speicherungsfunktion verhindert.
                    // Wahrscheinlichkeit einer Situation mit korrupten Daten ist gering wird in Kauf genommen.
                    // daher numerieren wird sie alle einfach durch.
                    shortcut.setId(i);
                }

                // Wenn ein ID-Map benötigt wird (ohne ID - kein EIntrag)
                if (m != null) {
                    m.put(shortcut.getId(), shortcut);
                }
            }
        } catch (WorkbenchException e) {
            e.printStackTrace();
        }
        return m;
    }

    private void saveShortcuts(Map<Integer, Shortcut> shortcuts) {
        this.getStore().setValue(ShortcutPreferenceStoreDAO.ROOT_TAG, ShortcutPreferenceStoreDAO.convertShortcutToString(shortcuts, ShortcutPreferenceStoreDAO.SHORTCUTS_TAG));
    }

    private static String convertShortcutToString(Map<Integer, Shortcut> shortcuts, String root) {
        if (shortcuts != null) {
            XMLMemento rootMemento = XMLMemento.createWriteRoot(root);
            for (Integer id : shortcuts.keySet()) {
                Shortcut shortcut = shortcuts.get(id);
                // Einträge ohne ID ignorieren (darf eigentlich nicht passieren, wäre ein interner Fehler).
                if (shortcut.getId() == null) {
                    continue;
                }
                IMemento memento = rootMemento.createChild(ShortcutPreferenceStoreDAO.SHORTCUT_TAG);
                memento.putInteger(ShortcutPreferenceStoreDAO.ID_TAG, shortcut.getId());
                memento.putString(ShortcutPreferenceStoreDAO.NAME_TAG, shortcut.getName());
                memento.putString(ShortcutPreferenceStoreDAO.GROUP_TAG, shortcut.getGroup());
                memento.putString(ShortcutPreferenceStoreDAO.LOCATION_TAG, shortcut.getLocation());
                memento.putString(ShortcutPreferenceStoreDAO.PRIORITY_TAG, shortcut.getPriority());
                memento.putString(ShortcutPreferenceStoreDAO.CATEGORY_TAG, shortcut.getCategory1());
                memento.putString(ShortcutPreferenceStoreDAO.CATEGORY2_TAG, shortcut.getCategory2());
                memento.putString(ShortcutPreferenceStoreDAO.WORKINGDIR_TAG, shortcut.getWorkingDir());
                memento.putString(ShortcutPreferenceStoreDAO.MCMDS_TAG, shortcut.getMoreCommands());
                memento.putString(ShortcutPreferenceStoreDAO.RGB_TAG, shortcut.getRgb());
                memento.putBoolean(ShortcutPreferenceStoreDAO.GRABOUTPUT_TAG, shortcut.isGrabOutput());
            }

            StringWriter writer = new StringWriter();
            try {
                rootMemento.save(writer);
                return writer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private IPreferenceStore getStore() {
        return this.store;
    }

    @Override
    public boolean isReadOnly() {
        // Interne Speicherung immer zulässig
        return false;
    }

}
