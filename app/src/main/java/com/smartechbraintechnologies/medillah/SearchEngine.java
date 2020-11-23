package com.smartechbraintechnologies.medillah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Activities.MyAddressesActivity;
import com.smartechbraintechnologies.medillah.Activities.ProductDetailsActivity;
import com.smartechbraintechnologies.medillah.Adapters.AdapterProductShort;
import com.smartechbraintechnologies.medillah.Adapters.AdapterSearchHistory;
import com.smartechbraintechnologies.medillah.Models.ModelProductShort;

import java.util.ArrayList;
import java.util.Date;

public class SearchEngine extends AppCompatActivity implements AdapterSearchHistory.OnSearchHistoryClickListener {

    public static final long OTP_START_TIME_IN_MILLIS = 0;

    private SearchView searchEngine;
    private RecyclerView searchResultRecycler, searchHistoryRecycler;
    private LinearLayout noResultView, historyLayout;
    private ProgressBar progressBar;

    private AdapterProductShort sAdapter;
    private AdapterSearchHistory hAdapter;
    private FirebaseFirestore db;
    private CollectionReference productRef;

    private DatabaseHelper sqlDB;
    private Cursor cursor;

    private ArrayList<String> searchResults = new ArrayList<>();
    private ArrayList<String> searchHistory = new ArrayList<>();
    private ArrayList<String> searchProductID = new ArrayList<>();

    private CountDownTimer mCountDownTimer;
    private boolean isCountDownTimerRunning = false;
    private long mTimeLeftInMillis = OTP_START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_engine);

        initValues();

        setUpSearchView();

        loadSearchHistory();

    }

    private void loadSearchHistory() {
        cursor = sqlDB.readData();
        if (!(cursor.getCount() == 0)) {
            while (cursor.moveToNext()) {
                searchHistory.add(cursor.getString(0));
                searchProductID.add(cursor.getString(1));
            }
            if (searchHistory.isEmpty()) {
                historyLayout.setVisibility(View.GONE);
            } else {
                historyLayout.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchEngine.this);
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                searchHistoryRecycler.setLayoutManager(linearLayoutManager);
                hAdapter = new AdapterSearchHistory(SearchEngine.this, searchHistory, SearchEngine.this);
                searchHistoryRecycler.setAdapter(hAdapter);
                hAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setUpSearchView() {
        searchEngine.setIconified(false);
        searchEngine.setFocusable(true);

        searchEngine.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    if (isCountDownTimerRunning) {
                        mCountDownTimer.cancel();
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    startTimer(newText.toLowerCase());
                } else if (newText.equals("")) {
                    historyLayout.setVisibility(View.VISIBLE);
                    searchResultRecycler.setVisibility(View.GONE);
                    noResultView.setVisibility(View.GONE);
                } else {
                    historyLayout.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void startTimer(String newText) {
        isCountDownTimerRunning = true;
        mTimeLeftInMillis = OTP_START_TIME_IN_MILLIS;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                search(newText);
            }
        }.start();
    }

    private void search(String newText) {
        productRef.orderBy("searchKeywords", Query.Direction.ASCENDING)
                .whereArrayContains("searchKeywords", newText).limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //Checking if there are any matching products.
                    if (task.getResult().getDocuments().size() == 0) {
                        progressBar.setVisibility(View.GONE);
                        searchResultRecycler.setVisibility(View.GONE);
                        historyLayout.setVisibility(View.GONE);
                        noResultView.setVisibility(View.VISIBLE);
                    } else {
                        //Displaying the matching products.
                        Query query = productRef.orderBy("searchKeywords", Query.Direction.ASCENDING)
                                .whereArrayContains("searchKeywords", newText)
                                .limit(10);

                        PagedList.Config config = new PagedList.Config.Builder()
                                .setInitialLoadSizeHint(5)
                                .setPageSize(2)
                                .build();

                        FirestorePagingOptions<ModelProductShort> options = new FirestorePagingOptions.Builder<ModelProductShort>()
                                .setLifecycleOwner(SearchEngine.this)
                                .setQuery(query, config, new SnapshotParser<ModelProductShort>() {
                                    @NonNull
                                    @Override
                                    public ModelProductShort parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                                        ModelProductShort modelProductShort = snapshot.toObject(ModelProductShort.class);
                                        String id = snapshot.getId();
                                        searchResults.add(id);
                                        return modelProductShort;
                                    }
                                })
                                .build();

                        sAdapter = new AdapterProductShort(options, SearchEngine.this);
                        searchResultRecycler.setHasFixedSize(true);
                        searchResultRecycler.setLayoutManager(new LinearLayoutManager(SearchEngine.this));
                        searchResultRecycler.setAdapter(sAdapter);
                        sAdapter.setOnProductClickListener(new AdapterProductShort.OnProductClickListener() {
                            @Override
                            public void OnProductClick(DocumentSnapshot documentSnapshot, int position) {
                                Date date = new Date();
                                long timeMilli = date.getTime();
                                sqlDB.AddSearch(documentSnapshot.getString("productName"), documentSnapshot.getId(), String.valueOf(timeMilli));
                                finish();
                                startActivity(new Intent(SearchEngine.this, ProductDetailsActivity.class).putExtra("Product ID", documentSnapshot.getId()));
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        historyLayout.setVisibility(View.GONE);
                        noResultView.setVisibility(View.GONE);
                        searchResultRecycler.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void initValues() {
        searchEngine = (SearchView) findViewById(R.id.search_engine);
        searchResultRecycler = (RecyclerView) findViewById(R.id.search_engine_recycler_view);
        searchHistoryRecycler = (RecyclerView) findViewById(R.id.search_engine_search_history_recycler_view);
        noResultView = (LinearLayout) findViewById(R.id.search_engine_no_result_view);
        historyLayout = (LinearLayout) findViewById(R.id.search_engine_history_layout);
        progressBar = (ProgressBar) findViewById(R.id.search_engine_progress_bar);
        progressBar.setVisibility(View.GONE);
        noResultView.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        sqlDB = new DatabaseHelper(this);
        productRef = db.collection("Products").document("Ayurveda").collection("Ayurvedic Wellness");

    }

    @Override
    public void OnSearchHistoryClick(int position, View view) {
        Date date = new Date();
        long timeMilli = date.getTime();
        sqlDB.updateSearch(searchProductID.get(position), String.valueOf(timeMilli));
        finish();
        startActivity(new Intent(SearchEngine.this, ProductDetailsActivity.class)
                .putExtra("Product ID", searchProductID.get(position)));
    }
}