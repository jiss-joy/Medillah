package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDescriptionActivity extends AppCompatActivity {

    private ImageButton toolbar_backBTN, searchBTN;
    private ImageView image;
    private TextView name, sp, mrp, discount, brand, life, form, dosage, mfg, pck, imp, caution;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private DocumentReference productRef;

    private String productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        Intent intent = getIntent();
        productID = intent.getStringExtra("PRODUCT ID");

        initValues();

        loadingDialog.showLoadingDialog("Loading details...");
        loadData();
    }

    private void loadData() {
        productRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<String> images = new ArrayList<>();
                images = (ArrayList<String>) value.get("productImage");

                Picasso.get().load(images.get(0)).into(image);
                name.setText(value.getString("productName"));
                sp.setText("₹" + String.valueOf(value.getDouble("productSellingPrice")));
                mrp.setText("₹" + String.valueOf(value.getDouble("productMRP")));
                mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                discount.setText(String.valueOf(value.getDouble("productDiscount")));
                brand.setText(value.getString("productBrand"));
                life.setText(value.getString("productShelfLife"));
                form.setText(value.getString("productDosageForm"));
                mfg.setText(value.getString("productManufacturedBy"));
                pck.setText(value.getString("productPackedBy"));
                imp.setText(value.getString("productImportedBy"));
                caution.setText(value.getString("productCaution"));
                dosage.setText(value.getString("productDosage"));
                loadingDialog.dismissLoadingDialog();
            }
        });
    }

    private void initValues() {
        image = (ImageView) findViewById(R.id.product_desc_image);
        name = (TextView) findViewById(R.id.product_desc_name);
        sp = (TextView) findViewById(R.id.product_desc_sp);
        mrp = (TextView) findViewById(R.id.product_desc_mrp);
        discount = (TextView) findViewById(R.id.product_desc_discount);
        brand = (TextView) findViewById(R.id.product_desc_brand);
        life = (TextView) findViewById(R.id.product_desc_life);
        form = (TextView) findViewById(R.id.product_desc_dosage_form);
        mfg = (TextView) findViewById(R.id.product_desc_mfg);
        pck = (TextView) findViewById(R.id.product_desc_pck);
        imp = (TextView) findViewById(R.id.product_desc_imp);
        caution = (TextView) findViewById(R.id.product_desc_caution);
        dosage = (TextView) findViewById(R.id.product_desc_dosage);

        loadingDialog = new LoadingDialog(this);

        db = FirebaseFirestore.getInstance();
        productRef = db.collection("Products").document(productID);
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