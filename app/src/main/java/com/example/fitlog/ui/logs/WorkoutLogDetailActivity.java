package com.example.fitlog.ui.logs;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlog.R;
import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.example.fitlog.models.WorkoutLog;

import java.util.List;

public class WorkoutLogDetailActivity extends AppCompatActivity {

    private TextView textWorkoutName, textNotes, textDate, textDuration, textVolume;
    private LinearLayout containerExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log_detail);

        // Bind views
        textWorkoutName = findViewById(R.id.textWorkoutName);
        textNotes = findViewById(R.id.textNotes);
        textDate = findViewById(R.id.textDate);
        textDuration = findViewById(R.id.textDuration);
        textVolume = findViewById(R.id.textVolume);
        containerExercises = findViewById(R.id.containerExercises);
        TextView textMuscles = findViewById(R.id.textMuscles);

        // Get log ID from intent
        int logId = getIntent().getIntExtra("log_id", -1);
        if (logId == -1) {
            finish();
            return;
        }

        // Load from DB
        WorkoutDatabaseHelper db = new WorkoutDatabaseHelper(this);
        WorkoutLog log = db.getWorkoutLogById(logId);
        if (log == null) {
            finish();
            return;
        }

        // Populate workout info
        textWorkoutName.setText(log.getWorkoutName());
        textNotes.setText(log.getWorkoutNotes().isEmpty() ? "No notes" : log.getWorkoutNotes());
        textDate.setText(log.getFormattedDate());
        textDuration.setText("Duration: " + log.getDurationMinutes() + " min");
        textVolume.setText("Volume: " + (int) log.getTotalWeight() + "kg");

        // Load muscles list
        String muscleList = db.getMusclesForWorkoutLog(logId);
        textMuscles.setText("Muscles: " + (muscleList.isEmpty() ? "N/A" : muscleList));

        // Load and display formatted exercise summary
        List<String> exerciseDetails = db.getWorkoutLogExercisesFormatted(logId);
        containerExercises.removeAllViews();

        for (String line : exerciseDetails) {
            TextView tv = new TextView(this);
            tv.setText("• " + line);
            tv.setTextSize(14f);
            tv.setTextColor(getResources().getColor(R.color.black));
            tv.setPadding(0, 4, 0, 4);
            containerExercises.addView(tv);
        }
    }

}
