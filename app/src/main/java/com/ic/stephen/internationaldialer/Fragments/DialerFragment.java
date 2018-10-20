package com.ic.stephen.internationaldialer.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ic.stephen.internationaldialer.CustomContactsAutoCompleteAdapter;
import com.ic.stephen.internationaldialer.CustomUtils.PermissionManager;
import com.ic.stephen.internationaldialer.Database.DBHelper;
import com.ic.stephen.internationaldialer.Database.DBModels.Contacts;
import com.ic.stephen.internationaldialer.Database.DBModels.History;
import com.ic.stephen.internationaldialer.Database.DBModels.Settings;
import com.ic.stephen.internationaldialer.EventModels.PermissionResultsEvent;
import com.ic.stephen.internationaldialer.EventModels.TabChangedEvent;
import com.ic.stephen.internationaldialer.EventModels.TabsHolderNotVisible;
import com.ic.stephen.internationaldialer.HistoryListAdapter;
import com.ic.stephen.internationaldialer.R;
import com.ic.stephen.internationaldialer.CustomUtils.FileManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class DialerFragment extends Fragment {
    private DBHelper dbHelper;
    private Button dial;
    private AutoCompleteTextView autoCompleteView;
    private ListView listView;
    private HistoryListAdapter adapter;
    private List<Contacts> contactsList;
    private CustomContactsAutoCompleteAdapter autoCompleteAdapter;
    private List<History> historyList;
    private View view;
    private PermissionManager permissionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialer, container, false);
        this.view = view;
        dbHelper = new DBHelper(view.getContext());
        permissionManager = PermissionManager.getManager();
        setup(view);
        renderHistories(view);
        return view;
    }

    private void handlePermissionsAndProceed(View view) {
        if(!permissionManager.hasPermission(view.getContext(), Manifest.permission.READ_CONTACTS)){
            Toast.makeText(getActivity(), "Grant Contacts Permission", Toast.LENGTH_SHORT).show();
        }
        else{
            setupAutoComplete(view);
        }
    }


    private void setup(View view) {
        setupUIElements(view);
        handlePermissionsAndProceed(view);
//        setupAutoComplete(view);
    }

    private void setupAutoComplete(View view) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoCompleteView.setThreshold(2);
                contactsList = dbHelper.getAllContacts(getActivity());
                autoCompleteAdapter = new CustomContactsAutoCompleteAdapter(getActivity(), R.layout.auto_complete, contactsList);
                autoCompleteView.setAdapter(autoCompleteAdapter);
            }
        }, 300);
    }

    private void setupUIElements(View view) {
        dial = (Button) view.findViewById(R.id.button_dail);
        autoCompleteView = (AutoCompleteTextView) view.findViewById(R.id.autoComplete_phoneNumber);
        listView = (ListView) view.findViewById(R.id.dailer_history_list);

        dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoCompleteView.getText().toString().equals("")) {
                    return;
                }
                callNumber(getActivity(), autoCompleteView.getText().toString());
            }
        });

        autoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contacts contact = contactsList.get(position);
                callNumber(getActivity(), contact.getPhoneNumber().replace(" ", "").trim());
                autoCompleteView.setText("");
            }
        });
    }

    private void renderHistories(View view) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (historyList != null && !historyList.isEmpty()) {
                    historyList.clear();
                    adapter.notifyDataSetChanged();
                    adapter = null;
                }
                historyList = dbHelper.getAllHistory();
                adapter = new HistoryListAdapter(getActivity(), historyList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try{
                            History history = (History) adapter.getItem(position);
                            String number = history.getNumber();
                            callNumber(getActivity(), number);
//                            String numberToCall = dbHelper.getPrefix().concat(number);
//                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numberToCall));
//                            addItemToHistory(history.getName(), history.getType(), number);
//                            startActivity(intent);
                        }catch (Exception e){
                            if(dbHelper.isLogEnabled()){
                                FileManager.getInstance().appendToLogFile(e.toString());
                            }
                        }
                    }
                });
            }
        }, 200);
    }

    private void addItemToHistory(String name, String type, String number){
        name = name == "" ? "unkown" : name;
        type = type == "" ? "unkown" : type;
        dbHelper.updateHistory(name, type, number);
        renderHistories(getView());
    }

    public void callNumber(Activity activity, String number) {
        if(permissionManager.hasPermission(getContext(), Manifest.permission.CALL_PHONE)){
            String numberToCall = number.replaceAll("[^0-9]+", "").replace("+", "");

            if (!dbHelper.isOnlyNumbers(numberToCall)) {
                Toast.makeText(activity.getApplicationContext(), "Malformed Number", Toast.LENGTH_SHORT).show();
                return;
            }
            Settings settings = dbHelper.getSettings();
            try {
                if (settings != null) {
                    numberToCall = settings.getPrefix() + number;
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Prefix not mentioned", Toast.LENGTH_SHORT).show();
                }
                addItemToHistory(number);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numberToCall));
                startActivity(intent);
            } catch (Exception e) {
                if (dbHelper.isLogEnabled()) {
                    FileManager.getInstance().appendToLogFile(e.toString());
                }
            }
        }
        else{
            Toast.makeText(activity.getApplicationContext(), "Grant calling permission", Toast.LENGTH_LONG).show();
        }
    }

    private void addItemToHistory(String number) {
        String name = dbHelper.getContactNameFromNumber(getActivity().getApplicationContext(), number);
        String type = dbHelper.getContactTypeFromNumber(getActivity().getApplicationContext(), number);
        dbHelper.updateHistory(name, type, number);
        renderHistories(getView());
    }

    @Subscribe
    public void onTabChangedEvent(TabChangedEvent tabChangedEvent){
        if(tabChangedEvent.getTabName() == "Dialer"){
            renderHistories(getView());
        }
    }

    @Subscribe
    public void onTabsHolderNotVisible(TabsHolderNotVisible tabsHolderNotVisible){
        EventBus.getDefault().unregister(this);
    }
}
