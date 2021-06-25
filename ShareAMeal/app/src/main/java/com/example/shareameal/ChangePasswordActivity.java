package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ChangePasswordActivity extends AppCompatActivity {
    private AppCompatButton backBtn, changePwBtn;
    private EditText oldPwEdt, newPwEdt;
    private ImageView changeOldPwVisibility, changeNewPwVisibility;
    private boolean isOldPwVisible, isNewPwVisible;
    private String userGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // If user is not logged in, direct user to login page. Else, direct to donor homepage.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference
                .child(userId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                userGroup = user.getUserGroup();
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });

        backBtn = findViewById(R.id.backBtn);
        changePwBtn = findViewById(R.id.changePwBtn);
        changePwBtn.setClickable(false);
        changePwBtn.setEnabled(false);
        changePwBtn.setBackground(getDrawable(R.drawable.disabledbutton));
        oldPwEdt = findViewById(R.id.oldPwEdt);
        newPwEdt = findViewById(R.id.newPwEdt);
        changeOldPwVisibility = findViewById(R.id.changeOldPwVisibility);
        changeNewPwVisibility = findViewById(R.id.changeNewPwVisibility);

        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userGroup.equals("donor")) {
                            Intent intent = new Intent(ChangePasswordActivity.this, DonorUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent =
                                    new Intent(ChangePasswordActivity.this, RecipientUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        isOldPwVisible = false;
        changeOldPwVisibility.setOnClickListener(
                new View.OnClickListener() {
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
        changeNewPwVisibility.setOnClickListener(
                new View.OnClickListener() {
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

        newPwEdt.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() >= 6) {
                            changePwBtn.setClickable(true);
                            changePwBtn.setEnabled(true);
                            changePwBtn.setBackground(getDrawable(R.drawable.button2));
                        } else {
                            changePwBtn.setClickable(false);
                            changePwBtn.setEnabled(false);
                            changePwBtn.setBackground(getDrawable(R.drawable.disabledbutton));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

        changePwBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String email = user.getEmail();
                        String oldPw = oldPwEdt.getText().toString();
                        String newPw = newPwEdt.getText().toString();

                        if (TextUtils.isEmpty(oldPw)) {
                            Toast.makeText(ChangePasswordActivity.this, "Please enter old password", Toast.LENGTH_SHORT).show();
                        } else {
                            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPw);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        user.updatePassword(newPw)
                                                                .addOnCompleteListener(
                                                                        new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(
                                                                                            ChangePasswordActivity.this,
                                                                                            "Password updated",
                                                                                            Toast.LENGTH_SHORT)
                                                                                            .show();
                                                                                } else {
                                                                                    Toast.makeText(
                                                                                            ChangePasswordActivity.this,
                                                                                            "Error: Password not updated",
                                                                                            Toast.LENGTH_SHORT)
                                                                                            .show();
                                                                                }
                                                                            }
                                                                        });
                                                    } else {
                                                        Toast.makeText(
                                                                ChangePasswordActivity.this,
                                                                "Wrong current password",
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                }
                                            });
                        }
                    }
                });
    }
}
