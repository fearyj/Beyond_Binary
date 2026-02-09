package com.beyondbinary.app.data.database;

import android.util.Log;

import com.beyondbinary.app.data.models.Event;
import com.beyondbinary.app.data.models.User;

public class DatabaseSeeder {

    private static final String TAG = "DatabaseSeeder";

    // Public sample video URLs from Google's test media
    private static final String VIDEO_1 = "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4";
    private static final String VIDEO_2 = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4";
    private static final String VIDEO_3 = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3";
    private static final String VIDEO_4 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
    private static final String VIDEO_5 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4";
    private static final String VIDEO_6 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4";
    private static final String VIDEO_7 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4";
    private static final String VIDEO_8 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4";
    private static final String VIDEO_9 = "https://storage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4";
    private static final String VIDEO_10 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";

    public static void seed(AppDatabaseHelper db) {
        if (db.getEventCount() > 0) {
            Log.d(TAG, "Database already seeded, skipping.");
            return;
        }

        Log.d(TAG, "Seeding database with 20 events...");

        // Walking events (5)
        db.insertEvent(new Event(0, "Morning Walk in the Park", "Walking",
                "A gentle morning walk through the botanical gardens. Perfect for clearing your mind and getting some fresh air.",
                VIDEO_1, "Botanical Gardens, Central Park"));
        db.insertEvent(new Event(0, "Sunset Beach Walk", "Walking",
                "Enjoy a calming walk along the shoreline as the sun sets. Great for reflection and relaxation.",
                VIDEO_4, "Brighton Beach"));
        db.insertEvent(new Event(0, "Forest Trail Hike", "Walking",
                "Explore a peaceful forest trail surrounded by nature. Ideal for de-stressing after a long week.",
                VIDEO_5, "Redwood National Forest"));
        db.insertEvent(new Event(0, "City Walking Tour", "Walking",
                "Discover hidden gems in the city on this guided walking tour through historic streets.",
                VIDEO_6, "Downtown Heritage District"));
        db.insertEvent(new Event(0, "Mindful Walking Session", "Walking",
                "Practice mindful walking techniques in a quiet garden setting. Focus on each step and breathe.",
                VIDEO_7, "Zen Garden, Westside"));

        // Outdoor events (3)
        db.insertEvent(new Event(0, "Outdoor Yoga in the Park", "Outdoor",
                "Join us for an invigorating outdoor yoga session surrounded by nature and fresh air.",
                VIDEO_8, "Riverside Park"));
        db.insertEvent(new Event(0, "Nature Photography Walk", "Outdoor",
                "Capture the beauty of nature with fellow photography enthusiasts. All skill levels welcome.",
                VIDEO_9, "Wildflower Reserve"));
        db.insertEvent(new Event(0, "Outdoor Bootcamp", "Outdoor",
                "A high-energy outdoor fitness session combining cardio and strength training.",
                VIDEO_10, "Sports Field, North Campus"));

        // Yoga events (4)
        db.insertEvent(new Event(0, "Sunrise Yoga Flow", "Yoga",
                "Start your day with an energizing yoga flow as the sun rises. Suitable for all levels.",
                VIDEO_1, "Hilltop Studio"));
        db.insertEvent(new Event(0, "Gentle Restorative Yoga", "Yoga",
                "A slow and gentle class focused on deep stretching and relaxation. Perfect for recovery.",
                VIDEO_4, "Wellness Center, Oak Street"));
        db.insertEvent(new Event(0, "Power Vinyasa Yoga", "Yoga",
                "A dynamic vinyasa session to build strength and flexibility. Intermediate level.",
                VIDEO_5, "Downtown Yoga Loft"));
        db.insertEvent(new Event(0, "Yoga for Stress Relief", "Yoga",
                "Targeted yoga poses and breathing exercises designed to reduce stress and anxiety.",
                VIDEO_6, "Community Hall, Elm Avenue"));

        // Meditation events (4)
        db.insertEvent(new Event(0, "Guided Morning Meditation", "Meditation",
                "A 30-minute guided meditation to set positive intentions for the day ahead.",
                VIDEO_7, "Mindfulness Center"));
        db.insertEvent(new Event(0, "Sound Bath Meditation", "Meditation",
                "Immerse yourself in healing sounds of crystal bowls and gongs for deep relaxation.",
                VIDEO_8, "Harmony Studio"));
        db.insertEvent(new Event(0, "Breathing & Meditation Workshop", "Meditation",
                "Learn powerful breathing techniques combined with meditation practice.",
                VIDEO_9, "Wellness Hub, Park Lane"));
        db.insertEvent(new Event(0, "Evening Wind-Down Meditation", "Meditation",
                "Release the tension of the day with this calming evening meditation session.",
                VIDEO_10, "Serenity Room, Lakeside"));

        // Social/Community events (4)
        db.insertEvent(new Event(0, "Community Garden Meetup", "Social",
                "Connect with your neighbors while tending to the community garden. All welcome!",
                VIDEO_1, "Community Garden, 5th Street"));
        db.insertEvent(new Event(0, "Wellness Book Club", "Community",
                "Discuss this month's wellness book pick with like-minded readers over tea.",
                VIDEO_4, "Local Library, Main Branch"));
        db.insertEvent(new Event(0, "Volunteer Beach Cleanup", "Community",
                "Give back to the environment and meet new friends during this beach cleanup event.",
                VIDEO_5, "Sandy Shores Beach"));
        db.insertEvent(new Event(0, "Mental Health Awareness Walk", "Social",
                "Walk together to raise awareness for mental health. A supportive community event.",
                VIDEO_6, "City Hall Plaza"));

        // Default user
        db.insertUser(new User(1, "Loves nature and mindfulness", "Walking,Yoga,Outdoor"));

        Log.d(TAG, "Database seeded with " + db.getEventCount() + " events.");
    }
}
