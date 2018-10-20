package com.ic.stephen.internationaldialer.Database;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.ic.stephen.internationaldialer.Constants.DBConstants;
import com.ic.stephen.internationaldialer.Database.DBModels.Contacts;
import com.ic.stephen.internationaldialer.Database.DBModels.History;
import com.ic.stephen.internationaldialer.Database.DBModels.Logging;
import com.ic.stephen.internationaldialer.Database.DBModels.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static String qStatic_settings = "INSERT OR REPLACE INTO " + DBConstants.SETTINGS_TABLE_NAME + " (" + DBConstants.SETTINGS_COLOUMN_ID + ", " + DBConstants.SETTINGS_COLOUMN_PREFIX + ") VALUES";
    private static String qStatic_log = "INSERT OR REPLACE INTO " + DBConstants.LOGGING_TABLE_NAME + " (" + DBConstants.LOGGING_COLOUMN_ID + ", " + DBConstants.LOGGING_COLOUMN_ENABLE_LOGGING + ") VALUES";

    private String getSettingsUpdateQuery(int id, String prefix){
        return qStatic_settings + " (" + id +",'" + prefix + "')";
    }

    private String getLogSettingsUpdateQuery(int id, String enable){
        return qStatic_log + " (" + id + ",'" + enable + "')";
    }

    public DBHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, 1);
    }

    private String getDateTime(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        date = date != null ? date : new Date();
        Date d = new Date(String.valueOf(date));
        return dateFormat.format(d);
    }

    public List getAllHistory(){
        List<History> histories = new ArrayList<History>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.rawQuery("SELECT * FROM " + DBConstants.HISTORY_TABLE_NAME + " ORDER BY " + DBConstants.HISTORY_COLOUMN_DATE + " DESC", null);
        while (cr.moveToNext()){
            History history = new History();
            history.setId(cr.getInt(cr.getColumnIndex(DBConstants.HISTORY_COLOUMN_ID)));
            history.setName(cr.getString(cr.getColumnIndex(DBConstants.HISTORY_COLOUMN_NAME)));
            history.setDate(cr.getString(cr.getColumnIndex(DBConstants.HISTORY_COLOUMN_DATE)));
            history.setTimesContacted(cr.getInt(cr.getColumnIndex(DBConstants.HISTORY_COLOUMN_TIMESCONTACTED)));
            history.setType(cr.getString(cr.getColumnIndex(DBConstants.HISTORY_COLOUMN_TYPE)));
            history.setNumber(cr.getString(cr.getColumnIndex(DBConstants.HISTORY_COLOUMN_NUMBER)));
            histories.add(history);
        }
        cr.close();
        return histories;
    }

    public String getContactNameFromNumber(Context context, String number){
        String name = "unkown";
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cr = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if(cr != null && cr.moveToFirst()){
            name = cr.getString(cr.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if(cr != null && !cr.isClosed()){
            cr.close();
        }
        return name;
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

    public String getContactTypeFromNumber(Context context, String number){
        String type = "unkown";
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cr = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.TYPE}, null, null, null);
        if(cr != null && cr.moveToFirst()){
            type = determinePhoneType(cr.getInt(cr.getColumnIndex(ContactsContract.PhoneLookup.TYPE)));
        }
        if(cr != null && !cr.isClosed()){
            cr.close();
        }
        return type;
    }

    public boolean updateHistory(String name, String type, String number){
        SQLiteDatabase db = getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + DBConstants.HISTORY_TABLE_NAME + " WHERE number='" + number + "'", null);
        if(!res.moveToNext()){
            //nothing found insert
            ContentValues values = new ContentValues();
            values.put(DBConstants.HISTORY_COLOUMN_NAME, name);
            values.put(DBConstants.HISTORY_COLOUMN_NUMBER, number);
            values.put(DBConstants.HISTORY_COLOUMN_TYPE, type);
            values.put(DBConstants.HISTORY_COLOUMN_DATE, getDateTime(new Date()));
            values.put(DBConstants.HISTORY_COLOUMN_TIMESCONTACTED, 1);
            db.insert(DBConstants.HISTORY_TABLE_NAME, null, values);
        }
        else{
            //row found, increment timesContacted
            String query = "UPDATE " + DBConstants.HISTORY_TABLE_NAME + " SET timesContacted = timesContacted + 1, " + DBConstants.HISTORY_COLOUMN_DATE + " = '" + getDateTime(new Date())
                    + "' WHERE " + DBConstants.HISTORY_COLOUMN_NUMBER + " = '" + number + "'";
            db.execSQL(query);
        }
        if(res != null && !res.isClosed()){
            res.close();
        }
        return true;
    }

    public boolean isOnlyNumbers(String str){
        boolean res = false;
        if(str.matches("\\d+")){
            res = true;
        }
        return res;
    }

    public boolean updateSettings(int id, String prefix) {
        boolean res = false;
        String query = getSettingsUpdateQuery(id, prefix);
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL(query);
            res = true;
        }catch (Exception e){
            Log.v("ID", e.toString());
        }
        return res;
    }

    public Settings getSettings(){
        Settings settings = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.rawQuery("SELECT * FROM " + DBConstants.SETTINGS_TABLE_NAME, null);
        if(cr.moveToFirst()){
            settings = new Settings();
            settings.setId(cr.getInt(cr.getColumnIndex(DBConstants.SETTINGS_COLOUMN_ID)));
            settings.setPrefix(cr.getString(cr.getColumnIndex(DBConstants.SETTINGS_COLOUMN_PREFIX)));
        }
        if(cr != null && !cr.isClosed()){
            cr.close();
        }
        return settings;
    }

    public List<Contacts> getAllContacts(Activity activity) {
        Cursor cursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        List<Contacts> conts = new ArrayList<>();
        List<String> dc = new ArrayList<>();
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(!dc.contains(phoneNumber.replace(" ", "").trim())){
                conts.add(new Contacts(id, name, type, phoneNumber));
                dc.add(phoneNumber.replace(" ", "").trim());
            }
        }
        cursor.close();
        return conts;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DBConstants.SETTINGS_TABLE_NAME + "(id integer primary key, prefix text)");
        db.execSQL("CREATE TABLE " + DBConstants.HISTORY_TABLE_NAME + "(id integer primary key, name text, number text, type text, date datetime, timesContacted integer)");
        db.execSQL("CREATE TABLE " + DBConstants.LOGGING_TABLE_NAME + "(id integer primary key, logging_enabled text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.SETTINGS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.HISTORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.LOGGING_TABLE_NAME);
        onCreate(db);
    }

    public String getPrefix() {
        String res = "";
        Settings settings = getSettings();
        if(settings != null && settings.getPrefix() != null){
            res = settings.getPrefix();
        }
        return res;
    }

    public boolean isLogEnabled(){
        boolean res = false;
        Logging log = getLogSettings();
        if(log != null && log.getEnableLogging().equalsIgnoreCase("true")){
            res = true;
        }
        return res;
    }

    public Logging getLogSettings() {
        Logging log = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.rawQuery("SELECT * FROM " + DBConstants.LOGGING_TABLE_NAME, null);
        if(cr.moveToFirst()){
            log = new Logging();
            log.setId(cr.getInt(cr.getColumnIndex(DBConstants.LOGGING_COLOUMN_ID)));
            log.setEnableLogging(cr.getString(cr.getColumnIndex(DBConstants.LOGGING_COLOUMN_ENABLE_LOGGING)));
        }
        if(cr != null && !cr.isClosed()){
            cr.close();
        }
        return log;
    }

    public boolean updateLogSettings(int id, boolean enable) {
        boolean res = false;
        String query = getLogSettingsUpdateQuery(id, enable ? "true" : "false");
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL(query);
            res = true;
        }catch (Exception e){
            Log.v("Log_settings_change", e.toString());
        }
        return res;
    }

    public void clearHistory() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + DBConstants.HISTORY_TABLE_NAME);
    }
}
