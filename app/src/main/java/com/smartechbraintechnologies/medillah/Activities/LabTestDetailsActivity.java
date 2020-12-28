package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ornach.nobobutton.NoboButton;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.smartechbraintechnologies.medillah.Adapters.AdapterImageSliderFireBase;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

public class LabTestDetailsActivity extends AppCompatActivity {

    private ImageButton toolbar_backBTN, searchBTN;
    private TextView testName, testMRP, testSP;
    private ShowMoreTextView testDetails;
    private NoboButton scheduleTestBTN;
    private LoadingDialog loadingDialog;
    private ImageView testImage;

    private FirebaseFirestore db;
    private CollectionReference labRef;

    private String labID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test_details);

        Intent intent = getIntent();
        labID = intent.getStringExtra("LAB ID");

        setToolbarListeners();
        initValues();
        loadingDialog.showLoadingDialog("Please wait...");
        loadValues();
        loadingDialog.dismissLoadingDialog();

        scheduleTestBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LabTestDetailsActivity.this, ScheduleLabTestActivity.class).putExtra("LAB TEST ID", labID));
            }
        });


    }

    private void loadValues() {
        labRef.document(labID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    Picasso.get().load(value.getString("testImage")).into(testImage);
                    testName.setText(value.getString("testName"));
                    testSP.setText("₹" + value.getDouble("testSellingPrice").toString());
                    testMRP.setPaintFlags(testMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    testMRP.setText("₹" + value.getDouble("testMRP").toString());
                    testDetails.setText(value.getString("testDetails"));
                    testDetails.setShowingLine(3);
                    loadingDialog.dismissLoadingDialog();
                }
            }
        });
    }

    private void initValues() {
        testName = (TextView) findViewById(R.id.lab_test_details_name);
        testMRP = (TextView) findViewById(R.id.lab_test_details_mrp);
        testSP = (TextView) findViewById(R.id.lab_test_details_sp);
        testImage = (ImageView) findViewById(R.id.lab_test_details_image);
        testDetails = (ShowMoreTextView) findViewById(R.id.lab_test_details_test_details);

        scheduleTestBTN = (NoboButton) findViewById(R.id.lab_test_details_choose_time__btn);
        loadingDialog = new LoadingDialog(this);


        db = FirebaseFirestore.getInstance();
        labRef = db.collection("Lab Tests");
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