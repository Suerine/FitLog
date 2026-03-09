package com.example.fitlog.ui.routine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlog.R;
import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.example.fitlog.models.Exercise;
import com.example.fitlog.models.Workout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AddWorkoutActivity extends AppCompatActivity {

    private EditText editName, editNotes;
    private LinearLayout selectedExercisesContainer;
    private List<Exercise> selectedExercises = new ArrayList<>();
    private WorkoutDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        editName = findViewById(R.id.editRoutineName);
        editNotes = findViewById(R.id.editRoutineNotes);
        selectedExercisesContainer = findViewById(R.id.selectedExercisesContainer);

        // ✅ Initialize database helper
        dbHelper = new WorkoutDatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.btnAddExercises).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.fitlog.ui.exercise.AddExerciseActivity.class);
            addExerciseLauncher.launch(intent);
        });

        FloatingActionButton fabSave = findViewById(R.id.fabSaveWorkout);
        fabSave.setOnClickListener(v -> saveWorkout());
    }

    // ✅ Handle result from AddExerciseActivity
    private final ActivityResultLauncher<Intent> addExerciseLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<Integer> exerciseIds = result.getData().getIntegerArrayListExtra("selected_exercise_ids");
                    if (exerciseIds != null && !exerciseIds.isEmpty()) {
                        selectedExercises.clear();
                        for (int id : exerciseIds) {
                            Exercise e = dbHelper.getExerciseById(id);
                            if (e != null) selectedExercises.add(e);
                        }
                        updateSelectedExerciseList();
                    }
                }
            });

    // ✅ Show selected exercises below the button
    private void updateSelectedExerciseList() {
        selectedExercisesContainer.removeAllViews();
        for (Exercise exercise : selectedExercises) {
            TextView textView = new TextView(this);
            textView.setText("• " + exercise.getName());
            textView.setTextSize(16);
            textView.setTextColor(getResources().getColor(R.color.black));
            selectedExercisesContainer.addView(textView);
        }
    }

    // ✅ Save workout and selected exercises
    private void saveWorkout() {
        String name = editName.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Workout name is required");
            return;
        }

        if (selectedExercises.isEmpty()) {
            Toast.makeText(this, "Add at least one exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        Workout workout = new Workout(name, notes);
        long workoutId = dbHelper.insertWorkoutAndGetId(workout);

        if (workoutId != -1) {
            for (Exercise e : selectedExercises) {
                dbHelper.insertWorkoutExercise((int) workoutId, e.getId());
            }
            Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save workout", Toast.LENGTH_SHORT).show();
        }
    }
}
