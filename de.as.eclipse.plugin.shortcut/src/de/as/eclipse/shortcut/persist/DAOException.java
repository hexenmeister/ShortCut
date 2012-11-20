package de.as.eclipse.shortcut.persist;

/**
 * Exception beim Persistenz-Problemen.
 *
 * @author Alexander Schulz
 * Date: 20.11.2012
 */
public class DAOException extends Exception {

    private static final long serialVersionUID = 7250841112017217216L;

    /**
     * Constructor.
     * @param message Meldung
     * @param cause Parent-Exception
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

}
