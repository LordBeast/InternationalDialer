package com.ic.stephen.internationaldialer.EventModels;

import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * Created by mew on 18-03-2017.
 */

public class PermissionResultsEvent extends BaseEventModel {
    private String[] permissions;
    private int[] grantResults;
    private boolean isAllPermissionAvailable;
    private HashMap<String, Integer> map = new HashMap<>();

    public PermissionResultsEvent(@NonNull String[] permissions, @NonNull int[] grantResults, boolean permissionsAvailable) {
        this.permissions = permissions;
        this.grantResults = grantResults;
        this.isAllPermissionAvailable = permissionsAvailable;
        updateMap();
    }

    private void updateMap() {
        map = new HashMap<>();
        for(int i = 0; i < this.permissions.length; i++){
            map.put(this.permissions[i], this.grantResults[i]);
        }
    }

    public String[] getPermissions() {
        return this.permissions;
    }

    public int[] getGrantResults(){
        return this.grantResults;
    }

    public HashMap<String, Integer> getPermissionMap(){
        return map;
    }

    public boolean isAllPermissionsAvailable(){
        return isAllPermissionAvailable;
    }
}
