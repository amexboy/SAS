package eitex.sas.store;

import eitex.sas.address.Address;
import java.sql.SQLException;
import java.util.ArrayList;

public class Store {

    private String storeCode;
    private String storeName;
    private String storeDisc;
    private Address address;

    private boolean isNew = false;
    private boolean modified = false;

    private Validation validation = new Validation();

    public Store() {
    }

    public Store(String storeCode) throws SQLException {
        this.storeCode = storeCode;
        Store s = StoreModel.getStore(storeCode);
        if (s != null) {
            this.storeName = s.storeName;
            this.storeDisc = s.storeDisc;
            isNew = false;
            this.address = s.address;
        } else {
            throw new NullPointerException("Store is not found!");
        }
    }

    public Store(String storeCode, String storeName, String storeDisc, Address address) {
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.storeDisc = storeDisc;
        this.isNew=true;
        this.address = address;
    }

    

    public Validation validate() {
        this.validation.reset();

        boolean noError = true;
        if (storeCode.isEmpty()) {
            noError = false;
            this.validation.STORE_CODE_ERROR = true;
        }
        if (storeName.isEmpty() ) {
            noError = false;
            this.validation.STORE_NAME_ERROR = true;
        }
        if (storeDisc.isEmpty()) {
            noError = false;
            this.validation.STORE_DISC_ERROR = true;
        }
        if (!address.validate().NO_ERROR) {
            noError = false;
            this.validation.ADDRESS_ERROR = true;
        }
        if (noError) {
            this.validation.NO_ERROR = true;
        }
        return this.validation;
    }

    public boolean save(String userName) throws SQLException {
        if (validation.NO_ERROR) {
            if (isNew) {
                if (address.save(userName)) {
                    return StoreModel.saveToDataBase(this, userName);
                }
            } else if (modified) {
                if (address.save(userName)) {
                    return StoreModel.updateDataBase(this, userName);
                }
            }
        }
        return false;
    }

    public boolean delete(String storeName) throws SQLException {
        if (isNew) {
            return false;
        }
        return StoreModel.delete(this, storeName);
    }

    public static ArrayList<Store> getAllStores() throws SQLException {
        return StoreModel.getAllStores();
    }

    //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public String getStoreCode() {
        return storeCode;
    }
    
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.modified = true;
        this.storeName = storeName;
    }

    public String getStoreDisc() {
        return storeDisc;
    }

    public void setStoreDisc(String storeDisc) {
        this.modified = true;
        this.storeDisc = storeDisc;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.modified = true;
        this.address = address;
    }

    //</editor-fold>
    
    public class Validation {

        public boolean NO_ERROR;
        public boolean STORE_CODE_ERROR;
        public boolean STORE_NAME_ERROR;
        public boolean STORE_DISC_ERROR;
        public boolean ADDRESS_ERROR;

        public void reset() {
            NO_ERROR = false;
            STORE_CODE_ERROR = false;
            STORE_NAME_ERROR = false;
            STORE_DISC_ERROR = false;
            ADDRESS_ERROR = false;
        }

    }

}
