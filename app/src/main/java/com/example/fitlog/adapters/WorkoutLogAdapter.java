package com.example.fitlog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.R;
import com.example.fitlog.models.WorkoutLog;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WorkoutLogAdapter extends RecyclerView.Adapter<WorkoutLogAdapter.ViewHolder> {

    private List<WorkoutLog> logs;
    private OnLogClickListener listener;

    public interface OnLogClickListener {
        void onLogClick(WorkoutLog log);
    }

    public WorkoutLogAdapter(List<WorkoutLog> logs, OnLogClickListener listener) {
        this.logs = logs;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textWorkoutName, textDate, textVolumeSummary;

        public ViewHolder(View itemView) {
            super(itemView);
            textWorkoutName = itemView.findViewById(R.id.textWorkoutName);
            textDate = itemView.findViewById(R.id.textDate);
            textVolumeSummary = itemView.findViewById(R.id.textVolumeSummary);
        }
    }

    @NonNull
    @Override
    public WorkoutLogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_log, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutLogAdapter.ViewHolder holder, int position) {
        WorkoutLog log = logs.get(position);

        holder.textWorkoutName.setText(log.getWorkoutName());

        // Format timestamp to date pill like "02 Apr"
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        holder.textDate.setText(sdf.format(log.getTimestamp()));

        // Example volume summary: "3 exercises, 8 sets, 650 kg"
        String volume = log.getCompletedExercises() + " exercises, "
                + log.getCompletedSets() + " sets, "
                + (int) log.getTotalWeight() + " kg";
        holder.textVolumeSummary.setText(volume);

        holder.itemView.setOnClickListener(v -> listener.onLogClick(log));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }
}
