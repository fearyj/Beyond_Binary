package com.beyondbinary.app.api;

import com.beyondbinary.app.Event;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Event endpoints
    @GET("events")
    Call<EventsResponse> getAllEvents();

    @GET("events")
    Call<EventsResponse> getEventsByType(@Query("eventType") String eventType);

    @GET("events/{id}")
    Call<EventResponse> getEventById(@Path("id") int id);

    @GET("events/nearby")
    Call<EventsResponse> getNearbyEvents(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("radius") double radiusKm
    );

    @POST("events")
    Call<CreateEventResponse> createEvent(@Body Event event);

    @PUT("events/{id}")
    Call<UpdateEventResponse> updateEvent(@Path("id") int id, @Body Event event);

    @DELETE("events/{id}")
    Call<DeleteEventResponse> deleteEvent(@Path("id") int id);

    @GET("stats")
    Call<StatsResponse> getStatistics();

    // User endpoints
    @POST("users")
    Call<CreateUserResponse> createUser(@Body Map<String, String> body);

    @POST("users/login")
    Call<CreateUserResponse> loginUser(@Body Map<String, String> body);

    @GET("users/{id}")
    Call<UserResponse> getUser(@Path("id") int id);

    @PUT("users/{id}")
    Call<UserResponse> updateUser(@Path("id") int id, @Body Map<String, String> body);

    // Interaction endpoints
    @POST("interactions")
    Call<CreateInteractionResponse> createInteraction(@Body Map<String, Object> body);

    @GET("interactions/{userId}")
    Call<InteractionsResponse> getUserInteractions(@Path("userId") int userId);
}
