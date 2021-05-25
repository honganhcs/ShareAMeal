package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView mTextViewSignup;
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private Button mBtnLogin;
    private ImageView mShowPassword;

    private boolean isPasswordVisible;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // Initialise widgets
        mEditTextEmail = findViewById(R.id.emailEdt);
        mEditTextPw = findViewById(R.id.passwordEdt);
        mBtnLogin = findViewById(R.id.loginBtn);
        mTextViewSignup = findViewById(R.id.signup);
        mShowPassword = findViewById(R.id.changePwVisibility);

        mTextViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();

                // Check if email/password is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    // Authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Signup successful, got to main activity
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

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
    }
}