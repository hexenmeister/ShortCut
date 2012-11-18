package de.as.eclipse.shortcut.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import de.as.eclipse.shortcut.business.Shortcut;
import de.as.eclipse.shortcut.ui.console.ShortcutConsole;
import de.as.eclipse.shortcut.ui.views.ShortCutView;

public class ProcessExecutor {

    private static List<Process> processList = new ArrayList<Process>();

    //TODO: Start von exe-files mit Parameter (und ohne GrabInput) funktioniert nicht richtig.

    public static void launchShortcut(Shortcut shortcut) {
        String location = shortcut.getLocation();
        //        String parameter = shortcut.getParameters();
        // TODO: MoreCommands-Feld
        String workDir = shortcut.getWorkingDir();

        // Variablen auflösen
        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
        try {
            location = variableManager.performStringSubstitution(location, false);
            //            parameter = variableManager.performStringSubstitution(parameter, false);
            if (workDir != null) {
                workDir = variableManager.performStringSubstitution(workDir, false);
            }
        } catch (CoreException e1) {
            // ignore
        }

        boolean grabOutput = shortcut.isGrabOutput();

        if (OSUtil.isWinNt() || OSUtil.isWinOld()) {
            File workDirFile = null;
            if (workDir != null) {
                workDirFile = new File(workDir);
                if (!workDirFile.exists() || !workDirFile.isDirectory()) {
                    workDirFile = null;
                }
            }
            try {
                Process process = null;
                // Process process = Runtime.getRuntime().exec(location);
                ShortcutConsole console = null;

                List<String> tokenListL = ProcessExecutor.tokenize(location);
                //                List<String> tokenListP = ProcessExecutor.tokenize(parameter);
                //                tokenListL.addAll(tokenListP);

                if (grabOutput) {
                    String name = shortcut.getName();
                    if ((name == null) || (name.trim().length() == 0)) {
                        name = shortcut.getLocation();
                    }
                    console = ProcessExecutor.openNewConsole(name);
                    ProcessExecutor.showConsole(console);
                    if (OSUtil.isWinNt()) {
                        tokenListL.add(0, "cmd.exe");
                        tokenListL.add(1, "/C");
                    } else if (OSUtil.isWinOld()) {
                        tokenListL.add(0, "command.com");
                        tokenListL.add(1, "/C");
                    }
                } else {
                    tokenListL.add(0, "rundll32");
                    tokenListL.add(1, "url.dll,FileProtocolHandler");
                }

                // TODO: Umbauen: Parameter in Array!
                // process = Runtime.getRuntime().exec(new String[] { "cmd", "/C", "dir" });

                process = Runtime.getRuntime().exec(tokenListL.toArray(new String[0]), null, workDirFile);

                StreamGrabber errorGrabber = new StreamGrabber(process.getErrorStream(), console);
                StreamGrabber outputGrabber = new StreamGrabber(process.getInputStream(), console);
                errorGrabber.start();
                outputGrabber.start();
                // TODO: Gedacht, um später in der Console Prozesse beenden zu können.
                // Es muss noch eine sichere Möglichkeit geschaffen werden, die beendte Prozesse in jedem Fall zu entfernen.
                // Ansonsten entstehen Leaks (wie auch gerade jetzt).
                ProcessExecutor.processList.add(process);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MessageDialog.openError(ShortCutView.getShell(), "Launch Error", "This feature is supported only for Microsoft Windows operating system.");
        }
    }

    private static List<String> tokenize(String str) {
        List<String> ret = new ArrayList<String>();
        if (str != null) {
            for (StringTokenizer st = new StringTokenizer(str); st.hasMoreElements();) {
                ret.add(st.nextToken());
            }
        }
        return ret;
    }

    private static void showConsole(IConsole console) {
        IWorkbench wb = PlatformUI.getWorkbench();
        IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        IWorkbenchPage page = win.getActivePage();

        String id = IConsoleConstants.ID_CONSOLE_VIEW;
        IConsoleView view;
        try {
            view = (IConsoleView) page.showView(id);
            view.display(console);
        } catch (PartInitException e) {
            // Ignore
            e.printStackTrace();
        }
    }

    private static ShortcutConsole openNewConsole(String name) {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        ShortcutConsole myConsole = new ShortcutConsole(name, null, true); // TODO: Image?
        conMan.addConsoles(new IConsole[] {myConsole});
        return myConsole;
    }

    // private static ShortcutConsole findConsole(String name) {
    // ConsolePlugin plugin = ConsolePlugin.getDefault();
    // IConsoleManager conMan = plugin.getConsoleManager();
    // IConsole[] existing = conMan.getConsoles();
    // for (int i = 0; i < existing.length; i++) {
    // if (name.equals(existing[i].getName())) {
    // return (ShortcutConsole) existing[i];
    // }
    // }
    // //no console found, so create a new one
    // ShortcutConsole myConsole = new ShortcutConsole(name, null);
    // conMan.addConsoles(new IConsole[] { myConsole });
    // return myConsole;
    // }

    private static class StreamGrabber extends Thread {
        InputStream is;

        // private ShortcutConsole console;
        private MessageConsoleStream consoleStream;

        StreamGrabber(InputStream is, MessageConsole console) {
            this.is = is;
            // this.console = console;
            this.consoleStream = console != null ? console.newMessageStream() : null;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(this.is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (this.consoleStream != null) {
                        this.consoleStream.println(line);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
