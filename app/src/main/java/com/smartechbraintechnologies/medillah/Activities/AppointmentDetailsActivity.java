package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class AppointmentDetailsActivity extends AppCompatActivity {

    private final Map<String, Object> appointmentDetails = new HashMap<>();
    private NoboButton cancelBTN;
    private TextView appointmentStatusLong, appointmentStatusShort, appointment_ID, time, pName, pPrice, pQty,
            venueAddress, pDelivery;
    private TextView speciality_tv, timing_tv, total_tv;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference addressRef;
    private CollectionReference appointmentRef;

    private String appointmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        Intent intent = getIntent();
        appointmentID = intent.getStringExtra("APPOINTMENT ID");

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

        final Dialog dialog = new Dialog(AppointmentDetailsActivity.this);
        dialog.setContentView(R.layout.popup_order_cancellation);
        Window window = dialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ExtendedFloatingActionButton confirm_btn = dialog.findViewById(R.id.confirm_cancel_confirm);
        ExtendedFloatingActionButton cancel_btn = dialog.findViewById(R.id.confirm_cancel_cancel);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                loadingDialog.showLoadingDialog("Cancelling Appointment...");
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
        appointmentDetails.put("consultationStatus", "F");
        appointmentRef.document(appointmentID).update(appointmentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                cancelBTN.setVisibility(View.GONE);
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(AppointmentDetailsActivity.this, "This appointment was cancelled by you.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAddress() {
        addressRef.document("Pharmacy Details").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String venue = task.getResult().getString("pharmacyAddress");
                venueAddress.setText(venue);
                loadOrderSummary();
            }
        });

    }

    private void loadOrderSummary() {
        appointmentRef.document(appointmentID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                switch (value.getString("consultationStatus")) {
                    case "B":
                        appointmentStatusShort.setText("Placed");
                        appointmentStatusLong.setText("This appointment has been placed with Medillah.");
                        appointmentStatusShort.setTextColor(getResources().getColor(R.color.white));
                        appointmentStatusShort.setBackgroundResource(R.drawable.custom_unchecked_radio);
                        cancelBTN.setVisibility(View.VISIBLE);
                        break;
                    case "A":
                        appointmentStatusShort.setText("Accepted");
                        appointmentStatusShort.setTextColor(getResources().getColor(R.color.secondary));
                        appointmentStatusShort.setBackgroundResource(R.drawable.custom_checked_radio);
                        appointmentStatusLong.setText("This appointment has been accepted by Medillah.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                    case "E":
                        appointmentStatusShort.setText("Declined");
                        appointmentStatusShort.setTextColor(getResources().getColor(R.color.white));
                        appointmentStatusShort.setBackgroundResource(R.drawable.custom_field_red);
                        appointmentStatusLong.setText("This appointment has been declined by Medillah.\nSorry for the inconvenience caused.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                    case "D":
                        appointmentStatusShort.setText("Completed");
                        appointmentStatusShort.setTextColor(getResources().getColor(R.color.white));
                        appointmentStatusShort.setBackgroundResource(R.drawable.custom_field_green);
                        appointmentStatusLong.setText("This appointment has been completed.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                    case "C":
                        appointmentStatusShort.setText("In Progress");
                        appointmentStatusShort.setTextColor(getResources().getColor(R.color.white));
                        appointmentStatusShort.setBackgroundResource(R.drawable.custom_field_orange);
                        appointmentStatusLong.setText("This appointment is in progress.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                    case "F":
                        appointmentStatusShort.setText("Cancelled");
                        appointmentStatusShort.setTextColor(getResources().getColor(R.color.white));
                        appointmentStatusShort.setBackgroundResource(R.drawable.custom_field_red);
                        appointmentStatusLong.setText("This appointment was cancelled by you.");
                        cancelBTN.setVisibility(View.GONE);
                        break;
                }
                pName.setText(value.getString("consultationDoctorName"));
                pDelivery.setText(value.getString("consultationTiming") + " on " + value.getString("consultationDate"));
                pQty.setText(value.getString("consultationDoctorSpeciality"));
                pPrice.setText("₹" + value.getDouble("consultationTotalPrice"));
                appointment_ID.setText(value.getId());
                time.setText("On " + value.getString("consultationDate") + " at " + value.getString("consultationTime"));
                loadingDialog.dismissLoadingDialog();
            }
        });
    }

    private void initValues() {
        appointment_ID = findViewById(R.id.appointment_details_appointment_id);
        appointmentStatusLong = findViewById(R.id.appointment_details_status_tv);
        appointmentStatusShort = findViewById(R.id.order_details_short_product_status);
        pName = findViewById(R.id.order_details_short_product_name);
        pPrice = findViewById(R.id.order_details_short_total);
        pQty = findViewById(R.id.order_details_short_qty);
        pDelivery = findViewById(R.id.order_details_short_delivery_charge);
        time = findViewById(R.id.appointment_details_appointment_time);
        cancelBTN = (NoboButton) findViewById(R.id.appointment_details_cancel_btn);
        venueAddress = (TextView) findViewById(R.id.consultation_venue_address);

        speciality_tv = (TextView) findViewById(R.id.order_details_short_qty_tv);
        timing_tv = (TextView) findViewById(R.id.order_details_short_delivery_charge_tv);
        total_tv = (TextView) findViewById(R.id.order_details_short_total_tv);
        loadingDialog = new LoadingDialog(this);

        speciality_tv.setText("Specialiy: ");
        timing_tv.setText("Timing: ");
        total_tv.setText("Appointment Total: ");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        addressRef = db.collection("Medillah");

        appointmentRef = db.collection("Consultation Bookings");
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