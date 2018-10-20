package com.ic.stephen.internationaldialer.Constants;

import android.Manifest;

/**
 * Created by mew on 18-03-2017.
 */

public class MandatoryPermissions {
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS
            , Manifest.permission.CALL_PHONE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int PERMISSIONS_ALL = 1;
}
