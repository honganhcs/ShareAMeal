package com.example.shareameal;

public class Verification {
    private String fileUrl;
    private String recipientId;
    private double monthlyIncome;

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
}
