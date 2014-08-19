package eitex.sas.user;

/**
 * Exception thrown when error is occurs while validating a user object. The
 * exception contains a set of boolean fields to indicate which fields contain
 * the error.
 */
public class UserFieldException extends Exception {

    private boolean userNameError;
    private boolean passwordError;
    private boolean firstNameError;
    private boolean lastNameError;
    private boolean addressError;
    private boolean cocError;

    @Override
    public String getMessage() {
        return "Error occured while validating the user. Please check your input and try again!";
    }

    public boolean hasUserNameError() {
        return userNameError;
    }

    public void setUserNameError(boolean userNameError) {
        this.userNameError = userNameError;
    }

    public boolean hasPasswordError() {
        return passwordError;
    }

    public void setPasswordError(boolean passwordError) {
        this.passwordError = passwordError;
    }

    public boolean hasFirstNameError() {
        return firstNameError;
    }

    public void setFirstNameError(boolean firstNameError) {
        this.firstNameError = firstNameError;
    }

    public boolean hasLastNameError() {
        return lastNameError;
    }

    public void setLastNameError(boolean lastNameError) {
        this.lastNameError = lastNameError;
    }

    public boolean hasAddressError() {
        return addressError;
    }

    public void setAddressError(boolean addressError) {
        this.addressError = addressError;
    }

    public boolean hasCocError() {
        return cocError;
    }

    public void setCocError(boolean cocError) {
        this.cocError = cocError;
    }

    
}
