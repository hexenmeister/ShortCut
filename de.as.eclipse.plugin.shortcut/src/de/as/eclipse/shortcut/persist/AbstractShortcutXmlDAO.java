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

    protected static final String SHORTCUT_TAG = "shortcut";

    /**
     * Default-Constructor.
     */
    protected AbstractShortcutXmlDAO() {
        super();
    }

    /**
     * Abstrakte Basis für Klassen, die XML-Daten und die BE-Struktur ineinander überführen.
     * Die Idee ist, mehrere solche Codecs gleichzeitig zu unterstützen, damit kann Datei-Format geändert werden,
     * ohne dass eine ältere Ausgabe nicht mehr interpretierbar wird. Zurückgeschrieben wird immer in der aktuellen Version.
     * Dies führt zu so einer Art automatischen Datenmigration.
     *
     * @author Alexander Schulz
     * Date: 01.12.2012
     */
    private static abstract class AbstractShortcutXmlCodec {

        // XML-Tags
        protected static final String ID_TAG = "id";

        protected static final String NAME_TAG = "name";

        protected static final String GROUP_TAG = "group";

        protected static final String PRIORITY_TAG = "priority";

        protected static final String CATEGORY_TAG = "category";

        protected static final String CATEGORY2_TAG = "category2";

        protected static final String WORKINGDIR_TAG = "workingdir";

        protected static final String GRABOUTPUT_TAG = "graboutput";

        protected static final String RGB_TAG = "rgb";

        protected static final String PAYLOAD_TAG = "payload";

        /**
         * Überführt Struktur ins XML
         * @param shortcut Eingabe
         * @param memento Ausgabe
         * @throws DAOException bei Problemen
         */
        public abstract void writeShortcut(Shortcut shortcut, IMemento memento) throws DAOException;

        /**
         * Überführt XML-Daten ins BE.
         * @param shortcut Ausgabe
         * @param memento Eingabe
         * @throws DAOException bei Problemen
         */
        public abstract void readShortcut(Shortcut shortcut, IMemento memento) throws DAOException;
    }

    /**
     * XMLCodec Version 1.0.
     *
     * @author Alexander Schulz
     * Date: 01.12.2012
     */
    private static class ShortcutXmlCodecV1_0 extends AbstractShortcutXmlCodec {

        protected static final String LOCATION_TAG = "location";

        protected static final String MCMDS_TAG = "more_cmds";

        @Override
        public void writeShortcut(Shortcut shortcut, IMemento memento) throws DAOException {
            memento.putInteger(AbstractShortcutXmlCodec.ID_TAG, shortcut.getId());
            memento.putString(AbstractShortcutXmlCodec.NAME_TAG, shortcut.getName());
            memento.putString(AbstractShortcutXmlCodec.GROUP_TAG, shortcut.getGroup());
            memento.putString(ShortcutXmlCodecV1_0.LOCATION_TAG, shortcut.getPayload());
            memento.putString(AbstractShortcutXmlCodec.PRIORITY_TAG, shortcut.getPriority());
            memento.putString(AbstractShortcutXmlCodec.CATEGORY_TAG, shortcut.getCategory1());
            memento.putString(AbstractShortcutXmlCodec.CATEGORY2_TAG, shortcut.getCategory2());
            memento.putString(AbstractShortcutXmlCodec.WORKINGDIR_TAG, shortcut.getWorkingDir());
            // memento.putString(MCMDS_TAG, shortcut.getMoreCommands());
            memento.putString(AbstractShortcutXmlCodec.RGB_TAG, shortcut.getRgb());
            memento.putBoolean(AbstractShortcutXmlCodec.GRABOUTPUT_TAG, shortcut.isGrabOutput());
        }

        @Override
        public void readShortcut(Shortcut shortcut, IMemento memento) throws DAOException {
            shortcut.setId(memento.getInteger(AbstractShortcutXmlCodec.ID_TAG));
            shortcut.setName(memento.getString(AbstractShortcutXmlCodec.NAME_TAG));
            shortcut.setGroup(memento.getString(AbstractShortcutXmlCodec.GROUP_TAG));
            String payload = memento.getString(ShortcutXmlCodecV1_0.LOCATION_TAG);
            String moreCommands = memento.getString(ShortcutXmlCodecV1_0.MCMDS_TAG);
            if (moreCommands != null) {
                payload += "\r\n" + moreCommands;
            }
            shortcut.setPayload(payload);
            shortcut.setPriority(memento.getString(AbstractShortcutXmlCodec.PRIORITY_TAG));
            shortcut.setCategory1(memento.getString(AbstractShortcutXmlCodec.CATEGORY_TAG));
            shortcut.setCategory2(memento.getString(AbstractShortcutXmlCodec.CATEGORY2_TAG));
            shortcut.setWorkingDir(memento.getString(AbstractShortcutXmlCodec.WORKINGDIR_TAG));
            shortcut.setRgb(memento.getString(AbstractShortcutXmlCodec.RGB_TAG));
            Boolean grabOutput = memento.getBoolean(AbstractShortcutXmlCodec.GRABOUTPUT_TAG);
            shortcut.setGrabOutput(grabOutput != null ? grabOutput : false);
        }
    }

    /**
     * XMLCodec Verion 1.1.
     * Änderungen gegenüber v1.0:
     *  - Umbenennung: 'location'->'payload'
     *  - 'more_cmds' wird nicht mehr unterstützt (weitere Befehle können jetzt alle in 'payload' gespeichert werden.
     *
     * @author Alexander Schulz
     * Date: 01.12.2012
     */
    private static class ShortcutXmlCodecV1_1 extends AbstractShortcutXmlCodec {

        @Override
        public void writeShortcut(Shortcut shortcut, IMemento memento) throws DAOException {
            memento.putInteger(AbstractShortcutXmlCodec.ID_TAG, shortcut.getId());
            memento.putString(AbstractShortcutXmlCodec.NAME_TAG, shortcut.getName());
            memento.putString(AbstractShortcutXmlCodec.GROUP_TAG, shortcut.getGroup());
            memento.putString(AbstractShortcutXmlCodec.PAYLOAD_TAG, shortcut.getPayload());
            memento.putString(AbstractShortcutXmlCodec.PRIORITY_TAG, shortcut.getPriority());
            memento.putString(AbstractShortcutXmlCodec.CATEGORY_TAG, shortcut.getCategory1());
            memento.putString(AbstractShortcutXmlCodec.CATEGORY2_TAG, shortcut.getCategory2());
            memento.putString(AbstractShortcutXmlCodec.WORKINGDIR_TAG, shortcut.getWorkingDir());
            memento.putString(AbstractShortcutXmlCodec.RGB_TAG, shortcut.getRgb());
            memento.putBoolean(AbstractShortcutXmlCodec.GRABOUTPUT_TAG, shortcut.isGrabOutput());
        }

        @Override
        public void readShortcut(Shortcut shortcut, IMemento memento) throws DAOException {
            shortcut.setId(memento.getInteger(AbstractShortcutXmlCodec.ID_TAG));
            shortcut.setName(memento.getString(AbstractShortcutXmlCodec.NAME_TAG));
            shortcut.setGroup(memento.getString(AbstractShortcutXmlCodec.GROUP_TAG));
            shortcut.setPayload(memento.getString(AbstractShortcutXmlCodec.PAYLOAD_TAG));
            shortcut.setPriority(memento.getString(AbstractShortcutXmlCodec.PRIORITY_TAG));
            shortcut.setCategory1(memento.getString(AbstractShortcutXmlCodec.CATEGORY_TAG));
            shortcut.setCategory2(memento.getString(AbstractShortcutXmlCodec.CATEGORY2_TAG));
            shortcut.setWorkingDir(memento.getString(AbstractShortcutXmlCodec.WORKINGDIR_TAG));
            shortcut.setRgb(memento.getString(AbstractShortcutXmlCodec.RGB_TAG));
            Boolean grabOutput = memento.getBoolean(AbstractShortcutXmlCodec.GRABOUTPUT_TAG);
            shortcut.setGrabOutput(grabOutput != null ? grabOutput : false);
        }
    }

    /**
     * XMLCodec Verion 1.2.
     * Änderungen gegenüber v1.1:
     *  - 'description' neues Attrubut - Beschreibung (Text)
     *
     * @author Alexander Schulz
     * Date: 01.12.2012
     */
    private static class ShortcutXmlCodecV1_2 extends ShortcutXmlCodecV1_1 {

        protected static final String DESCRIPTION_TAG = "description";

        @Override
        public void writeShortcut(Shortcut shortcut, IMemento memento) throws DAOException {
            super.writeShortcut(shortcut, memento);
            memento.putString(ShortcutXmlCodecV1_2.DESCRIPTION_TAG, shortcut.getDescription());
        }

        @Override
        public void readShortcut(Shortcut shortcut, IMemento memento) throws DAOException {
            super.readShortcut(shortcut, memento);
            shortcut.setDescription(memento.getString(ShortcutXmlCodecV1_2.DESCRIPTION_TAG));
        }

    }

    /**
     * Codec-Map.
     */
    private static Map<String, AbstractShortcutXmlCodec> codecMap = new HashMap<String, AbstractShortcutXmlDAO.AbstractShortcutXmlCodec>();
    static {
        AbstractShortcutXmlDAO.codecMap.put("1.0", new ShortcutXmlCodecV1_0());
        AbstractShortcutXmlDAO.codecMap.put("1.1", new ShortcutXmlCodecV1_1());
        AbstractShortcutXmlDAO.codecMap.put("1.2", new ShortcutXmlCodecV1_2());
    }

    /**
     * Aktuelle Codec-Version
     */
    private static final String CURRENT_CODEC_VERSION = "1.2";

    /**
     * Liefert zu der gewünschten Version passende Codec-Instanz (falls vorhanden).
     * @param version Gewünschte Version
     * @return Codec
     * @throws DAOException wenn Version nicht bekannt ist
     */
    private static AbstractShortcutXmlCodec getCodec(String version) throws DAOException {
        if (version == null) {
            // Annahme für unbekannte Version
            version = "1.0";
        }
        AbstractShortcutXmlCodec codec = AbstractShortcutXmlDAO.codecMap.get(version);
        if (codec == null) {
            throw new DAOException("unknown data version: " + version, null);
        }
        return codec;
    }

    /**
     * Überführt die Einträge aus der Map ins XML.
     * @param shortcuts Map mit en Einträgen (id, Item).
     * @param root Root-Tag
     * @param containerName Name des Containers
     * @param containerDescription Beshreibung des Containers
     * @return String mit XML-Daten
     * @throws DAOException Persistenz-Probleme
     */
    protected static String writeShortcutsToString(Map<String, String> prolog, Map<Integer, Shortcut> shortcuts, String root) throws DAOException {
        if (shortcuts != null) {
            StringWriter writer = new StringWriter();
            AbstractShortcutXmlDAO.writeShortcuts(prolog, shortcuts, root, writer);
            return writer.toString();
        }
        return "";
    }

    /**
     * Überführt die Einträge aus der Map ins XML und schreibt diese in ein gegebenen Writer.
     * @param shortcuts Map mit en Einträgen (id, Item).
     * @param root Root-Tag
     * @param containerName Name des Containers
     * @param containerDescription Beshreibung des Containers
     * @param writer Ziel für die Datenspeicherung
     * @throws DAOException Persistenz-Probleme
     */
    protected static void writeShortcuts(Map<String, String> prolog, Map<Integer, Shortcut> shortcuts, String root, Writer writer) throws DAOException {
        if (shortcuts != null) {
            XMLMemento rootMemento = XMLMemento.createWriteRoot(root);

            String containerName = prolog.get(IShortcutDAO.CONTAINER_NAME_TAG);
            String containerDescription = prolog.get(IShortcutDAO.CONTAINER_DESCRIPTION_TAG);
            String accessMode = prolog.get(IShortcutDAO.CONTAINER_ACCES_MODE_TAG);
            // Default-Access: keine Beschränkung
            if (accessMode == null) {
                accessMode = IShortcutDAO.CONTAINER_ACCES_MODE_RW;
            }

            // Prolog
            rootMemento.putString(IShortcutDAO.CREATOR_TAG, "ShortCut");
            rootMemento.putString(IShortcutDAO.CONTAINER_NAME_TAG, containerName);
            rootMemento.putString(IShortcutDAO.CONTAINER_DESCRIPTION_TAG, containerDescription);
            rootMemento.putString(IShortcutDAO.USER_TAG, System.getProperty("user.name")); // TODO: hier und im ShortcutContainer: eine Funktion erstellen.
            rootMemento.putString(IShortcutDAO.CONTAINER_ACCES_MODE_TAG, accessMode);
            rootMemento.putString(IShortcutDAO.OS_TAG, System.getProperty("os.name") + ", " + System.getProperty("os.version") + ", " + System.getProperty("os.arch"));
            rootMemento.putString(IShortcutDAO.DATE_TAG, DateFormat.getDateTimeInstance().format(new Date()));

            // Version
            rootMemento.putString(IShortcutDAO.VERSION_TAG, AbstractShortcutXmlDAO.CURRENT_CODEC_VERSION);

            AbstractShortcutXmlCodec codec = AbstractShortcutXmlDAO.getCodec(AbstractShortcutXmlDAO.CURRENT_CODEC_VERSION);

            // Data
            for (Integer id : shortcuts.keySet()) {
                Shortcut shortcut = shortcuts.get(id);
                // Einträge ohne ID ignorieren (darf eigentlich nicht passieren, wäre ein interner Fehler).
                if (shortcut.getId() == null) {
                    continue;
                }
                IMemento itemMemento = rootMemento.createChild(AbstractShortcutXmlDAO.SHORTCUT_TAG);
                codec.writeShortcut(shortcut, itemMemento);
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

            String version = rootMemento.getString(IShortcutDAO.VERSION_TAG);
            AbstractShortcutXmlCodec codec = AbstractShortcutXmlDAO.getCodec(version);

            IMemento[] mementos = rootMemento.getChildren(AbstractShortcutXmlDAO.SHORTCUT_TAG);
            for (int i = 0; i < mementos.length; i++) {
                IMemento memento = mementos[i];

                Shortcut shortcut = factory.createNewShortcut();

                codec.readShortcut(shortcut, memento);

                // Wegen der Migration der Daten aus einer (alten) Vorversion: ggf. eine neue ID hier erstellen
                if (shortcut.getId() == null) {
                    // Wenn Einträge ohne ID gefunden werden, dann dürften alle ohne ID sein (Daten der Vorversion).
                    // Einzelne Einträge ohne ID sind in der Speicherungsfunktion verhindert.
                    // Wahrscheinlichkeit einer Situation mit korrupten Daten ist gering wird in Kauf genommen.
                    // daher numerieren wird sie alle einfach durch.
                    shortcut.setId(i);
                }

                // Wenn ein ID-Map benötigt wird (ohne ID - kein Eintrag)
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
