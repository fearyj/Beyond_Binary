# Buddeee

A community-driven Android application that helps isolated individuals connect through AI-powered event recommendations, interactive maps, and personalized event discovery.

## Architecture

```
+-----------------------------------------------------------+
|                  Android App (Frontend)                     |
|  Java / Android SDK / Material Design 3                    |
|  Google Maps, Gemini AI, Retrofit, Glide                   |
+---------------------------+-------------------------------+
                            | HTTP/REST (Retrofit)
                            v
+---------------------------+-------------------------------+
|               Node.js Backend (Express.js)                 |
|  RESTful API / User Auth / Event CRUD                      |
+---------------------------+-------------------------------+
                            |
                            v
+-----------------------------------------------------------+
|                    SQLite Database                          |
|  Events, Users, Interactions                               |
+-----------------------------------------------------------+
```

## Features

### Event Discovery
- Personalized and Recommended tabs with swipeable feeds
- Google Gemini 2.0 Flash AI-powered event ranking
- Event detail view with join, share, and map integration

### Interactive Event Map
- Google Maps with custom emoji markers for 70+ event categories
- Tap markers to view details; auto-zoom from event detail
- Address geocoding and location search

### Event Creation
- Create events with title, location, time, description, category
- Date/time pickers, participant limits, backend sync

### Community Profile (Achievement View)
- Gamified isometric neighborhood with houses, roads, traffic light
- Shop overlay to buy decorations (Fountain, Garden, Bench, Signboard)
- Profile stats: events attended, hosted, friends
- Photo grid from attended events

### AI Chatbot
- Gemini-powered assistant for event discovery and queries
- Context-aware responses using event database

### Messaging
- Direct messaging between users
- Event card sharing in chat

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Java, Android SDK (compileSdk 36), Material Design 3 |
| AI | Google Gemini 2.0 Flash (generative AI SDK) |
| Maps | Google Maps SDK for Android |
| Networking | Retrofit 2, OkHttp |
| Image Loading | Glide |
| Local DB | SQLite (AppDatabaseHelper) |
| Backend | Node.js, Express.js |
| Backend DB | SQLite3 |
| Build | Gradle 9.1.0, AGP 9.0.0 |

## Project Structure

```
Beyond_Binary/
├── frontend/                          # Android application
│   ├── app/src/main/
│   │   ├── java/com/beyondbinary/app/
│   │   │   ├── MainActivity.java              # Main entry, bottom nav, fragment routing
│   │   │   ├── SplashActivity.java            # Splash/front page
│   │   │   ├── ProfileActivity.java           # Profile + achievement community view
│   │   │   ├── MapsActivity.java              # Interactive event map
│   │   │   ├── AddEventActivity.java          # Event creation form
│   │   │   ├── EventDetailActivity.java       # Full event detail page
│   │   │   ├── MessagesActivity.java          # Messaging list
│   │   │   ├── ShopItem.java / ShopItemAdapter.java  # Shop overlay
│   │   │   ├── agents/
│   │   │   │   └── EventRankingAgent.java     # Gemini AI integration
│   │   │   ├── api/                           # Retrofit API service + response models
│   │   │   ├── chatbot/                       # AI chatbot fragment
│   │   │   ├── data/
│   │   │   │   ├── database/AppDatabaseHelper.java
│   │   │   │   ├── models/User.java, Event.java
│   │   │   │   └── providers/EventProvider.java
│   │   │   ├── fyp/                           # Home tabs (Personalized, Recommended)
│   │   │   ├── messaging/                     # Chat activity
│   │   │   ├── onboarding/                    # Interest selection onboarding
│   │   │   └── registration/                  # Sign up / sign in
│   │   └── res/
│   │       ├── layout/                        # XML layouts
│   │       ├── drawable/                      # Vector icons, shape backgrounds
│   │       ├── drawable-xxhdpi/               # PNG assets (houses, shop items, logo)
│   │       ├── font/itim.ttf                  # Itim font
│   │       └── values/                        # Colors, strings, themes
│   ├── build.gradle                           # Dependencies, API keys from local.properties
│   └── local.properties                       # API keys (gitignored)
│
├── backend/                           # Node.js REST API
│   ├── server.js                      # Express server with all endpoints
│   ├── init-database.js               # Database seeder (sample Singapore events)
│   ├── database/events.db             # SQLite database (gitignored)
│   ├── package.json
│   └── .env                           # Environment config (gitignored)
│
└── README.md
```

