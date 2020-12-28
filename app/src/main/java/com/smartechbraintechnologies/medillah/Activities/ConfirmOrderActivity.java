package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConfirmOrderActivity extends AppCompatActivity {

    public static float DELIVERY_CHARGE;

    private LinearLayout placeOrderBTN, noAddressLayout;
    private ImageButton optionsBTN;
    private CardView addressCard;
    private ImageView typeImage;
    private TextView productName, mrp, qty, total, type, address, discount, gst,
            address_delivery_status, delivery_charge_tv, delivery_charge;
    private LoadingDialog loadingDialog;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private CollectionReference addressRef;
    private CollectionReference orderRef;
    private CollectionReference productRef;

    private String id;
    private String quantity;
    private Double orderTotal;
    private final Map<String, Object> order = new HashMap<>();
    private final Map<String, Object> orderAddress = new HashMap<>();
    private boolean addressFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        Intent intent = getIntent();
        id = intent.getStringExtra("Product Id");
        quantity = intent.getStringExtra("Quantity");

        setToolbarListeners();

        initValues();
        loadingDialog.showLoadingDialog("Loading Details");

        loadAddress();

        loadOrderDetails();

        optionsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ConfirmOrderActivity.this, v);
                popupMenu.inflate(R.menu.menu_change_address_option);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_address_options_change_address:
                                Intent intent = new Intent(ConfirmOrderActivity.this, MyAddressesActivity.class);
                                startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        placeOrderBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addressFlag) {
                    Toast.makeText(ConfirmOrderActivity.this, "Please add an address to continue.", Toast.LENGTH_LONG).show();
                } else {
                    loadingDialog.showLoadingDialog("Placing order...");
                    placeOrder();
                }
            }
        });

        noAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmOrderActivity.this, MyAddressesActivity.class));
            }
        });
    }

    private void placeOrder() {
        productRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Random random = new Random();
                String deliveryCode = String.valueOf(random.nextInt(9000) + 1000);

                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String currentDate = dateFormat.format(date);

                Date time = Calendar.getInstance().getTime();
                DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                String currentTime = timeFormat.format(time);

                DocumentSnapshot document = task.getResult();

                ArrayList<String> images = new ArrayList<>();
                images = (ArrayList<String>) document.get("productImage");
                order.put("orderName", document.getString("productName"));
                order.put("orderImage", images.get(0));
                order.put("orderTotal", Double.parseDouble(new DecimalFormat("#.00").format(orderTotal)));
                order.put("orderProductID", id);
                order.put("orderCustomerID", mAuth.getCurrentUser().getUid());
                order.put("orderQuantity", Double.parseDouble(quantity));
                order.put("orderDate", currentDate);
                order.put("orderTime", currentTime);
                order.put("orderDeliveryCode", deliveryCode);
                order.put("deliveryCharge", Double.parseDouble(String.valueOf(DELIVERY_CHARGE)));
                order.put("orderStatus", "B");
                orderRef.add(order).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            String orderID = task.getResult().getId();
                            addressRef.whereEqualTo("addressStatus", "Default").limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    orderAddress.put("address", task.getResult().getDocuments().get(0).getString("address"));
                                    orderAddress.put("addressLatitude", task.getResult().getDocuments().get(0).getDouble("addressLatitude"));
                                    orderAddress.put("addressLongitude", task.getResult().getDocuments().get(0).getDouble("addressLongitude"));
                                    orderAddress.put("addressID", task.getResult().getDocuments().get(0).getId());
                                    orderRef.document(orderID).collection("Order Address").document(task.getResult().getDocuments().get(0).getId()).set(orderAddress)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        loadingDialog.dismissLoadingDialog();
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
        });


    }

    private void loadAddress() {
        addressRef.whereEqualTo("addressStatus", "Default").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()) {
                    addressFlag = false;
                    addressCard.setVisibility(View.GONE);
                    noAddressLayout.setVisibility(View.VISIBLE);
                } else {
                    noAddressLayout.setVisibility(View.GONE);
                    addressCard.setVisibility(View.VISIBLE);
                    addressFlag = true;
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        type.setText(documentSnapshot.getString("addressType"));
                        switch (documentSnapshot.getString("addressType")) {
                            case "Home":
                                Picasso.get().load(R.drawable.home).into(typeImage);
                                break;
                            case "Work":
                                Picasso.get().load(R.drawable.work).into(typeImage);
                                break;
                            case "Other":
                                Picasso.get().load(R.drawable.location).into(typeImage);
                                break;
                        }
                        address.setText(documentSnapshot.getString("address"));
                        if (documentSnapshot.getString("addressDeliveryStatus").equals("No Delivery")) {
                            address_delivery_status.setVisibility(View.VISIBLE);
                            delivery_charge_tv.setVisibility(View.GONE);
                            delivery_charge.setVisibility(View.GONE);
                            total.setVisibility(View.GONE);
                            placeOrderBTN.setEnabled(false);
                        } else if (documentSnapshot.getString("addressDeliveryStatus").equals("Delivery Charge")) {
                            address_delivery_status.setVisibility(View.GONE);
                            delivery_charge_tv.setVisibility(View.VISIBLE);
                            delivery_charge.setVisibility(View.VISIBLE);
                            total.setVisibility(View.VISIBLE);
                            placeOrderBTN.setEnabled(true);
                            DELIVERY_CHARGE = 50.0f;
                        } else {
                            address_delivery_status.setVisibility(View.GONE);
                            delivery_charge_tv.setVisibility(View.GONE);
                            delivery_charge.setVisibility(View.GONE);
                            total.setVisibility(View.VISIBLE);
                            placeOrderBTN.setEnabled(true);
                            DELIVERY_CHARGE = 0.0f;
                        }
                    }
                }
            }
        });
    }

    private void loadOrderDetails() {
        productRef.document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                productName.setText(value.getString("productName"));
                mrp.setText("₹" + value.getDouble("productMRP"));
                Double mrp = value.getDouble("productMRP");
                Double sp = value.getDouble("productSellingPrice");
                discount.setText(new DecimalFormat("-₹#.00").format(mrp - sp));
                qty.setText(quantity);
                gst.setText(new DecimalFormat("₹#.00").format((0.18 * mrp) * Double.parseDouble(quantity)));
                orderTotal = (sp + (0.18 * mrp)) * Double.valueOf(quantity) + DELIVERY_CHARGE;
                total.setText(new DecimalFormat("₹#.00").format(orderTotal));
                loadingDialog.dismissLoadingDialog();
            }
        });
    }

    private void initValues() {
        productName = findViewById(R.id.order_details_product_name);
        discount = findViewById(R.id.order_details_discount);
        qty = findViewById(R.id.order_details_qty);
        mrp = findViewById(R.id.order_details_mrp);
        gst = findViewById(R.id.order_details_gst);
        total = findViewById(R.id.order_details_total);
        type = findViewById(R.id.address_card_address_type);
        address = findViewById(R.id.address_card_address);
        address_delivery_status = findViewById(R.id.address_card_no_delivery);
        typeImage = findViewById(R.id.address_card_type_image);
        placeOrderBTN = findViewById(R.id.confirm_order_place_order_btn);
        optionsBTN = findViewById(R.id.address_card_options_btn);
        addressCard = findViewById(R.id.confirm_order_address_card);
        noAddressLayout = findViewById(R.id.confirm_order_no_address_layout);
        delivery_charge_tv = findViewById(R.id.order_details_delivery_charge_tv);
        delivery_charge = findViewById(R.id.order_details_delivery_charge);
        loadingDialog = new LoadingDialog(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        productRef = db.collection("Products");
        addressRef = db.collection("Users").document(currentUser.getUid()).collection("Addresses");
        orderRef = db.collection("Orders");
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