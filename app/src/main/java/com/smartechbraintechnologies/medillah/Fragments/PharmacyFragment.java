package com.smartechbraintechnologies.medillah.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartechbraintechnologies.medillah.Activities.ProductListActivity;
import com.smartechbraintechnologies.medillah.Activities.ProfileActivity;
import com.smartechbraintechnologies.medillah.Adapters.AdapterCategory;
import com.smartechbraintechnologies.medillah.Adapters.AdapterImageSlider;
import com.smartechbraintechnologies.medillah.MainActivity;
import com.smartechbraintechnologies.medillah.R;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class PharmacyFragment extends Fragment implements AdapterCategory.OnCategoryClickListener {

    private final int[] images = {R.drawable.offer1, R.drawable.offer2, R.drawable.offer3};
    private final String[] categories = {"Ayurveda", "Nutrition & Supplements", "Sexual Wellness",
            "Devices & Accessories", "Baby Care", "Woman Healthcare"};
    private final int[] categoryImages = {R.drawable.ayurveda, R.drawable.nutrition, R.drawable.sexual_wellness,
            R.drawable.healthcare_devices, R.drawable.baby_care, R.drawable.woman};
    private SliderView sliderView;
    private RecyclerView recyclerView_1, recyclerView_2;
    private AdapterImageSlider sliderAdapter;
    private AdapterCategory categoryAdapter;
    private Button add_prescription_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pharmacy, container, false);

        initValues(view);

        setUpSlider_1();
        setUpRecycler_2();

        add_prescription_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });

        return view;
    }

    private void setUpRecycler_2() {
        categoryAdapter = new AdapterCategory(getContext(), categoryImages, categories, PharmacyFragment.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView_2.setLayoutManager(gridLayoutManager);
        recyclerView_2.setAdapter(categoryAdapter);
    }

    private void setUpSlider_1() {
        sliderAdapter = new AdapterImageSlider(getContext(), images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.startAutoCycle();
    }

    private void initValues(View view) {
        sliderView = view.findViewById(R.id.slider_view);
        recyclerView_2 = (RecyclerView) view.findViewById(R.id.pharmacy_shopping_category_recycler_view);
        add_prescription_btn = (Button) view.findViewById(R.id.pharmacy_upload_prescription_btn);
    }

    @Override
    public void OnCategoryClick(int position, View view) {
        startActivity(new Intent(getActivity(), ProductListActivity.class));
    }
}
