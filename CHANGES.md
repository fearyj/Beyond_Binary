# Beyond Binary — FYP Feature Documentation

## What Was Built


The app has two feed tabs:

- **Personalized** — Shows events matched to the user's interests, re-ranked by an AI agent (Google Gemini) so the most meaningful events appear first.
- **Recommended** — Shows events based on the user's health data (step count & heart rate). For example, if a user has been inactive, it suggests walking events; if their heart rate is elevated, it suggests calming activities like yoga or meditation.

All event data (titles, descriptions, categories, locations, video URLs) is **pre-seeded mock data** stored in a local SQLite database. Health data is also mocked via a JSON file. Video playback uses ExoPlayer with publicly available sample videos.

---

## Project Structure

```
Beyond_Binary/
├── app/
│   ├── build.gradle                         # App-level build config & dependencies
│   ├── proguard-rules.pro                   # ProGuard rules (empty, not used yet)
│   └── src/main/
│       ├── AndroidManifest.xml              # App manifest
│       ├── java/com/beyondbinary/app/
│       │   ├── BeyondBinaryApplication.java # App startup — seeds the database on first launch
│       │   ├── MainActivity.java            # Single activity — loads FypFragment, fullscreen mode
│       │   ├── agents/
│       │   │   └── EventRankingAgent.java   # AI agent — sends events to Gemini API for ranking
│       │   ├── data/
│       │   │   ├── database/
│       │   │   │   ├── AppDatabaseHelper.java  # SQLite helper — events & users tables
│       │   │   │   └── DatabaseSeeder.java     # Seeds 20 mock events + 1 default user
│       │   │   ├── models/
│       │   │   │   ├── Event.java           # Event model (title, category, description, mediaUrl, location)
│       │   │   │   └── User.java            # User model (bio, interestTags)
│       │   │   └── providers/
│       │   │       └── HealthDataProvider.java # Reads mock health data from JSON
│       │   └── fyp/
│       │       ├── FypFragment.java         # Host fragment — TabLayout + ViewPager2
│       │       ├── FypPagerAdapter.java     # Adapter wiring the two tab fragments
│       │       ├── PersonalizedFragment.java # "Personalized" tab — interests + AI ranking
│       │       ├── RecommendedFragment.java  # "Recommended" tab — health-data-driven
│       │       ├── EventCardAdapter.java     # RecyclerView adapter for event cards
│       │       └── ExoPlayerManager.java     # Singleton ExoPlayer — manages video playback
│       └── res/
│           ├── drawable/                     # Launcher icon vectors
│           ├── layout/
│           │   ├── activity_main.xml         # Single FrameLayout container
│           │   ├── fragment_fyp.xml          # TabLayout + ViewPager2
│           │   ├── fragment_feed.xml         # RecyclerView for event cards
│           │   └── item_event_card.xml       # Individual card — video + text overlay
│           ├── mipmap-*/                     # Launcher icons at various densities
│           ├── raw/
│           │   └── mock_health_data.json     # Mock health data (steps, heartRate)
│           └── values/
│               ├── colors.xml               # App color palette
│               ├── strings.xml              # App name & tab labels
│               └── themes.xml               # Dark theme definition
├── build.gradle                              # Root build file (AGP 9.0.0)
├── settings.gradle                           # Project settings (Gradle 9.1.0)
├── gradle.properties                         # JVM & Android settings
├── gradle/wrapper/                           # Gradle wrapper (9.1.0)
├── gradlew / gradlew.bat                     # Gradle wrapper scripts
└── local.properties                          # LOCAL ONLY — SDK path & Gemini API key (gitignored)
```

---

## How to Set Up and Run

### Prerequisites

- **JDK 17+** (JDK 25 works; JDK 17 is the minimum for source compatibility)
- **Android SDK** with:
  - Platform: `android-36`
  - Build tools: `36.0.0`
- **Gradle 9.1.0** (bundled via the wrapper — no separate install needed)

### Steps

1. **Clone the repo:**
   ```bash
   git clone https://github.com/fearyj/Beyond_Binary.git
   cd Beyond_Binary
   ```

2. **Create `local.properties`** in the project root (this file is gitignored):
   ```properties
   sdk.dir=C\:\\path\\to\\your\\Android\\Sdk
   gemini.api.key=YOUR_GEMINI_API_KEY_HERE
   ```
   - Get a Gemini API key from [Google AI Studio](https://aistudio.google.com/apikey).
   - If you skip the API key, the Personalized tab will still work — it just won't re-rank events with AI (they'll appear in database order).

