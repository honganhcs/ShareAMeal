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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private AppCompatButton mProceedBtn;
    private FirebaseAuth auth;
    private ImageView mShowPassword;
    private boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEditTextEmail = findViewById(R.id.emailEdt);
        mEditTextPw = findViewById(R.id.passwordEdt);
        mProceedBtn = findViewById(R.id.proceedBtn);
        mShowPassword = findViewById(R.id.changePwVisibility);

        auth = FirebaseAuth.getInstance();

        // To show/hide password field
        isPasswordVisible = false;
        mShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    mShowPassword.setImageResource(R.drawable.showpassword);
                    mEditTextPw.setTransformationMethod(new PasswordTransformationMethod());
                    isPasswordVisible = false;
                } else {
                    mShowPassword.setImageResource(R.drawable.hidepassword);
                    mEditTextPw.setTransformationMethod(null);
                    isPasswordVisible = true;
                }
            }
        });

        // Registering new user in database
        mProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();

                // Check if email/password is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignupActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new user
                    auth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Signup successful, direct user to getUserInfo activity
                                        Intent intent = new Intent(SignupActivity.this, SignupActivity_getUserInfo.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}