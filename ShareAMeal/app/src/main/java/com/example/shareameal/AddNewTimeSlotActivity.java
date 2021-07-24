package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

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

import java.util.Calendar;
import java.util.Locale;

public class AddNewTimeSlotActivity extends AppCompatActivity implements CustomTimePickerFragment.ExampleDialogListener {

    // widgets
    private DatePickerDialog datePickerDialog;
    private Button btnDatePicker;
    private Button btnStartPicker, btnEndPicker;
    private com.wdullaer.materialdatetimepicker.time.TimePickerDialog startTimePickerDialog, endTimePickerDialog;

    // data
    private FirebaseUser user;
    private String userId;
    private int Year, Month, Day, startHour, startMinute, endHour, endMinute;

    // other vars
    int todayYear, todayMonth, today;
    int currentHour, currentMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_time_slot);

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Add Time Slot");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDatePicker();
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnStartPicker = findViewById(R.id.btnStartPicker);
        btnEndPicker = findViewById(R.id.btnEndPicker);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        Calendar cal = Calendar.getInstance();
        todayYear = cal.get(Calendar.YEAR);
        todayMonth = cal.get(Calendar.MONTH) + 1;
        today = cal.get(Calendar.DAY_OF_MONTH);
        currentHour = cal.get(Calendar.HOUR_OF_DAY);
        currentMinute = cal.get(Calendar.MINUTE);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        Year = year;
                        Month = month;
                        Day = day;

                        String date = makeDateString(day, month, year);
                        btnDatePicker.setText(date);
                    }
                };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + String.format("%02d", day) + " " + year;
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

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void openStartPicker(View view) {
        CustomTimePickerFragment startPicker = new CustomTimePickerFragment(0);
        startPicker.show(getSupportFragmentManager(), "StartTimePickerDialog");
//        Calendar now = Calendar.getInstance();
//
//        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener listener =
//                new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
//                        startHour = hourOfDay;
//                        startMinute = minute;
//                        btnStartPicker.setText(String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute));
//                    }
//                };
//        startTimePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
//                listener, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
//        startTimePickerDialog.setThemeDark(false);
//        startTimePickerDialog.setTitle("Select start time:");
//        startTimePickerDialog.setTimeInterval(1, 30, 60);
//        startTimePickerDialog.show(getSupportFragmentManager(), "startTimePickerDialog");
    }

    public void openEndPicker(View view) {
        CustomTimePickerFragment startPicker = new CustomTimePickerFragment(1);
        startPicker.show(getSupportFragmentManager(), "EndTimePickerDialog");

//        Calendar now = Calendar.getInstance();
//        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener listener =
//                new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
//                        endHour = hourOfDay;
//                        endMinute = minute;
//                        btnEndPicker.setText(String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute));
//                    }
//                };
//        endTimePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
//                listener, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
//        endTimePickerDialog.setThemeDark(false);
//        endTimePickerDialog.setTitle("Select start time:");
//        endTimePickerDialog.setTimeInterval(1, 30, 60);
//        endTimePickerDialog.show(getSupportFragmentManager(), "endTimePickerDialog");
    }

    public void onBtnCreateSlotClick(View view) {
        String date = btnDatePicker.getText().toString().trim();
        String startTime = btnStartPicker.getText().toString().trim();
        String endTime = btnEndPicker.getText().toString().trim();

        if (date.equals("MMM DD YYY") || startTime.equals("HH:MM") || endTime.equals("HH:MM")) {
            Toast.makeText(
                    AddNewTimeSlotActivity.this,
                    "Please ensure that date, start time and end time are all selected.",
                    Toast.LENGTH_SHORT)
                    .show();
        } else if (Year < todayYear
                || (Year == todayYear && Month < todayMonth)
                || (Year == todayYear && Month == todayMonth && Day < today)) {
            Toast.makeText(
                    AddNewTimeSlotActivity.this, "Please select a date no earlier than today.", Toast.LENGTH_SHORT)
                    .show();
        } else if (Year == todayYear && Month == todayMonth && Day == today && startHour < currentHour + 2){
            Toast.makeText(
                    AddNewTimeSlotActivity.this, "Please select a time 2 hours after now.", Toast.LENGTH_SHORT)
                    .show();
        } else if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
            Toast.makeText(
                    AddNewTimeSlotActivity.this, "End time must be after start time.", Toast.LENGTH_SHORT)
                    .show();
        } else{

            DatabaseReference reference =
                    FirebaseDatabase.getInstance().getReference("Slots").child("Pending").child(userId);
            int numSlots = ((endHour - startHour) * 60 + endMinute - startMinute) / 30;

            String end = startTime;
            for (int i = 0; i < numSlots; i++) {
                int startM = (startMinute + 30 * i) % 60;
                int startH = startHour + (startMinute + 30 * i) / 60;
                int endM = (startM + 30) % 60;
                int endH = startH + (startM + 30) / 60;
                // start and end time in string form for each slot
                String start = end;
                end = String.format(Locale.getDefault(), "%02d:%02d", endH, endM);

                // push new slot to database:
                String slotId = reference.push().getKey();
                Slot slot = new Slot();

                slot.setDate(date);
                slot.setStartTime(start);
                slot.setEndTime(end);
                slot.setYear(Year);
                slot.setMonth(Month);
                slot.setDayOfMonth(Day);
                slot.setStartHour(startH);
                slot.setStartMinute(startM);
                slot.setNumRecipients(0);

                slot.setSlotId(slotId);

                reference.child(slotId).setValue(slot);
            }

            Toast.makeText(
                    AddNewTimeSlotActivity.this,
                    "Time slots successfully added.",
                    Toast.LENGTH_SHORT)
                    .show();
            Intent intent = new Intent(AddNewTimeSlotActivity.this, DonorsScheduleActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(AddNewTimeSlotActivity.this, DonorsScheduleActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddNewTimeSlotActivity.this, DonorsScheduleActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void setTime(int hour, int minute, int type) {
        if(type == 0) {
            startHour = hour;
            startMinute = minute;
            btnStartPicker.setText(String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute));
        } else {
            endHour = hour;
            endMinute = minute;
            btnEndPicker.setText(String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute));
        }
    }
}
