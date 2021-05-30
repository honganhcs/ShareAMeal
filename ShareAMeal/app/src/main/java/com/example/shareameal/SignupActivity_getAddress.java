package com.example.shareameal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity_getAddress extends AppCompatActivity {

    private String userGroup, username, restaurant, unit, building, street, pCode, address;
    private EditText edtUnit, edtBuilding, edtStreet, edtPostalCode;
    private AppCompatButton createAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_get_address);

        Intent intent = getIntent();
        userGroup = intent.getStringExtra("userGroup");
        username = intent.getStringExtra("username");
        restaurant = intent.getStringExtra("restaurant");

        edtUnit = findViewById(R.id.edtUnit);
        edtBuilding = findViewById(R.id.edtBuilding);
        edtStreet = findViewById(R.id.edtStreet);
        edtPostalCode = findViewById(R.id.edtPostalCode);

        unit = edtUnit.getText().toString().trim();
        building = edtBuilding.getText().toString().trim();
        street = edtStreet.getText().toString().trim();
        pCode = edtPostalCode.getText().toString().trim();

        createAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(unit.matches("") || building.matches("") || street.matches("")
                   || pCode.matches("")) {
                    Toast.makeText(SignupActivity_getAddress.this, "Please fill in all of the above fields",
                            Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User();
                    user.setUserGroup(userGroup);
                    user.setName(username);
                    user.setRestaurant(restaurant);

                    address = "#" + unit + ", " + building + ", " + street + ", Singapore " + pCode;
                    user.setAddress(address);

                    FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = loggedInUser.getUid();
                    user.setUserId(uid);

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                    mDatabase.child(uid).setValue(user);

                    Intent intent = new Intent(SignupActivity_getAddress.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}