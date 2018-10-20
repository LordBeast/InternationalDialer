package com.ic.stephen.internationaldialer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ic.stephen.internationaldialer.Database.DBModels.BaseDBModel;

import java.util.List;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class CustomListAdapterBase extends BaseAdapter {

    public Activity context;
    public List<BaseDBModel> list;
    public LayoutInflater layoutInflater;

    public CustomListAdapterBase(Activity activity, List<BaseDBModel> list){
        this.context= activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setupView(int position, View converView, ViewGroup parent){

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(layoutInflater == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.list_row, null);
        }
        setupView(position, convertView, parent);
        return convertView;
    }
}
