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
public class ShortcutFileDAO extends AbstractShortcutXmlDAO {

    // Eintrag-Tag
    private static final String SHORTCUTS_TAG = "shortcuts";

    private File containerFile;

    public ShortcutFileDAO(String path) {
        this.containerFile = new File(path);
    }

    @Override
    protected Map<Integer, Shortcut> getShortcutsMap() {
        Reader reader;
        try {
            reader = new FileReader(this.containerFile);
        } catch (FileNotFoundException e) {
            // TODO Exception behandlung
            e.printStackTrace();
            return null;
        }

        BufferedReader bufferedReader = new BufferedReader(reader);
        Map<Integer, Shortcut> m = AbstractShortcutXmlDAO.readShortcuts(bufferedReader, this.getFactory());
        return m;
    }

    @Override
    protected void saveShortcuts(Map<Integer, Shortcut> shortcuts) {
        Writer writer;
        try {
            writer = new FileWriter(this.containerFile);
        } catch (IOException e) {
            // TODO Exception behandlung
            e.printStackTrace();
            return;
        }
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        AbstractShortcutXmlDAO.writeShortcuts(shortcuts, ShortcutFileDAO.SHORTCUTS_TAG, bufferedWriter);
    }

}
