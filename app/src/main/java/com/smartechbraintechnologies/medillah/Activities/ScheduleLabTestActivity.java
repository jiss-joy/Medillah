package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.ornach.nobobutton.NoboButton;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ScheduleLabTestActivity extends AppCompatActivity {

    private TextView testName, testMRP, testSavings, testGST, testTotal, no_delivery, address_type, address_tv, date;
    private NoboButton bookTestBTN;
    private LoadingDialog loadingDialog;
    private ImageView address_icon;
    private Button myAddressesBTN;
    private ImageButton optionsBTN;
    private RadioGroup radioGroup;
    private View view;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference labRef;
    private CollectionReference labOrderRef;
    private CollectionReference addressRef;

    private String labID;
    private String testTiming = "";
    private Boolean addressFlag = false;
    private Map<String, Object> testOrder = new HashMap();
    private final Map<String, Object> testAddress = new HashMap<>();
    private GeoPoint geoPoint;
    private Double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_lab_test);

        Intent intent = getIntent();
        labID = intent.getStringExtra("LAB TEST ID");

        setToolbarListeners();
        initValues();
        loadingDialog.showLoadingDialog("Please wait...");
        setTimings();
        loadValues();
        loadAddress();
        loadingDialog.dismissLoadingDialog();

        bookTestBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });
    }

    private void verifyDetails() {
        if (!addressFlag) {
            Toast.makeText(this, "Please add your address to continue", Toast.LENGTH_SHORT).show();
        } else if (testTiming.equals("")) {
            Toast.makeText(this, "Please select a timing to continue.", Toast.LENGTH_SHORT).show();
        } else {
            bookLabTest();
        }
    }

    private void bookLabTest() {
        labRef.document(labID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> testTimings = new ArrayList<>();
                ArrayList<String> updatedTestTimings = new ArrayList<>();
                testTimings = (ArrayList<String>) task.getResult().get("testAvailableTimings");
                for (String timing : testTimings) {
                    if (!timing.equals(testTiming)) {
                        updatedTestTimings.add(timing);
                    }
                }
                labRef.document(labID).update("testAvailableTimings", updatedTestTimings);
            }
        });
        Random random = new Random();
        String deliveryCode = String.valueOf(random.nextInt(9000) + 1000);

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = dateFormat.format(date);

        Date time = Calendar.getInstance().getTime();
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(time);

        labRef.document(labID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                testOrder.put("testID", labID);
                testOrder.put("testOrderImage", task.getResult().getString("testImage"));
                testOrder.put("testOrderName", task.getResult().getString("testName"));
                testOrder.put("testOrderStatus", "B");
                testOrder.put("testOrderTime", currentTime);
                testOrder.put("testOrderDate", currentDate);
                testOrder.put("testOrderCustomerID", mAuth.getCurrentUser().getUid());
                testOrder.put("testOrderDeliveryCode", deliveryCode);
                testOrder.put("testOrderTiming", testTiming);
                testOrder.put("testOrderTotalPrice", totalPrice);
                labOrderRef.add(testOrder).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {


                        if (task.isSuccessful()) {
                            String testID = task.getResult().getId();
                            addressRef.whereEqualTo("addressStatus", "Default").limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    testAddress.put("address", task.getResult().getDocuments().get(0).getString("address"));
                                    testAddress.put("addressLatitude", task.getResult().getDocuments().get(0).getDouble("addressLatitude"));
                                    testAddress.put("addressLongitude", task.getResult().getDocuments().get(0).getDouble("addressLongitude"));
                                    testAddress.put("addressID", task.getResult().getDocuments().get(0).getId());
                                    labOrderRef.document(testID).collection("Test Address")
                                            .document(task.getResult().getDocuments().get(0).getId())
                                            .set(testAddress)
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
                            });
                        }
                        finish();
                        Toast.makeText(ScheduleLabTestActivity.this, "Lab Test Order Placed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setTimings() {
        labRef.document(labID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<String> testTimings = new ArrayList<>();
                testTimings.clear();
                testTimings = (ArrayList<String>) value.get("testAvailableTimings");
                int idCounter = 0;
                for (String timing : testTimings) {
                    idCounter++;
                    RadioButton radioButton = new RadioButton(ScheduleLabTestActivity.this);
                    radioButton.setId(idCounter);
                    radioButton.setPaddingRelative(10, 0, 0, 0);
                    radioButton.setText(timing);
                    radioButton.setButtonDrawable(getDrawable(R.drawable.radio_button_selector));
                    radioButton.setTextColor(getResources().getColor(R.color.secondary));
                    radioGroup.addView(radioButton);
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = new RadioButton(ScheduleLabTestActivity.this);
                        radioButton = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                        testTiming = radioButton.getText().toString();
                        Toast.makeText(ScheduleLabTestActivity.this, testTiming, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void loadValues() {
        labRef.document(labID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Double mrp = document.getDouble("testMRP");
                Double sp = document.getDouble("testSellingPrice");
                testName.setText(document.getString("testName"));
                testMRP.setText("₹" + String.valueOf(mrp));
                testSavings.setText("-" + "₹" + (mrp - sp));
                testGST.setText("₹" + String.valueOf(0.18 * mrp));
                totalPrice = sp + (0.18 * mrp);
                testTotal.setText("₹" + String.valueOf(totalPrice));
                Date dateInstance = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String currentDate = dateFormat.format(dateInstance);
                date.setText(currentDate);
            }
        });
    }

    private void loadAddress() {
        addressRef.whereEqualTo("addressStatus", "Default").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.getDocuments().isEmpty()) {
                    addressFlag = false;
                    myAddressesBTN.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                } else {
                    addressFlag = true;
                    myAddressesBTN.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    DocumentSnapshot documentSnapshot = value.getDocuments().get(0);
                    String type = documentSnapshot.getString("addressType");
                    String address = documentSnapshot.getString("address");
                    String delivery_status = documentSnapshot.getString("addressDeliveryStatus");
                    geoPoint = (GeoPoint) documentSnapshot.get("addressGeoPoint");
                    testOrder.put("testOrderLocation", geoPoint);

                    if (type.equals("Home")) {
                        address_icon.setImageResource(R.drawable.home);
                        address_type.setText(type);
                    } else if (type.equals("Work")) {
                        address_icon.setImageResource(R.drawable.work);
                        address_type.setText(type);
                    } else {
                        address_icon.setImageResource(R.drawable.location);
                        address_type.setText(type);
                    }
                    address_tv.setText(address);
                    if (delivery_status.equals("No Delivery")) {
                        no_delivery.setVisibility(View.VISIBLE);
                    } else {
                        no_delivery.setVisibility(View.GONE);
                    }
                }
            }

        });
    }


    private void initValues() {
        testName = (TextView) findViewById(R.id.schedule_lab_test_test_name);
        testMRP = (TextView) findViewById(R.id.schedule_lab_test_mrp);
        testSavings = (TextView) findViewById(R.id.schedule_lab_test_savings);
        testTotal = (TextView) findViewById(R.id.schedule_lab_test_total);
        testGST = (TextView) findViewById(R.id.schedule_lab_test_gst);
        date = (TextView) findViewById(R.id.schedule_lab_test_test_date);

        view = (View) findViewById(R.id.schedule_lab_test_address_card);
        address_icon = (ImageView) findViewById(R.id.address_card_type_image);
        address_type = (TextView) findViewById(R.id.address_card_address_type);
        address_tv = (TextView) findViewById(R.id.address_card_address);
        no_delivery = (TextView) findViewById(R.id.address_card_no_delivery);
        no_delivery.setVisibility(View.GONE);
        optionsBTN = (ImageButton) findViewById(R.id.address_card_options_btn);
        myAddressesBTN = (Button) findViewById(R.id.schedule_lab_test_add_address_btn);
        radioGroup = (RadioGroup) findViewById(R.id.schedule_lab_test_timings_radio);
        bookTestBTN = (NoboButton) findViewById(R.id.schedule_lab_test_schedule_btn);

        loadingDialog = new LoadingDialog(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        labRef = db.collection("Lab Tests");
        labOrderRef = db.collection("Lab Test Bookings");
        addressRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Addresses");
    }

    private void setToolbarListeners() {
        ImageButton toolbar_backBTN = findViewById(R.id.toolbar_back_btn);
        ImageButton searchBTN = (ImageButton) findViewById(R.id.toolbar_search_btn);
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
}