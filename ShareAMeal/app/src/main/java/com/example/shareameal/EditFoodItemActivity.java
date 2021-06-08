package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditFoodItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private AppCompatButton chooseImageBtn, uploadImageBtn;
    private ImageView foodImage;
    private Uri imageUri;
    private StorageReference mStorageRef;

    private EditText foodNameEdt;
    private EditText foodDescriptionEdt;
    private AppCompatButton editFoodItemBtn;
    private String imageUrl, foodId;
    private Food oldFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food_item);

        // If user is not logged in, direct user to login page. Else, direct to donor homepage.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(EditFoodItemActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        chooseImageBtn = findViewById(R.id.chooseImageBtn);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        uploadImageBtn.setClickable(false);
        uploadImageBtn.setEnabled(false);
        foodImage = findViewById(R.id.foodImage);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("imageUploads");
        mStorageRef = storageReference.child(userId);

        foodNameEdt = findViewById(R.id.foodNameEdt);
        foodDescriptionEdt = findViewById(R.id.descriptionEdt);
        editFoodItemBtn = findViewById(R.id.editFoodItemBtn);

        // Loading the previous food entry into the different fields
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            oldFood = bundle.getParcelable("food");
            foodNameEdt.setText(oldFood.getName());
            if (!oldFood.getDescription().equals("No Description Provided")) {
                foodDescriptionEdt.setText(oldFood.getDescription());
            }

            if (oldFood.getImageUrl() == null) {
                foodImage.setImageResource(R.drawable.dish);
            } else {
                if (oldFood.getImageUrl().equals("null")) {
                    foodImage.setImageResource(R.drawable.dish);
                } else {
                    Picasso.get().load(oldFood.getImageUrl()).into(foodImage);
                }
            }

            foodId = oldFood.getFoodId();
        } else {
            new NullPointerException();
        }

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deleting the old photo from the cloud storage
                if (oldFood.getImageUrl() != null) {
                    if (!oldFood.getImageUrl().equals("null")) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldFood.getImageUrl());
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {}
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {}
                        });
                    }
                }
                uploadImage();
            }
        });

        editFoodItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = foodNameEdt.getText().toString().trim();
                String description = foodDescriptionEdt.getText().toString().trim();

                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Foods").child(userId);

                Food food = new Food();
                food.setName(name);

                if (TextUtils.isEmpty(description)) {
                    food.setDescription("No Description Provided");
                } else {
                    food.setDescription(description);
                }

                if (imageUrl == null) {
                    food.setImageUrl(oldFood.getImageUrl());
                } else {
                    food.setImageUrl(imageUrl);
                }

                food.setQuantity(oldFood.getQuantity());
                food.setFoodId(oldFood.getFoodId());

                database.child(foodId).setValue(food);

                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Toast.makeText(EditFoodItemActivity.this, "Food item successfully edited", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditFoodItemActivity.this, DonateFoodActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditFoodItemActivity.this, "Food item failed to edit", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        uploadImageBtn.setClickable(true);
        uploadImageBtn.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(foodImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileReference.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                imageUrl = downloadUri.toString();
                                Toast.makeText(EditFoodItemActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            }
                            else { Toast.makeText(EditFoodItemActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditFoodItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(EditFoodItemActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}