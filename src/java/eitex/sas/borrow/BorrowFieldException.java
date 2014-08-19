package eitex.sas.borrow;

/**
 * Exception thrown when error is occurs while validating a borrow object. The
 * exception contains a set of boolean fields to indicate which fields contain
 * the error.
 */
public class BorrowFieldException extends Exception {

    private boolean borrowCodeError;
    private boolean borrowerError;
    private boolean givenByError;
    private boolean itemError;
    private boolean storeError;
    private boolean quantityError;

    @Override
    public String getMessage() {
        return "Error occured while validating the borrow. Please check your input and try again!";
    }

    //<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public boolean hasBorrowCodeError() {
        return borrowCodeError;
    }

    public void setBorrowCodeError(boolean borrowCodeError) {
        this.borrowCodeError = borrowCodeError;
    }

    public boolean hasBorrowerError() {
        return borrowerError;
    }

    public void setBorrowerError(boolean borrowerError) {
        this.borrowerError = borrowerError;
    }

    public boolean hasGivenByError() {
        return givenByError;
    }

    public void setGivenByError(boolean givenByError) {
        this.givenByError = givenByError;
    }

    public boolean hasItemError() {
        return itemError;
    }

    public void setItemError(boolean itemError) {
        this.itemError = itemError;
    }

    public boolean hasStoreError() {
        return storeError;
    }

    public void setStoreError(boolean storeError) {
        this.storeError = storeError;
    }

    public boolean hasQuantityError() {
        return quantityError;
    }

    public void setQuantityError(boolean quantityError) {
        this.quantityError = quantityError;
    }
    //</editor-fold>
}
