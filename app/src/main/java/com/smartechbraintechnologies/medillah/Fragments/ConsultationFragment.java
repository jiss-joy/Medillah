package com.smartechbraintechnologies.medillah.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartechbraintechnologies.medillah.Activities.DoctorListActivity;
import com.smartechbraintechnologies.medillah.Activities.ProductListActivity;
import com.smartechbraintechnologies.medillah.Adapters.AdapterCategory;
import com.smartechbraintechnologies.medillah.R;

public class ConsultationFragment extends Fragment implements AdapterCategory.OnCategoryClickListener {

    private final String[] categories = {"Family Physician", "Pediatrician", "Gynecologist",
            "Surgeon", "Psychiatrist", "Cardiologist", "Dermatologist", "Gastroenterologist", "Nephrologist",
            "Ophthalmologist", "Otolaryngologist", "Pulmonologist", "Neurologist",
            "Radiologist", "Anesthesiologist", "Oncologist"};
    private final int[] categoryImages = {R.drawable.family_physician, R.drawable.pediatrician, R.drawable.gynecologist,
            R.drawable.surgeon, R.drawable.psychiatrist, R.drawable.cardiologist, R.drawable.dermatologist,
            R.drawable.gastroenterologist, R.drawable.nephrologist, R.drawable.ophthalmologist,
            R.drawable.otolaryngologist, R.drawable.pulmonologist, R.drawable.neurologist, R.drawable.radiologist,
            R.drawable.anesthesiologist, R.drawable.oncologist};

    private RecyclerView recyclerView;
    private AdapterCategory categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation, container, false);

        initValues(view);

        setUpRecycler();
        return view;
    }

    private void setUpRecycler() {
        categoryAdapter = new AdapterCategory(getContext(), categoryImages, categories, ConsultationFragment.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(categoryAdapter);
    }

    private void initValues(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.consultations_specialities_recycler);
    }

    @Override
    public void OnCategoryClick(int position, View view) {
        startActivity(new Intent(getActivity(), DoctorListActivity.class).putExtra("Doctor Category", categories[position]));
    }
}