## System Setup

### Prerequisites

| Tool | Minimum Version | Notes |
|------|----------------|-------|
| JDK | 17+ | JDK 25 supported with Gradle 9.1.0 |
| Android SDK | API 36 | build-tools 36.0.0 |
| Node.js | v14+ | For backend |
| Android Studio | Latest | Or build via CLI with Gradle |

### 1. Clone the Repository

```bash
git clone https://github.com/fearyj/Beyond_Binary.git
cd Beyond_Binary
```

### 2. Get API Keys

- **Google Maps API Key**: [Google Cloud Console](https://console.cloud.google.com/) - Enable Maps SDK for Android
- **Google Gemini API Key**: [Google AI Studio](https://aistudio.google.com/app/apikey)

### 3. Backend Setup

```bash
cd backend
npm install
npm run init-db    # Seed database with sample events
npm start          # Start server on port 3000
```

For development with auto-restart: `npm run dev`

### 4. Frontend Setup

Create `frontend/local.properties`:
```properties
sdk.dir=YOUR_ANDROID_SDK_PATH
geminiApiKey=YOUR_GEMINI_API_KEY
mapsApiKey=YOUR_GOOGLE_MAPS_API_KEY
```

**Build via Android Studio:**
1. Open `Beyond_Binary/frontend` in Android Studio
2. Sync Gradle
3. Run on emulator or device

**Build via CLI (PowerShell):**
```powershell
cd frontend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
$env:ANDROID_HOME = "C:\Android"
.\gradlew.bat assembleDebug
C:\Android\platform-tools\adb.exe install -r app\build\outputs\apk\debug\app-debug.apk
```

**Build via CLI (Bash/Mac/Linux):**
```bash
cd frontend
JAVA_HOME="/path/to/jdk" ANDROID_HOME="/path/to/android-sdk" ./gradlew assembleDebug
```

## Backend API

Base URL: `http://localhost:3000/api` (emulator: `http://10.0.2.2:3000/api`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/health | Health check |
| GET | /api/events | List all events |
| GET | /api/events/:id | Get event by ID |
| GET | /api/events/nearby?latitude=&longitude=&radius= | Nearby events |
| POST | /api/events | Create event |
| PUT | /api/events/:id | Update event |
| DELETE | /api/events/:id | Delete event |
| GET | /api/stats | System statistics |
| POST | /api/users | Create user |
| GET | /api/users/:id | Get user |
| PUT | /api/users/:id | Update user |
| GET | /api/users/:id/interactions | User interactions |

## App Navigation Flow

```
SplashActivity (Front page with logo)
    |
    v
MainActivity
├── Registration / Sign In
│   └── Profile Setup → Onboarding → Home
├── Home (FYP tabs)
│   ├── Personalized Events → Event Detail → Map
│   └── Recommended Events → Event Detail → Map
├── Map View (emoji markers → Event Detail)
├── Add Event
├── My Events
├── AI Chatbot
├── Messages → Chat
└── Profile (Achievement Community + Shop)
```

## Configuration (Gitignored)

**frontend/local.properties:**
```properties
sdk.dir=/path/to/android/sdk
geminiApiKey=YOUR_KEY
mapsApiKey=YOUR_KEY
```

**backend/.env:**
```
PORT=3000
DATABASE_PATH=./database/events.db
NODE_ENV=development
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Gradle sync failed | Ensure JDK and SDK paths are correct in local.properties |
| Map not loading | Verify mapsApiKey; enable Maps SDK in Google Cloud |
| Backend connection failed | Ensure backend is running; emulator uses 10.0.2.2 not localhost |
| Events not loading | Check backend logs; verify Retrofit config in RetrofitClient.java |
| Port 3000 in use | Kill the process or use `PORT=3001 npm start` |

## Development Workflow

| What Changed | Rebuild APK? | Reinstall? | Restart Emulator? | Restart Backend? |
|---|---|---|---|---|
| Backend code | No | No | No | Auto (nodemon) |
| Java / Kotlin | Yes | Yes | No | No |
| XML layouts | Yes | Yes | No | No |
| Drawables / resources | Yes | Yes | No | No |
| build.gradle | Yes | Yes | No | No |

## Security Notes

Never commit: `local.properties`, `backend/.env`, `backend/database/events.db`, `node_modules/`, `build/` -- all are in `.gitignore`.

## License

MIT
