package com.ic.stephen.internationaldialer.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ic.stephen.internationaldialer.Database.DBHelper;
import com.ic.stephen.internationaldialer.EventModels.TabChangedEvent;
import com.ic.stephen.internationaldialer.PageAdapter;
import com.ic.stephen.internationaldialer.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class TabsHolderFragment extends Fragment {

    DBHelper dbHelper;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabsholder, container, false);
        dbHelper = new DBHelper(view.getContext());
        setup(view);
        return view;
    }

    private void setup(View view) {
        setupTabs(view);
        setupViewPager(view);
    }

    private void setupViewPager(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        pageAdapter = new PageAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                EventBus.getDefault().post(new TabChangedEvent(tab.getText().toString()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupTabs(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Dialer"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }
}
