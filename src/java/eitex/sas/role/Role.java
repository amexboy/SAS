package eitex.sas.role;

import eitex.sas.role.*;
import eitex.sas.address.Address;
import eitex.sas.coc.CoC;
import eitex.sas.common.ExceptionLogger;
import eitex.sas.common.NotFoundException;
import eitex.sas.user.User;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;

/**
 * Represents a Role. This class contains fields to represent a measurement
 * role.
 *
 * @author Amanu
 */
public class Role {

    private String roleCode;
    private String roleName;
    private String roleDisc;

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

    public Role() {
    }

    /**
     * Constructs an object of Role. This constructor shall be used if
     * values of all the fields are known.
     *
     * @param roleCode
     * @param roleName
     * @param roleDisc
     */
    public Role(String roleCode, String roleName, String roleDisc) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.roleDisc = roleDisc;
        this.isNew = true;
    }

    /**
     * Constructs an object of Role class getting the fields from database.
     * This constructor should be used when the roleCode is known and to get
     * the rest from database.
     *
     * @param roleCode
     * @throws eitex.sas.common.NotFoundException
     */
    public Role(String roleCode) throws NotFoundException {
        this.roleCode = roleCode;
        Role un = RoleModel.getRole(roleCode);
        this.roleName = un.roleName;
        this.roleDisc = un.roleDisc;
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
     * @throws eitex.sas.role.RoleFieldException
     * @return
     */
    public boolean save(String userName) throws RoleFieldException {
        if (!isValidated()) {
            this.validate();
        }
        if (this.isValid()) {
            if (isNew) {
                return RoleModel.saveToDataBase(this, userName);
            } else if (modified) {
                return RoleModel.updateDataBase(this, userName);
            }
        }
        return false;
    }

    /**
     * Validates the objects fields. It performs a check to all fields. this
     * method sets validated fields to true. If there is a wrong field
     * eitex.sas.role.RoleFieldException is thrown with set of boolean
     * fields to indicate which field contains error. roleCode, and
     * roleName are required and are always validated. Other fields are
     * validated only if they exist.
     *
     * @throws eitex.sas.role.RoleFieldException
     * @return
     */
    public boolean validate() throws RoleFieldException {
        this.validated = true;
        this.valid = false;
        boolean errorFound = false;

        RoleFieldException ex = new RoleFieldException();

        if (roleCode == null || roleCode.isEmpty() || roleCode.matches(".*[><=].*") || roleCode.length() < 4) {
            errorFound = true;
            ex.setRoleCodeError(true);
        }

        if (roleName == null || roleName.isEmpty() || roleName.matches(".*[><=&%-].*") || roleName.length() < 4) {
            errorFound = true;
            ex.setRoleNameError(true);
        }

        if (roleDisc != null && !roleDisc.isEmpty()) {
//            if (roleDisc.matches(".*[><=&%-].*")) {
//                errorFound = true;
//                ex.setRoleDiscError(true);
//            }
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
     * Delete the role in the database that this object represents.
     *
     * @param userName
     * @return
     */
    public boolean delete(String userName) {
        if (this.isNew || !this.modified) {
            return false;
        } else {
            return RoleModel.delete(this, userName);
        }
    }

    /**
     * Recover the coc in the database that this object represents. This method
     * is used to recover entries of the database after deletion.
     *
     * @param userName
     * @return
     */
    public boolean recover(String userName) {
        if (this.isNew) {
            return false;
        } else {
            return RoleModel.recover(this, userName);
        }
    }

    /**
     * Returns all Entries of this class that are registered in the database.
     *
     * @return
     */
    public static java.util.ArrayList<Role> getAllRoles() {
        return RoleModel.getAllRoles(true);
    }

    /**
     * Returns all Entries of this class that are registered in the database.
     *
     * @return
     */
    public static java.util.ArrayList<Role> getAllDeletedRoles() {
        return RoleModel.getAllRoles(false);
    }

    public static ArrayList<Role> createRoleFormJSON(String model) {

        final ArrayList<Role> categories = new ArrayList<>();
        class RoleFromJsonModel {

            public String roleCode = "", roleName = "", roleDisc = "";
            public String json;
            public JsonParser p;

            public RoleFromJsonModel ObjectFromJSON(RoleFromJsonModel model) {
                switch (p.next()) {
                    case END_ARRAY:
                        return model;
                    case END_OBJECT:
                        categories.add(new Role(roleCode, roleName, roleDisc));
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

            public RoleFromJsonModel(String json) {
                this.json = json;
                p = Json.createParser(new StringReader(json));
                ObjectFromJSON(this);
            }
        }
        RoleFromJsonModel user = new RoleFromJsonModel(model);
        return categories;
    }

//<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public String getRoleCode() {
        return this.roleCode;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
        this.modified = true;
        this.validated = false;
    }

    public String getRoleDisc() {
        return this.roleDisc;
    }

    public void setRoleDisc(String roleDisc) {
        this.roleDisc = roleDisc;
        this.modified = true;
        this.validated = false;
    }
    public boolean isIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    
//</editor-fold>

}
