package com.smartechbraintechnologies.medillah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.smartechbraintechnologies.medillah.Authentication.PhoneAuthActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button logOut;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logOut = (Button) findViewById(R.id.log_out_btn);
        mAuth = FirebaseAuth.getInstance();

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, PhoneAuthActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}