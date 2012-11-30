package de.as.eclipse.shortcut.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.nightlabs.base.ui.table.AbstractInvertableTableSorter;

import de.as.eclipse.shortcut.business.Shortcut;

public class ShortcutSorterFactory {

    private static abstract class ShortcutSorterBase<T extends Comparable<T>> extends AbstractInvertableTableSorter<Shortcut> {
        @Override
        protected int compareTyped(Viewer viewer, Shortcut e1, Shortcut e2) {
            T t1 = this.getComparisonProperty(e1);
            T t2 = this.getComparisonProperty(e2);

            if (t1 == t2) {
                return 0;
            }
            if (t1 == null) {
                return -1;
            }
            if (t2 == null) {
                return 1;
            }

            return t1.compareTo(t2);
        }

        protected abstract T getComparisonProperty(Shortcut shortcut);

    }

    public static final ShortcutSorterBase<Long> SIZE_SORTER = new ShortcutSorterBase<Long>() {
        @Override
        protected Long getComparisonProperty(Shortcut shortcut) {
            return shortcut.getSize();
        }
    };

    public static final ShortcutSorterBase<Long> LASTMODIFIED_SORTER = new ShortcutSorterBase<Long>() {
        @Override
        protected Long getComparisonProperty(Shortcut shortcut) {
            return shortcut.getLastModified();
        }
    };

    public static final ShortcutSorterBase<String> NAME_SORTER = new ShortcutSorterBase<String>() {
        @Override
        protected String getComparisonProperty(Shortcut shortcut) {
            return shortcut.getName();
        }
    };

    public static final ShortcutSorterBase<String> GROUP_SORTER = new ShortcutSorterBase<String>() {
        @Override
        protected String getComparisonProperty(Shortcut shortcut) {
            return shortcut.getGroup();
        }
    };

    public static final ShortcutSorterBase<String> CATEGORY1_SORTER = new ShortcutSorterBase<String>() {
        @Override
        protected String getComparisonProperty(Shortcut shortcut) {
            return shortcut.getCategory1();
        }
    };

    public static final ShortcutSorterBase<String> CATEGORY2_SORTER = new ShortcutSorterBase<String>() {
        @Override
        protected String getComparisonProperty(Shortcut shortcut) {
            return shortcut.getCategory2();
        }
    };

    public static final ShortcutSorterBase<String> PAYLOAD_SORTER = new ShortcutSorterBase<String>() {
        @Override
        protected String getComparisonProperty(Shortcut shortcut) {
            return shortcut.getPayload();
        }
    };

    //    public static final ShortcutSorterBase<String> MCMDS_SORTER = new ShortcutSorterBase<String>() {
    //        @Override
    //        protected String getComparisonProperty(Shortcut shortcut) {
    //            return shortcut.getMoreCommands();
    //        }
    //    };

    public static final ShortcutSorterBase<String> WORKDIR_SORTER = new ShortcutSorterBase<String>() {
        @Override
        protected String getComparisonProperty(Shortcut shortcut) {
            return shortcut.getWorkingDir();
        }
    };

    public static final ShortcutSorterBase<String> PRIORITY_SORTER = new ShortcutSorterBase<String>() {
        @Override
        protected String getComparisonProperty(Shortcut shortcut) {
            return shortcut.getPriority();
        }
    };

}
