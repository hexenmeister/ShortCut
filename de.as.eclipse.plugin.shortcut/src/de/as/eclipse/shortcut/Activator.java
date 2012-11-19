package de.as.eclipse.shortcut;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.as.eclipse.shortcut.persist.ShortcutContainer;
import de.as.eclipse.shortcut.persist.ShortcutPreferenceStoreDAO;
import de.as.eclipse.shortcut.persist.ShortcutStore;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "de.as.eclipse.plugin.shortcut"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    // DAO instance
    private ShortcutStore store;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        Activator.plugin = this;
        this.store = new ShortcutStore();
        this.store.init();
        ShortcutPreferenceStoreDAO dao = new ShortcutPreferenceStoreDAO(this.getPreferenceStore());
        ShortcutContainer defaultContainer = this.store.createNewContainer(dao, "Intern (Workspace)");
        this.store.addContainer(defaultContainer);
        // TODO: Weitere Container lesen

        //XXX:Test
        //        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
        //        //        IStringVariable[] variables = variableManager.getVariables();
        //        //        System.out.println("---");
        //        //        for (int i = 0; i < variables.length; i++) {
        //        //            try {
        //        //                System.out.println(variables[i].getName() + ":" + variableManager.performStringSubstitution("${" + variables[i].getName() + "}", false));
        //        //            } catch (Exception e) {
        //        //                // TODO Auto-generated catch block
        //        //                e.printStackTrace();
        //        //            }
        //        //        }
        //        System.out.println("---");
        //        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        //        IWorkspaceRoot root = workspace.getRoot();
        //        IPath location = root.getLocation();
        //        System.out.println(location.toString());
        //        System.out.println(variableManager.performStringSubstitution("${workspace_loc:/External Plug-in Libraries}", false));
        //        System.out.println(variableManager.performStringSubstitution("${workspace_loc:/FileNameConvert}", false));
        //        //        System.out.println(variableManager.performStringSubstitution("${workspace_loc:/X}", false));
        //        System.out.println(variableManager.performStringSubstitution("${eclipse_home}", false));
        //        //        System.out.println(variableManager.performStringSubstitution("${workspace_loc:ShortCut}", false));
        //
        //        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        //        for (int i = 0; i < projects.length; i++) {
        //            System.out.println(projects[i].getName() + ":" + projects[i].getLocation() + ":" + projects[i].getFullPath());
        //        }

    }

    @Override
    public void stop(BundleContext context) throws Exception {
        this.store.close();
        this.store = null;
        Activator.plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return Activator.plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, path);
    }

    /**
     * Liefert eine Instance der Datenzugriffsklasse.
     * @return DAO-Instance
     */
    public ShortcutStore getShortcutStore() {
        return this.store;
    }
}
