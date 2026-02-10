package com.beyondbinary.eventapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {
    @Insert
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events WHERE id = :eventId")
    Event getEventById(int eventId);

    @Query("SELECT * FROM events WHERE eventType = :type")
    List<Event> getEventsByType(String type);
}
