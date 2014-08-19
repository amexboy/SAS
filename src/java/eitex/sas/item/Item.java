package eitex.sas.item;

import eitex.sas.category.Category;
import eitex.sas.category.CategoryFieldException;
import eitex.sas.common.ExceptionLogger;
import eitex.sas.store.Store;
import eitex.sas.unit.Unit;
import eitex.sas.unit.UnitFieldException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import eitex.sas.common.NotFoundException;

public class Item {

    private String itemCode;
    private String itemName;
    private Unit unit;
    private Category category;
    private String itemDisc;

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
     * Constructs an object of Item class. This constructor shall be used if
     * values of all the fields are known.
     *
     *
     * @param itemCode
     * @param unit
     * @param category
     * @param itemName
     * @param itemDisc
     * @throws eitex.sas.item.ItemFieldException
     */
    public Item(String itemCode, Unit unit, Category category, String itemName, String itemDisc) throws ItemFieldException {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.unit = unit;
        this.category = category;
        this.itemDisc = itemDisc;
        this.isNew = true;
        this.valid = this.validate();
    }
 
    public Item() {
    }

    /**
     * Constructs an object of Item class getting the fields from database. This
     * constructor should be used when the categoryCode is known and to get the
     * rest from database.
     *
     * @param itemCode
     */
    public Item(String itemCode) throws NotFoundException, ItemFieldException {
        this.itemCode = itemCode;
        Item ca = ItemModel.getItem(itemCode);
        this.itemName = ca.itemName;
        this.itemDisc = ca.itemDisc;
        this.unit = ca.unit;
        this.category = ca.category;
        this.isNew = false;
    }

    public static ArrayList<Item> getAllItems() throws ItemFieldException {
        return ItemModel.getAllItems();
    }

    /**
     * Validates the objects fields. It performs a check to all fields. this
     * method sets validated fields to true. If there is a wrong field
     * eitex.sas.item.ItemFieldException is thrown with set of boolean fields to
     * indicate which field contains error. categoryCode, and categoryName are
     * required and are always validated. Other fields are validated only if
     * they exist.
     *
     * @throws eitex.sas.item.ItemFieldException
     * @return
     */
    public boolean validate() throws ItemFieldException{
        this.validated = true;
        this.valid = false;
        boolean errorFound = false;

        ItemFieldException ex = new ItemFieldException();

        if (itemCode == null || itemCode.isEmpty() || itemCode.matches(".*[><=].*") || itemCode.length() < 4) {
            errorFound = true;
            ex.setItemCodeError(true);
        }

        if (itemName == null || itemName.isEmpty() || itemName.matches(".*[><=&%-].*") || itemName.length() < 4) {
            errorFound = true;
            ex.setItemNameError(true);
        }

        try {
            if (category == null || !category.validate()) {
                errorFound = true;
                ex.setCategoryError(true);
            }
        } catch (CategoryFieldException ex1) {
            ExceptionLogger.log(ex);
            errorFound = true;
            ex.setCategoryError(true);
        }

        try {
            if (unit == null || !unit.validate()) {
                errorFound = true;
                ex.setUnitError(true);
            }
        } catch (UnitFieldException ex1) {
            ExceptionLogger.log(ex);
            errorFound = true;
            ex.setUnitError(true);
        }

        if (itemDisc != null && !itemDisc.isEmpty()) {
            if (itemDisc.matches(".*[><=&%-].*")) {
                errorFound = true;
                ex.setItemDiscError(true);
            }
        }

        if (errorFound) {
            throw ex;
        }

        this.valid = true;

        return true;
    }

    public boolean save(String userName) throws ItemFieldException {
        if (!validated) {
            this.valid = this.validate();
        }
        if (valid) {
            if (isNew) {
                return ItemModel.saveToDataBase(this, userName);
            } else if (modified) {
                return ItemModel.updateDataBase(this, userName);
            }
        }
        return false;
    }

    /**
     * Delete the item in the database that this object represents.
     *
     * @param userName
     * @return
     */
    public boolean delete(String userName){
        if (isNew || !modified) {
            return false;
        }
        return ItemModel.delete(this, userName);
    }

    public HashMap<Store, Integer> getAllQuntities() throws SQLException {
        return ItemModel.getAllQuntities(this);
    }

    public int getTotalInAllStores() throws SQLException {
        return ItemModel.getTotalInAllStores(this);
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

    //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public String getItemCode() {
        return this.itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDisc() {
        return itemDisc;
    }

    public Unit getUnit() {
        return unit;
    }

    public Category getCategory() {
        return category;
    }

    public void setUnit(Unit unit) {
        this.modified = true;
        this.validated = false;
        this.unit = unit;
    }

    public void setCategory(Category category) {
        this.modified = true;
        this.validated = false;
        this.category = category;
    }

    public void setItemName(String itemName) {
        this.modified = true;
        this.validated = false;
        this.itemName = itemName;
    }

    public void setItemDisc(String itemDisc) {
        this.modified = true;
        this.validated = false;
        this.itemDisc = itemDisc;
    }

//</editor-fold>
}
