package com.smartechbraintechnologies.medillah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyAddressesActivity extends AppCompatActivity {

    private FloatingActionButton addBTN;
    private TextView toolbar_title;
    private ImageButton toolbar_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        initValues();

        toolbar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        toolbar_title.setText("My Addresses");

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAddressesActivity.this, GetLocationActivity.class));
            }
        });
    }

    private void initValues() {
        addBTN = findViewById(R.id.my_addresses_add_btn);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_btn = findViewById(R.id.toolbar_back_btn);
    }
}