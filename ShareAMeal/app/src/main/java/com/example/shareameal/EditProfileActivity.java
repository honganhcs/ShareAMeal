package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class EditProfileActivity extends AppCompatActivity {
    private AppCompatButton backBtn, updateProfileInfoBtn;
    private EditText usernameEdt, addressEdt, restaurantEdt;
    private String userGroup;
    private TextInputLayout restaurantWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // If user is not logged in, direct user to login page. Else, direct to donor homepage.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        backBtn = findViewById(R.id.backBtn);
        updateProfileInfoBtn = findViewById(R.id.updateProfileInfoBtn);
        usernameEdt = findViewById(R.id.usernameEdt);
        addressEdt = findViewById(R.id.addressEdt);
        restaurantEdt = findViewById(R.id.restaurantEdt);
        restaurantWrapper = findViewById(R.id.restaurantWrapper);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, DonorUserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // "Update Profile Info" button is clickable only after any of the fields are updated
        usernameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateProfileInfoBtn.setClickable(true);
                updateProfileInfoBtn.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        addressEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateProfileInfoBtn.setClickable(true);
                updateProfileInfoBtn.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        restaurantEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateProfileInfoBtn.setClickable(true);
                updateProfileInfoBtn.setEnabled(true);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Obtaining the user info from the database and printing them onto the edit fields
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                usernameEdt.setText(user.getName());
                addressEdt.setText(user.getAddress());
                userGroup = user.getUserGroup();

                // If user is recipient, the "Name of food service" field will be uneditable
                if (user.getUserGroup().equals("recipient")) {
                    restaurantEdt.setEnabled(false);
                    restaurantEdt.setClickable(false);
                } else {
                    if (!TextUtils.isEmpty(user.getRestaurant())) {
                        restaurantEdt.setText(user.getRestaurant());
                    }
                }

                updateProfileInfoBtn.setClickable(false);
                updateProfileInfoBtn.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });

        // Updating the database of the new user information
        updateProfileInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = usernameEdt.getText().toString();
                String newAddress = addressEdt.getText().toString();
                String newRestaurant = restaurantEdt.getText().toString();

                if (TextUtils.isEmpty(newUsername)) {
                    Toast.makeText(EditProfileActivity.this, "Must provide username", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newAddress)) {
                    Toast.makeText(EditProfileActivity.this, "Must provide address", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference databaseReference1 = databaseReference.child(userId);
                    User user = new User();
                    user.setName(newUsername);
                    user.setAddress(newAddress);
                    user.setUserId(userId);
                    user.setUserGroup(userGroup);
                    user.setRestaurant(newRestaurant);

                    databaseReference1.setValue(user);
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Toast.makeText(EditProfileActivity.this, "Profile information successfully updated", Toast.LENGTH_SHORT).show();
                            updateProfileInfoBtn.setClickable(false);
                            updateProfileInfoBtn.setEnabled(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(EditProfileActivity.this, "Profile information failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}