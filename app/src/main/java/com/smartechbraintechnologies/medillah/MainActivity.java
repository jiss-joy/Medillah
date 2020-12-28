package com.smartechbraintechnologies.medillah;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smartechbraintechnologies.medillah.Activities.ContactUsActivity;
import com.smartechbraintechnologies.medillah.Activities.MyAppointmentsActivity;
import com.smartechbraintechnologies.medillah.Activities.MyLabTestsActivity;
import com.smartechbraintechnologies.medillah.Activities.MyOrdersActivity;
import com.smartechbraintechnologies.medillah.Activities.ProfileActivity;
import com.smartechbraintechnologies.medillah.Authentication.PhoneAuthActivity;
import com.smartechbraintechnologies.medillah.Fragments.ConsultationFragment;
import com.smartechbraintechnologies.medillah.Fragments.LabTestFragment;
import com.smartechbraintechnologies.medillah.Fragments.PharmacyFragment;
import com.smartechbraintechnologies.medillah.Network.ConnectivityReceiver;
import com.smartechbraintechnologies.medillah.Network.MyApp;
import com.smartechbraintechnologies.medillah.Network.NoNetworkActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    public static final int REQUEST_CHECK_SETTING = 1001;

    private Fragment selectedFragment;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ImageButton hamburger;
    private NavigationView navigationView;
    private Button searchBTN;
    private CircleImageView profilePic;
    private TextView name, number, gender, dob, blood;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private LocationRequest locationRequest;

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
        searchBTN = (Button) findViewById(R.id.main_activity_search_btn);

        db = FirebaseFirestore.getInstance();
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

        Fresco.initialize(
                this,
                ImagePipelineConfig.newBuilder(this)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        initValues();
        setSupportActionBar(toolbar);


        View view = navigationView.getHeaderView(0);
        loadSideNavData(view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.side_nav_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                    case R.id.side_nav_appointments:
                        startActivity(new Intent(MainActivity.this, MyAppointmentsActivity.class));
                        break;
                    case R.id.side_nav_lab_tests:
                        startActivity(new Intent(MainActivity.this, MyLabTestsActivity.class));
                        break;
                    case R.id.side_nav_orders:
                        startActivity(new Intent(MainActivity.this, MyOrdersActivity.class));
                        break;
                    case R.id.side_nav_contact_us:
                        startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                        break;
                    case R.id.side_nav_privacy_policy:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vQR5E0-H9OD1N0Hp9UxhAcC0OUXMJ6HSI2DJMH9qCVPcaUF6uQwJ-AWV0HqSKe4-MDmTyIBQXrGl2De/pub")));
                        break;
                    case R.id.side_nav_terms_and_conditions:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/e/2PACX-1vSFtKDl9mE_hMMfbK_9Ge6CWlO8inkLlP_W-ZeMmv1owuSqAa-vF5IV4MJIdZ_0zuhB-QbyB8aYH9JS/pub")));
                        break;
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
    }

    private void loadSideNavData(View view) {
        profilePic = (CircleImageView) view.findViewById(R.id.nav_header_profile_pic);
        name = (TextView) view.findViewById(R.id.nav_header_user_name);
        number = (TextView) view.findViewById(R.id.nav_header_phone_number);
        gender = (TextView) view.findViewById(R.id.nav_header_gender);
        dob = (TextView) view.findViewById(R.id.nav_header_dob);
        blood = (TextView) view.findViewById(R.id.nav_header_blood_group);

        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        String image = value.getString("userImage");
                        if (!image.equals("")) {
                            Picasso.get().load(image).into(profilePic);
                        }
                        name.setText(value.getString("userName"));
                        number.setText(value.getString("userPhone"));
                        blood.setText(value.getString("userBloodGroup") + "ve");
                        gender.setText(value.getString("userGender"));
                        dob.setText(value.getString("userDay") + "/" + value.getString("userMonth") + "/"
                                + value.getString("userYear"));
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            finish();
            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
        } else {
            requestGPS();
        }
    }

    private void requestGPS() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTING);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        MyApp.getInstance().setConnectivityListener(this);

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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            startActivity(new Intent(this, NoNetworkActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTING) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    break;
                case Activity.RESULT_CANCELED:
            }
        }
    }
}