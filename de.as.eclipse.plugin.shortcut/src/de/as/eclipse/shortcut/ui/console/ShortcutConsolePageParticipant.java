/*
 * Datei: ShortcutConsolePageParticipant.java
 *
 * Rev.    Datum       Rel.    Anwender                        Aenderung und Grund
 * -------------------------------------------------------------------------------
 * ${RevHist}
 */
package de.as.eclipse.shortcut.ui.console;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.actions.CloseConsoleAction;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * TODO: JavaDoc-Kommentar einfügen!
 */
public class ShortcutConsolePageParticipant implements IConsolePageParticipant {

    private CloseConsoleAction closeAction;

    public void init(IPageBookViewPage page, IConsole console) {
        this.closeAction = new CloseConsoleAction(console);
        IToolBarManager manager = page.getSite().getActionBars().getToolBarManager();
        manager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, this.closeAction);
    }

    public void dispose() {
        this.closeAction = null;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    public void activated() {
    }

    public void deactivated() {
    }

}
