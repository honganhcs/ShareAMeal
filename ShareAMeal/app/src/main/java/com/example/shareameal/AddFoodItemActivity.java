package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddFoodItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private AppCompatButton chooseImageBtn;
    private AppCompatButton uploadImageBtn;
    private ImageView foodImage;
    private Uri imageUri;
    private StorageReference mStorageRef;

    private EditText foodNameEdt;
    private EditText foodDescriptionEdt;
    private AppCompatButton addFoodItemBtn;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);

        // If user is not logged in, direct user to login page. Else, direct to donor homepage.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(AddFoodItemActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        chooseImageBtn = findViewById(R.id.chooseImageBtn);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        uploadImageBtn.setClickable(false);
        uploadImageBtn.setEnabled(false);
        foodImage = findViewById(R.id.foodImage);

        // Getting user id of current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("imageUploads");
        mStorageRef = storageReference.child(userId);

        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        foodNameEdt = findViewById(R.id.foodNameEdt);
        foodDescriptionEdt = findViewById(R.id.descriptionEdt);
        addFoodItemBtn = findViewById(R.id.addFoodItemBtn);

        addFoodItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = foodNameEdt.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AddFoodItemActivity.this, "Please enter food name", Toast.LENGTH_SHORT).show();
                } else {
                    String description = foodDescriptionEdt.getText().toString().trim();
                    int quantity = 0;

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("Foods").child(userId);

                    Food food = new Food();
                    food.setName(name);

                    if (TextUtils.isEmpty(description)) {
                        food.setDescription("No Description Provided");
                    } else {
                        food.setDescription(description);
                    }

                    food.setQuantity(quantity);

                    if (imageUrl == null) {
                        food.setImageUrl("null");
                    } else {
                        food.setImageUrl(imageUrl);
                    }

                    String foodId = database.push().getKey();
                    food.setFoodId(foodId);
                    database.child(foodId).setValue(food);

                    database.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Toast.makeText(AddFoodItemActivity.this, "Food item successfully added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddFoodItemActivity.this, DonateFoodActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AddFoodItemActivity.this, "Food item failed to add", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
                                Toast.makeText(AddFoodItemActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(AddFoodItemActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddFoodItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(AddFoodItemActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}