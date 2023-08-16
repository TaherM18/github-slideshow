package com.example.fimsdelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.fimsdelivery.global.Global;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNav;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);

        bottomNav = findViewById(R.id.bottomNav);

        //        TOOLBAR
        setSupportActionBar(toolbar);

//        NAVIGATION DRAWER MENU
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        Intent iget = getIntent();

        if ((iget.getStringExtra("orderId")) != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, new DeliveryDetailsFragment()).commit();
        }
        else if ((iget.getStringExtra("id")) != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, new DeliveriesFragment()).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, new HomeFragment()).commit();
        }

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment temp = null;

                switch (item.getItemId()) {
                    case R.id.navHome:
                        temp = new HomeFragment();
                        break;
                    case R.id.navDeliveries:
                        temp = new DeliveriesFragment();
                        break;
                    case R.id.navNotification:
                        temp = new NotificationFragment();
                        break;
                    case R.id.navProfile:
                        temp = new ProfileFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, temp).commit();

                return true;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Global.menuNavigation(item, this);
        return true;
    }
}