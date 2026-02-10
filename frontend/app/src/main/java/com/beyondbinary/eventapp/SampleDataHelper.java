package com.beyondbinary.eventapp;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper class to populate the database with sample events for testing
 */
public class SampleDataHelper {

    public static void populateSampleEvents(Context context) {
        EventDatabase database = EventDatabase.getInstance(context);
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            // Check if database is already populated
            if (database.eventDao().getAllEvents().isEmpty()) {

                // Sports Events
                Event basketballEvent = new Event(
                        "Basketball Pickup Game",
                        "East Coast Park, Singapore",
                        "Casual basketball game at the outdoor courts. All skill levels welcome!",
                        "Saturday, 5:00 PM",
                        6,
                        12,
                        "Basketball"
                );
                database.eventDao().insert(basketballEvent);

                Event soccerEvent = new Event(
                        "Weekend Soccer Match",
                        "Marina Bay Sands, Singapore",
                        "Friendly soccer match at Marina Bay. Looking for more players!",
                        "Saturday, 4:00 PM",
                        14,
                        22,
                        "Soccer"
                );
                database.eventDao().insert(soccerEvent);

                Event pingPongEvent = new Event(
                        "Ping Pong Tournament",
                        "Toa Payoh HDB Hub, Singapore",
                        "Casual ping pong tournament with prizes. All levels welcome!",
                        "Friday, 7:00 PM",
                        10,
                        16,
                        "Ping Pong"
                );
                database.eventDao().insert(pingPongEvent);

                Event yogaEvent = new Event(
                        "Sunrise Yoga Session",
                        "Gardens by the Bay, Singapore",
                        "Morning yoga in the beautiful gardens. Bring your own mat!",
                        "Sunday, 6:30 AM",
                        15,
                        25,
                        "Yoga"
                );
                database.eventDao().insert(yogaEvent);

                Event runningEvent = new Event(
                        "Morning Run Group",
                        "MacRitchie Reservoir, Singapore",
                        "Join us for a refreshing morning run around the reservoir!",
                        "Sunday, 7:00 AM",
                        8,
                        20,
                        "Running"
                );
                database.eventDao().insert(runningEvent);

                // Social Events
                Event coffeeEvent = new Event(
                        "Coffee & Chat Meetup",
                        "Tiong Bahru, Singapore",
                        "Meet new people at trendy Tiong Bahru cafes. Great conversations guaranteed!",
                        "Sunday, 10:00 AM",
                        10,
                        18,
                        "Coffee"
                );
                database.eventDao().insert(coffeeEvent);

                Event boardGamesEvent = new Event(
                        "Board Game Night",
                        "Orchard Road, Singapore",
                        "Bring your favorite board games or try new ones. Beginners welcome!",
                        "Thursday, 7:00 PM",
                        12,
                        20,
                        "Board Games"
                );
                database.eventDao().insert(boardGamesEvent);

                Event movieEvent = new Event(
                        "Movie Night at Rooftop",
                        "Clarke Quay, Singapore",
                        "Outdoor movie screening by the riverside. Bring cushions!",
                        "Friday, 8:00 PM",
                        20,
                        40,
                        "Movie"
                );
                database.eventDao().insert(movieEvent);

                // Reading/Learning Events
                Event bookClubEvent = new Event(
                        "Book Club Discussion",
                        "National Library, Singapore",
                        "Monthly book club discussing local and international literature. All welcome!",
                        "Friday, 7:00 PM",
                        10,
                        18,
                        "Book Club"
                );
                database.eventDao().insert(bookClubEvent);

                Event languageEvent = new Event(
                        "Mandarin Language Exchange",
                        "Chinatown, Singapore",
                        "Practice Mandarin with native speakers. All levels welcome!",
                        "Wednesday, 6:30 PM",
                        8,
                        15,
                        "Language Exchange"
                );
                database.eventDao().insert(languageEvent);

                // Dining Events
                Event hawkerEvent = new Event(
                        "Hawker Centre Food Tour",
                        "Maxwell Food Centre, Singapore",
                        "Explore the best local hawker food together! Try famous chicken rice.",
                        "Saturday, 12:00 PM",
                        8,
                        15,
                        "Lunch"
                );
                database.eventDao().insert(hawkerEvent);

                Event bbqEvent = new Event(
                        "BBQ & Beach Gathering",
                        "Sentosa Beach, Singapore",
                        "Beach BBQ at Sentosa! Bring food to share and enjoy the sunset.",
                        "Sunday, 4:00 PM",
                        20,
                        35,
                        "BBQ"
                );
                database.eventDao().insert(bbqEvent);

                Event dinnerEvent = new Event(
                        "Dinner at Marina Bay",
                        "Marina Bay Waterfront, Singapore",
                        "Group dinner with stunning views of Marina Bay Sands!",
                        "Saturday, 7:00 PM",
                        10,
                        16,
                        "Dinner"
                );
                database.eventDao().insert(dinnerEvent);

                // Arts Events
                Event paintingEvent = new Event(
                        "Watercolor Painting Workshop",
                        "National Gallery Singapore",
                        "Beginner-friendly watercolor class. All materials provided!",
                        "Wednesday, 6:00 PM",
                        12,
                        18,
                        "Painting"
                );
                database.eventDao().insert(paintingEvent);

                Event photographyEvent = new Event(
                        "Photography Walk",
                        "Merlion Park, Singapore",
                        "Golden hour photography session at iconic Singapore landmarks!",
                        "Saturday, 6:00 PM",
                        10,
                        15,
                        "Photography"
                );
                database.eventDao().insert(photographyEvent);

                Event museumEvent = new Event(
                        "Museum Tour & Discussion",
                        "ArtScience Museum, Singapore",
                        "Guided tour of latest exhibits followed by group discussion.",
                        "Sunday, 2:00 PM",
                        8,
                        12,
                        "Museum"
                );
                database.eventDao().insert(museumEvent);

                // Outdoor Events
                Event hikingEvent = new Event(
                        "Hiking at Bukit Timah",
                        "Bukit Timah Nature Reserve, Singapore",
                        "Nature hike through Singapore's rainforest. Great workout with city views!",
                        "Sunday, 8:00 AM",
                        10,
                        20,
                        "Hiking"
                );
                database.eventDao().insert(hikingEvent);

                Event cyclingEvent = new Event(
                        "Cycling at East Coast",
                        "East Coast Park, Singapore",
                        "Leisure cycling along the beach. Rent bikes available nearby!",
                        "Saturday, 7:00 AM",
                        8,
                        15,
                        "Cycling"
                );
                database.eventDao().insert(cyclingEvent);

                Event beachEvent = new Event(
                        "Beach Volleyball & Chill",
                        "Palawan Beach, Sentosa, Singapore",
                        "Beach volleyball games and relaxing by the sea!",
                        "Sunday, 3:00 PM",
                        12,
                        20,
                        "Beach"
                );
                database.eventDao().insert(beachEvent);
            }
        });
    }
}
