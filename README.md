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
| Chatbot (retrieval) | Gemini Embedding + LangChain MemoryVectorStore | Semantic search over events |
| Chatbot (synthesis) | Google Gemini 2.5 Flash | Generates natural-language responses with event context |
| Chatbot (orchestration) | LangGraph (StateGraph) | 3-node RAG pipeline: retrieval → synthesis → constraint check |

## Features

- **Personalized Feed** — Events matched to user interests, re-ranked by Gemini AI
- **Health-Based Recommendations** — Events suggested based on step count & heart rate data
- **Interactive Map** — Google Maps with custom emoji markers for 70+ event categories
- **Event Creation** — Create events with photos, location, time, category, and participant limits
- **AI Chatbot** — RAG-powered chatbot using LangGraph + Gemini for event discovery
- **Messaging** — Direct messaging with event card sharing / invites
- **Community Profile** — Gamified isometric neighborhood with achievements and a decoration shop

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
| Local DB | Room, SQLite |
| Backend | Node.js, Express.js, bcrypt, Multer |
| Backend DB | SQLite3 |
| Build | Gradle 9.1.0, AGP 9.0.0 |

## Project Structure

```
Beyond_Binary/
├── frontend/                     # Android application
│   ├── app/src/main/
│   │   ├── java/com/beyondbinary/app/
│   │   │   ├── agents/           # Gemini AI event ranking
│   │   │   ├── api/              # Retrofit API service
│   │   │   ├── chatbot/          # AI chatbot fragment
│   │   │   ├── data/             # Models, database, providers
│   │   │   ├── fyp/              # Home feed (Personalized + Recommended tabs)
│   │   │   ├── messaging/        # Chat activity + adapter
│   │   │   ├── onboarding/       # Interest selection
│   │   │   └── registration/     # Sign up / sign in
│   │   └── res/                  # Layouts, drawables, values
│   ├── build.gradle
│   └── local.properties          # API keys (gitignored)
│
├── backend/                      # Node.js REST API
│   ├── server.js                 # Express server with all endpoints
│   ├── chatbot/                  # LangGraph RAG pipeline
│   ├── init-database.js          # Database seeder (sample events)
│   ├── package.json
│   └── .env                      # Environment config (gitignored)
│
└── README.md
```

## Setup

### Prerequisites

- JDK 17+ (JDK 25 supported with Gradle 9.1.0)
- Android SDK API 36, build-tools 36.0.0
- Node.js v14+

### 1. Clone

```bash
git clone https://github.com/fearyj/Beyond_Binary.git
cd Beyond_Binary
```

### 2. API Keys

- **Google Maps**: [Cloud Console](https://console.cloud.google.com/) — Enable Maps SDK for Android
- **Google Gemini**: [AI Studio](https://aistudio.google.com/app/apikey) — For frontend ranking + backend chatbot

### 3. Backend

```bash
cd backend
npm install
npm run init-db    # Seed database with sample events
npm start          # Starts on port 3000
```

Create `backend/.env`:
```
PORT=3000
DATABASE_PATH=./database/events.db
NODE_ENV=development
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

### 4. Frontend

Create `frontend/local.properties`:
```properties
sdk.dir=YOUR_ANDROID_SDK_PATH
geminiApiKey=YOUR_GEMINI_API_KEY
mapsApiKey=YOUR_GOOGLE_MAPS_API_KEY
```

Open `Beyond_Binary/frontend` in Android Studio, sync Gradle, and run.

**CLI build (PowerShell):**
```powershell
cd frontend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
$env:ANDROID_HOME = "C:\Android"
.\gradlew.bat assembleDebug
```

## Backend API

Base URL: `http://localhost:3000/api` (emulator: `http://10.0.2.2:3000/api`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/events | List all events |
| GET | /api/events/:id | Get event by ID |
| GET | /api/events/nearby?lat=&lng=&radius= | Nearby events |
| POST | /api/events | Create event |
| PUT | /api/events/:id | Update event |
| DELETE | /api/events/:id | Delete event |
| POST | /api/users | Register |
| POST | /api/users/login | Login |
| GET | /api/users/:id | Get user |
| PUT | /api/users/:id | Update profile |
| GET | /api/users/:userId/events | User's events |
| POST | /api/events/:id/photos | Upload photo |
| GET | /api/events/:id/photos | Get photos |
| POST | /api/messages/invite | Send event invite |
| POST | /api/messages | Send message |
| GET | /api/messages/:userId/:otherUserId | Conversation thread |
| POST | /api/chatbot/chat | AI chatbot |
| GET | /api/health | Health check |
| GET | /api/stats | System statistics |

## App Navigation

```
SplashActivity
    │
    v
MainActivity
├── Sign In / Register → Onboarding → Home
├── Home (FYP tabs)
│   ├── Personalized → Event Detail → Map
│   └── Recommended  → Event Detail → Map
├── Map View (emoji markers → Event Detail)
├── Add Event
├── My Events
├── AI Chatbot
├── Messages → Chat (text + event invites)
└── Profile (Achievement Community + Shop)
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Gradle sync failed | Ensure JDK and SDK paths are correct in `local.properties` |
| Map not loading | Verify `mapsApiKey`; enable Maps SDK in Google Cloud |
| Backend connection failed | Ensure backend is running; emulator uses `10.0.2.2` not `localhost` |
| Events not loading | Check backend logs; verify Retrofit config |
| Chatbot not responding | Check `GEMINI_API_KEY` in backend `.env` |

## License

MIT
