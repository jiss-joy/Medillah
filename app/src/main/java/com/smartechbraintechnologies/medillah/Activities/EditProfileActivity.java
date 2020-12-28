package com.smartechbraintechnologies.medillah.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.MainActivity;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.ShowSnackbar;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    public static final int HEIGHT = 1;
    public static final int WEIGHT = 2;
    public static final int BLOOD_GROUP = 3;

    private EditText mName_tv;
    private TextView mDateOfBirth_tv, mHeight_tv, mWeight_tv, mBloodGroup_tv;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private Button nextBTN;
    private Chip cMale, cFemale, cOther;
    private RelativeLayout relativeLayout;
    private ChipGroup chipGroup;
    private final String[] bloodGroups = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};

    private String mName, mDob, mGender, mHeight, mWeight, mBloodGroup, mYear, mMonth, mDay;
    private int yearIndex = 1999, monthIndex = 1, dayIndex = 1, heightIndex = 80, weightIndex = 40, bloodGroupIndex = 5;
    private final String[] heights = new String[130];
    private final String[] weights = new String[130];
    private final Map<String, Object> user = new HashMap<>();
    private LoadingDialog loadingDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initValues();

        loadData();

        mHeight_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(heights, HEIGHT);
            }
        });

        mWeight_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(weights, WEIGHT);
            }
        });

        mBloodGroup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(bloodGroups, BLOOD_GROUP);
            }
        });

        mDateOfBirth_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog dialog = new DatePickerDialog(
                        EditProfileActivity.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mOnDateSetListener,
                        yearIndex, monthIndex - 1, dayIndex);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }

        });

        mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mYear = String.valueOf(year);
                mMonth = String.valueOf(month + 1);
                mDay = String.valueOf(day);
                yearIndex = year;
                monthIndex = month + 1;
                dayIndex = day;
                mDob = mDay + "/" + mMonth + "/" + mYear;
                mDateOfBirth_tv.setText(mDob);
            }
        };

        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.showLoadingDialog("Verifying Details...");
                validateData();
            }
        });
    }

    private void loadData() {
        userRef.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    mName = document.getString("userName");
                    mName_tv.setText(mName);
                    mHeight = document.getString("userHeight");
                    mHeight_tv.setText(mHeight);
                    mWeight = document.getString("userWeight");
                    mWeight_tv.setText(mWeight);
                    mBloodGroup = document.getString("userBloodGroup");
                    mBloodGroup_tv.setText(mBloodGroup);
                    mDay = document.getString("userDay");
                    mMonth = document.getString("userMonth");
                    mYear = document.getString("userYear");
                    mDob = mDay + "/" + mMonth + "/" + mYear;
                    mDateOfBirth_tv.setText(mDay + "/" + mMonth + "/" + mYear);

                    mGender = document.getString("userGender");
                    if (mGender.equals(cMale.getText().toString())) {
                        cMale.setChecked(true);
                    } else if (mGender.equals(cFemale.getText().toString())) {
                        cFemale.setChecked(true);
                    } else if (mGender.equals(cOther.getText().toString())) {
                        cOther.setChecked(true);
                    }
                }
            }
        });
    }

    private void validateData() {
        mName = mName_tv.getText().toString();
        int id = chipGroup.getCheckedChipId();
        chipGroup.getChildAt(chipGroup.getCheckedChipId());
        if (mName.isEmpty() || id == -1 || mHeight.isEmpty() || mWeight.isEmpty() || mBloodGroup.isEmpty() || mDob.isEmpty()) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(EditProfileActivity.this, getResources().getString(R.string.pleasefillallthedetails),
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else if (mName.contains("[0-9]+")) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(EditProfileActivity.this, getResources().getString(R.string.pleasecheckyourname),
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else {
            if (cMale.isChecked()) {
                mGender = "Male";
            } else if (cFemale.isChecked()) {
                mGender = "Female";
            } else if (cOther.isChecked()) {
                mGender = "Other";
            }
            loadingDialog.showLoadingDialog("Updating Details...");
            updateUserDetails();
        }
    }

    private void updateUserDetails() {
        user.put("userName", mName);
        user.put("userYear", mYear);
        user.put("userMonth", mMonth);
        user.put("userDay", mDay);
        user.put("userGender", mGender);
        user.put("userHeight", mHeight);
        user.put("userWeight", mWeight);
        user.put("userBloodGroup", mBloodGroup);
        userRef.document(mAuth.getCurrentUser().getUid()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingDialog.dismissLoadingDialog();
                    finish();
                }
            }
        });
    }

    private void showDialog(String[] arrayValues, int arrayType) {
        Dialog dialog = new Dialog(EditProfileActivity.this);

        dialog.setContentView(R.layout.popup_value_picker);
        TextView title = dialog.findViewById(R.id.value_picker_title);
        TextView units = dialog.findViewById(R.id.value_picker_unit);
        Button setBTN = dialog.findViewById(R.id.value_picker_set_btn);
        Button cancelBTN = dialog.findViewById(R.id.value_picker_cancel_btn);
        NumberPicker valuePicker = dialog.findViewById(R.id.value_picker);
        valuePicker.setMaxValue(arrayValues.length - 1);
        valuePicker.setMinValue(0);
        valuePicker.setWrapSelectorWheel(true);
        valuePicker.setDisplayedValues(arrayValues);
        switch (arrayType) {
            case HEIGHT:
                title.setText("Select your Height");
                units.setText("CM");
                valuePicker.setValue(heightIndex);
                break;
            case WEIGHT:
                title.setText("Select your Weight");
                units.setText("KG");
                valuePicker.setValue(weightIndex);
                break;
            case BLOOD_GROUP:
                valuePicker.setWrapSelectorWheel(false);
                title.setText("Select your Blood Group");
                units.setText("");
                valuePicker.setValue(bloodGroupIndex);
                break;
        }
        setBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (arrayType) {
                    case HEIGHT:
                        heightIndex = valuePicker.getValue();
                        mHeight = heights[heightIndex];
                        mHeight_tv.setText(mHeight + " cm");
                        break;
                    case WEIGHT:
                        weightIndex = valuePicker.getValue();
                        mWeight = weights[weightIndex];
                        mWeight_tv.setText(mWeight + " kg");
                        break;
                    case BLOOD_GROUP:
                        bloodGroupIndex = valuePicker.getValue();
                        mBloodGroup = bloodGroups[bloodGroupIndex];
                        mBloodGroup_tv.setText(mBloodGroup);
                        break;
                }
                dialog.dismiss();
            }
        });
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initValues() {
        mName_tv = findViewById(R.id.edit_profile_name);
        mDateOfBirth_tv = findViewById(R.id.edit_profile_dob);
        mHeight_tv = findViewById(R.id.edit_profile_height);
        mWeight_tv = findViewById(R.id.edit_profile_weight);
        mBloodGroup_tv = findViewById(R.id.edit_profile_blood_group);
        nextBTN = findViewById(R.id.edit_profile_next_btn);
        relativeLayout = findViewById(R.id.edit_profile_layout);
        chipGroup = findViewById(R.id.edit_profile_chip_group);
        loadingDialog = new LoadingDialog(EditProfileActivity.this);

        cMale = findViewById(R.id.edit_profile_chip_male);
        cFemale = findViewById(R.id.edit_profile_chip_female);
        cOther = findViewById(R.id.edit_profile_chip_other);

        initializeStringArrays();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = db.collection("Users");
    }

    private void initializeStringArrays() {
        for (int i = 0; i < 130; i++) {
            heights[i] = String.valueOf(i + 80);
            weights[i] = String.valueOf(i + 20);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}