package eitex.sas.coc;

import eitex.sas.coc.*;

/**
 * Exception thrown when error is occurs while validating a coc object. The
 * exception contains a set of boolean fields to indicate which fields contain
 * the error.
 */
public class CoCFieldException extends Exception {

    private boolean cocCodeError;
    private boolean cocNameError;
    private boolean cocDiscError;

    @Override
    public String getMessage() {
        return "Error occured while validating the coc. Please check your input and try again!";
    }

    //<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public boolean hasCoCCodeError() {
        return cocCodeError;
    }

    public void setCoCCodeError(boolean cocCodeError) {
        this.cocCodeError = cocCodeError;
    }

    public boolean hasCoCNameError() {
        return cocNameError;
    }

    public void setCoCNameError(boolean cocNameError) {
        this.cocNameError = cocNameError;
    }

    public boolean hasCoCDiscError() {
        return cocDiscError;
    }

    public void setCoCDiscError(boolean cocDiscError) {
        this.cocDiscError = cocDiscError;
    }
    //</editor-fold>
}
