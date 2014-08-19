package eitex.sas.borrow;

import eitex.sas.item.Item;
import eitex.sas.store.Store;
import eitex.sas.user.User;
import eitex.sas.user.UserFieldException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.NotFoundException;

public class Borrow {

    private int borrowCode;
    private Item item;
    private Store store;
    private User borrower;
    private User givenBy;
    private int quantity;
    private boolean returned;

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

    public Borrow() {
    }

    /**
     * Constructs an object of Borrow. This constructor shall be used if values
     * of all the fields are known.
     *
     * @param borrowCode
     * @throws eitex.sas.borrow.BorrowFieldException
     */
    public Borrow(int borrowCode, Item item, Store store, User acceptedBy, User givenBy, int quantity, boolean returned) throws BorrowFieldException, UserFieldException {
        this.borrowCode = borrowCode;
        this.item = item;
        this.store = store;
        this.quantity = quantity;
        this.borrower = acceptedBy;
        this.givenBy = givenBy;
        this.returned = returned;
        this.isNew = true;
        this.valid = this.validate();
    }

    /**
     * Constructs an object of Borrow class getting the fields from database.
     * This constructor should be used when the borrowCode is known and to get
     * the rest from database.
     *
     * @param borrowCode
     */
    public Borrow(int borrowCode) throws NotFoundException, BorrowFieldException {
        this.borrowCode = borrowCode;
        Borrow b = BorrowModel.getBorrow(borrowCode);
        this.borrower = b.borrower;
        this.givenBy = b.givenBy;
        this.item = b.item;
        this.store = b.store;
        this.quantity = b.quantity;
        this.returned = b.returned;

    }

    /**
     * Saves the changes of this object to database. This method either adds a
     * new entry in the data base or updates the existing according to isNew
     * field. If the object is validated and is valid the save is executed right
     * away. Else the validate() method is called and execution continues goes
     * accordingly.
     *
     * @throws eitex.sas.borrow.BorrowFieldException
     * @return
     */
    public boolean save() throws BorrowFieldException {
        if (!this.validated) {
            this.valid = this.validate();
        }
        if (this.valid) {
            return BorrowModel.saveToDataBase(this);
        }
        return false;
    }

    /**
     * Validates the objects fields. It performs a check to all fields. this
     * method sets validated fields to true. If there is a wrong field
     * eitex.sas.borrow.BorrowFieldException is thrown with set of boolean
     * fields to indicate which field contains error.
     *
     * @throws eitex.sas.borrow.BorrowFieldException
     * @throws eitex.sas.user.UserFieldException
     * @return
     */
    public boolean validate() throws BorrowFieldException, UserFieldException {
        this.validated = true;
        this.valid = false;
        boolean errorFound = false;

        BorrowFieldException ex = new BorrowFieldException();

        if (borrowCode < 0) {
            errorFound = true;
            ex.setBorrowCodeError(true);
        }

        if (this.store == null || !this.store.validate()) {
            ex.setStoreError(true);
            errorFound = true;
        }
        if (this.item == null || !this.item.validate()) {
            ex.setStoreError(true);
            errorFound = true;
        }
        if (this.givenBy == null || !this.givenBy.validate()) {
            ex.setStoreError(true);
            errorFound = true;
        }
        if (this.borrower == null || !this.borrower.validate()) {
            ex.setStoreError(true);
            errorFound = true;
        }

        if (this.quantity <= 0) {
            ex.setQuantityError(true);
            errorFound = true;
        }

        if (!errorFound) {
            throw ex;
        }

        this.valid = true;
        return true;
    }

    /**
     * Returns all Entries of this class that are registered in the database.
     *
     * @return
     */
    public static ArrayList<Borrow> getAllBorrows() throws BorrowFieldException {
        return BorrowModel.getAllBorrows();
    }

    /**
     * After an item is borrowed the user that borrowed the item my be required to
     * return the item according to type of the item. If the item is consumable, the
     * user will not return the item. Else if the item is a fixed asset the user should
     * return the item.
     * 
     * @return
     */
    public boolean returnBorrow() {
        if (!this.isNew && !this.returned) {
            return BorrowModel.returnBorrow(this);
        }
        return false;
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

    //<editor-fold defaultstate="collapsed" desc="Setters & Getters">
    public int getBorrowCode() {
        return borrowCode;
    }
    
    public Item getItem() {
        return item;
    }
    
    public Store getStore() {
        return store;
    }
    
    public User getBorrower() {
        return borrower;
    }
    
    public User getGivenBy() {
        return givenBy;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public boolean isReturned() {
        return returned;
    }
    
//</editor-fold>
        
}
