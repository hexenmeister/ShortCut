package de.as.eclipse.shortcut.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.as.eclipse.shortcut.business.Shortcut;

public class ShortcutsFilter extends ViewerFilter {

    // TODO: Filtering for WorkingDir und Parameters (auch in View und in ShortcutsStyledLabelProvider!)

    private String searchString;

    private boolean useName = true;

    private boolean useCategory = true;

    private boolean useLocation = true;

    public boolean isUseName() {
        return this.useName;
    }

    public boolean isUseCategory() {
        return this.useCategory;
    }

    public boolean isUseLocation() {
        return this.useLocation;
    }

    public String getSearchString() {
        return this.searchString;
    }

    public void setSearchString(String s) {
        this.searchString = s.toLowerCase();
    }

    public void defineSearchFilds(boolean useName, boolean useCategory, boolean useLocation) {
        this.useName = useName;
        this.useCategory = useCategory;
        this.useLocation = useLocation;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if ((this.searchString == null) || (this.searchString.length() == 0)) {
            return true;
        }
        Shortcut shortcut = (Shortcut) element;

        if (this.useName && (shortcut.getName() != null)) {
            if (shortcut.getName().toLowerCase().contains(this.searchString)) {
                return true;
            }
        }

        if (this.useCategory) {
            if (shortcut.getCategory1() != null) {
                if (shortcut.getCategory1().toLowerCase().contains(this.searchString)) {
                    return true;
                }
            }
            if (shortcut.getCategory1() != null) {
                if (shortcut.getCategory1().toLowerCase().contains(this.searchString)) {
                    return true;
                }
            }
        }

        if (this.useLocation && (shortcut.getPayload() != null)) {
            if (shortcut.getPayload().toLowerCase().contains(this.searchString)) {
                return true;
            }
        }

        return false;
    }
}
