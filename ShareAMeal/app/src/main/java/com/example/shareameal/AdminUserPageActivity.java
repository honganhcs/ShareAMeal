package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class AdminUserPageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private TextView editProfileTxt, logoutTxt, changePasswordTxt;
    private TextView userNameTxt;
    private ImageView userProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_page);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(AdminUserPageActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        userNameTxt = findViewById(R.id.userNameTxt);
        userProfilePic = findViewById(R.id.userProfilePic);
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

                                String imageUrl = user.getImageUrl();
                                if (imageUrl == null) {
                                    userProfilePic.setImageResource(R.drawable.profile128px);
                                } else {
                                    if (imageUrl.equals("null")) {
                                        userProfilePic.setImageResource(R.drawable.profile128px);
                                    } else {
                                        Picasso.get().load(imageUrl).into(userProfilePic);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

        // Highlighting the right icon in the bottom navigation bar
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.profile);

        editProfileTxt = findViewById(R.id.editProfileTxt);
        changePasswordTxt = findViewById(R.id.changePasswordTxt);
        logoutTxt = findViewById(R.id.logoutTxt);
        editProfileTxt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdminUserPageActivity.this, EditProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        changePasswordTxt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =
                                new Intent(AdminUserPageActivity.this, ChangePasswordActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        logoutTxt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(AdminUserPageActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int curr = item.getItemId();
                if (curr == R.id.reports) {
                    Intent intent = new Intent(AdminUserPageActivity.this, AdminViewReportedDonors.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.verifications) {

                } else if (curr == R.id.home) {
                    Intent intent = new Intent(AdminUserPageActivity.this, AdminHomepageActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }
}