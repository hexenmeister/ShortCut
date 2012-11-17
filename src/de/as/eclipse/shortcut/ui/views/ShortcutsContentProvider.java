package de.as.eclipse.shortcut.ui.views;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.business.Shortcut;

public class ShortcutsContentProvider implements IStructuredContentProvider {
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        // System.out.println();
    }

    public void dispose() {
    }

    public Object[] getElements(Object parent) {
        //        IWorkbenchPartSite site = (IWorkbenchPartSite)parent;
        //        IWorkbenchPart part = site.getPart();
        //        ShortCutView view = (ShortCutView)part;
        //        List<Shortcut> shortcuts = view.getData();
        List<Shortcut> shortcuts = Activator.getDefault().getShortcutStore().getVisibleShortcuts();
        return shortcuts.toArray();
    }

}
