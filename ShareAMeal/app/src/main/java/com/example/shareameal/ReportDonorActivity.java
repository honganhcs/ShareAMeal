package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class ReportDonorActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private AppCompatButton chooseImageBtn, uploadImageBtn;
    private ImageView reportImage;
    private Uri imageUri;
    private StorageReference mStorageRef;
    private String imageUrl;

    private EditText reportEdt;
    private AppCompatButton reportUserBtn;
    private ProgressBar progressBar;
    private String donorId, slotId, foodId, recipientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_donor);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(ReportDonorActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Report Donor");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recipientId = user.getUid();

        chooseImageBtn = findViewById(R.id.chooseImageBtn);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        uploadImageBtn.setClickable(false);
        uploadImageBtn.setEnabled(false);
        uploadImageBtn.setBackground(getDrawable(R.drawable.disabledbutton));
        reportImage = findViewById(R.id.reportImage);

        reportEdt = findViewById(R.id.reportEdt);
        reportUserBtn = findViewById(R.id.reportUserBtn);

        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        donorId = intent.getStringExtra("donorId");
        slotId = intent.getStringExtra("slotId");
        foodId = intent.getStringExtra("foodId");
        Log.d("foodId", foodId);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("reportsUploads");
        mStorageRef = storageReference.child(recipientId);

        chooseImageBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFileChooser();
                    }
                });

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

                uploadImageBtn.setClickable(false);
                uploadImageBtn.setEnabled(false);
                uploadImageBtn.setBackground(getDrawable(R.drawable.disabledbutton));
            }
        });

        reportUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reportContent = reportEdt.getText().toString().trim();

                if (TextUtils.isEmpty(reportContent)) {
                    Toast.makeText(ReportDonorActivity.this, "Please fill in the report description", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().getReference("Users").child(donorId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            User currUser = task.getResult().getValue(User.class);
                            String donorName;
                            if (currUser.getRestaurant() == null || currUser.getRestaurant().equals("null")) {
                                donorName = currUser.getName();
                            } else {
                                donorName = currUser.getRestaurant();
                            }
                            AlertDialog dialog = new AlertDialog.Builder(ReportDonorActivity.this)
                                    .setTitle("Send Report")
                                    .setMessage("Are you sure you want to report " + donorName + "?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DatabaseReference database =
                                                    FirebaseDatabase.getInstance().getReference("Reports").child(donorId);

                                            Report report = new Report();
                                            report.setDonorId(donorId);
                                            report.setSlotId(slotId);
                                            report.setRecipientId(recipientId);
                                            report.setReportContent(reportContent);
                                            report.setFoodId(foodId);

                                            if (imageUrl == null) {
                                                report.setReportImageUrl("null");
                                            } else {
                                                report.setReportImageUrl(imageUrl);
                                            }

                                            Calendar curr = Calendar.getInstance();
                                            int year = curr.get(Calendar.YEAR);
                                            int monthInt = curr.get(Calendar.MONTH) + 1;
                                            String month = getMonthFormat(monthInt);
                                            int day = curr.get(Calendar.DAY_OF_MONTH);
                                            int hour = curr.get(Calendar.HOUR_OF_DAY);
                                            int minute = curr.get(Calendar.MINUTE);

                                            String date;
                                            if (minute < 10) {
                                                date = month + " " + day + " " + year + " " + hour + ":0" + minute;
                                            } else {
                                                date = month + " " + day + " " + year + " " + hour + ":" + minute;
                                            }
                                            report.setReportTime(date);
                                            report.setYear(year);
                                            report.setMonth(monthInt);
                                            report.setDay(day);
                                            report.setHour(hour);
                                            report.setMinute(minute);

                                            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child("Completed");
                                            orderRef.child(recipientId).child(slotId).child(foodId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                    if(!task.isSuccessful()) {
                                                        Log.e("Firebase", "Failed to get order");
                                                    } else {
                                                        Order order = task.getResult().getValue(Order.class);
                                                        order.setReported(true);
                                                        orderRef.child(recipientId).child(slotId).child(foodId).setValue(order);
                                                    }
                                                }
                                            });

                                            String reportId = database.push().getKey();
                                            report.setReportId(reportId);
                                            database.child(reportId).setValue(report);

                                            Toast.makeText(ReportDonorActivity.this, "Report successfully sent in", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ReportDonorActivity.this, RecipientsRecords.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#AF1B1B"));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#AF1B1B"));
                        }
                    });
                }
            }
        });
    }

    private String getMonthFormat(int month) {
        if (month == 1) return "Jan";
        if (month == 2) return "Feb";
        if (month == 3) return "Mar";
        if (month == 4) return "Apr";
        if (month == 5) return "May";
        if (month == 6) return "Jun";
        if (month == 7) return "Jul";
        if (month == 8) return "Aug";
        if (month == 9) return "Sep";
        if (month == 10) return "Oct";
        if (month == 11) return "Nov";
        if (month == 12) return "Dec";
        // should never happen
        return "Jan";
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        uploadImageBtn.setClickable(true);
        uploadImageBtn.setEnabled(true);
        uploadImageBtn.setBackground(getDrawable(R.drawable.button));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(reportImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference fileReference =
                    mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            progressBar.setVisibility(View.VISIBLE);
            fileReference
                    .putFile(imageUri)
                    .continueWithTask(
                            new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                                        throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return fileReference.getDownloadUrl();
                                }
                            })
                    .addOnCompleteListener(
                            new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        imageUrl = downloadUri.toString();
                                        Toast.makeText(
                                                ReportDonorActivity.this, "Upload successful", Toast.LENGTH_LONG)
                                                .show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(
                                                ReportDonorActivity.this,
                                                "Upload failed: " + task.getException().getMessage(),
                                                Toast.LENGTH_LONG)
                                                .show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ReportDonorActivity.this, e.getMessage(), Toast.LENGTH_LONG)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
        } else {
            Toast.makeText(ReportDonorActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ReportDonorActivity.this, ViewCompletedOrder.class);
        intent.putExtra("donorId", donorId);
        intent.putExtra("slotId", slotId);
        intent.putExtra("foodId", foodId);
        intent.putExtra("recipientId", recipientId);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReportDonorActivity.this, ViewCompletedOrder.class);
        intent.putExtra("donorId", donorId);
        intent.putExtra("slotId", slotId);
        intent.putExtra("foodId", foodId);
        intent.putExtra("recipientId", recipientId);
        startActivity(intent);
        finish();
    }
}
