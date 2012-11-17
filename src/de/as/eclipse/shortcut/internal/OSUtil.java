package de.as.eclipse.shortcut.internal;

/**
 * Utilities zur Bestimmung des verwendeten Betriebssystems.
 * 
 * @author Alexander Schulz
 *
 */
public class OSUtil {

    // Bei der Ermittlung des BS konnte man auch Property 'os.name' verwenden,
    // da diese durch -D Parameter �berschrieben werden kann.
    // Daher wurde der Umweg �ber die Verf�gbarkeit von bestimmten BS-Commandos gew�hlt.

    private static Boolean isMac;

    /**
     * Pr�fung, ob bei dem verwendeten Betriebssystem um MacOS handelt.
     * @return true/false
     */
    public static boolean isMac() {
        if (OSUtil.isMac == null) {
            try {
                Class.forName("com.apple.eawt.Application");
                OSUtil.isMac = true;
            } catch (Exception e) {
                OSUtil.isMac = false;
            }
        }
        return OSUtil.isMac;
    }

    private static Boolean isWinNt;

    /**
     * Pr�fung, ob bei dem verwendeten Betriebssystem um WindowsNT (NT, 2000, XP, Vista, 7 etc.) handelt.
     * @return true/false
     */
    public static boolean isWinNt() {
        if (OSUtil.isWinNt == null) {
            try {
                Runtime.getRuntime().exec(new String[] { "cmd.exe", "/C", "dir" });
                OSUtil.isWinNt = true;
            } catch (Exception e) {
                OSUtil.isWinNt = false;
            }
        }
        return OSUtil.isWinNt;
    }

    private static Boolean isWinOld;

    /**
     * Pr�fung, ob bei dem verwendeten Betriebssystem um Windows 95, 98 oder ME handelt.
     * @return true/false
     */
    public static boolean isWinOld() {
        if (OSUtil.isWinOld == null) {
            if (OSUtil.isWinNt()) {
                OSUtil.isWinOld = false;
            } else {
                try {
                    Runtime.getRuntime().exec(new String[] { "command.com", "/C", "dir" });
                    OSUtil.isWinOld = true;
                } catch (Exception e) {
                    OSUtil.isWinOld = false;
                }
            }
        }
        return OSUtil.isWinOld;
    }

    private static Boolean isUnix;

    /**
     * Pr�fung, ob bei dem verwendeten Betriebssystem um ein Unix-Derivat (aber nicht MacOS X) handelt.
     * @return true/false
     */
    public static boolean isUnix() {
        if (OSUtil.isUnix == null) {
            if (OSUtil.isMac()) {
                OSUtil.isUnix = false;
            } else {
                try {
                    Runtime.getRuntime().exec(new String[] { "sh", "-c", "ls" });
                    OSUtil.isUnix = true;
                } catch (Exception e) {
                    OSUtil.isUnix = false;
                }
            }
        }
        return OSUtil.isUnix;
    }

}
