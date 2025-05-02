package com.example.firebase;

public class RunRecord {
    public float distance; // in meters
    public long time;      // in milliseconds
    public float avgSpeed; // in km/h
    public long timestamp; // when the run was recorded

    public RunRecord() {
        // Required for Firebase
    }

    public RunRecord(float distance, long time, float avgSpeed, long timestamp) {
        this.distance = distance;
        this.time = time;
        this.avgSpeed = avgSpeed;
        this.timestamp = timestamp;
    }
}

