package com.example.fitlog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.R;
import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.example.fitlog.models.Exercise;
import com.example.fitlog.models.Workout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private final List<Workout> workouts;
    private final OnWorkoutClickListener listener;
    private final WorkoutDatabaseHelper dbHelper;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }

    public WorkoutAdapter(List<Workout> workouts, WorkoutDatabaseHelper dbHelper, OnWorkoutClickListener listener) {
        this.workouts = workouts;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView title, notes, exerciseCount, muscles;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textWorkoutTitle);
            notes = itemView.findViewById(R.id.textWorkoutSubtitle);
            exerciseCount = itemView.findViewById(R.id.textExerciseCount);
            muscles = itemView.findViewById(R.id.textMuscles);
        }
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);

        holder.title.setText(workout.getName());
        holder.notes.setText(workout.getNotes());

        List<Exercise> exercises = dbHelper.getExercisesForWorkout(workout.getId());
        int count = exercises.size();
        holder.exerciseCount.setText(count + (count == 1 ? " Exercise" : " Exercises"));


        // Aggregate muscles
        Set<String> muscleSet = new HashSet<>();
        for (Exercise exercise : exercises) {
            muscleSet.addAll(dbHelper.getPrimaryMuscles(exercise.getId()));
            muscleSet.addAll(dbHelper.getSecondaryMuscles(exercise.getId()));
        }

        String musclesInvolved = muscleSet.isEmpty()
                ? "No muscles listed"
                : String.join(", ", muscleSet);
        holder.muscles.setText(musclesInvolved);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWorkoutClick(workout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }
}
