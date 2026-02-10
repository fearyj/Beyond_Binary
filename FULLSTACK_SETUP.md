# Full Stack Setup Guide - Beyond Binary

Complete guide to run the Beyond Binary event map application with backend API and Android frontend.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Android App (Frontend)                    │
│  - Google Maps with emoji markers                           │
│  - Location search                                           │
│  - Event filtering                                           │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/REST API
                       │ (Retrofit)
┌──────────────────────▼──────────────────────────────────────┐
│              Node.js Backend API (Express)                   │
│  - RESTful endpoints                                         │
│  - Event CRUD operations                                     │
│  - Nearby events search                                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                SQLite Database                               │
│  - 19 Singapore events                                       │
│  - Event details with coordinates                            │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start (3 Steps)

### Step 1: Start Backend (2 minutes)
```bash
cd backend
npm install
npm run init-db
npm start
```

✅ Backend running on http://localhost:3000

### Step 2: Configure Android App
Update your local IP for physical devices:
```bash
# Find your local IP
ifconfig | grep "inet "  # Mac/Linux
ipconfig                  # Windows
```

For emulator: Already configured (uses `10.0.2.2`)

### Step 3: Run Android App
```bash
cd frontend
# Open in Android Studio
# Click Run ▶️
```

## 📋 Detailed Setup

### Backend Setup

#### 1. Navigate to Backend
```bash
cd Beyond_Binary/backend
```

#### 2. Install Dependencies
```bash
npm install
```

This installs:
- Express (web framework)
- SQLite3 (database)
- CORS (cross-origin requests)
- dotenv (environment variables)

#### 3. Configure Environment
The `.env` file is already created with defaults:
```
PORT=3000
NODE_ENV=development
DATABASE_PATH=./database/events.db
ALLOWED_ORIGINS=*
```

#### 4. Initialize Database
```bash
npm run init-db
```

You should see:
```
✅ 1. Basketball Pickup Game (Basketball)
✅ 2. Weekend Soccer Match (Soccer)
...
✨ Successfully added 19 events to database!
```

#### 5. Start Server
```bash
npm start
```

You should see:
```
🚀 Beyond Binary API running on http://localhost:3000
📍 Environment: development
💾 Database: ./database/events.db

Available endpoints:
  GET    /api/health
  GET    /api/events
  ...
```

#### 6. Verify Backend is Running
Open browser or use curl:
```bash
curl http://localhost:3000/api/health
```

Response:
```json
{
  "status": "ok",
  "message": "Beyond Binary API is running",
  "timestamp": "2024-..."
}
```

### Frontend Setup

#### 1. API Configuration

**For Android Emulator (Default - Already Configured):**
```gradle
// frontend/app/build.gradle (already set)
buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:3000/api/\""
```

**For Physical Android Device:**

Find your local IP:
```bash
# Mac
ifconfig en0 | grep "inet "

# Linux
ip addr show

# Windows
ipconfig
```

Example output: `192.168.1.100`

Update `frontend/app/build.gradle`:
```gradle
buildConfigField "String", "API_BASE_URL", "\"http://192.168.1.100:3000/api/\""
```

⚠️ **Important**: Device must be on same WiFi network!

#### 2. Sync Gradle
In Android Studio:
- File → Sync Project with Gradle Files
- Wait for sync to complete

#### 3. Run the App
- Click Run button (green ▶️)
- Or press `Shift + F10`
- Select your device/emulator

## 🧪 Testing the Full Stack

### Test 1: Backend API
```bash
# Test health endpoint
curl http://localhost:3000/api/health

# Get all events
curl http://localhost:3000/api/events

# Get nearby events (Marina Bay)
curl "http://localhost:3000/api/events/nearby?latitude=1.2834&longitude=103.8607&radius=10"
```

### Test 2: Android App

**What You Should See:**
1. Map loads centered on Singapore
2. "Loading events from server..." message
3. "Loaded 19 events" message
4. 19 emoji markers appear on map
5. Click any marker → Event details popup

**Test Location Search:**
- Search "Marina Bay" → Shows nearby events
- Search "Sentosa" → Shows beach area events
- Search "Orchard Road" → Shows shopping area events

### Test 3: Create Event via API
```bash
curl -X POST http://localhost:3000/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Ping Pong Game",
    "location": "Tampines, Singapore",
    "description": "Friendly ping pong match!",
    "time": "Today, 7:00 PM",
    "currentParticipants": 2,
    "maxParticipants": 8,
    "eventType": "Ping Pong",
    "latitude": 1.3496,
    "longitude": 103.9568
  }'
```

Refresh Android app → New event appears!

## 🎯 Complete Workflow

### User Journey:
```
1. User opens app
   ↓
2. App fetches events from backend API
   ↓
3. Backend queries SQLite database
   ↓
4. Returns 19 Singapore events
   ↓
5. App displays emoji markers on map
   ↓
6. User clicks marker
   ↓
7. Event details popup appears
   ↓
8. User searches "Marina Bay"
   ↓
9. App calls /api/events/nearby
   ↓
10. Shows filtered events within 50km
```

