package com.example.shareameal;

public class Verification {
    private String fileUrl;
    private String recipientId;
    private double monthlyIncome;
    private String verificationTime;
    private int year, month, day, hour, minute;
    private String fileExtension;

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setVerificationTime(String verificationTime) {
        this.verificationTime = verificationTime;
    }

    public String getVerificationTime() {
        return verificationTime;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
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
}
