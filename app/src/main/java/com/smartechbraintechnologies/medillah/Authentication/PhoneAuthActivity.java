package com.smartechbraintechnologies.medillah.Authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.ShowSnackbar;

import java.util.Locale;

public class PhoneAuthActivity extends AppCompatActivity {

    private EditText phone_no_et;
    private Button sendOTP_BTN;
    private RelativeLayout relativeLayout;
    private ImageButton lang_settings;
    private LoadingDialog loadingDialog;


    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_phone_auth);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.secondary));
        }
        initValues();
        loadingDialog.dismissLoadingDialog();

        sendOTP_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.showLoadingDialog("Validating Number...");
                validateNumber();
            }
        });

        lang_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageMenu(view);
            }
        });
    }

    private void showChangeLanguageMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(PhoneAuthActivity.this, view);
        popupMenu.inflate(R.menu.menu_change_language);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.language_change_english:
                        loadingDialog.showLoadingDialog("Changing Language...");
                        setLocale("en");
                        recreate();
                        return true;
                    case R.id.language_change_malayalam:
                        loadingDialog.showLoadingDialog("Changing Language...");
                        setLocale("ml");
                        recreate();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = preferences.getString("My_Lang", "");
        setLocale(language);
    }

    private void validateNumber() {
        mPhoneNumber = phone_no_et.getText().toString();
        if (mPhoneNumber.isEmpty()) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(PhoneAuthActivity.this, getResources().getString(R.string.phonenumbercannotbeempty),
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else if (mPhoneNumber.length() != 10) {
            loadingDialog.dismissLoadingDialog();
            ShowSnackbar.show(PhoneAuthActivity.this, getResources().getString(R.string.invalidphonenumber),
                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
        } else {
            loadingDialog.dismissLoadingDialog();
            startActivity(new Intent(PhoneAuthActivity.this, VerifyOtpActivity.class)
                    .putExtra("Phone Number", mPhoneNumber));
        }
    }


    private void initValues() {
        phone_no_et = findViewById(R.id.phone_auth_phone_no);
        sendOTP_BTN = findViewById(R.id.phone_auth_send_otp_btn);
        relativeLayout = findViewById(R.id.phone_auth_relative_layout);
        lang_settings = findViewById(R.id.phone_auth_language_settings);
        lang_settings.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(PhoneAuthActivity.this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}