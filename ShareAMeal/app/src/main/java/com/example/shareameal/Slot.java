package com.example.shareameal;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class Slot implements Parcelable {

    private String date, startTime, endTime, recipientId, slotId;
    private boolean availability;

    public Slot() {
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

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
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
        dest.writeString(recipientId);
        dest.writeString(slotId);
        dest.writeBoolean(availability);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Slot(Parcel in) {
        this.date = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.recipientId = in.readString();
        this.slotId = in.readString();
        this.availability = in.readBoolean();
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
