package com.ic.stephen.internationaldialer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.ic.stephen.internationaldialer.Database.DBModels.Contacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class CustomContactsAutoCompleteAdapter extends ArrayAdapter<Contacts> {

    Context context;
    int layoutResourceId;
    List<Contacts> contactsList, tempList, suggestions;

    public CustomContactsAutoCompleteAdapter(Context context, int resource, List<Contacts> contactsList) {
        super(context, resource, contactsList);
        this.context = context;
        this.layoutResourceId = resource;
        this.contactsList = contactsList;
        tempList = new ArrayList<Contacts>(contactsList);
        suggestions = new ArrayList<Contacts>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try{
            if(convertView == null){
                LayoutInflater inflater = ((MainActivity) context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }
            Contacts contact = contactsList.get(position);
            TextView contactName = (TextView) convertView.findViewById(R.id.autocomplete_contact_name);
            TextView phoneNumber = (TextView) convertView.findViewById(R.id.autocomplete_phone_number);
            TextView phoneType = (TextView) convertView.findViewById(R.id.autocomplete_phone_type);

            contactName.setText(contact.getName());
            phoneNumber.setText(contact.getPhoneNumber());
            phoneType.setText(contact.getPhoneType());
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return  convertView;
    }

    @Override
    public Filter getFilter() {
        return new SearchFilter();
    }

    class SearchFilter extends Filter{

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Contacts) resultValue).getPhoneNumber().replace(" ", "").trim();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null){
                suggestions.clear();
                for(Contacts contact : tempList){
                    if(contact.getName().toLowerCase().contains(constraint.toString().toLowerCase()) || contact.getPhoneNumber().replace(" ", "").trim().contains(constraint.toString())){
                        suggestions.add(contact);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Contacts> contacts = (List<Contacts>) results.values;
            if(results != null && results.count > 0){
                clear();
                contactsList.addAll(contacts);
                notifyDataSetChanged();
            }
        }
    }
}
