package com.beyondbinary.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.database.DatabaseSeeder;

import java.util.concurrent.Executors;

public class BeyondBinaryApplication extends Application {

    private static final String TAG = "BeyondBinaryApp";
    private static final String PREFS_NAME = "beyondbinary_prefs";
    private static final String KEY_DB_SEEDED = "db_seeded";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isSeeded = prefs.getBoolean(KEY_DB_SEEDED, false);

        if (!isSeeded) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Log.d(TAG, "Seeding database on first launch...");
                AppDatabaseHelper db = AppDatabaseHelper.getInstance(this);
                DatabaseSeeder.seed(db);
                prefs.edit().putBoolean(KEY_DB_SEEDED, true).apply();
                Log.d(TAG, "Database seeding complete.");
            });
        } else {
            Log.d(TAG, "Database already seeded.");
        }
    }
}
