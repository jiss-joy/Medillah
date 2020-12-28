package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class MyLabTestDetailsActivity extends AppCompatActivity {

    private final Map<String, Object> testDetails = new HashMap<>();
    private NoboButton cancelBTN;
    private ImageView address_icon;
    private TextView testStatusLong, testStatusShort, test_ID, time, pName, pPrice, pQty,
            address, pDelivery, address_type, no_delivery;
    private TextView speciality_tv, timing_tv, total_tv;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference addressRef;
    private CollectionReference testRef;

    private String testID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lab_test_details);

        Intent intent = getIntent();
        testID = intent.getStringExtra("TEST ID");

        setToolbarListeners();

        initValues();
        loadingDialog.showLoadingDialog("Loading Details...");
        loadAddress();

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCancellations();
            }
        });
    }

    private void confirmCancellations() {

        final Dialog dialog = new Dialog(MyLabTestDetailsActivity.this);
        dialog.setContentView(R.layout.popup_order_cancellation);
        Window window = dialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ExtendedFloatingActionButton confirm_btn = dialog.findViewById(R.id.confirm_cancel_confirm);
        ExtendedFloatingActionButton cancel_btn = dialog.findViewById(R.id.confirm_cancel_cancel);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                loadingDialog.showLoadingDialog("Cancelling Lab Test...");
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


    private void cancelOrder() {
        testDetails.put("testOrderStatus", "F");
        testRef.document(testID).update(testDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                cancelBTN.setVisibility(View.GONE);
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(MyLabTestDetailsActivity.this, "This lab test was cancelled by you.", Toast.LENGTH_SHORT).show();
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
        testRef.document(testID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                switch (value.getString("testOrderStatus")) {
                    case "B":
                        testStatusShort.setText("Placed");
                        testStatusLong.setText("This lab test order has been placed with Medillah.");
                        testStatusShort.setTextColor(getResources().getColor(R.color.white));
                        testStatusShort.setBackgroundResource(R.drawable.custom_unchecked_radio);
                        cancelBTN.setVisibility(View.VISIBLE);
                        break;
                    case "A":
                        testStatusShort.setText("Accepted");
                        testStatusShort.setTextColor(getResources().getColor(R.color.secondary));
                        testStatusShort.setBackgroundResource(R.drawable.custom_checked_radio);
                        testStatusLong.setText("This lab test order has been accepted by Medillah.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                    case "E":
                        testStatusShort.setText("Declined");
                        testStatusShort.setTextColor(getResources().getColor(R.color.white));
                        testStatusShort.setBackgroundResource(R.drawable.custom_field_red);
                        testStatusLong.setText("This lab test order has been declined by Medillah.\nSorry for the inconvenience caused.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                    case "D":
                        testStatusShort.setText("Completed");
                        testStatusShort.setTextColor(getResources().getColor(R.color.white));
                        testStatusShort.setBackgroundResource(R.drawable.custom_field_green);
                        testStatusLong.setText("This lab test order has been completed.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                    case "C":
                        testStatusShort.setText("In Progress");
                        testStatusShort.setTextColor(getResources().getColor(R.color.white));
                        testStatusShort.setBackgroundResource(R.drawable.custom_field_orange);
                        testStatusLong.setText("This lab test order is in progress.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                    case "F":
                        testStatusShort.setText("Cancelled");
                        testStatusShort.setTextColor(getResources().getColor(R.color.white));
                        testStatusShort.setBackgroundResource(R.drawable.custom_field_red);
                        testStatusLong.setText("This lab test order was cancelled by you.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                }
                pName.setText(value.getString("testOrderName"));
                pDelivery.setText(value.getString("testOrderTiming") + " on " + value.getString("testOrderDate"));
                pQty.setVisibility(View.GONE);
                pPrice.setText("₹" + value.getDouble("testOrderTotalPrice"));
                test_ID.setText(value.getId());
                time.setText("On " + value.getString("testOrderDate") + " at " + value.getString("testOrderTime"));
                loadingDialog.dismissLoadingDialog();
            }
        });
    }

    private void initValues() {
        test_ID = findViewById(R.id.my_lab_test_details_test_id);
        testStatusLong = findViewById(R.id.my_lab_test_details_status_tv);
        testStatusShort = findViewById(R.id.order_details_short_product_status);
        pName = findViewById(R.id.order_details_short_product_name);
        pPrice = findViewById(R.id.order_details_short_total);
        pQty = findViewById(R.id.order_details_short_qty);
        pDelivery = findViewById(R.id.order_details_short_delivery_charge);
        time = findViewById(R.id.my_lab_test_details_test_time);
        cancelBTN = (NoboButton) findViewById(R.id.my_lab_test_details_cancel_btn);
        address = (TextView) findViewById(R.id.address_card_address);
        address_icon = (ImageView) findViewById(R.id.address_card_type_image);
        address_type = (TextView) findViewById(R.id.address_card_address_type);
        no_delivery = (TextView) findViewById(R.id.address_card_no_delivery);
        no_delivery.setVisibility(View.GONE);

        speciality_tv = (TextView) findViewById(R.id.order_details_short_qty_tv);
        timing_tv = (TextView) findViewById(R.id.order_details_short_delivery_charge_tv);
        total_tv = (TextView) findViewById(R.id.order_details_short_total_tv);
        loadingDialog = new LoadingDialog(this);

        speciality_tv.setVisibility(View.GONE);
        timing_tv.setText("Timing: ");
        total_tv.setText("Lab Test Total: ");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        addressRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Addresses");

        testRef = db.collection("Lab Test Bookings");
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