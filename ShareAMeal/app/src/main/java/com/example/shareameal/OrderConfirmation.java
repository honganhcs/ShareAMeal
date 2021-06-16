package com.example.shareameal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderConfirmation extends AppCompatActivity {

    private Button btnBack, btnConfirmOrder;
    private ImageView foodImage;
    private TextView foodNameTxt, foodDescriptionTxt, txtCurrentQuantity, foodQuantityHeader, txtSchedule, txtAddress;
    private EditText foodQuantityEdt;

    //data
    private DatabaseReference reference;
    private Bundle bundle;
    private Slot slot;
    private String donorId, foodId;
    private Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_confirmation);

        Intent intent = getIntent();
        bundle = intent.getExtras();
        slot = bundle.getParcelable("slot");
        donorId = bundle.getString("donorId");
        foodId = bundle.getString("foodId");

        reference = FirebaseDatabase.getInstance().getReference("Foods");


    }
}