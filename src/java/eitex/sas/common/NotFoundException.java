
package eitex.sas.common;

/**
 *
 * @author Amanu
 */
public class NotFoundException extends Exception {

    /**
     * Creates a new instance of <code>NotFoundException</code> without detail
     * message.
     */
    public NotFoundException() {
        super("The entity you requested is not found in the database.");
    }

    /**
     * Constructs an instance of <code>NotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NotFoundException(String msg) {
        super(msg);
    }
}
