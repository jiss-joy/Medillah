package com.smartechbraintechnologies.medillah.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smartechbraintechnologies.medillah.Adapters.AdapterAddress;
import com.smartechbraintechnologies.medillah.Authentication.PhoneAuthActivity;
import com.smartechbraintechnologies.medillah.LoadingDialog;
import com.smartechbraintechnologies.medillah.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_CODE = 1;

    private TextView name, number, bloodGroup, height, weight, gender, dob;
    private TextView address_type, address_tv, address_status, no_delivery;
    private ImageView address_icon;
    private CircleImageView profilePic;
    private Button logOutBTN, myAddressesBTN;
    private ImageButton backBTN, cartBTN, optionsBTN;
    private LoadingDialog loadingDialog;
    private View view;

    private Uri profilePic_uri;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference userRef;
    private CollectionReference addressRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initValues();
        loadingDialog.showLoadingDialog("Loading details...");
        loadDetails();
        loadAddress();

        logOutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, PhoneAuthActivity.class));
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cartBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ProfileActivity.this, ShoppingCartActivity.class));
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissions();
            }
        });

        myAddressesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MyAddressesActivity.class));
            }
        });

        optionsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.inflate(R.menu.menu_change_address_option);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_address_options_change_address:
                                startActivity(new Intent(ProfileActivity.this, MyAddressesActivity.class));
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void loadDetails() {
        userRef.document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    name.setText(documentSnapshot.getString("userName"));
                    number.setText(documentSnapshot.getString("userPhone"));
                    bloodGroup.setText(documentSnapshot.getString("userBloodGroup") + "ve");
                    height.setText(documentSnapshot.getString("userHeight") + "CM");
                    weight.setText(documentSnapshot.getString("userWeight") + "KG");
                    gender.setText(documentSnapshot.getString("userGender"));
                    dob.setText(documentSnapshot.getString("userDay") + "/" + documentSnapshot.getString("userMonth") + "/"
                            + documentSnapshot.getString("userYear"));
                }
                loadingDialog.dismissLoadingDialog();
            }
        });
    }

    private void loadAddress() {
        addressRef.whereEqualTo("addressStatus", "Default").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.getDocuments().isEmpty()) {
                    myAddressesBTN.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                } else {
                    myAddressesBTN.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    DocumentSnapshot documentSnapshot = value.getDocuments().get(0);
                    String type = documentSnapshot.getString("addressType");
                    String address = documentSnapshot.getString("address");
                    String delivery_status = documentSnapshot.getString("addressDeliveryStatus");

                    if (type.equals("Home")) {
                        address_icon.setImageResource(R.drawable.home);
                        address_type.setText(type);
                    } else if (type.equals("Work")) {
                        address_icon.setImageResource(R.drawable.work);
                        address_type.setText(type);
                    } else {
                        address_icon.setImageResource(R.drawable.location);
                        address_type.setText(type);
                    }
                    address_tv.setText(address);
//                    if (delivery_status.equals("No Delivery")) {
//                        no_delivery.setVisibility(View.VISIBLE);
//                    } else {
//                        no_delivery.setVisibility(View.GONE);
//                    }
                }
            }

        });
    }

    private void initValues() {
        logOutBTN = (Button) findViewById(R.id.profile_log_out_btn);
        backBTN = (ImageButton) findViewById(R.id.toolbar_back_btn);
        cartBTN = (ImageButton) findViewById(R.id.toolbar_shopping_btn);
        profilePic = (CircleImageView) findViewById(R.id.profile_pic);
        myAddressesBTN = (Button) findViewById(R.id.profile_my_addresses_btn);
        name = (TextView) findViewById(R.id.profile_name);
        number = (TextView) findViewById(R.id.profile_number);
        bloodGroup = (TextView) findViewById(R.id.profile_blood_group);
        height = (TextView) findViewById(R.id.profile_height);
        weight = (TextView) findViewById(R.id.profile_weight);
        gender = (TextView) findViewById(R.id.profile_gender);
        dob = (TextView) findViewById(R.id.profile_dob);

        view = (View) findViewById(R.id.profile_address_card);
        address_icon = (ImageView) findViewById(R.id.address_card_type_image);
        address_status = (TextView) findViewById(R.id.address_card_address_status);
        address_tv = (TextView) findViewById(R.id.address_card_address);
        address_type = (TextView) findViewById(R.id.address_card_address_type);
        no_delivery = (TextView) findViewById(R.id.address_card_no_delivery);
        no_delivery.setVisibility(View.GONE);
        optionsBTN = (ImageButton) findViewById(R.id.address_card_options_btn);

        loadingDialog = new LoadingDialog(ProfileActivity.this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = db.collection("Users");
        addressRef = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Addresses");
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.CAMERA)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Please grant this Permission to continue.");
                builder.setMessage("Camera");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ProfileActivity.this,
                                new String[]{
                                        Manifest.permission.CAMERA,
                                },
                                PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create();
                builder.show();
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{
                                Manifest.permission.CAMERA
                        },
                        PERMISSIONS_REQUEST_CODE
                );
            }

        } else {
            //Permissions already granted part.
            CropImage.startPickImageActivity(ProfileActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            profilePic_uri = imageUri;
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profilePic_uri = resultUri;
                Toast.makeText(ProfileActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                Picasso.get().load(resultUri).into(profilePic);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Could not upload.\nPlease try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PERMISSIONS_REQUEST_CODE == requestCode) {
            if ((grantResults.length > 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                CropImage.startPickImageActivity(ProfileActivity.this);
            } else {
                //Permissions Denied part.
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}