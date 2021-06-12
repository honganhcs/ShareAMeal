package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DonorFoodItemPageActivity extends AppCompatActivity {
    private AppCompatButton backToListingsBtn, updateFoodQuantityBtn, editFoodListingBtn, deleteFoodListingBtn;
    private ImageView foodImage;
    private TextView foodNameTxt, foodDescriptionTxt;
    private EditText foodQuantityEdt;
    private String foodId;
    private Food food;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_food_item_page);

        // Initialise widgets
        backToListingsBtn = findViewById(R.id.backToListingsBtn);
        updateFoodQuantityBtn = findViewById(R.id.updateFoodQuantityBtn);
        editFoodListingBtn = findViewById(R.id.editFoodListingBtn);
        deleteFoodListingBtn = findViewById(R.id.deleteFoodListingBtn);
        foodImage = findViewById(R.id.foodImage);
        foodNameTxt = findViewById(R.id.foodNameTxt);
        foodDescriptionTxt = findViewById(R.id.foodDescriptionTxt);
        foodQuantityEdt = findViewById(R.id.foodQuantityEdt);

        updateFoodQuantityBtn.setClickable(false);
        updateFoodQuantityBtn.setEnabled(false);
        updateFoodQuantityBtn.setBackground(getDrawable(R.drawable.disabledbutton));

        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            food = bundle.getParcelable("food");
            foodNameTxt.setText(food.getName());
            foodDescriptionTxt.setText(food.getDescription());
            foodQuantityEdt.setText(String.valueOf(food.getQuantity()));
            foodId = food.getFoodId();

            if (food.getImageUrl() == null) {
                foodImage.setImageResource(R.drawable.dish);
            } else {
                if (food.getImageUrl().equals("null")) {
                    foodImage.setImageResource(R.drawable.dish);
                } else {
                    Picasso.get().load(food.getImageUrl()).into(foodImage);
                }
            }
        } else {
            new NullPointerException();
        }

        backToListingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonorFoodItemPageActivity.this, DonateFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });

        foodQuantityEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFoodQuantityBtn.setClickable(true);
                updateFoodQuantityBtn.setEnabled(true);
                updateFoodQuantityBtn.setBackground(getDrawable(R.drawable.button));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        updateFoodQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFoodQty = foodQuantityEdt.getText().toString();
                food.setQuantity(Integer.valueOf(newFoodQty));

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Foods").child(userId);
                database.child(foodId).setValue(food);

                Toast.makeText(DonorFoodItemPageActivity.this, "Quantity successfully updated", Toast.LENGTH_SHORT).show();

                updateFoodQuantityBtn.setClickable(false);
                updateFoodQuantityBtn.setEnabled(false);
                updateFoodQuantityBtn.setBackground(getDrawable(R.drawable.disabledbutton));
            }
        });

        editFoodListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonorFoodItemPageActivity.this, EditFoodItemActivity.class);
                bundle.putParcelable("food", food);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        deleteFoodListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Foods").child(userId);
                database.child(foodId).removeValue();

                // Deletes the image from the cloud storage
                if (food.getImageUrl() != null) {
                    if (!food.getImageUrl().equals("null")) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(food.getImageUrl());
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {}
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {}
                        });
                    }
                }

                Intent intent = new Intent(DonorFoodItemPageActivity.this, DonateFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}