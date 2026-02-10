# Testing Guide for Beyond Binary Event Map

## Prerequisites

1. **Android Studio** - Download from [developer.android.com](https://developer.android.com/studio)
2. **Google Maps API Key** - Get from [Google Cloud Console](https://console.cloud.google.com/)
3. **Android Device or Emulator** - Physical device (recommended) or AVD emulator

## Step 1: Get Google Maps API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project (e.g., "Beyond Binary Events")
3. Enable APIs:
   - Click **"APIs & Services"** → **"Library"**
   - Search for **"Maps SDK for Android"** → **Enable**
   - Search for **"Geocoding API"** → **Enable** (for address to coordinates)
4. Create credentials:
   - Click **"APIs & Services"** → **"Credentials"**
   - Click **"Create Credentials"** → **"API Key"**
   - Copy your API key

## Step 2: Configure the Project

1. Open the project in Android Studio:
   ```bash
   cd Beyond_Binary/frontend
   # Open this folder in Android Studio
   ```

2. Add your API key to `app/src/main/AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="AIzaSy..." />  <!-- Replace with your actual key -->
   ```

3. Sync Gradle files (Android Studio will prompt you)

## Step 3: Run the App

### Option A: Using Android Emulator (Easy but slower)

1. In Android Studio, click **Tools** → **Device Manager**
2. Create a new virtual device:
   - Choose **Pixel 5** or similar
   - System Image: **API 34 (Android 14)**
   - Click **Finish**
3. Click the **Run** button (green play icon) or press `Shift + F10`
4. Select your emulator and click **OK**

### Option B: Using Physical Android Device (Recommended)

1. Enable Developer Options on your phone:
   - Go to **Settings** → **About Phone**
   - Tap **Build Number** 7 times
2. Enable USB Debugging:
   - Go to **Settings** → **Developer Options**
   - Enable **USB Debugging**
3. Connect your phone via USB
4. Click the **Run** button in Android Studio
5. Select your device and click **OK**

## Step 4: What You Should See

### Initial App Launch

1. **Map loads** centered on San Francisco (default location)
2. **13 emoji markers** appear on the map automatically:
   - 🏀 Basketball game at Golden Gate Park
   - ⚽ Soccer match at Marina Green
   - 🏓 Ping Pong tournament at SPIN SF
   - 🧘 Yoga session at Dolores Park
   - ☕ Coffee meetup at Ferry Building
   - 🎲 Board games at Game Parlour
   - 📚 Book club at SF Public Library
   - 🍣 Sushi night in Japantown
   - 🍖 BBQ at Crissy Field
   - 🎨 Painting workshop in Mission
   - 📷 Photography at Golden Gate Bridge
   - 🥾 Hiking at Twin Peaks
   - 🐕 Dog park at Fort Funston

### Testing the Core Function

**Test 1: Verify Markers Display**
- ✅ All 13 markers should appear on the map
- ✅ Each marker should have a unique emoji
- ✅ Markers should be in the correct San Francisco locations

**Test 2: Test Info Window (Click Events)**
1. Click/tap any marker on the map
2. A popup window should appear showing:
   - 📋 Event title
   - 📍 Location address
   - 🕐 Time
   - 👥 Participant count
   - 📝 Description
3. Try clicking different markers to see different events

**Test 3: Test Map Interaction**
- ✅ Pinch to zoom in/out
- ✅ Drag to pan around the map
- ✅ Rotate with two fingers
- ✅ Tilt for 3D view

**Test 4: Verify Database Storage**
1. Close the app completely
2. Reopen the app
3. All markers should still be there (data persists in Room database)

## Step 5: Test with Custom Events

Add this code to test programmatically adding new events:

### Option A: Using Android Studio Logcat

1. Open the app
2. In Android Studio, open **Logcat** at the bottom
3. Watch for log messages like:
   ```
   I/MapsActivity: Geocoding failed for location: ...
   ```

### Option B: Add Test Events via Code

Edit `SampleDataHelper.java` and add a new event:

```java
Event testEvent = new Event(
    "Test Event - Ping Pong Practice",
    "Golden Gate Park, San Francisco, CA",
    "Testing the map marker functionality!",
    "Today, 5:00 PM",
    1,
    10,
    "Ping Pong"
);
database.eventDao().insert(testEvent);
```

Then:
1. Uninstall the app from your device
2. Run again to see the new test event

## Troubleshooting

### Issue: Map shows gray screen
**Solution:**
- Verify your API key is correct
- Make sure "Maps SDK for Android" is enabled in Google Cloud Console
- Check internet connection

### Issue: No markers appear
**Solution:**
- Check Logcat for errors
- Verify sample data is being inserted
- Try this debug code in `MapsActivity.java`:
  ```java
  executorService.execute(() -> {
      List<Event> events = eventDatabase.eventDao().getAllEvents();
      Log.d(TAG, "Total events in database: " + events.size());
  });
  ```

### Issue: Geocoding fails
**Solution:**
- Enable "Geocoding API" in Google Cloud Console
- Check that addresses are valid
- Verify internet connection

### Issue: App crashes on launch
**Solution:**
- Check Logcat for stack trace
- Verify all dependencies in `build.gradle` are correct
- Try **Build** → **Clean Project** → **Rebuild Project**

## Testing the Location Search Feature

### Test 5: Search Functionality

**Test Search by City:**
1. Type `Los Angeles, CA` in the search bar
2. Click the search button (🔍)
3. Expected results:
   - ✅ Map moves to Los Angeles
   - ✅ Shows "Showing X events within 50 km" message
   - ✅ "Show All Events" button appears
   - ✅ Only events near LA are visible (sample events will be hidden since they're in SF)

**Test Search with Different Locations:**
Try these searches:
- `New York City` - Should move to NYC
- `Tokyo, Japan` - Should move to Tokyo
- `San Francisco, CA` - Should show all 13 sample events (they're all in SF)
- `Chicago, IL` - Should show no events (no events there)

**Test Show All Events:**
1. After doing a search, click "Show All Events"
2. Expected results:
   - ✅ All 13 markers reappear
   - ✅ Map returns to default San Francisco view
   - ✅ "Show All Events" button disappears
   - ✅ Search bar clears

**Test Invalid Search:**
1. Type `asdfghjkl` or gibberish
2. Click search
3. Expected: "Location not found" message

**Test Empty Search:**
1. Leave search bar empty
2. Click search
3. Expected: "Please enter a location" message

**Test Search Radius:**
1. Add test events at different distances from San Francisco
2. Search for `San Francisco, CA`
3. Verify only events within 50km appear

## Testing Checklist

### Basic Functionality
- [ ] Map loads successfully
- [ ] 13 sample markers appear with correct emojis
- [ ] Clicking markers shows info windows with event details
- [ ] Can zoom, pan, and navigate the map
- [ ] Events persist after app restart
- [ ] Different event types show different emojis
- [ ] Info windows display all event information correctly

### Location Search
- [ ] Search bar visible at top of map
- [ ] Can enter text in search bar
- [ ] Search button works and geocodes location
- [ ] Map moves to searched location
- [ ] Events filtered by 50km radius
- [ ] "Show All Events" button appears after search
- [ ] Can reset to show all events
- [ ] Invalid searches show error message
- [ ] Empty search shows warning

## Performance Testing

### Test Geocoding Performance
The app geocodes addresses in the background. To test:

1. Clear app data to reset the database
2. Launch the app
3. Watch markers appear gradually as addresses are geocoded
4. Second launch should be instant (coordinates cached)

### Test with Many Events

Add 50+ events to test performance:
```java
for (int i = 0; i < 50; i++) {
    Event event = new Event(
        "Test Event " + i,
        "San Francisco, CA",
        "Test description",
        "Test time",
        0, 10, "Soccer"
    );
    database.eventDao().insert(event);
}
```

## Next Steps

Once basic testing works:
1. ✅ Test adding custom event types
2. ✅ Test with real addresses in your area
3. ✅ Add location search (see implementation guide)
4. ✅ Add event filtering by type
5. ✅ Add user location tracking

## Need Help?

If you encounter issues:
1. Check Logcat in Android Studio for error messages
2. Verify your Google Maps API key permissions
3. Ensure all required permissions are granted on your device
4. Check that GPS/Location services are enabled
