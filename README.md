# Beyond Binary

A community-driven Android application that helps isolated individuals connect through AI-powered event recommendations, interactive maps, and personalized event discovery.

## 🎯 Mission

Beyond Binary creates a platform where people can discover events through personalized AI recommendations, explore activities on an interactive map, and build meaningful connections in their community.

## ✨ Features

### 🏠 Event Discovery Home
- **Personalized & Recommended Tabs**: Swipe between curated event feeds
- **Google Gemini AI Integration**: Intelligent event ranking based on your profile
- **Clean List Interface**: Browse events with titles, locations, times, and participant counts
- **Event Detail View**: Tap any event to see full details, join, or view on map
- **Quick Event Creation**: Floating Action Button for instant event creation
- **Bottom Navigation**: Consistent navigation across all screens

### 📋 Event Detail Page
- **Full Event Information**: Complete details including description, time, location, participants
- **Join Events**: One-tap joining with real-time participant tracking
- **Map Integration**: "View on Map" button with auto-zoom to event location
- **Event Type Badges**: Visual category indicators
- **Bottom Navigation**: Navigate anywhere without going back

### 🗺️ Interactive Event Map
- **Google Maps Integration**: Explore events on an interactive map
- **Custom Emoji Markers**: Visual event type indicators (⚽🏀🎾☕🍣🎨📚)
- **Tap to View Details**: Click map markers to navigate to full event details
- **Auto-Zoom Feature**: Navigate from event details to see exact location
- **70+ Event Categories** including:
  - 🏀 Sports: Soccer, Basketball, Yoga, Running, Tennis
  - 🥾 Outdoor: Hiking, Camping, Rock Climbing, Beach activities
  - ☕ Social: Coffee meetups, Board Games, Karaoke, Movie nights
  - 🍣 Dining: Sushi, BBQ, Pizza, Wine Tasting, Cooking Classes
  - 🎨 Arts: Painting, Photography, Museum visits, Theater
  - 📚 Learning: Book Clubs, Language Exchange, Workshops
- **Real-time Event Details**: View participant count, time, location in info windows
- **Address Geocoding**: Automatic address-to-coordinate conversion
- **Location Search**: Find events near specific locations

### ➕ Event Creation
- **Easy Event Posting**: Create events with title, location, time, description
- **Category Selection**: Choose from 70+ specific event types
- **Date & Time Pickers**: Intuitive date/time selection
- **Participant Management**: Set and track participant limits
- **Backend Integration**: Events synced to SQLite database
- **Form Validation**: Ensures all required fields are filled

### 👥 Community Profile
- **Instagram-Style Design**: Clean, modern profile interface
- **User Stats**: Track events attended, events hosted, and friend connections
- **Events Photos Tab**: View photos from attended events
- **Community Tab**: Social features and connections
- **Profile Editing**: Customize your profile

### 🤖 AI Chatbot (Coming Soon)
- Smart event recommendations and queries

## 🏗️ Tech Stack

### Frontend (Android)
- **Java** with Android SDK
- **Google Maps API** - Interactive map visualization
- **Google Gemini 2.0 Flash** - AI-powered event ranking
- **Room Database** - Local data persistence
- **Retrofit** - REST API communication
- **Material Design 3** - Modern UI components
- **ViewPager2** - Swipeable content tabs
- **RecyclerView** - Efficient list rendering
- **CoordinatorLayout** - Advanced UI behaviors

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
git clone https://github.com/fearyj/Beyond_Binary.git
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
│   │   │   │   ├── MainActivity.java           # Main entry with bottom nav
│   │   │   │   ├── HomeFragment.java           # Event discovery with tabs
│   │   │   │   ├── EventListFragment.java      # Personalized events list
│   │   │   │   ├── RecommendedEventsFragment.java  # Recommended events
│   │   │   │   ├── EventDetailActivity.java    # Full event details page
│   │   │   │   ├── EventListAdapter.java       # RecyclerView adapter
│   │   │   │   ├── HomePagerAdapter.java       # Tab management
│   │   │   │   ├── MapsActivity.java           # Interactive event map
│   │   │   │   ├── AddEventActivity.java       # Create new events
│   │   │   │   ├── ProfileActivity.java        # User profile/community
│   │   │   │   ├── agents/
│   │   │   │   │   └── EventRankingAgent.java  # Gemini AI integration
│   │   │   │   ├── api/                         # Backend API clients
│   │   │   │   │   ├── ApiService.java
│   │   │   │   │   ├── RetrofitClient.java
│   │   │   │   │   └── *Response.java
│   │   │   │   ├── data/                        # Data models & providers
│   │   │   │   ├── Event.java                   # Event model
│   │   │   │   ├── EventDao.java                # Room DAO
│   │   │   │   └── EventDatabase.java           # Room database
│   │   │   └── res/                             # UI layouts, drawables
│   │   │       ├── layout/
│   │   │       │   ├── activity_main.xml
│   │   │       │   ├── fragment_home_with_tabs.xml
│   │   │       │   ├── activity_event_detail.xml
│   │   │       │   ├── fragment_event_list.xml
│   │   │       │   ├── item_event_list.xml
│   │   │       │   └── ...
│   │   │       └── ...
│   │   └── build.gradle                         # Dependencies
│   └── local.properties                         # API keys (gitignored)
│
├── backend/                            # Node.js REST API
│   ├── server.js                       # Express server
│   ├── init-database.js                # Database seeder
│   ├── database/
│   │   └── events.db                   # SQLite database (gitignored)
│   ├── package.json                    # Node dependencies
│   └── .env                            # Environment vars (gitignored)
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

