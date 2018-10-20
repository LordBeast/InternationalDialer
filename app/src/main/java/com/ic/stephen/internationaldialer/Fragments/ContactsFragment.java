package com.ic.stephen.internationaldialer.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ic.stephen.internationaldialer.ContactsListAdapter;
import com.ic.stephen.internationaldialer.CustomUtils.PermissionManager;
import com.ic.stephen.internationaldialer.Database.DBHelper;
import com.ic.stephen.internationaldialer.Database.DBModels.Contacts;
import com.ic.stephen.internationaldialer.EventModels.PermissionResultsEvent;
import com.ic.stephen.internationaldialer.R;
import com.ic.stephen.internationaldialer.CustomUtils.FileManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class ContactsFragment extends Fragment {

    private ListView listView;
    private ContactsListAdapter adapter;
    private EditText searchContact;
    private List<Contacts> contactsList;
    private List<Contacts> searchLists;
    private final int contactsPermissionCode = 2;
    private View view;
    DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
//        this.view = view;
        dbHelper = new DBHelper(view.getContext());
        listView = (ListView) view.findViewById(R.id.list);
        searchContact = (EditText) view.findViewById(R.id.editText_searchContact);
        handlePermissionsAndProceed(view);
        return view;
    }

    private void handlePermissionsAndProceed(View view) {
        if(PermissionManager.getManager().hasPermission(getContext(), Manifest.permission.READ_CONTACTS)){
            setupContactsList(view);
            setupSearch(view);
        }
        else{
            Toast.makeText(getActivity(), "Grant Contacts Permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearch(View view) {
        searchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchString = searchContact.getText().toString();
                int textLength = searchString.length();
                searchLists.clear();
                for (int i = 0; i < contactsList.size(); i++) {
                    Contacts contact = contactsList.get(i);
                    String name = contact.getName().toLowerCase();
                    if ((textLength <= name.length() && name.toLowerCase().contains(searchString.toLowerCase())) || (contact.getPhoneNumber().contains(searchString))) {
                        searchLists.add(contact);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupContactsList(View view) {
        contactsList = getAllContacts(view);
        searchLists = new ArrayList<>(contactsList);
        adapter = new ContactsListAdapter(getActivity(), searchLists);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    Contacts contacts = (Contacts) adapter.getItem(position);
                    String number = contacts.getPhoneNumber().replace(" ", "").trim();
                    String numberToCall = dbHelper.getPrefix() + number;
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numberToCall));
                    addItemToHistory(contacts.getName(), contacts.getPhoneType(), number);
                    startActivity(intent);
                }catch (Exception e){
                    if(dbHelper.isLogEnabled()){
                        FileManager.getInstance().appendToLogFile(e.toString());
                    }
                }
            }
        });
    }

    private void addItemToHistory(String name, String type, String number) {
        name = name == "" ? "unkown" : name;
        type = type == "" ? "unkown" : type;
        dbHelper.updateHistory(name, type, number);
    }

    private List<Contacts> getAllContacts(View view) {
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
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
}
