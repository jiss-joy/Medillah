package com.smartechbraintechnologies.medillah;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartechbraintechnologies.medillah.Activities.ProfileActivity;
import com.smartechbraintechnologies.medillah.Activities.ShoppingCartActivity;
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
    private NavigationView navigationView;
    private Button searchBTN;
    private FrameLayout cartBTN;
    private TextView cartBadgeText;

    private FirebaseFirestore db;
    private DocumentReference cartRef;
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
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        hamburger = findViewById(R.id.main_hamburger_btn);
        navigationView = findViewById(R.id.side_nav);
//        relativeLayout = findViewById(R.id.main_activity_layout);
        searchBTN = (Button) findViewById(R.id.main_activity_search_btn);
        cartBTN = (FrameLayout) findViewById(R.id.main_activity_cart_btn);
        cartBadgeText = (TextView) findViewById(R.id.item_cart_text);

        db = FirebaseFirestore.getInstance();
        cartRef = db.collection("Users").document(currentUser.getUid())
                .collection("Shopping Cart").document("Cart Quantity");
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
//        Fresco.initialize(this);

        Fresco.initialize(
                this,
                ImagePipelineConfig.newBuilder(this)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        initValues();
        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.side_nav_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
//                        case R.id.
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

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

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchEngine.class));
            }
        });

        cartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShoppingCartActivity.class));
            }
        });

        setCartBadge();
    }

    private void setCartBadge() {
        cartRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
        } else {
//            startActivity(new Intent(MainActivity.this, GetLocationActivity.class));
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