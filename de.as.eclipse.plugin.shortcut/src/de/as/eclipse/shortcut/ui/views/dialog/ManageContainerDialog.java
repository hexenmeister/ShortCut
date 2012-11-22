package de.as.eclipse.shortcut.ui.views.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import de.as.eclipse.shortcut.persist.ShortcutContainer;
import de.as.eclipse.shortcut.persist.ShortcutFileDAO;
import de.as.eclipse.shortcut.persist.ShortcutStore;

public class ManageContainerDialog extends TrayDialog {
    private Table table;

    /**
     * Create the dialog.
     * @param parentShell
     */
    public ManageContainerDialog(Shell parentShell) {
        super(parentShell);
        this.setHelpAvailable(false);
        this.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
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
        CheckboxTableViewer checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
        this.table = checkboxTableViewer.getTable();
        this.table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 7));

        checkboxTableViewer.setContentProvider(new IStructuredContentProvider() {
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

        checkboxTableViewer.setLabelProvider(new ITableLabelProvider() {
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

        checkboxTableViewer.setCheckStateProvider(new ICheckStateProvider() {
            @Override
            public boolean isGrayed(Object arg0) {
                return false;
            }

            @Override
            public boolean isChecked(Object arg0) {
                ShortcutContainer sc = (ShortcutContainer) arg0;
                return sc.isVisible();
            }
        });

        checkboxTableViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                ShortcutContainer sc = (ShortcutContainer) event.getElement();
                sc.setVisible(event.getChecked());
            }
        });

        // It egal was, Hauptsache nicht null, da sonst nichts angezeigt wird
        // (Content/LabelProvider definieren den sichtbaren Inhalt).
        checkboxTableViewer.setInput(Activator.getDefault().getShortcutStore().getContainers());

        Button btnCreate = new Button(container, SWT.NONE);
        GridData gd_btnCreate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnCreate.widthHint = 80;
        btnCreate.setLayoutData(gd_btnCreate);
        btnCreate.setText("Create");

        Button btnRemove = new Button(container, SWT.NONE);
        GridData gd_btnRemove = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnRemove.widthHint = 80;
        btnRemove.setLayoutData(gd_btnRemove);
        btnRemove.setText("Remove");

        Button btnDelete = new Button(container, SWT.NONE);
        GridData gd_btnDelete = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnDelete.widthHint = 80;
        btnDelete.setLayoutData(gd_btnDelete);
        btnDelete.setText("Delete");

        Button btnImport = new Button(container, SWT.NONE);
        GridData gd_btnImport = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnImport.widthHint = 80;
        btnImport.setLayoutData(gd_btnImport);
        btnImport.setText("Import");

        Button btnExportToFile = new Button(container, SWT.NONE);
        btnExportToFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //TODO: File Dialog
                if (ManageContainerDialog.this.table.getSelectionIndex() >= 0) {
                    ShortcutStore shortcutStore = Activator.getDefault().getShortcutStore();
                    try {
                        ShortcutContainer from = shortcutStore.getContainers().get(ManageContainerDialog.this.table.getSelectionIndex());
                        ShortcutContainer to = shortcutStore.createNewContainer(new ShortcutFileDAO("d:\\test.xml"), "Kopie");
                        shortcutStore.copyShortcuts(from, to);
                    } catch (DAOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        GridData gd_btnExportToFile = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnExportToFile.widthHint = 80;
        btnExportToFile.setLayoutData(gd_btnExportToFile);
        btnExportToFile.setText("Export to File");
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);

        return container;
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
