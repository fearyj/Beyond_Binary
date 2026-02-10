const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const fs = require('fs');
require('dotenv').config();

const DB_PATH = process.env.DATABASE_PATH || './database/events.db';

// Ensure database directory exists
const dbDir = path.dirname(DB_PATH);
if (!fs.existsSync(dbDir)) {
    fs.mkdirSync(dbDir, { recursive: true });
}

const db = new sqlite3.Database(DB_PATH);

// Singapore sample events with coordinates
const singaporeEvents = [
    // Sports Events
    {
        title: 'Basketball Pickup Game',
        location: 'East Coast Park, Singapore',
        description: 'Casual basketball game at the outdoor courts. All skill levels welcome!',
        time: 'Saturday, 5:00 PM',
        currentParticipants: 6,
        maxParticipants: 12,
        eventType: 'Basketball',
        latitude: 1.3008,
        longitude: 103.9128
    },
    {
        title: 'Weekend Soccer Match',
        location: 'Marina Bay Sands, Singapore',
        description: 'Friendly soccer match at Marina Bay. Looking for more players!',
        time: 'Saturday, 4:00 PM',
        currentParticipants: 14,
        maxParticipants: 22,
        eventType: 'Soccer',
        latitude: 1.2834,
        longitude: 103.8607
    },
    {
        title: 'Ping Pong Tournament',
        location: 'Toa Payoh HDB Hub, Singapore',
        description: 'Casual ping pong tournament with prizes. All levels welcome!',
        time: 'Friday, 7:00 PM',
        currentParticipants: 10,
        maxParticipants: 16,
        eventType: 'Ping Pong',
        latitude: 1.3326,
        longitude: 103.8476
    },
    {
        title: 'Sunrise Yoga Session',
        location: 'Gardens by the Bay, Singapore',
        description: 'Morning yoga in the beautiful gardens. Bring your own mat!',
        time: 'Sunday, 6:30 AM',
        currentParticipants: 15,
        maxParticipants: 25,
        eventType: 'Yoga',
        latitude: 1.2816,
        longitude: 103.8636
    },
    {
        title: 'Morning Run Group',
        location: 'MacRitchie Reservoir, Singapore',
        description: 'Join us for a refreshing morning run around the reservoir!',
        time: 'Sunday, 7:00 AM',
        currentParticipants: 8,
        maxParticipants: 20,
        eventType: 'Running',
        latitude: 1.3494,
        longitude: 103.8279
    },

    // Social Events
    {
        title: 'Coffee & Chat Meetup',
        location: 'Tiong Bahru, Singapore',
        description: 'Meet new people at trendy Tiong Bahru cafes. Great conversations guaranteed!',
        time: 'Sunday, 10:00 AM',
        currentParticipants: 10,
        maxParticipants: 18,
        eventType: 'Coffee',
        latitude: 1.2860,
        longitude: 103.8267
    },
    {
        title: 'Board Game Night',
        location: 'Orchard Road, Singapore',
        description: 'Bring your favorite board games or try new ones. Beginners welcome!',
        time: 'Thursday, 7:00 PM',
        currentParticipants: 12,
        maxParticipants: 20,
        eventType: 'Board Games',
        latitude: 1.3048,
        longitude: 103.8318
    },
    {
        title: 'Movie Night at Rooftop',
        location: 'Clarke Quay, Singapore',
        description: 'Outdoor movie screening by the riverside. Bring cushions!',
        time: 'Friday, 8:00 PM',
        currentParticipants: 20,
        maxParticipants: 40,
        eventType: 'Movie',
        latitude: 1.2897,
        longitude: 103.8467
    },

    // Learning Events
    {
        title: 'Book Club Discussion',
        location: 'National Library, Singapore',
        description: 'Monthly book club discussing local and international literature. All welcome!',
        time: 'Friday, 7:00 PM',
        currentParticipants: 10,
        maxParticipants: 18,
        eventType: 'Book Club',
        latitude: 1.2975,
        longitude: 103.8543
    },
    {
        title: 'Mandarin Language Exchange',
        location: 'Chinatown, Singapore',
        description: 'Practice Mandarin with native speakers. All levels welcome!',
        time: 'Wednesday, 6:30 PM',
        currentParticipants: 8,
        maxParticipants: 15,
        eventType: 'Language Exchange',
        latitude: 1.2827,
        longitude: 103.8443
    },

    // Dining Events
    {
        title: 'Hawker Centre Food Tour',
        location: 'Maxwell Food Centre, Singapore',
        description: 'Explore the best local hawker food together! Try famous chicken rice.',
        time: 'Saturday, 12:00 PM',
        currentParticipants: 8,
        maxParticipants: 15,
        eventType: 'Lunch',
        latitude: 1.2806,
        longitude: 103.8445
    },
    {
        title: 'BBQ & Beach Gathering',
        location: 'Sentosa Beach, Singapore',
        description: 'Beach BBQ at Sentosa! Bring food to share and enjoy the sunset.',
        time: 'Sunday, 4:00 PM',
        currentParticipants: 20,
        maxParticipants: 35,
        eventType: 'BBQ',
        latitude: 1.2494,
        longitude: 103.8303
    },
    {
        title: 'Dinner at Marina Bay',
        location: 'Marina Bay Waterfront, Singapore',
        description: 'Group dinner with stunning views of Marina Bay Sands!',
        time: 'Saturday, 7:00 PM',
        currentParticipants: 10,
        maxParticipants: 16,
        eventType: 'Dinner',
        latitude: 1.2835,
        longitude: 103.8591
    },

    // Arts Events
    {
        title: 'Watercolor Painting Workshop',
        location: 'National Gallery Singapore',
        description: 'Beginner-friendly watercolor class. All materials provided!',
        time: 'Wednesday, 6:00 PM',
        currentParticipants: 12,
        maxParticipants: 18,
        eventType: 'Painting',
        latitude: 1.2903,
        longitude: 103.8519
    },
    {
        title: 'Photography Walk',
        location: 'Merlion Park, Singapore',
        description: 'Golden hour photography session at iconic Singapore landmarks!',
        time: 'Saturday, 6:00 PM',
        currentParticipants: 10,
        maxParticipants: 15,
        eventType: 'Photography',
        latitude: 1.2868,
        longitude: 103.8545
    },
    {
        title: 'Museum Tour & Discussion',
        location: 'ArtScience Museum, Singapore',
        description: 'Guided tour of latest exhibits followed by group discussion.',
        time: 'Sunday, 2:00 PM',
        currentParticipants: 8,
        maxParticipants: 12,
        eventType: 'Museum',
        latitude: 1.2863,
        longitude: 103.8593
    },

    // Outdoor Events
    {
        title: 'Hiking at Bukit Timah',
        location: 'Bukit Timah Nature Reserve, Singapore',
        description: 'Nature hike through Singapore\'s rainforest. Great workout with city views!',
        time: 'Sunday, 8:00 AM',
        currentParticipants: 10,
        maxParticipants: 20,
        eventType: 'Hiking',
        latitude: 1.3544,
        longitude: 103.7761
    },
    {
        title: 'Cycling at East Coast',
        location: 'East Coast Park, Singapore',
        description: 'Leisure cycling along the beach. Rent bikes available nearby!',
        time: 'Saturday, 7:00 AM',
        currentParticipants: 8,
        maxParticipants: 15,
        eventType: 'Cycling',
        latitude: 1.3016,
        longitude: 103.9189
    },
    {
        title: 'Beach Volleyball & Chill',
        location: 'Palawan Beach, Sentosa, Singapore',
        description: 'Beach volleyball games and relaxing by the sea!',
        time: 'Sunday, 3:00 PM',
        currentParticipants: 12,
        maxParticipants: 20,
        eventType: 'Beach',
        latitude: 1.2510,
        longitude: 103.8297
    }
];

