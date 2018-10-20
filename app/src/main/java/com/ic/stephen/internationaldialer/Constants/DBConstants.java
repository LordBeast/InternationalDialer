package com.ic.stephen.internationaldialer.Constants;

import android.os.Environment;

/**
 * Created by Jarvis on 6/24/2016.
 */
public class DBConstants {
    public static final String DATABASE_NAME = "InternationalDailer";
    public static final String SETTINGS_TABLE_NAME = "Settings";
    public static final String SETTINGS_COLOUMN_ID = "id";
    public static final String SETTINGS_COLOUMN_PREFIX = "prefix";

    public static final String HISTORY_TABLE_NAME = "History";
    public static final String HISTORY_COLOUMN_ID = "id";
    public static final String HISTORY_COLOUMN_NAME = "name";
    public static final String HISTORY_COLOUMN_NUMBER = "number";
    public static final String HISTORY_COLOUMN_DATE = "date";
    public static final String HISTORY_COLOUMN_TYPE = "type";
    public static final String HISTORY_COLOUMN_TIMESCONTACTED = "timesContacted";

    public static final String LOGGING_TABLE_NAME = "Logging";
    public static final String LOGGING_COLOUMN_ID = "id";
    public static final String LOGGING_COLOUMN_ENABLE_LOGGING= "logging_enabled";
    public static final String LOG_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/InternationalDialer/log.txt";
}
