package com.example.fitlog.ui.logs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitlog.adapters.WorkoutLogAdapter;
import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.example.fitlog.databinding.FragmentLogsBinding;
import com.example.fitlog.models.WorkoutLog;
import com.example.fitlog.ui.logs.WorkoutLogDetailActivity;

import java.util.List;

public class LogsFragment extends Fragment {

    private FragmentLogsBinding binding;
    private WorkoutLogAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogsBinding.inflate(inflater, container, false);

        // Setup RecyclerView
        binding.logRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.logRecyclerView.setAdapter(adapter);

        loadWorkoutLogs();

        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            loadWorkoutLogs();
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        return binding.getRoot();
    }
    private String formatDuration(int minutes) {
        int hrs = minutes / 60;
        int mins = minutes % 60;
        if (hrs > 0) {
            return hrs + "h " + mins + "m";
        } else {
            return mins + "m";
        }
    }

    private String getCurrentMonthYear() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    private void loadWorkoutLogs() {
        WorkoutDatabaseHelper db = new WorkoutDatabaseHelper(getContext());
        List<WorkoutLog> logs = db.getAllWorkoutLogs();

        binding.textLogMonthYear.setText(getCurrentMonthYear());

        // Update summary values
        int totalWorkouts = logs.size();
        int totalDuration = 0;
        double totalVolume = 0;

        for (WorkoutLog log : logs) {
            totalDuration += log.getDurationMinutes();
            totalVolume += log.getTotalWeight();
        }

        // Set text on the summary views
        binding.summaryWorkouts.setText(String.valueOf(totalWorkouts));
        binding.summaryDuration.setText(formatDuration(totalDuration));
        binding.summaryVolume.setText((int) totalVolume + "kg");

        // Set up adapter
        adapter = new WorkoutLogAdapter(logs, log -> {
            Intent intent = new Intent(getContext(), WorkoutLogDetailActivity.class);
            intent.putExtra("log_id", log.getId());
            startActivity(intent);
        });

        binding.logRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        loadWorkoutLogs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
