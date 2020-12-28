package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Adapters.AdapterDoctor;
import com.smartechbraintechnologies.medillah.Adapters.AdapterMyAppointments;
import com.smartechbraintechnologies.medillah.Adapters.AdapterMyLabTests;
import com.smartechbraintechnologies.medillah.Models.ModelDoctor;
import com.smartechbraintechnologies.medillah.Models.ModelMyAppointments;
import com.smartechbraintechnologies.medillah.Models.ModelMyLabTest;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.util.ArrayList;
import java.util.Collections;

public class MyAppointmentsActivity extends AppCompatActivity implements AdapterMyAppointments.OnAppointmentClickListener {

    private ImageButton toolbar_backBTN, searchBTN;
    private RecyclerView consultationRecyclerView;
    private LinearLayout noAppointmentLayout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference consultationRef;

    private AdapterMyAppointments cAdapter;
    private ArrayList<ModelMyAppointments> consultationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appoinments);

        setToolbarListeners();
        initValues();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        consultationRef.whereEqualTo("consultationCustomerID", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        consultationList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String id = document.getId();
                            String image = document.getString("consultationDoctorImage");
                            String name = document.getString("consultationDoctorName");
                            String speciality = document.getString("consultationDoctorSpeciality");
                            String status = document.getString("consultationStatus");
                            String timing = document.getString("consultationTiming");

                            ModelMyAppointments modelMyAppointments = new ModelMyAppointments(id, image, name, speciality, timing, status);
                            consultationList.add(modelMyAppointments);
                        }

                        if (consultationList.size() == 0) {
                            consultationRecyclerView.setVisibility(View.GONE);
                            noAppointmentLayout.setVisibility(View.VISIBLE);
                        } else {
                            Collections.sort(consultationList, (t1, t2) ->
                                    (t1.getConsultationStatus().compareTo(t2.getConsultationStatus())));

                            cAdapter = new AdapterMyAppointments(MyAppointmentsActivity.this, consultationList, MyAppointmentsActivity.this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyAppointmentsActivity.this);
                            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            consultationRecyclerView.setLayoutManager(linearLayoutManager);
                            consultationRecyclerView.setAdapter(cAdapter);
                            consultationRecyclerView.setVisibility(View.VISIBLE);
                            noAppointmentLayout.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void initValues() {
        consultationRecyclerView = (RecyclerView) findViewById(R.id.my_appointments_recycler);
        noAppointmentLayout = (LinearLayout) findViewById(R.id.my_appointments_no_doctor_layout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        consultationRef = db.collection("Consultation Bookings");
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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void OnAppointmentClick(int position, View view) {
        startActivity(new Intent(MyAppointmentsActivity.this, AppointmentDetailsActivity.class)
                .putExtra("APPOINTMENT ID", consultationList.get(position).getConsultationID()));
    }
}