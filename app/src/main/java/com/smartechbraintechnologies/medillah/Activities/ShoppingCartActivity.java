package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ornach.nobobutton.NoboButton;
import com.smartechbraintechnologies.medillah.Adapters.AdapterCartItem;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.Models.ModelCartItem;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity implements AdapterCartItem.OnDeleteClickListener {

    private RecyclerView cartItemsRecycler;
    private TextView address_type, address_tv, address_status, no_delivery;
    private TextView total, gst, delivery, savings, bottomTotal;
    private View view;
    private ImageButton toolbar_backBTN, searchBTN;
    private LoadingDialog loadingDialog;
    private Button addAddressBTN;
    private ImageButton backBTN, optionsBTN;
    private ImageView address_icon;
    private NoboButton placeOrderBTN;
    private LinearLayout noCartItemsLayout;
    private CardView orderDetailsLayout;

    private AdapterCartItem cAdapter;
    private ArrayList<ModelCartItem> cartItems = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference userRef;
    private CollectionReference addressRef;
    private CollectionReference shoppingCartRef;
    private CollectionReference productRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        setToolbarListeners();

        initValues();
//        loadingDialog.showLoadingDialog("Loading details...");


        loadCartItems();

        loadAddress();

//        loadOrderDetails();

        placeOrderBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });


    }

    private void placeOrder() {

    }

    private void loadAddress() {
        addressRef.whereEqualTo("addressStatus", "Default").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.getDocuments().isEmpty()) {
                    addAddressBTN.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                } else {
                    addAddressBTN.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    DocumentSnapshot documentSnapshot = value.getDocuments().get(0);
                    String type = documentSnapshot.getString("addressType");
                    String address = documentSnapshot.getString("address");
                    String delivery_status = documentSnapshot.getString("addressDeliveryStatus");

                    if (type.equals("Home")) {
                        address_icon.setImageResource(R.drawable.home);
                        address_type.setText(type);
                    } else if (type.equals("Work")) {
                        address_icon.setImageResource(R.drawable.work);
                        address_type.setText(type);
                    } else {
                        address_icon.setImageResource(R.drawable.location);
                        address_type.setText(type);
                    }
                    address_tv.setText(address);
                    if (delivery_status.equals("No Delivery")) {
                        no_delivery.setVisibility(View.VISIBLE);
                    } else {
                        no_delivery.setVisibility(View.GONE);
                    }
                }
            }

        });
    }

    private void loadCartItems() {
        shoppingCartRef.whereNotEqualTo("productID", "null")
                .orderBy("productID", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        cartItems.clear();
                        if (value.getDocuments().isEmpty()) {
                            noCartItemsLayout.setVisibility(View.VISIBLE);
                            cartItemsRecycler.setVisibility(View.GONE);
                            orderDetailsLayout.setVisibility(View.GONE);
                            addAddressBTN.setVisibility(View.GONE);
                            view.setVisibility(View.GONE);
                        } else {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                String productID = documentSnapshot.getString("productID");
                                Integer productQuantity = documentSnapshot.getDouble("productQuantity").intValue();

                                productRef.document(productID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot1 = task.getResult();
                                            ArrayList<String> productImage = (ArrayList<String>) documentSnapshot1.get("productImage");
                                            String productName = documentSnapshot1.getString("productName");
                                            String productBrand = documentSnapshot1.getString("productBrand");
                                            Double productSellingPrice = documentSnapshot1.getDouble("productSellingPrice");
                                            Double productMRP = documentSnapshot1.getDouble("productMRP");
                                            Double productDiscount = documentSnapshot1.getDouble("productDiscount");
                                            Double productSavings = productMRP - productSellingPrice;

                                            String a = total.getText().toString();
                                            total.setText(String.valueOf(Double.parseDouble(a) + productMRP * productQuantity));

                                            gst.setText(String.valueOf(Math.floor(Double.parseDouble(total.getText().toString()) * 0.18)));

                                            String c = savings.getText().toString();
                                            savings.setText(String.valueOf(Math.ceil(Double.parseDouble(c) + productSavings)));

//                                            String t = total.getText().toString();
//                                            total.setText(String.valueOf(Double.parseDouble(t) + (a - c + ga)));

                                            ModelCartItem modelCartItem = new ModelCartItem(productImage.get(0), productID, productBrand, productName, productSellingPrice,
                                                    productMRP, productDiscount, productSavings, productQuantity);

                                            cartItems.add(modelCartItem);
                                        }
                                    }
                                });
                            }
                            cartItemsRecycler.setVisibility(View.VISIBLE);
                            orderDetailsLayout.setVisibility(View.VISIBLE);
                            noCartItemsLayout.setVisibility(View.GONE);
                            cAdapter = new AdapterCartItem(ShoppingCartActivity.this, cartItems,
                                    ShoppingCartActivity.this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ShoppingCartActivity.this);
                            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            cartItemsRecycler.setAdapter(cAdapter);
                            cartItemsRecycler.setLayoutManager(linearLayoutManager);
                        }

                    }
                });
    }

    private void initValues() {
        cartItemsRecycler = (RecyclerView) findViewById(R.id.shopping_cart_recycler_view);

        view = (View) findViewById(R.id.shopping_cart_address_card);
        address_icon = (ImageView) findViewById(R.id.address_card_type_image);
        address_status = (TextView) findViewById(R.id.address_card_address_status);
        address_tv = (TextView) findViewById(R.id.address_card_address);
        address_type = (TextView) findViewById(R.id.address_card_address_type);
        no_delivery = (TextView) findViewById(R.id.address_card_no_delivery);
        no_delivery.setVisibility(View.GONE);
        optionsBTN = (ImageButton) findViewById(R.id.address_card_options_btn);
        addAddressBTN = (Button) findViewById(R.id.shopping_cart_add_address_btn);
        placeOrderBTN = (NoboButton) findViewById(R.id.shopping_cart_place_order_btn);
        total = (TextView) findViewById(R.id.shopping_cart_total_cart_mrp);
        gst = (TextView) findViewById(R.id.shopping_cart_total_gst);
        savings = (TextView) findViewById(R.id.shopping_cart_total_savings);
        bottomTotal = (TextView) findViewById(R.id.shopping_cart_bottom_bar_total);

        total.setText("0.0");
        gst.setText("0.0");
        savings.setText("0.0");
        bottomTotal.setText("0.0");

        loadingDialog = new LoadingDialog(ShoppingCartActivity.this);
        noCartItemsLayout = (LinearLayout) findViewById(R.id.no_cart_items_layout);
        orderDetailsLayout = (CardView) findViewById(R.id.shopping_cart_details_layout);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = db.collection("Users");
        addressRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Addresses");
        shoppingCartRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Shopping Cart");
        productRef = db.collection("Products");
    }

    private void setToolbarListeners() {
        toolbar_backBTN = findViewById(R.id.toolbar_back_btn);
        searchBTN = (ImageButton) findViewById(R.id.toolbar_search_btn);
        searchBTN.setVisibility(View.GONE);
        toolbar_backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDeleteClick(int position, View v) {
        //TODO: Finish of Shopping Cart Functionality
        shoppingCartRef.document(cartItems.get(position).getProductID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                shoppingCartRef.document("Cart Quantity").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Double qty = task.getResult().getDouble("cartQuantity");
                        shoppingCartRef.document("Cart Quantity").update("cartQuantity", qty - 1);
                    }
                });
            }
        });
    }
}