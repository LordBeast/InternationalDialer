package com.ic.stephen.internationaldialer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ic.stephen.internationaldialer.Fragments.ContactsFragment;
import com.ic.stephen.internationaldialer.Fragments.DialerFragment;
import com.ic.stephen.internationaldialer.Fragments.HistoryFragment;

/**
 * Created by Jarvis on 6/22/2016.
 */
public class PageAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;
    DialerFragment dialerFragment = new DialerFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    ContactsFragment contactsFragment = new ContactsFragment();

    public PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return dialerFragment;
            case 1:
                return historyFragment;
            case 2:
                return contactsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
