package de.as.eclipse.shortcut.ui.views;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;

public class ShortcutsStyledLabelProvider extends StyledCellLabelProvider {

    private ShortcutsLabelProvider labelProvider;

    private ShortcutsFilter filter;

    public ShortcutsStyledLabelProvider(ShortcutsLabelProvider labelProvider, ShortcutsFilter filter) {
        super();
        this.labelProvider = labelProvider;
        this.filter = filter;
    }

    @Override
    public void update(ViewerCell cell) {
        int columnIndex = cell.getColumnIndex();
        cell.setImage(this.labelProvider.getColumnImage(cell.getElement(), columnIndex));

        String text = this.labelProvider.getColumnText(cell.getElement(), columnIndex);
        String filterString = this.filter.getSearchString();

        cell.setText(text);
        cell.setStyleRanges(null);

        if ((text != null) && this.isColumnRelevant(columnIndex)) {
            if ((filterString != null) && (filterString.length() > 0)) {
                Styler style = null; //fBoldStyler;
                StyledString styledString = new StyledString(text, style);

                String textLC = text.toLowerCase();
                int len = filterString.length();
                int pos = -1;
                while ((pos = textLC.indexOf(filterString, pos)) >= 0) {
                    styledString.setStyle(pos, len, StyledString.COUNTER_STYLER);
                    pos += len;
                }

                cell.setStyleRanges(styledString.getStyleRanges());

                //        String decoration = MessageFormat.format(" ({0} bytes)", new Object[] { new Long(file.length()) }); //$NON-NLS-1$
                //        styledString.append(decoration, StyledString.COUNTER_STYLER);
            }
        }

        super.update(cell);
    }

    private boolean isColumnRelevant(int columnIndex) {
        boolean useName = this.filter.isUseName();
        boolean useCategory = this.filter.isUseCategory();
        boolean useLocation = this.filter.isUseLocation();

        if (useName && (columnIndex == ShortcutsLabelProvider.COL_INDEX_NAME)) {
            return true;
        }
        if (useCategory) {
            if (columnIndex == ShortcutsLabelProvider.COL_INDEX_CATEGORY1) {
                return true;
            }
            if (columnIndex == ShortcutsLabelProvider.COL_INDEX_CATEGORY2) {
                return true;
            }
        }
        if (useLocation && (columnIndex == ShortcutsLabelProvider.COL_INDEX_PAYLOAD)) {
            return true;
        }

        return false;
    }

}
