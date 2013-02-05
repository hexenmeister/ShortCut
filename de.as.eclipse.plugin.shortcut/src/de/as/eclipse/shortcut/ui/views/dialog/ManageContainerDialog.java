package de.as.eclipse.shortcut.ui.views.dialog;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

    private Button btnRename;

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

        //        this.table.setLinesVisible(true);

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

        //        this.checkboxTableViewer.setLabelProvider(new ITableLabelProvider() {
        //            // TODO : change anonym classes to nested
        //            @Override
        //            public void removeListener(ILabelProviderListener arg0) {
        //            }
        //
        //            @Override
        //            public boolean isLabelProperty(Object arg0, String arg1) {
        //                return false;
        //            }
        //
        //            @Override
        //            public void dispose() {
        //            }
        //
        //            @Override
        //            public void addListener(ILabelProviderListener arg0) {
        //            }
        //
        //            @Override
        //            public String getColumnText(Object arg0, int arg1) {
        //                ShortcutContainer sc = (ShortcutContainer) arg0;
        //                String colText = sc.getName();
        //                ShortcutStore shortcutStore = ManageContainerDialog.this.getShortcutStore();
        //                if (shortcutStore.isDefault(sc)) {
        //                    colText += " (default)";
        //                }
        //                if (sc.isReadOnly()) {
        //                    colText += " (read only)";
        //                }
        //                return colText;
        //            }
        //
        //            @Override
        //            public Image getColumnImage(Object arg0, int arg1) {
        //                return null;
        //            }
        //        });

        ColumnViewerToolTipSupport.enableFor(this.checkboxTableViewer, ToolTip.NO_RECREATE);
        this.checkboxTableViewer.setLabelProvider(new StyledCellLabelProvider() {
            // TODO : change anonym classes to nested
            @Override
            public String getToolTipText(Object element) {
                ShortcutContainer sc = (ShortcutContainer) element;
                return sc.getDescription();
            }

            @Override
            public Point getToolTipShift(Object object) {
                return new Point(5, 5);
            }

            @Override
            public int getToolTipDisplayDelayTime(Object object) {
                return 2000;
            }

            @Override
            public int getToolTipTimeDisplayed(Object object) {
                return 50000;
            }

            @Override
            public void update(ViewerCell cell) {
                ShortcutContainer sc = (ShortcutContainer) cell.getElement();
                String colText = sc.getName();
                int len = colText.length();

                ShortcutStore shortcutStore = ManageContainerDialog.this.getShortcutStore();
                if (shortcutStore.isDefault(sc)) {
                    colText += " (default)";
                }
                if (sc.isReadOnly()) {
                    colText += " (read only)";
                }
                cell.setText(colText);

                Styler style = null;
                StyledString styledString = new StyledString(colText, style);
                styledString.setStyle(len, colText.length() - len, StyledString.QUALIFIER_STYLER);
                cell.setStyleRanges(styledString.getStyleRanges());

                //                cell.setImage(image);

                super.update(cell);
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

                ContainerDialog add = new ContainerDialog(ManageContainerDialog.this.getShell());
                int res = add.open();
                if (res == Window.OK) {
                    ManageContainerDialog.this.refreshContainerList();
                }
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
                if (ManageContainerDialog.this.table.getSelectionIndex() >= 0) {
                    ShortcutStore shortcutStore = ManageContainerDialog.this.getShortcutStore();
                    ShortcutContainer container = shortcutStore.getContainers().get(ManageContainerDialog.this.table.getSelectionIndex());
                    if (!shortcutStore.isDefault(container)) {
                        boolean doDelete = MessageDialog.openConfirm(ManageContainerDialog.this.getShell(), "Remove", "Do you want to remove container '" + container.getName() + "'?");
                        if (doDelete) {
                            shortcutStore.removeContainer(container);
                            ManageContainerDialog.this.refreshContainerList();
                        }
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

        this.btnRename = new Button(container, SWT.NONE);
        this.btnRename.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (ManageContainerDialog.this.table.getSelectionIndex() >= 0) {
                    ShortcutStore shortcutStore = ManageContainerDialog.this.getShortcutStore();
                    ShortcutContainer container = shortcutStore.getContainers().get(ManageContainerDialog.this.table.getSelectionIndex());
                    String oldName = container.getName();
                    InputDialog input = new InputDialog(ManageContainerDialog.this.getShell(), "Rename container", "Please enter new container name", oldName, new ContainerNameValidator(2, 70));
                    if (input.open() == Window.OK) {
                        try {
                            String newName = input.getValue();
                            if (!newName.equals(oldName)) {
                                container.rename(newName);
                                ManageContainerDialog.this.checkboxTableViewer.refresh();
                            }
                        } catch (DAOException e1) {
                            // TODO log
                            MessageDialog.openError(ManageContainerDialog.this.getShell(), "Rename failed", "Container rename was not successful.");
                        }
                    }
                }
            }

            /**
             * Prüft den eingegebenen Container-Name auf seine Gültigkein.
             */
            final class ContainerNameValidator implements IInputValidator {
                private int minLength = 0;

                private int maxLength = 100;

                public ContainerNameValidator(int minLength, int maxLength) {
                    this.minLength = minLength;
                    this.maxLength = maxLength;
                }

                /**
                 * Prüft die eingegebene Zeichenkette. Liefert null für gültig oder Fehlermeldung anderfalls.
                 * 
                 * @param text zu validierende Zeichenkette
                 * @return String als Fehlermeldung oder null für OK
                 */
                public String isValid(String text) {
                    int len = text.length();

                    // Determine if input is too short or too long
                    if (len < this.minLength) {
                        return "The new container name is too short";
                    }
                    if (len > this.maxLength) {
                        return "The new container name is too long";
                    }

                    // Input must be OK
                    return null;
                }
            }

        });
        GridData gd_btnRename = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnRename.widthHint = 80;
        this.btnRename.setLayoutData(gd_btnRename);
        this.btnRename.setText("Rename");

        Button btnImport = new Button(container, SWT.NONE);
        btnImport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String path = UIUtils.browseFile("container.import.file", ManageContainerDialog.this.getShell(), null, SWT.OPEN);
                if (path != null) {
                    ShortcutStore shortcutStore = ManageContainerDialog.this.getShortcutStore();
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
                        ShortcutStore shortcutStore = ManageContainerDialog.this.getShortcutStore();
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
        this.checkboxTableViewer.setInput(this.getShortcutStore().getContainers());
        this.checkboxTableViewer.refresh();
    }

    private void adjustButtonsEnabledStatus() {
        this.btnRemove.setEnabled(true);
        this.btnRename.setEnabled(true);
        this.btnExportToFile.setEnabled(true);
        if (this.table.getSelectionIndex() >= 0) {
            ShortcutStore shortcutStore = this.getShortcutStore();
            ShortcutContainer container = shortcutStore.getContainers().get(this.table.getSelectionIndex());
            if (shortcutStore.isDefault(container)) {
                this.btnRemove.setEnabled(false);
                this.btnRename.setEnabled(false);
            }
            if (container.isReadOnly()) {
                this.btnRename.setEnabled(false);
            }
        } else {
            this.btnRemove.setEnabled(false);
            this.btnRename.setEnabled(false);
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

    /**
     * Liefert die verwendete ShortcutStore.
     * @return Store
     */
    private ShortcutStore getShortcutStore() {
        return Activator.getDefault().getShortcutStore();
    }
}
