package de.as.eclipse.shortcut.persist;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
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
    private static final String CREATOR_TAG = "creator";

    private static final String OS_TAG = "os";

    private static final String USER_TAG = "user";

    private static final String DATE_TAG = "date";

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

    private static final String VERSION_TAG = "version";

    private static final String CURRENT_XML_DATA_VERSION = "1.0";

    /**
     * Überführt die Einträge aus der Map ins XML.
     * @param shortcuts Map mit en Einträgen (id, Item).
     * @param root Root-Tag
     * @return String mit XML-Daten
     * @throws DAOException Persistenz-Probleme
     */
    protected static String writeShortcutsToString(Map<Integer, Shortcut> shortcuts, String root) throws DAOException {
        if (shortcuts != null) {
            StringWriter writer = new StringWriter();
            AbstractShortcutXmlDAO.writeShortcuts(shortcuts, root, writer);
            return writer.toString();
        }
        return "";
    }

    /**
     * Überführt die Einträge aus der Map ins XML und schreibt diese in ein gegebenen Writer.
     * @param shortcuts Map mit en Einträgen (id, Item).
     * @param root Root-Tag
     * @param writer Ziel für die Datenspeicherung
     * @throws DAOException Persistenz-Probleme
     */
    protected static void writeShortcuts(Map<Integer, Shortcut> shortcuts, String root, Writer writer) throws DAOException {
        if (shortcuts != null) {
            XMLMemento rootMemento = XMLMemento.createWriteRoot(root);

            // Prolog
            rootMemento.putString(AbstractShortcutXmlDAO.CREATOR_TAG, "ShortCut");
            rootMemento.putString(AbstractShortcutXmlDAO.USER_TAG, System.getProperty("user.name"));
            rootMemento.putString(AbstractShortcutXmlDAO.OS_TAG, System.getProperty("os.name") + ", " + System.getProperty("os.version") + ", " + System.getProperty("os.arch"));
            rootMemento.putString(AbstractShortcutXmlDAO.DATE_TAG, DateFormat.getDateTimeInstance().format(new Date()));

            // Version
            rootMemento.putString(AbstractShortcutXmlDAO.VERSION_TAG, AbstractShortcutXmlDAO.CURRENT_XML_DATA_VERSION);

            // Data
            for (Integer id : shortcuts.keySet()) {
                Shortcut shortcut = shortcuts.get(id);
                // Einträge ohne ID ignorieren (darf eigentlich nicht passieren, wäre ein interner Fehler).
                if (shortcut.getId() == null) {
                    continue;
                }
                IMemento itemMemento = rootMemento.createChild(AbstractShortcutXmlDAO.SHORTCUT_TAG);
                itemMemento.putInteger(AbstractShortcutXmlDAO.ID_TAG, shortcut.getId());
                itemMemento.putString(AbstractShortcutXmlDAO.NAME_TAG, shortcut.getName());
                itemMemento.putString(AbstractShortcutXmlDAO.GROUP_TAG, shortcut.getGroup());
                itemMemento.putString(AbstractShortcutXmlDAO.LOCATION_TAG, shortcut.getLocation());
                itemMemento.putString(AbstractShortcutXmlDAO.PRIORITY_TAG, shortcut.getPriority());
                itemMemento.putString(AbstractShortcutXmlDAO.CATEGORY_TAG, shortcut.getCategory1());
                itemMemento.putString(AbstractShortcutXmlDAO.CATEGORY2_TAG, shortcut.getCategory2());
                itemMemento.putString(AbstractShortcutXmlDAO.WORKINGDIR_TAG, shortcut.getWorkingDir());
                itemMemento.putString(AbstractShortcutXmlDAO.MCMDS_TAG, shortcut.getMoreCommands());
                itemMemento.putString(AbstractShortcutXmlDAO.RGB_TAG, shortcut.getRgb());
                itemMemento.putBoolean(AbstractShortcutXmlDAO.GRABOUTPUT_TAG, shortcut.isGrabOutput());
            }

            try {
                rootMemento.save(writer);
            } catch (IOException e) {
                throw new DAOException("could not save shortcut list", e);
            }
        }
    }

    /**
     * Parst XML-String und erstellt daraus ein Map mit Prolog-Einträgen (key, value).
     * 
     * XXX: für spätere Verwendung, noch keine Ahnung wo.
     * @param stringData String mit XML-Daten
     * @return Map mit Prolog-Parametern (key, value)
     * @throws DAOException Persistenz-Probleme
     */
    protected static Map<String, String> readPrologFromString(String stringData) throws DAOException {
        if (stringData.length() == 0) {
            return new HashMap<String, String>();
        }

        StringReader reader = new StringReader(stringData);

        return AbstractShortcutXmlDAO.readProlog(reader);
    }

    /**
     * Liest die Daten aus dem gegebenen Reader, parst XML-String und erstellt daraus ein Map mit Prolog-Einträgen (key, value).
     * @param reader Source
     * @return Map mit Prolog-Parametern (key, value)
     * @throws DAOException Persistenz-Probleme
     */
    protected static Map<String, String> readProlog(Reader reader) throws DAOException {
        Map<String, String> ret = new HashMap<String, String>();

        try {
            XMLMemento rootMemento = XMLMemento.createReadRoot(reader);
            String[] keys = rootMemento.getAttributeKeys();
            for (int i = 0, n = keys.length; i < n; i++) {
                ret.put(keys[i], rootMemento.getString(keys[i]));
            }
        } catch (WorkbenchException e) {
            throw new DAOException("could not read shortcut list", e);
        }

        return ret;
    }

    /**
     * Parst XML-String und erstellt daraus ein Map mit Shortcut-Einträgen (id, Item).
     * @param stringData String mit XML-Daten
     * @param factory ShortcutFactory (notwendig, um neue Instanzen der Klasse Shortcut zu erzeugen)
     * @return Map mit Shortcuts (id, Item)
     * @throws DAOException Persistenz-Probleme
     */
    protected static Map<Integer, Shortcut> readShortcutsFromString(String stringData, ShortcutFactory factory) throws DAOException {
        if (stringData.length() == 0) {
            return new HashMap<Integer, Shortcut>();
        }

        StringReader reader = new StringReader(stringData);

        return AbstractShortcutXmlDAO.readShortcuts(reader, factory);
    }

    /**
     * Liest die Daten aus dem gegebenen Reader, parst XML-String und erstellt daraus ein Map mit Shortcut-Einträgen (id, Item).
     * @param reader Source
     * @param factory ShortcutFactory (notwendig, um neue Instanzen der Klasse Shortcut zu erzeugen)
     * @return Map mit Shortcuts (id, Item)
     * @throws DAOException Persistenz-Probleme
     */
    protected static Map<Integer, Shortcut> readShortcuts(Reader reader, ShortcutFactory factory) throws DAOException {
        Map<Integer, Shortcut> m = new HashMap<Integer, Shortcut>();
        try {
            XMLMemento rootMemento = XMLMemento.createReadRoot(reader);

            String version = rootMemento.getString(AbstractShortcutXmlDAO.VERSION_TAG);
            if ((version != null) && !"1.0".equals(version)) {
                throw new DAOException("unknown data version: "+version,null);
            }

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
            throw new DAOException("could not read shortcut list", e);
        }
        return m;
    }
}
