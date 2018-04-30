package com.example.RestoApp.screens.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.RestoApp.API.zomatoApi.ZomatoApiManager;
import com.example.RestoApp.data.local.LocalStorageManager;
import com.example.RestoApp.models.RestoItem;
import com.example.RestoApp.screens.main.events.NewEventFragment;
import com.example.RestoApp.screens.main.profile.ProfileFragment;
import com.example.RestoApp.R;
import com.example.RestoApp.models.User;
import com.example.RestoApp.screens.authentication.AuthenticationActivity;
import com.example.RestoApp.screens.main.events.EventsListFragment;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.ProfileFragmentListener,
        NewEventFragment.LocationFragmentListener, EventsListFragment.EventsListFragmentListener {

    private ZomatoApiManager zomatoApiManager;
    private LocalStorageManager localStorageManager;
    private RestoListAdapter restoListAdapter;
    private TextView nameHeaderTextView, emailHeaderTextView;
    private RecyclerView restoRecyclerView;
    private Gson gson;
    private ArrayList<RestoItem> restosarr;
    private int [] restosIds = {16501671,16501398,16503727,16501468,17741694,16501730,16504804};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        zomatoApiManager = new ZomatoApiManager().newInstance();
        localStorageManager = LocalStorageManager.getInstance(this);

        getInfo(0);

        restoRecyclerView = findViewById(R.id.resto_recycler);
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

        addEventsList();
        int i;
        getInfo(restosIds[0]);
//        for(i = 0; i < restosIds.length; i++){
//           zomatoApiManager.getInfo(restosIds[i]);
//        }



    }

    public void getInfo(int res_id) {
        zomatoApiManager.getInfo(res_id).enqueue(new Callback<RestoItem>() {
            @Override
            public void onResponse(Call<RestoItem> call, Response<RestoItem> response) {
                if (response.isSuccessful()) {
                    String json = response.body().toString();

                    RestoItem items =gson.fromJson(json,RestoItem.class);
                    //showRestos();
                }
            }

            @Override
            public void onFailure(Call<RestoItem> call, Throwable t) {
            }
        });
    }

    private void showRestos(List<RestoItem> info) {

        RestoListAdapter adapter = new RestoListAdapter(info);
        restoRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        restoRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public static class RestoListAdapter extends RecyclerView.Adapter<RestoListAdapter.ViewHolder>    {

        private List<RestoItem> items;

        public RestoListAdapter(List<RestoItem> items)    {
            this.items = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restocard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Context context = holder.itemView.getContext();
            RestoItem restoItem = items.get(position);

            String title = restoItem.getName();
            holder.restoTitle.setText(title);

            String address = restoItem.getRestoInfo().getAddress();
            holder.restoAddress.setText(address);

            String price = restoItem.getAverage_cost_for_two();
            holder.restoPrice.setText(price);

            String image = restoItem.getFeatured_image();
            Picasso.with(context).load(image).into(holder.restoImage);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
        
        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView restoImage;
            TextView restoRating;
            TextView restoTitle;
            TextView restoAddress;
            TextView restoPrice;

            public ViewHolder(View itemView) {
                super(itemView);
                restoTitle = itemView.findViewById(R.id.resto_title);
                restoImage = itemView.findViewById(R.id.resto_image);
                restoRating = itemView.findViewById(R.id.resto_rating);
                restoAddress = itemView.findViewById(R.id.resto_address);
                restoPrice = itemView.findViewById(R.id.avg_price);
            }
        }
    }
}
