# Beyond Binary

A community-driven Android application that helps isolated individuals connect through AI-powered event recommendations, interactive maps, and engaging video content.

## 🎯 Mission

Beyond Binary creates a platform where people can discover events through personalized AI recommendations, explore activities on an interactive map, and build meaningful connections in their community.

## ✨ Features

### 🎥 AI-Powered Video Feed (For You Page)
- **Personalized & Recommended Tabs**: Swipe between curated content streams
- **Google Gemini AI Integration**: Intelligent event ranking based on your profile
- **Health Data Integration**: Recommendations tailored to your activity levels
- **ExoPlayer Video Playback**: Smooth, fullscreen video experiences
- **20+ Seeded Events**: Pre-loaded mock events for testing

### 🗺️ Interactive Event Map
- **Google Maps Integration**: Explore events on an interactive map
- **Custom Markers**: Visual event type indicators
- **70+ Event Categories** including:
  - 🏀 Sports: Soccer, Basketball, Yoga, Running, Tennis
  - 🥾 Outdoor: Hiking, Camping, Rock Climbing, Beach activities
  - ☕ Social: Coffee meetups, Board Games, Karaoke, Movie nights
  - 🍣 Dining: Sushi, BBQ, Pizza, Wine Tasting, Cooking Classes
  - 🎨 Arts: Painting, Photography, Museum visits, Theater
  - 📚 Learning: Book Clubs, Language Exchange, Workshops
- **Real-time Event Details**: View participant count, time, location
- **Address Geocoding**: Automatic address-to-coordinate conversion

### ➕ Event Creation
- **Easy Event Posting**: Create events with title, location, time, description
- **Category Selection**: Choose from 70+ specific event types
- **Participant Management**: Set and track participant limits
- **Backend Integration**: Events synced to SQLite database

### 👥 Community Profile
- **Instagram-Style Design**: Clean, modern profile interface
- **User Stats**: Track events attended and friend connections
- **Events Photos Tab**: View photos from attended events
- **Community Tab**: Social features and connections

### 🤖 AI Chatbot (Coming Soon)
- Smart event recommendations and queries

## 🏗️ Tech Stack

### Frontend (Android)
- **Java** with Android SDK
- **Google Maps API** - Interactive map visualization
- **Google Gemini 2.0 Flash** - AI-powered event ranking
- **ExoPlayer** - High-quality video playback
- **Room Database** - Local data persistence
- **Retrofit** - REST API communication
- **Material Design 3** - Modern UI components
- **ViewPager2** - Swipeable content tabs

### Backend (Node.js)
- **Express.js** - REST API server
- **SQLite3** - Event database
- **CORS** - Cross-origin requests
- **dotenv** - Environment configuration

## 🚀 Setup from Scratch

### Prerequisites
- **Android Studio** (latest version)
- **Node.js** (v14+)
- **Google Cloud Account** (for API keys)

### Step 1: Clone the Repository

```bash
git clone <your-repo-url>
cd Beyond_Binary
```

### Step 2: Get API Keys

#### Google Maps API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable **Maps SDK for Android**
4. Go to **Credentials** → **Create Credentials** → **API Key**
5. Copy the API key

#### Google Gemini API Key
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Click **Get API Key**
3. Copy the API key

### Step 3: Backend Setup

```bash
# Navigate to backend
cd backend

# Install dependencies
npm install

# Create .env file
cat > .env << EOF
PORT=3000
DATABASE_PATH=./database/events.db
NODE_ENV=development
EOF

# Initialize database with sample events
npm run init-db

# Start the server
npm start
```

You should see:
```
Connected to SQLite database
Events table ready
Server running on port 3000
```

**Keep this terminal running** while testing the app.

### Step 4: Frontend Setup

```bash
# Navigate to frontend
cd ../frontend

# Create local.properties file
cat > local.properties << EOF
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
geminiApiKey=YOUR_GEMINI_API_KEY_HERE
mapsApiKey=YOUR_GOOGLE_MAPS_API_KEY_HERE
EOF
```

**Important**: Replace the placeholders:
- `YOUR_USERNAME` - Your Mac username
- `YOUR_GEMINI_API_KEY_HERE` - Your Gemini API key from Step 2
- `YOUR_GOOGLE_MAPS_API_KEY_HERE` - Your Maps API key from Step 2

### Step 5: Open in Android Studio

```bash
# Open Android Studio with the project
open -a "Android Studio" /path/to/Beyond_Binary/frontend
```

Or manually:
1. Open Android Studio
2. Click **File** → **Open**
3. Navigate to `Beyond_Binary/frontend`
4. Click **Open**

### Step 6: Configure Gradle JDK

1. In Android Studio: **Preferences** → **Build, Execution, Deployment** → **Build Tools** → **Gradle**
2. Set **Gradle JDK** to **Embedded JDK (jbr-17)**
3. Click **Apply** and **OK**
4. Wait for Gradle sync to complete

### Step 7: Run the App

1. **Start an Android Emulator** or connect a physical device
   - Recommended: Pixel 5 or newer with API 24+
