package com.beyondbinary.app.api;

import com.beyondbinary.app.Event;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    // User events endpoint
    @GET("users/{userId}/events")
    Call<UserEventsResponse> getUserEvents(@Path("userId") int userId);

    // Event photo endpoints
    @Multipart
    @POST("events/{id}/photos")
    Call<UploadPhotoResponse> uploadEventPhoto(
            @Path("id") int eventId,
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part photo
    );

    // Attended galleries endpoint (profile grid)
    @GET("users/{userId}/attended-galleries")
    Call<AttendedGalleriesResponse> getAttendedGalleries(@Path("userId") int userId);

    // Messaging endpoints
    @POST("messages/invite")
    Call<SendInviteResponse> sendEventInvite(@Body Map<String, Object> body);

    @POST("messages")
    Call<SendMessageResponse> sendMessage(@Body Map<String, Object> body);

    @GET("messages/{userId}/{otherUserId}")
    Call<MessagesResponse> getMessages(@Path("userId") int userId, @Path("otherUserId") int otherUserId);

    // Chatbot endpoint
    @POST("chatbot/chat")
    Call<ChatbotResponse> sendChatMessage(@Body ChatbotRequest request);
}