console.log('🗄️  Initializing Beyond Binary Database...\n');

db.serialize(() => {
    // Create table
    console.log('Creating events table...');
    db.run(`
        CREATE TABLE IF NOT EXISTS events (
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
    `);

    // Clear existing data
    console.log('Clearing existing events...');
    db.run('DELETE FROM events');

    // Insert Singapore events
    console.log('Inserting Singapore events...\n');
    const insertStmt = db.prepare(`
        INSERT INTO events (
            title, location, description, time,
            currentParticipants, maxParticipants, eventType,
            latitude, longitude
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    `);

    singaporeEvents.forEach((event, index) => {
        insertStmt.run(
            event.title,
            event.location,
            event.description,
            event.time,
            event.currentParticipants,
            event.maxParticipants,
            event.eventType,
            event.latitude,
            event.longitude,
            (err) => {
                if (err) {
                    console.error(`❌ Error inserting event ${index + 1}:`, err);
                } else {
                    console.log(`✅ ${index + 1}. ${event.title} (${event.eventType})`);
                }
            }
        );
    });

    insertStmt.finalize();

    // Verify insertion
    db.get('SELECT COUNT(*) as count FROM events', (err, row) => {
        if (err) {
            console.error('Error counting events:', err);
        } else {
            console.log(`\n✨ Successfully added ${row.count} events to database!`);
            console.log(`📍 Database location: ${DB_PATH}`);
            console.log('\n🚀 You can now start the server with: npm start\n');
        }

        db.close();
    });
});
