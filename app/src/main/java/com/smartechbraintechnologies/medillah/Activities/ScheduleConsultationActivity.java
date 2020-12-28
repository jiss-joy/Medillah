package com.smartechbraintechnologies.medillah.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ornach.nobobutton.NoboButton;
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

public class ScheduleConsultationActivity extends AppCompatActivity {

    private ImageButton toolbar_backBTN, searchBTN;
    private TextView docName, docSpeciality, docMRP, docSavings, docGST, docTotal, venueAddress, date;
    private NoboButton bookConsultationBTN;
    private LoadingDialog loadingDialog;
    private RadioGroup radioGroup;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference docRef;
    private CollectionReference consultationRef;
    private CollectionReference venueRef;

    private String docID;
    private String consultationTiming = "";
    private Map<String, Object> consultationData = new HashMap();
    private Double totalPrice = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_consultation);
        Intent intent = getIntent();
        docID = intent.getStringExtra("DOCTOR ID");

        setToolbarListeners();
        initValues();
        loadingDialog.showLoadingDialog("Please wait...");
        setTimings();
        loadValues();
        loadVenue();
        loadingDialog.dismissLoadingDialog();

        bookConsultationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });
    }

    private void verifyDetails() {
        if (consultationTiming.equals("")) {
            Toast.makeText(this, "Please select a timing to continue.", Toast.LENGTH_SHORT).show();
        } else {
            loadingDialog.showLoadingDialog("Booking Appointment...");
            bookConsultation();
        }
    }

    private void bookConsultation() {
        docRef.document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> docTimings = new ArrayList<>();
                ArrayList<String> updatedDocTimings = new ArrayList<>();
                docTimings = (ArrayList<String>) task.getResult().get("doctorAvailableTimings");
                for (String timing : docTimings) {
                    if (!timing.equals(consultationTiming)) {
                        updatedDocTimings.add(timing);
                    }
                }
                docRef.document(docID).update("doctorAvailableTimings", updatedDocTimings);
            }
        });
        Random random = new Random();
        String consultationOTP = String.valueOf(random.nextInt(9000) + 1000);

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = dateFormat.format(date);

        Date time = Calendar.getInstance().getTime();
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(time);

        docRef.document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                consultationData.put("consultationDoctorID", docID);
                consultationData.put("consultationDoctorImage", task.getResult().getString("doctorImage"));
                consultationData.put("consultationDoctorName", task.getResult().getString("doctorName"));
                consultationData.put("consultationDoctorSpeciality", task.getResult().getString("doctorSpeciality"));
                consultationData.put("consultationStatus", "B");
                consultationData.put("consultationTime", currentTime);
                consultationData.put("consultationDate", currentDate);
                consultationData.put("consultationCustomerID", mAuth.getCurrentUser().getUid());
                consultationData.put("consultationOTP", consultationOTP);
                consultationData.put("consultationTiming", consultationTiming);
                consultationData.put("consultationTotalPrice", totalPrice);
                consultationRef.add(consultationData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        loadingDialog.dismissLoadingDialog();
                        finish();
                        Toast.makeText(ScheduleConsultationActivity.this, "Appointment booked", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setTimings() {
        docRef.document(docID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<String> testTimings = new ArrayList<>();
                testTimings.clear();
                testTimings = (ArrayList<String>) value.get("doctorAvailableTimings");
                int idCounter = 0;
                for (String timing : testTimings) {
                    idCounter++;
                    RadioButton radioButton = new RadioButton(ScheduleConsultationActivity.this);
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
                        RadioButton radioButton = new RadioButton(ScheduleConsultationActivity.this);
                        radioButton = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                        consultationTiming = radioButton.getText().toString();
                        Toast.makeText(ScheduleConsultationActivity.this, consultationTiming, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadValues() {
        docRef.document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Double mrp = document.getDouble("doctorMRP");
                Double sp = document.getDouble("doctorConsultationPrice");
                docName.setText(document.getString("doctorName"));
                docSpeciality.setText(document.getString("doctorSpeciality"));
                docMRP.setText("₹" + mrp);
                docSavings.setText("-" + "₹" + (mrp - sp));
                docGST.setText("₹" + String.valueOf(0.18 * mrp));
                totalPrice = sp + (0.18 * mrp);
                docTotal.setText("₹" + String.valueOf(totalPrice));
                Date dateInstance = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String currentDate = dateFormat.format(dateInstance);
                date.setText(currentDate);
            }
        });
    }

    private void loadVenue() {
        venueRef.document("Pharmacy Details").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String venue = task.getResult().getString("pharmacyAddress");
                venueAddress.setText(venue);
            }
        });
    }

    private void initValues() {
        docName = (TextView) findViewById(R.id.schedule_consultation_doctor_name);
        docSpeciality = (TextView) findViewById(R.id.schedule_consultation_doctor_speciality);
        docMRP = (TextView) findViewById(R.id.schedule_consultation_mrp);
        docSavings = (TextView) findViewById(R.id.schedule_consultation_savings);
        docTotal = (TextView) findViewById(R.id.schedule_consultation_total);
        docGST = (TextView) findViewById(R.id.schedule_consultation_gst);
        date = (TextView) findViewById(R.id.schedule_consultation_date);

        radioGroup = (RadioGroup) findViewById(R.id.schedule_consultation_timings_radio);
        bookConsultationBTN = (NoboButton) findViewById(R.id.schedule_consultation_schedule_btn);
        venueAddress = (TextView) findViewById(R.id.consultation_venue_address);

        loadingDialog = new LoadingDialog(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        docRef = db.collection("Doctors");
        consultationRef = db.collection("Consultation Bookings");
        venueRef = db.collection("Medillah");
    }

    private void setToolbarListeners() {
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
}