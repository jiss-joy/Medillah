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
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ornach.nobobutton.NoboButton;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;
import com.squareup.picasso.Picasso;

public class DoctorDetailsActivity extends AppCompatActivity {

    private ImageButton toolbar_backBTN, searchBTN;
    private TextView docName, docMRP, docCP;
    private ShowMoreTextView docDetails;
    private NoboButton scheduleConsultationBTN;
    private LoadingDialog loadingDialog;
    private ImageView docImage;

    private FirebaseFirestore db;
    private CollectionReference docRef;

    private String docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        Intent intent = getIntent();
        docID = intent.getStringExtra("DOC ID");

        setToolbarListeners();
        initValues();
        loadingDialog.showLoadingDialog("Please wait...");
        loadValues();
        loadingDialog.dismissLoadingDialog();

        scheduleConsultationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorDetailsActivity.this, ScheduleConsultationActivity.class).putExtra("DOCTOR ID", docID));
            }
        });
    }

    private void loadValues() {
        docRef.document(docID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    Picasso.get().load(value.getString("doctorImage")).into(docImage);
                    docName.setText(value.getString("doctorName"));
                    docCP.setText("₹" + value.getDouble("doctorConsultationPrice").toString());
                    docMRP.setPaintFlags(docMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    docMRP.setText("₹" + value.getDouble("doctorMRP").toString());
                    docDetails.setText(value.getString("doctorInfo"));
                    docDetails.setShowingLine(3);
                    loadingDialog.dismissLoadingDialog();
                }
            }
        });
    }

    private void initValues() {
        docName = (TextView) findViewById(R.id.doctor_details_name);
        docMRP = (TextView) findViewById(R.id.doctor_details_mrp);
        docCP = (TextView) findViewById(R.id.doctor_details_cp);
        docImage = (ImageView) findViewById(R.id.doctor_details_image);
        docDetails = (ShowMoreTextView) findViewById(R.id.doctor_details_info);

        scheduleConsultationBTN = (NoboButton) findViewById(R.id.doctor_details_choose_time__btn);
        loadingDialog = new LoadingDialog(this);


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
}