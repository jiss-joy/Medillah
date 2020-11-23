package com.smartechbraintechnologies.medillah.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.smartechbraintechnologies.medillah.R;

public class ShoppingCartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}