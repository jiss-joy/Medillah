package com.smartechbraintechnologies.medillah.Activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Adapters.AdapterAddress;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.Models.ModelAddress;
import com.smartechbraintechnologies.medillah.R;

import java.util.ArrayList;

public class MyAddressesActivity extends AppCompatActivity implements AdapterAddress.OnOptionsClickListener {

    public static final int REQUEST_CHECK_SETTING = 1001;

    private FloatingActionButton addBTN;
    private TextView toolbar_title, noAddressText;
    private ImageButton toolbar_btn, optionsBTN;
    private RecyclerView addressRecycler;
    private LottieAnimationView noAddressAnime;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference addressRef;

    private AdapterAddress mAdapter;
    private ArrayList<ModelAddress> addressList = new ArrayList<>();
    private ArrayList<String> addressIDList = new ArrayList<>();

    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        initValues();

        loadingDialog.showLoadingDialog("Loading your addresses...");
        setUpRecycler();

        toolbar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGPS();
                startActivity(new Intent(MyAddressesActivity.this, GetLocationActivity.class));
            }
        });

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
                                resolvableApiException.startResolutionForResult(MyAddressesActivity.this, REQUEST_CHECK_SETTING);
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

    private void setUpRecycler() {
        addressRef.orderBy("addressStatus", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        addressList.clear();
                        addressIDList.clear();
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            String addressID = documentSnapshot.getId();
                            String address = documentSnapshot.getString("address");
                            String addressType = documentSnapshot.getString("addressType");
                            String addressStatus = documentSnapshot.getString("addressStatus");
                            String addressDeliveryStatus = documentSnapshot.getString("addressDeliveryStatus");
                            ModelAddress modelAddress;
                            switch (addressType) {
                                case "Home":
                                    int icon = R.drawable.home;
                                    modelAddress = new ModelAddress(icon, addressType, addressStatus, address, addressDeliveryStatus);
                                    addressList.add(modelAddress);
                                    addressIDList.add(addressID);
                                    break;
                                case "Work":
                                    modelAddress = new ModelAddress(R.drawable.work, addressType, addressStatus, address, addressDeliveryStatus);
                                    addressList.add(modelAddress);
                                    addressIDList.add(addressID);
                                    break;
                                case "Other":
                                    modelAddress = new ModelAddress(R.drawable.location, addressType, addressStatus, address, addressDeliveryStatus);
                                    addressList.add(modelAddress);
                                    addressIDList.add(addressID);
                                    break;
                            }

                            if (addressList.isEmpty()) {
                                addressRecycler.setVisibility(View.GONE);
                                noAddressAnime.setVisibility(View.VISIBLE);
                                noAddressText.setVisibility(View.VISIBLE);
                            } else {
                                addressRecycler.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyAddressesActivity.this);
                                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                                addressRecycler.setLayoutManager(linearLayoutManager);
                                mAdapter = new AdapterAddress(MyAddressesActivity.this, addressList, MyAddressesActivity.this);
                                addressRecycler.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                noAddressAnime.setVisibility(View.GONE);
                                noAddressText.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        loadingDialog.dismissLoadingDialog();
    }

    private void initValues() {
        addBTN = findViewById(R.id.my_addresses_add_btn);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_btn = findViewById(R.id.toolbar_back_btn);
        addressRecycler = (RecyclerView) findViewById(R.id.my_addresses_recycler_view);
        noAddressAnime = (LottieAnimationView) findViewById(R.id.my_addresses_no_address_anime);
        noAddressText = (TextView) findViewById(R.id.my_addresses_no_address_tv);
        optionsBTN = (ImageButton) findViewById(R.id.address_card_options_btn);

        loadingDialog = new LoadingDialog(MyAddressesActivity.this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        addressRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Addresses");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onOptionsClick(int position, View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_address_options);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_address_options_set_as_default:
                        loadingDialog.showLoadingDialog("Setting address as default...");
                        makeDefault(position);
                        return true;
                    case R.id.menu_address_options_edit_address:
                        startActivity(new Intent(MyAddressesActivity.this, GetLocationActivity.class)
                                .putExtra("Address ID", addressIDList.get(position)));
                        return true;
                    case R.id.menu_address_options_delete_address:
                        loadingDialog.showLoadingDialog("Removing address...");
                        deleteAddress(position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void deleteAddress(int position) {
        addressRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() == 1) {
                        addressRef.document(addressIDList.get(position)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingDialog.dismissLoadingDialog();
                                }
                            }
                        });
                    } else {
                        addressRef.whereEqualTo("addressStatus", "Default").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getDocuments().get(0).getId().equals(addressIDList.get(position))) {
                                        addressRef.document(task.getResult().getDocuments().get(0).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                addressRef.whereEqualTo("addressStatus", "Not Default").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            addressRef.document(task.getResult().getDocuments().get(0).getId()).update("addressStatus", "Default").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    loadingDialog.dismissLoadingDialog();
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        addressRef.document(addressIDList.get(position)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    loadingDialog.dismissLoadingDialog();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void makeDefault(int position) {
        addressRef.whereEqualTo("addressStatus", "Default").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 0) {
                        addressRef.document(addressIDList.get(position)).update("addressStatus", "Default").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingDialog.dismissLoadingDialog();
                                }
                            }
                        });
                    } else {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        addressRef.document(documentSnapshot.getId()).update("addressStatus", "Not Default");

                        addressRef.document(addressIDList.get(position)).update("addressStatus", "Default").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingDialog.dismissLoadingDialog();
                                }
                            }
                        });
                    }

                }
            }
        });
    }


}