## 🔧 Development Workflow

### Backend Development
```bash
cd backend
npm run dev  # Auto-restart on file changes
```

### Android Development
- Make changes in Android Studio
- Sync Gradle if needed
- Run to see changes

### Add New Event Type
1. Add emoji to Android `eventTypeEmojis` map
2. Create event in backend with new type
3. App automatically displays new emoji

## 🐛 Troubleshooting

### Problem: Android can't connect to backend

**Check 1: Backend Running?**
```bash
curl http://localhost:3000/api/health
```

**Check 2: Using Correct IP?**
- Emulator: Must use `10.0.2.2` (not localhost)
- Physical device: Use your local IP (e.g., `192.168.1.100`)

**Check 3: Same Network?**
- Phone and computer on same WiFi

**Check 4: Firewall?**
- Allow port 3000 in firewall

### Problem: No events showing

**Check Backend Logs:**
Look for:
```
GET /api/events 200 OK
```

**Check Android Logcat:**
Look for:
```
Loaded 19 events from API
```

**If seeing errors:**
```
Error connecting to server
```
→ Backend not reachable, check network/IP

### Problem: Events load but no markers

**Check Event Data:**
```bash
curl http://localhost:3000/api/events | json_pp
```

Verify each event has:
- `latitude` (not null)
- `longitude` (not null)

### Problem: Port 3000 already in use

**Find and kill process:**
```bash
# Mac/Linux
lsof -i :3000
kill -9 <PID>

# Or use different port
PORT=3001 npm start
```

Update Android to use new port.

## 📱 Device-Specific Setup

### Android Emulator
✅ Already configured! Uses `10.0.2.2:3000`

### Physical Android Device (Same WiFi)
1. Find your local IP: `ifconfig` (Mac) / `ipconfig` (Windows)
2. Update `app/build.gradle`:
   ```gradle
   buildConfigField "String", "API_BASE_URL", "\"http://YOUR_IP:3000/api/\""
   ```
3. Sync Gradle
4. Run app

### Physical Android Device (Different Network/Remote)

**Option A: ngrok (Temporary)**
```bash
# Install ngrok: https://ngrok.com/
ngrok http 3000
```

Use the ngrok URL in Android app:
```gradle
buildConfigField "String", "API_BASE_URL", "\"https://xyz.ngrok.io/api/\""
```

**Option B: Deploy Backend**
Deploy to Heroku/AWS/DigitalOcean and use production URL.

## 🚀 Production Deployment

### Backend Deployment

**Option 1: Heroku**
```bash
heroku create beyond-binary-api
git push heroku main
```

**Option 2: DigitalOcean/AWS**
- Deploy Node.js app
- Use PM2 for process management
- Configure HTTPS

**Update Android:**
```gradle
buildConfigField "String", "API_BASE_URL", "\"https://your-domain.com/api/\""
```

### Database Migration

For production, migrate from SQLite to PostgreSQL:
```javascript
// Use pg instead of sqlite3
const { Pool } = require('pg');
```

## 📊 Monitoring

### Backend Logs
```bash
# Show real-time logs
tail -f backend/logs/app.log

# Or if using PM2
pm2 logs
```

### Android Logs
```bash
# Via ADB
adb logcat | grep "MapsActivity"

# Or use Android Studio Logcat window
```

## 🔐 Security Checklist

- [ ] Backend running on localhost (dev) or HTTPS (prod)
- [ ] CORS properly configured
- [ ] API rate limiting (production)
- [ ] Input validation
- [ ] SQL injection prevention (✅ using parameterized queries)
- [ ] HTTPS for production
- [ ] Authentication (if needed)

## ✅ Success Checklist

- [ ] Backend starts without errors
- [ ] Database has 19 events
- [ ] API health check returns 200 OK
- [ ] Android app connects to backend
- [ ] Events load and display on map
- [ ] Location search works
- [ ] Event markers clickable
- [ ] Info windows show event details

## 📖 Next Steps

1. ✅ **Basic Setup Working** - You're here!
2. ⏭️ **Add User Authentication**
3. ⏭️ **Allow users to create events from app**
4. ⏭️ **Add event categories filter**
5. ⏭️ **Implement event RSVP**
6. ⏭️ **Push notifications for nearby events**
7. ⏭️ **Deploy to production**

## 🆘 Need Help?

**Backend Issues:**
- Check `backend/README.md`
- View backend logs
- Test with curl/Postman

**Android Issues:**
- Check `frontend/README.md`
- View Logcat in Android Studio
- Verify API_BASE_URL

**Both Not Working:**
- Restart backend: `npm restart`
- Clean Android: Build → Clean Project → Rebuild
- Check network connectivity

---

**You're all set!** 🎉

Backend: http://localhost:3000
Android: Running on your device with live data from Singapore! 🇸🇬
