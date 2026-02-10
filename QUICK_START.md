# Quick Start Guide - Beyond Binary Event Map

## 🎯 What You Have Now

### Core Features
1. ✅ **Google Maps Integration** with emoji markers for 70+ event types
2. ✅ **Location Search** - Find events anywhere in the world
3. ✅ **Event Filtering** - Show only events within 50km of searched location
4. ✅ **Local Database** - Events stored using Room/SQLite
5. ✅ **Auto Geocoding** - Addresses automatically converted to map coordinates
6. ✅ **Custom Info Windows** - Click markers to see event details

## 🚀 How to Test (Step-by-Step)

### Step 1: Get Google Maps API Key (5 minutes)
```
1. Go to: https://console.cloud.google.com/
2. Create new project: "Beyond Binary"
3. Enable APIs:
   - Maps SDK for Android
   - Geocoding API
4. Create API Key under Credentials
5. Copy the key (starts with AIza...)
```

### Step 2: Add API Key to Project
```
1. Open: frontend/app/src/main/AndroidManifest.xml
2. Find line 17: android:value="YOUR_GOOGLE_MAPS_API_KEY_HERE"
3. Replace with your actual key
4. Save file
```

### Step 3: Run the App
```
Option A - Physical Device (Recommended):
1. Enable Developer Options on phone (tap Build Number 7x)
2. Enable USB Debugging
3. Connect phone to computer
4. Open project in Android Studio
5. Click Run button (green triangle)

Option B - Emulator:
1. In Android Studio: Tools → Device Manager
2. Create Virtual Device (Pixel 5, API 34)
3. Click Run button
```

### Step 4: Test the Features

#### Test 1: View Sample Events (Automatic)
**Expected Result:**
- Map loads showing San Francisco
- 13 emoji markers appear automatically:
  - 🏀 Basketball at Golden Gate Park
  - ⚽ Soccer at Marina Green
  - 🏓 Ping Pong at SPIN SF
  - 🧘 Yoga at Dolores Park
  - ☕ Coffee at Ferry Building
  - 🎲 Board Games at Game Parlour
  - 📚 Book Club at SF Library
  - 🍣 Sushi in Japantown
  - 🍖 BBQ at Crissy Field
  - 🎨 Painting in Mission
  - 📷 Photography at Golden Gate Bridge
  - 🥾 Hiking at Twin Peaks
  - 🐕 Dog Park at Fort Funston

#### Test 2: Click Markers
**Action:** Tap any emoji marker
**Expected Result:**
- Info window pops up showing:
  - Event title
  - Location address
  - Time
  - Participant count (e.g., "5/10 participants")
  - Description

#### Test 3: Search for Location
**Action:** Type `San Francisco, CA` in search bar → Click 🔍
**Expected Result:**
- Map stays on San Francisco
- Shows "Showing 13 events within 50 km"
- "Show All Events" button appears

**Action:** Type `Los Angeles, CA` → Click 🔍
**Expected Result:**
- Map moves to Los Angeles
- Shows "Showing 0 events within 50 km" (no events there)
- All SF markers disappear

**Action:** Click "Show All Events" button
**Expected Result:**
- All 13 markers reappear
- Map returns to San Francisco
- Button disappears

#### Test 4: Test Different Searches
Try these locations:
- `New York City` → Map moves to NYC
- `Chicago, IL` → Map moves to Chicago
- `Tokyo, Japan` → Map moves to Tokyo
- `London, UK` → Map moves to London

## 🎨 Adding Your Own Events

### Via Code (Temporary - for testing)

Edit `SampleDataHelper.java` and add:
```java
Event myEvent = new Event(
    "My Ping Pong Game",
    "Your Address Here, City, State",
    "Come play ping pong!",
    "Saturday, 3:00 PM",
    5,      // current participants
    10,     // max participants
    "Ping Pong"  // This will show 🏓 emoji
);
database.eventDao().insert(myEvent);
```

### Available Event Types (70+ options)

