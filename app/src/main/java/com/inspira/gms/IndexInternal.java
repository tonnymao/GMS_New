package com.inspira.gms;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import layout.ChangePasswordFragment;
import layout.ContactFragment;
import layout.DashboardInternalFragment;


public class IndexInternal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static GlobalVar global;
    public static JSONObject jsonObject;   //added by Tonny @30-Jul-2017
    TextView tvUsername;
    private FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_internal);

        global = new GlobalVar(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navigationHeader = navigationView.getHeaderView(0);
        tvUsername = (TextView) navigationHeader.findViewById(R.id.tvUsername);
        tvUsername.setText(LibInspira.getShared(global.userpreferences, global.user.nama, "User"));

        Context context = getApplicationContext();
        LibInspira.AddFragment(this.getSupportFragmentManager(), R.id.fragment_container, new DashboardInternalFragment());
    }

    /*private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(this, );

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }*/

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
        getMenuInflater().inflate(R.menu.activity_index_internal_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {  //added by Tonny @30-Jul-2017
            // Handle the camera action
        } else if (id == R.id.action_changepassword) {  //added by Tonny @30-Jul-2017
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ChangePasswordFragment());
        } else if (id == R.id.action_logout) {
            GlobalVar.clearDataUser();

            Intent intent = new Intent(IndexInternal.this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new DashboardInternalFragment());  //added by Tonny @01-Aug-2017
        } else if (id == R.id.nav_contact) {
            LibInspira.ReplaceFragment(getSupportFragmentManager(), R.id.fragment_container, new ContactFragment());  //added by Tonny @01-Aug-2017
        } else if (id == R.id.nav_group) {

        } else if (id == R.id.nav_salesorder) {

        } else if (id == R.id.nav_stockreport) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
