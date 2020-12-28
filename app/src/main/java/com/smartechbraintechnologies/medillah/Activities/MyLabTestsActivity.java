package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Adapters.AdapterMyLabTests;
import com.smartechbraintechnologies.medillah.Models.ModelMyLabTest;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.util.ArrayList;
import java.util.Collections;

public class MyLabTestsActivity extends AppCompatActivity implements AdapterMyLabTests.OnLabTestClickListener {

    private ImageButton toolbar_backBTN, searchBTN;
    private RecyclerView labRecyclerView;
    private LinearLayout noLabTestLayout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference labRef;

    private AdapterMyLabTests lAdapter;
    private ArrayList<ModelMyLabTest> testList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lab_tests);

        setToolbarListeners();
        initValues();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        labRef.whereEqualTo("testOrderCustomerID", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        testList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String id = document.getId();
                            String image = document.getString("testOrderImage");
                            String name = document.getString("testOrderName");
                            String status = document.getString("testOrderStatus");
                            String timing = document.getString("testOrderTiming");

                            ModelMyLabTest modelMyLabTest = new ModelMyLabTest(id, image, name, status, timing);
                            testList.add(modelMyLabTest);
                        }

                        if (testList.size() == 0) {
                            labRecyclerView.setVisibility(View.GONE);
                            noLabTestLayout.setVisibility(View.VISIBLE);
                        } else {
                            Collections.sort(testList, (t1, t2) ->
                                    (t1.getTestStatus().compareTo(t2.getTestStatus())));

                            lAdapter = new AdapterMyLabTests(MyLabTestsActivity.this, testList, MyLabTestsActivity.this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyLabTestsActivity.this);
                            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            labRecyclerView.setLayoutManager(linearLayoutManager);
                            labRecyclerView.setAdapter(lAdapter);
                            labRecyclerView.setVisibility(View.VISIBLE);
                            noLabTestLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initValues() {
        labRecyclerView = (RecyclerView) findViewById(R.id.my_lab_tests_recycler);
        noLabTestLayout = (LinearLayout) findViewById(R.id.my_lab_test_no_labs_layout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        labRef = db.collection("Lab Test Bookings");
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
    public void OnLabTestClick(int position, View view) {
        startActivity(new Intent(MyLabTestsActivity.this, MyLabTestDetailsActivity.class)
                .putExtra("TEST ID", testList.get(position).getTestID()));
    }
}