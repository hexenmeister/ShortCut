package de.as.eclipse.shortcut.ui;

import java.util.StringTokenizer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.InvertableSorter;
import org.nightlabs.base.ui.table.TableSortSelectionListener;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.business.Shortcut;

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
}
