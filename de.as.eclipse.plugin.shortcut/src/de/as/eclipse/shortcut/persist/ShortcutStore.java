package de.as.eclipse.shortcut.persist;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import de.as.eclipse.shortcut.business.Shortcut;
import de.as.eclipse.shortcut.persist.ShortcutFactory.ShortcutDecorator;

/**
 * Verwaltet angemeldete Instanzen von Shortcut-Containern.
 *
 * @author Alexander Schulz
 * Date: 21.11.2012
 */
public class ShortcutStore {

    private static final String DAO_CLASS_TAG = "dao-class";

    private static final String DAO_INIT_TAG = "dao-init";

    private static final String CONTAINER_NAME_TAG = "name";

    private static final String CONTAINER_CONFIG_LIST_TAG = "container-config-list";

    private static final String CONTAINERS_TAG = "containers";

    private static final String CONTAINER_TAG = "container";

    private List<ShortcutContainer> containerList;

    private IPreferenceStore config;

    private ShortcutContainer defaultContainer;

    /**
     * Constructor.
     */
    public ShortcutStore(IPreferenceStore configPreferenceStore) {
        this.config = configPreferenceStore;
        this.containerList = new ArrayList<ShortcutContainer>();
        this.init();
    }

    /**
     * Initialisierung.
     */
    protected void init() {
        // Default-Container, ist immer da und muss weder gespeichert, noch gelesen werden.
        ShortcutPreferenceStoreDAO dao = new ShortcutPreferenceStoreDAO(this.config);
        ShortcutContainer defaultContainer = this.createNewContainer(dao, "Internal / Workspace");
        this.containerList.add(defaultContainer);
        this.defaultContainer = defaultContainer;
        // container lesen
        this.readContainerList();
    }

    /**
     * Gibt an, ob der gegebener Container der Default-Container ist.
     * Dieser ist der erste, immer vorhandene Container und kann nicht entfernt werden.
     * @param container Vergleichscontainer
     * @return true = default
     */
    public boolean isDefault(ShortcutContainer container) {
        return this.defaultContainer == container;
    }

    /**
     * Aufräumen.
     */
    public void close() {
        this.saveContainerList();
    }

    /**
     * Wird aufgerufen, wenn Container-Liste verändert wurde.
     */
    private void listUpdated() {
        // Liste speichern
        this.saveContainerList();
    }

    /**
     * Liest gespeicherte Container-Konfiguration und stellt die Liste der entsprechenden Instanzen wieder her.
     * Achtung: die existierende Liste wird ergänzt, keine bereits vorhandene Container werden entfernt ode ersetzt!
     */
    private void readContainerList() {
        String configStr = this.config.getString(ShortcutStore.CONTAINER_CONFIG_LIST_TAG);
        if ((configStr != null) && (configStr.trim().length() > 0)) {
            // Wenn vorhanden
            List<ShortcutContainer> containers = this.verarbeiteContainerConfig(configStr);
            this.containerList.addAll(containers);
        }
    }

    /**
     * Speichert (dauerhaft) Liste der Container so, dass die Container daraus wieder hergestellt werden können.
     */
    private void saveContainerList() {
        String cconf = this.createContainerConfig();
        if (cconf != null) {
            this.config.setValue(ShortcutStore.CONTAINER_CONFIG_LIST_TAG, cconf);
        } else {
            // TODO: Meldung?
        }
    }

