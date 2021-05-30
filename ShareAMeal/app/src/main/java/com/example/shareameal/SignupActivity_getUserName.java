package com.example.shareameal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity_getUserName extends AppCompatActivity {

    private String userGroup, username, restaurant;
    private EditText edtUsername, edtRestaurant;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_get_user_name);

        Intent intent = getIntent();
        userGroup = intent.getStringExtra("userGroup");

        edtUsername = findViewById(R.id.edtUsername);
        edtRestaurant = findViewById(R.id.edtRestaurant);

        username = edtUsername.getText().toString().trim();
        restaurant = edtRestaurant.getText().toString().trim();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.matches("")) {
                    Toast.makeText(SignupActivity_getUserName.this, "Please fill in your username", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SignupActivity_getUserName.this, SignupActivity_getAddress.class);
                    intent.putExtra("userGroup", userGroup);
                    intent.putExtra("username", username);
                    intent.putExtra("restaurant", restaurant);
                    startActivity(intent);
                }
            }
        });

    }
}