# Buddeee

A community-driven Android application that helps isolated individuals connect through AI-powered event recommendations, interactive maps, and personalized event discovery.

## Architecture

```
+----------------------------------------------------------------------+
|                       Android App (Frontend)                          |
|  Java · Android SDK (API 36) · Material Design 3                     |
|  Google Maps SDK · Retrofit 2 / OkHttp · Glide · ExoPlayer (Media3) |
|  Google Gemini 2.0 Flash (on-device event ranking)                   |
|  Room · Gson · Flexbox · ViewPager2                                  |
+----------------------------------+-----------------------------------+
                                   | HTTP / REST (Retrofit)
                                   v
+----------------------------------------------------------------------+
|                    Node.js Backend (Express.js)                       |
|  RESTful API · User Auth (bcrypt) · Event CRUD · Multer (uploads)    |
|  ----------------------------------------------------------------    |
|  RAG Chatbot Pipeline (LangGraph)                                    |
|    Retrieval  →  Synthesis  →  Constraint Checker                    |
|    LangChain MemoryVectorStore    Google Gemini 2.5 Flash            |
|    Gemini Embedding (gemini-embedding-001)                           |
+----------------------------------+-----------------------------------+
                                   |
                                   v
+----------------------------------------------------------------------+
|                         SQLite Database                               |
|  Events · Users · Interactions · Messages · Event Photos             |
+----------------------------------------------------------------------+
```

### AI / ML Summary

| Component | Model / Tool | Purpose |
|-----------|-------------|---------|
| Event ranking | Google Gemini 2.0 Flash | Re-ranks personalized feed by relevance to user interests |
| Health-based recommendations | Rule engine | Filters events based on step count and heart rate |
| Chatbot (retrieval) | Gemini Embedding (`gemini-embedding-001`) + LangChain MemoryVectorStore | Semantic search over events |
| Chatbot (synthesis) | Google Gemini 2.5 Flash | Generates natural-language responses with event context |
| Chatbot (orchestration) | LangGraph (StateGraph) | 3-node RAG pipeline: retrieval → synthesis → constraint check |

## Features

### Event Discovery (FYP Tabs)
- **Personalized** — Events matched to user interests, re-ranked by Gemini AI
- **Recommended** — Events based on health data (step count & heart rate)
- Vertical-swipe card feed with full-screen video backgrounds (ExoPlayer)

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
- Backend-driven RAG chatbot using LangGraph + Gemini
- Semantic event search via vector embeddings
- Context-aware responses with conversation history

### Messaging
- Direct messaging between users
- Event card sharing / invites in chat

## How the Feed Tabs Work

### Personalized Tab (`PersonalizedFragment`)

1. Reads the **user profile** from SQLite (bio + interest tags like "Walking,Yoga,Outdoor").
2. Queries events whose **category** matches the user's interest tags.
3. If a **Gemini API key** is configured:
   - Sends the user profile + event list to `EventRankingAgent`.
   - The agent calls **Gemini 2.0 Flash** with a prompt asking it to rank events by relevance.
   - Gemini returns a comma-separated list of event IDs in ranked order.
   - The agent reorders the events accordingly.
4. Displays the (ranked) events in a vertical-swipe feed.

### Recommended Tab (`RecommendedFragment`)

1. Reads **mock health data** from `res/raw/mock_health_data.json` (e.g. `{"steps": 3500, "heartRate": 92}`).
2. Applies rules:
   - **Steps < 4,000** → show Walking & Outdoor events.
   - **Heart rate > 85** → show Meditation & Yoga events.
   - **Otherwise** → show all events.
3. Displays the filtered events in the same vertical-swipe feed.

Both tabs share the same card layout (`item_event_card.xml`) and adapter (`EventCardAdapter`). Each card shows a full-screen video background with an overlaid text panel (title, category, description, location).

### How the AI Ranking Works

The `EventRankingAgent` class:

