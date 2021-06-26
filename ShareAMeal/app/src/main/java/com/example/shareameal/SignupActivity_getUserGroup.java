package com.example.shareameal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// To get basic user information after account creation
public class SignupActivity_getUserGroup extends AppCompatActivity {
    private RadioGroup rgUserGroups;
    private Button nextBtn;
    private String userGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_get_user_group);

        // Initialise widgets
        rgUserGroups = findViewById(R.id.rgUserGroups);
        nextBtn = findViewById(R.id.nextBtn);

        // Make edit fields for address uneditable if user is a money donor
        rgUserGroups.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.recipientBtn) {
                            userGroup = "recipient";
                        } else {
                            userGroup = "donor";
                        }
                    }
                });

        nextBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userGroup == null) {
                            Toast.makeText(
                                    SignupActivity_getUserGroup.this,
                                    "Please select one of the above options",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Intent intent =
                                    new Intent(SignupActivity_getUserGroup.this, SignupActivity_getUserName.class);
                            intent.putExtra("userGroup", userGroup);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignupActivity_getUserGroup.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }
}
