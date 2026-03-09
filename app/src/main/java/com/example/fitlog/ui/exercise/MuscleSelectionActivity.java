package com.example.fitlog.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlog.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MuscleSelectionActivity extends AppCompatActivity {

    private List<String> muscleList = Arrays.asList("Chest", "Back", "Shoulders", "Biceps", "Triceps", "Legs", "Abs");
    private ArrayList<String> selectedItems = new ArrayList<>();
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_list);

        container = findViewById(R.id.selectionContainer);
        Button btnConfirm = findViewById(R.id.btnConfirmSelection);

        for (String muscle : muscleList) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(muscle);
            container.addView(checkBox);
        }

        btnConfirm.setOnClickListener(v -> {
            selectedItems.clear();
            for (int i = 0; i < container.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) container.getChildAt(i);
                if (checkBox.isChecked()) selectedItems.add(checkBox.getText().toString());
            }

            Intent result = new Intent();
            result.putStringArrayListExtra("selected", selectedItems);
            setResult(RESULT_OK, result);
            finish();
        });
    }
}
