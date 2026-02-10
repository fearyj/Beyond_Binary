# Beyond Binary Backend API

Node.js + Express + SQLite backend for the Beyond Binary event map application.

## 🚀 Quick Start

### 1. Install Dependencies
```bash
cd backend
npm install
```

### 2. Initialize Database
```bash
npm run init-db
```

This creates the SQLite database and populates it with 19 Singapore events.

### 3. Start the Server
```bash
npm start
```

Server will run on: `http://localhost:3000`

For development (auto-restart on changes):
```bash
npm run dev
```

## 📋 API Endpoints

### Health Check
```
GET /api/health
```
Returns server status and timestamp.

### Get All Events
```
GET /api/events
```

Query parameters:
- `eventType` (optional) - Filter by event type
- `limit` (optional) - Limit number of results

Response:
```json
{
  "success": true,
  "count": 19,
  "events": [...]
}
```

### Get Event by ID
```
GET /api/events/:id
```

Response:
```json
{
  "success": true,
  "event": {...}
}
```

### Get Nearby Events
```
GET /api/events/nearby?latitude=1.3521&longitude=103.8198&radius=50
```

Parameters:
- `latitude` (required) - Center latitude
- `longitude` (required) - Center longitude
- `radius` (optional, default: 50) - Search radius in kilometers

### Create Event
```
POST /api/events
Content-Type: application/json

{
  "title": "New Event",
  "location": "Marina Bay, Singapore",
  "description": "Event description",
  "time": "Saturday, 5:00 PM",
  "currentParticipants": 0,
  "maxParticipants": 10,
  "eventType": "Soccer",
  "latitude": 1.2834,
  "longitude": 103.8607
}
```

### Update Event
```
PUT /api/events/:id
Content-Type: application/json

{
  "title": "Updated Title",
  ...
}
```

### Delete Event
```
DELETE /api/events/:id
```

### Get Statistics
```
GET /api/stats
```

Returns:
```json
{
  "success": true,
  "stats": {
    "totalEvents": 19,
    "eventTypes": 15,
    "totalParticipants": 185,
    "avgOccupancy": 67.5
  }
}
```

## 🗄️ Database Schema

SQLite database located at: `./database/events.db`

### Events Table
```sql
CREATE TABLE events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    location TEXT NOT NULL,
    description TEXT,
    time TEXT,
    currentParticipants INTEGER DEFAULT 0,
    maxParticipants INTEGER DEFAULT 10,
    eventType TEXT NOT NULL,
    latitude REAL,
    longitude REAL,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
)
```

## 📍 Singapore Sample Events

The database is pre-populated with 19 events across Singapore:

**Sports & Fitness (5):**
- Basketball at East Coast Park
- Soccer at Marina Bay Sands
- Ping Pong at Toa Payoh HDB Hub
- Yoga at Gardens by the Bay
- Running at MacRitchie Reservoir

**Social (3):**
- Coffee at Tiong Bahru
- Board Games at Orchard Road
- Movie at Clarke Quay

**Learning (2):**
- Book Club at National Library
- Language Exchange at Chinatown

**Dining (3):**
- Hawker Tour at Maxwell Food Centre
- BBQ at Sentosa Beach
- Dinner at Marina Bay Waterfront

**Arts (3):**
- Painting at National Gallery
- Photography at Merlion Park
- Museum at ArtScience Museum

**Outdoor (3):**
- Hiking at Bukit Timah Nature Reserve
- Cycling at East Coast Park
- Beach Volleyball at Palawan Beach

## 🔧 Configuration

### Environment Variables (.env)
```
PORT=3000
NODE_ENV=development
DATABASE_PATH=./database/events.db
ALLOWED_ORIGINS=*
```

## 🧪 Testing

### Using cURL

**Get all events:**
```bash
curl http://localhost:3000/api/events
```

**Get nearby events:**
```bash
curl "http://localhost:3000/api/events/nearby?latitude=1.3521&longitude=103.8198&radius=50"
```

**Create event:**
```bash
curl -X POST http://localhost:3000/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Event",
    "location": "Singapore",
    "description": "Test",
    "time": "Now",
    "eventType": "Soccer",
    "latitude": 1.3521,
    "longitude": 103.8198
  }'
```

### Using Postman

1. Import the API endpoints
2. Base URL: `http://localhost:3000/api`
3. Test each endpoint

## 🔄 Database Management

### Reset Database
```bash
rm -rf database/
npm run init-db
```

### Backup Database
```bash
cp database/events.db database/events_backup.db
```

### View Database
```bash
sqlite3 database/events.db
sqlite> SELECT * FROM events;
sqlite> .exit
```

## 📱 Android App Connection

The Android app connects to:
- **Emulator**: `http://10.0.2.2:3000/api/`
- **Physical Device**: `http://YOUR_LOCAL_IP:3000/api/`

To find your local IP:
```bash
# Mac/Linux
ifconfig | grep "inet "

# Windows
ipconfig
```

Update `frontend/app/build.gradle`:
```gradle
buildConfigField "String", "API_BASE_URL", "\"http://192.168.1.100:3000/api/\""
```

## 🛡️ CORS

CORS is enabled for all origins in development. For production, restrict to your app's domain:

```javascript
// server.js
app.use(cors({
  origin: ['https://yourdomain.com']
}));
```

## 🚨 Troubleshooting

### Port already in use
```bash
# Find process using port 3000
lsof -i :3000

# Kill process
kill -9 <PID>
```

### Database locked
```bash
# Close all connections and restart
rm database/events.db
npm run init-db
```

### Android app can't connect
1. Check backend is running: `curl http://localhost:3000/api/health`
2. For emulator, use `10.0.2.2` not `localhost`
3. For physical device, ensure same WiFi network
4. Check firewall allows port 3000

## 📊 Monitoring

Logs show:
- Incoming requests
- Response codes
- Errors
- Database operations

Example output:
```
🚀 Beyond Binary API running on http://localhost:3000
📍 Environment: development
💾 Database: ./database/events.db

Available endpoints:
  GET    /api/health
  GET    /api/events
  POST   /api/events
  ...
```

## 🔐 Security Notes

**For Development:**
- Uses SQLite (file-based database)
- CORS enabled for all origins
- No authentication required

**For Production:**
- Migrate to PostgreSQL/MySQL
- Add authentication (JWT)
- Restrict CORS origins
- Use HTTPS
- Add rate limiting
- Validate all inputs

## 📝 License

MIT
