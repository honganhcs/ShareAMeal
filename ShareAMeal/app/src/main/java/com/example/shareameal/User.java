package com.example.shareameal;

public class User {
    private String userGroup;
    private String name;
    private String address;
    private String userId;
    private String imageUrl;
    private double addressLatitude;
    private double addressLongitude;
    private int numberOfReports;

    //for donors only
    private String restaurant;
    private int numberOfPoints;
    private int numberOfWeeklyPoints;

    //for recipients only
    private int year, month, dayOfMonth;
    private int numOrdersLeft;

    // 0 - has not verified yet, 1 - in process of checking verification, 2 - verified, 3 - rejected, 4 - unclear document(s)
    private int verificationState;

    public User() {
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

    public void setNumberOfReports(int numberOfReports) {
        this.numberOfReports = numberOfReports;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setVerificationState(int verificationState) {
        this.verificationState = verificationState;
    }

    public int getVerificationState() {
        return verificationState;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfWeeklyPoints(int numberOfWeeklyPoints) {
        this.numberOfWeeklyPoints = numberOfWeeklyPoints;
    }

    public int getNumberOfWeeklyPoints() {
        return numberOfWeeklyPoints;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAddressLatitude(double addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public void setAddressLongitude(double addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public void setNumOrdersLeft(int numOrdersLeft) {
        this.numOrdersLeft = numOrdersLeft;
    }

    public int getNumOrdersLeft() {
        return numOrdersLeft;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public String getName() {
        return name;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getAddress() {
        return address;
    }

    public String getUserId() {
        return userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getAddressLatitude() {
        return addressLatitude;
    }

    public double getAddressLongitude() {
        return addressLongitude;
    }
}
