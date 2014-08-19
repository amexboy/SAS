package eitex.sas.unit;

import javax.ws.rs.NotFoundException;

/**
 * Represents a Unit. This class contains fields to represent a measurement
 * unit.
 *
 * @author Amanu
 */
public class Unit {

    private String unitCode;
    private String unitName;
    private String unitDisc;

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

    public Unit() {
    }

    /**
     * Constructs an object of Unit. This constructor shall be used if values of
     * all the fields are known.
     *
     * @param unitCode
     * @param unitName
     * @param unitDisc
     * @throws eitex.sas.unit.UnitFieldException
     */
    public Unit(String unitCode, String unitName, String unitDisc) throws UnitFieldException {
        this.unitCode = unitCode;
        this.unitName = unitName;
        this.unitDisc = unitDisc;
        this.isNew = true;
        this.valid = this.validate();
    }

    /**
     * Constructs an object of Unit class getting the fields from database. This
     * constructor should be used when the unitCode is known and to get the rest
     * from database.
     *
     * @param unitCode
     */
    public Unit(String unitCode) throws NotFoundException {
        this.unitCode = unitCode;
        Unit un = UnitModel.getUnit(unitCode);
        this.unitName = un.unitName;
        this.unitDisc = un.unitDisc;
        this.isNew = false;
    }

    /**
     * Saves the changes of this object to database. This method either adds a
     * new entry in the data base or updates the existing according to isNew
     * field. If the object is validated and is valid the save is executed right
     * away. Else the validate() method is called and execution continues goes
     * accordingly.
     *
     * @param userName, the user that initiated the action
     * @throws eitex.sas.unit.UnitFieldException
     * @return
     */
    public boolean save(String userName) throws UnitFieldException {
        if (!isValidated()) {
            this.validate();
        }
        if (this.isValid()) {
            if (isNew) {
                return UnitModel.saveToDataBase(this, userName);
            } else if (modified) {
                return UnitModel.updateDataBase(this, userName);
            }
        }
        return false;
    }

    /**
     * Validates the objects fields. It performs a check to all fields. this
     * method sets validated fields to true. If there is a wrong field
     * eitex.sas.unit.UnitFieldException is thrown with set of boolean fields to
     * indicate which field contains error. unitCode, and unitName are required
     * and are always validated. Other fields are validated only if they exist.
     *
     * @throws eitex.sas.unit.UnitFieldException
     * @return
     */
    public boolean validate() throws UnitFieldException {
        this.validated = true;
        this.valid = false;
        boolean errorFound = false;

        UnitFieldException ex = new UnitFieldException();

        if (unitCode == null || unitCode.isEmpty() || unitCode.matches(".*[><=].*") || unitCode.length() < 4) {
            errorFound = true;
            ex.setUnitCodeError(true);
        }

        if (unitName == null || unitName.isEmpty() || unitName.matches(".*[><=&%-].*") || unitName.length() < 4) {
            errorFound = true;
            ex.setUnitNameError(true);
        }

        if (unitDisc != null && !unitDisc.isEmpty()) {
            if (unitDisc.matches(".*[><=&%-].*")) {
                errorFound = true;
                ex.setUnitDiscError(true);
            }
        }

        if (errorFound) {
            throw ex;
        }
        
        this.valid = true;
        return true;
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
     * Delete the unit in the database that this object represents.
     *
     * @param userName
     * @return
     */
    public boolean delete(String userName) {
        if (this.isNew) {
            throw new NotFoundException();
        } else {
            return UnitModel.delete(this, userName);
        }
    }

    /**
     * Returns all units that are registered in the database. If there is no unit 
     * registered in the database this method returns an empty ArrayList.
     * @return
     */
    public static java.util.ArrayList<Unit> getAllUnits(){
        return UnitModel.getAllUnits();
    }

//<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public String getUnitCode() {
        return this.unitCode;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
        this.modified = true;
        this.validated = false;
    }

    public String getUnitDisc() {
        return this.unitDisc;
    }

    public void setUnitDisc(String unitDisc) {
        this.unitDisc = unitDisc;
        this.modified = true;
        this.validated = false;
    }
//</editor-fold>

}
