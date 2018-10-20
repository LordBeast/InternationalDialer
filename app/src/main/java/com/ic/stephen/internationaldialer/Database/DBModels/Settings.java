package com.ic.stephen.internationaldialer.Database.DBModels;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class Settings extends BaseDBModel {
    private int id;
    private String prefix;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
