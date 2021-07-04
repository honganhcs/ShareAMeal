package com.example.shareameal;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class Slot implements Parcelable {

    private String date, startTime, endTime, slotId;
    private String recipientId1, recipientId2, recipientId3;
    private int year;
    private int month;
    private int dayOfMonth;
    private int startHour;
    private int startMinute;
    private int numRecipients;

    public Slot() {
    }

    public int getNumRecipients() {
        return numRecipients;
    }

    public void setNumRecipients(int numRecipients) {
        this.numRecipients = numRecipients;
    }


    public String getRecipientId1() {
        return recipientId1;
    }

    public void setRecipientId1(String recipientId1) {
        this.recipientId1 = recipientId1;
    }

    public String getRecipientId2() {
        return recipientId2;
    }

    public void setRecipientId2(String recipientId2) {
        this.recipientId2 = recipientId2;
    }

    public String getRecipientId3() {
        return recipientId3;
    }

    public void setRecipientId3(String recipientId3) {
        this.recipientId3 = recipientId3;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(slotId);
        dest.writeString(recipientId1);
        dest.writeString(recipientId2);
        dest.writeString(recipientId3);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(dayOfMonth);
        dest.writeInt(startHour);
        dest.writeInt(startMinute);
        dest.writeInt(numRecipients);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Slot(Parcel in) {
        this.date = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.slotId = in.readString();
        this.recipientId1 = in.readString();
        this.recipientId2 = in.readString();
        this.recipientId3 = in.readString();
        this.year = in.readInt();
        this.month = in.readInt();
        this.dayOfMonth = in.readInt();
        this.startHour = in.readInt();
        this.startMinute = in.readInt();
        this.numRecipients = in.readInt();
    }

    public static final Creator<Slot> CREATOR =
            new Creator<Slot>() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public Slot createFromParcel(Parcel in) {
                    return new Slot(in);
                }

                @Override
                public Slot[] newArray(int size) {
                    return new Slot[size];
                }
            };
}