    /**
     * Geht die Liste der Container durch und erstellt daruas ein Konfiguration-String (XML).
     * Es werden nur Container berücksichtigt, deren DAOs Interface IReloadableDAO implementieren.
     * @return Konfiguration-String
     */
    private String createContainerConfig() {
        XMLMemento rootMemento = XMLMemento.createWriteRoot(ShortcutStore.CONTAINERS_TAG);
        for (ShortcutContainer container : this.containerList) {
            IShortcutDAO dao = container.getDAO();
            if (dao instanceof IReloadableDAO) {
                IMemento containerMemento = rootMemento.createChild(ShortcutStore.CONTAINER_TAG);
                containerMemento.putString(ShortcutStore.CONTAINER_NAME_TAG, container.getName());
                containerMemento.putString(ShortcutStore.DAO_CLASS_TAG, dao.getClass().getName());
                containerMemento.putString(ShortcutStore.DAO_INIT_TAG, ((IReloadableDAO) dao).getConfigString());
            }
        }

        try {
            StringWriter writer = new StringWriter();
            rootMemento.save(writer);
            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Verarbeitet Konfiguration-String, erstellt daraus Container-Instanzen mit entsprechenden DAO-Klassen.
     * @param configString Konfiguration-String
     * @return Liste der erstellten Container
     */
    private List<ShortcutContainer> verarbeiteContainerConfig(String configString) {
        List<ShortcutContainer> ret = new ArrayList<ShortcutContainer>();
        StringReader reader = new StringReader(configString);
        try {
            XMLMemento rootMemento = XMLMemento.createReadRoot(reader);
            IMemento[] mementos = rootMemento.getChildren(ShortcutStore.CONTAINER_TAG);
            for (int i = 0; i < mementos.length; i++) {
                IMemento memento = mementos[i];
                // TODO: XML lesen, Container/DAOs erstellen

                String name = memento.getString(ShortcutStore.CONTAINER_NAME_TAG);
                String daoClass = memento.getString(ShortcutStore.DAO_CLASS_TAG);
                String daoInit = memento.getString(ShortcutStore.DAO_INIT_TAG);
                // Parameter überprüfen (not null)
                if ((name != null) && (daoClass != null)) {
                    try {
                        Class<?> clazz = Class.forName(daoClass);
                        Constructor<?> c = clazz.getConstructor(String.class);
                        IShortcutDAO dao = (IShortcutDAO) c.newInstance(daoInit);
                        ShortcutContainer container = new ShortcutContainer(dao, name);
                        ret.add(container);
                    } catch (ClassNotFoundException e) {
                        // TODO vernünftige Exception und/oder Log?
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        // TODO vernünftige Exception und/oder Log?
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO vernünftige Exception und/oder Log?
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        // TODO vernünftige Exception und/oder Log?
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO vernünftige Exception und/oder Log?
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO vernünftige Exception und/oder Log?
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // TODO vernünftige Exception und/oder Log?
                        e.printStackTrace();
                    }
                }
            }

        } catch (WorkbenchException e) {
            // TODO vernünftige Exception und/oder Log?
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Liefert Liste bekannter Shortcut-Container.
     * @return Leiste der Shortcut-Container.
     */
    public List<ShortcutContainer> getContainers() {
        return Collections.unmodifiableList(this.containerList);
    }

    /**
     * Erstellt ein neues Shortcut-Container mit den gegebenen Namen und Persistenz-Schnittstelle (DAO).
     * @param dao DAO
     * @param name Name
     * @return Instanz
     */
    public ShortcutContainer createNewContainer(IShortcutDAO dao, String name) {
        return new ShortcutContainer(dao, name);
    }

    /**
     * Meldet einen neuen Shortcut-Container an. D.h. deren Inhalte können angezeigt und verändert werden.
     * @param container Shortcut-Container
     */
    public final void addContainer(ShortcutContainer container) {
        this.containerList.add(container);
        this.listUpdated();
    }

    /**
     * Meldet einen Shortcut-Container ab.
     * @param container Shortcut-Container
     * @return true, wenn der Container bekannt war. false, wenn nicht unternommen wurde
     */
    public final boolean removeContainer(ShortcutContainer container) {
        boolean ret = this.containerList.remove(container);
        this.listUpdated();
        return ret;
    }

    /**
     * Liefert Liste der sichtbaren Container.
     * @return Liste der Container
     */
    public List<ShortcutContainer> getVisibleContainers() {
        // liste filtern: nur sichtbare
        List<ShortcutContainer> ret = new ArrayList<ShortcutContainer>();
        for (ShortcutContainer shortcutContainer : this.containerList) {
            if (shortcutContainer.isVisible()) {
                ret.add(shortcutContainer);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    /**
     * Liefert liste der änderbaren Container.
     * @return Liste der Container
     */
    public List<ShortcutContainer> getChangeableContainers() {
        // liste filtern: nur beschreibbare
        List<ShortcutContainer> ret = new ArrayList<ShortcutContainer>();
        for (ShortcutContainer shortcutContainer : this.containerList) {
            if (!shortcutContainer.isReadOnly()) {
                ret.add(shortcutContainer);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    /**
     * Lifert Liste aller SHortcuts aller sichtbaren Contianer.
     * @return Liste der Shortcuts
     */
    public List<Shortcut> getVisibleShortcuts() {
        List<Shortcut> ret = new ArrayList<Shortcut>();
        List<ShortcutContainer> list = this.getVisibleContainers();
        for (ShortcutContainer container : list) {
            try {
                ret.addAll(container.getShortcuts());
            } catch (Exception e) {
                // ignore: wenn ein Container nicht zugreifbar ist, dann wird er ausgelassen und mit den restlichen fortgefahren
                // TODO: Vormerken, Meldung ausgeben?
            }
        }
        return ret;
    }

    /**
     * Entfernt (löscht) gegebenen Shortcut aus seine ParentContainer.
     * Wirft RuntimeException, falls Shortcut-Instanz kein ShortcutDecorator ist, und so kein ParentContainer festgestellt werden kann (darf eigentlich nicht passieren).
     * @param shortcut zu entfernendes Shortcut
     * @throws DAOException falls dabei Probleme auftretten
     */
    public void removeShortcut(Shortcut shortcut) throws DAOException {
        if (shortcut instanceof ShortcutDecorator) {
            ShortcutContainer container = ((ShortcutDecorator) shortcut).getParentContainer();
            container.removeShortcut(shortcut);
        } else if (shortcut != null) {
            throw new RuntimeException("unexpected subclass of Shortcut: " + shortcut.getClass().getName());
        }
    }

    /**
     * Liefert den zu dem gegebenen Shortcut gehörenden Vater-Container.
     * Wirft RuntimeException, falls Shortcut-Instanz kein ShortcutDecorator ist, und so kein ParentContainer festgestellt werden kann (darf eigentlich nicht passieren).
     * @param shortcut Shortcut, zu dem Container gesucht wird
     * @return Container.
     */
    public ShortcutContainer getParentContainer(Shortcut shortcut) {
        if (shortcut instanceof ShortcutDecorator) {
            ShortcutContainer container = ((ShortcutDecorator) shortcut).getParentContainer();
            return container;
        } else if (shortcut != null) {
            throw new RuntimeException("unexpected subclass of Shortcut: " + shortcut.getClass().getName());
        }

        return null;
    }

    /**
     * Kopiert alle Shortcuts aus einem Container in ein anderes. Die Shortcuts werden geklonnt.
     * Es ist nicht sinnvoll Quelle und Ziel gleich zu setzen, denn der Container bleibt dabei unverändert.
     * @param from Quell-Container
     * @param to Ziel-Container
     * @throws DAOException falls dabei Probleme auftretten
     */
    public void copyShortcuts(ShortcutContainer from, ShortcutContainer to) throws DAOException {
        to.mergeShortcuts(from.getShortcuts());
    }

    // TODO: Filter-Methoden (definieren, abfragen, ...)
}
