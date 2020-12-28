package com.smartechbraintechnologies.medillah.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ornach.nobobutton.NoboButton;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private final Map<String, Object> orderDetails = new HashMap<>();
    private NoboButton cancelBTN, deliverBTN;
    private ImageView address_icon;
    private TextView orderStatusLong, orderStatusShort, order_ID, time, pName, pPrice, pQty,
            address, address_type, no_delivery, pDelivery;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CollectionReference addressRef;
    private CollectionReference orderRef;

    private String orderID, userDeliveryCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent intent = getIntent();
        orderID = intent.getStringExtra("ORDER ID");

        setToolbarListeners();
        initValues();
        loadingDialog.showLoadingDialog("Loading Order Summary...");

        loadAddress();

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCancellations();
            }
        });

        deliverBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeliveryCode();
            }
        });

    }

    private void confirmCancellations() {

        final Dialog dialog = new Dialog(OrderDetailsActivity.this);
        dialog.setContentView(R.layout.popup_order_cancellation);
        Window window = dialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ExtendedFloatingActionButton confirm_btn = dialog.findViewById(R.id.confirm_cancel_confirm);
        ExtendedFloatingActionButton cancel_btn = dialog.findViewById(R.id.confirm_cancel_cancel);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadingDialog.showLoadingDialog("Cancelling Order...");
                cancelOrder();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getDeliveryCode() {
        final Dialog dialog = new Dialog(OrderDetailsActivity.this);
        dialog.setContentView(R.layout.popup_delivery_code);
        Window window = dialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final EditText deliveryCode_et = dialog.findViewById(R.id.delivery_code);
        Button submitBTN = dialog.findViewById(R.id.delivery_code_next);
        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDeliveryCode = deliveryCode_et.getText().toString();
                if (userDeliveryCode.isEmpty()) {
                    Toast.makeText(OrderDetailsActivity.this, "Please enter the delivery code.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    loadingDialog.showLoadingDialog("Authenticating Delivery Code");
                    authenticateDeliveryCode();
                }
            }
        });
        dialog.show();
    }

    private void authenticateDeliveryCode() {
        orderRef.document(orderID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (userDeliveryCode.equals(task.getResult().getString("orderDeliveryCode"))) {
                        orderDetails.put("orderStatus", "D");
                        orderRef.document(orderID).update(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                cancelBTN.setVisibility(View.GONE);
                                deliverBTN.setVisibility(View.GONE);
                                loadingDialog.dismissLoadingDialog();
                                Toast.makeText(OrderDetailsActivity.this, "Product Successfully Delivered!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(OrderDetailsActivity.this, "Invalid Delivery Code", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void cancelOrder() {
        orderDetails.put("orderStatus", "F");
        orderRef.document(orderID).update(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                cancelBTN.setVisibility(View.GONE);
                deliverBTN.setVisibility(View.GONE);
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(OrderDetailsActivity.this, "Your order was cancelled by you.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAddress() {
        addressRef.whereEqualTo("addressStatus", "Default").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                DocumentSnapshot documentSnapshot = value.getDocuments().get(0);
                String type = documentSnapshot.getString("addressType");
                address.setText(documentSnapshot.getString("address"));
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

                if (delivery_status.equals("No Delivery")) {
                    no_delivery.setVisibility(View.VISIBLE);
                } else {
                    no_delivery.setVisibility(View.GONE);
                }
            }

        });
        loadOrderSummary();
    }

    private void loadOrderSummary() {
        orderRef.document(orderID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                switch (value.getString("orderStatus")) {
                    case "B":
                        orderStatusShort.setText("Placed");
                        orderStatusLong.setText(R.string.order_placed);
                        orderStatusShort.setTextColor(getResources().getColor(R.color.white));
                        orderStatusShort.setBackgroundResource(R.drawable.custom_unchecked_radio);
                        deliverBTN.setVisibility(View.GONE);
                        cancelBTN.setVisibility(View.VISIBLE);
                        break;
                    case "A":
                        orderStatusShort.setText("Accepted");
                        orderStatusShort.setTextColor(getResources().getColor(R.color.secondary));
                        orderStatusShort.setBackgroundResource(R.drawable.custom_checked_radio);
                        orderStatusLong.setText(R.string.order_accepted);
                        cancelBTN.setVisibility(View.GONE);
                        deliverBTN.setVisibility(View.GONE);
                        break;
                    case "E":
                        orderStatusShort.setText("Declined");
                        orderStatusShort.setTextColor(getResources().getColor(R.color.white));
                        orderStatusShort.setBackgroundResource(R.drawable.custom_field_red);
                        orderStatusLong.setText(R.string.order_declined);
                        cancelBTN.setVisibility(View.GONE);
                        deliverBTN.setVisibility(View.GONE);
                        break;
                    case "D":
                        orderStatusShort.setText("Delivered");
                        orderStatusShort.setTextColor(getResources().getColor(R.color.white));
                        orderStatusShort.setBackgroundResource(R.drawable.custom_field_green);
                        orderStatusLong.setText(R.string.order_delivered);
                        cancelBTN.setVisibility(View.GONE);
                        deliverBTN.setVisibility(View.GONE);
                        break;
                    case "C":
                        orderStatusShort.setText("Delivery");
                        orderStatusShort.setTextColor(getResources().getColor(R.color.white));
                        orderStatusShort.setBackgroundResource(R.drawable.custom_field_orange);
                        orderStatusLong.setText(R.string.order_delivery);
                        cancelBTN.setVisibility(View.GONE);
                        deliverBTN.setVisibility(View.VISIBLE);
                        break;
                    case "F":
                        orderStatusShort.setText("Cancelled");
                        orderStatusShort.setTextColor(getResources().getColor(R.color.white));
                        orderStatusShort.setBackgroundResource(R.drawable.custom_field_red);
                        orderStatusLong.setText(R.string.order_cancelled);
                        cancelBTN.setVisibility(View.GONE);
                        deliverBTN.setVisibility(View.GONE);
                        break;
                }
                pName.setText(value.getString("orderName"));
                pQty.setText(String.valueOf(value.getDouble("orderQuantity")));
                pDelivery.setText("₹" + value.getDouble("deliveryCharge"));
                pPrice.setText("₹" + value.getDouble("orderTotal"));
                order_ID.setText(value.getId());
                time.setText("On " + value.getString("orderDate") + " at " + value.getString("orderTime"));
                loadingDialog.dismissLoadingDialog();
            }
        });
    }

    private void initValues() {
        order_ID = findViewById(R.id.order_details_order_id);
        orderStatusLong = findViewById(R.id.order_details_status_tv);
        orderStatusShort = findViewById(R.id.order_details_short_product_status);
        pName = findViewById(R.id.order_details_short_product_name);
        pPrice = findViewById(R.id.order_details_short_total);
        pQty = findViewById(R.id.order_details_short_qty);
        pDelivery = findViewById(R.id.order_details_short_delivery_charge);
        address = findViewById(R.id.address_card_address);
        time = findViewById(R.id.order_details_order_time);
        cancelBTN = (NoboButton) findViewById(R.id.order_details_cancel_order_btn);
        deliverBTN = (NoboButton) findViewById(R.id.order_details_delivery_btn);
        address_icon = (ImageView) findViewById(R.id.address_card_type_image);
        address_type = (TextView) findViewById(R.id.address_card_address_type);
        no_delivery = (TextView) findViewById(R.id.address_card_no_delivery);
        no_delivery.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(this);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        addressRef = db.collection("Users").document(currentUser.getUid()).collection("Addresses");
        orderRef = db.collection("Orders");
    }

    @Override
    public void onBackPressed() {
        finish();
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

}