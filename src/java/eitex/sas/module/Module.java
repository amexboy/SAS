package eitex.sas.module;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Module {

    private int moduleCode;
    private String moduleName;
    private String moduleURL;

    private boolean isNew;
    private boolean modified;
    private Validation validation;

    public Module() {
    }

    public Module(int moduleCode) throws SQLException {
        this.moduleCode = moduleCode;
        Module c = ModuleModel.getModule(moduleCode);
        if (c == null) {
            throw new NullPointerException("Module Code was not valid...");
        }
        this.moduleName = c.moduleName;
        this.moduleURL = c.moduleURL;

        isNew = false;
        modified = false;
        this.validation = new Validation();
    }

    public Module(int moduleCode, String moduleName, String moduleURL) {
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.moduleURL = moduleURL;
        this.isNew = true;
        this.modified = false;
        this.validation = new Validation();
    }

    public Validation validate() {
        boolean errorFound = false;
        this.validation.reset();

        if (this.moduleCode < 0) {
            this.validation.CODE_ERROR = true;
            errorFound = true;
        }
        if (this.moduleName.isEmpty()) {
            this.validation.NAME_ERROR = true;
            errorFound = true;
        }
        if (this.moduleURL == null) {
            this.validation.URL_ERROR = true;
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
                return ModuleModel.saveToDataBase(this, userName);
            }
        } else if (modified) {
            //Updation code goes here...;
            if (validation.NO_ERROR) {
                return ModuleModel.updateDataBase(this, userName);
            }
        }
        return false;
    }

    public boolean delete(String userName) throws SQLException {
        if (isNew) {
            return false;
        }
        return ModuleModel.delete(this, userName);
    }

    //<editor-fold defaultstate="collapsed" desc="Getters & Setters">
    public int getModuleCode() {
        return this.moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleURL() {
        return moduleURL;
    }

    public void setModuleName(String moduleName) {
        this.modified = true;
        this.moduleName = moduleName;
    }

    public void setModuleURL(String moduleURL) {
        this.modified = true;
        this.moduleURL = moduleURL;
    }

//</editor-fold>
    public static void getModuleURLs(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
//                        out.write(file.getAbsolutePath());
                files.add(file);
            } else if (file.isDirectory()) {
                getModuleURLs(file.getAbsolutePath(), files);
            }
        }
    }

    public static ArrayList<Module> getAllModule() throws SQLException {
        return ModuleModel.getAllModule();
    }

    public class Validation {

        public boolean CODE_ERROR;
        public boolean NAME_ERROR;
        public boolean URL_ERROR;
        public boolean NO_ERROR;

        public void reset() {
            CODE_ERROR = false;
            NAME_ERROR = false;
            URL_ERROR = false;
            NO_ERROR = false;
        }

    }

}
