package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int ACCESS_LOCATION_REQUEST_CODE = 1001;
    private static final double DELIVERY_DISTANCE = 11.0;
    private static final double NO_CHARGE_DELIVERY_DISTANCE = 5.5;

    private GoogleMap mMap;
    private ImageView fixedMarker;
    private TextView address_area_tv, address_tv;
    private Button confirmAddress_btn;
    private CheckBox checkBox;
    private RadioGroup radioGroup;
    private LoadingDialog loadingDialog;

    private Geocoder geocoder;
    private FusedLocationProviderClient client;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CollectionReference addressRef;
    private DocumentReference medillahRef;

    private String location_address;
    private Double location_latitude;
    private Double location_longitude;
    private float[] results = new float[10];
    private float[] correctedResult = new float[10];
    private String address_delivery_status = "Not Working";
    private String address_default = "Not Default";
    private String address_type = "Home";
    private String updateAddressID;

    private boolean updateFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.get_location_map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        updateAddressID = intent.getStringExtra("Address ID");

        initValues();

        if (updateAddressID != null) {
            updateFlag = true;
            checkBox.setVisibility(View.GONE);
        }

        confirmAddress_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showLoadingDialog("Saving your address...");
                calculateDistance();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.get_location_home_radio:
                        address_type = "Home";
                        break;
                    case R.id.get_location_work_radio:
                        address_type = "Work";
                        break;
                    case R.id.get_location_other_radio:
                        address_type = "Other";
                        break;
                }
            }
        });
    }

    private void updateAddress() {
        Map<String, Object> address = new HashMap<>();

        DecimalFormat df = new DecimalFormat("#.##");
        String distance = df.format(correctedResult[0]);
        address.put("addressDistance", distance);
        address.put("addressDeliveryStatus", address_delivery_status);
        address.put("address", location_address);
        address.put("addressStatus", address_default);
        address.put("addressLatitude", location_latitude);
        address.put("addressLongitude", location_longitude);
        address.put("addressType", address_type);
        addressRef.document(updateAddressID).update(address)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loadingDialog.dismissLoadingDialog();
                            finish();
                        }
                    }
                });
    }

    private void addAddress() {
        Map<String, Object> address = new HashMap<>();

        DecimalFormat df = new DecimalFormat("#.##");
        String distance = df.format(correctedResult[0]);
        address.put("addressDistance", distance);
        address.put("addressDeliveryStatus", address_delivery_status);
        address.put("address", location_address);
        address.put("addressStatus", address_default);
        address.put("addressLatitude", location_latitude);
        address.put("addressLongitude", location_longitude);
        address.put("addressType", address_type);
        addressRef.add(address)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            loadingDialog.dismissLoadingDialog();
                            finish();
                        }
                    }
                });
    }

    private void calculateDistance() {
        medillahRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Double sourceLatitude = documentSnapshot.getDouble("addressLatitude");
                    Double sourceLongitude = documentSnapshot.getDouble("addressLongitude");


                    Location.distanceBetween(sourceLatitude, sourceLongitude, location_latitude, location_longitude, results);
                    //Dividing by 1000 to get the result in Kilo-Meters.
                    //Multiplying by 1.365 to get the corrected result.
                    //Here the result is 30% less accurate. Hence the correction.
                    correctedResult[0] = (float) (results[0] * 0.001365);
                    if (correctedResult[0] > DELIVERY_DISTANCE) {
                        address_delivery_status = "No Delivery";
                    } else if ((correctedResult[0] < DELIVERY_DISTANCE) && (correctedResult[0] > NO_CHARGE_DELIVERY_DISTANCE)) {
                        address_delivery_status = "Delivery Charge";
                    } else if (correctedResult[0] < NO_CHARGE_DELIVERY_DISTANCE) {
                        address_delivery_status = "Free Delivery";
                    }
                }
                checkForDefault();
            }
        });
    }

    private void checkForDefault() {
        addressRef.whereEqualTo("addressStatus", "Default").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 0) {
                        address_default = "Default";
                        if (updateFlag) {
                            updateAddress();
                        } else {
                            addAddress();
                        }
                    } else {
                        if (checkBox.isChecked()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String id = documentSnapshot.getId();
                            Map<String, Object> address = new HashMap<>();
                            address.put("addressStatus", "Not Default");
                            addressRef.document(id).set(address, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    address_default = "Default";
                                    if (updateFlag) {
                                        updateAddress();
                                    } else {
                                        addAddress();
                                    }
                                }
                            });
                        } else {
                            address_default = "Not Default";
                            if (updateFlag) {
                                updateAddress();
                            } else {
                                addAddress();
                            }
                        }
                    }
                }
            }
        });
    }


    private void initValues() {
        geocoder = new Geocoder(this);
        client = LocationServices.getFusedLocationProviderClient(this);


        fixedMarker = (ImageView) findViewById(R.id.get_location_fixed_marker);
        address_tv = (TextView) findViewById(R.id.get_location_address);
        address_area_tv = (TextView) findViewById(R.id.get_location_area);
        confirmAddress_btn = (Button) findViewById(R.id.get_location_confirm_btn);
        checkBox = (CheckBox) findViewById(R.id.get_location_default_checkbox);
        checkBox.setChecked(false);
        radioGroup = (RadioGroup) findViewById(R.id.get_location_radio_group);

        loadingDialog = new LoadingDialog(GetLocationActivity.this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        addressRef = db.collection("Users").document(currentUser.getUid()).collection("Addresses");
        medillahRef = db.collection("Medillah").document("Pharmacy Details");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));


        } catch (Resources.NotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    LatLng latLng = mMap.getCameraPosition().target;
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    location_address = addresses.get(0).getLocality();
                    if (!location_address.isEmpty()) {
                        address_area_tv.setText(location_address);
                    } else {
                        address_area_tv.setText("Unknown Locality");
                    }
                    location_address = addresses.get(0).getAddressLine(0);
                    address_tv.setText(location_address);
                    location_latitude = latLng.latitude;
                    location_longitude = latLng.longitude;
                } catch (Exception e) {
                    Log.d("ARRAY INDEX ERROR", e.getMessage());
                }
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mMap.clear();
                fixedMarker.setVisibility(View.VISIBLE);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, ACCESS_LOCATION_REQUEST_CODE);
            }
        }

    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (updateFlag) {
            addressRef.document(updateAddressID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String status = task.getResult().getString("addressStatus");
                        String type = task.getResult().getString("addressType");
                        Double lat = task.getResult().getDouble("addressLatitude");
                        Double lng = task.getResult().getDouble("addressLongitude");
                        LatLng latLng = new LatLng(lat, lng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                        if (status.equals("Default")) {
                            checkBox.setChecked(true);
                        } else {
                            checkBox.setChecked(false);
                        }

                        if (type.equals("Home")) {
                            radioGroup.check(R.id.get_location_home_radio);
                        } else if (type.equals("Work")) {
                            radioGroup.check(R.id.get_location_work_radio);
                        } else {
                            radioGroup.check(R.id.get_location_other_radio);
                        }

                        try {
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            location_address = addresses.get(0).getAddressLine(0);
                            address_tv.setText(location_address);
                            location_latitude = latLng.latitude;
                            location_longitude = latLng.longitude;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            Task<Location> locationTask = client.getLastLocation();
            locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        LatLng latLng = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            location_address = addresses.get(0).getAddressLine(0);
                            address_tv.setText(location_address);
                            location_latitude = latLng.latitude;
                            location_longitude = latLng.longitude;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            } else {

            }
        }
    }

}