3. **Build:**
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

4. **Run** on an emulator or physical device (API 24+, i.e. Android 7.0+).

---

## How the Two Feed Tabs Work

### Personalized Tab (`PersonalizedFragment`)

1. Reads the **user profile** from the SQLite database (bio + interest tags like "Walking,Yoga,Outdoor").
2. Queries events whose **category** matches the user's interest tags.
3. If a **Gemini API key** is configured:
   - Sends the user profile + event list to `EventRankingAgent`.
   - The agent calls **Gemini 2.0 Flash** with a prompt asking it to rank events by "meaningfulness" to the user.
   - Gemini returns a comma-separated list of event IDs in ranked order.
   - The agent reorders the events accordingly.
4. Displays the (ranked) events in a vertical-swipe feed.

### Recommended Tab (`RecommendedFragment`)

1. Reads **mock health data** from `res/raw/mock_health_data.json` (currently: `{"steps": 3500, "heartRate": 92}`).
2. Applies simple rules:
   - **Steps < 4,000** → show Walking & Outdoor events.
   - **Heart rate > 85** → show Meditation & Yoga events.
   - **Otherwise** → show all events.
3. Displays the filtered events in the same vertical-swipe feed.

Both tabs share the same card layout (`item_event_card.xml`) and adapter (`EventCardAdapter`). Each card shows a full-screen video background with an overlaid text panel (title, category, description, location).

---

## How the AI Ranking Works

The `EventRankingAgent` class:

1. Creates a `GenerativeModel` using **Gemini 2.0 Flash** and the API key from `BuildConfig`.
2. Builds a text prompt containing the user's bio, interests, and a numbered list of events (ID, title, category, description).
3. Asks Gemini to return **only a comma-separated list of event IDs** sorted by relevance.
4. Parses the response, reorders events to match, and appends any events not mentioned by the AI at the end.
5. If the API call fails for any reason (no key, network error, bad response), it **falls back silently** to the original event order — the app never crashes due to AI issues.

---

## What's Mock/Seeded Data vs Real Data

| Component | Data Source | Notes |
|---|---|---|
| Events (20 total) | Seeded into SQLite on first launch | 5 Walking, 3 Outdoor, 4 Yoga, 4 Meditation, 4 Social/Community |
| Event videos | Public Google sample video URLs | Streamed from the internet |
| User profile | Seeded (bio: "Loves nature and mindfulness", interests: Walking, Yoga, Outdoor) | Hardcoded in `DatabaseSeeder` |
| Health data | `mock_health_data.json` | Static JSON file; steps=3500, heartRate=92 |
| AI ranking | Real Gemini API call | Requires a valid API key; gracefully degrades without one |

To switch to real data later, you would:
- Replace `DatabaseSeeder` with actual event data from an API or backend.
- Replace `HealthDataProvider` to read from Google Fit, Samsung Health, etc.
- Replace the hardcoded user with actual user registration/profile data.

---

## How to Modify the UI Later

### Change the event card layout
Edit `app/src/main/res/layout/item_event_card.xml`. The current layout is a `FrameLayout` with a full-screen `PlayerView` and a bottom-aligned `LinearLayout` for text. Add new fields (e.g., date, attendee count) here and bind them in `EventCardAdapter.onBindViewHolder()`.

### Change the color scheme
Edit `app/src/main/res/values/colors.xml` and `themes.xml`. The app uses a dark theme by default.

### Add a new tab
1. Create a new `Fragment` class in the `fyp/` package.
2. Add it to `FypPagerAdapter` (increment `getItemCount()` and add a case in `createFragment()`).
3. Add a new string resource for the tab label in `strings.xml` and wire it in `FypFragment`'s `TabLayoutMediator`.

### Change the health data thresholds
Edit the `if/else` logic in `RecommendedFragment.loadRecommendations()` (lines 61-75). The current thresholds are `steps < 4000` and `heartRate > 85`.

### Change the mock health data values
Edit `app/src/main/res/raw/mock_health_data.json`. The format is `{"steps": <number>, "heartRate": <number>}`.

### Change the seeded events
Edit `DatabaseSeeder.seed()`. Each event has: title, category, description, video URL, and location. After changing seed data, **uninstall the app** (or clear app data) so the database re-seeds on next launch.

### Change the user profile
Edit the `db.insertUser(...)` call at the bottom of `DatabaseSeeder.seed()`. The interest tags are a comma-separated string (e.g., `"Walking,Yoga,Outdoor"`). These must match event categories for the Personalized tab to filter correctly.
