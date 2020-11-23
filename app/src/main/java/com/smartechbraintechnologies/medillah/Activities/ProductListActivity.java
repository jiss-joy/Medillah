package com.smartechbraintechnologies.medillah.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Adapters.AdapterProductShort;
import com.smartechbraintechnologies.medillah.Models.ModelProductShort;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.SearchEngine;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    private ImageButton toolbar_backBTN, searchBTN, shoppingCartBTN;
    private RecyclerView productRecyclerView;

    private FirebaseFirestore db;
    private CollectionReference productRef;
    private AdapterProductShort pAdapter;
    private ArrayList<String> productID = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        setToolbarListeners();
        initValues();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        productRef.limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().getDocuments().size() == 0) {
                    //Checking if there are any matching products.
                    Toast.makeText(ProductListActivity.this, "No Products", Toast.LENGTH_SHORT).show();
                } else {
                    //Displaying the matching products.
                    Query query = productRef.orderBy("productName", Query.Direction.ASCENDING).limit(3);

                    PagedList.Config config = new PagedList.Config.Builder()
                            .setInitialLoadSizeHint(6)
                            .setPageSize(2)
                            .build();

                    FirestorePagingOptions<ModelProductShort> options = new FirestorePagingOptions.Builder<ModelProductShort>()
                            .setLifecycleOwner(ProductListActivity.this)
                            .setQuery(query, config, new SnapshotParser<ModelProductShort>() {
                                @NonNull
                                @Override
                                public ModelProductShort parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                    ModelProductShort modelProductShort = snapshot.toObject(ModelProductShort.class);
                                    String id = snapshot.getId();
                                    productID.add(id);
                                    return modelProductShort;
                                }
                            })
                            .build();

                    pAdapter = new AdapterProductShort(options, ProductListActivity.this);
                    productRecyclerView.setHasFixedSize(true);
                    productRecyclerView.setLayoutManager(new LinearLayoutManager(ProductListActivity.this));
                    productRecyclerView.setAdapter(pAdapter);

                    pAdapter.setOnProductClickListener(new AdapterProductShort.OnProductClickListener() {
                        @Override
                        public void OnProductClick(DocumentSnapshot documentSnapshot, int position) {
                            startActivity(new Intent(ProductListActivity.this, ProductDetailsActivity.class).putExtra("Product ID", documentSnapshot.getId()));
                        }
                    });
                }
            }
        });

    }

    private void initValues() {
        productRecyclerView = (RecyclerView) findViewById(R.id.product_list_recycler_view);

        db = FirebaseFirestore.getInstance();
        productRef = db.collection("Products").document("Ayurveda").collection("Ayurvedic Wellness");
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
}