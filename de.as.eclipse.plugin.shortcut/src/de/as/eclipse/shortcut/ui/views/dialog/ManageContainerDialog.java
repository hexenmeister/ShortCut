package de.as.eclipse.shortcut.ui.views.dialog;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.persist.DAOException;
import de.as.eclipse.shortcut.persist.IShortcutDAO;
import de.as.eclipse.shortcut.persist.ShortcutContainer;
import de.as.eclipse.shortcut.persist.ShortcutFileDAO;
import de.as.eclipse.shortcut.persist.ShortcutStore;
import de.as.eclipse.shortcut.ui.UIConstants;
import de.as.eclipse.shortcut.ui.UIUtils;

public class ManageContainerDialog extends TrayDialog {
    private Table table;

    private Button btnRemove;

    private Button btnDelete;

    private Button btnExportToFile;

    private CheckboxTableViewer checkboxTableViewer;

    /**
     * Create the dialog.
     * @param parentShell
     */
    public ManageContainerDialog(Shell parentShell) {
        super(parentShell);
        this.setHelpAvailable(false);
        this.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        // Wunschgröße
        int w = 600, h = 400;
        // In der Mitte des Parent-Fensters ausrichten
        Rectangle r = shell.getParent().getBounds();
        shell.setBounds(r.x + ((r.width - w) / 2), r.y + ((r.height - h) / 2), w, h);

        shell.setImage(Activator.getImage(UIConstants.ICON_CONTAINERS));
    }

