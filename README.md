# Beyond Binary

A community-driven Android application designed to help isolated individuals connect with others through local events and activities.

## 🎯 Mission

Beyond Binary creates a platform where people can discover and join events ranging from sports and social gatherings to reading clubs, dining experiences, arts activities, and outdoor adventures.

## 🗺️ Features

- **Interactive Event Map** with custom emoji markers for specific activities
- **70+ Specific Event Types** including:
  - 🏀 Sports: Soccer, Basketball, Ping Pong, Tennis, Yoga, Running, and more
  - 🥾 Outdoor: Hiking, Camping, Rock Climbing, Fishing, Beach activities
  - ☕ Social: Coffee meetups, Board Games, Parties, Karaoke, Movie nights
  - 🍣 Dining: Sushi, BBQ, Pizza, Cooking Classes, Wine Tasting
  - 🎨 Arts: Painting, Photography, Museum visits, Theater, Dance
  - 📚 Learning: Book Clubs, Study Groups, Language Exchange, Workshops
  - And many more!
- **Detailed Event Information** showing title, location, time, participant count, and description
- **Local Database** using Room for offline event storage
- **Address Geocoding** automatically converts addresses to map coordinates

## 🚀 Quick Start

### Frontend (Android App)

1. Navigate to the frontend directory: `cd frontend`
2. Get a [Google Maps API Key](https://console.cloud.google.com/)
3. Add your API key to `app/src/main/AndroidManifest.xml`
4. Open in Android Studio and run

📖 **Detailed setup instructions**: See [frontend/README.md](frontend/README.md)

### Backend

Backend implementation coming soon.

## 📁 Project Structure

```
Beyond_Binary/
├── frontend/          # Android application
│   ├── app/
│   │   └── src/main/
│   │       ├── java/com/beyondbinary/eventapp/
│   │       │   ├── MapsActivity.java      # Main map functionality
│   │       │   ├── Event.java             # Event data model
│   │       │   └── ...
│   │       └── res/
│   └── README.md     # Detailed Android setup guide
└── backend/          # Backend services (coming soon)
```

## 🛠️ Technologies

- **Android SDK** (Java)
- **Google Maps API** for map visualization
- **Room Database** for local data persistence
- **Material Design** components

## 📝 Key Functionality

The core function `loadEventsAndDisplayOnMap()` in MapsActivity:
1. Fetches events from the local database
2. Geocodes event addresses to coordinates
3. Creates custom emoji markers based on event type
4. Displays interactive markers on the map
5. Shows detailed event info when markers are clicked

## 🤝 Contributing

This project aims to build a supportive community platform. Contributions are welcome!

## 📄 License

Beyond Binary - Connecting people through shared experiences.