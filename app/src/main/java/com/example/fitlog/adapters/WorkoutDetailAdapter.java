package com.example.fitlog.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.R;
import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.example.fitlog.models.Exercise;
import com.example.fitlog.models.ExerciseSet;

import java.util.List;

public class WorkoutDetailAdapter extends RecyclerView.Adapter<WorkoutDetailAdapter.ViewHolder> {

    private final Context context;
    private final List<Exercise> exerciseList;
    private final int workoutId;

    public WorkoutDetailAdapter(Context context, List<Exercise> exerciseList, int workoutId) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.workoutId = workoutId;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, equipmentText;
        LinearLayout setContainer;
        TextView btnAddSet;
        ImageView deleteIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exerciseName);
            equipmentText = itemView.findViewById(R.id.exerciseEquipment);
            setContainer = itemView.findViewById(R.id.setContainer);
            btnAddSet = itemView.findViewById(R.id.btnAddSet);
            deleteIcon = itemView.findViewById(R.id.iconDelete);
        }
    }

    @NonNull
    @Override
    public WorkoutDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutDetailAdapter.ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);

        holder.nameText.setText(exercise.getName());
        holder.equipmentText.setText(exercise.getEquipment() != null ? exercise.getEquipment() : "No equipment");

        holder.setContainer.removeAllViews();

        for (ExerciseSet set : exercise.getSets()) {
            View setView = LayoutInflater.from(context).inflate(R.layout.item_set_row, holder.setContainer, false);
            EditText inputReps = setView.findViewById(R.id.editReps);
            EditText inputWeight = setView.findViewById(R.id.editWeight);
            CheckBox checkBox = setView.findViewById(R.id.checkboxComplete);

            inputReps.setText(String.valueOf(set.getReps()));
            inputWeight.setText(String.valueOf(set.getWeight()));
            checkBox.setChecked(set.isCompleted());

            // Handle input updates
            inputReps.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        set.setReps(Integer.parseInt(s.toString()));
                    } catch (Exception ignored) {}
                }
            });

            inputWeight.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        set.setWeight(Double.parseDouble(s.toString()));
                    } catch (Exception ignored) {}
                }
            });

            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> set.setCompleted(isChecked));

            holder.setContainer.addView(setView);
        }

        // Add new set
        holder.btnAddSet.setOnClickListener(v -> {
            exercise.getSets().add(new ExerciseSet(10, 0));  // Default values
            notifyItemChanged(holder.getAdapterPosition());
        });

        // Delete exercise
        holder.deleteIcon.setOnClickListener(v -> {
            WorkoutDatabaseHelper db = new WorkoutDatabaseHelper(context);
            db.removeExerciseFromWorkout(workoutId, exercise.getId());
            exerciseList.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    // Inline simplified TextWatcher
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
