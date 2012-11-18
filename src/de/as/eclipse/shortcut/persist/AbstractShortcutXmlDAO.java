package de.as.eclipse.shortcut.persist;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Ergänzt AbstractShortcutDAO um die Funktionalität Einträge (Shortcuts) im XML-Form zu speichern. Benutzt XMLMemento (Eclipse).
 * 
 * @author Alexander Schulz
 * Date: 18.11.2012
 */
public abstract class AbstractShortcutXmlDAO extends AbstractShortcutDAO {

    /**
     * Default-Constructor.
     */
    protected AbstractShortcutXmlDAO() {
        super();
    }

    // XML-Tags

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

    /**
     * Überführt die Einträge aus der Map ins XML.
     * @param shortcuts Map mit en Einträgen (id, Item).
     * @param root Root-Tag
     * @return String mit XML-Daten
     */
    protected static String convertShortcutToString(Map<Integer, Shortcut> shortcuts, String root) {
        if (shortcuts != null) {
            XMLMemento rootMemento = XMLMemento.createWriteRoot(root);
            for (Integer id : shortcuts.keySet()) {
                Shortcut shortcut = shortcuts.get(id);
                // Einträge ohne ID ignorieren (darf eigentlich nicht passieren, wäre ein interner Fehler).
                if (shortcut.getId() == null) {
                    continue;
                }
                IMemento memento = rootMemento.createChild(AbstractShortcutXmlDAO.SHORTCUT_TAG);
                memento.putInteger(AbstractShortcutXmlDAO.ID_TAG, shortcut.getId());
                memento.putString(AbstractShortcutXmlDAO.NAME_TAG, shortcut.getName());
                memento.putString(AbstractShortcutXmlDAO.GROUP_TAG, shortcut.getGroup());
                memento.putString(AbstractShortcutXmlDAO.LOCATION_TAG, shortcut.getLocation());
                memento.putString(AbstractShortcutXmlDAO.PRIORITY_TAG, shortcut.getPriority());
                memento.putString(AbstractShortcutXmlDAO.CATEGORY_TAG, shortcut.getCategory1());
                memento.putString(AbstractShortcutXmlDAO.CATEGORY2_TAG, shortcut.getCategory2());
                memento.putString(AbstractShortcutXmlDAO.WORKINGDIR_TAG, shortcut.getWorkingDir());
                memento.putString(AbstractShortcutXmlDAO.MCMDS_TAG, shortcut.getMoreCommands());
                memento.putString(AbstractShortcutXmlDAO.RGB_TAG, shortcut.getRgb());
                memento.putBoolean(AbstractShortcutXmlDAO.GRABOUTPUT_TAG, shortcut.isGrabOutput());
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

    /**
     * Parst XML-String uns erstellt daraus ein Map mit Shortcut-Einträgen (id, Item).
     * @param stringData String mit XML-Daten
     * @param factory ShortcutFactory (notwendig, um neue Instanzen der Klasse Shortcut zu erzeugen)
     * @return Map mit Shortcuts (id, Item)
     */
    protected static Map<Integer, Shortcut> convertShortcutFromString(String stringData, ShortcutFactory factory) {
        Map<Integer, Shortcut> m = new HashMap<Integer, Shortcut>();

        if (stringData.length() == 0) {
            return m;
        }

        try {
            XMLMemento rootMemento = XMLMemento.createReadRoot(new StringReader(stringData));
            IMemento[] mementos = rootMemento.getChildren(AbstractShortcutXmlDAO.SHORTCUT_TAG);
            for (int i = 0; i < mementos.length; i++) {
                IMemento memento = mementos[i];

                Shortcut shortcut = factory.createNewShortcut();
                shortcut.setId(memento.getInteger(AbstractShortcutXmlDAO.ID_TAG));
                shortcut.setName(memento.getString(AbstractShortcutXmlDAO.NAME_TAG));
                shortcut.setGroup(memento.getString(AbstractShortcutXmlDAO.GROUP_TAG));
                shortcut.setLocation(memento.getString(AbstractShortcutXmlDAO.LOCATION_TAG));
                shortcut.setPriority(memento.getString(AbstractShortcutXmlDAO.PRIORITY_TAG));
                shortcut.setCategory1(memento.getString(AbstractShortcutXmlDAO.CATEGORY_TAG));
                shortcut.setCategory2(memento.getString(AbstractShortcutXmlDAO.CATEGORY2_TAG));
                shortcut.setWorkingDir(memento.getString(AbstractShortcutXmlDAO.WORKINGDIR_TAG));
                shortcut.setMoreCommands(memento.getString(AbstractShortcutXmlDAO.MCMDS_TAG));
                shortcut.setRgb(memento.getString(AbstractShortcutXmlDAO.RGB_TAG));
                Boolean grabOutput = memento.getBoolean(AbstractShortcutXmlDAO.GRABOUTPUT_TAG);
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
}
