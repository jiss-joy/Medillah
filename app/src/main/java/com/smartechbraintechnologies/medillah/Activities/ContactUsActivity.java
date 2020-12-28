package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

public class ContactUsActivity extends AppCompatActivity {

    private TextView name, number1, number2, email;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private DocumentReference contactUsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        setToolbarListeners();

        initValues();

        loadingDialog.showLoadingDialog("Loading details...");

        contactUsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    name.setText(task.getResult().getString("companyName"));
                    number1.setText(task.getResult().getString("companyNumber1"));
                    number2.setText(task.getResult().getString("companyNumber2"));
                    email.setText(task.getResult().getString("companyEmail"));
                }
                loadingDialog.dismissLoadingDialog();
            }
        });

    }

    private void initValues() {
        name = findViewById(R.id.contact_us_name);
        number1 = findViewById(R.id.contact_us_number1);
        number2 = findViewById(R.id.contact_us_number2);
        email = findViewById(R.id.contact_us_email);
        loadingDialog = new LoadingDialog(this);

        db = FirebaseFirestore.getInstance();
        contactUsRef = db.collection("Medillah").document("Pharmacy Details");
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
    public void onBackPressed() {
        finish();
    }
}