2. Click the **Run** button (▶️) in Android Studio
3. Select your device/emulator
4. Wait for build and installation

## 📁 Project Structure

```
Beyond_Binary/
├── frontend/                           # Android application
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/beyondbinary/app/
│   │   │   │   ├── MainActivity.java           # Main entry point with bottom nav
│   │   │   │   ├── MapsActivity.java           # Interactive event map
│   │   │   │   ├── AddEventActivity.java       # Create new events
│   │   │   │   ├── ProfileActivity.java        # User profile/community
│   │   │   │   ├── fyp/                         # For You Page (video feed)
│   │   │   │   │   ├── FypFragment.java
│   │   │   │   │   ├── PersonalizedFragment.java
│   │   │   │   │   ├── RecommendedFragment.java
│   │   │   │   │   └── ExoPlayerManager.java
│   │   │   │   ├── agents/
│   │   │   │   │   └── EventRankingAgent.java  # Gemini AI integration
│   │   │   │   ├── api/                         # Backend API clients
│   │   │   │   ├── data/                        # Data models & providers
│   │   │   │   └── ...
│   │   │   └── res/                             # UI layouts, drawables, etc.
│   │   └── build.gradle                         # Dependencies configuration
│   └── local.properties                         # API keys (gitignored)
│
├── backend/                            # Node.js REST API
│   ├── server.js                       # Express server
│   ├── init-database.js                # Database seeder
│   ├── database/
│   │   └── events.db                   # SQLite database
│   ├── package.json                    # Node dependencies
│   └── .env                            # Environment variables (gitignored)
│
└── README.md                           # This file
```

## 🔧 Configuration Files

### `frontend/local.properties` (Gitignored)
```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
geminiApiKey=YOUR_GEMINI_API_KEY
mapsApiKey=YOUR_MAPS_API_KEY
```

### `backend/.env` (Gitignored)
```env
PORT=3000
DATABASE_PATH=./database/events.db
NODE_ENV=development
```

## 🎮 How to Use

### 1. Home - For You Page
- Swipe between **Personalized** and **Recommended** tabs
- Videos are ranked by Gemini AI based on your profile
- Tap anywhere to pause/play
- Bottom navigation always accessible

### 2. Map View
- Explore events on interactive Google Maps
- Tap markers to see event details
- Click "Join" to increase participant count

### 3. Add Event
- Fill in event details (title, location, description, time)
- Select event category
- Set participant limits
- Submit to backend database

### 4. Profile/Community
- View your stats (events attended, friends)
- Browse **Events Photos** tab
- Explore **Community** connections

## 🧪 Testing Features

### Backend Endpoints
The backend runs on `http://localhost:3000` (maps to `http://10.0.2.2:3000` in Android emulator):

- `GET /api/events` - List all events
- `POST /api/events` - Create new event
- `GET /api/events/:id` - Get specific event
- `PUT /api/events/:id` - Update event
- `DELETE /api/events/:id` - Delete event
- `GET /api/stats` - Get system statistics

### Mock Data
The app includes:
- 20 pre-seeded events in the database
- Mock health data from JSON files
- Sample user profiles
- Test video URLs for the feed

## 🐛 Troubleshooting

### Gradle Sync Failed
- Ensure Gradle JDK is set to Embedded JDK
- Check `local.properties` has correct paths
- Try **File** → **Invalidate Caches** → **Restart**

### Map Not Loading
- Verify `mapsApiKey` in `local.properties`
- Ensure Maps SDK for Android is enabled in Google Cloud
- Check internet connection

### Backend Connection Failed
- Confirm backend is running (`npm start`)
- Check it's on port 3000
- For emulator, use `10.0.2.2:3000` not `localhost:3000`

### Video Feed Not Playing
- Check `geminiApiKey` in `local.properties`
- Verify internet connection for video URLs
- Check ExoPlayer dependencies in `build.gradle`

## 🔐 Security Notes

**Never commit these files:**
- `frontend/local.properties` - Contains API keys
- `backend/.env` - Contains secrets
- `backend/node_modules/` - Large dependencies
- `frontend/build/` - Build artifacts

These are all in `.gitignore` for your protection.

## 🚢 Building for Release

```bash
cd frontend
./gradlew assembleRelease
```

The APK will be in: `frontend/app/build/outputs/apk/release/`

## 📝 API Documentation

### Event Object
```json
{
  "id": 1,
  "title": "Morning Yoga at Marina Bay",
  "location": "Marina Bay Sands",
  "description": "Relaxing yoga session with sea views",
  "time": "2024-02-15 08:00",
  "currentParticipants": 5,
  "maxParticipants": 20,
  "eventType": "Yoga",
  "latitude": 1.2834,
  "longitude": 103.8607
}
```

## 🤝 Contributing

Contributions are welcome! This project aims to build a supportive community platform.

## 📄 License

Beyond Binary - Connecting people through shared experiences.

---

**Built with ❤️ to reduce isolation and build community**
