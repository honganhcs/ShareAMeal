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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;

public class AddNewTimeSlotActivity extends AppCompatActivity {

  // widgets
  private DatePickerDialog datePickerDialog;
  private Button btnDatePicker;
  private Button btnStartPicker, btnEndPicker;

  // data
  private FirebaseUser user;
  private String userId;
  private int Year, Month, Day, startHour, startMinute, endHour, endMinute;

  // other vars
  int todayYear, todayMonth, today;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new_time_slot);

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
    TimePickerDialog.OnTimeSetListener listener =
        new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            startHour = selectedHour;
            startMinute = selectedMinute;
            btnStartPicker.setText(
                String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute));
          }
        };
    TimePickerDialog timePickerDialog =
        new TimePickerDialog(this, listener, startHour, startMinute, true);
    timePickerDialog.setTitle("Select start time:");
    timePickerDialog.show();
  }

  public void openEndPicker(View view) {
    TimePickerDialog.OnTimeSetListener listener =
        new TimePickerDialog.OnTimeSetListener() {
          @Override
          public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            endHour = selectedHour;
            endMinute = selectedMinute;
            btnEndPicker.setText(
                String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute));
          }
        };
    TimePickerDialog timePickerDialog =
        new TimePickerDialog(this, listener, endHour, endMinute, true);
    timePickerDialog.setTitle("Select end time:");
    timePickerDialog.show();
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
        || (Year == todayYear && Month == todayMonth && Day <= today)) {
      Toast.makeText(
              AddNewTimeSlotActivity.this, "Please select a date after today.", Toast.LENGTH_SHORT)
          .show();
    } else if (endHour < startHour || (endHour == startHour && endMinute <= startMinute)) {
      Toast.makeText(
              AddNewTimeSlotActivity.this, "End time must be after start time.", Toast.LENGTH_SHORT)
          .show();
    } else {

      DatabaseReference reference =
          FirebaseDatabase.getInstance().getReference("Slots").child(userId);
      String slotId = reference.push().getKey();

      Slot slot = new Slot();
      slot.setDate(date);
      slot.setStartTime(startTime);
      slot.setEndTime(endTime);
      slot.setAvailability(true);
      slot.setRecipientId("");
      slot.setSlotId(slotId);

      reference.child(slotId).setValue(slot);

      reference.addValueEventListener(
          new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
              Toast.makeText(
                      AddNewTimeSlotActivity.this,
                      "Time slot successfully added.",
                      Toast.LENGTH_SHORT)
                  .show();
              Intent intent = new Intent(AddNewTimeSlotActivity.this, DonorsScheduleActivity.class);
              startActivity(intent);
              finish();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
              Toast.makeText(
                      AddNewTimeSlotActivity.this,
                      "Time slot failed to be added.",
                      Toast.LENGTH_SHORT)
                  .show();
            }
          });
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    Intent intent = new Intent(AddNewTimeSlotActivity.this, DonorsScheduleActivity.class);
    startActivity(intent);
    finish();
    return true;
  }
}
