package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class AdminSignUp extends AppCompatActivity {
    private EditText emailEdt, passwordEdt, usernameEdt, adminSignUpKeyEdt;
    private ImageView changePwVisibility;
    private AppCompatButton signUpBtn;
    private boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);

        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        usernameEdt = findViewById(R.id.usernameEdt);
        adminSignUpKeyEdt = findViewById(R.id.adminSignUpKeyEdt);
        changePwVisibility = findViewById(R.id.changePwVisibility);
        signUpBtn = findViewById(R.id.signupBtn);

        isPasswordVisible = false;
        changePwVisibility.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isPasswordVisible) {
                            changePwVisibility.setImageResource(R.drawable.showpassword);
                            passwordEdt.setTransformationMethod(new PasswordTransformationMethod());
                            isPasswordVisible = false;
                        } else {
                            changePwVisibility.setImageResource(R.drawable.hidepassword);
                            passwordEdt.setTransformationMethod(null);
                            isPasswordVisible = true;
                        }
                    }
                });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdt.getText().toString().trim();
                String password = passwordEdt.getText().toString().trim();
                String username = usernameEdt.getText().toString().trim();
                String authToken = adminSignUpKeyEdt.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(AdminSignUp.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(AdminSignUp.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(username)) {
                    Toast.makeText(AdminSignUp.this, "Enter Username", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(authToken)) {
                    Toast.makeText(AdminSignUp.this, "Enter Authorisation Token", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().getReference("AdminSignUpKey").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            String dbAuthToken = task.getResult().getValue().toString();
                            if (authToken.equals(dbAuthToken)) {
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(
                                                AdminSignUp.this,
                                                new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (!task.isSuccessful()) {
                                                            Toast.makeText(
                                                                    AdminSignUp.this,
                                                                    "Authentication Failed",
                                                                    Toast.LENGTH_SHORT)
                                                                    .show();
                                                        } else {
                                                            User admin = new User();
                                                            admin.setUserGroup("admin");
                                                            FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
                                                            String uid = loggedInUser.getUid();
                                                            admin.setUserId(uid);
                                                            admin.setName(username);
                                                            admin.setImageUrl("null");

                                                            DatabaseReference mDatabase =
                                                                    FirebaseDatabase.getInstance().getReference("Users");
                                                            mDatabase.child(uid).setValue(admin);

                                                            Intent intent = new Intent(AdminSignUp.this, AdminHomepageActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                            } else {
                                Toast.makeText(AdminSignUp.this, "Incorrect Authorisation Token", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminSignUp.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }
}