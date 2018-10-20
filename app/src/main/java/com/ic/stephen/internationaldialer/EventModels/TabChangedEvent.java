package com.ic.stephen.internationaldialer.EventModels;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class TabChangedEvent extends BaseEventModel {
    private String tabName = "";

    public TabChangedEvent(String text) {
        tabName = text;
    }

    public String getTabName() {
        return tabName;
    }
}
