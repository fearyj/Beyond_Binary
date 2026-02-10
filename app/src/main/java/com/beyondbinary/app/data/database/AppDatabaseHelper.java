package com.beyondbinary.app.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.beyondbinary.app.data.models.Event;
import com.beyondbinary.app.data.models.User;

import java.util.ArrayList;
import java.util.List;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "beyondbinary.db";
    private static final int DATABASE_VERSION = 1;

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
        db.execSQL("CREATE TABLE events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "category TEXT, " +
                "description TEXT, " +
                "media_url TEXT, " +
                "location TEXT)");

        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY, " +
                "bio TEXT, " +
                "interest_tags TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public long insertEvent(Event event) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("category", event.getCategory());
        values.put("description", event.getDescription());
        values.put("media_url", event.getMediaUrl());
        values.put("location", event.getLocation());
        return db.insert("events", null, values);
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM events", null);
        while (cursor.moveToNext()) {
            events.add(cursorToEvent(cursor));
        }
        cursor.close();
        return events;
    }

    public List<Event> getEventsByCategories(List<String> categories) {
        List<Event> events = new ArrayList<>();
        if (categories == null || categories.isEmpty()) {
            return events;
        }
        SQLiteDatabase db = getReadableDatabase();
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        String query = "SELECT * FROM events WHERE category IN (" + placeholders + ")";
        Cursor cursor = db.rawQuery(query, categories.toArray(new String[0]));
        while (cursor.moveToNext()) {
            events.add(cursorToEvent(cursor));
        }
        cursor.close();
        return events;
    }

    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("bio", user.getBio());
        values.put("interest_tags", user.getInterestTags());
        return db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public User getUser() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users LIMIT 1", null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setBio(cursor.getString(cursor.getColumnIndexOrThrow("bio")));
            user.setInterestTags(cursor.getString(cursor.getColumnIndexOrThrow("interest_tags")));
        }
        cursor.close();
        return user;
    }

    public int getEventCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM events", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        event.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        event.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
        event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        event.setMediaUrl(cursor.getString(cursor.getColumnIndexOrThrow("media_url")));
        event.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
        return event;
    }
}
