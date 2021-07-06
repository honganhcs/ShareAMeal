package com.example.shareameal;

public class LeaderboardRanking {
    private String donorId;
    private int numberOfPoints;
    private int position;
    private String donorName;

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorId() {
        return donorId;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public int getPosition() {
        return position;
    }

    public String getDonorName() {
        return donorName;
    }
}
