package com.example.fitlog.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fitlog.models.Exercise;
import com.example.fitlog.models.Workout;
import com.example.fitlog.models.WorkoutLog;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "FitLog.db";
    private static final int DB_VERSION = 3; // Make sure this is bumped
    private Context context;

    public WorkoutDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS workouts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, notes TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS exercise_muscle (" +
                "exercise_id INTEGER, " +
                "muscle_id INTEGER, " +
                "FOREIGN KEY(exercise_id) REFERENCES exercises(id), " +
                "FOREIGN KEY(muscle_id) REFERENCES muscles(id))");


        db.execSQL("CREATE TABLE IF NOT EXISTS workout_exercises (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "workout_id INTEGER, " +
                "exercise_id INTEGER, " +
                "FOREIGN KEY(workout_id) REFERENCES workouts(id), " +
                "FOREIGN KEY(exercise_id) REFERENCES exercises(id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS exercises (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "type TEXT, " +
                "description TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS exercise_equipment (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "exercise_id INTEGER, " +
                "equipment TEXT, " +
                "FOREIGN KEY(exercise_id) REFERENCES exercises(id))");

        // ✅ RESTORE THESE TABLES
        db.execSQL("CREATE TABLE IF NOT EXISTS exercise_primary_muscles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "exercise_id INTEGER, " +
                "muscle TEXT, " +
                "FOREIGN KEY(exercise_id) REFERENCES exercises(id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS exercise_secondary_muscles (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "exercise_id INTEGER, " +
                "muscle TEXT, " +
                "FOREIGN KEY(exercise_id) REFERENCES exercises(id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS workout_summary (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "workout_id INTEGER, " +
                "completed_exercises INTEGER, " +
                "completed_sets INTEGER, " +
                "total_reps INTEGER, " +
                "total_weight REAL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // Finished workout logs table
        db.execSQL("CREATE TABLE IF NOT EXISTS workout_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "workout_id INTEGER, " +
                "workout_name TEXT, " +
                "notes TEXT, " +
                "timestamp INTEGER, " +
                "completed_exercises INTEGER, " +
                "completed_sets INTEGER, " +
                "total_reps INTEGER, " +
                "total_weight REAL, " +
                "duration_minutes INTEGER)");

        // Log–Exercise linking table
        db.execSQL("CREATE TABLE IF NOT EXISTS workout_log_exercises (" +
                "log_id INTEGER, " +
                "exercise_id INTEGER)");


    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS workout_logs");
        db.execSQL("DROP TABLE IF EXISTS workout_log_exercises");
        onCreate(db); // recreate all tables
    }


    // Insert a Workout
    public void insertWorkout(Workout workout) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", workout.getName());
        values.put("notes", workout.getNotes());
        db.insert("workouts", null, values);
        db.close();
    }

    // Insert a new Exercise with equipment and muscles
    public long insertExerciseFull(String name, String type, String description,
                                   List<String> equipmentList,
                                   List<String> primaryMuscles,
                                   List<String> secondaryMuscles) {
        long exerciseId = insertExercise(name, type, description);
        if (exerciseId != -1) {
            insertExerciseEquipment(exerciseId, equipmentList);
            insertExerciseMuscles(exerciseId, primaryMuscles, secondaryMuscles);
        }
        return exerciseId;
    }

    public long insertExercise(String name, String type, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("type", type);
        values.put("description", description);
        long id = db.insert("exercises", null, values);
        db.close();
        return id;
    }

    public void insertExerciseEquipment(long exerciseId, List<String> equipmentList) {
        SQLiteDatabase db = getWritableDatabase();
        for (String equipment : equipmentList) {
            ContentValues values = new ContentValues();
            values.put("exercise_id", exerciseId);
            values.put("equipment", equipment);
            db.insert("exercise_equipment", null, values);
        }
        db.close();
    }

    public void insertExerciseMuscles(long exerciseId, List<String> primary, List<String> secondary) {
        SQLiteDatabase db = getWritableDatabase();

        for (String muscle : primary) {
            ContentValues values = new ContentValues();
            values.put("exercise_id", exerciseId);
            values.put("muscle", muscle);
            db.insert("exercise_primary_muscles", null, values);
        }

        for (String muscle : secondary) {
            ContentValues values = new ContentValues();
            values.put("exercise_id", exerciseId);
            values.put("muscle", muscle);
            db.insert("exercise_secondary_muscles", null, values);
        }

        db.close();
    }

    // Get all workouts
    public List<Workout> getAllWorkouts() {
        List<Workout> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM workouts ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Workout workout = new Workout(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
                list.add(workout);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name FROM exercises ORDER BY id DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            List<String> equipment = getExerciseEquipment(id);
            String equipmentLabel = equipment.isEmpty() ? "No equipment" : String.join(", ", equipment);
            exercises.add(new Exercise(id, name, equipmentLabel));
        }

        cursor.close();
        db.close();
        return exercises;
    }




    public Workout getWorkoutById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("workouts", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            Workout workout = new Workout(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("notes"))
            );
            cursor.close();
            return workout;
        }
        return null;
    }

    public List<Exercise> getExercisesForWorkout(int workoutId) {
        List<Exercise> exerciseList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Join to get exercise + first equipment (if any)
        String query = "SELECT e.id, e.name, " +
                "(SELECT equipment FROM exercise_equipment WHERE exercise_id = e.id LIMIT 1) AS equipment " +
                "FROM workout_exercises we " +
                "JOIN exercises e ON we.exercise_id = e.id " +
                "WHERE we.workout_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(workoutId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String equipment = cursor.getString(cursor.getColumnIndexOrThrow("equipment"));
                exerciseList.add(new Exercise(id, name, equipment != null ? equipment : "No Equipment"));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return exerciseList;
    }


    public List<String> getExerciseEquipment(int exerciseId) {
        return getListFromColumn("exercise_equipment", "equipment", "exercise_id", exerciseId);
    }

    public List<String> getPrimaryMuscles(int exerciseId) {
        return getListFromColumn("exercise_primary_muscles", "muscle", "exercise_id", exerciseId);
    }

    public List<String> getSecondaryMuscles(int exerciseId) {
        return getListFromColumn("exercise_secondary_muscles", "muscle", "exercise_id", exerciseId);
    }

    private List<String> getListFromColumn(String table, String column, String whereColumn, int id) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(table, new String[]{column}, whereColumn + "=?", new String[]{String.valueOf(id)}, null, null, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndexOrThrow(column)));
        }
        cursor.close();
        db.close();
        return list;
    }

    public void insertWorkoutExercise(int workoutId, int exerciseId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("workout_id", workoutId);
        values.put("exercise_id", exerciseId);
        db.insert("workout_exercises", null, values);
    }

    public Exercise getExerciseById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, name, type FROM exercises WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Exercise exercise = new Exercise(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("type"))
            );
            cursor.close();
            return exercise;
        }
        return null;
    }

    public long insertWorkoutAndGetId(Workout workout) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", workout.getName());
        values.put("notes", workout.getNotes());
        long id = db.insert("workouts", null, values);
        db.close();
        return id;
    }

    public void linkExerciseToWorkout(int workoutId, int exerciseId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("workout_id", workoutId);
        values.put("exercise_id", exerciseId);
        db.insert("workout_exercises", null, values);
        db.close();
    }

    public void insertWorkoutSummary(int workoutId, int exercises, int sets, int reps, double weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("workout_id", workoutId);
        values.put("completed_exercises", exercises);
        values.put("completed_sets", sets);
        values.put("total_reps", reps);
        values.put("total_weight", weight);
        db.insert("workout_summary", null, values);
        db.close();
    }

    public void removeExerciseFromWorkout(int workoutId, int exerciseId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("workout_exercises", "workout_id=? AND exercise_id=?", new String[]{
                String.valueOf(workoutId), String.valueOf(exerciseId)
        });
        db.close();
    }


    public void saveWorkoutSummary(int workoutId, int exercises, int sets, int reps, double totalWeight, int durationMinutes) {
        Workout workout = getWorkoutById(workoutId);
        if (workout == null) return;

        SQLiteDatabase db = getWritableDatabase();
        Log.d("DB", "Saving summary with workoutId=" + workoutId + ", name=" + workout.getName());

        // Insert workout log
        ContentValues values = new ContentValues();
        values.put("workout_id", workoutId);
        values.put("workout_name", workout.getName());
        values.put("notes", workout.getNotes());
        values.put("timestamp", System.currentTimeMillis());
        values.put("completed_exercises", exercises);
        values.put("completed_sets", sets);
        values.put("total_reps", reps);
        values.put("total_weight", totalWeight);
        values.put("duration_minutes", durationMinutes);

        long logId = db.insertWithOnConflict("workout_logs", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        Log.d("DB", "Workout summary insert result: " + logId);

        if (logId == -1) {
            Log.w("DB", "Log already exists for this workout and timestamp. Skipping insert.");
            db.close();
            return;
        }

        // 🔒 Get exercises before loop to avoid accidental db closure
        List<Exercise> exerciseList;
        try (WorkoutDatabaseHelper tempHelper = new WorkoutDatabaseHelper(context)) {
            exerciseList = tempHelper.getExercisesForWorkout(workoutId);
        }

        // Save exercises linked to log
        for (Exercise ex : exerciseList) {
            ContentValues link = new ContentValues();
            link.put("log_id", logId);
            link.put("exercise_id", ex.getId());
            db.insertWithOnConflict("workout_log_exercises", null, link, SQLiteDatabase.CONFLICT_IGNORE);
        }

        Log.d("DB", "Inserted " + exerciseList.size() + " exercises for logId=" + logId);
        db.close();
    }


    public void addExerciseToWorkout(int workoutId, int exerciseId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("workout_id", workoutId);
        values.put("exercise_id", exerciseId);
        db.insert("workout_exercises", null, values);
        db.close();
    }



    public List<WorkoutLog> getAllWorkoutLogs() {
        List<WorkoutLog> logs = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM workout_logs ORDER BY timestamp DESC", null);

        if (cursor.moveToFirst()) {
            do {
                WorkoutLog log = new WorkoutLog(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("workout_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("completed_exercises")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("completed_sets")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("total_reps")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("total_weight")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("duration_minutes")) // ✅ added
                );
                logs.add(log);
            } while (cursor.moveToNext());
        }

        cursor.close();
        //db.close();
        Log.d("DB", "Returning " + logs.size() + " workout logs.");
        return logs;
    }


    public WorkoutLog getWorkoutLogById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("workout_logs", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            WorkoutLog log = new WorkoutLog(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("workout_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("completed_exercises")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("completed_sets")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("total_reps")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("total_weight")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("duration_minutes")) // ✅ added
            );
            cursor.close();
            return log;
        }

        cursor.close();
        return null;
    }


    public List<String> getWorkoutLogExercises(int logId) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT e.name, ee.equipment " +
                "FROM workout_log_exercises we " +
                "JOIN exercises e ON we.exercise_id = e.id " +
                "LEFT JOIN exercise_equipment ee ON e.id = ee.exercise_id " +
                "WHERE we.log_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(logId)});
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String equipment = cursor.getString(1);
            list.add(name + " - " + (equipment != null ? equipment : "N/A"));
        }
        cursor.close();
        return list;
    }

    public List<String> getWorkoutLogExercisesFormatted(int logId) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT e.name, ee.equipment " +
                "FROM workout_log_exercises we " +
                "JOIN exercises e ON we.exercise_id = e.id " +
                "LEFT JOIN exercise_equipment ee ON e.id = ee.exercise_id " +
                "WHERE we.log_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(logId)});
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String equipment = cursor.getString(1);
            String displayText = name + (equipment != null ? " - " + equipment : "");
            list.add(displayText);
        }

        cursor.close();
        //db.close();
        return list;
    }

    public String getMusclesForWorkoutLog(int logId) {
        SQLiteDatabase db = getReadableDatabase();
        String muscles = "";

        try {
            String query = "SELECT m.name FROM workout_log_exercises wle " +
                    "JOIN exercise_muscle em ON wle.exercise_id = em.exercise_id " +
                    "JOIN muscles m ON em.muscle_id = m.id WHERE wle.log_id = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(logId)});
            List<String> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
            cursor.close();
            muscles = android.text.TextUtils.join(", ", list);
        } catch (Exception e) {
            Log.e("DB", "Failed to load muscles for log: " + e.getMessage());
            muscles = "Unknown";
        }

        return muscles;
    }




}
