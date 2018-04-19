package com.example.myevents.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.myevents.R;
import com.example.myevents.data.local.LocalStorageManager;
import com.example.myevents.models.User;
import com.example.myevents.screens.authentication.AuthenticationActivity;
import com.example.myevents.screens.main.events.EventsListFragment;
import com.example.myevents.screens.main.events.NewEventFragment;
import com.example.myevents.screens.main.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.ProfileFragmentListener,
        NewEventFragment.LocationFragmentListener, EventsListFragment.EventsListFragmentListener {

    private LocalStorageManager localStorageManager;
    private TextView nameHeaderTextView, emailHeaderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        localStorageManager = LocalStorageManager.getInstance(this);

        NavigationView navigationView1 = findViewById(R.id.nav_view);
        View header = navigationView1.getHeaderView(0);
        nameHeaderTextView = header.findViewById(R.id.name);
        emailHeaderTextView = header.findViewById(R.id.email);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addEventsList();
        populateHeaderViews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_events:
                addEventsList();
                break;

            case R.id.nav_logout:
                logout();
                break;

            case R.id.nav_profile:
                showProfileFragment();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addEventsList() {
        setTitle(getString(R.string.title_events));
        EventsListFragment fragment = EventsListFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void logout() {
        LocalStorageManager.getInstance(this).deleteUser();
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProfileFragment() {
        setTitle(getString(R.string.title_profile));
        ProfileFragment fragment = ProfileFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }

    private void populateHeaderViews() {
        User user = localStorageManager.getUser();
        if (user != null) {
            nameHeaderTextView.setText(user.getName());
            emailHeaderTextView.setText(user.getEmail());
        }
    }

    private void gotoAuthenticationScreen() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onProfileFetchFailure() {
        gotoAuthenticationScreen();
    }

    @Override
    public void onRequestCreateNewEvent() {
        setTitle(getString(R.string.title_create_event));
        NewEventFragment fragment = NewEventFragment.newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(NewEventFragment.TAG).commit();
    }

    @Override
    public void onErrorFetchingEvents() {
        gotoAuthenticationScreen();
    }

    @Override
    public void onNewEvertCreatedSuccessfully() {
        setTitle(getString(R.string.title_events));
        getFragmentManager().popBackStack();
    }

    @Override
    public void onNewEvertCreationFailure() {
        gotoAuthenticationScreen();
    }
}
