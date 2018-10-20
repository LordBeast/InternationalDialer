package com.ic.stephen.internationaldialer.Database.DBModels;

import android.provider.ContactsContract;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class Contacts extends BaseDBModel {
    private String name;
    private String id;
    private String phoneNumber;
    private String imagePath;
    private String phoneType;

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(int phoneType) {
        this.phoneType = determinePhoneType(phoneType);
    }

    public Contacts(String id, String name, int phoneType, String phoneNumber){
        this.id = id;
        this.name = name;
        this.phoneType = determinePhoneType(phoneType);
        this.phoneNumber = phoneNumber;
    }

    private String determinePhoneType(int phoneType) {
        String type = "unkown";
        switch (phoneType)
        {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                type = "Mobile";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                type = "Home";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                type = "Work";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                type = "Work_Mobile";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                type = "Other";
                break;
            default:
                break;
        }
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
