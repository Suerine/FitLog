package com.example.fitlog.models;

import android.annotation.SuppressLint;

public class WorkoutLog {
    private int id;
    private String workoutName;
    private String workoutNotes;
    private long timestamp;
    private int completedExercises;
    private int completedSets;
    private int totalReps;
    private double totalWeight;
    private int durationMinutes;

    public WorkoutLog(int id, String workoutName, String workoutNotes, long timestamp,
                      int completedExercises, int completedSets, int totalReps, double totalWeight,int durationMinutes) {
        this.id = id;
        this.workoutName = workoutName;
        this.workoutNotes = workoutNotes;
        this.timestamp = timestamp;
        this.completedExercises = completedExercises;
        this.completedSets = completedSets;
        this.totalReps = totalReps;
        this.totalWeight = totalWeight;
        this.durationMinutes = durationMinutes;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getWorkoutNotes() {
        return workoutNotes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getCompletedExercises() {
        return completedExercises;
    }

    public int getCompletedSets() {
        return completedSets;
    }

    public int getTotalReps() {
        return totalReps;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    // Optional - for formatted display (e.g. used in date pill)
    public String getFormattedDateTime() {
        @SuppressLint("SimpleDateFormat") java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy - HH:mm");
        return sdf.format(new java.util.Date(timestamp));
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
    public String getFormattedDate() {
        @SuppressLint("SimpleDateFormat") java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy");
        return sdf.format(new java.util.Date(timestamp));
    }


}
