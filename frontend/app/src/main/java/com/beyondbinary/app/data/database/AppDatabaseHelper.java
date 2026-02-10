package com.beyondbinary.app.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.beyondbinary.app.data.models.User;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "beyondbinary.db";
    private static final int DATABASE_VERSION = 3;

    private static AppDatabaseHelper instance;

    public static synchronized AppDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY, " +
                "email TEXT, " +
                "bio TEXT, " +
                "interest_tags TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("email", user.getEmail());
        values.put("bio", user.getBio());
        values.put("interest_tags", user.getInterestTags());
        return db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public User getUserById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(id)});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setBio(cursor.getString(cursor.getColumnIndexOrThrow("bio")));
            user.setInterestTags(cursor.getString(cursor.getColumnIndexOrThrow("interest_tags")));
        }
        cursor.close();
        return user;
    }
}
