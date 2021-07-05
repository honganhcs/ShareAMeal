package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.Formatter;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ReviewVerification extends AppCompatActivity {
    private final double MAX_ALLOWED_INCOME_LEVEL = 3000;

    private AppCompatButton downloadBtn, verifyBtn, rejectBtn, reverifyBtn;
    private TextView incomeLevelTxt;
    private ConstraintLayout tickLayout, crossLayout;
    private String recipientId, fileUrl, fileExtension, recipientName;
    private double monthlyIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_verification);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Review Verification Request");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        downloadBtn = findViewById(R.id.downloadBtn);
        verifyBtn = findViewById(R.id.verifyBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        reverifyBtn = findViewById(R.id.reverifyBtn);
        incomeLevelTxt = findViewById(R.id.incomeLevelTxt);
        tickLayout = findViewById(R.id.tickLayout);
        crossLayout = findViewById(R.id.crossLayout);

        Intent intent = getIntent();
        recipientId = intent.getStringExtra("recipientId");

        DatabaseReference verificationsRef = FirebaseDatabase.getInstance().getReference("Verifications");
        verificationsRef.child(recipientId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                Verification ver = task.getResult().getValue(Verification.class);
                fileUrl = ver.getFileUrl();
                fileExtension = ver.getFileExtension();
                monthlyIncome = ver.getMonthlyIncome();

                if (monthlyIncome <= MAX_ALLOWED_INCOME_LEVEL) {
                    crossLayout.setVisibility(View.GONE);
                } else {
                    tickLayout.setVisibility(View.GONE);
                }

                Formatter formatter = new Formatter();
                formatter.format("%.2f", monthlyIncome);
                incomeLevelTxt.setText("$ " + formatter.toString());
            }
        });

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.child(recipientId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);
                recipientName = user.getName();
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(ReviewVerification.this, "Verification Document - " + recipientName, "." + fileExtension,
                        DIRECTORY_DOWNLOADS, fileUrl);
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationsRef.child(recipientId).removeValue();
                usersRef.child(recipientId).child("verificationState").setValue(2);
                FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl).delete();
                Intent intent = new Intent(ReviewVerification.this, AdminViewVerifications.class);
                startActivity(intent);
                finish();
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationsRef.child(recipientId).removeValue();
                usersRef.child(recipientId).child("verificationState").setValue(3);
                FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl).delete();
                Intent intent = new Intent(ReviewVerification.this, AdminViewVerifications.class);
                startActivity(intent);
                finish();
            }
        });

        reverifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationsRef.child(recipientId).removeValue();
                usersRef.child(recipientId).child("verificationState").setValue(4);
                FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl).delete();
                Intent intent = new Intent(ReviewVerification.this, AdminViewVerifications.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReviewVerification.this, AdminViewVerifications.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ReviewVerification.this, AdminViewVerifications.class);
        startActivity(intent);
        finish();
        return true;
    }
}