    /**
     * Create contents of the dialog.
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        this.getShell().setText("Manage shortcut container");

        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.numColumns = 2;

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setText("available container");
        new Label(container, SWT.NONE);

        // XXX: Beispiel: http://www.javadocexamples.com/java_source/org/eclipse/jdt/internal/debug/ui/propertypages/InstanceFilterEditor.java.html
        this.checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
        this.table = this.checkboxTableViewer.getTable();
        this.table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 7));

        this.checkboxTableViewer.setContentProvider(new IStructuredContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
            }

            @Override
            public Object[] getElements(Object arg0) {
                List<?> list = (List<?>) arg0;
                return list.toArray();
            }

        });

        this.checkboxTableViewer.setLabelProvider(new ITableLabelProvider() {
            // TODO : change anonym classes to nested
            @Override
            public void removeListener(ILabelProviderListener arg0) {
            }

            @Override
            public boolean isLabelProperty(Object arg0, String arg1) {
                return false;
            }

            @Override
            public void dispose() {
            }

            @Override
            public void addListener(ILabelProviderListener arg0) {
            }

            @Override
            public String getColumnText(Object arg0, int arg1) {
                ShortcutContainer sc = (ShortcutContainer) arg0;
                String colText = sc.getName();
                ShortcutStore shortcutStore = Activator.getDefault().getShortcutStore();
                if (shortcutStore.isDefault(sc)) {
                    colText += " (default)";
                }
                if (sc.isReadOnly()) {
                    colText += " (read only)";
                }
                return colText;
            }

            @Override
            public Image getColumnImage(Object arg0, int arg1) {
                return null;
            }
        });

        this.checkboxTableViewer.setCheckStateProvider(new ICheckStateProvider() {
            @Override
            public boolean isGrayed(Object element) {
                return false;
            }

            @Override
            public boolean isChecked(Object element) {
                ShortcutContainer sc = (ShortcutContainer) element;
                return sc.isVisible();
            }
        });

        this.checkboxTableViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                ShortcutContainer sc = (ShortcutContainer) event.getElement();
                sc.setVisible(event.getChecked());
            }
        });

        this.checkboxTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ManageContainerDialog.this.adjustButtonsEnabledStatus();
            }
        });

        this.refreshContainerList();

        Button btnCreate = new Button(container, SWT.NONE);
        btnCreate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO
            }
        });
        GridData gd_btnCreate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnCreate.widthHint = 80;
        btnCreate.setLayoutData(gd_btnCreate);
        btnCreate.setText("Create");

        this.btnRemove = new Button(container, SWT.NONE);
        this.btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //TODO
                if (ManageContainerDialog.this.table.getSelectionIndex() >= 0) {
                    ShortcutStore shortcutStore = Activator.getDefault().getShortcutStore();
                    ShortcutContainer container = shortcutStore.getContainers().get(ManageContainerDialog.this.table.getSelectionIndex());
                    if (!shortcutStore.isDefault(container)) {
                        shortcutStore.removeContainer(container);
                        ManageContainerDialog.this.refreshContainerList();
                    } else {
                        MessageDialog.openError(ManageContainerDialog.this.getShell(), "Error", "Default-Container could not be removed");
                    }
                }
            }
        });
        GridData gd_btnRemove = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnRemove.widthHint = 80;
        this.btnRemove.setLayoutData(gd_btnRemove);
        this.btnRemove.setText("Remove");

        this.btnDelete = new Button(container, SWT.NONE);
        this.btnDelete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // TODO
            }
        });
        GridData gd_btnDelete = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnDelete.widthHint = 80;
        this.btnDelete.setLayoutData(gd_btnDelete);
        this.btnDelete.setText("Delete");

        Button btnImport = new Button(container, SWT.NONE);
        btnImport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String path = UIUtils.browseFile("container.import.file", ManageContainerDialog.this.getShell(), null, SWT.OPEN);
                if (path != null) {
                    ShortcutStore shortcutStore = Activator.getDefault().getShortcutStore();
                    try {
                        ShortcutFileDAO dao = new ShortcutFileDAO(path);
                        Map<String, String> prologMap = dao.readProlog();
                        ShortcutContainer container = shortcutStore.createNewContainer(dao, prologMap.get(IShortcutDAO.CONTAINER_NAME_TAG));
                        // TODO: Prüfung, ob derselbe (nicht nur gleichbenannte) Container bereits in der Liste vorhanden ist
                        shortcutStore.addContainer(container);
                        ManageContainerDialog.this.refreshContainerList();
                    } catch (DAOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        GridData gd_btnImport = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnImport.widthHint = 80;
        btnImport.setLayoutData(gd_btnImport);
        btnImport.setText("Import");

        this.btnExportToFile = new Button(container, SWT.NONE);
        this.btnExportToFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // XXX: NiceToHave: Vorschlag für Dateinamen setzen: <ContainerName>.xml
                String path = UIUtils.browseFile("container.export.file", ManageContainerDialog.this.getShell(), null, SWT.SAVE);
                if (path != null) {
                    if (ManageContainerDialog.this.table.getSelectionIndex() >= 0) {
                        ShortcutStore shortcutStore = Activator.getDefault().getShortcutStore();
                        try {
                            // Prüfen, ob die Datei bereits existiert, in diesem Fall Bestätigung abfragen
                            File f = new File(path);
                            if (f.exists() && f.isFile()) {
                                boolean doOverride = MessageDialog.openConfirm(ManageContainerDialog.this.getShell(), "File allready exists", "This file already exists, are you sure to overwrite it?");
                                if (!doOverride) {
                                    return;
                                }
                            }
                            ShortcutContainer from = shortcutStore.getContainers().get(ManageContainerDialog.this.table.getSelectionIndex());
                            ShortcutContainer to = shortcutStore.createNewContainer(new ShortcutFileDAO(path), "Copie of " + from.getName());
                            shortcutStore.copyShortcuts(from, to);
                            MessageDialog.openInformation(ManageContainerDialog.this.getShell(), "Export successful", "Container was successful exported to file:\n" + path);
                        } catch (DAOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        GridData gd_btnExportToFile = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnExportToFile.widthHint = 80;
        this.btnExportToFile.setLayoutData(gd_btnExportToFile);
        this.btnExportToFile.setText("Export to File");
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);

        this.adjustButtonsEnabledStatus();

        return container;
    }

    private void refreshContainerList() {
        // Content/LabelProvider definieren den sichtbaren Inhalt.
        this.checkboxTableViewer.setInput(Activator.getDefault().getShortcutStore().getContainers());
        this.checkboxTableViewer.refresh();
    }

    private void adjustButtonsEnabledStatus() {
        this.btnRemove.setEnabled(true);
        this.btnDelete.setEnabled(true);
        this.btnExportToFile.setEnabled(true);
        if (this.table.getSelectionIndex() >= 0) {
            ShortcutStore shortcutStore = Activator.getDefault().getShortcutStore();
            ShortcutContainer container = shortcutStore.getContainers().get(this.table.getSelectionIndex());
            if (shortcutStore.isDefault(container)) {
                this.btnRemove.setEnabled(false);
                this.btnDelete.setEnabled(false);
            }
            if (container.isReadOnly()) {
                this.btnDelete.setEnabled(false);
            }
        } else {
            this.btnRemove.setEnabled(false);
            this.btnDelete.setEnabled(false);
            this.btnExportToFile.setEnabled(false);
        }
    }

    /**
     * Create contents of the button bar.
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        this.createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(450, 336);
    }

}
