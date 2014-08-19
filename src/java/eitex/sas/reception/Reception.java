package eitex.sas.reception;

import eitex.sas.category.Category;
import eitex.sas.item.Item;
import eitex.sas.item.ItemModel;
import eitex.sas.store.Store;
import eitex.sas.unit.Unit;
import eitex.sas.user.User;
import java.sql.SQLException;
import java.util.ArrayList;

public class Reception {

    private String receiptCode;
    private Item item;
    private Store store;
    private User acceptedBy;
    private int quantity;

    private boolean isNew;
    private Validation validation;

    public Reception(String receiptCode, Item item, Store store, User acceptedBy, int quantity) {
        this.receiptCode = receiptCode;
        this.item = item;
        this.store = store;
        this.quantity = quantity;
        this.acceptedBy = acceptedBy;
        this.isNew = true;
        this.validation = new Validation();
    }

    public Reception() {
    }

    public Reception(String receiptCode) throws SQLException {
        this.receiptCode = receiptCode;
        Reception r = ReceptionModel.getReception(receiptCode);

        if (r == null) {
            throw new NullPointerException("Reception is not Found In the Database");
        }
        this.item = r.item;
        this.quantity = r.quantity;
        this.store = r.store;
        this.acceptedBy = r.acceptedBy;
        this.isNew = false;
        this.validation = new Validation();
    }

    public static ArrayList<Reception> getAllReceptions() throws SQLException {
        return ReceptionModel.getAllReceptions();
    }

    public Reception.Validation validate() {
        boolean errorFound = false;
        this.validation.reset();

        if (this.receiptCode.isEmpty()) {
            this.validation.CODE_ERROR = true;
            errorFound = true;
        }

        if (!this.item.validate().NO_ERROR) {
            this.validation.ITEM_ERROR = true;
            errorFound = true;
        }

        if (!this.store.validate().NO_ERROR) {
            this.validation.STORE_ERROR = true;
            errorFound = true;
        }

        if (!this.acceptedBy.validate().NO_ERROR) {
            this.validation.USER_ERROR = true;
            errorFound = true;
        }

        if (this.quantity <= 0) {
            this.validation.QUANTITY_ERROR = true;
            errorFound = true;
        }

        if (!errorFound) {
            this.validation.NO_ERROR = true;
        }

        return this.validation;
    }

    public boolean save(String userName) throws SQLException {
        if (isNew) {
            if (validation.NO_ERROR) {
                return ReceptionModel.saveToDataBase(this, userName);
            }
        }
        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public String getReceiptCode() {
        return receiptCode;
    }

    public Item getItem() {
        return item;
    }

    public Store getStore() {
        return store;
    }

    public User getAcceptedBy() {
        return acceptedBy;
    }

    public int getQuantity() {
        return quantity;
    }

    //</editor-fold>
    public class Validation {

        public boolean CODE_ERROR;
        public boolean STORE_ERROR;
        public boolean USER_ERROR;
        public boolean ITEM_ERROR;
        public boolean QUANTITY_ERROR;
        public boolean NO_ERROR;

        public void reset() {
            CODE_ERROR = false;
            STORE_ERROR = false;
            USER_ERROR = false;
            ITEM_ERROR = false;
            QUANTITY_ERROR = false;
            NO_ERROR = false;
        }

    }
}
