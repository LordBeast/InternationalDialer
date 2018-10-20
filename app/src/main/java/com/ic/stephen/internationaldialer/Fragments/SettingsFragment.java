package com.ic.stephen.internationaldialer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ic.stephen.internationaldialer.Database.DBHelper;
import com.ic.stephen.internationaldialer.R;

import org.w3c.dom.Text;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class SettingsFragment extends Fragment {

    private Button saveSettings;
    private EditText prefix;
    private Switch enableLogging;
    private Button clearHistory;
    private Button removeAds;
    private TextView logQuestionMark;

    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        saveSettings = (Button) view.findViewById(R.id.button_saveSettings);
        prefix = (EditText) view.findViewById(R.id.editText_prefix);
        enableLogging = (Switch) view.findViewById(R.id.switch_logging);
        clearHistory = (Button) view.findViewById(R.id.button_clearHistory);
//        removeAds = (Button) view.findViewById(R.id.button_removeAds);
        logQuestionMark = (TextView) view.findViewById(R.id.textView_logQuestionMark);

        dbHelper = new DBHelper(view.getContext());

        prefix.setText(dbHelper.getPrefix());
        boolean enabled = dbHelper.isLogEnabled();
        enableLogging.setChecked(enabled);

        enableLogging.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbHelper.updateLogSettings(1, isChecked);
            }
        });

        logQuestionMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Log stored : InternationalDialer/log.txt", Toast.LENGTH_LONG).show();
            }
        });

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.clearHistory();
            }
        });

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = prefix.getText().toString().trim();
                if (p.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "Provide Prefix", Toast.LENGTH_SHORT).show();
                } else if (!validatePrefix(p)){
                    Toast.makeText(getActivity().getApplicationContext(), "Malformed Prefix", Toast.LENGTH_SHORT).show();
                }else if (dbHelper.updateSettings(1, p)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public boolean validatePrefix(String prefix){
//        boolean res = true;
//        String str = new String(prefix);

//        if(str.charAt(str.length() - 1) != ','){
//            res = false;
//            return res;
//        }
//
//        while(str.length() > 0 && str.charAt(str.length() - 1) == ','){
//            str = str.substring(0, str.length() - 1);
//        }
//
//
//
//        if (str.equals("")) {
//            res = false;
//            return res;
//        }

        //[+0]+[\d,]+[\d]+[,]
//        res = prefix.matches("[+0]+[\\d,]+[\\d]+[,]");

        return prefix.matches("[+0]+[\\d,*]+[\\d]+[,]+[,]*");
    }
}
