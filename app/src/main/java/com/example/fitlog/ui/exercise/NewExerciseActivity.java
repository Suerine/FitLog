
package com.example.fitlog.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitlog.R;
import com.example.fitlog.database.WorkoutDatabaseHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class NewExerciseActivity extends AppCompatActivity {

    private EditText nameInput, descriptionInput;
    private Spinner typeSpinner;
    private TextView equipmentDisplay, primaryMusclesDisplay, secondaryMusclesDisplay;
    private LinearLayout equipmentListContainer, primaryMusclesListContainer, secondaryMusclesListContainer;

    private static final int REQUEST_EQUIPMENT = 1001;
    private static final int REQUEST_PRIMARY_MUSCLES = 1002;
    private static final int REQUEST_SECONDARY_MUSCLES = 1003;

    private ArrayList<String> selectedEquipment = new ArrayList<>();
    private ArrayList<String> selectedPrimaryMuscles = new ArrayList<>();
    private ArrayList<String> selectedSecondaryMuscles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hides the default app name title bar (ActionBar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exercise);

        nameInput = findViewById(R.id.inputExerciseName);
        descriptionInput = findViewById(R.id.inputExerciseDescription);
        typeSpinner = findViewById(R.id.spinnerExerciseType);

        equipmentDisplay = findViewById(R.id.equipmentDisplay);
        primaryMusclesDisplay = findViewById(R.id.primaryMusclesDisplay);
        secondaryMusclesDisplay = findViewById(R.id.secondaryMusclesDisplay);

        equipmentListContainer = findViewById(R.id.equipmentListContainer);
        primaryMusclesListContainer = findViewById(R.id.primaryMusclesListContainer);
        secondaryMusclesListContainer = findViewById(R.id.secondaryMusclesListContainer);

        MaterialButton saveButton = findViewById(R.id.btnSaveExercise);
        saveButton.setOnClickListener(v -> saveExercise());

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save_exercise) {
                saveExercise();
                return true;
            }
            return false;
        });
        getSupportActionBar().setTitle("Add Exercise");
        toolbar.setNavigationOnClickListener(v -> finish());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.exercise_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        equipmentDisplay.setOnClickListener(v -> {
            Intent intent = new Intent(this, EquipmentSelectionActivity.class);
            intent.putStringArrayListExtra("selected", selectedEquipment);
            startActivityForResult(intent, REQUEST_EQUIPMENT);
        });

        primaryMusclesDisplay.setOnClickListener(v -> {
            Intent intent = new Intent(this, MuscleSelectionActivity.class);
            intent.putStringArrayListExtra("selected", selectedPrimaryMuscles);
            intent.putExtra("muscle_type", "primary");
            startActivityForResult(intent, REQUEST_PRIMARY_MUSCLES);
        });

        secondaryMusclesDisplay.setOnClickListener(v -> {
            Intent intent = new Intent(this, MuscleSelectionActivity.class);
            intent.putStringArrayListExtra("selected", selectedSecondaryMuscles);
            intent.putExtra("muscle_type", "secondary");
            startActivityForResult(intent, REQUEST_SECONDARY_MUSCLES);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> selected = data.getStringArrayListExtra("selected");

            if (selected == null) return;

            switch (requestCode) {
                case REQUEST_EQUIPMENT:
                    selectedEquipment = selected;
                    updateContainerViews(equipmentListContainer, selectedEquipment);
                    break;
                case REQUEST_PRIMARY_MUSCLES:
                    selectedPrimaryMuscles = selected;
                    updateContainerViews(primaryMusclesListContainer, selectedPrimaryMuscles);
                    break;
                case REQUEST_SECONDARY_MUSCLES:
                    selectedSecondaryMuscles = selected;
                    updateContainerViews(secondaryMusclesListContainer, selectedSecondaryMuscles);
                    break;
            }
        }
    }

    private void updateContainerViews(LinearLayout container, ArrayList<String> items) {
        container.removeAllViews();
        for (String item : items) {
            TextView tv = new TextView(this);
            tv.setText("• " + item);
            tv.setTextSize(14f);
            tv.setTextColor(getResources().getColor(R.color.black));
            container.addView(tv);
        }
    }

    private void saveExercise() {
        String name = nameInput.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
        String description = descriptionInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Exercise name is required");
            nameInput.requestFocus();
            return;
        }

        WorkoutDatabaseHelper dbHelper = new WorkoutDatabaseHelper(this);
        long id = dbHelper.insertExerciseFull(
                name,
                type,
                description,
                selectedEquipment,
                selectedPrimaryMuscles,
                selectedSecondaryMuscles
        );

        if (id != -1) {
            Toast.makeText(this, "Exercise saved!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to previous screen
        } else {
            Toast.makeText(this, "Failed to save exercise", Toast.LENGTH_SHORT).show();
        }
    }


}
