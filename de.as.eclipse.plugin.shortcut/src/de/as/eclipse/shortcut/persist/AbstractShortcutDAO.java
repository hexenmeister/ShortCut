package de.as.eclipse.shortcut.persist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Abstrakte Implementierung der Basis-Funktionalit�t zum Persistieren von Shortcut-Eintr�gen.
 * Keine Angaben hier zum Format, oder Speicherort (-Typ).
 *
 * @author Alexander Schulz
 * Date: 19.11.2012
 */
public abstract class AbstractShortcutDAO implements IShortcutDAO {

    /**
     * Default-Constructor.
     */
    protected AbstractShortcutDAO() {
        super();
    }

    private ShortcutFactory factory;

    @Override
    public Map<String, String> init(ShortcutFactory factory) throws DAOException {
        this.factory = factory;
        // Existierenden Prolog lesen
        Map<String, String> prolog = this.readProlog();
        return prolog;
    }

    protected ShortcutFactory getFactory() {
        return this.factory;
    }

    @Override
    public List<Shortcut> getShortcuts() throws DAOException {
        return Collections.unmodifiableList(new ArrayList<Shortcut>(this.getShortcutsMap().values()));
    }

    @Override
    public void addShortcut(Map<String, String> prolog, Shortcut shortcut) throws DAOException {
        if (shortcut.getId() != null) {
            throw new RuntimeException("DAO error: attempt to add a new shortcut with an existing id. please use update function.");
        }
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        shortcut.setId(this.getNewId(m));
        m.put(shortcut.getId(), shortcut);
        this.saveShortcuts(prolog, m);
    }

    @Override
    public void removeShortcut(Map<String, String> prolog, Shortcut shortcut) throws DAOException {
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        m.remove(shortcut.getId());
        this.saveShortcuts(prolog, m);
    }

    @Override
    public void updateShortcut(Map<String, String> prolog, Shortcut shortcut) throws DAOException {
        if (shortcut.getId() == null) {
            throw new RuntimeException("DAO error: attempt to update a shortcut without an existing id. please use add function.");
        }

        Map<Integer, Shortcut> m = this.getShortcutsMap();
        m.put(shortcut.getId(), shortcut);
        this.saveShortcuts(prolog, m);
    }

    @Override
    public void mergeShortcuts(Map<String, String> prolog, List<Shortcut> newList) throws DAOException {
        Map<Integer, Shortcut> m = this.getShortcutsMap();

        if (newList != null) {
            if (m == null) {
                // z.B. wenn die (Quell-)Datei nicht existiert.
                // Map neu anlegen
                m = new HashMap<Integer, Shortcut>();
            }

            for (Shortcut newShortcut : newList) {
                // Prüfen, ob ein entsprechender Eintrag bereits vorliegt
                if (!m.containsValue(newShortcut)) {
                    // ID erstellen / erneuern
                    newShortcut.setId(this.getNewId(m));
                    m.put(newShortcut.getId(), newShortcut);
                }
            }
            this.saveShortcuts(prolog, m);
        }
    }

    @Override
    public void removeAllShortcuts() throws DAOException {
        this.saveShortcuts(null);
    }

    @Override
    public boolean isReadOnly() {
        // Speicherung defaultmäßig zulässig
        return false;
    }

    /**
     * Liefert eine noch ungebrauchte Integer-ID (verwendbar für einen neuen Eintrag).
     * IDs werden intern benötigt, um ein gegebenen Shortcut-Eintrag wieder zu erkennen.
     * @param m Map mit den aktuell existierenden Shortcut-Einträgen
     * @return eine neue ID (Integer)
     */
    private Integer getNewId(Map<Integer, Shortcut> m) {
        int newId = m.size();

        // Wenn die Anzahl der Elemente als ID bereits existiert, dann haben wir eine ID-Lücke, die gefüllt werden kann.
        while (m.get(newId) != null) {
            newId--;
        }

        return newId;
    }

    /**
     * Schreibt die Datensätze neu.
     * @param prolog Map mit Meta-Daten (Name, Description)
     * @throws DAOException Persistenz-Probleme
     */
    @Override
    public void saveShortcuts(Map<String, String> prolog) throws DAOException {
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        this.saveShortcuts(prolog, m);
    }

    /**
     * Liest die persistierte Einträge.
     * @throws DAOException Persistenz-Probleme
     * @return Map mit den Einträgen.
     */
    protected abstract Map<Integer, Shortcut> getShortcutsMap() throws DAOException;

    /**
     * Persistiert die Einträge.
     * @param prolog Map mit Meta-Daten (Name, Description)
     * @param shortcuts Map mit den Einträgen
     * @throws DAOException Persistenz-Probleme
     */
    protected abstract void saveShortcuts(Map<String, String> prolog, Map<Integer, Shortcut> shortcuts) throws DAOException;

}
