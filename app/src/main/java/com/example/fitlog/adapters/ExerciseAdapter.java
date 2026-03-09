package com.example.fitlog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.R;
import com.example.fitlog.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<Exercise> exerciseList;

    public ExerciseAdapter(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);

        holder.name.setText(exercise.getName());
        holder.equipment.setText(exercise.getEquipment());
        holder.checkBox.setChecked(exercise.isSelected());

        // Toggle checkbox state when row is clicked
        holder.itemView.setOnClickListener(v -> {
            boolean newState = !holder.checkBox.isChecked();
            holder.checkBox.setChecked(newState);
            exercise.setSelected(newState);
        });

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            exercise.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public List<Exercise> getSelectedExercises() {
        List<Exercise> selectedItems = new ArrayList<>();
        for (Exercise e : exerciseList) {
            if (e.isSelected()) {
                selectedItems.add(e);
            }
        }
        return selectedItems;
    }



    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView name, equipment;
        CheckBox checkBox;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.exerciseName);
            equipment = itemView.findViewById(R.id.exerciseEquipment);
            checkBox = itemView.findViewById(R.id.checkboxExercise);
        }
    }
}
