package de.as.eclipse.shortcut.persist;

import java.util.List;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Representiert ein Container (Speicherort) für eine Shortcut-Liste.
 *
 * @author Alexander Schulz
 * Date: 18.11.2012
 */
public final class ShortcutContainer {

    private String name;

    private IShortcutDAO dao;

    private ShortcutFactory shortcutFactory;

    private boolean visible = true;

    // Ermöglicht in der Anwendung die Schreibzugriffe zu sperren, auch wenn DAO sie erlaubt.
    // Andersherum kann der im DAO verweigerter Zugriff natürlich nicht freigegeben werden.
    private boolean readOnly = false;

    /**
     * Geschützter Konsruktor.
     * @param dao ShortcutDAO
     * @param name Name/Neschreibung
     */
    ShortcutContainer(IShortcutDAO dao, String name) {
        this.dao = dao;
        this.shortcutFactory = new ShortcutFactory(this);
        dao.init(this.shortcutFactory);
        this.name = name;
    }

    /**
     * Liefert den symbolischen Namen der Shortcut-Container.
     * Ein Container enthält eine Liste der Shortcuts, die an einem bestimmten Ort gespeichert werden.
     * Mehrere Container werden in einem Store zusammengefasst (s. ShortcutStore).
     * @return Container-Name
     */
    public String getName() {
        return this.name;
    }

    //    private ShortcutFactory getShortcutFactory() {
    //        return this.shortcutFactory;
    //    }

    /**
     * Erstellt eine neue Instanz der Klasse Shortcut.
     * @return ein neues Shortcut-Objekt
     */
    public Shortcut createNewShortcut() {
        return this.shortcutFactory.createNewShortcut();
    }

    protected IShortcutDAO getDAO() {
        return this.dao;
    }

    /**
     * Gibt an, ob die Elemente aus diesem Container sichtbar sein sollen.
     * @return Sichtbarkeit
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Definiert, ob die Elemente aus diesem Container sichtbar sein sollen.
     * @param visible Sichtbarkeit
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gibt an, ob der Container beschreibbar ist (d.h. Elemente können hinzugefügt, geändert oder gelöscht werden).
     * Das DAO ist vorrangig für die Änderbarkeit zuständig,
     * daher wird für einen im DAO als nicht änderbar definierten Container
     * immer der Wert false geliefert.
     * @return true, wenn das Container beschreibbar ist
     */
    public boolean isReadOnly() {
        // nur wenn hier und im DAO Schreibzugriffe erlaubt
        return this.readOnly && this.getDAO().isReadOnly();
    }

    /**
     * Erlaubt die �nerbarkeit des Containers zu definieren.
     * Das DAO ist vorrangig für die Änderbarkeit zuständig,
     * daher kann im DAO als nicht änderbar definierter Container
     * nicht nachträglich als beschreibbar umdefiniert werden.
     * 
     * @param readOnly true, wenn änderbar, fals sonst
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Liefert Liste aller gespeicherten Einträge.
     * @throws DAOException Persistenz-Probleme
     * @return Liste der Einträge
     */
    public List<Shortcut> getShortcuts() throws DAOException {
        return this.getDAO().getShortcuts();
    }

    /**
     * Fügt ein neuen Eintrag hinzu.
     * @param shortcut Shortcut-Eintrag
     * @return false, wenn Container nicht beschreibbar, true sonst.
     */
    public boolean addShortcut(Shortcut shortcut) throws DAOException {
        if (!this.isReadOnly()) {
            this.getDAO().addShortcut(shortcut);
            return true;
        }
        return false;
    }

    /**
     * Entfernt (löscht) den übergeben Shortcut.
     * @param shortcut Shortcut-Eintrag
     * @return false, wenn Container nicht beschreibbar, true sonst.
     */
    public boolean removeShortcut(Shortcut shortcut) throws DAOException {
        if (!this.isReadOnly()) {
            this.getDAO().removeShortcut(shortcut);
            return true;
        }
        return false;
    }

    /**
     * Aktualisiert die Daten f�r ein gegebenen Eintrag.
     * @param shortcut Shortcut-Eintrag
     * @return false, wenn Container nicht beschreibbar, true sonst.
     */
    public boolean updateShortcut(Shortcut shortcut) throws DAOException {
        if (!this.isReadOnly()) {
            this.getDAO().updateShortcut(shortcut);
            return true;
        }
        return false;
    }

    /**
     * Fügt alle Einträge aus der gegebenen Liste zu den gespeicheren Einträgen hinzu.
     * @param newList Liste der Einträge
     * @return false, wenn Container nicht beschreibbar, true sonst.
     */
    public boolean mergeShortcuts(List<Shortcut> newList) throws DAOException {
        if (!this.isReadOnly()) {
            this.getDAO().mergeShortcuts(newList);
            return true;
        }
        return false;
    }

    /**
     * Entfernt alle gespeicherten Einträge.
     * @return false, wenn Container nicht beschreibbar, true sonst.
     */
    public boolean removeAllShortcuts() throws DAOException {
        if (!this.isReadOnly()) {
            this.getDAO().removeAllShortcuts();
            return true;
        }
        return false;
    }
}
