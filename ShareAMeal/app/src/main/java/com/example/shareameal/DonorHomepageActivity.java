package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DonorHomepageActivity extends AppCompatActivity {
    // From Monday 00:00:00 to Sunday 23:59:59, 1 second = 1000 ms
    private final long ONE_WEEK_IN_MS = 604800000L;

    private BottomNavigationView bottomNav;
    private TextView userNameTxt, reminderQty, refreshTimestampTxt, weeklyStatusTxt;
    private int numberOfReports;
    private ConstraintLayout donorBlockedMsg;
    private TextView leaderboardOneFirstTxt, leaderboardOneFirstQtyTxt, leaderboardOneSecondTxt, leaderboardOneSecondQtyTxt,
            leaderboardOneThirdTxt, leaderboardOneThirdQtyTxt, leaderboardOneFourthTxt, leaderboardOneFourthQtyTxt,
            leaderboardOneFifthTxt, leaderboardOneFifthQtyTxt;
    private TextView leaderboardTwoFirstTxt, leaderboardTwoFirstQtyTxt, leaderboardTwoSecondTxt, leaderboardTwoSecondQtyTxt,
            leaderboardTwoThirdTxt, leaderboardTwoThirdQtyTxt, leaderboardTwoFourthTxt, leaderboardTwoFourthQtyTxt,
            leaderboardTwoFifthTxt, leaderboardTwoFifthQtyTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_homepage);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(DonorHomepageActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        userNameTxt = findViewById(R.id.userNameTxt);
        reminderQty = findViewById(R.id.reminderQty);
        donorBlockedMsg = findViewById(R.id.donorBlockedMsg);
        refreshTimestampTxt = findViewById(R.id.refreshTimestampTxt);
        weeklyStatusTxt = findViewById(R.id.weeklyStatusTxt);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .child(userid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                userNameTxt.setText(user.getName());
                                numberOfReports = user.getNumberOfReports();

                                if (numberOfReports < 3) {
                                    donorBlockedMsg.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

        leaderboardOneFirstTxt = findViewById(R.id.leaderboardOneFirstTxt);
        leaderboardOneFirstQtyTxt = findViewById(R.id.leaderboardOneFirstQtyTxt);
        leaderboardOneSecondTxt = findViewById(R.id.leaderboardOneSecondTxt);
        leaderboardOneSecondQtyTxt = findViewById(R.id.leaderboardOneSecondQtyTxt);
        leaderboardOneThirdTxt = findViewById(R.id.leaderboardOneThirdTxt);
        leaderboardOneThirdQtyTxt = findViewById(R.id.leaderboardOneThirdQtyTxt);
        leaderboardOneFourthTxt = findViewById(R.id.leaderboardOneFourthTxt);
        leaderboardOneFourthQtyTxt = findViewById(R.id.leaderboardOneFourthQtyTxt);
        leaderboardOneFifthTxt = findViewById(R.id.leaderboardOneFifthTxt);
        leaderboardOneFifthQtyTxt = findViewById(R.id.leaderboardOneFifthQtyTxt);
        ArrayList<TextView> leaderboardOneTextViews = new ArrayList<>(Arrays.asList(leaderboardOneFirstTxt, leaderboardOneSecondTxt,
                leaderboardOneThirdTxt, leaderboardOneFourthTxt, leaderboardOneFifthTxt, leaderboardOneFirstQtyTxt, leaderboardOneSecondQtyTxt,
                leaderboardOneThirdQtyTxt, leaderboardOneFourthQtyTxt, leaderboardOneFifthQtyTxt));

        leaderboardTwoFirstTxt = findViewById(R.id.leaderboardTwoFirstTxt);
        leaderboardTwoFirstQtyTxt = findViewById(R.id.leaderboardTwoFirstQtyTxt);
        leaderboardTwoSecondTxt = findViewById(R.id.leaderboardTwoSecondTxt);
        leaderboardTwoSecondQtyTxt = findViewById(R.id.leaderboardTwoSecondQtyTxt);
        leaderboardTwoThirdTxt = findViewById(R.id.leaderboardTwoThirdTxt);
        leaderboardTwoThirdQtyTxt = findViewById(R.id.leaderboardTwoThirdQtyTxt);
        leaderboardTwoFourthTxt = findViewById(R.id.leaderboardTwoFourthTxt);
        leaderboardTwoFourthQtyTxt = findViewById(R.id.leaderboardTwoFourthQtyTxt);
        leaderboardTwoFifthTxt = findViewById(R.id.leaderboardTwoFifthTxt);
        leaderboardTwoFifthQtyTxt = findViewById(R.id.leaderboardTwoFifthQtyTxt);
        ArrayList<TextView> leaderboardTwoTextViews = new ArrayList<>(Arrays.asList(leaderboardTwoFirstTxt, leaderboardTwoSecondTxt,
                leaderboardTwoThirdTxt, leaderboardTwoFourthTxt, leaderboardTwoFifthTxt, leaderboardTwoFirstQtyTxt, leaderboardTwoSecondQtyTxt,
                leaderboardTwoThirdQtyTxt, leaderboardTwoFourthQtyTxt, leaderboardTwoFifthQtyTxt));

        // Setting up weekly leaderboard
        FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                long lastRefreshedTimeInMs = (long) snapshot.child("lastRefreshedTimeInMs").getValue();
                long currentTimeInMs = Calendar.getInstance().getTimeInMillis();
                long refreshIntervalInMin = (long) snapshot.child("refreshInterval").getValue();
                long refreshIntervalInMs = refreshIntervalInMin * 60000L;
                long startDateInMs = (long) snapshot.child("startDateInMs").getValue();

                // Resetting weekly points of donors if a week has passed since start date
                if (currentTimeInMs >= startDateInMs + ONE_WEEK_IN_MS) {
                    reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            DataSnapshot snapshot = task.getResult();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User currUser = data.getValue(User.class);
                                if (currUser.getUserGroup().equals("donor")) {
                                    String donorId = currUser.getUserId();
                                    reference.child(donorId).child("numberOfWeeklyPoints").setValue(0);
                                }
                            }
                        }
                    });
                    long timeDifferentialInMs = currentTimeInMs - startDateInMs;
                    long numberOfWeeksPassed = timeDifferentialInMs / ONE_WEEK_IN_MS;
                    long latestWeekInMs = startDateInMs + (numberOfWeeksPassed * ONE_WEEK_IN_MS);
                    String latestStartDate = getDate(latestWeekInMs, "dd MMM yyyy");
                    String latestStartDateAndTime = getDate(latestWeekInMs, "dd MMM yyyy HH:mm:ss");
                    String latestEndDate = getDate(latestWeekInMs + ONE_WEEK_IN_MS - 1000, "dd MMM yyyy");
                    FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").child("startDate").setValue(latestStartDateAndTime);
                    FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").child("startDateInMs").setValue(latestWeekInMs);
                    weeklyStatusTxt.setText("(" + latestStartDate + " - " + latestEndDate + ")");
                } else {
                    String startDate = getDate(startDateInMs, "dd MMM yyyy");
                    String endDate = getDate(startDateInMs + ONE_WEEK_IN_MS - 1000, "dd MMM yyyy");
                    weeklyStatusTxt.setText("(" + startDate + " - " + endDate + ")");
                }

                // Refreshes weekly leaderboard according to refresh interval
                if (currentTimeInMs > lastRefreshedTimeInMs + refreshIntervalInMs) {
                    reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            ArrayList<User> donorWeeklyRankings = new ArrayList<>();
                            DataSnapshot snapshot = task.getResult();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User currUser = data.getValue(User.class);
                                if (currUser.getUserGroup().equals("donor")) {
                                    if (currUser.getNumberOfReports() < 3) {
                                        donorWeeklyRankings.add(currUser);
                                    }
                                }
                            }
                            Collections.sort(donorWeeklyRankings, new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) {
                                    if (o1.getNumberOfWeeklyPoints() < o2.getNumberOfWeeklyPoints()) {
                                        return 1;
                                    } else if (o1.getNumberOfWeeklyPoints() > o2.getNumberOfWeeklyPoints()) {
                                        return -1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                            for (int i = 0; i < 5; i++) {
                                User rankedUser = donorWeeklyRankings.get(i);
                                LeaderboardRanking ranking = new LeaderboardRanking();
                                ranking.setPosition(i + 1);
                                ranking.setDonorId(rankedUser.getUserId());
                                if (rankedUser.getRestaurant() == null || TextUtils.isEmpty(rankedUser.getRestaurant())) {
                                    ranking.setDonorName(rankedUser.getName());
                                } else {
                                    ranking.setDonorName(rankedUser.getRestaurant());
                                }
                                ranking.setNumberOfPoints(rankedUser.getNumberOfWeeklyPoints());
                                FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").child(String.valueOf(i + 1)).setValue(ranking);
                            }
                            for (int i = 1; i <= 5; i++) {
                                int pos = i;
                                FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").child(String.valueOf(i)).get()
                                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                LeaderboardRanking currRanking = task.getResult().getValue(LeaderboardRanking.class);
                                                if (currRanking.getDonorId().equals(userid)) {
                                                    leaderboardOneTextViews.get(pos - 1).setText(String.valueOf(pos) + ". " + currRanking.getDonorName() + " (Me)");
                                                    leaderboardOneTextViews.get(pos - 1).setTextColor(Color.parseColor("#C45508"));
                                                    leaderboardOneTextViews.get(pos - 1).setTypeface(null, Typeface.BOLD);
                                                    leaderboardOneTextViews.get(pos + 4).setText(String.valueOf(currRanking.getNumberOfPoints()));
                                                    leaderboardOneTextViews.get(pos + 4).setTextColor(Color.parseColor("#C45508"));
                                                    leaderboardOneTextViews.get(pos + 4).setTypeface(null, Typeface.BOLD);
                                                } else {
                                                    leaderboardOneTextViews.get(pos - 1).setText(String.valueOf(pos) + ". " + currRanking.getDonorName());
                                                    leaderboardOneTextViews.get(pos + 4).setText(String.valueOf(currRanking.getNumberOfPoints()));
                                                }
                                            }
                                        });
                            }
                        }
                    });
                } else {
                    for (int i = 1; i <= 5; i++) {
                        int pos = i;
                        FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").child(String.valueOf(i)).get()
                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                        LeaderboardRanking currRanking = task.getResult().getValue(LeaderboardRanking.class);
                                        if (currRanking.getDonorId().equals(userid)) {
                                            leaderboardOneTextViews.get(pos - 1).setText(String.valueOf(pos) + ". " + currRanking.getDonorName() + " (Me)");
                                            leaderboardOneTextViews.get(pos - 1).setTextColor(Color.parseColor("#C45508"));
                                            leaderboardOneTextViews.get(pos - 1).setTypeface(null, Typeface.BOLD);
                                            leaderboardOneTextViews.get(pos + 4).setText(String.valueOf(currRanking.getNumberOfPoints()));
                                            leaderboardOneTextViews.get(pos + 4).setTextColor(Color.parseColor("#C45508"));
                                            leaderboardOneTextViews.get(pos + 4).setTypeface(null, Typeface.BOLD);
                                        } else {
                                            leaderboardOneTextViews.get(pos - 1).setText(String.valueOf(pos) + ". " + currRanking.getDonorName());
                                            leaderboardOneTextViews.get(pos + 4).setText(String.valueOf(currRanking.getNumberOfPoints()));
                                        }
                                    }
                                });
                    }
                }
            }
        });

        // Setting up all time leaderboard
        FirebaseDatabase.getInstance().getReference("AllTimeLeaderboard").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                long lastRefreshedTimeInMs = (long) snapshot.child("lastRefreshedTimeInMs").getValue();
                long currentTimeInMs = Calendar.getInstance().getTimeInMillis();
                long refreshIntervalInMin = (long) snapshot.child("refreshInterval").getValue();
                long refreshIntervalInMs = refreshIntervalInMin * 60000L;

                // If current system time has passed the last refreshed time by a set interval, refresh the leaderboard standings in database
                if (currentTimeInMs > lastRefreshedTimeInMs + refreshIntervalInMs) {
                    reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                            ArrayList<User> donorRankings = new ArrayList<>();
                            DataSnapshot snapshot = task.getResult();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User currUser = data.getValue(User.class);
                                if (currUser.getUserGroup().equals("donor")) {
                                    if (currUser.getNumberOfReports() < 3) {
                                        donorRankings.add(currUser);
                                    }
                                }
                            }
                            Collections.sort(donorRankings, new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) {
                                    if (o1.getNumberOfPoints() < o2.getNumberOfPoints()) {
                                        return 1;
                                    } else if (o1.getNumberOfPoints() > o2.getNumberOfPoints()) {
                                        return -1;
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                            for (int i = 0; i < 5; i++) {
                                User rankedUser = donorRankings.get(i);
                                LeaderboardRanking ranking = new LeaderboardRanking();
                                ranking.setPosition(i + 1);
                                ranking.setDonorId(rankedUser.getUserId());
                                if (rankedUser.getRestaurant() == null || TextUtils.isEmpty(rankedUser.getRestaurant())) {
                                    ranking.setDonorName(rankedUser.getName());
                                } else {
                                    ranking.setDonorName(rankedUser.getRestaurant());
                                }
                                ranking.setNumberOfPoints(rankedUser.getNumberOfPoints());
                                FirebaseDatabase.getInstance().getReference("AllTimeLeaderboard").child(String.valueOf(i + 1)).setValue(ranking);
                            }

                            long timeDifferentialInMs = currentTimeInMs - lastRefreshedTimeInMs;
                            long numberOfIntervalsPassed = timeDifferentialInMs / refreshIntervalInMs;
                            long latestRefreshedTimeInMs = lastRefreshedTimeInMs + (numberOfIntervalsPassed * refreshIntervalInMs);
                            String latestRefreshedDateAndTime = getDate(latestRefreshedTimeInMs, "dd MMM yyyy HH:mm:ss");
                            FirebaseDatabase.getInstance().getReference("AllTimeLeaderboard").child("lastRefreshedTime").setValue(latestRefreshedDateAndTime);
                            FirebaseDatabase.getInstance().getReference("AllTimeLeaderboard").child("lastRefreshedTimeInMs").setValue(latestRefreshedTimeInMs);
                            FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").child("lastRefreshedTime").setValue(latestRefreshedDateAndTime);
                            FirebaseDatabase.getInstance().getReference("WeeklyLeaderboard").child("lastRefreshedTimeInMs").setValue(latestRefreshedTimeInMs);
                            refreshTimestampTxt.setText("Last refreshed at: " + latestRefreshedDateAndTime + "\n(Refreshed every " +
                                    String.valueOf(refreshIntervalInMin) + " mins)");

                            for (int i = 1; i <= 5; i++) {
                                int pos = i;
                                FirebaseDatabase.getInstance().getReference("AllTimeLeaderboard").child(String.valueOf(i)).get()
                                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                LeaderboardRanking currRanking = task.getResult().getValue(LeaderboardRanking.class);
                                                if (currRanking.getDonorId().equals(userid)) {
                                                    leaderboardTwoTextViews.get(pos - 1).setText(String.valueOf(pos) + ". " + currRanking.getDonorName() + " (Me)");
                                                    leaderboardTwoTextViews.get(pos - 1).setTextColor(Color.parseColor("#C45508"));
                                                    leaderboardTwoTextViews.get(pos - 1).setTypeface(null, Typeface.BOLD);
                                                    leaderboardTwoTextViews.get(pos + 4).setText(String.valueOf(currRanking.getNumberOfPoints()));
                                                    leaderboardTwoTextViews.get(pos + 4).setTextColor(Color.parseColor("#C45508"));
                                                    leaderboardTwoTextViews.get(pos + 4).setTypeface(null, Typeface.BOLD);
                                                } else {
                                                    leaderboardTwoTextViews.get(pos - 1).setText(String.valueOf(pos) + ". " + currRanking.getDonorName());
                                                    leaderboardTwoTextViews.get(pos + 4).setText(String.valueOf(currRanking.getNumberOfPoints()));
                                                }
                                            }
                                        });
                            }
                        }
                    });
                } else {
                    for (int i = 1; i <= 5; i++) {
                        int pos = i;
                        FirebaseDatabase.getInstance().getReference("AllTimeLeaderboard").child(String.valueOf(i)).get()
                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                        LeaderboardRanking currRanking = task.getResult().getValue(LeaderboardRanking.class);
                                        if (currRanking.getDonorId().equals(userid)) {
                                            leaderboardTwoTextViews.get(pos - 1).setText(String.valueOf(pos) + ". " + currRanking.getDonorName() + " (Me)");
                                            leaderboardTwoTextViews.get(pos - 1).setTextColor(Color.parseColor("#C45508"));
                                            leaderboardTwoTextViews.get(pos - 1).setTypeface(null, Typeface.BOLD);
                                            leaderboardTwoTextViews.get(pos + 4).setText(String.valueOf(currRanking.getNumberOfPoints()));
                                            leaderboardTwoTextViews.get(pos + 4).setTextColor(Color.parseColor("#C45508"));
                                            leaderboardTwoTextViews.get(pos + 4).setTypeface(null, Typeface.BOLD);
                                        } else {
                                            leaderboardTwoTextViews.get(pos - 1).setText(String.valueOf(pos) + ". " + currRanking.getDonorName());
                                            leaderboardTwoTextViews.get(pos + 4).setText(String.valueOf(currRanking.getNumberOfPoints()));
                                        }
                                    }
                                });
                    }
                    FirebaseDatabase.getInstance().getReference("AllTimeLeaderboard").get()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                    refreshTimestampTxt.setText("Last refreshed at: " + task.getResult().child("lastRefreshedTime").getValue()
                                            + "\n(Refreshed every " + String.valueOf(task.getResult().child("refreshInterval").getValue()) + " mins)");
                                }
                            });
                }
            }
        });


        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");
        foodRef
                .child(userid)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                int totalQty = 0;
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Food currFood = data.getValue(Food.class);
                                    totalQty += currFood.getQuantity();
                                }
                                reminderQty.setText(String.valueOf(totalQty));
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int curr = item.getItemId();
                        if (curr == R.id.food) {
                            if (numberOfReports < 3) {
                                Intent intent = new Intent(DonorHomepageActivity.this, DonateFoodActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(DonorHomepageActivity.this, "You are not allowed access to this page", Toast.LENGTH_SHORT).show();
                            }
                        } else if (curr == R.id.schedule) {
                            if (numberOfReports < 3) {
                                Intent intent = new Intent(DonorHomepageActivity.this, DonorsScheduleActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(DonorHomepageActivity.this, "You are not allowed access to this page", Toast.LENGTH_SHORT).show();
                            }
                        } else if (curr == R.id.profile) {
                            Intent intent = new Intent(DonorHomepageActivity.this, DonorUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {}

    private String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
