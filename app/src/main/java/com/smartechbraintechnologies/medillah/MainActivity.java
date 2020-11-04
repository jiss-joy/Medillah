package com.smartechbraintechnologies.medillah;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartechbraintechnologies.medillah.Authentication.PhoneAuthActivity;
import com.smartechbraintechnologies.medillah.Fragments.ConsultationFragment;
import com.smartechbraintechnologies.medillah.Fragments.LabTestFragment;
import com.smartechbraintechnologies.medillah.Fragments.PharmacyFragment;
import com.smartechbraintechnologies.medillah.Fragments.WellnessFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment selectedFragment;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ImageButton hamburger;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private int fragmentFlag = 1;


    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_pharmacy:
                            selectedFragment = new PharmacyFragment();
                            fragmentFlag = 1;
                            break;
                        case R.id.nav_consultation:
                            selectedFragment = new ConsultationFragment();
                            fragmentFlag = 2;
                            break;
                        case R.id.nav_wellness:
                            selectedFragment = new WellnessFragment();
                            fragmentFlag = 3;
                            break;
                        case R.id.nav_lab_test:
                            selectedFragment = new LabTestFragment();
                            fragmentFlag = 4;
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    private void initValues() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        hamburger = (ImageButton) findViewById(R.id.hamburger);
//        relativeLayout = findViewById(R.id.main_activity_layout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primary));
        }

        initValues();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setSelectedItemId(R.id.nav_pharmacy);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PharmacyFragment()).commit();

        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        } else {

        }
    }

    private void exit() {
        this.finishAffinity();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentFlag == 1) {
            exit();
        } else {
            Fragment selectedFragment = new PharmacyFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_pharmacy);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
    }
}