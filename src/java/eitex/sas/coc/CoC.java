package eitex.sas.coc;

import eitex.sas.common.ModuleTemplate;
import eitex.sas.common.NotFoundException;
import eitex.sas.user.User;
import java.io.StringReader;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

/**
 * Represents a CoC. This class contains fields to represent a measurement coc.
 *
 * @author Amanu
 */
public class CoC extends ModuleTemplate{

    private String cocCode;
    private String cocName;
    private String cocDisc;
    private User cocHead;
    private User cocVice;

    public CoC() {
    }

    /**
     * Constructs an object of CoC. This constructor shall be used if values of
     * all the fields are known.
     *
     * @param cocCode
     * @param cocName
     * @param cocDisc
     * @param cocHead
     * @param cocVise
     */
    public CoC(String cocCode, String cocName, String cocDisc, User cocHead, User cocVise){
        this.cocCode = cocCode;
        this.cocName = cocName;
        this.cocDisc = cocDisc;
        this.cocHead = cocHead;
        this.cocVice = cocVise;
        this.setNew(true);
    } 
    
    /**
     * Constructs an object of CoC. This constructor shall be used if values of
     * all the fields are known.
     *
     * @param cocCode
     * @param cocName
     * @param cocDisc
     */
    public CoC(String cocCode, String cocName, String cocDisc){
        this.cocCode = cocCode;
        this.cocName = cocName;
        this.cocDisc = cocDisc;
        this.setNew(true);
    }

    /**
     * Constructs an object of CoC class getting the fields from database. This
     * constructor should be used when the cocCode is known and to get the rest
     * from database.
     *
     * @param cocCode
     * @throws eitex.sas.common.NotFoundException
     */
    public CoC(String cocCode) throws NotFoundException {
        this.cocCode = cocCode;
        CoC un = CoCModel.getCoC(cocCode);
        this.cocName = un.cocName;
        this.cocDisc = un.cocDisc;
        this.cocHead = un.cocHead;
        this.cocVice = un.cocVice;
        this.setNew(false);
    }

    /**
     * @param userName, the user that initiated the action
     * @throws eitex.sas.coc.CoCFieldException
     * @return
     */
    @Override
    public boolean save(String userName) throws CoCFieldException {
        if (!isValidated()) {
            this.validate();
        }
        if (this.isValid()) {
            if (isNew()) {
                return CoCModel.saveToDataBase(this, userName);
            } else if (isModified()) {
                return CoCModel.updateDataBase(this, userName);
            }
        }
        return false;
    }

    /**
     * @throws eitex.sas.coc.CoCFieldException
     * @return
     */
    @Override
    public boolean validate() throws CoCFieldException {
        this.setValidated(true);
        this.setValid(false);
        boolean errorFound = false;

        CoCFieldException ex = new CoCFieldException();
        System.out.println("cocCode: " + cocCode);
        System.out.println("cocName: " + cocName);
        if (cocCode == null || cocCode.isEmpty() || cocCode.matches(".*[><=].*") || cocCode.length() < 2) {
            errorFound = true;
            ex.setCoCCodeError(true);
        }

        if (cocName == null || cocName.isEmpty() || cocName.matches(".*[><=&%-].*") || cocName.length() < 4) {
            errorFound = true;
            ex.setCoCNameError(true);
        }

        if (cocDisc != null && !cocDisc.isEmpty()) {
//            if (cocDisc.matches(".*[=&%-].*")) {
//                errorFound = true;
//                ex.setCoCDiscError(true);
//            }

        }

        if (errorFound) {
            throw ex;
        }

        this.setValid(true);
        return true;
    }

    /**
     * @param userName
     * @return
     */
    @Override
    public boolean delete(String userName) {
        if (this.isNew()) {
            return false;
        } else {
            return CoCModel.delete(this, userName);
        }
    }
    
    /**
     * @param userName
     * @return
     */
    @Override
    public boolean recover(String userName) {
        if (this.isNew()) {
            return false;
        } else {
            return CoCModel.recover(this, userName);
        }
    }

    /**
     * @return  all the entities of this class that are registered in the database.
     */
    public static java.util.ArrayList<CoC> getAll() {
        return CoCModel.getAllCoCs(true);
        
    }
    /**
     *
     * @return all the entities of this class that are marked as deleted in the database.
     */
    public static java.util.ArrayList<CoC> getAllDeleted() {
        return CoCModel.getAllCoCs(false);
    }

    public static ArrayList<CoC> createObjectFormJSON(String model){
        String cocCode = "", cocName = "", cocDisc = "";
        ArrayList<CoC> cocs = new ArrayList<>();
        JsonParser p = Json.createParser(new StringReader(model));
        while (p.hasNext()) {
            Event next = p.next();
            if (next == Event.START_OBJECT) {
                while (p.next() != Event.END_OBJECT) {
                    if (p.getString().equals("cocCode")) {
                        p.next();
                        cocCode = p.getString();
                    } else if (p.getString().equals("cocName")) {
                        p.next();
                        cocName = p.getString();
                    } else if (p.getString().equals("cocDisc")) {
                        p.next();
                        cocDisc = p.getString();
                    }
                }
                cocs.add(new CoC(cocCode, cocName, cocDisc));
            }
        }
        return cocs;
    }

    public boolean assignHeads(User head, User vice){
        return CoCModel.assignHeads(this, head, vice);
    }
    
//<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public String getCoCCode() {
        return this.cocCode;
    }

    public String getCoCName() {
        return this.cocName;
    }

    public void setCoCName(String cocName) {
        this.cocName = cocName;
        this.setModified(true);
        this.setValidated(false);
    }

    public String getCoCDisc() {
        return this.cocDisc;
    }

    public void setCoCDisc(String cocDisc) {
        this.cocDisc = cocDisc;
        this.setModified(true);
        this.setValidated(false);
    }

    public User getCoCHead() {
        return this.cocHead;
    }

    public User getCoCVice() {
        return this.cocVice;
    }

//</editor-fold>

}
