package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.shareameal.notifications.NotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class DonorViewSlot extends AppCompatActivity {

    //widgets
    private TextView txtDate, txtTime, txtReserved, txtReservedItem, txtRecipients;

    //data
    private Slot slot;
    private String donorId, slotId, donorName;

    private HashMap<String, ArrayList<Order>> recipientIdToOrder;
    private ArrayList<String> recipientNames;
    private String recipients;
    private Food food;

    private DatabaseReference reference1, reference2, reference3, reference4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_view_slot);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Time Slot");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        donorId = user.getUid();

        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtReserved = findViewById(R.id.txtReserved);
        txtReservedItem = findViewById(R.id.txtReservedItem);
        txtRecipients = findViewById(R.id.txtRecipients);

        reference1 = FirebaseDatabase.getInstance().getReference("Orders").child("Pending");
        recipientNames = new ArrayList<>();
        recipients = "";

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        slot = bundle.getParcelable("slot");
        slotId = slot.getSlotId();

        txtDate.setText("Date: " + slot.getDate());
        txtTime.setText("Time: " + slot.getStartTime() + " - " + slot.getEndTime());
        txtReserved.setText(slot.getNumRecipients() == 0 ? "Not Reserved" : "Reserved for Donation of:");

        reference4 = FirebaseDatabase.getInstance().getReference("Users");
        reference4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    if(data.getKey().equals(donorId)) {
                        User donor = data.getValue(User.class);
                        donorName = donor.getName();
                    } else if(data.getKey().equals(slot.getRecipientId1())) {
                        recipientNames.add(data.getValue(User.class).getName());
                    } else if(data.getKey().equals(slot.getRecipientId2())) {
                        recipientNames.add(data.getValue(User.class).getName());
                    } else if(data.getKey().equals(slot.getRecipientId3())) {
                        recipientNames.add(data.getValue(User.class).getName());
                    }
                }


                for(int i = 0; i < recipientNames.size(); i++) {
                    Log.d("recipient", recipientNames.get(i));
                    if(i == recipientNames.size() - 1) {
                        recipients = recipients + recipientNames.get(i) + ".";
                    } else {
                        recipients = recipients + recipientNames.get(i) + ", ";
                    }
                }
                txtRecipients.setText("Recipient(s): " + recipients);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        if (slot.getNumRecipients() == 0) {
            txtReserved.setText("Not reserved.");
            txtReservedItem.setVisibility(View.GONE);
            txtRecipients.setVisibility(View.GONE);
        } else {
            txtReserved.setText("Reserved for donation of: ");

            // for food items
            ArrayList<String> items = new ArrayList<>();

            recipientIdToOrder = new HashMap<>();

            if(slot.getRecipientId1() != null) {
                String recipientId = slot.getRecipientId1();
                reference1.child(recipientId).child(slotId).addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Order order = data.getValue(Order.class);
                                    if (recipientIdToOrder.get(recipientId) == null) {
                                        ArrayList<Order> arrayList = new ArrayList<>();
                                        arrayList.add(order);
                                        recipientIdToOrder.put(recipientId, arrayList);
                                    } else {
                                        recipientIdToOrder.get(recipientId).add(order);
                                    }
                                    String item = order.getQuantity() + " " + order.getFoodName() + "\n";
                                    items.add(item);
                                }
                                if(slot.getRecipientId2() != null) {
                                    String recipientId = slot.getRecipientId2();
                                    reference1.child(recipientId).child(slotId).addValueEventListener(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    for (DataSnapshot data : snapshot.getChildren()) {
                                                        Order order = data.getValue(Order.class);
                                                        if (recipientIdToOrder.get(recipientId) == null) {
                                                            ArrayList<Order> arrayList = new ArrayList<>();
                                                            arrayList.add(order);
                                                            recipientIdToOrder.put(recipientId, arrayList);
                                                        } else {
                                                            recipientIdToOrder.get(recipientId).add(order);
                                                        }
                                                        String item = order.getQuantity() + " " + order.getFoodName() + "\n";
                                                        items.add(item);
                                                    }
                                                    if (slot.getRecipientId3() != null) {
                                                        String recipientId = slot.getRecipientId3();
                                                        reference1.child(recipientId).child(slotId).addValueEventListener(
                                                                new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                                        for (DataSnapshot data : snapshot.getChildren()) {
                                                                            Order order = data.getValue(Order.class);
                                                                            if (recipientIdToOrder.get(recipientId) == null) {
                                                                                ArrayList<Order> arrayList = new ArrayList<>();
                                                                                arrayList.add(order);
                                                                                recipientIdToOrder.put(recipientId, arrayList);
                                                                            } else {
                                                                                recipientIdToOrder.get(recipientId).add(order);
                                                                            }
                                                                            String item = order.getQuantity() + " " + order.getFoodName() + "\n";
                                                                            items.add(item);
                                                                        }

                                                                        String itemsText = "";
                                                                        for(int i = 0; i < items.size(); i++) {
                                                                            itemsText = new StringBuilder().append(itemsText).append(items.get(i)).toString();
                                                                        }
                                                                        txtReservedItem.setText(itemsText);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                                                    }
                                                                });
                                                    } else {
                                                        String itemsText = "";

                                                        for(int i = 0; i < items.size(); i++) {
                                                            itemsText = new StringBuilder().append(itemsText).append(items.get(i)).toString();
                                                        }
                                                        txtReservedItem.setText(itemsText);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                                }
                                            });
                                } else if(slot.getRecipientId3() != null) {
                                String recipientId = slot.getRecipientId3();
                                reference1.child(recipientId).child(slotId).addValueEventListener(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                for (DataSnapshot data : snapshot.getChildren()) {
                                                    Order order = data.getValue(Order.class);
                                                    if (recipientIdToOrder.get(recipientId) == null) {
                                                        ArrayList<Order> arrayList = new ArrayList<>();
                                                        arrayList.add(order);
                                                        recipientIdToOrder.put(recipientId, arrayList);
                                                    } else {
                                                        recipientIdToOrder.get(recipientId).add(order);
                                                    }
                                                    String item = order.getQuantity() + " " + order.getFoodName() + "\n";
                                                    items.add(item);
                                                }

                                                String itemsText = "";

                                                for(int i = 0; i < items.size(); i++) {
                                                    itemsText = new StringBuilder().append(itemsText).append(items.get(i)).toString();
                                                }
                                                txtReservedItem.setText(itemsText);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                            }
                                        });
                                } else {
                                    String itemsText = "";

                                    for(int i = 0; i < items.size(); i++) {
                                        itemsText = new StringBuilder().append(itemsText).append(items.get(i)).toString();
                                    }
                                    txtReservedItem.setText(itemsText);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
            } else if(slot.getRecipientId2() != null) {
                String recipientId = slot.getRecipientId2();
                reference1.child(recipientId).child(slotId).addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Order order = data.getValue(Order.class);
                                    if (recipientIdToOrder.get(recipientId) == null) {
                                        ArrayList<Order> arrayList = new ArrayList<>();
                                        arrayList.add(order);
                                        recipientIdToOrder.put(recipientId, arrayList);
                                    } else {
                                        recipientIdToOrder.get(recipientId).add(order);
                                    }
                                    String item = order.getQuantity() + " " + order.getFoodName() + "\n";
                                    items.add(item);
                                }

                                if (slot.getRecipientId3() != null) {
                                    String recipientId = slot.getRecipientId3();
                                    reference1.child(recipientId).child(slotId).addValueEventListener(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    for (DataSnapshot data : snapshot.getChildren()) {
                                                        Order order = data.getValue(Order.class);
                                                        if (recipientIdToOrder.get(recipientId) == null) {
                                                            ArrayList<Order> arrayList = new ArrayList<>();
                                                            arrayList.add(order);
                                                            recipientIdToOrder.put(recipientId, arrayList);
                                                        } else {
                                                            recipientIdToOrder.get(recipientId).add(order);
                                                        }
                                                        String item = order.getQuantity() + " " + order.getFoodName() + "\n";
                                                        items.add(item);
                                                    }

                                                    String itemsText = "";

                                                    for(int i = 0; i < items.size(); i++) {
                                                        itemsText = new StringBuilder().append(itemsText).append(items.get(i)).toString();
                                                    }
                                                    txtReservedItem.setText(itemsText);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                                }
                                            });
                                } else {
                                    String itemsText = "";

                                    for(int i = 0; i < items.size(); i++) {
                                        itemsText = new StringBuilder().append(itemsText).append(items.get(i)).toString();
                                    }
                                    txtReservedItem.setText(itemsText);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
            } else if (slot.getRecipientId3() != null) {
                String recipientId = slot.getRecipientId3();
                reference1.child(recipientId).child(slotId).addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Order order = data.getValue(Order.class);
                                    if (recipientIdToOrder.get(recipientId) == null) {
                                        ArrayList<Order> arrayList = new ArrayList<>();
                                        arrayList.add(order);
                                        recipientIdToOrder.put(recipientId, arrayList);
                                    } else {
                                        recipientIdToOrder.get(recipientId).add(order);
                                    }
                                    String item = order.getQuantity() + " " + order.getFoodName() + "\n";
                                    items.add(item);
                                }

                                String itemsText = "";

                                for(int i = 0; i < items.size(); i++) {
                                    itemsText = new StringBuilder().append(itemsText).append(items.get(i)).toString();
                                }
                                txtReservedItem.setText(itemsText);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
            } else {
                String itemsText = "";

                for(int i = 0; i < items.size(); i++) {
                    itemsText = new StringBuilder().append(itemsText).append(items.get(i)).toString();
                }
                txtReservedItem.setText(itemsText);
            }
        }
    }

    public void onDeleteBtn(View view) {
        FirebaseDatabase.getInstance().getReference("Slots").child("Pending").child(donorId).child(slotId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                Slot currSlot = task.getResult().getValue(Slot.class);
                String date = currSlot.getDate();
                String startTime = currSlot.getStartTime();
                String endTime = currSlot.getEndTime();
                AlertDialog dialog = new AlertDialog.Builder(DonorViewSlot.this)
                        .setTitle("Delete Time Slot")
                        .setMessage("Are you sure you want to delete this time slot?\n(" + date + ", " + startTime + " - " + endTime + ")")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference2 = FirebaseDatabase.getInstance().getReference("Slots").child("Pending").child(donorId);
                                // remove slot
                                reference2.child(slotId).removeValue();

                                // remove orders & update food quantities & notify recipients of cancelled orders
                                reference3 = FirebaseDatabase.getInstance().getReference("Foods").child(donorId);

                                if(slot.getRecipientId1() != null) {
                                    for (Order order : recipientIdToOrder.get(slot.getRecipientId1())) {
                                        reference3.child(order.getFoodId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                food = snapshot.getValue(Food.class);
                                                food.setQuantity(food.getQuantity() + order.getQuantity());
                                                reference3.child(order.getFoodId()).setValue(food);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    reference1.child(slot.getRecipientId1()).child(slotId).removeValue();
                                    reference4.child(slot.getRecipientId1()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            User recipient = snapshot.getValue(User.class);
                                            String fcmToken = recipient.getFcmToken();
                                            NotificationsSender notificationsSender = new NotificationsSender(fcmToken, "Order(s) cancelled by donor", "Your order(s) scheduled at "
                                                    + slot.getStartTime() + " on " + slot.getDate() + " have been cancelled by " + donorName, getApplication(), DonorViewSlot.this);
                                            notificationsSender.sendNotification();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }
                                if(slot.getRecipientId2() != null) {
                                    for (Order order : recipientIdToOrder.get(slot.getRecipientId2())) {
                                        reference3.child(order.getFoodId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                food = snapshot.getValue(Food.class);
                                                food.setQuantity(food.getQuantity() + order.getQuantity());
                                                reference3.child(order.getFoodId()).setValue(food);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    reference1.child(slot.getRecipientId2()).child(slotId).removeValue();
                                    reference4.child(slot.getRecipientId2()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            User recipient = snapshot.getValue(User.class);
                                            String fcmToken = recipient.getFcmToken();
                                            NotificationsSender notificationsSender = new NotificationsSender(fcmToken, "Order(s) cancelled by donor", "Your order(s) scheduled at "
                                                    + slot.getStartTime() + " on " + slot.getDate() + " have been cancelled by " + donorName, getApplication(), DonorViewSlot.this);
                                            notificationsSender.sendNotification();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }
                                if(slot.getRecipientId3() != null) {
                                    for (Order order : recipientIdToOrder.get(slot.getRecipientId3())) {
                                        reference3.child(order.getFoodId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                food = snapshot.getValue(Food.class);
                                                food.setQuantity(food.getQuantity() + order.getQuantity());
                                                reference3.child(order.getFoodId()).setValue(food);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    reference1.child(slot.getRecipientId3()).child(slotId).removeValue();
                                    reference4.child(slot.getRecipientId3()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            User recipient = snapshot.getValue(User.class);
                                            String fcmToken = recipient.getFcmToken();
                                            NotificationsSender notificationsSender = new NotificationsSender(fcmToken, "Order(s) cancelled by donor", "Your order(s) scheduled at "
                                                    + slot.getStartTime() + " on " + slot.getDate() + " have been cancelled by " + donorName, getApplication(), DonorViewSlot.this);
                                            notificationsSender.sendNotification();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }

                                Intent intent = new Intent(DonorViewSlot.this, DonorsScheduleActivity.class);
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

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(DonorViewSlot.this, DonorsScheduleActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DonorViewSlot.this, DonorsScheduleActivity.class);
        startActivity(intent);
        finish();
    }
}
