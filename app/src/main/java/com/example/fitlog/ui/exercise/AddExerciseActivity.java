package com.example.fitlog.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fitlog.R;
import com.example.fitlog.adapters.ExerciseAdapter;
import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.example.fitlog.models.Exercise;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

public class AddExerciseActivity extends AppCompatActivity {

    private ExerciseAdapter adapter;
    private List<Exercise> allExercises = new ArrayList<>();
    private List<Exercise> filteredList = new ArrayList<>();

    private SearchView searchView;
    private WorkoutDatabaseHelper dbHelper;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_add_custom) {
                startActivity(new Intent(this, NewExerciseActivity.class));
                return true;
            }
            return false;
        });

        getSupportActionBar().setTitle("Select Exercise");

        dbHelper = new WorkoutDatabaseHelper(this);
        searchView = findViewById(R.id.searchView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        adapter = new ExerciseAdapter(filteredList);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load initial exercises
        loadExercisesFromDatabase();

        // Search logic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList.clear();
                for (Exercise e : allExercises) {
                    if (e.getName().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(e);
                    }
                }
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        // FAB to add new custom exercise
        findViewById(R.id.fabAddCustomExercise).setOnClickListener(v -> {
            startActivity(new Intent(this, NewExerciseActivity.class));
        });

        // Pull-to-refresh logic
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadExercisesFromDatabase();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Add button to return selected exercises
        findViewById(R.id.btnAddSelectedExercises).setOnClickListener(v -> {
            List<Exercise> selected = adapter.getSelectedExercises();
            if (selected.isEmpty()) {
                Toast.makeText(this, "Select at least one exercise", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Integer> selectedIds = new ArrayList<>();
            for (Exercise e : selected) {
                selectedIds.add(e.getId());
            }

            Intent resultIntent = new Intent();
            resultIntent.putIntegerArrayListExtra("selected_exercise_ids", selectedIds);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, selected.size() + " added to workout", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExercisesFromDatabase();  // Refresh list after returning
    }

    private void loadExercisesFromDatabase() {
        allExercises = dbHelper.getAllExercises();
        filteredList.clear();
        filteredList.addAll(allExercises);
        adapter.notifyDataSetChanged();
    }
}
