package com.smartechbraintechnologies.medillah.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Adapters.AdapterLabTest;
import com.smartechbraintechnologies.medillah.Activities.LabTestDetailsActivity;
import com.smartechbraintechnologies.medillah.Models.ModelLabTest;
import com.smartechbraintechnologies.medillah.R;

import java.util.ArrayList;

public class LabTestFragment extends Fragment implements AdapterLabTest.OnLabTestClickListener {

    private RecyclerView labRecyclerView;
    private LinearLayout noLabLayout;

    private FirebaseFirestore db;
    private CollectionReference labRef;
    private AdapterLabTest pAdapter;
    private ArrayList<ModelLabTest> labTest = new ArrayList<>();
    private ArrayList<String> labTestID = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lab_test, container, false);

        initValues(view);

        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {
        labRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                labTest.clear();
                labTestID.clear();
                for (DocumentSnapshot document : value.getDocuments()) {
                    String image = document.getString("testImage");
                    String name = document.getString("testName");
                    Double mrp = document.getDouble("testMRP");
                    Double sp = document.getDouble("testSellingPrice");

                    ModelLabTest modelLabTest = new ModelLabTest(image, name, mrp, sp);
                    labTestID.add(document.getId());
                    labTest.add(modelLabTest);
                }

                if (labTest.size() == 0) {
                    labRecyclerView.setVisibility(View.GONE);
                    noLabLayout.setVisibility(View.VISIBLE);
                } else {
                    pAdapter = new AdapterLabTest(getActivity(), labTest, LabTestFragment.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    labRecyclerView.setLayoutManager(linearLayoutManager);
                    labRecyclerView.setAdapter(pAdapter);
                    labRecyclerView.setVisibility(View.VISIBLE);
                    noLabLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    private void initValues(View view) {
        labRecyclerView = (RecyclerView) view.findViewById(R.id.lab_test_list_recycler);
        noLabLayout = (LinearLayout) view.findViewById(R.id.lab_test_no_labs_layout);

        db = FirebaseFirestore.getInstance();
        labRef = db.collection("Lab Tests");
    }

    @Override
    public void OnLabTestClick(int position, View view) {
        String id = labTestID.get(position);
        startActivity(new Intent(getActivity(), LabTestDetailsActivity.class).putExtra("LAB ID", id));
    }
}
