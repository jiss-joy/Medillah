package com.smartechbraintechnologies.medillah;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartechbraintechnologies.medillah.Authentication.PhoneAuthActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initValues();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, PhoneAuthActivity.class);
            startActivity(intent);
        } else {

        }
    }

    private void initValues() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        relativeLayout = findViewById(R.id.main_activity_layout);
    }

    private void exit() {
        this.finishAffinity();
    }
}