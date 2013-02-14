package de.as.eclipse.shortcut.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Representiert ein Container (Speicherort) für eine Shortcut-Liste.
 *
 * @author Alexander Schulz
 * Date: 18.11.2012
 */
public final class ShortcutContainer {

    private IShortcutDAO dao;

    private Map<String, String> prolog;

    private ShortcutFactory shortcutFactory;

    private boolean visible = true;

    // Ermöglicht in der Anwendung die Schreibzugriffe zu sperren, auch wenn DAO sie erlaubt.
    // Andersherum kann der im DAO verweigerter Zugriff natürlich nicht freigegeben werden.
    private boolean readOnly = false;

    /**
     * Geschützter Konsruktor.
     * @param dao ShortcutDAO
     * @param name Name/Neschreibung
     * @throws DAOException Persistenz-Probleme
     */
    ShortcutContainer(IShortcutDAO dao, String name) throws DAOException {
        this.checkContainerName(name);
        this.checkDao(dao);

        this.dao = dao;
        this.shortcutFactory = new ShortcutFactory(this);
        this.prolog = dao.init(this.shortcutFactory);
        //        try {
        //            this.prolog = this.dao.readProlog();
        if (this.prolog == null) {
            this.prolog = new HashMap<String, String>();
        }
        //        } catch (DAOException e) {
        //            // TODO Log
        //        }
        this.setName(name);

        String user = System.getProperty("user.name");
        boolean ro = (user != null && user.equalsIgnoreCase(this.prolog.get(IShortcutDAO.USER_TAG)));
        this.setReadOnly(ro);
    }

    /**
     * Prüft, ob der Name nicht null und nicht leer ist.
     * @param name Container name
     */
    private void checkContainerName(String name) {
        if (name == null) {
            throw new RuntimeException("Container name may not be null");
        }
        if (name.trim().length() == 0) {
            throw new RuntimeException("Container name may not be empty");
        }
    }

    /**
     * Prüft, ob DAO nicht null ist.
     * @param dao DAO
     */
    private void checkDao(IShortcutDAO dao) {
        if (dao == null) {
            throw new RuntimeException("DAO may not be null");
        }
    }

    /**
     * Liefert den symbolischen Namen der Shortcut-Container.
     * Ein Container enthält eine Liste der Shortcuts, die an einem bestimmten Ort gespeichert werden.
     * Mehrere Container werden in einem Store zusammengefasst (s. ShortcutStore).
     * @return Container-Name
     */
    public String getName() {
        return this.prolog.get(IShortcutDAO.CONTAINER_NAME_TAG);
    }

    private void setName(String name) {
        this.prolog.put(IShortcutDAO.CONTAINER_NAME_TAG, name);
    }

    /**
     * Bietet die Möglichkeit, den Container zu umbenennen.
     * @param name Der neue containername.
     * @throws DAOException Persistenz-Probleme
     * @return true, wenn erfolgreich (Container nicht schreibgeschützt)
     */
    public boolean rename(String name) throws DAOException {
        if (!this.isReadOnly()) {
            this.checkContainerName(name);

            this.setName(name);
            // Den neuen Namen dem DAO mitteilen
            this.dao.saveShortcuts(this.prolog);
            return true;
        }
        return false;
    }

    /**
     * Liefert Container-Beschreibung-Text.
     * @return Description-String
     */
    public String getDescription() {
        return this.prolog.get(IShortcutDAO.CONTAINER_DESCRIPTION_TAG);
    }

    /**
     * Definiert die Container-Beschreibung.
     * @param description neue Container-Beschreibung
     * @throws DAOException Persistenz-Probleme
     * @return true, wenn erfolgreich (Container nicht schreibgeschützt)
     */
    public boolean setDescription(String description) throws DAOException {
        if (!this.isReadOnly()) {
            this.prolog.put(IShortcutDAO.CONTAINER_DESCRIPTION_TAG, description);
            this.dao.saveShortcuts(this.prolog);
            return true;
        }
        return false;
    }

    public Map<String, String> getProlog() {
        return this.prolog;
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
    protected void setReadOnly(boolean readOnly) {
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
     * Operation wird nicht zugelassen, falls der Shortcut zu einem anderen Container zugeordnet ist.
     * @param shortcut Shortcut-Eintrag
     * @return true, wenn Operation erfolgreich, false sonst (z.B.  Container nicht beschreibbar, oder falscher Container).
     */
    public boolean addShortcut(Shortcut shortcut) throws DAOException {
        if (!this.isReadOnly() && this.shortcutFactory.checkContainer(shortcut)) {
            this.getDAO().addShortcut(this.prolog, shortcut);
            return true;
        }
        return false;
    }

    /**
     * Entfernt (löscht) den übergeben Shortcut.
     * Operation wird nicht zugelassen, falls der Shortcut zu einem anderen Container zugeordnet ist.
     * @param shortcut Shortcut-Eintrag
     * @return true, wenn Operation erfolgreich, false sonst (z.B.  Container nicht beschreibbar, oder falscher Container).
     */
    public boolean removeShortcut(Shortcut shortcut) throws DAOException {
        if (!this.isReadOnly() && this.shortcutFactory.checkContainer(shortcut)) {
            this.getDAO().removeShortcut(this.prolog, shortcut);
            return true;
        }
        return false;
    }

    /**
     * Aktualisiert die Daten für ein gegebenen Eintrag.
     * Operation wird nicht zugelassen, falls der Shortcut zu einem anderen Container zugeordnet ist.
     * @param shortcut Shortcut-Eintrag
     * @return true, wenn Operation erfolgreich, false sonst (z.B.  Container nicht beschreibbar, oder falscher Container).
     */
    public boolean updateShortcut(Shortcut shortcut) throws DAOException {
        if (!this.isReadOnly() && this.shortcutFactory.checkContainer(shortcut)) {
            this.getDAO().updateShortcut(this.prolog, shortcut);
            return true;
        }
        return false;
    }

    /**
     * Fügt alle Einträge aus der gegebenen Liste zu den gespeicheren Einträgen hinzu.
     * Ist auch dann möglich, falls die Shortcuts bereits einem anderen Container zugeordnet sind.
     * In Letzten Fall werden die Einträge geklonnt und mit diesem Container verknüpft.
     * @param newList Liste der Einträge
     * @return false, wenn Container nicht beschreibbar, true sonst.
     */
    public boolean mergeShortcuts(List<Shortcut> list) throws DAOException {
        // ggf. Container korrigieren
        List<Shortcut> newList = new ArrayList<Shortcut>(list.size());
        for (Shortcut shortcut : list) {
            newList.add(this.shortcutFactory.ensureCorrectContainer(shortcut));
        }
        if (!this.isReadOnly()) {
            this.getDAO().mergeShortcuts(this.prolog, newList);
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
