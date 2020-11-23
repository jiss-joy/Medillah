package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ornach.nobobutton.NoboButton;
import com.skyhope.showmoretextview.ShowMoreTextView;
import com.smartechbraintechnologies.medillah.Adapters.AdapterImageSliderFireBase;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;
import com.smartechbraintechnologies.medillah.ShowSnackbar;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageButton toolbar_backBTN, searchBTN, shoppingCartBTN, addBTN, removeBTN;
    private TextView productName, productMRP, productSP, productDiscount, productSavings, productRating;
    private TextView reviewName, reviewTime, reviewRating, quantity, review_title;
    private ShowMoreTextView review;
    private SliderView sliderView;
    private CircleImageView reviewImage;
    private LinearLayout ratingBackground;
    private RelativeLayout relativeLayout;
    private CardView reviewCard;
    private NoboButton addToCartBTN, allReviewsBTN;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference userRef;
    private CollectionReference productRef;
    private CollectionReference reviewRef;
    private CollectionReference shoppingCartRef;

    private AdapterImageSliderFireBase sliderAdapter;

    private String productID;
    private Timestamp timestamp;
    private boolean cartFlag = false;

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
        loadReview();
        loadSimilarProducts();
        loadingDialog.dismissLoadingDialog();

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartFlag = false;
                addToCartBTN.setText("Add to Cart");
                quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString()) + 1));
            }
        });

        removeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartFlag = false;
                addToCartBTN.setText("Add to Cart");
                if (!quantity.getText().toString().equals("0")) {
                    quantity.setText(String.valueOf(Integer.parseInt(quantity.getText().toString()) - 1));
                }
            }
        });

        addToCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cartFlag) {
                    if (quantity.getText().toString().equals("0")) {
                        ShowSnackbar.show(ProductDetailsActivity.this, "Quantity cannot be zero", relativeLayout,
                                getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                    } else {
                        shoppingCartRef.document(productID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().exists()) {
                                        loadingDialog.showLoadingDialog("Add item to cart...");
                                        Map<String, Object> shoppingCartItem = new HashMap<>();
                                        shoppingCartItem.put("productQuantity", Integer.parseInt(quantity.getText().toString()));
                                        shoppingCartItem.put("productID", productID);
                                        shoppingCartRef.document(productID).set(shoppingCartItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    cartFlag = true;
                                                    addToCartBTN.setText("Go to cart");
                                                    loadingDialog.dismissLoadingDialog();
                                                }
                                            }
                                        });
                                    } else {
                                        loadingDialog.showLoadingDialog("Updating cart...");
                                        Double existingProductQty = task.getResult().getDouble("productQuantity");
                                        shoppingCartRef.document(productID).update("productQuantity",
                                                existingProductQty + Double.parseDouble(quantity.getText().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    cartFlag = true;
                                                    addToCartBTN.setText("Go to cart");
                                                    loadingDialog.dismissLoadingDialog();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(ProductDetailsActivity.this, ShoppingCartActivity.class));
                }

            }
        });

        allReviewsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this, AllReviewsActivity.class));
            }
        });
    }

    private void loadSimilarProducts() {

    }

    private void loadReview() {
        reviewRef.orderBy("productRating", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        timestamp = documentSnapshot.getTimestamp("reviewTime");
                        Date dates = timestamp.toDate();
                        String date = DateFormat.format("dd MMM, yyyy", dates).toString();
                        String time = DateFormat.format("HH:mm A", dates).toString();
                        Calendar calendar = Calendar.getInstance();
                        String today = DateFormat.format("dd MMM, yyyy", calendar).toString();
                        calendar.add(Calendar.DATE, -1);
                        String yesterday = DateFormat.format("dd MMM, yyyy", calendar).toString();
                        if (date.equals(today)) {
                            reviewTime.setText("Today at " + time);
                        } else if (date.equals(yesterday)) {
                            reviewTime.setText("Yesterday at " + time);
                        } else {
                            reviewTime.setText(time + " on " + date);
                        }
                        reviewRating.setText(String.valueOf(documentSnapshot.getDouble("productRating")));

                        review.setText(documentSnapshot.getString("productReview"));
                        review.setShowingLine(2);
                        review.setShowLessTextColor(getResources().getColor(R.color.primary_dark));
                        review.setShowMoreColor(getResources().getColor(R.color.primary_dark));

                        userRef.document(documentSnapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    reviewName.setText(task.getResult().getString("userName"));
                                    Picasso.get().load(task.getResult().getString("userImage")).fit().into(reviewImage);
                                }
                            }
                        });
                    } else {
                        reviewCard.setVisibility(View.GONE);
                        review_title.setText("There are no reviews till now.");
                    }
                }
            }
        });
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
                    productMRP.setText("₹" + value.getDouble("productMRP"));
                    productMRP.setPaintFlags(productMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    productSP.setText("₹" + value.getDouble("productSellingPrice"));
                    productDiscount.setText(String.valueOf(value.getDouble("productDiscount")));
                    productSavings.setText("Save ₹" + String.valueOf(value.getDouble("productMRP") - value.getDouble("productSellingPrice")));
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
        productRating = (TextView) findViewById(R.id.item_rating_product_rating);

        reviewCard = (CardView) findViewById(R.id.product_details_review_card);
        reviewImage = (CircleImageView) findViewById(R.id.product_details_review_image);
        review = (ShowMoreTextView) findViewById(R.id.product_details_review);
        reviewName = (TextView) findViewById(R.id.product_details_review_name);
        reviewRating = (TextView) findViewById(R.id.item_user_rating_product_rating);
        reviewTime = (TextView) findViewById(R.id.product_details_review_time);
        ratingBackground = (LinearLayout) findViewById(R.id.item_rating_layout);
        review_title = (TextView) findViewById(R.id.product_details_review_tv);

        addBTN = (ImageButton) findViewById(R.id.product_details_add_btn);
        removeBTN = (ImageButton) findViewById(R.id.product_details_remove_btn);
        addToCartBTN = (NoboButton) findViewById(R.id.product_details_add_to_cart_btn);
        allReviewsBTN = (NoboButton) findViewById(R.id.product_details_all_review_btn);
        quantity = (TextView) findViewById(R.id.product_details_quantity);
        relativeLayout = (RelativeLayout) findViewById(R.id.product_details_relative_layout);
        loadingDialog = new LoadingDialog(this);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = db.collection("Users");
        productRef = db.collection("Products").document("Ayurveda").collection("Ayurvedic Wellness");
        reviewRef = db.collection("Products").document("Ayurveda").collection("Ayurvedic Wellness")
                .document(productID).collection("Reviews");
        shoppingCartRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Shopping Cart");
    }

    private void setToolbarListeners() {
        toolbar_backBTN = findViewById(R.id.toolbar_back_btn);
        searchBTN = (ImageButton) findViewById(R.id.toolbar_search_btn);
        shoppingCartBTN = (ImageButton) findViewById(R.id.toolbar_shopping_btn);
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

        shoppingCartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ShoppingCartActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}