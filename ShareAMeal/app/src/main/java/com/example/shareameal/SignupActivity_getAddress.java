package com.example.shareameal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SignupActivity_getAddress extends AppCompatActivity {

    private String userGroup, username, restaurant, unit, building, street, pCode, address;
    private double latitude, longitude;
    private EditText edtUnit, edtBuilding, edtStreet, edtPostalCode;
    private AppCompatButton createAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_get_address);

        Intent intent = getIntent();
        userGroup = intent.getStringExtra("userGroup");
        username = intent.getStringExtra("username");
        if(userGroup.equals("donor")) {
            restaurant = intent.getStringExtra("restaurant");
        }

        edtUnit = findViewById(R.id.edtUnit);
        edtBuilding = findViewById(R.id.edtBuilding);
        edtStreet = findViewById(R.id.edtStreet);
        edtPostalCode = findViewById(R.id.edtPostalCode);

        createAccountBtn = findViewById(R.id.finishRegBtn);
        createAccountBtn.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        unit = edtUnit.getText().toString().trim();
                        building = edtBuilding.getText().toString().trim();
                        street = edtStreet.getText().toString().trim();
                        pCode = edtPostalCode.getText().toString().trim();

                        if (unit.contains("#")) {
                            unit = unit.substring(1);
                        }

                        if (TextUtils.isEmpty(pCode)) {
                            Toast.makeText(
                                    SignupActivity_getAddress.this,
                                    "Please enter postal code",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else if (TextUtils.isEmpty(street)) {
                            Toast.makeText(
                                    SignupActivity_getAddress.this,
                                    "Please enter block/street name",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else if (pCode.length() != 6) {
                            Toast.makeText(
                                    SignupActivity_getAddress.this,
                                    "Please enter a valid postal code",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            User user = new User();
                            user.setUserGroup(userGroup);
                            user.setName(username);
                            user.setRestaurant(restaurant);
                            user.setNumberOfReports(0);
                            user.setProfileDescription("");

                            if(userGroup.equals("recipient")) {
                                Calendar calendar = Calendar.getInstance();
                                user.setYear(calendar.get(Calendar.YEAR));
                                user.setMonth(calendar.get(Calendar.MONTH));
                                user.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
                                user.setNumOrdersLeft(3);
                                user.setVerificationState(0);
                            } else {
                                user.setVerificationState(2);
                                user.setNumberOfPoints(0);
                                user.setNumberOfWeeklyPoints(0);
                            }

                            if (TextUtils.isEmpty(building) && TextUtils.isEmpty(unit)) {
                                address = street + " Singapore " + pCode;
                            } else if (TextUtils.isEmpty(building)) {
                                address = street + " #" + unit + " Singapore " + pCode;
                            } else if (TextUtils.isEmpty(unit)) {
                                address = building + " " + street + " Singapore " + pCode;
                            } else {
                                address = building + " " + street + " #" + unit + " Singapore " + pCode;
                            }
                            user.setAddress(address);

                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                List addressList = geocoder.getFromLocationName(address, 1);
                                if (addressList != null && addressList.size() > 0) {
                                    Address addressItem = (Address) addressList.get(0);
                                    longitude = addressItem.getLongitude();
                                    latitude = addressItem.getLatitude();

                                    user.setAddressLatitude(latitude);
                                    user.setAddressLongitude(longitude);

                                    FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String uid = loggedInUser.getUid();
                                    user.setUserId(uid);

                                    user.setImageUrl("null");

                                    DatabaseReference mDatabase =
                                            FirebaseDatabase.getInstance().getReference("Users");
                                    mDatabase.child(uid).setValue(user);

                                    Intent intent = new Intent(SignupActivity_getAddress.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(
                                            SignupActivity_getAddress.this,
                                            "Please provide a valid address",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(SignupActivity_getAddress.this, "Please complete account registration process!", Toast.LENGTH_SHORT).show();
    }
}
