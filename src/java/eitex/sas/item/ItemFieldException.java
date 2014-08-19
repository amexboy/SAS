package eitex.sas.item;

/**
 * Exception thrown when error is occurs while validating an Item object. The
 * exception contains a set of boolean fields to indicate which fields contain
 * the error.
 */
public class ItemFieldException extends Exception {

    private boolean itemCodeError;
    private boolean itemNameError;
    private boolean itemDiscError;
    private boolean unitError;
    private boolean categoryError;

    @Override
    public String getMessage() {
        return "Error occured while validating the item. Please check your input and try again!";
    }

    //<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public boolean hasItemCodeError() {
        return itemCodeError;
    }

    public void setItemCodeError(boolean itemCodeError) {
        this.itemCodeError = itemCodeError;
    }

    public boolean hasItemNameError() {
        return itemNameError;
    }

    public void setItemNameError(boolean itemNameError) {
        this.itemNameError = itemNameError;
    }

    public boolean hasItemDiscError() {
        return itemDiscError;
    }

    public void setItemDiscError(boolean itemDiscError) {
        this.itemDiscError = itemDiscError;
    }

    public boolean hasUnitError() {
        return unitError;
    }

    public void setUnitError(boolean unitError) {
        this.unitError = unitError;
    }

    public boolean hasCaetgoryError() {
        return categoryError;
    }

    public void setCategoryError(boolean categoryError) {
        this.categoryError = categoryError;
    }
    //</editor-fold>
}
