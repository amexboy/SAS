package eitex.sas.unit;

import eitex.sas.unit.*;
import eitex.sas.common.ExceptionLogger;
import eitex.sas.common.ModuleTemplate;
import eitex.sas.common.NotFoundException;
import eitex.sas.user.User;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;

/**
 * Represents a Unit. This class contains fields to represent a measurement
 * unit.
 *
 * @author Amanu
 */
public class Unit extends ModuleTemplate{

    private String unitCode;
    private String unitName;
    private String unitDisc;

    

    public Unit() {
    }

    /**
     * Constructs an object of Unit. This constructor shall be used if
     * values of all the fields are known.
     *
     * @param unitCode
     * @param unitName
     * @param unitDisc
     */
    public Unit(String unitCode, String unitName, String unitDisc) {
        this.unitCode = unitCode;
        this.unitName = unitName;
        this.unitDisc = unitDisc;
        this.setNew(true);
    }

    /**
     * Constructs an object of Unit class getting the fields from database.
     * This constructor should be used when the unitCode is known and to get
     * the rest from database.
     *
     * @param unitCode
     * @throws eitex.sas.common.NotFoundException
     */
    public Unit(String unitCode) throws NotFoundException {
        this.unitCode = unitCode;
        Unit un = UnitModel.getUnit(unitCode);
        this.unitName = un.unitName;
        this.unitDisc = un.unitDisc;
        this.setNew(false);
    }

    /**
     * @param userName
     * @throws eitex.sas.unit.UnitFieldException
     * @return boolean to indicate the success of the operation.
     */
    @Override
    public boolean save(String userName) throws UnitFieldException {
        if (!isValidated()) {
            this.validate();
        }
        if (this.isValid()) {
            if (isNew()) {
                return UnitModel.saveToDataBase(this, userName);
            } else if (isModified()) {
                return UnitModel.updateDataBase(this, userName);
            }
        }
        return false;
    }

    /**
     * @throws eitex.sas.unit.UnitFieldException
     * @return returns a boolean to indicate if the object is valid or not.
     */
    @Override
    public boolean validate() throws UnitFieldException {
        this.setValidated(true);
        this.setValid(false);
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
//            if (unitDisc.matches(".*[><=&%-].*")) {
//                errorFound = true;
//                ex.setUnitDiscError(true);
//            }
        }

        if (errorFound) {
            throw ex;
        }

        this.setValid(true);
        this.setValidated(true);
        return true;
    }


    /**
     * Delete the unit in the database that this object represents.
     *
     * @param userName
     * @return returns a boolean to indicate if the object is valid or not.
     */
    @Override
    public boolean delete(String userName) {
        if (this.isNew() || !this.isModified()) {
            return false;
        } else {
            return UnitModel.delete(this, userName);
        }
    }

    /**
     * Recover the unit in the database that this object represents. This method
     * is used to recover entries of the database after deletion.
     *
     * @param userName
     * @return returns a boolean to indicate if the object is valid or not.
     */
    @Override
    public boolean recover(String userName) {
        if (this.isNew()) {
            return false;
        } else {
            return UnitModel.recover(this, userName);
        }
    }

    /**
     * Returns all Entries of this class that are registered in the database.
     *
     * @return returns all categories
     */
    public static java.util.ArrayList<Unit> getAll() {
        return UnitModel.getAllUnits(true);
    }

    /**
     * Returns all Entries of this class that are registered in the database.
     *
     * @return returns all deleted categories
     */
    public static java.util.ArrayList<Unit> getAllDeleted() {
        return UnitModel.getAllUnits(false);
    }

    public static ArrayList<Unit> createUnitFormJSON(String model) {

        final ArrayList<Unit> categories = new ArrayList<>();
        class UnitFromJsonModel {

            public String unitCode = "", unitName = "", unitDisc = "";
            public String json;
            public JsonParser p;

            public UnitFromJsonModel ObjectFromJSON(UnitFromJsonModel model) {
                switch (p.next()) {
                    case END_ARRAY:
                        return model;
                    case END_OBJECT:
                        categories.add(new Unit(unitCode, unitName, unitDisc));
                        break;
                    case KEY_NAME:
                        String keyName = p.getString();
                        JsonParser.Event next = p.next();
                        try {
                            if (next == JsonParser.Event.VALUE_STRING) {
                                model.getClass().getField(keyName).set(model, p.getString());
                            }  else if(next == JsonParser.Event.VALUE_TRUE) {
                                model.getClass().getField(keyName).set(model, true);
                            } else if(next == JsonParser.Event.VALUE_FALSE) {
                                model.getClass().getField(keyName).set(model, false);
                            }else {
                                model.getClass().getField(keyName).set(model, null);
                            }

//                            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "key name " + keyName);
//                            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "key value " + model.getClass().getField(keyName).get(model));
                        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                            ExceptionLogger.log(ex);
                            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                        }

                }
                return ObjectFromJSON(model);
            }

            public UnitFromJsonModel(String json) {
                this.json = json;
                p = Json.createParser(new StringReader(json));
                ObjectFromJSON(this);
            }
        }
        UnitFromJsonModel user = new UnitFromJsonModel(model);
        return categories;
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
        this.setModified(true);
        this.setValidated(false);
    }

    public String getUnitDisc() {
        return this.unitDisc;
    }

    public void setUnitDisc(String unitDisc) {
        this.unitDisc = unitDisc;
        this.setModified(true);
        this.setValidated(false);
    }
      
    
//</editor-fold>

}
