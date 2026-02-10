# Beyond Binary - Event Map Android App

An Android application that helps isolated people connect by displaying community events on an interactive map with emoji markers.

## Features

- 📍 **Interactive Google Maps** integration showing event locations
- 🔍 **Location Search** - Search for any city/location and find nearby events within 50km radius
- 🎯 **Custom Emoji Markers** representing specific event types
- 💾 **Room Database** for local event storage
- 🗺️ **Address Geocoding** to convert addresses to map coordinates
- 📋 **Event Info Windows** showing detailed event information on marker click
- 🎛️ **Event Filtering** - Filter events by searched location or show all
- **70+ Specific Event Types** including Soccer ⚽, Basketball 🏀, Ping Pong 🏓, Hiking 🥾, Coffee ☕, and many more!

## Project Structure

```
frontend/
├── app/
│   ├── build.gradle                    # App dependencies
│   └── src/main/
│       ├── AndroidManifest.xml         # App configuration
│       ├── java/com/beyondbinary/eventapp/
│       │   ├── Event.java              # Event entity (Room database)
│       │   ├── EventDao.java           # Database access object
│       │   ├── EventDatabase.java      # Room database setup
│       │   ├── MapsActivity.java       # Main activity with map functionality
│       │   └── SampleDataHelper.java   # Helper to populate sample events
│       └── res/
│           └── layout/
│               ├── activity_maps.xml          # Main map layout
│               └── custom_info_window.xml     # Event popup layout
└── build.gradle                        # Project-level build config
```

## Setup Instructions

### 1. Get Google Maps API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable **Maps SDK for Android**
4. Go to **Credentials** → **Create Credentials** → **API Key**
5. Restrict the API key to Android apps (optional but recommended)

### 2. Add API Key to Project

