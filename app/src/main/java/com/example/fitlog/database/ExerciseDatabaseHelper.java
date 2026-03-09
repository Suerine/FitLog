package com.example.fitlog.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fitlog.models.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDatabaseHelper extends SQLiteOpenHelper {

    public ExerciseDatabaseHelper(Context context) {
        super(context, "FitLogDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS exercises (id INTEGER PRIMARY KEY, name TEXT, equipment TEXT)");
        // Optional: Insert seed data
        db.execSQL("INSERT INTO exercises (name, equipment) VALUES ('Ab Rollout', 'Barbell'), ('Air Bike', 'Body'), ('Arnold Press', 'Dumbbell')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public List<Exercise> getAllExercises() {
        List<Exercise> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, equipment FROM exercises", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String equipment = cursor.getString(1);
                list.add(new Exercise(-1, name, equipment)); // Use -1 for unknown ID
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }


}
