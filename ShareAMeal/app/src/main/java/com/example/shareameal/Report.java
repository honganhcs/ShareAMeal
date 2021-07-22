package com.example.shareameal;

import android.os.Parcel;
import android.os.Parcelable;

public class Report implements Parcelable {
    private String slotId;
    private String recipientId;
    private String donorId;
    private String reportId;
    private String reportContent;
    private String reportImageUrl;
    private String reportTime;
    private String foodId;
    private int year, month, day, hour, minute;

    public Report() {}

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public void setReportImageUrl(String reportImageUrl) {
        this.reportImageUrl = reportImageUrl;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getSlotId() {
        return slotId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getDonorId() {
        return donorId;
    }

    public String getReportId() {
        return reportId;
    }

    public String getReportContent() {
        return reportContent;
    }

    public String getReportImageUrl() {
        return reportImageUrl;
    }

    public String getReportTime() {
        return reportTime;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(slotId);
        dest.writeString(recipientId);
        dest.writeString(donorId);
        dest.writeString(reportContent);
        dest.writeString(reportImageUrl);
        dest.writeString(reportTime);
    }

    public Report(Parcel source) {
        this.slotId = source.readString();
        this.recipientId = source.readString();
        this.donorId = source.readString();
        this.reportContent = source.readString();
        this.reportImageUrl = source.readString();
        this.reportTime = source.readString();
    }

    public static final Parcelable.Creator<Report> CREATOR =
            new Parcelable.Creator<Report>() {
                public Report createFromParcel(Parcel in) {
                    return new Report(in);
                }

                public Report[] newArray(int size) {
                    return new Report[size];
                }
            };
}
