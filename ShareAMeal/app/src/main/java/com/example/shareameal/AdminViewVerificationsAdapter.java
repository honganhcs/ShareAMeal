package com.example.shareameal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdminViewVerificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Verification> verificationsList = new ArrayList<>();

    public AdminViewVerificationsAdapter(Context ctx, ArrayList<Verification> verificationsList) {
        this.context = ctx;
        this.verificationsList = verificationsList;
    }

    public class VerificationVH extends RecyclerView.ViewHolder {
        public TextView txt_recipient, txt_verification_time;
        public CardView cardView;

        public VerificationVH(@NonNull @NotNull View itemView) {
            super(itemView);
            txt_recipient = itemView.findViewById(R.id.txt_recipient);
            txt_verification_time = itemView.findViewById(R.id.txt_verification_time);
            cardView = itemView.findViewById(R.id.cvVerification);
        }
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_verification_item, parent, false);
        return new AdminViewVerificationsAdapter.VerificationVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VerificationVH vh = (VerificationVH) holder;
        Verification verification = verificationsList.get(position);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        String recipientId = verification.getRecipientId();
        usersRef.child(recipientId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
                } else {
                    User recipient = task.getResult().getValue(User.class);
                    vh.txt_recipient.setText(recipient.getName());
                }
            }
        });

        vh.txt_verification_time.setText("Sent at: " + verification.getVerificationTime());

        ((VerificationVH) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReviewVerification.class);
                intent.putExtra("recipientId", verification.getRecipientId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return verificationsList.size();
    }
}
