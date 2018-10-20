package com.ic.stephen.internationaldialer.CustomUtils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.ic.stephen.internationaldialer.Constants.MandatoryPermissions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mew on 18-03-2017.
 */

public class PermissionManager extends AppCompatActivity {

    public boolean isAllPermissionsAvailable = false;
    public static PermissionManager permissionManager;
    private HashMap<String, Integer> permissionMap = new HashMap<>();

    private PermissionManager(){

    }

    public static PermissionManager getManager(){
        if(permissionManager != null){
            return permissionManager;
        }
        else{
            permissionManager = new PermissionManager();
        }
        return permissionManager;
    }

    private boolean isAboveMCode(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           return true;
        }
        return false;
    }

    public boolean hasPermission(Context context, String permission){
        if (isAboveMCode() && ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public boolean hasPermissions(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (!hasPermission(context, permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    private String[] getPermissionsToRequst(Context context){
        ArrayList<String> permissionToReqeust = new ArrayList<>();

        for(String permission : MandatoryPermissions.PERMISSIONS){
            if(!hasPermission(context, permission)){
                permissionToReqeust.add(permission);
            }
        }

        String[] p = new String[permissionToReqeust.size()];
        for(int i = 0; i < permissionToReqeust.size(); i++){
            p[i] = permissionToReqeust.get(i).toString();
        }
        return p;
    }

    public boolean doWeHaveAllPermissions(Context context){
       return isAllPermissionsAvailable = hasPermissions(context, MandatoryPermissions.PERMISSIONS);
    }

    public void gainPermissions(Context context) {
        String[] permissionsRequired = getPermissionsToRequst(context);
        if(!hasPermissions(context, permissionsRequired)){
            ActivityCompat.requestPermissions((Activity) context, permissionsRequired, MandatoryPermissions.PERMISSIONS_ALL);
        }
    }

    public void updatePermissionMap(String[] permissions, int[] grantResults){
        for(int i = 0; i < permissions.length; i++){
            permissionMap.put(permissions[i], grantResults[i]);
        }
        if(permissionMap.values().contains(-1)){
            isAllPermissionsAvailable = false;
        }
        else{
            isAllPermissionsAvailable = true;
        }
    }

    public boolean doWeHavePermissionFor(String task){
        return getPermissionMap().get(task) == 1 ? true : false;
    }

    public HashMap<String, Integer> getPermissionMap(){
        return permissionMap;
    }
}
