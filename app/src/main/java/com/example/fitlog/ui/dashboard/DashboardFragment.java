package com.example.fitlog.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.example.fitlog.databinding.FragmentDashboardBinding;
import com.example.fitlog.models.WorkoutLog;

import java.io.OutputStream;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final int EXPORT_CSV_REQUEST_CODE = 101;
    private FragmentDashboardBinding binding;
    private List<WorkoutLog> workoutLogs;

    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          android.view.ViewGroup container,
                                          Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        // Load logs from DB
        WorkoutDatabaseHelper db = new WorkoutDatabaseHelper(getContext());
        workoutLogs = db.getAllWorkoutLogs();

        // Export button logic
        binding.btnExportData.setOnClickListener(v -> {
            if (workoutLogs.isEmpty()) {
                Toast.makeText(getContext(), "No workout logs to export.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, "workout_logs.csv");
            startActivityForResult(intent, EXPORT_CSV_REQUEST_CODE);
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EXPORT_CSV_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                exportToCsv(uri);
            }
        }
    }

    private void exportToCsv(Uri uri) {
        try (OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri)) {
            StringBuilder csv = new StringBuilder();
            csv.append("Workout Name,Notes,Date,Duration (min),Completed Exercises,Completed Sets,Total Reps,Total Weight (kg)\n");

            for (WorkoutLog log : workoutLogs) {
                csv.append(log.getWorkoutName()).append(",")
                        .append("\"").append(log.getWorkoutNotes().replace("\"", "\"\"")).append("\"").append(",")
                        .append(log.getFormattedDate()).append(",")
                        .append(log.getDurationMinutes()).append(",")
                        .append(log.getCompletedExercises()).append(",")
                        .append(log.getCompletedSets()).append(",")
                        .append(log.getTotalReps()).append(",")
                        .append(log.getTotalWeight()).append("\n");
            }

            outputStream.write(csv.toString().getBytes());
            Toast.makeText(getContext(), "Exported successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
