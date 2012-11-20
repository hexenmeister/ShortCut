package de.as.eclipse.shortcut.persist;

import java.util.ArrayList;
import java.util.Collections;
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
    public void init(ShortcutFactory factory) {
        this.factory = factory;
    }

    protected ShortcutFactory getFactory() {
        return this.factory;
    }

    @Override
    public List<Shortcut> getShortcuts() throws DAOException {
        return Collections.unmodifiableList(new ArrayList<Shortcut>(this.getShortcutsMap().values()));
    }

    @Override
    public void addShortcut(Shortcut shortcut) throws DAOException {
        if (shortcut.getId() != null) {
            throw new RuntimeException("DAO error: attempt to add a new shortcut with an existing id. please use update function.");
        }
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        shortcut.setId(this.getNewId(m));
        m.put(shortcut.getId(), shortcut);
        this.saveShortcuts(m);
    }

    @Override
    public void removeShortcut(Shortcut shortcut) throws DAOException {
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        m.remove(shortcut.getId());
        this.saveShortcuts(m);
    }

    @Override
    public void updateShortcut(Shortcut shortcut) throws DAOException {
        if (shortcut.getId() == null) {
            throw new RuntimeException("DAO error: attempt to update a shortcut without an existing id. please use add function.");
        }

        Map<Integer, Shortcut> m = this.getShortcutsMap();
        m.put(shortcut.getId(), shortcut);
        this.saveShortcuts(m);
    }

    @Override
    public void mergeShortcuts(List<Shortcut> newList) throws DAOException {
        Map<Integer, Shortcut> m = this.getShortcutsMap();
        if (newList != null) {
            for (Shortcut newShortcut : newList) {
                // Pr�fen, ob ein entsprechender Eintrag bereits vorliegt
                if (!m.containsValue(newShortcut)) {
                    // ID erstellen / erneuern
                    newShortcut.setId(this.getNewId(m));
                    m.put(newShortcut.getId(), newShortcut);
                }
            }
            this.saveShortcuts(m);
        }
    }

    @Override
    public void removeAllShortcuts() throws DAOException {
        this.saveShortcuts(null);
    }

    @Override
    public boolean isReadOnly() {
        // Speicherung defaultm��ig zul�ssig
        return false;
    }

    /**
     * Liefert eine noch ungebrauchte Integer-ID (verwendbar f�r einen neuen Eintrag).
     * IDs werden intern ben�tigt, um ein gegebenen Shortcut-Eintrag wieder zu erkennen.
     * @param m Map mit den aktuell existierenden Shortcut-Eintr�gen
     * @return eine neue ID (Integer)
     */
    private Integer getNewId(Map<Integer, Shortcut> m) {
        int newId = m.size();

        // Wenn die Anzahl der Elemente als ID bereits existiert, dann haben wir eine ID-L�cke, die gef�llt werden kann.
        while (m.get(newId) != null) {
            newId--;
        }

        return newId;
    }

    /**
     * Liest die persistierte Einträge.
     * @throws DAOException Persistenz-Probleme
     * @return Map mit den Einträgen.
     */
    protected abstract Map<Integer, Shortcut> getShortcutsMap() throws DAOException;

    /**
     * Persistiert die Einträge.
     * @param shortcuts Map mit den Einträgen
     */
    protected abstract void saveShortcuts(Map<Integer, Shortcut> shortcuts) throws DAOException;

}