Open [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml#L17) and replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual API key:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_ACTUAL_API_KEY" />
```

### 3. Build and Run

1. Open the project in Android Studio
2. Connect an Android device or start an emulator
3. Click **Run** or press `Shift + F10`

## Core Functionality

### Main Function: `loadEventsAndDisplayOnMap()`

Located in [`MapsActivity.java`](app/src/main/java/com/beyondbinary/eventapp/MapsActivity.java#L88), this is the primary function that:

1. **Fetches events** from the Room database
2. **Geocodes addresses** to get latitude/longitude coordinates
3. **Creates emoji markers** based on event type
4. **Displays markers** on the Google Map
5. **Shows event details** in a popup when marker is clicked

### Location Search Feature

The app includes a powerful location search feature at the top of the map:

#### How to Use:
1. **Enter a location** in the search bar (e.g., "Los Angeles, CA", "New York", "Tokyo")
2. **Click the search button** (🔍)
3. The map will:
   - Move to the searched location
   - Show only events within **50 km radius**
   - Display a count of nearby events
4. **Click "Show All Events"** button to reset and see all events again

#### Search Examples:
- `Los Angeles, CA` - Find events in Los Angeles
- `New York City` - Find events in NYC
- `Chicago, Illinois` - Find events in Chicago
- `London, UK` - Find events in London
- `123 Main St, San Francisco, CA` - Search by specific address

#### Key Functions:
- **`searchLocation()`** - Geocodes user input and filters events
- **`filterEventsByLocation()`** - Shows only events within 50km radius
- **`showAllEvents()`** - Resets filter and displays all events
- **`calculateDistance()`** - Calculates distance between two points

### Key Methods

#### `loadEventsAndDisplayOnMap()`
- Main entry point that fetches all events from database
- Runs on background thread to avoid blocking UI

#### `processAndAddEventMarker(Event event)`
- Checks if event has coordinates, if not, geocodes the address
- Adds marker to map once coordinates are available

#### `geocodeEventLocation(Event event)`
- Converts address strings to latitude/longitude using Android Geocoder
- Caches coordinates in database for future use

#### `addEmojiMarkerToMap(Event event)`
- Creates custom bitmap marker with emoji icon
- Adds marker to map at event location
- Associates event data with marker for info window

#### `createEmojiMarkerIcon(String emoji)`
- Generates a custom map marker with emoji
- Creates circular background with border
- Returns bitmap descriptor for Google Maps

#### `CustomInfoWindowAdapter`
- Custom adapter that displays event details when marker is clicked
- Shows: Title, Location, Time, Participants, Description

## Event Data Model

The `Event` class includes:
- `title` - Event name
- `location` - Address string
- `description` - Event details
- `time` - When the event occurs
- `currentParticipants` - Number of people joined
- `maxParticipants` - Maximum capacity
- `eventType` - Category (Sports, Social, Reading, Dining, Arts, Outdoor)
- `latitude/longitude` - Cached coordinates

## Testing

The app includes [`SampleDataHelper.java`](app/src/main/java/com/beyondbinary/eventapp/SampleDataHelper.java) which automatically populates the database with 13 diverse sample events on first run:

**Sports:** Basketball 🏀, Soccer ⚽, Ping Pong 🏓, Yoga 🧘
**Social:** Coffee ☕, Board Games 🎲
**Dining:** Sushi 🍣, BBQ 🍖
**Arts:** Painting 🎨, Photography 📷
**Reading:** Book Club 📚
**Outdoor:** Hiking 🥾, Dog Walking 🐕

**Note:** Sample data population runs once on first launch. To reset, clear app data or uninstall/reinstall.

## Adding New Events

To add events programmatically:

```java
Event newEvent = new Event(
    "Tennis Doubles Match",
    "Golden Gate Park Tennis Courts, San Francisco, CA",
    "Looking for players for doubles tennis. Intermediate level.",
    "Saturday, 2:00 PM",
    2,      // current participants
    4,      // max participants
    "Tennis"  // Use any of the 70+ available event types
);

EventDatabase db = EventDatabase.getInstance(context);
new Thread(() -> db.eventDao().insert(newEvent)).start();
```

**Available Event Types:** See the [Available Event Types](#available-event-types) section for the complete list of 70+ event types you can use.

## Available Event Types

The app supports 70+ specific event types with unique emojis:

### Sports & Fitness
⚽ Soccer | 🏀 Basketball | 🎾 Tennis | 🏓 Ping Pong | 🏐 Volleyball | ⚾ Baseball | 🏈 Football | 🏸 Badminton | 🏊 Swimming | 🚴 Cycling | 🏃 Running | 🧘 Yoga | 🏋️ Gym | 🥊 Boxing | 🥋 Martial Arts | 🛹 Skateboarding | 🏄 Surfing | ⛳ Golf

### Outdoor Activities
🥾 Hiking | ⛺ Camping | 🧗 Rock Climbing | 🎣 Fishing | ⛷️ Skiing | 🏂 Snowboarding | 🏖️ Beach | 🧺 Picnic | 🦜 Bird Watching

### Social & Entertainment
🎉 Party | ☕ Coffee | 🎬 Movie | 🎵 Concert | 🎤 Karaoke | 💃 Dancing | 🎲 Board Games | 🎮 Video Games | 🧠 Trivia Night | 👥 Meetup

### Food & Dining
🍽️ Dinner | 🍱 Lunch | 🍳 Breakfast | 🍖 BBQ | 🍕 Pizza | 🍣 Sushi | 🍰 Dessert | 🍷 Wine Tasting | 🍺 Beer Tasting | 👨‍🍳 Cooking Class

### Arts & Culture
🎨 Painting | 📷 Photography | 🏛️ Museum | 🎭 Theater | 🎵 Music | ✂️ Crafts | 🏺 Pottery | ✏️ Drawing | 💃 Dance Class

### Learning & Reading
📚 Book Club | 📖 Study Group | 🗣️ Language Exchange | 🔧 Workshop | 🎓 Lecture | ✍️ Writing

### Nature & Animals
🐕 Dog Walking | 🐾 Pet Meetup | 🌱 Gardening | 🌳 Park Visit

### Tech & Professional
💻 Coding | 🤝 Networking | 🚀 Startup

## Customizing Event Types and Emojis

To add or modify event types, edit the `eventTypeEmojis` map in [`MapsActivity.java`](app/src/main/java/com/beyondbinary/eventapp/MapsActivity.java#L47):

```java
private final Map<String, String> eventTypeEmojis = new HashMap<String, String>() {{
    // Add your custom types here
    put("Music Festival", "🎶");
    put("Esports", "🎮");
    put("Meditation", "🧘‍♀️");
}};
```

## Permissions

The app requires:
- `INTERNET` - For loading map tiles
- `ACCESS_FINE_LOCATION` - For geocoding and optional user location
- `ACCESS_COARSE_LOCATION` - Fallback for geocoding

## Dependencies

- Google Play Services Maps: 18.2.0
- Google Play Services Location: 21.0.1
- Room Database: 2.6.1
- AndroidX AppCompat: 1.6.1
- Material Components: 1.11.0

## Troubleshooting

### Map doesn't load
- Verify your Google Maps API key is correct
- Ensure Maps SDK for Android is enabled in Google Cloud Console
- Check internet connection

### Markers don't appear
- Check if events have valid addresses
- Verify geocoding is working (check Logcat for errors)
- Ensure sample data is populated

### Geocoding fails
- Requires internet connection
- Some addresses may not be recognized - try more specific addresses
- Check Geocoder availability: `Geocoder.isPresent()`

## License

This project is part of the Beyond Binary initiative to help isolated individuals connect through community events.
