package de.as.eclipse.shortcut.ui.views.dialog;

import java.io.File;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.persist.ShortcutContainer;
import de.as.eclipse.shortcut.ui.UIConstants;
import de.as.eclipse.shortcut.ui.UIUtils;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ContainerDialog extends TrayDialog {

    private ShortcutContainer container = null;

    private Text fName;

    private Text fFile;

    private StyledText fDescription;

    private Button btnRestrictedAccess;

    private Combo fType;

    public ContainerDialog(Shell shell) {
        this(shell, null);
    }

    public ContainerDialog(Shell shell, ShortcutContainer container) {
        super(shell);
        this.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
        this.container = container;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);

        // Wunschgröße
        int w = 600, h = 400;
        // In der Mitte des Parent-Fensters ausrichten
        Rectangle r = shell.getParent().getBounds();
        shell.setBounds(r.x + ((r.width - w) / 2), r.y + ((r.height - h) / 2), w, h);

        // Fenster-Titel setzen (je nach dem, ob neu oder edit)
        if (this.container == null) {
            shell.setText("Create new Container");
        } else {
            shell.setText("Edit Container Description");
        }

        shell.setImage(Activator.getImage(UIConstants.ICON_CONTAINERS)); // TODO
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite comp = (Composite) super.createDialogArea(parent);
        comp.setLayout(new GridLayout(4, false));

        Label lblType = new Label(comp, SWT.NONE);
        lblType.setText("Type:");

        this.fType = new Combo(comp, SWT.NONE | SWT.READ_ONLY);
        this.fType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        // Wenn nur ein Eintrag vorhanden: Combo auf 'nicht änderbar' setzen
        if (this.fType.getItemCount() == 1) {
            this.fType.setEnabled(false);
        }

        Label lblName = new Label(comp, 0);
        lblName.setText("Name:");

        this.fName = new Text(comp, SWT.BORDER);
        GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        gd_txtName.widthHint = 150;
        this.fName.setLayoutData(gd_txtName);

        Label lblDescription = new Label(comp, SWT.WRAP);
        lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        lblDescription.setText("Description:");

        this.fDescription = new StyledText(comp, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData gd_fDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        gd_fDescription.heightHint = 44;
        this.fDescription.setLayoutData(gd_fDescription);

        Label lblFile = new Label(comp, SWT.NONE);
        lblFile.setText("File:");

        this.fFile = new Text(comp, SWT.BORDER);
        this.fFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Button btnFile = new Button(comp, SWT.NONE);
        btnFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Text aus der Box verwenden
                String path = UIUtils.browseLocation(ContainerDialog.this.getShell(), ContainerDialog.this.fFile.getText());
                if (path != null) {
                    // Parentverzeichnis suchen
                    File f = new File(path);
                    if (f.isFile()) {
                        f = f.getParentFile();
                        path = f.getAbsolutePath();
                    }
                    path = UIUtils.substitureWorkspaceLocations(path);
                    ContainerDialog.this.fFile.setText(path);
                }
            }
        });
        GridData gd_btnFile = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_btnFile.widthHint = 60;
        btnFile.setLayoutData(gd_btnFile);
        btnFile.setText("Browse...");

        Composite composite = new Composite(comp, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

        this.btnRestrictedAccess = new Button(composite, SWT.CHECK);
        this.btnRestrictedAccess.setText("restricted write access");
        this.setHelpAvailable(false);

        if (this.container != null) {
            this.applyValues(this.container);
        }

        return comp;
    }

    private void applyValues(ShortcutContainer container) {
        // TODO
    }

    @Override
    protected void buttonPressed(int buttonId) {
        this.setReturnCode(buttonId); // return code je nach button
        if (buttonId == 0) {
            // Create Container
            // TODO Create / Edit Container
            this.close();
        } else {
            this.close();
        }

    }
}