### 1. Home - Event Discovery
- Browse events in **Personalized** and **Recommended** tabs
- Tap any event card to view full details
- Use the floating "+" button to quickly create events
- Bottom navigation always accessible

### 2. Event Details
- View complete event information
- Tap **Join Event** to participate
- Tap **View on Map** to see exact location
- Navigate using bottom navigation bar

### 3. Map View
- Explore events on interactive Google Maps
- Tap emoji markers to see event info windows
- Tap info windows to view full event details
- Auto-zooms when navigating from event details
- Search for events near specific locations

### 4. Add Event
- Fill in event details (title, location, description, time)
- Select event category from dropdown
- Choose date and time with pickers
- Set participant limits
- Submit to backend database

### 5. Profile/Community
- View your stats (events attended, events hosted, friends)
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
- `GET /api/events/nearby` - Find events near location
- `GET /api/stats` - Get system statistics

### Mock Data
The app includes:
- 20+ pre-seeded events in the database
- Mock health data for AI ranking
- Sample user profiles
- Various event categories and types

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

### Events Not Loading
- Verify backend is running and database is initialized
- Check Retrofit API configuration in `RetrofitClient.java`
- Check Android logs for API errors

### UI Elements Cut Off (Pixel 9)
- The app uses `fitsSystemWindows="true"` for proper spacing
- Should work correctly on devices with notches/punch holes

## 🔐 Security Notes

**Never commit these files:**
- `frontend/local.properties` - Contains API keys
- `backend/.env` - Contains secrets
- `backend/database/events.db` - Database file
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
  "location": "Marina Bay Sands, Singapore",
  "description": "Relaxing yoga session with sea views",
  "time": "Thu, Feb 15, 2024 • 8:00 AM - 9:30 AM",
  "currentParticipants": 5,
  "maxParticipants": 20,
  "eventType": "Yoga",
  "latitude": 1.2834,
  "longitude": 103.8607
}
```

### Create Event Request
```json
{
  "title": "Event Title",
  "location": "Location Name",
  "description": "Event description",
  "time": "Formatted time string",
  "currentParticipants": 1,
  "maxParticipants": 20,
  "eventType": "Soccer",
  "latitude": 1.2834,
  "longitude": 103.8607
}
```

## 📱 App Navigation Flow

```
MainActivity (Home)
├── HomeFragment (with tabs)
│   ├── EventListFragment (Personalized)
│   │   └── EventDetailActivity → MapsActivity
│   └── RecommendedEventsFragment
│       └── EventDetailActivity → MapsActivity
├── MapsActivity
│   └── Marker click → EventDetailActivity
├── AddEventActivity
├── ProfileActivity
└── AI Chatbot (Coming Soon)
```

## 🎨 UI/UX Features

- **Material Design 3**: Modern, clean interface
- **Bottom Navigation**: Persistent navigation across all screens
- **Floating Action Button**: Quick access to event creation
- **RecyclerView**: Smooth, efficient scrolling
- **Tab Layout**: Easy switching between event feeds
- **Custom Info Windows**: Rich map marker details
- **Responsive Layouts**: Works on all screen sizes
- **System Insets**: Proper handling of notches and navigation bars

## 🤝 Contributing

Contributions are welcome! This project aims to build a supportive community platform.

## 📄 License

Beyond Binary - Connecting people through shared experiences.

---

**Built with ❤️ to reduce isolation and build community**