1. Creates a `GenerativeModel` using **Gemini 2.0 Flash** and the API key from `BuildConfig`.
2. Builds a text prompt containing the user's bio, interests, and a numbered list of events.
3. Asks Gemini to return **only a comma-separated list of event IDs** sorted by relevance.
4. Parses the response, reorders events to match, and appends any unmentioned events at the end.
5. If the API call fails (no key, network error, bad response), it **falls back silently** to the original event order.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Java, Android SDK (compileSdk 36), Material Design 3 |
| AI (on-device) | Google Gemini 2.0 Flash (generative AI SDK) |
| AI (backend) | Google Gemini 2.5 Flash, Gemini Embedding, LangChain, LangGraph |
| Maps | Google Maps SDK for Android |
| Video | ExoPlayer (Media3) |
| Networking | Retrofit 2, OkHttp |
| Image Loading | Glide |
| Local DB | Room, SQLite (AppDatabaseHelper) |
| Backend | Node.js, Express.js, bcrypt, Multer |
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
│   │   │   ├── ShareEventActivity.java        # Event sharing / invite
│   │   │   ├── ShopItem.java / ShopItemAdapter.java  # Shop overlay
│   │   │   ├── agents/
│   │   │   │   └── EventRankingAgent.java     # Gemini AI event ranking
│   │   │   ├── api/                           # Retrofit API service + response models
│   │   │   ├── chatbot/                       # AI chatbot fragment
│   │   │   ├── data/
│   │   │   │   ├── database/AppDatabaseHelper.java
│   │   │   │   ├── models/User.java, Event.java
│   │   │   │   └── providers/EventProvider.java, HealthDataProvider.java
│   │   │   ├── fyp/                           # Home tabs (Personalized, Recommended)
│   │   │   ├── messaging/                     # Chat activity + adapter
│   │   │   ├── onboarding/                    # Interest selection onboarding
│   │   │   └── registration/                  # Sign up / sign in
│   │   └── res/
│   │       ├── layout/                        # XML layouts
│   │       ├── drawable/                      # Vector icons, shape backgrounds
│   │       ├── drawable-xxhdpi/               # PNG assets (houses, shop items, logo)
│   │       ├── font/itim.ttf                  # Itim font
│   │       ├── raw/mock_health_data.json      # Mock health data
│   │       └── values/                        # Colors, strings, themes, dimens
│   ├── build.gradle                           # Dependencies, API keys from local.properties
│   └── local.properties                       # API keys (gitignored)
│
├── backend/                           # Node.js REST API
│   ├── server.js                      # Express server with all endpoints
│   ├── chatbot/
│   │   ├── graph.js                   # LangGraph RAG pipeline (3-node StateGraph)
│   │   ├── vectorStore.js             # LangChain MemoryVectorStore + Gemini embeddings
│   │   └── prompts.js                 # Chatbot prompt templates
│   ├── init-database.js               # Database seeder (sample Singapore events)
│   ├── database/events.db             # SQLite database (gitignored)
│   ├── public/uploads/                # Uploaded event photos
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

- **Google Maps API Key**: [Google Cloud Console](https://console.cloud.google.com/) — Enable Maps SDK for Android
- **Google Gemini API Key**: [Google AI Studio](https://aistudio.google.com/app/apikey) — Used for both frontend ranking and backend chatbot

### 3. Backend Setup

```bash
cd backend
npm install
npm run init-db    # Seed database with sample events
npm start          # Start server on port 3000
```

Create `backend/.env`:
```
PORT=3000
DATABASE_PATH=./database/events.db
NODE_ENV=development
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
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
| GET | /api/events/nearby?lat=&lng=&radius= | Nearby events |
| POST | /api/events | Create event |
| PUT | /api/events/:id | Update event |
| DELETE | /api/events/:id | Delete event |
| POST | /api/users | Register user |
| POST | /api/users/login | Login (email + password) |
| GET | /api/users/:id | Get user |
| PUT | /api/users/:id | Update user profile |
| GET | /api/users/:userId/events | User's joined/created events |
| POST | /api/interactions | Track user-event interaction |
| GET | /api/interactions/:userId | User interaction history |
| POST | /api/events/:id/photos | Upload event photo |
| GET | /api/events/:id/photos | Get event photos |
| GET | /api/users/:userId/attended-galleries | Attended event photo galleries |
| POST | /api/messages/invite | Send event invite message |
| POST | /api/messages | Send text message |
| GET | /api/messages/:userId/:otherUserId | Get conversation thread |
| POST | /api/chatbot/chat | AI chatbot |
| GET | /api/stats | System statistics |

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
├── Messages → Chat (text + event invites)
└── Profile (Achievement Community + Shop)
```

## Mock / Seeded Data

| Component | Source | Notes |
|-----------|--------|-------|
| Events | Seeded into SQLite via `init-database.js` | Sample Singapore events |
| User profile | Created via registration | Or seeded default user |
| Health data | `mock_health_data.json` | Static JSON; steps=3500, heartRate=92 |
| AI event ranking | Real Gemini API call | Requires valid API key; degrades gracefully without one |
| Event videos | Public sample video URLs | Streamed from the internet |

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Gradle sync failed | Ensure JDK and SDK paths are correct in local.properties |
| Map not loading | Verify mapsApiKey; enable Maps SDK in Google Cloud |
| Backend connection failed | Ensure backend is running; emulator uses 10.0.2.2 not localhost |
| Events not loading | Check backend logs; verify Retrofit config in RetrofitClient.java |
| Port 3000 in use | Kill the process or use `PORT=3001 npm start` |
| Chatbot not responding | Check GEMINI_API_KEY in backend `.env`; wait for vector store init |

## Security Notes

Never commit: `local.properties`, `backend/.env`, `backend/database/events.db`, `node_modules/`, `build/` — all are in `.gitignore`.

## License

MIT
