package com.example.shareameal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp_getUserNameRecipients extends AppCompatActivity {
    private String username;
    private EditText edtUsername;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_get_user_name_recipients);

        edtUsername = findViewById(R.id.edtUsername);

        nextBtn = findViewById(R.id.nextBtn1);
        nextBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        username = edtUsername.getText().toString().trim();

                        if (TextUtils.isEmpty(username)) {
                            Toast.makeText(
                                    SignUp_getUserNameRecipients.this,
                                    "Please fill in your username",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Intent intent =
                                    new Intent(SignUp_getUserNameRecipients.this, SignupActivity_getAddress.class);
                            intent.putExtra("userGroup", "recipient");
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(SignUp_getUserNameRecipients.this, "Please complete account registration process!", Toast.LENGTH_SHORT).show();
    }
}