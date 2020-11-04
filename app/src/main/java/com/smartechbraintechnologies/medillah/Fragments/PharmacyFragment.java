package com.smartechbraintechnologies.medillah.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.smartechbraintechnologies.medillah.R;

public class PharmacyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pharmacy, container, false);

        ViewPager viewPager = view.findViewById(R.id.view_pager);
        ImageAdapter imageAdapter = new ImageAdapter(getContext());

        return view;
    }
}
