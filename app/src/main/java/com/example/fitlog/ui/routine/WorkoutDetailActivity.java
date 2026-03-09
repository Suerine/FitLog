package com.example.fitlog.ui.routine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.R;
import com.example.fitlog.adapters.WorkoutDetailAdapter;
import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.example.fitlog.models.Exercise;
import com.example.fitlog.models.ExerciseSet;
import com.example.fitlog.models.Workout;
import com.example.fitlog.ui.exercise.AddExerciseActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailActivity extends AppCompatActivity {

    private TextView workoutTitle, timerText;
    private RecyclerView exerciseRecyclerView;
    private ExtendedFloatingActionButton fabStartWorkout;
    private ImageView btnPauseResume, btnStop, menuButton;
    private MaterialButton btnAddExtraExercise;

    private boolean isRunning = false;
    private long elapsedSeconds = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private WorkoutDetailAdapter adapter;
    private int workoutId;
    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        // UI references
        workoutTitle = findViewById(R.id.textWorkoutTitle);
        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        timerText = findViewById(R.id.timerText);
        fabStartWorkout = findViewById(R.id.fabStartWorkout);
        btnPauseResume = findViewById(R.id.buttonPlayPause);
        btnStop = findViewById(R.id.buttonStop);
        menuButton = findViewById(R.id.menuButton);
        btnAddExtraExercise = findViewById(R.id.btnAddExtraExercise);

        // Load workout
        workoutId = getIntent().getIntExtra("workout_id", -1);
        if (workoutId == -1) {
            finish();
            return;
        }

        loadWorkout();

        // Menu button
        menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.menu_workout_detail, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_finish) {
                    finishWorkout();
                    return true;
                } else if (item.getItemId() == R.id.action_discard) {
                    discardWorkout();
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Add exercise
        btnAddExtraExercise.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExerciseActivity.class);
            startActivityForResult(intent, 101);
        });

        // FAB Start Workout
        fabStartWorkout.setOnClickListener(v -> startWorkout());

        // Pause/Resume
        btnPauseResume.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            } else {
                resumeTimer();
            }
        });

        // Stop
        btnStop.setOnClickListener(v -> stopWorkout());
    }

    private void loadWorkout() {
        WorkoutDatabaseHelper db = new WorkoutDatabaseHelper(this);
        workout = db.getWorkoutById(workoutId);
        List<Exercise> exercises = db.getExercisesForWorkout(workoutId);

        if (workout != null) workoutTitle.setText(workout.getName());

        adapter = new WorkoutDetailAdapter(this, exercises, workoutId);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseRecyclerView.setAdapter(adapter);
    }

    private void startWorkout() {
        isRunning = true;
        fabStartWorkout.setVisibility(View.GONE);
        findViewById(R.id.timerLayout).setVisibility(View.VISIBLE);
        startTimer();
    }

    private void pauseTimer() {
        isRunning = false;
        btnPauseResume.setImageResource(R.drawable.ic_play);
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resumeTimer() {
        isRunning = true;
        btnPauseResume.setImageResource(R.drawable.ic_pause);
        startTimer();
    }

    private void stopWorkout() {
        new AlertDialog.Builder(this)
                .setTitle("Stop Workout")
                .setMessage("Do you want to stop this workout?")
                .setPositiveButton("Stop", (dialog, which) -> {
                    pauseTimer();
                    findViewById(R.id.timerLayout).setVisibility(View.GONE);
                    fabStartWorkout.setVisibility(View.VISIBLE);
                    elapsedSeconds = 0;
                    timerText.setText("00:00");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                if (isRunning) {
                    elapsedSeconds++;
                    long mins = elapsedSeconds / 60;
                    long secs = elapsedSeconds % 60;
                    timerText.setText(String.format("%02d:%02d", mins, secs));
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void finishWorkout() {
        int totalExercises = 0;
        int completedSets = 0;
        int totalReps = 0;
        double totalWeight = 0;

        for (Exercise exercise : adapter.getExerciseList()) {
            boolean completed = false;
            for (ExerciseSet set : exercise.getSets()) {
                if (set.isCompleted()) {
                    completed = true;
                    completedSets++;
                    totalReps += set.getReps();
                    totalWeight += set.getReps() * set.getWeight();
                }
            }
            if (completed) totalExercises++;
        }

        int durationMinutes = (int) (elapsedSeconds / 60);

        int finalTotalExercises = totalExercises;
        int finalCompletedSets = completedSets;
        int finalTotalReps = totalReps;
        double finalTotalWeight = totalWeight;

        new AlertDialog.Builder(this)
                .setTitle("Workout Summary")
                .setMessage("✅ Exercises Completed: " + finalTotalExercises +
                        "\n✅ Sets Completed: " + finalCompletedSets +
                        "\n💪 Total Reps: " + finalTotalReps +
                        "\n🏋️ Total Weight: " + finalTotalWeight + "kg")
                .setPositiveButton("Finish", (dialog, which) -> {
                    new WorkoutDatabaseHelper(this).saveWorkoutSummary(
                            workoutId, finalTotalExercises, finalCompletedSets,
                            finalTotalReps, finalTotalWeight, durationMinutes
                    );
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void discardWorkout() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Workout")
                .setMessage("Are you sure you want to discard this workout?")
                .setPositiveButton("Discard", (dialog, which) -> finish())
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            ArrayList<Integer> ids = data.getIntegerArrayListExtra("selected_exercise_ids");
            if (ids != null) {
                WorkoutDatabaseHelper db = new WorkoutDatabaseHelper(this);
                for (int id : ids) db.addExerciseToWorkout(workoutId, id);
                loadWorkout();
                finish();
            }
        }
    }
}
