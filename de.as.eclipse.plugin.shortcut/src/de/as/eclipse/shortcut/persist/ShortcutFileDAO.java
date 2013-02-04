package de.as.eclipse.shortcut.persist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Diese ShortcutDAO-Implementierung benutzt  eine Daei (angegeben per Pfad) als Daten-Container (im XML-Format).
 *
 * @author Alexander Schulz
 * Date: 20.11.2012
 */
public class ShortcutFileDAO extends AbstractShortcutXmlDAO implements IReloadableDAO {

    // Eintrag-Tag
    private static final String SHORTCUTS_TAG = "shortcuts";

    private File containerFile;

    public ShortcutFileDAO(String path) {
        this.containerFile = new File(path);
    }

    @Override
    public Map<String, String> readProlog() throws DAOException {
        Reader reader;
        try {
            reader = new FileReader(this.containerFile);
        } catch (FileNotFoundException e) {
            // TODO Exception Behandlung
            e.printStackTrace();
            return null;
        }

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(reader);
            Map<String, String> m = AbstractShortcutXmlDAO.readProlog(bufferedReader);
            return m;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    protected Map<Integer, Shortcut> getShortcutsMap() throws DAOException {
        Reader reader;
        try {
            reader = new FileReader(this.containerFile);
        } catch (FileNotFoundException e) {
            // TODO Exception Behandlung
            e.printStackTrace();
            return null;
        }

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(reader);
            Map<Integer, Shortcut> m = AbstractShortcutXmlDAO.readShortcuts(bufferedReader, this.getFactory());
            return m;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    protected void saveShortcuts(Map<String, String> prolog, Map<Integer, Shortcut> shortcuts) throws DAOException {
        Writer writer;
        try {
            writer = new FileWriter(this.containerFile);
        } catch (IOException e) {
            // TODO Exception behandlung
            e.printStackTrace();
            return;
        }

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(writer);
            AbstractShortcutXmlDAO.writeShortcuts(prolog, shortcuts, ShortcutFileDAO.SHORTCUTS_TAG, bufferedWriter);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public String getConfigString() {
        // Liefert Datei-Pfad, mit dem eine gleichwertige (auf der gleichen Datei besierende) Instanz erstellt werden kann.
        return this.containerFile.getAbsolutePath();
    }

}
