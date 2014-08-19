package eitex.sas.role;

import eitex.sas.role.*;

/**
 * Exception thrown when error is occurs while validating a role object. The
 * exception contains a set of boolean fields to indicate which fields contain
 * the error.
 */
public class RoleFieldException extends Exception {

    private boolean roleCodeError;
    private boolean roleNameError;
    private boolean roleDiscError;

    @Override
    public String getMessage() {
        return "Error occured while validating the role. Please check your input and try again!";
    }

    //<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public boolean hasRoleCodeError() {
        return roleCodeError;
    }

    public void setRoleCodeError(boolean roleCodeError) {
        this.roleCodeError = roleCodeError;
    }

    public boolean hasRoleNameError() {
        return roleNameError;
    }

    public void setRoleNameError(boolean roleNameError) {
        this.roleNameError = roleNameError;
    }

    public boolean hasRoleDiscError() {
        return roleDiscError;
    }

    public void setRoleDiscError(boolean roleDiscError) {
        this.roleDiscError = roleDiscError;
    }
    //</editor-fold>
}
