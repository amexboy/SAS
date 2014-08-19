package eitex.sas.unit;

/**
 * Exception thrown when error is occurs while validating a unit object. The
 * exception contains a set of boolean fields to indicate which fields contain
 * the error.
 */
public class UnitFieldException extends Exception {

    private boolean unitCodeError;
    private boolean unitNameError;
    private boolean unitDiscError;

    @Override
    public String getMessage() {
        return "Error occured while validating the unit. Please check your input and try again!";
    }

    //<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public boolean hasUnitCodeError() {
        return unitCodeError;
    }

    public void setUnitCodeError(boolean unitCodeError) {
        this.unitCodeError = unitCodeError;
    }

    public boolean hasUnitNameError() {
        return unitNameError;
    }

    public void setUnitNameError(boolean unitNameError) {
        this.unitNameError = unitNameError;
    }

    public boolean hasUnitDiscError() {
        return unitDiscError;
    }

    public void setUnitDiscError(boolean unitDiscError) {
        this.unitDiscError = unitDiscError;
    }
    //</editor-fold>
}
