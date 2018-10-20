package com.ic.stephen.internationaldialer.Database.DBModels;

/**
 * Created by Jarvis on 6/24/2016.
 */
public class Logging extends BaseDBModel {
    private int id;
    private String enableLogging;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnableLogging() {
        return enableLogging;
    }

    public void setEnableLogging(String enableLogging) {
        this.enableLogging = enableLogging;
    }
}
