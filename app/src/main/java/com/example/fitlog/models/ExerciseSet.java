package com.example.fitlog.models;

public class ExerciseSet {
    private int reps;
    private double weight;
    private boolean isCompleted;

    public ExerciseSet(int reps, double weight) {
        this.reps = reps;
        this.weight = weight;
        this.isCompleted = false;
    }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
