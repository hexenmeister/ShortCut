package de.as.eclipse.shortcut.ui.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.business.Shortcut;
import de.as.eclipse.shortcut.internal.ProcessExecutor;
import de.as.eclipse.shortcut.persist.DAOException;
import de.as.eclipse.shortcut.ui.UIConstants;
import de.as.eclipse.shortcut.ui.UIUtils;
import de.as.eclipse.shortcut.ui.views.dialog.ManageContainerDialog;
import de.as.eclipse.shortcut.ui.views.dialog.ShortcutDialog;

public class ShortCutView extends ViewPart {

    //XXX: NB: http://www.eclipse.org/articles/article.php?file=Article-CustomDrawingTableAndTreeItems/index.html#_example6
    //XXX: Cell editing: http://www.subshell.com/en/subshell/blog/Eclipse-RCP-Comboboxes-inside-a-JFace-TableViewer100.html

    //XXX: http://www.eclipse.org/swt/snippets/

    //XXX: http://www.java2s.com/Tutorial/Java/0280__SWT/DrawdifferentforegroundcolorsandfontsfortextinaTableItem.htm

    //XXX: !!! StylelabelProvider: http://wiki.eclipse.org/JFaceSnippets#Snippet049StyledCellLabelProvider


    public ShortCutView() {
    }

    private Table table;

    private static TableViewer tableViewer;

    private ShortcutsFilter filter;

    private Action editShortcut;

    private Action addShortcut;

    private Action removeShortcut;

    private Action manageContainers;

    private Action importShortcuts;

    private Action exportShortcuts;

    private Action doubleClickAction;

    private static final String NAME_COLUMN = "Name";

    private static final String GROUP_COLUMN = "Group";

    private static final String CATEGORY1_COLUMN = "Cat1";

    private static final String CATEGORY2_COLUMN = "Cat2";

    private static final String PRIORITY_COLUMN = "Priority";

    private static final String LOCATION_COLUMN = "Location/Script";

    //    private static final String MCMDS_COLUMN = "more commands";

    private static final String WORKDIR_COLUMN = "Work dir";

    private static final String SIZE_COLUMN = "Size (bytes)";

    private static final String LAST_MODIFIED_COLUMN = "Last Modified";

    public FontData FONT_ARIAL_8 = new FontData("Arial", 8, 0);

    //TODO: ColNames sollen auch im LabelProvider definiert werden (denn die Reihenfolge hiere wie da wichtig ist)
    private String[] columnNames = { ShortCutView.NAME_COLUMN, ShortCutView.GROUP_COLUMN, ShortCutView.CATEGORY1_COLUMN, ShortCutView.CATEGORY2_COLUMN, ShortCutView.PRIORITY_COLUMN,
            ShortCutView.SIZE_COLUMN,
            ShortCutView.WORKDIR_COLUMN, ShortCutView.LAST_MODIFIED_COLUMN };

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout(9, false);
        parent.setLayout(layout);

        Label searchLabel = new Label(parent, SWT.NONE);
        searchLabel.setImage(Activator.getImage(UIConstants.ICON_SEARCHFILE));

        final Combo searchText = new Combo(parent, SWT.SEARCH); //SWT.ICON_SEARCH
        searchText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        searchText.setVisibleItemCount(10);
        UIUtils.readHistoryItems("searchText", searchText);

        ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        ToolItem btnBtnclearsearch = new ToolItem(toolBar, SWT.NONE);
        btnBtnclearsearch.setImage(Activator.getImage(UIConstants.ICON_CLEAR));

        final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
        //        toolBar.setMenu(new Menu(ShortCutView.tableViewer.getControl()));
        //        MenuItem item2 = new MenuItem(toolBar.getMenu(), SWT.PUSH);

        //        Label btnBtnclearsearch = new Label(parent, SWT.NONE);
        //        btnBtnclearsearch.setImage(Activator.getImage(UIConstants.ICON_CLEAR));
        Label filler = new Label(parent, SWT.NONE);
        filler.setText("   ");

        Label lblIn = new Label(parent, SWT.NONE);
        // lblIn.setText("in:");
        lblIn.setImage(Activator.getImage(UIConstants.ICON_CHOOSECOLUMNS));

        final Button btnFilterName = new Button(parent, SWT.CHECK);
        btnFilterName.setText("Name");
        final Button btnFilterCategory = new Button(parent, SWT.CHECK);
        btnFilterCategory.setText("Category");
        final Button btnFilterLocation = new Button(parent, SWT.CHECK);
        btnFilterLocation.setText("Location");

        Composite composite = new Composite(parent, SWT.NONE);

