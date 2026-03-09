package com.example.fitlog.models;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private int id;
    private String name;
    private String equipment;
    private boolean selected;
    private List<ExerciseSet> sets = new ArrayList<>();

    public Exercise(int id, String name, String equipment) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEquipment() { return equipment; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public List<ExerciseSet> getSets() {
        return sets;
    }

    public void setSets(List<ExerciseSet> sets) {
        this.sets = sets;
    }

    public boolean isFullyCompleted() {
        for (ExerciseSet set : sets) {
            if (!set.isCompleted()) return false;
        }
        return true;
    }


}
