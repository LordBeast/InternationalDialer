package com.ic.stephen.internationaldialer.Database.DBModels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class History extends BaseDBModel {
    private int id;
    private String name;
    private String number;
    private int timesContacted;
    private Date date;
    private String type;

    public History(){

    }

    public History(String name, String number, String type, int timesContacted){
        this.name = name;
        this.number = number;
        this.timesContacted = timesContacted;
        this.type = type;
    }

    public History(int id, String name, String number, String type, int timesContacted){
        this.id = id;
        this.name = name;
        this.number = number;
        this.type = type;
        this.timesContacted = timesContacted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getTimesContacted() {
        return timesContacted;
    }

    public void setTimesContacted(int timesContacted) {
        this.timesContacted = timesContacted;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        try {
            this.date = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
