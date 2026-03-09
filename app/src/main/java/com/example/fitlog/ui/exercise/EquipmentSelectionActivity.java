package com.example.fitlog.ui.exercise;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlog.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquipmentSelectionActivity extends AppCompatActivity {

    private LinearLayout container;
    private final List<String> equipmentList = Arrays.asList(
            "Dumbbell", "Barbell", "Machine", "Bodyweight", "Kettlebell", "Cable"
    );
    private final ArrayList<String> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_list);

        container = findViewById(R.id.selectionContainer);
        Button btnConfirm = findViewById(R.id.btnConfirmSelection);

        // Dynamically add checkboxes
        for (String item : equipmentList) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(item);
            container.addView(checkBox);
        }

        btnConfirm.setOnClickListener(v -> {
            selectedItems.clear();

            for (int i = 0; i < container.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) container.getChildAt(i);
                if (checkBox.isChecked()) {
                    selectedItems.add(checkBox.getText().toString());
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Please select at least one equipment", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent result = new Intent();
            result.putStringArrayListExtra("selected", selectedItems);
            setResult(Activity.RESULT_OK, result);
            finish();
        });
    }
}
