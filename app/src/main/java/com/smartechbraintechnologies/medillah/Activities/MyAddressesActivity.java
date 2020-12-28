package com.smartechbraintechnologies.medillah.Activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.service.controls.actions.ModeAction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.smartechbraintechnologies.medillah.Adapters.AdapterAddress;
import com.smartechbraintechnologies.medillah.Adapters.AdapterProductShort;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.Models.ModelAddress;
import com.smartechbraintechnologies.medillah.Models.ModelProductShort;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class MyAddressesActivity extends AppCompatActivity implements AdapterAddress.OnOptionsClickListener {

    public static final int REQUEST_CHECK_SETTING = 1001;

    private FloatingActionButton addBTN;
    private TextView noAddressText;
    private ImageButton toolbar_btn, optionsBTN;
    private RecyclerView addressRecycler;
    private LottieAnimationView noAddressAnime;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference addressRef;

    private AdapterAddress mAdapter;
    private ArrayList<ModelAddress> addressList = new ArrayList<>();

    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        setToolbarListeners();
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
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            String id = documentSnapshot.getId();
                            String address = documentSnapshot.getString("address");
                            String type = documentSnapshot.getString("addressType");
                            String status = documentSnapshot.getString("addressStatus");
                            String delivery = documentSnapshot.getString("addressDeliveryStatus");

                            ModelAddress modelAddress = new ModelAddress(id, type, status, address, delivery);
                            addressList.add(modelAddress);

                        }

                        if (addressList.size() == 0) {
                            addressRecycler.setVisibility(View.GONE);
                            noAddressAnime.setVisibility(View.VISIBLE);
                            noAddressText.setVisibility(View.VISIBLE);
                        } else {
                            mAdapter = new AdapterAddress(MyAddressesActivity.this, addressList, MyAddressesActivity.this);
                            addressRecycler.setHasFixedSize(true);
                            addressRecycler.setLayoutManager(new LinearLayoutManager(MyAddressesActivity.this));
                            addressRecycler.setAdapter(mAdapter);
                            addressRecycler.setVisibility(View.VISIBLE);
                            noAddressAnime.setVisibility(View.GONE);
                            noAddressText.setVisibility(View.GONE);
                        }
                        loadingDialog.dismissLoadingDialog();
                    }
                });
    }

    private void initValues() {
        addBTN = findViewById(R.id.my_addresses_add_btn);
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

    private void deleteAddress(ModelAddress address) {
        addressRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() == 1) {
                        addressRef.document(address.getAddressID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    if (task.getResult().getDocuments().get(0).getId().equals(address.getAddressID())) {
                                        WriteBatch batch = db.batch();
                                        addressRef.whereEqualTo("addressStatus", "Not Default").limit(1)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    db.runTransaction(new Transaction.Function<Void>() {
                                                        @Nullable
                                                        @Override
                                                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                                            DocumentSnapshot documentSnapshot = transaction.get(addressRef.document(task.getResult().getDocuments().get(0).getId()));

                                                            batch.delete(addressRef.document(task.getResult().getDocuments().get(0).getId()));

                                                            transaction.update(addressRef.document(documentSnapshot.getId()), "addressStatus", "Default");
                                                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    loadingDialog.dismissLoadingDialog();
                                                                    Toast.makeText(MyAddressesActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                                    finish();
                                                                }
                                                            });
                                                            return null;
                                                        }
                                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            loadingDialog.dismissLoadingDialog();
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            loadingDialog.dismissLoadingDialog();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    } else {
                                        addressRef.document(address.getAddressID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void makeDefault(ModelAddress address) {
        addressRef.whereEqualTo("addressStatus", "Default").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                WriteBatch batch = db.batch();
                Map<String, Object> defaultSettings = new HashMap<>();
                defaultSettings.put("addressStatus", "Not Default");
                batch.update(addressRef.document(task.getResult().getDocuments().get(0).getId()), defaultSettings);
                defaultSettings.put("addressStatus", "Default");
                batch.update(addressRef.document(address.getAddressID()), defaultSettings);
                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.dismissLoadingDialog();
                    }
                });
            }
        });
    }

    private void setToolbarListeners() {
        ImageButton toolbar_backBTN, searchBTN;

        toolbar_backBTN = findViewById(R.id.toolbar_back_btn);
        searchBTN = (ImageButton) findViewById(R.id.toolbar_search_btn);
        toolbar_backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchEngine.class));
            }
        });

    }

    @Override
    public void OnOptionsClick(int position, View v) {
        PopupMenu popupMenu = new PopupMenu(MyAddressesActivity.this, v);
        popupMenu.inflate(R.menu.menu_address_options);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_address_options_set_as_default:
                        loadingDialog.showLoadingDialog("Setting address as default...");
                        makeDefault(addressList.get(position));
                        return true;
                    case R.id.menu_address_options_edit_address:
                        startActivity(new Intent(MyAddressesActivity.this, GetLocationActivity.class)
                                .putExtra("Address ID", addressList.get(position).getAddressID()));
                        return true;
                    case R.id.menu_address_options_delete_address:
                        loadingDialog.showLoadingDialog("Removing address...");
                        deleteAddress(addressList.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}