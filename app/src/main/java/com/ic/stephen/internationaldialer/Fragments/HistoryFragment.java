package com.ic.stephen.internationaldialer.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ic.stephen.internationaldialer.Database.DBHelper;
import com.ic.stephen.internationaldialer.Database.DBModels.History;
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
public class HistoryFragment extends Fragment {
    private DBHelper dbHelper;
    private ListView listView;
    private HistoryListAdapter adapter;
    private List<History> historyList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        dbHelper = new DBHelper(view.getContext());
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        listView = (ListView) view.findViewById(R.id.history_list);
        return view;
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
                            String numberToCall = dbHelper.getPrefix().concat(number);
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numberToCall));
                            addItemToHistory(history.getName(), history.getType(), number);
                            renderHistories(view);
                            startActivity(intent);
                        }catch (Exception e){
                            if(dbHelper.isLogEnabled()){
                                FileManager.getInstance().appendToLogFile(e.toString());
                            }
                        }
                    }
                });
            }
        }, 300);
    }

    private void addItemToHistory(String name, String type, String number) {
        name = name == "" ? "unkown" : name;
        type = type == "" ? "unkown" : type;
        dbHelper.updateHistory(name, type, number);
    }

    @Subscribe
    public void onTabChangedEvent(TabChangedEvent tabChangedEvent){
        if(tabChangedEvent.getTabName() == "History"){
            renderHistories(getView());
        }
    }

    @Subscribe
    public void onTabsHolderNotVisible(TabsHolderNotVisible tabsHolderNotVisible){
        EventBus.getDefault().unregister(this);
    }
}
