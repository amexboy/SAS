package eitex.sas.user;

import eitex.sas.address.Address;
import eitex.sas.address.AddressFieldException;
import eitex.sas.coc.CoC;
import eitex.sas.coc.CoCFieldException;
import eitex.sas.common.ExceptionLogger;
import eitex.sas.common.ModuleTemplate;
import eitex.sas.common.NotFoundException;
import eitex.sas.role.Role;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class User extends ModuleTemplate{ 

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private CoC coc;
    private Address address;
    private ArrayList<Role> roles;


    public User() {
    }

    /**
     * Constructs an object of User. This constructor shall be used if values of
     * all the fields are known.
     *
     * @param userName
     * @param password
     * @param firstName
     * @param lastName
     * @param coc
     * @param address
     */
    public User(String userName, String password, String firstName, String lastName, CoC coc, Address address) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.coc = coc;
        this.address = address;
        this.roles = new ArrayList<>();

        this.setNew(true);
    }

    /**
     * Constructs an object of User class getting the fields from database. This
     * constructor should be used when the userCode is known and to get the rest
     * from database.
     *
     * @param userName
     * @throws eitex.sas.common.NotFoundException
     */
    public User(String userName) throws NotFoundException {
        this.userName = userName;
        User un = UserModel.getUser(userName);
        this.userName = un.userName;
        this.firstName = un.firstName;
        this.lastName = un.lastName;
        this.password = un.password;
        this.address = un.address;
        this.coc = un.coc;
        this.setNew(false);
    }

    /**
     * Saves the changes of this object to database. This method either adds a
     * new entry in the data base or updates the existing according to isNew
     * field. If the object is validated and is valid the save is executed right
     * away. Else the validate() method is called and execution continues goes
     * accordingly.
     *
     * @param userName, the user that initiated the action
     * @throws eitex.sas.user.UserFieldException
     * @return
     */
    @Override
    public boolean save(String userName) throws UserFieldException {
        if (!isValidated()) {
            this.validate();
        }
        try {
            if (this.isValid()) {
                if (isNew()) {
                    if (address.save(userName)) {
                        return UserModel.saveToDataBase(this, userName);
                    }
                } else if (isModified()) {
                    if (address.save(userName)) {
                        return UserModel.updateDataBase(this, userName);
                    }
                }
            }
        } catch (AddressFieldException ex) {
            ExceptionLogger.log(ex);
        }
        return false;
    }

    /**
     * @throws eitex.sas.user.UserFieldException
     * @return
     */
    @Override
    public boolean validate() throws UserFieldException {
        this.setValidated(true);
        this.setValid(false);
        boolean errorFound = false;

        UserFieldException ex = new UserFieldException();

        if (userName == null || userName.isEmpty() || userName.matches(".*[><=-].*") || userName.length() < 3) {
            errorFound = true;
            ex.setUserNameError(true);
        }

        if (!isNew()) {
             if (password == null || password.matches(".*[><=-].*") || (password.length() < 3 && !password.isEmpty())) {
                errorFound = true;
                ex.setPasswordError(true);
            }
        } else {
            if (password == null || password.isEmpty() || password.matches(".*[><=-].*") || password.length() < 3) {
                errorFound = true;
                ex.setPasswordError(true);
            }
        }

        if (firstName == null || firstName.isEmpty() || firstName.matches(".*[><=&%-].*") || firstName.length() < 3) {
            errorFound = true;
            ex.setFirstNameError(true);
        }

        if (lastName == null || lastName.isEmpty() || lastName.matches(".*[><=&%-].*") || lastName.length() < 3) {
            errorFound = true;
            ex.setLastNameError(true);
        }
        if (coc != null) {
            try {
                if (!coc.validate()) {
                    errorFound = true;
                    ex.setCocError(true);
                }
            } catch (CoCFieldException ex1) {
                errorFound = true;
                ex.setCocError(true);
            }
        }

        if (address != null) {
            try {
                if (!address.validate()) {
                    errorFound = true;
                    ex.setAddressError(true);
                }
            } catch (AddressFieldException ex1) {
                errorFound = true;
                ex.setAddressError(true);
            }
        }

        if (errorFound) {
            ExceptionLogger.log(ex);
            throw ex;
        }

        this.setValid(true);
        return true;
    }

    @Override
    public boolean delete(String userName) {
        if (this.isNew()) {
            return false;
        } else {
            return UserModel.delete(this, userName);
        }
    }
    
    @Override
    public boolean recover(String userName){
         if (this.isNew()) {
            return false;
        } else {
            return UserModel.recover(this, userName);
        }
    }

    /**
     * Returns all users that are registered in the database. If there is no
     * user registered in the database this method returns an empty ArrayList.
     *
     *
     * @return
     */
    public static java.util.ArrayList<User> getAllUsers() {
        return UserModel.getAllUsers(true);
    }

    /**
     * Returns all users that are deleted from the database. If there is no user
     * registered in the database this method returns an empty ArrayList.
     *
     * @return
     */
    public static java.util.ArrayList<User> getAllDeletedUsers() {
        return UserModel.getAllUsers(false);
    }

    /**
     * Checks if the give pair of userName and password match. boolean true or
     * false is returned accordingly.
     *
     * @param userName
     * @param password
     * @return
     */
    public static boolean checkLogin(String userName, String password) {
        return UserModel.checkLogin(userName, password);
    }

    public static ArrayList<User> createUserFormJSON(String model) {

        final ArrayList<User> users = new ArrayList<>();
        class UserFromJsonModel {

            public String userName = "", firstName = "", lastName = "", password = "", cocCode = "";
            public String officeBuilding = "", officeNumber = "", officePhoneNumber = "", mobilePhoneNumber = "", email = "";
            public String json;
            public JsonParser p;

            public UserFromJsonModel ObjectFromJSON(UserFromJsonModel model) {
                switch (p.next()) {
                    case END_ARRAY:
                        return model;
                    case END_OBJECT:
                        CoC coc = null;
                        try {
                            coc = new CoC(cocCode);
                        } catch (NotFoundException ex) {
                            coc = null;
                        } users.add(new User(userName, password, firstName, lastName, coc, new Address(0, officeBuilding, officeNumber, mobilePhoneNumber, officePhoneNumber, email)));
                        break;
                    case KEY_NAME:
                        String keyName = p.getString();
                        Event next = p.next();
                        try {
                            if (next == Event.VALUE_STRING) {
                                model.getClass().getField(keyName).set(model, p.getString());
                            } else if(next == JsonParser.Event.VALUE_TRUE) {
                                model.getClass().getField(keyName).set(model, true);
                            } else if(next == JsonParser.Event.VALUE_FALSE) {
                                model.getClass().getField(keyName).set(model, false);
                            }else if (next == Event.START_OBJECT) {//CoC is a sub object
                                p.next();
                                String k2 = p.getString();
//                                Logger.getLogger(User.class.getName()).log(Level.SEVERE, "coc key name {0}", k2);
                                next = p.next();
                                if (next == Event.VALUE_STRING) {
                                    cocCode = p.getString();
//                                    Logger.getLogger(User.class.getName()).log(Level.SEVERE, "coc code {0}", cocCode);
                                    p.next();//advance to cocName
                                    p.next();//advance to cocName value
                                    next = p.next();//advance to end object of 

//                                    Logger.getLogger(User.class.getName()).log(Level.SEVERE, "next (i expect END_OBJECT){0}", (next == Event.END_OBJECT));
                                } else {

                                }

                            } else {
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

            public UserFromJsonModel(String json) {
                this.json = json;
                p = Json.createParser(new StringReader(json));
                ObjectFromJSON(this);
            }
        }
        UserFromJsonModel user = new UserFromJsonModel(model);
        return users;
    }

    //<editor-fold defaultstate="collapsed" desc="Setters & Getters">
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.setModified(true);
        this.setValidated(false);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            return;
        }
        this.password = password;
        this.setModified(true);
        this.setValidated(false);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.setModified(true);
        this.setValidated(false);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.setModified(true);
        this.setValidated(false);
    }

    public CoC getCoc() {
        return coc;
    }

    public void setCoc(CoC coc) {
        this.coc = coc;
        this.setModified(true);
        this.setValidated(false);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        int id = this.address.getId();
        this.address = address;
        this.address.setId(id);
        this.setModified(true);
        this.setValidated(false);
    }

    public ArrayList<Role> getRoles() {
        return this.roles;
    }

    public boolean setRoles(ArrayList<Role> roles) {
        return UserModel.assignRoles(this, roles);
    }
//</editor-fold>

}
