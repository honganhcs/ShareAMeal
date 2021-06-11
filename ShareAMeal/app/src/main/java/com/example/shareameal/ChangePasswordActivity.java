package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class ChangePasswordActivity extends AppCompatActivity {
    private AppCompatButton backBtn, changePwBtn;
    private EditText oldPwEdt, newPwEdt;
    private ImageView changeOldPwVisibility, changeNewPwVisibility;
    private boolean isOldPwVisible, isNewPwVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // If user is not logged in, direct user to login page. Else, direct to donor homepage.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        backBtn = findViewById(R.id.backBtn);
        changePwBtn = findViewById(R.id.changePwBtn);
        changePwBtn.setClickable(false);
        changePwBtn.setEnabled(false);
        oldPwEdt = findViewById(R.id.oldPwEdt);
        newPwEdt = findViewById(R.id.newPwEdt);
        changeOldPwVisibility = findViewById(R.id.changeOldPwVisibility);
        changeNewPwVisibility = findViewById(R.id.changeNewPwVisibility);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, DonorUserPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        isOldPwVisible = false;
        changeOldPwVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOldPwVisible) {
                    changeOldPwVisibility.setImageResource(R.drawable.showpassword);
                    oldPwEdt.setTransformationMethod(new PasswordTransformationMethod());
                    isOldPwVisible = false;
                } else {
                    changeOldPwVisibility.setImageResource(R.drawable.hidepassword);
                    oldPwEdt.setTransformationMethod(null);
                    isOldPwVisible = true;
                }
            }
        });

        isNewPwVisible = false;
        changeNewPwVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewPwVisible) {
                    changeNewPwVisibility.setImageResource(R.drawable.showpassword);
                    newPwEdt.setTransformationMethod(new PasswordTransformationMethod());
                    isNewPwVisible = false;
                } else {
                    changeNewPwVisibility.setImageResource(R.drawable.hidepassword);
                    newPwEdt.setTransformationMethod(null);
                    isNewPwVisible = true;
                }
            }
        });

        newPwEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6) {
                    changePwBtn.setClickable(true);
                    changePwBtn.setEnabled(true);
                } else {
                    changePwBtn.setClickable(false);
                    changePwBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        changePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                String oldPw = oldPwEdt.getText().toString();
                String newPw = newPwEdt.getText().toString();

                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPw);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Error: Password not updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Wrong current password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}