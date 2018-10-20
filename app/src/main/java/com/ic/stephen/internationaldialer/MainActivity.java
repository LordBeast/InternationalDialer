package com.ic.stephen.internationaldialer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.ic.stephen.internationaldialer.CustomUtils.PermissionManager;
import com.ic.stephen.internationaldialer.Database.DBHelper;
import com.ic.stephen.internationaldialer.Database.DBModels.Settings;
import com.ic.stephen.internationaldialer.EventModels.PermissionResultsEvent;
import com.ic.stephen.internationaldialer.EventModels.TabsHolderNotVisible;
import com.ic.stephen.internationaldialer.Fragments.AboutFragment;
import com.ic.stephen.internationaldialer.Fragments.HelpFragment;
import com.ic.stephen.internationaldialer.Fragments.SettingsFragment;
import com.ic.stephen.internationaldialer.Fragments.TabsHolderFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DBHelper dbHelper;
    NavigationView navigationView;
    AdView adView;
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        adView = (AdView) findViewById(R.id.adView);
        navigationView.setNavigationItemSelectedListener(this);
        permissionManager = PermissionManager.getManager();
        init();
    }

    public void init(){
        EventBus.getDefault().register(this);
        setupAdView();
        if(!permissionManager.doWeHaveAllPermissions(this)){
            permissionManager.gainPermissions(this);
        }
        else{
            setup();
        }
    }

    @Subscribe
    public void onPermissionResultsEvent(PermissionResultsEvent permissionData){
        if(!permissionData.isAllPermissionsAvailable()){
            Toast.makeText(this, "Some Permissions not granted", Toast.LENGTH_LONG).show();
        }
        else{
            setup();
        }
    }

    private void setup(){
        dbHelper = new DBHelper(this);
        Settings settings = dbHelper.getSettings();
        if(settings == null || settings.getPrefix() == ""){
            handleFirstTimeUser();
        } else {
            goToDialer();
        }
    }

    private void setupAdView() {
        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void goToDialer() {
        Fragment fragment = new TabsHolderFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
        setTitle("Dialer");
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void handleFirstTimeUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Thank you for installing International Dialer, Please Configure Settings");
        alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment fragment = new SettingsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                EventBus.getDefault().post(new TabsHolderNotVisible());
                setTitle("Settings");
                navigationView.getMenu().getItem(1).setChecked(true);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // bring settings fragment
            Fragment fragment = new SettingsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
            EventBus.getDefault().post(new TabsHolderNotVisible());
        } else if (id == R.id.nav_tabsHolder) {
            // bring dialer fragment
            Fragment fragment = new TabsHolderFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
        } else if (id == R.id.nav_helper){
            // bring help fragment
            Fragment fragment = new HelpFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
            EventBus.getDefault().post(new TabsHolderNotVisible());
        }else if (id == R.id.nav_about) {
            // bring about fragment
            Fragment fragment = new AboutFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
            EventBus.getDefault().post(new TabsHolderNotVisible());
        }
        item.setChecked(true);
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.updatePermissionMap(permissions, grantResults);
        EventBus.getDefault().post(new PermissionResultsEvent(permissions, grantResults, permissionManager.isAllPermissionsAvailable));
        EventBus.getDefault().unregister(this);
    }
}
