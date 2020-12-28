package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Adapters.AdapterDoctor;
import com.smartechbraintechnologies.medillah.Models.ModelDoctor;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.util.ArrayList;

public class DoctorListActivity extends AppCompatActivity implements AdapterDoctor.OnDocClickListener {

    private ImageButton toolbar_backBTN, searchBTN;
    private RecyclerView docRecyclerView;
    private LinearLayout noDocLayout;

    private FirebaseFirestore db;
    private CollectionReference docRef;

    private AdapterDoctor dAdapter;
    private ArrayList<ModelDoctor> doctorsList = new ArrayList<>();
    private ArrayList<String> doctorID = new ArrayList<>();

    private String speciality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        Intent intent = getIntent();
        speciality = intent.getStringExtra("Doctor Category");

        setToolbarListeners();
        initValues();

        setUpRecyclerView();
    }


    private void setUpRecyclerView() {
        docRef.whereEqualTo("doctorSpeciality", speciality).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                doctorsList.clear();
                doctorID.clear();
                for (DocumentSnapshot document : value.getDocuments()) {
                    String image = document.getString("doctorImage");
                    String name = document.getString("doctorName");
                    Double exp = document.getDouble("doctorExperience");
                    Double mrp = document.getDouble("doctorMRP");
                    Double cp = document.getDouble("doctorConsultationPrice");

                    ModelDoctor modelDoctor = new ModelDoctor(image, name, speciality, exp, mrp, cp);
                    doctorID.add(document.getId());
                    doctorsList.add(modelDoctor);
                }

                if (doctorsList.size() == 0) {
                    docRecyclerView.setVisibility(View.GONE);
                    noDocLayout.setVisibility(View.VISIBLE);
                } else {
                    dAdapter = new AdapterDoctor(DoctorListActivity.this, doctorsList, DoctorListActivity.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DoctorListActivity.this);
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    docRecyclerView.setLayoutManager(linearLayoutManager);
                    docRecyclerView.setAdapter(dAdapter);
                    noDocLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    private void initValues() {
        docRecyclerView = (RecyclerView) findViewById(R.id.doctor_list_recycler);
        noDocLayout = (LinearLayout) findViewById(R.id.doctor_list_no_doctor_layout);

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("Doctors");
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
    public void OnDocClick(int position, View view) {
        String id = doctorID.get(position);
        startActivity(new Intent(DoctorListActivity.this, DoctorDetailsActivity.class).putExtra("DOC ID", id));
    }
}