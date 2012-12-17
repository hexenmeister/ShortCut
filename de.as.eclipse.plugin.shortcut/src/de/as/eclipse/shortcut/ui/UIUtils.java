package de.as.eclipse.shortcut.ui;

import java.io.File;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.InvertableSorter;
import org.nightlabs.base.ui.table.TableSortSelectionListener;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.business.Shortcut;

/**
 * Allgemeingebräuchliche UI-Utilities.
 *
 * @author Alexander Schulz
 * Date: 17.12.2012
 */
public class UIUtils {

    // convenience methods

    public static TableSortSelectionListener createTableColumn(TableViewer viewer, String text, String tooltip, InvertableSorter<Shortcut> sorter, int style, int initialDirection,
            boolean keepDirection) {
        TableColumn column = new TableColumn(viewer.getTable(), style);
        column.setText(text);
        column.setToolTipText(tooltip);
        column.setResizable(true);
        column.setMoveable(true);
        return new TableSortSelectionListener(viewer, column, sorter, initialDirection, keepDirection);
    }

    public static void handleHistoryItems(Combo combo, int maxItem) {
        String text = combo.getText();
        String[] items = combo.getItems();
        boolean found = false;
        for (String string : items) {
            if (string.equalsIgnoreCase(text)) {
                found = true;
                break;
            }
        }
        if (!found) {
            combo.add(text, 0);
            while (combo.getItemCount() > maxItem) {
                combo.remove(maxItem);
            }
        }
    }

    public static void saveHistoryItems(String comboName, Combo combo) {
        StringBuilder itemsStr = new StringBuilder();
        String items[] = combo.getItems();
        for (int i = 0, n = items.length; i < n; i++) {
            String itemStr = items[i];
            itemsStr.append(itemStr);
            if (i < (n - 1)) {
                itemsStr.append(",");
            }
        }

        Activator.getDefault().getPreferenceStore().setValue("history.items.list." + comboName, itemsStr.toString());
    }

    public static void readHistoryItems(String comboName, Combo combo) {
        String itemsStr = Activator.getDefault().getPreferenceStore().getString("history.items.list." + comboName);
        combo.removeAll();
        if (itemsStr != null) {
            for (StringTokenizer st = new StringTokenizer(itemsStr, ","); st.hasMoreElements();) {
                combo.add(st.nextToken());
            }
        }
    }

    /**
     * Erstellt eine der Eingabe entsprechende Instanz der SWT-Klasse RGB.
     * @param rgb String in Form '#FFFFFF'. Falls null, wird default-Wert zurückgegeben (s. UIConstants).
     * @return RGB
     */
    public static RGB decodeColor(String rgb) {
        if (rgb == null) {
            rgb = UIConstants.DEFAULT_SHORTCUT_RGB;
        }

        int i;
        try {
            i = Integer.decode(rgb);
        } catch (NumberFormatException e) {
            i = Integer.decode(UIConstants.DEFAULT_SHORTCUT_RGB);
        }

        int r = (i >> 16) & 0xFF, g = (i >> 8) & 0xFF, b = i & 0xFF;

        return new RGB(r, g, b);
    }

    /**
     * Öffnet ein Benutzerdialog zur Auswahl von Dateien/Verzeichnisen.
     * @param shell Parent-Shell
     * @param path Initial-Pfad (Vorbelegung)
     * @param selectFile true, wenn eine Datei ausgewählt werden soll, falsch, wenn ein Verzeichniss.
     * @return ausgewählter Pfad, null beim Abbruch
     */
    public static String browseLocation(Shell shell, String path, boolean selectFile) {
        // Variablen auflösen
        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
        try {
            path = variableManager.performStringSubstitution(path, false);
        } catch (CoreException e1) {
            // ignore
        }

        // Prüfen, ob es eine Datei/Verzeichnis ist
        File f = new File(path);
        File orig = f;
        // ggf. Probieren in der Hierarchie ein existierendes Verzeichnis zu finden
        while ((f != null) && !f.exists()) {
            f = f.getParentFile();
        }
        // Prüfen, ob etwas gefunden wurde und ggf. das letze Verzeichnis nehmen
        if (f != null) {
            if (f.isFile()) {
                f = f.getParentFile();
            }
        }

        if (selectFile) {
            FileDialog dialog = new FileDialog(shell);

            // Zweite Prüfung wegen getParent vorher notwendig
            if (f != null) {
                dialog.setFilterPath(f.getAbsolutePath());
            }

            // Name der Datei vorbelegen
            if ((orig != null) && (f != null) && !f.getAbsoluteFile().equals(orig.getAbsoluteFile())) {
                dialog.setFileName(orig.getName());
            }

            String ret = dialog.open();
            return ret;
        } else {
            DirectoryDialog dialog = new DirectoryDialog(shell);

            // Zweite Prüfung wegen getParent vorher notwendig
            if (f != null) {
                dialog.setFilterPath(f.getAbsolutePath());
            }
            String ret = dialog.open();
            return ret;
        }
    }
}
