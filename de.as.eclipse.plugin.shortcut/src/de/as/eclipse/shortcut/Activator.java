package de.as.eclipse.shortcut;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

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

    private static ImageRegistry imageRegistry = new ImageRegistry();

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        Activator.plugin = this;
        this.store = new ShortcutStore(this.getPreferenceStore());
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

    /**
     * Liefert Image zu dem gegeben Pfad. Falls noch unbekannt, wird ein Image erstellt und dem ImageRegistry hinzugefügt.
     * Registry kümmert sich um die Freigebe von Resources beim Zerstören von Display.
     * Die erstellen Images belegen Resources während der ganzen Zeit wenn Eclipse läuft.
     * Daher soll dieses Mechanismus sparsam verwendet werden.
     * @param desc Pfad für Image Descriptor (auch Schlüssel für Registry)
     * @return Image
     */
    public static Image getImage(String desc) {
        Image image = Activator.imageRegistry.get(desc);
        if (image == null) {
            image = Activator.getImageDescriptor(desc).createImage();
            Activator.imageRegistry.put(desc, image);
        }

        return image;
    }
}
