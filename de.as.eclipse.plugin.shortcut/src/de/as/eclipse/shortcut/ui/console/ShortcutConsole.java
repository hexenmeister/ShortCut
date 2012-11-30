/*
 * Datei: ShortcutConsole.java
 *
 * Rev.    Datum       Rel.    Anwender                        Aenderung und Grund
 * -------------------------------------------------------------------------------
 * ${RevHist}
 */
package de.as.eclipse.shortcut.ui.console;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.MessageConsole;

/**
 * TODO: JavaDoc-Kommentar einfügen! Beispiel: https://fisheye6.atlassian.com/browse/ant/ivy/ivyde/trunk/org.apache.ivyde.eclipse/src/java/org/apache/ivyde/eclipse/ui/console/IvyConsole.java?hb=true
 */
public class ShortcutConsole extends MessageConsole {

    /**
     * Konstruktor der Klasse.
     * @param name
     * @param imageDescriptor
     * @param autoLifecycle
     */
    public ShortcutConsole(String name, ImageDescriptor imageDescriptor, boolean autoLifecycle) {
        super(name, imageDescriptor, autoLifecycle);
        // TODO Auto-generated constructor stub
    }

    /**
     * Konstruktor der Klasse.
     * @param name
     * @param imageDescriptor
     */
    public ShortcutConsole(String name, ImageDescriptor imageDescriptor) {
        super(name, imageDescriptor);
        // TODO Auto-generated constructor stub
    }

    /**
     * Konstruktor der Klasse.
     * @param name
     * @param consoleType
     * @param imageDescriptor
     * @param autoLifecycle
     */
    public ShortcutConsole(String name, String consoleType, ImageDescriptor imageDescriptor, boolean autoLifecycle) {
        super(name, consoleType, imageDescriptor, autoLifecycle);
        // TODO Auto-generated constructor stub
    }

    /**
     * Konstruktor der Klasse.
     * @param name
     * @param consoleType
     * @param imageDescriptor
     * @param encoding
     * @param autoLifecycle
     */
    public ShortcutConsole(String name, String consoleType, ImageDescriptor imageDescriptor, String encoding, boolean autoLifecycle) {
        super(name, consoleType, imageDescriptor, encoding, autoLifecycle);
        // TODO Auto-generated constructor stub
    }

}
