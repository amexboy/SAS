package eitex.sas.role;

import eitex.sas.role.*;
import eitex.sas.address.Address;
import eitex.sas.coc.CoC;
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
 * Represents a Role. This class contains fields to represent a measurement
 * role.
 *
 * @author Amanu
 */
public class Role extends ModuleTemplate{

    private String roleCode;
    private String roleName;
    private String roleDisc;

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
        this.setNew(true);
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
        this.setNew(false);
    }

    /**
     * @param userName, the user that initiated the action
     * @throws eitex.sas.role.RoleFieldException
     * @return
     */
    @Override
    public boolean save(String userName) throws RoleFieldException {
        if (!isValidated()) {
            this.validate();
        }
        if (this.isValid()) {
            if (isNew()) {
                return RoleModel.saveToDataBase(this, userName);
            } else if (isModified()) {
                return RoleModel.updateDataBase(this, userName);
            }
        }
        return false;
    }

    /**
     * @throws eitex.sas.role.RoleFieldException
     * @return
     */
    @Override
    public boolean validate() throws RoleFieldException {
        this.setValidated(true);
        this.setValid(false);
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

        this.setValid(true);
        return true;
    }

    
    @Override
    public boolean delete(String userName) {
        if (this.isNew() || !this.isModified()) {
            return false;
        } else {
            return RoleModel.delete(this, userName);
        }
    }

    
    @Override
    public boolean recover(String userName) {
        if (this.isNew()) {
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
        this.setModified(true);
        this.setValidated(false);
    }

    public String getRoleDisc() {
        return this.roleDisc;
    }

    public void setRoleDisc(String roleDisc) {
        this.roleDisc = roleDisc;
        this.setModified(true);
        this.setValidated(false);
    }
    
//</editor-fold>

}
