package de.as.eclipse.shortcut.ui.views.dialog;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.business.Shortcut;
import de.as.eclipse.shortcut.persist.DAOException;
import de.as.eclipse.shortcut.persist.ShortcutContainer;
import de.as.eclipse.shortcut.persist.ShortcutStore;
import de.as.eclipse.shortcut.ui.UIConstants;
import de.as.eclipse.shortcut.ui.UIUtils;

public class ShortcutDialog extends TrayDialog {
    private Text fName;

    private Combo fPrio;

    private Combo fCategory1;

    private Combo fCategory2;

    private Shortcut shortcut = null;

    private Text fWorkDir;

    private StyledText fLocation;

    private StyledText fDescription;

    private Button btnGrabOutput;

    private ColorButton cColor;

    private Combo fContainer;

    private Combo fGroup;

    private List<ShortcutContainer> containerList;

    /**
     * @wbp.parser.constructor
     */
    public ShortcutDialog(Shell shell) {
        this(shell, null);
    }

    public ShortcutDialog(Shell shell, Shortcut shortcut) {
        super(shell);
        this.setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
        this.shortcut = shortcut;
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
        if (this.shortcut == null) {
            shell.setText("Add New Shortcut");
        } else {
            shell.setText("Edit Shortcut");
        }

        shell.setImage(Activator.getImage(UIConstants.ICON_SHORTCUT));
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite comp = (Composite) super.createDialogArea(parent);
        comp.setLayout(new GridLayout(4, false));

        Label lblContainer = new Label(comp, SWT.NONE);
        lblContainer.setText("Container:");

        this.fContainer = new Combo(comp, SWT.NONE | SWT.READ_ONLY);
        this.fContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Button btnManageContainers = new Button(comp, SWT.NONE);
        btnManageContainers.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Container-Verwaltung
                ManageContainerDialog mc = new ManageContainerDialog(ShortcutDialog.this.getShell());
                mc.open();
            }
        });
        GridData gd_btnManageContainers = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_btnManageContainers.widthHint = 60;
        btnManageContainers.setLayoutData(gd_btnManageContainers);
        btnManageContainers.setText("Manage...");

        this.containerList = Activator.getDefault().getShortcutStore().getChangeableContainers();
        for (ShortcutContainer shortcutContainer : this.containerList) {
            this.fContainer.add(shortcutContainer.getName());
        }
        if (this.shortcut == null) {
            // Wenn Neuanlage: erste auswählen
            this.fContainer.select(0);
            this.fContainer.setEnabled(true);
        } else {
            // Wenn Bearbeitung: entsprechenden Container auswählen
            ShortcutContainer container = Activator.getDefault().getShortcutStore().getParentContainer(this.shortcut);
            this.fContainer.select(this.containerList.indexOf(container));
            this.fContainer.setEnabled(false);
        }
        // Wenn nur ein Eintrag vorhanden: Combo auf 'nicht änderbar' setzen
        if (this.fContainer.getItemCount() == 1) {
            this.fContainer.setEnabled(false);
        }

        Label lblName = new Label(comp, 0);
        lblName.setText("Name:");

        this.fName = new Text(comp, SWT.BORDER);
        GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_txtName.widthHint = 150;
        this.fName.setLayoutData(gd_txtName);

        Label lblColor = new Label(comp, SWT.NONE);
        lblColor.setText("  Color:");

        this.cColor = new ColorButton(comp);
        GridData gd_cColor = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_cColor.heightHint = 22;
        gd_cColor.widthHint = 32;
        this.cColor.setLayoutData(gd_cColor);

        Label lblGroup = new Label(comp, SWT.NONE);
        lblGroup.setText("Group:");

        this.fGroup = new Combo(comp, SWT.NONE);
        this.fGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // TODO: Umbauen: lesen alle Vorhandene Varianten aus der ShortcutListe
        UIUtils.readHistoryItems("group", this.fGroup);
        this.fGroup.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                UIUtils.handleHistoryItems(ShortcutDialog.this.fGroup, 10);
                UIUtils.saveHistoryItems("group", ShortcutDialog.this.fGroup);
            }
        });

        Label lblPrio = new Label(comp, 0);
        lblPrio.setText("  Priority:");

        this.fPrio = new Combo(comp, SWT.NONE);
        this.fPrio.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        // TODO: Umbauen: lesen alle Vorhandene Varianten aus der ShortcutListe
        UIUtils.readHistoryItems("priority", this.fPrio);
        this.fPrio.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                UIUtils.handleHistoryItems(ShortcutDialog.this.fPrio, 10);
                UIUtils.saveHistoryItems("priority", ShortcutDialog.this.fPrio);
            }
        });
        this.fPrio.select(1);
        if (this.fPrio.getItemCount() == 0) {
            this.fPrio.add("High");
            this.fPrio.add("Medium");
            this.fPrio.add("Low");
        }

        Label lblCategory = new Label(comp, SWT.NONE);
        lblCategory.setText("Category 1:");

        this.fCategory1 = new Combo(comp, SWT.NONE);
        this.fCategory1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // TODO: Umbauen: lesen alle Vorhandene Varianten aus der ShortcutListe
        UIUtils.readHistoryItems("category", this.fCategory1);
        this.fCategory1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                UIUtils.handleHistoryItems(ShortcutDialog.this.fCategory1, 10);
                UIUtils.saveHistoryItems("category", ShortcutDialog.this.fCategory1);
            }
        });

        Label lblCategory2 = new Label(comp, SWT.NONE);
        lblCategory2.setText("  2:");

        this.fCategory2 = new Combo(comp, SWT.NONE);
        this.fCategory2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        // TODO: Umbauen: lesen alle Vorhandene Varianten aus der ShortcutListe
        UIUtils.readHistoryItems("category2", this.fCategory2);
        this.fCategory2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                UIUtils.handleHistoryItems(ShortcutDialog.this.fCategory2, 10);
                UIUtils.saveHistoryItems("category2", ShortcutDialog.this.fCategory2);
            }
        });

        Label lblMoreCmds = new Label(comp, SWT.WRAP);
        lblMoreCmds.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        lblMoreCmds.setText("Location /\r\nScript:");

        this.fLocation = new StyledText(comp, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData gd_fLocation = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd_fLocation.heightHint = 44;
        this.fLocation.setLayoutData(gd_fLocation);

        Button btnLocation = new Button(comp, SWT.NONE);
        btnLocation.setText(" Browse ...");
        GridData gd_btnLocation = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
        gd_btnLocation.widthHint = 60;
        btnLocation.setLayoutData(gd_btnLocation);
        btnLocation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selection = true;
                Point sel = ShortcutDialog.this.fLocation.getSelection();
                int pos1 = sel.x, pos2 = sel.y;
                String text = ShortcutDialog.this.fLocation.getSelectionText();
                String fullText = ShortcutDialog.this.fLocation.getText();
                if ((text.length() == 0) && (fullText.length() > 0)) {
                    // wenn nichts selektiert, jedoch Feld nicht leer => erste zusammenhängende Zeichenkette suchen
                    // (Anführungszeichen beachten)
                    pos1 = 0;
                    pos2 = -1;
                    while ((pos1 < fullText.length()) && Character.isSpaceChar(fullText.charAt(pos1))) {
                        pos1++;
                    }
                    // Wenn das gefundene Wort mit Anführingszeichen anfängt, dann sein dazu Paar suchen
                    // (Möglichkeit der mehrfachen ineinandergelegten Anführungszeichenpaare wird nicht berücksichtigt (wäre zu viel des Guten))
                    if ((fullText.charAt(pos1) == '\"') || (fullText.charAt(pos1) == '\'')) {
                        pos2 = fullText.indexOf(fullText.charAt(pos1), pos1 + 1);
                    }

                    // Prüfen, ob ein Anführungszeichen gefunden wurde
                    if (pos2 < 0) {
                        // Wenn nicht, dann Lerrzeichen suchen
                        pos2 = pos1 + 1;
                        while ((pos2 < fullText.length()) && !Character.isSpaceChar(fullText.charAt(pos2))) {
                            pos2++;
                        }
                    } else {
                        pos2++;
                    }
                    ShortcutDialog.this.fLocation.setSelection(pos1, pos2);
                    selection = false;
                    text = ShortcutDialog.this.fLocation.getSelectionText();
                } else {
                    // Selection ggf. korrigieren (Leerzeichen, Anführungszeichen)
                    // Leerzeichen ausschliessen, Anführungszeichen einschliessen
                    // linker Rand
                    if (((pos1 > 0) && (fullText.charAt(pos1) != '\"') && (fullText.charAt(pos1) != '\'')) && ((fullText.charAt(pos1 - 1) == '\"') || (fullText.charAt(pos1 - 1) == '\''))) {
                        pos1--;
                    }
                    while (((pos1 >= 0) && (pos1 < fullText.length())) && Character.isSpaceChar(fullText.charAt(pos1))) {
                        pos1++;
                    }

                    // rechter Rand
                    if (((pos2 < (fullText.length() - 1)) && (fullText.charAt(pos2 - 1) != '\"') && (fullText.charAt(pos2 - 1) != '\''))
                            && ((fullText.charAt(pos2) == '\"') || (fullText.charAt(pos2) == '\''))) {
                        pos2++;
                    }
                    while (((pos2 > 0) && (pos2 <= fullText.length())) && Character.isSpaceChar(fullText.charAt(pos2 - 1))) {
                        pos2--;
                    }

                    ShortcutDialog.this.fLocation.setSelection(pos1, pos2);
                    text = ShortcutDialog.this.fLocation.getSelectionText();
                }

                // Text aus der Box verwenden
                text = text.trim();
                // ggf. vorhandenen Anführungszeichen entfernen
                if ((text.startsWith("\"") || text.startsWith("'"))) {
                    text = text.substring(1, text.length());
                }
                if ((text.endsWith("\"") || text.endsWith("'"))) {
                    text = text.substring(0, text.length() - 1);
                }

                String path = UIUtils.browseFile(ShortcutDialog.this.getShell(), text, SWT.OPEN);
                if (path != null) {
                    path = UIUtils.substitureWorkspaceLocations(path);
                    // Pfade mit Leerzeichen in Anführungszeichen nehmen
                    if (path.indexOf(' ') >= 0) {
                        path = "\"" + path + "\"";
                    }
                    ShortcutDialog.this.fLocation.setText(fullText.substring(0, pos1) + path + fullText.substring(pos2));
                    if (selection) {
                        ShortcutDialog.this.fLocation.setSelection(pos1, pos1 + path.length());
                    }
                }
                if (!selection) {
                    ShortcutDialog.this.fLocation.setSelection(0, 0);
                }
            }
        });

        Label lblWorkDir = new Label(comp, SWT.NONE);
        lblWorkDir.setText("Work dir:");

        this.fWorkDir = new Text(comp, SWT.BORDER);
        this.fWorkDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Button btnWorkDir = new Button(comp, SWT.NONE);
        btnWorkDir.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Text aus der Box verwenden
                String path = UIUtils.browseLocation(ShortcutDialog.this.getShell(), ShortcutDialog.this.fWorkDir.getText());
                if (path != null) {
                    // Parentverzeichnis suchen
                    File f = new File(path);
                    if (f.isFile()) {
                        f = f.getParentFile();
                        path = f.getAbsolutePath();
                    }
                    path = UIUtils.substitureWorkspaceLocations(path);
                    ShortcutDialog.this.fWorkDir.setText(path);
                }
            }
        });
        GridData gd_btnWorkDir = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_btnWorkDir.widthHint = 60;
        btnWorkDir.setLayoutData(gd_btnWorkDir);
        btnWorkDir.setText("Browse...");

        Label lblDescription = new Label(comp, SWT.WRAP);
        lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
        lblDescription.setText("Description:");

        this.fDescription = new StyledText(comp, SWT.BORDER | SWT.WRAP);
        GridData gd_fDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gd_fDescription.heightHint = 22;
        this.fDescription.setLayoutData(gd_fDescription);
        new Label(comp, SWT.NONE);

        Composite composite = new Composite(comp, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

        this.btnGrabOutput = new Button(composite, SWT.CHECK);
        this.btnGrabOutput.setText("grab output");
        this.setHelpAvailable(false);

        if (this.shortcut != null) {
            this.applyValues(this.shortcut);
        }

        return comp;
    }

    private void applyValues(Shortcut shortcut) {
        this.fName.setText(shortcut.getName() != null ? shortcut.getName() : "");
        this.fGroup.setText(shortcut.getGroup() != null ? shortcut.getGroup() : "");
        this.fLocation.setText(shortcut.getPayload() != null ? shortcut.getPayload() : "");
        this.fCategory1.setText(shortcut.getCategory1() != null ? shortcut.getCategory1() : "");
        this.fCategory2.setText(shortcut.getCategory2() != null ? shortcut.getCategory2() : "");
        this.fPrio.setText(shortcut.getPriority() != null ? shortcut.getPriority() : "");
        this.fWorkDir.setText(shortcut.getWorkingDir() != null ? shortcut.getWorkingDir() : "");
        this.fDescription.setText(shortcut.getDescription() != null ? shortcut.getDescription() : "");
        //        this.fLocation.setText(shortcut.getMoreCommands() != null ? shortcut.getMoreCommands() : "");
        this.cColor.setRgb(shortcut.getRgb());
        this.btnGrabOutput.setSelection(shortcut.isGrabOutput());
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == 0) {
            if (this.fLocation.getText().trim().equals("")) {
                MessageDialog.openInformation(this.getShell(), "Missing Location", "Location should not be empty.");
                return;
            }

            ShortcutStore store = Activator.getDefault().getShortcutStore();

            // Containerwahl
            ShortcutContainer container = store.getContainers().get(this.fContainer.getSelectionIndex());
            Shortcut shortcut = this.shortcut != null ? this.shortcut : container.createNewShortcut();
            String name = this.fName.getText();

            if (name.equals("")) {
                name = this.fLocation.getText().substring(this.fLocation.getText().lastIndexOf("\\") + 1);
            }

            shortcut.setName(name.trim());
            shortcut.setPayload(this.fLocation.getText().trim());
            shortcut.setGroup(this.fGroup.getText());
            shortcut.setPriority(this.fPrio.getText());
            shortcut.setCategory1(this.fCategory1.getText());
            shortcut.setCategory2(this.fCategory2.getText());
            shortcut.setWorkingDir(this.fWorkDir.getText());
            shortcut.setDescription(this.fDescription.getText());
            //            shortcut.setMoreCommands(this.fLocation.getText());
            shortcut.setRgb(this.cColor.getRgb());
            shortcut.setGrabOutput(this.btnGrabOutput.getSelection());

            try {
                if (this.shortcut == null) {
                    container.addShortcut(shortcut);
                } else {
                    container.updateShortcut(shortcut);
                }
            } catch (DAOException e) {
                // TODO Meldung anzeigen
                e.printStackTrace();
            }
            this.close();
        } else {
            this.close();
        }
    }

    private static class ColorButton extends Composite implements PaintListener {
        private String rgb;

        private Button button;

        // Subclasses sind im SWT nicht empfohlen, daher wird die klasse Button hier gewrapt (incl. Spa� mit Layout).
        public ColorButton(Composite parent) {
            super(parent, SWT.NONE);
            this.button = new Button(this, SWT.BORDER);
            GridLayout gl = new GridLayout();
            gl.marginWidth = 0;
            gl.marginHeight = 0;
            this.setLayout(gl);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            this.button.setLayoutData(gd);
            this.button.addPaintListener(this);
            this.button.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    ColorDialog cd = new ColorDialog(ColorButton.this.getShell());
                    cd.setText("Shortcut Color");
                    cd.setRGB(UIUtils.decodeColor(ColorButton.this.getRgb()));
                    RGB newColor = cd.open();
                    if (newColor != null) {
                        int irgb = (newColor.red << 16) + (newColor.green << 8) + (newColor.blue);
                        String color = '#' + Integer.toHexString(irgb);
                        ColorButton.this.setRgb(color);
                        ColorButton.this.button.redraw();
                    }
                }
            });
        }

        public String getRgb() {
            return this.rgb;
        }

        public void setRgb(String rgb) {
            this.rgb = rgb;
        }

        @Override
        public void paintControl(PaintEvent e) {
            RGB rgb = UIUtils.decodeColor(this.getRgb());

            Color color = new Color(e.display, rgb);
            e.gc.setBackground(color);
            e.gc.fillRectangle(2, 2, this.getBounds().width - 9, this.getBounds().height - 9);

            color.dispose();
            e.gc.dispose();
        }

    }

}
