package eitex.sas.category;

/**
 * Exception thrown when error is occurs while validating a category object. The
 * exception contains a set of boolean fields to indicate which fields contain
 * the error.
 */
public class CategoryFieldException extends Exception {

    private boolean categoryCodeError;
    private boolean categoryNameError;
    private boolean categoryDiscError;

    @Override
    public String getMessage() {
        return "Error occured while validating the category. Please check your input and try again!";
    }

    //<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public boolean hasCategoryCodeError() {
        return categoryCodeError;
    }

    public void setCategoryCodeError(boolean categoryCodeError) {
        this.categoryCodeError = categoryCodeError;
    }

    public boolean hasCategoryNameError() {
        return categoryNameError;
    }

    public void setCategoryNameError(boolean categoryNameError) {
        this.categoryNameError = categoryNameError;
    }

    public boolean hasCategoryDiscError() {
        return categoryDiscError;
    }

    public void setCategoryDiscError(boolean categoryDiscError) {
        this.categoryDiscError = categoryDiscError;
    }
    //</editor-fold>
}
