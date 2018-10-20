package com.ic.stephen.internationaldialer;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ic.stephen.internationaldialer.Database.DBModels.Contacts;

import java.util.List;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class ContactsListAdapter extends CustomListAdapterBase {

    public List<Contacts> list;

    public ContactsListAdapter(Activity activity, List<Contacts> contactsList){
        super(activity, null);
        this.list = contactsList;
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

        Contacts contacts = list.get(position);
        contactName.setText(contacts.getName());
        phoneType.setText(contacts.getPhoneType());
        phoneNumber.setText(contacts.getPhoneNumber());
    }
}
