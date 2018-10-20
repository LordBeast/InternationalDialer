package com.ic.stephen.internationaldialer;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ic.stephen.internationaldialer.Database.DBModels.History;

import java.util.List;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class HistoryListAdapter extends CustomListAdapterBase {

    public List<History> list;

    public HistoryListAdapter(Activity activity, List<History> historyList) {
        super(activity, null);
        this.list = historyList;
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
    public void setupView(int position, View convertView, ViewGroup parent) {
        TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
        TextView phoneType = (TextView) convertView.findViewById(R.id.phone_type);
        TextView phoneNumber = (TextView) convertView.findViewById(R.id.phone_number);

        History history = list.get(position);
        contactName.setText(history.getName() + ", " + history.getTimesContacted());
        phoneType.setText(history.getType() + ", (" + history.getDate().toString().substring(0, 20) + ")");
        phoneNumber.setText(history.getNumber());
    }
}
