package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ornach.nobobutton.NoboButton;
import com.smartechbraintechnologies.medillah.Adapters.AdapterImageSliderFireBase;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;
import com.smartechbraintechnologies.medillah.ShowSnackbar;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageButton toolbar_backBTN, searchBTN, addBTN, removeBTN;
    private TextView productName, productMRP, productSP, productDiscount, productSavings;
    private TextView quantity;
    private SliderView sliderView;
    private RelativeLayout relativeLayout;
    private NoboButton buyBTN;
    private LoadingDialog loadingDialog;
    private Button descBTN;

    private FirebaseFirestore db;
    private CollectionReference productRef;

    private AdapterImageSliderFireBase sliderAdapter;

    private String productID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent intent = getIntent();
        productID = intent.getStringExtra("Product ID");

        setToolbarListeners();
        initValues();
        loadingDialog.showLoadingDialog("Please wait...");
        loadValues();
        loadSimilarProducts();
        loadingDialog.dismissLoadingDialog();

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString()) + 1));
            }
        });

        removeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!quantity.getText().toString().equals("0")) {
                    quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString()) - 1));
                }
            }
        });

        buyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity.getText().toString().equals("0")) {
                    ShowSnackbar.show(ProductDetailsActivity.this, "Quantity cannot be zero", relativeLayout,
                            getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                } else {
                    startActivity(new Intent(ProductDetailsActivity.this, ConfirmOrderActivity.class)
                            .putExtra("Product Id", productID)
                            .putExtra("Quantity", quantity.getText().toString()));
                }
            }
        });

        descBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this, ProductDescriptionActivity.class)
                        .putExtra("PRODUCT ID", productID));
            }
        });
    }

    private void loadSimilarProducts() {

    }


    private void loadValues() {
        productRef.document(productID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    ArrayList<String> imageUri = new ArrayList<>();
                    imageUri = (ArrayList<String>) value.get("productImage");
                    sliderAdapter = new AdapterImageSliderFireBase(getApplicationContext(), imageUri);
                    sliderView.setSliderAdapter(sliderAdapter);
                    sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    productName.setText(value.getString("productName"));
                    productMRP.setText(new DecimalFormat("₹#.00").format(value.getDouble("productMRP")));
                    productMRP.setPaintFlags(productMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    productSP.setText(new DecimalFormat("₹#.00").format(value.getDouble("productSellingPrice")));
                    productDiscount.setText(String.valueOf(value.getDouble("productDiscount") + "% off"));
                    productSavings.setText(new DecimalFormat("Save ₹#.00").format(value.getDouble("productMRP") - value.getDouble("productSellingPrice")));
                }
            }
        });
    }

    private void initValues() {
        sliderView = (SliderView) findViewById(R.id.product_details_image_slider);
        productName = (TextView) findViewById(R.id.product_details_product_name);
        productMRP = (TextView) findViewById(R.id.product_details_product_mrp);
        productSP = (TextView) findViewById(R.id.product_details_product_selling_price);
        productDiscount = (TextView) findViewById(R.id.product_details_product_discount);
        productSavings = (TextView) findViewById(R.id.product_details_product_savings);

        addBTN = (ImageButton) findViewById(R.id.product_details_add_btn);
        removeBTN = (ImageButton) findViewById(R.id.product_details_remove_btn);
        buyBTN = (NoboButton) findViewById(R.id.product_details_buy_btn);
        quantity = (TextView) findViewById(R.id.product_details_quantity);
        relativeLayout = (RelativeLayout) findViewById(R.id.product_details_relative_layout);
        descBTN = (Button) findViewById(R.id.product_details_desc_btn);
        loadingDialog = new LoadingDialog(this);


        db = FirebaseFirestore.getInstance();
        productRef = db.collection("Products");
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
}