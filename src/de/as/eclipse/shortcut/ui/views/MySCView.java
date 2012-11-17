package de.as.eclipse.shortcut.ui.views;


import java.text.Collator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.ui.table.AbstractInvertableTableSorter;
import org.nightlabs.base.ui.table.InvertableSorter;
import org.nightlabs.base.ui.table.TableSortSelectionListener;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.business.Shortcut;
/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class MySCView extends ViewPart {

    private static final String NAME_COLUMN = "Name";

    private static final String PRIORITY_COLUMN = "Priority";

    private static final String LOCATION_COLUMN = "Location";

    private static final String SIZE_COLUMN = "Size (bytes)";

    private static final String LAST_MODIFIED_COLUMN = "Last Modified";

    public FontData FONT_ARIAL_8 = new FontData("Arial", 8, 0);

    private String[] columnNames = {MySCView.NAME_COLUMN, MySCView.PRIORITY_COLUMN, MySCView.SIZE_COLUMN, MySCView.LOCATION_COLUMN, MySCView.LAST_MODIFIED_COLUMN};

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "de.as.eclipse.shortcut.views.MySCView";

    private TableViewer viewer;
    private Action action1;
    private Action action2;
    private Action doubleClickAction;

    /*
     * The content provider class is responsible for
     * providing objects to the view. It can wrap
     * existing objects in adapters or simply return
     * objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore
     * it and always show the same content
     * (like Task List, for example).
     */

    class ViewContentProvider implements IStructuredContentProvider {
        @Override
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }
        @Override
        public void dispose() {
        }
        @Override
        public Object[] getElements(Object parent) {
            List<Shortcut> shortcuts = Activator.getDefault().getShortcutStore().getVisibleShortcuts();
            return shortcuts.toArray();
        }
    }
    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(Object obj, int index) {
            return this.getText(obj);
        }
        @Override
        public Image getColumnImage(Object obj, int index) {
            return this.getImage(obj);
        }
        @Override
        public Image getImage(Object obj) {
            return PlatformUI.getWorkbench().
                    getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }
    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public MySCView() {
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        this.viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        this.viewer.getTable().setLinesVisible(true);
        this.viewer.getTable().setHeaderVisible(true);
        this.viewer.setUseHashlookup(true);
        this.viewer.setColumnProperties(this.columnNames);

        (new TableColumn(this.viewer.getTable(), SWT.EMBEDDED, 0)).setWidth(10);
        this.createTableColumn(this.viewer, MySCView.NAME_COLUMN, MySCView.NAME_COLUMN, new TextSorter(), SWT.LEFT, SWT.UP, true);
        this.createTableColumn(this.viewer, MySCView.PRIORITY_COLUMN, MySCView.PRIORITY_COLUMN, new TextSorter(), SWT.LEFT, SWT.UP, true).chooseColumnForSorting();
        this.createTableColumn(this.viewer, MySCView.LOCATION_COLUMN, MySCView.LOCATION_COLUMN, new TextSorter(), SWT.LEFT, SWT.UP, true);
        this.createTableColumn(this.viewer, MySCView.SIZE_COLUMN, MySCView.SIZE_COLUMN, new TextSorter(), SWT.LEFT, SWT.UP, true);
        this.createTableColumn(this.viewer, MySCView.LAST_MODIFIED_COLUMN, MySCView.LAST_MODIFIED_COLUMN, new TextSorter(), SWT.LEFT, SWT.UP, true);

        // TableColumn column = new TableColumn(this.viewer.getTable(), SWT.EMBEDDED, 0);
        // column = new TableColumn(this.viewer.getTable(), SWT.LEFT, 1);
        // column.setText(NAME_COLUMN);
        // column = new TableColumn(this.viewer.getTable(), SWT.LEFT, 2);
        // column.setText(PRIORITY_COLUMN);
        // this.viewer.getTable().setSortColumn(column);
        // column = new TableColumn(this.viewer.getTable(), SWT.LEFT, 3);
        // column.setText(LOCATION_COLUMN);
        // column.setWidth(320);
        // column = new TableColumn(this.viewer.getTable(), SWT.LEFT, 4);
        // column.setText(SIZE_COLUMN);
        // column = new TableColumn(this.viewer.getTable(), SWT.LEFT, 5);
        // column.setText(LAST_MODIFIED_COLUMN);

        this.viewer.getTable().setFont(new Font(this.viewer.getTable().getDisplay(), this.FONT_ARIAL_8));

        GridData gridData = new GridData(GridData.FILL_BOTH); // GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_END | GridData.END
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        this.viewer.getTable().setLayoutData(gridData);


        this.viewer.setContentProvider(new ViewContentProvider());
        this.viewer.setLabelProvider(new ShortcutsLabelProvider());
        this.viewer.setSorter(new TextSorter());
        this.viewer.setInput(this.getViewSite());


        this.packColumns();

        this.makeActions();
        this.hookContextMenu();
        this.hookDoubleClickAction();
        this.contributeToActionBars();
    }

    private void packColumns() {
        this.viewer.getTable().getColumn(0).pack();
        this.viewer.getTable().getColumn(1).pack();
        this.viewer.getTable().getColumn(2).pack();
        this.viewer.getTable().getColumn(3).pack();
        this.viewer.getTable().getColumn(4).pack();
        // this.viewer.getTable().getColumn(5).pack();
    }

    public TableSortSelectionListener createTableColumn(TableViewer viewer, String text, String tooltip, InvertableSorter<Shortcut> sorter, int style, int initialDirection, boolean keepDirection) {
        TableColumn column = new TableColumn(viewer.getTable(), style);
        column.setText(text);
        column.setToolTipText(tooltip);
        return new TableSortSelectionListener(viewer, column, sorter, initialDirection, keepDirection);
    }

    private class TextSorter extends AbstractInvertableTableSorter<Shortcut> /* AbstractDemoSorter */{

        @Override
        protected int compareTyped(Viewer v, Shortcut d1, Shortcut d2) {
            return Collator.getInstance().compare(d1.getPriority(), d2.getPriority());
        }

    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                MySCView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
        this.viewer.getControl().setMenu(menu);
        this.getSite().registerContextMenu(menuMgr, this.viewer);
    }

    private void contributeToActionBars() {
        IActionBars bars = this.getViewSite().getActionBars();
        this.fillLocalPullDown(bars.getMenuManager());
        this.fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(this.action1);
        manager.add(new Separator());
        manager.add(this.action2);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(this.action1);
        manager.add(this.action2);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(this.action1);
        manager.add(this.action2);
    }

    private void makeActions() {
        this.action1 = new Action() {
            @Override
            public void run() {
                MySCView.this.showMessage("Action 1 executed");
            }
        };
        this.action1.setText("Action 1");
        this.action1.setToolTipText("Action 1 tooltip");
        this.action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        this.action2 = new Action() {
            @Override
            public void run() {
                MySCView.this.showMessage("Action 2 executed");
            }
        };
        this.action2.setText("Action 2");
        this.action2.setToolTipText("Action 2 tooltip");
        this.action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        this.doubleClickAction = new Action() {
            @Override
            public void run() {
                ISelection selection = MySCView.this.viewer.getSelection();
                Object obj = ((IStructuredSelection)selection).getFirstElement();
                MySCView.this.showMessage("Double-click detected on "+obj.toString());
            }
        };
    }

    private void hookDoubleClickAction() {
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                MySCView.this.doubleClickAction.run();
            }
        });
    }
    private void showMessage(String message) {
        MessageDialog.openInformation(
                this.viewer.getControl().getShell(),
                "MySCView",
                message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        this.viewer.getControl().setFocus();
    }
}