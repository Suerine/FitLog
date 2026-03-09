package com.example.fitlog.models;

import java.util.ArrayList;
import java.util.List;

public class Workout {
    private int id;
    private String name;
    private String notes;

    private List<Exercise> exercises = new ArrayList<>();

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }


    public Workout(int id, String name, String notes) {
        this.id = id;
        this.name = name;
        this.notes = notes;
    }

    // Constructor without ID (for inserting new)
    public Workout(String name, String notes) {
        this.name = name;
        this.notes = notes;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getNotes() { return notes; }
}
