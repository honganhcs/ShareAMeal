package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

public class RecipientSendVerification extends AppCompatActivity {
    private AppCompatButton selectFileBtn, uploadFileBtn, submitBtn;
    private TextView uploadStatusTxt;
    private Uri fileUri;
    private ProgressBar progressBar;
    private String userId, fileUrl;
    private EditText incomeLevelEdt;

    private FirebaseStorage storage;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_send_verification);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(RecipientSendVerification.this, LoginActivity.class);
            startActivity(intent);
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Income Level Verification");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        selectFileBtn = findViewById(R.id.selectFileBtn);
        uploadFileBtn = findViewById(R.id.uploadFileBtn);
        uploadFileBtn.setClickable(false);
        uploadFileBtn.setEnabled(false);
        uploadFileBtn.setBackground(getDrawable(R.drawable.disabledbutton));
        uploadStatusTxt = findViewById(R.id.uploadStatusTxt);
        submitBtn = findViewById(R.id.submitBtn);
        incomeLevelEdt = findViewById(R.id.incomeLevelEdt);

        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);

        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RecipientSendVerification.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    selectPDF();
                } else {
                    ActivityCompat.requestPermissions(RecipientSendVerification.this, new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE}, 478);
                }
            }
        });

        uploadFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileUri != null) {
                    uploadFile(fileUri);
                } else {
                    Toast.makeText(RecipientSendVerification.this, "Please select a file!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String monthlyIncomeText = incomeLevelEdt.getText().toString();
                if (TextUtils.isEmpty(monthlyIncomeText)) {
                    Toast.makeText(RecipientSendVerification.this, "Please state your monthly income", Toast.LENGTH_SHORT).show();
                } else if (fileUrl == null || TextUtils.isEmpty(fileUrl)) {
                    Toast.makeText(RecipientSendVerification.this, "Please upload your verification document", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference verificationsRef = database.getReference("Verifications").child(userId);
                    Verification ver = new Verification();
                    ver.setFileUrl(fileUrl);
                    ver.setMonthlyIncome(Double.valueOf(monthlyIncomeText));
                    ver.setRecipientId(userId);
                    verificationsRef.setValue(ver);

                    verificationsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Toast.makeText(RecipientSendVerification.this, "Verification request successfully submitted", Toast.LENGTH_SHORT).show();
                            DatabaseReference usersRef = database.getReference("Users");
                            usersRef.child(userId).child("verificationState").setValue(1);

                            Intent intent = new Intent(RecipientSendVerification.this, RecipientHomepageActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                    });
                }
            }
        });
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 478);
        uploadFileBtn.setClickable(true);
        uploadFileBtn.setEnabled(true);
        uploadFileBtn.setBackground(getDrawable(R.drawable.button));
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadFile(Uri fileUri) {
        progressBar.setVisibility(View.VISIBLE);
        final String fileName = System.currentTimeMillis() + "." + getFileExtension(fileUri);
        StorageReference mySto = storage.getReference().child("VerificationUploads");

        mySto.child(userId).child(fileName).putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                fileUrl = uri.toString();
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RecipientSendVerification.this, "File successfully uploaded.", Toast.LENGTH_SHORT).show();
                                uploadFileBtn.setClickable(false);
                                uploadFileBtn.setEnabled(false);
                                uploadFileBtn.setBackground(getDrawable(R.drawable.disabledbutton));
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecipientSendVerification.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressBar.setProgress(currentProgress);
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 478 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPDF();
        } else {
            Toast.makeText(this, "Please allow external storage reading!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 478 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            uploadStatusTxt.setText("Selected File: " + getFileName(fileUri));
        } else {
            Toast.makeText(this, "Please select a file!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(RecipientSendVerification.this, RecipientHomepageActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RecipientSendVerification.this, RecipientHomepageActivity.class);
        startActivity(intent);
        finish();
    }
}