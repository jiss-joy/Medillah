package com.smartechbraintechnologies.medillah.Authentication;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.MainActivity;
import com.smartechbraintechnologies.medillah.Models.ModelAddress;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.ShowSnackbar;

import java.util.HashMap;
import java.util.Map;

public class ClientSurveyActivity extends AppCompatActivity {

    public static final int HEIGHT = 1;
    public static final int WEIGHT = 2;
    public static final int BLOOD_GROUP = 3;

    private EditText mName_tv;
    private TextView mDateOfBirth_tv, mHeight_tv, mWeight_tv, mBloodGroup_tv;
    private DatePickerDialog.OnDateSetListener mOnDateSetListener;
    private Button nextBTN;
    private RelativeLayout relativeLayout;
    private ChipGroup chipGroup;
    private final String[] bloodGroups = {"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"};

    private String mName, mPhoneNumber, mDob, mGender, mHeight, mWeight, mBloodGroup, mYear, mMonth, mDay;
    private int yearIndex = 1999;
    private int monthIndex = 1;
    private int dayIndex = 1;
    private int heightIndex = 80;
    private int weightIndex = 40;
    private int bloodGroupIndex = 5;
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
        setContentView(R.layout.activity_client_survey);

        initValues();

        mPhoneNumber = getIntent().getStringExtra("Phone Number");

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
                        ClientSurveyActivity.this,
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

    private void validateData() {
        mName = mName_tv.getText().toString();
        int id = chipGroup.getCheckedChipId();
        chipGroup.getChildAt(chipGroup.getCheckedChipId());
        if (mName.isEmpty() || id == -1 || mHeight.isEmpty() || mWeight.isEmpty() || mBloodGroup.isEmpty() || mDob.isEmpty()) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(ClientSurveyActivity.this, getResources().getString(R.string.pleasefillallthedetails),
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else if (mName.contains("[0-9]+")) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(ClientSurveyActivity.this, getResources().getString(R.string.pleasecheckyourname),
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else {
            switch (id) {
                case 1:
                    mGender = "Male";
                    break;
                case 2:
                    mGender = "Female";
                    break;
                case 3:
                    mGender = "Other";
                    break;
            }
            loadingDialog.showLoadingDialog("Please wait while we create your account...");
            addUserDetails();
        }

    }

    private void addUserDetails() {
        user.put("userName", mName);
        user.put("userPhone", mPhoneNumber);
        user.put("userYear", mYear);
        user.put("userMonth", mMonth);
        user.put("userDay", mDay);
        user.put("userGender", mGender);
        user.put("userHeight", mHeight);
        user.put("userWeight", mWeight);
        user.put("userBloodGroup", mBloodGroup);
        userRef.document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finish();
                    loadingDialog.dismissLoadingDialog();
                    startActivity(new Intent(ClientSurveyActivity.this, MainActivity.class));
                }
            }
        });
    }


    private void showDialog(String[] arrayValues, int arrayType) {
        Dialog dialog = new Dialog(ClientSurveyActivity.this);

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
        mName_tv = findViewById(R.id.client_survey_name);
        mDateOfBirth_tv = findViewById(R.id.client_survey_dob);
        mHeight_tv = findViewById(R.id.client_survey_height);
        mWeight_tv = findViewById(R.id.client_survey_weight);
        mBloodGroup_tv = findViewById(R.id.client_survey_blood_group);
        nextBTN = findViewById(R.id.client_survey_next_btn);
        relativeLayout = findViewById(R.id.client_survey_relative_layout);
        chipGroup = findViewById(R.id.client_survey_chip_group);
        loadingDialog = new LoadingDialog(ClientSurveyActivity.this);

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

    }
}