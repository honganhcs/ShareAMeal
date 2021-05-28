package com.example.shareameal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// To get basic user information after account creation
public class SignupActivity_getUserInfo extends AppCompatActivity {
    private RadioGroup mUserGroups;
    private EditText mNameEdt;
    private EditText mPostalCodeEdt;
    private EditText mBlockOrStreetNameEdt;
    private EditText mBldgOrHseNumberEdt;
    private EditText mUnitNumberEdt;
    private AppCompatButton mfinishRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_get_user_info);

        // Initialise widgets
        mUserGroups = findViewById(R.id.userGroups);
        mNameEdt = findViewById(R.id.nameEdt);
        mPostalCodeEdt = findViewById(R.id.postalCodeEdt);
        mBlockOrStreetNameEdt = findViewById(R.id.blockOrStreetNameEdt);
        mBldgOrHseNumberEdt = findViewById(R.id.bldgOrHseNumberEdt);
        mUnitNumberEdt = findViewById(R.id.unitNumberEdt);
        mfinishRegBtn = findViewById(R.id.finishRegBtn);

        // Make edit fields for address uneditable if user is a money donor
        mUserGroups.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String userGroup;
                if (checkedId == R.id.donorBtn) {
                    userGroup = "donor";
                } else {
                    userGroup = "recipient";
                }
            }
        });

        // When the "Finish Registration" button is clicked
        mfinishRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();

                // Obtaining user information
                int selectedGroup = mUserGroups.getCheckedRadioButtonId();

                // Check if any of the radio buttons is checked
                if (selectedGroup == -1) {
                    Toast.makeText(SignupActivity_getUserInfo.this, "Please indicate group to register under", Toast.LENGTH_SHORT).show();
                } else {
                    String userGroup;
                    if (selectedGroup == R.id.donorBtn) {
                        userGroup = "donor";
                    } else {
                        userGroup = "recipient";
                    }

                    user.setUserGroup(userGroup);
                    String name = mNameEdt.getText().toString().trim();
                    user.setName(name);

                    FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = loggedInUser.getUid();
                    user.setUserId(uid);

                    if (!userGroup.equals("moneyDonor")) {
                        String postalCode = mPostalCodeEdt.getText().toString().trim();
                        String blockOrStreetName = mBlockOrStreetNameEdt.getText().toString().trim();
                        String bldgOrHseNumber = mBldgOrHseNumberEdt.getText().toString().trim();
                        String unitNumber = mUnitNumberEdt.getText().toString().trim();
                        if (TextUtils.isEmpty(postalCode)) {
                            Toast.makeText(SignupActivity_getUserInfo.this, "Please enter postal code", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(blockOrStreetName)) {
                            Toast.makeText(SignupActivity_getUserInfo.this, "Please enter block/street name", Toast.LENGTH_SHORT).show();
                        } else {
                            String address;
                            if (TextUtils.isEmpty(bldgOrHseNumber) && TextUtils.isEmpty(unitNumber)) {
                                address = blockOrStreetName + " Singapore " + postalCode;
                            } else if (TextUtils.isEmpty(bldgOrHseNumber)) {
                                address = blockOrStreetName + " #" + unitNumber + " Singapore " + postalCode;
                            } else if (TextUtils.isEmpty(unitNumber)) {
                                address = bldgOrHseNumber + " " + blockOrStreetName + " Singapore " + postalCode;
                            } else {
                                address = bldgOrHseNumber + " " + blockOrStreetName + " #" + unitNumber + " Singapore " + postalCode;
                            }
                            user.setAddress(address);

                            // Entering user information into backend database
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                            mDatabase.child(uid).setValue(user);

                            Intent intent = new Intent(SignupActivity_getUserInfo.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                        mDatabase.child(uid).setValue(user);

                        Intent intent = new Intent(SignupActivity_getUserInfo.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}