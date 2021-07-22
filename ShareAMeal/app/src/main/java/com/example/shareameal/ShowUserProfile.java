package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ShowUserProfile extends AppCompatActivity {
    private ImageView userProfilePic;
    private TextView userNameTxt, descriptionTxt, weeklyPointsTxt, allTimePointsTxt;
    private String leaderboard, rank, donorName, userGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        userProfilePic = findViewById(R.id.userProfilePic);
        userNameTxt = findViewById(R.id.userNameTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        weeklyPointsTxt = findViewById(R.id.weeklyPointsTxt);
        allTimePointsTxt = findViewById(R.id.allTimePointsTxt);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                User currUser = task.getResult().getValue(User.class);
                userGroup = currUser.getUserGroup();
            }
        });

        Intent intent = getIntent();
        leaderboard = intent.getStringExtra("leaderboard");
        rank = intent.getStringExtra("rank");

        if (leaderboard.equals("weekly")) {
            FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").child(rank).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    LeaderboardRanking currRanking = task.getResult().getValue(LeaderboardRanking.class);
                    userNameTxt.setText(currRanking.getDonorName());
                    donorName = currRanking.getDonorName();
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
                    getSupportActionBar().setTitle(donorName);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    String donorId = currRanking.getDonorId();
                    FirebaseDatabase.getInstance().getReference("Users").child(donorId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            User donor = task.getResult().getValue(User.class);
                            String imageUrl = donor.getImageUrl();
                            if (imageUrl == null) {
                                userProfilePic.setImageResource(R.drawable.profile128px);
                            } else {
                                if (imageUrl.equals("null")) {
                                    userProfilePic.setImageResource(R.drawable.profile128px);
                                } else {
                                    Picasso.get().load(imageUrl).into(userProfilePic);
                                }
                            }
                            String description = donor.getProfileDescription();
                            if (description == null) {
                                descriptionTxt.setText("No profile description provided");
                            } else {
                                if (TextUtils.isEmpty(description)) {
                                    descriptionTxt.setText("No profile description provided");
                                } else {
                                    descriptionTxt.setText(description);
                                }
                            }
                            weeklyPointsTxt.setText("Weekly Points: " + String.valueOf(donor.getNumberOfWeeklyPoints()));
                            allTimePointsTxt.setText("All-Time Points: " + String.valueOf(donor.getNumberOfPoints()));
                        }
                    });
                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference("AllTimeLeaderboard").child(rank).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                    LeaderboardRanking currRanking = task.getResult().getValue(LeaderboardRanking.class);
                    userNameTxt.setText(currRanking.getDonorName());
                    donorName = currRanking.getDonorName();
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
                    getSupportActionBar().setTitle(donorName);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    String donorId = currRanking.getDonorId();
                    FirebaseDatabase.getInstance().getReference("Users").child(donorId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            User donor = task.getResult().getValue(User.class);
                            String imageUrl = donor.getImageUrl();
                            if (imageUrl == null) {
                                userProfilePic.setImageResource(R.drawable.profile128px);
                            } else {
                                if (imageUrl.equals("null")) {
                                    userProfilePic.setImageResource(R.drawable.profile128px);
                                } else {
                                    Picasso.get().load(imageUrl).into(userProfilePic);
                                }
                            }
                            String description = donor.getProfileDescription();
                            if (description == null) {
                                descriptionTxt.setText("No profile description provided");
                            } else {
                                if (TextUtils.isEmpty(description)) {
                                    descriptionTxt.setText("No profile description provided");
                                } else {
                                    descriptionTxt.setText(description);
                                }
                            }
                            weeklyPointsTxt.setText("Weekly Points: " + String.valueOf(donor.getNumberOfWeeklyPoints()));
                            allTimePointsTxt.setText("All-Time Points: " + String.valueOf(donor.getNumberOfPoints()));
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (userGroup.equals("donor")) {
            Intent intent = new Intent(ShowUserProfile.this, DonorHomepageActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            Intent intent = new Intent(ShowUserProfile.this, RecipientHomepageActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (userGroup.equals("donor")) {
            Intent intent = new Intent(ShowUserProfile.this, DonorHomepageActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ShowUserProfile.this, RecipientHomepageActivity.class);
            startActivity(intent);
            finish();
        }
    }
}