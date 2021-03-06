package de.as.eclipse.shortcut.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.business.Shortcut;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model on the fly, but a real implementation would connect
 * to the model available either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "shortcut.views.SampleView";

    private TreeViewer viewer;

    private DrillDownAdapter drillDownAdapter;

    private Action action1;

    private Action action2;

    private Action doubleClickAction;

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or simply return objects as-is. These objects may be sensitive to the
     * current input of the view, or ignore it and always show the same content (like Task List, for example).
     */

    class TreeObject implements IAdaptable {
        private String name;

        private TreeParent parent;

        public TreeObject(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setParent(TreeParent parent) {
            this.parent = parent;
        }

        public TreeParent getParent() {
            return this.parent;
        }

        @Override
        public String toString() {
            return this.getName();
        }

        public Object getAdapter(Class key) {
            return null;
        }
    }

    class TreeParent extends TreeObject {
        private ArrayList children;

        public TreeParent(String name) {
            super(name);
            this.children = new ArrayList();
        }

        public void addChild(TreeObject child) {
            this.children.add(child);
            child.setParent(this);
        }

        public void removeChild(TreeObject child) {
            this.children.remove(child);
            child.setParent(null);
        }

        public TreeObject[] getChildren() {
            return (TreeObject[]) this.children.toArray(new TreeObject[this.children.size()]);
        }

        public boolean hasChildren() {
            return this.children.size() > 0;
        }
    }

    class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        private TreeParent invisibleRoot;

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            List<Shortcut> shortcuts = Activator.getDefault().getShortcutStore().getVisibleShortcuts();
            return shortcuts.toArray();

            // if (parent.equals(getViewSite())) {
            // if (invisibleRoot == null)
            // initialize();
            // return getChildren(invisibleRoot);
            // }
            // return getChildren(parent);
        }

        public Object getParent(Object child) {
            if (child instanceof TreeObject) {
                return ((TreeObject) child).getParent();
            }
            return null;
        }

        public Object[] getChildren(Object parent) {
            List<Shortcut> shortcuts = Activator.getDefault().getShortcutStore().getVisibleShortcuts();
            return shortcuts.toArray();

            // if (parent instanceof TreeParent) {
            // return ((TreeParent) parent).getChildren();
            // }
            // return new Object[0];
        }

        public boolean hasChildren(Object parent) {
            if (parent instanceof TreeParent) {
                return ((TreeParent) parent).hasChildren();
            }
            return false;
        }

        /*
         * We will set up a dummy model to initialize tree heararchy. In a real code, you will connect to a real model and expose its hierarchy.
         */
        private void initialize() {
            TreeObject to1 = new TreeObject("Leaf 1");
            TreeObject to2 = new TreeObject("Leaf 2");
            TreeObject to3 = new TreeObject("Leaf 3");
            TreeParent p1 = new TreeParent("Parent 1");
            p1.addChild(to1);
            p1.addChild(to2);
            p1.addChild(to3);

            TreeObject to4 = new TreeObject("Leaf 4");
            TreeParent p2 = new TreeParent("Parent 2");
            p2.addChild(to4);

            TreeParent root = new TreeParent("Root");
            root.addChild(p1);
            root.addChild(p2);

            this.invisibleRoot = new TreeParent("");
            this.invisibleRoot.addChild(root);
        }
    }

    class ViewLabelProvider extends LabelProvider {

        @Override
        public String getText(Object obj) {
            return obj.toString();
        }

        @Override
        public Image getImage(Object obj) {
            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
            if (obj instanceof TreeParent) {
                imageKey = ISharedImages.IMG_OBJ_FOLDER;
            }
            return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
        }
    }

    class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public SampleView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        this.viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        this.drillDownAdapter = new DrillDownAdapter(this.viewer);
        this.viewer.setContentProvider(new ViewContentProvider());
        this.viewer.setLabelProvider(new ShortcutsLabelProvider());
        this.viewer.setSorter(new ShortcutsSorter());
        this.viewer.setInput(this.getViewSite());

        // Create the help context id for the viewer's control

        PlatformUI.getWorkbench().getHelpSystem().setHelp(this.viewer.getControl(), "ShortCut.viewer");
        this.makeActions();
        this.hookContextMenu();
        this.hookDoubleClickAction();
        this.contributeToActionBars();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                SampleView.this.fillContextMenu(manager);
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
        manager.add(new Separator());
        this.drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(this.action1);
        manager.add(this.action2);
        manager.add(new Separator());
        this.drillDownAdapter.addNavigationActions(manager);
    }

    private void makeActions() {
        this.action1 = new Action() {
            @Override
            public void run() {
                SampleView.this.showMessage("Action 1 executed");
            }
        };
        this.action1.setText("Action 1");
        this.action1.setToolTipText("Action 1 tooltip");

        this.action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        this.action2 = new Action() {
            @Override
            public void run() {
                SampleView.this.showMessage("Action 2 executed");
            }
        };
        this.action2.setText("Action 2");
        this.action2.setToolTipText("Action 2 tooltip");

        this.action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        this.doubleClickAction = new Action() {
            @Override
            public void run() {
                ISelection selection = SampleView.this.viewer.getSelection();
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                SampleView.this.showMessage("Double-click detected on " + obj.toString());
            }
        };
    }

    private void hookDoubleClickAction() {
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                SampleView.this.doubleClickAction.run();
            }
        });
    }

    private void showMessage(String message) {

        MessageDialog.openInformation(this.viewer.getControl().getShell(), "Sample View", message);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        this.viewer.refresh();

        this.viewer.getControl().setFocus();
    }
}
