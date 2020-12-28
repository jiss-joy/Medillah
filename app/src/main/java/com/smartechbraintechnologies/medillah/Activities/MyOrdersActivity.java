package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Adapters.AdapterOrders;
import com.smartechbraintechnologies.medillah.Models.ModelOrder;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.util.ArrayList;
import java.util.Collections;

public class MyOrdersActivity extends AppCompatActivity implements AdapterOrders.OnOrderClickListener {

    private ImageButton toolbar_backBTN, searchBTN;
    private RecyclerView orderRecyclerView;
    private LinearLayout noOrderLayout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference orderRef;

    private AdapterOrders oAdapter;
    private ArrayList<ModelOrder> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        setToolbarListeners();
        initValues();

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        orderRef.whereEqualTo("orderCustomerID", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        orderList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String id = document.getId();
                            String image = document.getString("orderImage");
                            String name = document.getString("orderName");
                            Double qty = document.getDouble("orderQuantity");
                            Double total = document.getDouble("orderTotal");
                            String status = document.getString("orderStatus");

                            ModelOrder modelOrder = new ModelOrder(id, image, name, status, qty, total);
                            orderList.add(modelOrder);
                        }

                        if (orderList.size() == 0) {
                            orderRecyclerView.setVisibility(View.GONE);
                            noOrderLayout.setVisibility(View.VISIBLE);
                        } else {
                            Collections.sort(orderList, (o1, o2) ->
                                    (o1.getOrderStatus().compareTo(o2.getOrderStatus())));

                            oAdapter = new AdapterOrders(MyOrdersActivity.this, orderList, MyOrdersActivity.this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyOrdersActivity.this);
                            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            orderRecyclerView.setLayoutManager(linearLayoutManager);
                            orderRecyclerView.setAdapter(oAdapter);
                            orderRecyclerView.setVisibility(View.VISIBLE);
                            noOrderLayout.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void initValues() {
        orderRecyclerView = (RecyclerView) findViewById(R.id.my_orders_recycler);
        noOrderLayout = (LinearLayout) findViewById(R.id.my_orders_no_order_layout);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        orderRef = db.collection("Orders");
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

    @Override
    public void OnOrderClick(int position, View view) {
        startActivity(new Intent(MyOrdersActivity.this, OrderDetailsActivity.class)
                .putExtra("ORDER ID", orderList.get(position).getOrderID()));
    }
}