**Sports:** Soccer ⚽, Basketball 🏀, Tennis 🎾, Ping Pong 🏓, Volleyball 🏐, Baseball ⚾, Football 🏈, Badminton 🏸, Swimming 🏊, Cycling 🚴, Running 🏃, Yoga 🧘, Gym 🏋️, Boxing 🥊, Martial Arts 🥋, Skateboarding 🛹, Surfing 🏄, Golf ⛳

**Outdoor:** Hiking 🥾, Camping ⛺, Rock Climbing 🧗, Fishing 🎣, Skiing ⛷️, Snowboarding 🏂, Beach 🏖️, Picnic 🧺, Bird Watching 🦜

**Social:** Party 🎉, Coffee ☕, Movie 🎬, Concert 🎵, Karaoke 🎤, Dancing 💃, Board Games 🎲, Video Games 🎮, Trivia Night 🧠, Meetup 👥

**Food:** Dinner 🍽️, Lunch 🍱, Breakfast 🍳, BBQ 🍖, Pizza 🍕, Sushi 🍣, Dessert 🍰, Wine Tasting 🍷, Beer Tasting 🍺, Cooking Class 👨‍🍳

**Arts:** Painting 🎨, Photography 📷, Museum 🏛️, Theater 🎭, Music 🎵, Crafts ✂️, Pottery 🏺, Drawing ✏️, Dance Class 💃

**Learning:** Book Club 📚, Study Group 📖, Language Exchange 🗣️, Workshop 🔧, Lecture 🎓, Writing ✍️

**Nature:** Dog Walking 🐕, Pet Meetup 🐾, Gardening 🌱, Park Visit 🌳

**Tech:** Coding 💻, Networking 🤝, Startup 🚀

## 🔧 Customizing

### Change Search Radius
Edit `MapsActivity.java` line 40:
```java
private static final int SEARCH_RADIUS_KM = 50; // Change to 25, 100, etc.
```

### Add Custom Event Type
Edit `MapsActivity.java`, add to the emoji map:
```java
put("Bowling", "🎳");
put("Meditation", "🧘‍♀️");
put("Laser Tag", "🔫");
```

### Change Default Location
Edit `MapsActivity.java`, find the default location:
```java
LatLng defaultLocation = new LatLng(37.7749, -122.4194); // Change coordinates
```

## 📱 Testing Checklist

- [ ] App launches without crashes
- [ ] Map loads and shows San Francisco
- [ ] 13 emoji markers appear
- [ ] Clicking markers shows event info
- [ ] Search bar works
- [ ] Can search different cities
- [ ] Events filter by location
- [ ] "Show All Events" resets view
- [ ] Can zoom/pan map
- [ ] Invalid searches show error

## ❓ Troubleshooting

### Map shows gray screen
- Check API key is correct in AndroidManifest.xml
- Verify "Maps SDK for Android" is enabled in Google Cloud
- Check internet connection

### No markers appear
- Open Logcat in Android Studio
- Look for errors
- Check if sample data is being inserted

### Search doesn't work
- Verify "Geocoding API" is enabled
- Check internet connection
- Try more specific searches (include state/country)

### App crashes
- Check Logcat for stack trace
- Verify all dependencies in build.gradle
- Try: Build → Clean Project → Rebuild Project

## 📚 Next Steps

1. ✅ Test all features listed above
2. ⏭️ Add real events in your area
3. ⏭️ Customize event types for your needs
4. ⏭️ Build UI for users to add events (future feature)
5. ⏭️ Add event categories filter (future feature)
6. ⏭️ Implement user authentication (future feature)

## 📖 Full Documentation

- **Testing Guide:** [TESTING_GUIDE.md](TESTING_GUIDE.md)
- **Frontend README:** [frontend/README.md](frontend/README.md)
- **Main README:** [README.md](README.md)

## 🎉 You're Ready!

Your event map app is fully functional with:
- ✅ Google Maps with 70+ emoji event types
- ✅ Location search with 50km radius filtering
- ✅ Sample events for testing
- ✅ Event info popups
- ✅ Local database storage

Happy testing! 🚀
