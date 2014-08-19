package eitex.sas.address;

/**
 * Exception thrown when error is occurs while validating an address object. The
 * exception contains a set of boolean fields to indicate which fields contain
 * the error.
 */
public class AddressFieldException extends Exception {

    private boolean officeBuildingError;
    private boolean officeNumberError;
    private boolean officePhoneError;
    private boolean mobileNumberError;
    private boolean emailError;

    @Override
    public String getMessage() {
        return "Error occured while validating the address. Please check your input and try again!";
    }

    //<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public boolean hasOfficeBuildingError() {
        return officeBuildingError;
    }

    public void setOfficeBuildingError(boolean officeBuildingError) {
        this.officeBuildingError = officeBuildingError;
    }

    public boolean hasOfficeNumberError() {
        return officeNumberError;
    }

    public void setOfficeNumberError(boolean officeNumberError) {
        this.officeNumberError = officeNumberError;
    }

    public boolean hasOfficePhoneError() {
        return officePhoneError;
    }

    public void setOfficePhoneError(boolean officePhoneError) {
        this.officePhoneError = officePhoneError;
    }

    public boolean hasMobileNumberError() {
        return mobileNumberError;
    }

    public void setMobileNumberError(boolean mobileNumberError) {
        this.mobileNumberError = mobileNumberError;
    }

    public boolean hasEmailError() {
        return emailError;
    }

    public void setEmailError(boolean emailError) {
        this.emailError = emailError;
    }
    //</editor-fold>
}
