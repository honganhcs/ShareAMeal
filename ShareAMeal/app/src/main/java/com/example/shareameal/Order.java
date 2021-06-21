package com.example.shareameal;

public class Order {
    private String date;
    private String startTime;
    private String endTime;
    private String donorId;
    private String foodId;
    private String slotId;
    private String foodName;
    private String foodImageURL;

    private int quantity;

    public Order() {}

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

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }


    public String getDonorId() {
        return donorId;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodImageURL() {
        return foodImageURL;
    }

    public void setFoodImageURL(String foodImageURL) {
        this.foodImageURL = foodImageURL;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}
