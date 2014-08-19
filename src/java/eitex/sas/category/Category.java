package eitex.sas.category;

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
 * Represents a Category. This class contains fields to represent a measurement
 * category.
 *
 * @author Amanu
 */
public class Category extends ModuleTemplate{

    private String categoryCode;
    private String categoryName;
    private String categoryDisc;

    

    public Category() {
    }

    /**
     * Constructs an object of Category. This constructor shall be used if
     * values of all the fields are known.
     *
     * @param categoryCode
     * @param categoryName
     * @param categoryDisc
     */
    public Category(String categoryCode, String categoryName, String categoryDisc) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.categoryDisc = categoryDisc;
        this.setNew(true);
    }

    /**
     * Constructs an object of Category class getting the fields from database.
     * This constructor should be used when the categoryCode is known and to get
     * the rest from database.
     *
     * @param categoryCode
     * @throws eitex.sas.common.NotFoundException
     */
    public Category(String categoryCode) throws NotFoundException {
        this.categoryCode = categoryCode;
        Category un = CategoryModel.getCategory(categoryCode);
        this.categoryName = un.categoryName;
        this.categoryDisc = un.categoryDisc;
        this.setNew(false);
    }

    /**
     * @param userName
     * @throws eitex.sas.category.CategoryFieldException
     * @return boolean to indicate the success of the operation.
     */
    @Override
    public boolean save(String userName) throws CategoryFieldException {
        if (!isValidated()) {
            this.validate();
        }
        if (this.isValid()) {
            if (isNew()) {
                return CategoryModel.saveToDataBase(this, userName);
            } else if (isModified()) {
                return CategoryModel.updateDataBase(this, userName);
            }
        }
        return false;
    }

    /**
     * @throws eitex.sas.category.CategoryFieldException
     * @return returns a boolean to indicate if the object is valid or not.
     */
    @Override
    public boolean validate() throws CategoryFieldException {
        this.setValidated(true);
        this.setValid(false);
        boolean errorFound = false;

        CategoryFieldException ex = new CategoryFieldException();

        if (categoryCode == null || categoryCode.isEmpty() || categoryCode.matches(".*[><=].*") || categoryCode.length() < 4) {
            errorFound = true;
            ex.setCategoryCodeError(true);
        }

        if (categoryName == null || categoryName.isEmpty() || categoryName.matches(".*[><=&%-].*") || categoryName.length() < 4) {
            errorFound = true;
            ex.setCategoryNameError(true);
        }

        if (categoryDisc != null && !categoryDisc.isEmpty()) {
//            if (categoryDisc.matches(".*[><=&%-].*")) {
//                errorFound = true;
//                ex.setCategoryDiscError(true);
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
     * Delete the category in the database that this object represents.
     *
     * @param userName
     * @return returns a boolean to indicate if the object is valid or not.
     */
    @Override
    public boolean delete(String userName) {
        if (this.isNew() || !this.isModified()) {
            return false;
        } else {
            return CategoryModel.delete(this, userName);
        }
    }

    /**
     * Recover the category in the database that this object represents. This method
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
            return CategoryModel.recover(this, userName);
        }
    }

    /**
     * Returns all Entries of this class that are registered in the database.
     *
     * @return returns all categories
     */
    public static java.util.ArrayList<Category> getAll() {
        return CategoryModel.getAllCategorys(true);
    }

    /**
     * Returns all Entries of this class that are registered in the database.
     *
     * @return returns all deleted categories
     */
    public static java.util.ArrayList<Category> getAllDeleted() {
        return CategoryModel.getAllCategorys(false);
    }

    public static ArrayList<Category> createCategoryFormJSON(String model) {

        final ArrayList<Category> categories = new ArrayList<>();
        class CategoryFromJsonModel {

            public String categoryCode = "", categoryName = "", categoryDisc = "";
            public String json;
            public JsonParser p;

            public CategoryFromJsonModel ObjectFromJSON(CategoryFromJsonModel model) {
                switch (p.next()) {
                    case END_ARRAY:
                        return model;
                    case END_OBJECT:
                        categories.add(new Category(categoryCode, categoryName, categoryDisc));
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

            public CategoryFromJsonModel(String json) {
                this.json = json;
                p = Json.createParser(new StringReader(json));
                ObjectFromJSON(this);
            }
        }
        CategoryFromJsonModel user = new CategoryFromJsonModel(model);
        return categories;
    }

//<editor-fold defaultstate="collapsed" desc="Setter & Getter">
    public String getCategoryCode() {
        return this.categoryCode;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        this.setModified(true);
        this.setValidated(false);
    }

    public String getCategoryDisc() {
        return this.categoryDisc;
    }

    public void setCategoryDisc(String categoryDisc) {
        this.categoryDisc = categoryDisc;
        this.setModified(true);
        this.setValidated(false);
    }
      
    
//</editor-fold>

}