        StackLayout stack = new StackLayout();
        composite.setLayout(stack);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 9, 1));

        this.createTableViewer(composite);

        TreeViewer treeViewer = new TreeViewer(composite, SWT.BORDER);
        //        Tree tree = treeViewer.getTree();

        stack.topControl = this.table;

        btnFilterName.setSelection(this.filter.isUseName());
        btnFilterCategory.setSelection(this.filter.isUseCategory());
        btnFilterLocation.setSelection(this.filter.isUseLocation());

        // TableViewer tableViewer_1 = new TableViewer(composite,SWT.BORDER | SWT.FULL_SELECTION);
        // this.table_1 = tableViewer_1.getTable();

        btnBtnclearsearch.setToolTipText("Clear search string");
        // TODO: Listener zusammenfassen
        //        btnBtnclearsearch.addMouseListener(new MouseAdapter() {
        //            @Override
        //            public void mouseDown(MouseEvent e) {
        //                searchText.setText("");
        //                ShortCutView.this.filter.setSearchText(searchText.getText());
        //                ShortCutView.tableViewer.refresh();
        //            }
        //        });

        btnBtnclearsearch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchText.setText("");
                ShortCutView.this.filter.setSearchString(searchText.getText());
                ShortCutView.tableViewer.refresh();
            }
        });

        // TODO: Listener zusammenfassen
        searchText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                UIUtils.handleHistoryItems(searchText, 10);
                UIUtils.saveHistoryItems("searchText", searchText);
            }
        });
        searchText.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {
                // ENTER-Taste
                if (e.keyCode == 13) {
                    UIUtils.handleHistoryItems(searchText, 10);
                    UIUtils.saveHistoryItems("searchText", searchText);
                }
                ShortCutView.this.filter.setSearchString(searchText.getText());
                ShortCutView.tableViewer.refresh();
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });

        searchText.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                ShortCutView.this.filter.setSearchString(searchText.getText());
                ShortCutView.tableViewer.refresh();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        SelectionListener filterSelectionlistener = new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                ShortCutView.this.filter.defineSearchFilds(btnFilterName.getSelection(), btnFilterCategory.getSelection(), btnFilterLocation.getSelection());
                ShortCutView.tableViewer.refresh();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        };
        btnFilterName.addSelectionListener(filterSelectionlistener);
        btnFilterCategory.addSelectionListener(filterSelectionlistener);
        btnFilterLocation.addSelectionListener(filterSelectionlistener);

        ShortCutView.tableViewer.setInput(this.getViewSite());

        this.makeActions();
        this.hookContextMenu();
        this.hookDoubleClickAction();
        this.contributeToActionBars();
        this.updateTitle();
        this.packColumns();

    }

    public List<Shortcut> getData() {
        List<Shortcut> shortcuts = Activator.getDefault().getShortcutStore().getVisibleShortcuts();
        return shortcuts;
    }

    private void packColumns() {
        // this.table.getColumn(0).pack();
        this.table.getColumn(1).pack();
        this.table.getColumn(2).pack();
        this.table.getColumn(3).pack();
        this.table.getColumn(4).pack();
        this.table.getColumn(5).pack();
        this.table.getColumn(6).pack();
        this.table.getColumn(7).pack();
        this.table.getColumn(8).pack();
        this.table.getColumn(9).pack();
    }

    public TableViewer getTableViewer() {
        return ShortCutView.tableViewer;
    }

    private void createTableViewer(Composite parent) {
        ShortCutView.tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        ShortCutView.tableViewer.setUseHashlookup(true);
        ShortCutView.tableViewer.setColumnProperties(this.columnNames);
        ShortCutView.tableViewer.setContentProvider(new ShortcutsContentProvider());
        //        ShortCutView.tableViewer.setLabelProvider(new ShortcutsLabelProvider());
        this.filter = new ShortcutsFilter();
        ShortCutView.tableViewer.addFilter(this.filter);
        ShortCutView.tableViewer.setLabelProvider(new ShortcutsStyledLabelProvider(new ShortcutsLabelProvider(), this.filter));
        // ShortCutView.tableViewer.setSorter(new ShortcutsSorter());

        // }

        // private void createTable(Composite parent) {
        // int style = 67586;

        GridLayout layout = new GridLayout(1, true);

        // this.table = new Table(parent, style);
        this.table = ShortCutView.tableViewer.getTable();
        this.table.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalSpan = 3;
        this.table.setLayoutData(gridData);

        this.table.setLinesVisible(true);
        this.table.setHeaderVisible(true);

        TableColumn col0 = new TableColumn(this.table, SWT.RIGHT, 0);
        col0.setWidth(17);
        col0.setMoveable(false);
        col0.setResizable(false);
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.NAME_COLUMN, ShortCutView.NAME_COLUMN, ShortcutSorterFactory.NAME_SORTER, SWT.LEFT, SWT.UP, true).chooseColumnForSorting();
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.GROUP_COLUMN, ShortCutView.GROUP_COLUMN, ShortcutSorterFactory.GROUP_SORTER, SWT.LEFT, SWT.UP, true);
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.PRIORITY_COLUMN, ShortCutView.PRIORITY_COLUMN, ShortcutSorterFactory.PRIORITY_SORTER, SWT.LEFT, SWT.UP, true);
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.CATEGORY1_COLUMN, ShortCutView.CATEGORY1_COLUMN, ShortcutSorterFactory.CATEGORY1_SORTER, SWT.LEFT, SWT.UP, true);
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.CATEGORY2_COLUMN, ShortCutView.CATEGORY2_COLUMN, ShortcutSorterFactory.CATEGORY2_SORTER, SWT.LEFT, SWT.UP, true);
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.LOCATION_COLUMN, ShortCutView.LOCATION_COLUMN, ShortcutSorterFactory.PAYLOAD_SORTER, SWT.LEFT, SWT.UP, true);
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.WORKDIR_COLUMN, ShortCutView.WORKDIR_COLUMN, ShortcutSorterFactory.WORKDIR_SORTER, SWT.LEFT, SWT.UP, true);
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.SIZE_COLUMN, ShortCutView.SIZE_COLUMN, ShortcutSorterFactory.SIZE_SORTER, SWT.LEFT, SWT.UP, true);
        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.LAST_MODIFIED_COLUMN, ShortCutView.LAST_MODIFIED_COLUMN, ShortcutSorterFactory.LASTMODIFIED_SORTER, SWT.LEFT, SWT.UP, true);
        //        UIUtils.createTableColumn(ShortCutView.tableViewer, ShortCutView.MCMDS_COLUMN, ShortCutView.MCMDS_COLUMN, ShortcutSorterFactory.MCMDS_SORTER, SWT.LEFT, SWT.UP, true);

        this.table.setFont(new Font(this.table.getDisplay(), this.FONT_ARIAL_8));

        //        this.table.addListener(SWT.PaintItem, new Listener() {
        //            public void handleEvent(Event event) {
        //                TableItem item = (TableItem) event.item;
        //                //            Image trailingImage = (Image)item.getData();
        //                //            if (trailingImage != null) {
        //                //            int x = event.x + event.width + IMAGE_MARGIN;
        //                //            int itemHeight = tree.getItemHeight();
        //                //            int imageHeight = trailingImage.getBounds().height;
        //                //            int y = event.y + (itemHeight - imageHeight) / 2;
        //                //            event.gc.drawImage(trailingImage, x, y);
        //                int x = event.x + event.width;
        //                //                int itemHeight = ShortCutView.this.table.getItemHeight();
        //                int y = event.y + ((event.height - 12) / 2);//+ ((itemHeight ) / 2); // Fontmetrics!
        //                //                event.gc.drawText(item.getText(), x, y);
        //                Shortcut s = (Shortcut) item.getData();
        //                String text = ((ShortcutsLabelProvider) ShortCutView.tableViewer.getLabelProvider()).getColumnText(s, event.index);
        //                event.gc.drawText(text != null ? text : "", x, y);
        //            }
        //        });
    }

    // -----------

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                ShortCutView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(ShortCutView.tableViewer.getControl());
        ShortCutView.tableViewer.getControl().setMenu(menu);
        this.getSite().registerContextMenu(menuMgr, ShortCutView.tableViewer);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(this.addShortcut);
        manager.add(this.editShortcut);
        manager.add(this.removeShortcut);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void contributeToActionBars() {
        IActionBars bars = this.getViewSite().getActionBars();
        this.fillLocalPullDown(bars.getMenuManager());
        this.fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(this.addShortcut);
        manager.add(this.editShortcut);
        manager.add(this.removeShortcut);
        manager.add(new Separator());
        manager.add(this.manageContainers);
        manager.add(this.importShortcuts);
        manager.add(this.exportShortcuts);
        manager.add(new Separator());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(this.addShortcut);
        manager.add(this.editShortcut);
        manager.add(this.removeShortcut);
        manager.add(new Separator());
        manager.add(this.manageContainers);
        manager.add(new Separator());
    }

    private void makeActions() {
        this.manageContainers = new Action() {
            @Override
            public void run() {
                // Container-Verwaltung
                ManageContainerDialog mc = new ManageContainerDialog(ShortCutView.getShell());
                mc.open();

                ShortCutView.tableViewer.refresh();
                ShortCutView.this.packColumns();
                ShortCutView.this.updateTitle();
            }
        };
        this.manageContainers.setText("Manage Containers");
        this.manageContainers.setToolTipText("Create, add and delete shortcut containers");
        this.manageContainers.setImageDescriptor(Activator.getImageDescriptor(UIConstants.ICON_CONTAINERS));

        this.importShortcuts = new Action() {
            @Override
            public void run() {
                // TODO

                ShortCutView.tableViewer.refresh();
                ShortCutView.this.packColumns();
                ShortCutView.this.updateTitle();
            }
        };
        this.importShortcuts.setText("Import shortcuts");
        this.importShortcuts.setToolTipText("Import shortcuts");
        this.importShortcuts.setImageDescriptor(Activator.getImageDescriptor(UIConstants.ICON_SHORTCUT)); // TODO

        this.exportShortcuts = new Action() {
            @Override
            public void run() {
                // TODO

                // ShortCutView.tableViewer.refresh();
                // ShortCutView.this.packColumns();
                // ShortCutView.this.updateTitle();
            }
        };
        this.exportShortcuts.setText("Export shortcuts");
        this.exportShortcuts.setToolTipText("Export shortcuts");
        this.exportShortcuts.setImageDescriptor(Activator.getImageDescriptor(UIConstants.ICON_SHORTCUT)); // TODO

        this.editShortcut = new Action() {
            @Override
            public void run() {
                ISelection selection = ShortCutView.tableViewer.getSelection();
                Iterator<?> itr = ((IStructuredSelection) selection).iterator();
                if (itr.hasNext()) {
                    Shortcut shortcut = (Shortcut) itr.next();
                    ShortcutDialog add = new ShortcutDialog(ShortCutView.tableViewer.getControl().getShell(), shortcut);
                    add.open();

                    ShortCutView.tableViewer.refresh();
                    ShortCutView.this.packColumns();
                    ShortCutView.this.updateTitle();
                }
            }
        };
        this.editShortcut.setText("Edit shortcut");
        this.editShortcut.setToolTipText("Edit shortcut");
        this.editShortcut.setImageDescriptor(Activator.getImageDescriptor(UIConstants.ICON_EDITFILE));

        this.addShortcut = new Action() {
            @Override
            public void run() {
                ShortcutDialog add = new ShortcutDialog(ShortCutView.tableViewer.getControl().getShell());
                add.open();

                ShortCutView.tableViewer.refresh();
                ShortCutView.this.packColumns();
                ShortCutView.this.updateTitle();
            }
        };
        this.addShortcut.setText("Add new shortcut");
        this.addShortcut.setToolTipText("Add new shortcut");
        this.addShortcut.setImageDescriptor(Activator.getImageDescriptor(UIConstants.ICON_ADDFILE));

        this.removeShortcut = new Action() {
            @Override
            public void run() {
                ISelection selection = ShortCutView.tableViewer.getSelection();
                Iterator<?> itr = ((IStructuredSelection) selection).iterator();

                while (itr.hasNext()) {
                    try {
                        Activator.getDefault().getShortcutStore().removeShortcut((Shortcut) itr.next());
                    } catch (DAOException e) {
                        // TODO: Meldung anzeigen
                        e.printStackTrace();
                    }
                }

                ShortCutView.tableViewer.refresh();
                ShortCutView.this.updateTitle();
            }
        };
        this.removeShortcut.setText("Remove shortcut");
        this.removeShortcut.setToolTipText("Remove shortcut");
        this.removeShortcut.setImageDescriptor(Activator.getImageDescriptor(UIConstants.ICON_REMOVEFILE));

        this.doubleClickAction = new Action() {
            @Override
            public void run() {
                ISelection selection = ShortCutView.tableViewer.getSelection();
                Shortcut shortcut = (Shortcut) ((IStructuredSelection) selection).getFirstElement();

                ProcessExecutor.launchShortcut(shortcut);
            }
        };
    }

    private void hookDoubleClickAction() {
        ShortCutView.tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                ShortCutView.this.doubleClickAction.run();
            }
        });
    }

    @Override
    public void setFocus() {
        ShortCutView.tableViewer.getControl().setFocus();
    }

    private void updateTitle() {
        // setTitle("Contains " +String.valueOf(tableViewer.getTable().getItemCount()) + " shortcuts");
        // Ersatz?
        this.setPartName("Contains " + String.valueOf(ShortCutView.tableViewer.getTable().getItemCount()) + " shortcuts");
        this.setContentDescription("");
    }

    public static Shell getShell() {
        return ShortCutView.tableViewer.getControl().getShell();
    }
}
