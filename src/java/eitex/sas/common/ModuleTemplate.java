package eitex.sas.common;

/**
 * This class contains the common components of all the classes in this project.
 *
 * @author Amanu
 */
public abstract class ModuleTemplate {

    /**
     * Indicates if the object is populated from database or from user input.
     * isNew becomes true if the object is populated from user input.
     */
    private boolean isNew;
    /**
     * Indicates if the fields of the object are changed using setter methods.
     * Modified true if one of the setter methods is called. The field doesn't
     * matter if the isNew field is set to true;
     */
    private boolean modified;

    /**
     * Indicates if the object is validated. validated becomes true if the
     * validate() method is called.
     */
    private boolean validated = false;
    /**
     * Indicates if the current object is valid or not. valid is set to true if
     * the objects fields are all valid. The value of this field does not matter
     * if validated is false;
     */
    private boolean valid = false;

    /**
     * Saves the changes of this object to database. This method either adds a
     * new entry in the data base or updates the existing according to isNew
     * field. If the object is validated and is valid the save is executed right
     * away. Else the validate() method is called and execution continues goes
     * accordingly.
     *
     * @param userName
     * @throws Exception
     * @return boolean to indicate the success of the operation.
     */
    public abstract boolean save(String userName) throws Exception;

    /**
     * Validates the objects fields. It performs a check to all fields. this
     * method sets validated fields to true. If there is a wrong field an
     * exception is thrown with set of boolean fields to indicate which field
     * contains error.
     *
     * @return returns a boolean to indicate if the object is valid or not.
     * @throws Exception
     */
    public abstract boolean validate() throws Exception;

    /**
     * Delete the category in the database that this object represents.
     *
     * @param userName
     * @return returns a boolean to indicate if the object is valid or not.
     */
    public abstract boolean delete(String userName);

    /**
     * Recover the category in the database that this object represents. This
     * method is used to recover entries of the database after deletion.
     *
     * @param userName
     * @return returns a boolean to indicate if the object is valid or not.
     */
    public abstract boolean recover(String userName);

    /**
     * Sets the value for the valid field. True value indicates that the fields
     * of the object are valid.
     *
     * @param valid
     */
    protected void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Sets the value for the validated field. True value indicates that the
     * object has been validated;
     *
     * @param validated
     */
    protected void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * Indicates if the current object is valid or not. isValid() returns to
     * true if the objects fields are all valid. The value returned does not
     * matter if isValidated() returned false;
     *
     * @return
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Indicates if the object is validated. isValidated() returns true if the
     * validate() method is called.
     *
     * @return
     */
    public boolean isValidated() {
        return this.validated;
    }

    /**
     * Indicates if the object is a new. New objects are those objects that are
     * not in the database. If isNew is false, the object is a representation of
     * an entry in the database.
     *
     * @return
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * Set value for isNew field. True value indicates that the object is a new.
     * New objects are those objects that are not in the database. If isNew is
     * false, the object is a representation of an entry in the database.
     *
     * @param isNew
     */
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * Indicates if the object is modified or not. When a field is modified,
     * this field is set. This indicates the save method to update a database
     * entry. This value is only checked if the object's isNew is not set.
     *
     * @return
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Sets the value for modified field. Modified indicates if the object is
     * modified or not. When a field is modified, this field is set. This
     * indicates the save method to update a database entry. This value is only
     * checked if the object's isNew is not set.
     *
     * @param modified
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
