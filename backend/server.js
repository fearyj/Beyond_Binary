const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;
const DB_PATH = process.env.DATABASE_PATH || './database/events.db';

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Database connection
const db = new sqlite3.Database(DB_PATH, (err) => {
    if (err) {
        console.error('Error connecting to database:', err);
    } else {
        console.log('Connected to SQLite database');
        initializeDatabase();
    }
});

// Initialize database schema
function initializeDatabase() {
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
    `, (err) => {
        if (err) {
            console.error('Error creating events table:', err);
        } else {
            console.log('Events table ready');
        }
    });

    // Add creatorUserId column to events (ignore error if already exists)
    db.run('ALTER TABLE events ADD COLUMN creatorUserId INTEGER', (err) => {
        if (err && !err.message.includes('duplicate column')) {
            console.error('Error adding creatorUserId column:', err);
        }
    });

    db.run(`
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT UNIQUE NOT NULL,
            bio TEXT,
            interest_tags TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    `, (err) => {
        if (err) {
            console.error('Error creating users table:', err);
        } else {
            console.log('Users table ready');
        }
    });

    db.run(`
        CREATE TABLE IF NOT EXISTS user_interactions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            event_id INTEGER NOT NULL,
            interaction_type TEXT NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id),
            FOREIGN KEY (event_id) REFERENCES events(id)
        )
    `, (err) => {
        if (err) {
            console.error('Error creating user_interactions table:', err);
        } else {
            console.log('User interactions table ready');
        }
    });
}

// ==================== API ROUTES ====================

// Health check
app.get('/api/health', (req, res) => {
    res.json({
        status: 'ok',
        message: 'Beyond Binary API is running',
        timestamp: new Date().toISOString()
    });
});

// Get all events
app.get('/api/events', (req, res) => {
    const { eventType, limit } = req.query;

    let query = 'SELECT * FROM events WHERE 1=1';
    const params = [];

    if (eventType) {
        query += ' AND eventType = ?';
        params.push(eventType);
    }

    query += ' ORDER BY createdAt DESC';

    if (limit) {
        query += ' LIMIT ?';
        params.push(parseInt(limit));
    }

    db.all(query, params, (err, rows) => {
        if (err) {
            console.error('Error fetching events:', err);
            res.status(500).json({ error: 'Failed to fetch events' });
        } else {
            res.json({
                success: true,
                count: rows.length,
                events: rows
            });
        }
    });
});

// Get event by ID
app.get('/api/events/:id', (req, res) => {
    const { id } = req.params;

    db.get('SELECT * FROM events WHERE id = ?', [id], (err, row) => {
        if (err) {
            console.error('Error fetching event:', err);
            res.status(500).json({ error: 'Failed to fetch event' });
        } else if (!row) {
            res.status(404).json({ error: 'Event not found' });
        } else {
            res.json({
                success: true,
                event: row
            });
        }
    });
});

// Get events by location (within radius)
app.get('/api/events/nearby', (req, res) => {
    const { latitude, longitude, radius = 50 } = req.query;

    if (!latitude || !longitude) {
        return res.status(400).json({ error: 'Latitude and longitude required' });
    }

    db.all('SELECT * FROM events WHERE latitude IS NOT NULL AND longitude IS NOT NULL',
        (err, rows) => {
            if (err) {
                console.error('Error fetching events:', err);
                res.status(500).json({ error: 'Failed to fetch events' });
            } else {
                // Filter by distance
                const nearbyEvents = rows.filter(event => {
                    const distance = calculateDistance(
                        parseFloat(latitude),
                        parseFloat(longitude),
                        event.latitude,
                        event.longitude
                    );
                    return distance <= parseFloat(radius);
                });

                res.json({
                    success: true,
                    count: nearbyEvents.length,
                    radius: parseFloat(radius),
                    events: nearbyEvents
                });
            }
        }
    );
});

// Create new event
app.post('/api/events', (req, res) => {
    const {
        title,
        location,
        description,
        time,
        currentParticipants = 0,
        maxParticipants = 10,
        eventType,
        latitude,
        longitude,
        creatorUserId
    } = req.body;

    if (!title || !location || !eventType) {
        return res.status(400).json({
            error: 'Title, location, and eventType are required'
        });
    }

    const query = `
        INSERT INTO events (
            title, location, description, time,
            currentParticipants, maxParticipants, eventType,
            latitude, longitude, creatorUserId
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `;

    db.run(
        query,
        [title, location, description, time, currentParticipants,
         maxParticipants, eventType, latitude, longitude, creatorUserId || null],
        function(err) {
            if (err) {
                console.error('Error creating event:', err);
                res.status(500).json({ error: 'Failed to create event' });
            } else {
                res.status(201).json({
                    success: true,
                    message: 'Event created successfully',
                    eventId: this.lastID
                });
            }
        }
    );
});

// Update event
app.put('/api/events/:id', (req, res) => {
    const { id } = req.params;
    const {
        title,
        location,
        description,
        time,
        currentParticipants,
        maxParticipants,
        eventType,
        latitude,
        longitude
    } = req.body;

    const query = `
        UPDATE events SET
            title = COALESCE(?, title),
            location = COALESCE(?, location),
            description = COALESCE(?, description),
            time = COALESCE(?, time),
            currentParticipants = COALESCE(?, currentParticipants),
            maxParticipants = COALESCE(?, maxParticipants),
            eventType = COALESCE(?, eventType),
            latitude = COALESCE(?, latitude),
            longitude = COALESCE(?, longitude)
        WHERE id = ?
    `;

    db.run(
        query,
        [title, location, description, time, currentParticipants,
         maxParticipants, eventType, latitude, longitude, id],
        function(err) {
            if (err) {
                console.error('Error updating event:', err);
                res.status(500).json({ error: 'Failed to update event' });
            } else if (this.changes === 0) {
                res.status(404).json({ error: 'Event not found' });
            } else {
                res.json({
                    success: true,
                    message: 'Event updated successfully'
                });
            }
        }
    );
});

// Delete event
app.delete('/api/events/:id', (req, res) => {
    const { id } = req.params;

    db.run('DELETE FROM events WHERE id = ?', [id], function(err) {
        if (err) {
            console.error('Error deleting event:', err);
            res.status(500).json({ error: 'Failed to delete event' });
        } else if (this.changes === 0) {
            res.status(404).json({ error: 'Event not found' });
        } else {
            res.json({
                success: true,
                message: 'Event deleted successfully'
            });
        }
    });
});

// ==================== USER ROUTES ====================

// Register user (or return existing)
app.post('/api/users', (req, res) => {
    const { email } = req.body;

    if (!email) {
        return res.status(400).json({ error: 'Email is required' });
    }

    // Check if user already exists
    db.get('SELECT * FROM users WHERE email = ?', [email], (err, existing) => {
        if (err) {
            console.error('Error checking user:', err);
            return res.status(500).json({ error: 'Failed to check user' });
        }

        if (existing) {
            return res.json({
                success: true,
                message: 'User already exists',
                userId: existing.id,
                user: existing
            });
        }

        // Create new user
        db.run('INSERT INTO users (email) VALUES (?)', [email], function(err) {
            if (err) {
                console.error('Error creating user:', err);
                return res.status(500).json({ error: 'Failed to create user' });
            }

            res.status(201).json({
                success: true,
                message: 'User created successfully',
                userId: this.lastID
            });
        });
    });
});

// Get user by ID
app.get('/api/users/:id', (req, res) => {
    const { id } = req.params;

    db.get('SELECT * FROM users WHERE id = ?', [id], (err, user) => {
        if (err) {
            console.error('Error fetching user:', err);
            return res.status(500).json({ error: 'Failed to fetch user' });
        }

        if (!user) {
            return res.status(404).json({ error: 'User not found' });
        }

        res.json({ success: true, user });
    });
});

// Update user profile
app.put('/api/users/:id', (req, res) => {
    const { id } = req.params;
    const { bio, interest_tags } = req.body;

    db.run(
        'UPDATE users SET bio = COALESCE(?, bio), interest_tags = COALESCE(?, interest_tags) WHERE id = ?',
        [bio, interest_tags, id],
        function(err) {
            if (err) {
                console.error('Error updating user:', err);
                return res.status(500).json({ error: 'Failed to update user' });
            }

            if (this.changes === 0) {
                return res.status(404).json({ error: 'User not found' });
            }

            // Return updated user
            db.get('SELECT * FROM users WHERE id = ?', [id], (err, user) => {
                if (err) {
                    return res.status(500).json({ error: 'Failed to fetch updated user' });
                }
                res.json({ success: true, user });
            });
        }
    );
});

// ==================== INTERACTION ROUTES ====================

// Track user-event interaction
app.post('/api/interactions', (req, res) => {
    const { user_id, event_id, interaction_type } = req.body;

    if (!user_id || !event_id || !interaction_type) {
        return res.status(400).json({ error: 'user_id, event_id, and interaction_type are required' });
    }

    db.run(
        'INSERT INTO user_interactions (user_id, event_id, interaction_type) VALUES (?, ?, ?)',
        [user_id, event_id, interaction_type],
        function(err) {
            if (err) {
                console.error('Error creating interaction:', err);
                return res.status(500).json({ error: 'Failed to create interaction' });
            }

            res.status(201).json({
                success: true,
                interactionId: this.lastID
            });
        }
    );
});

// Get user's interaction history
app.get('/api/interactions/:userId', (req, res) => {
    const { userId } = req.params;

    db.all(
        `SELECT ui.id, ui.user_id, ui.event_id, ui.interaction_type, ui.created_at,
                e.title, e.eventType
         FROM user_interactions ui
         LEFT JOIN events e ON ui.event_id = e.id
         WHERE ui.user_id = ?
         ORDER BY ui.created_at DESC`,
        [userId],
        (err, rows) => {
            if (err) {
                console.error('Error fetching interactions:', err);
                return res.status(500).json({ error: 'Failed to fetch interactions' });
            }

            res.json({ success: true, interactions: rows });
        }
    );
});

// ==================== STATS ROUTES ====================

// Get event statistics
app.get('/api/stats', (req, res) => {
    db.get(`
        SELECT
            COUNT(*) as totalEvents,
            COUNT(DISTINCT eventType) as eventTypes,
            SUM(currentParticipants) as totalParticipants,
            AVG(currentParticipants * 100.0 / maxParticipants) as avgOccupancy
        FROM events
    `, (err, stats) => {
        if (err) {
            console.error('Error fetching stats:', err);
            res.status(500).json({ error: 'Failed to fetch statistics' });
        } else {
            res.json({
                success: true,
                stats: stats
            });
        }
    });
});

// ==================== HELPER FUNCTIONS ====================

// Calculate distance between two coordinates (in kilometers)
function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // Earth's radius in km
    const dLat = toRadians(lat2 - lat1);
    const dLon = toRadians(lon2 - lon1);

    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
              Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
}

function toRadians(degrees) {
    return degrees * (Math.PI / 180);
}

// ==================== START SERVER ====================

app.listen(PORT, () => {
    console.log(`🚀 Beyond Binary API running on http://localhost:${PORT}`);
    console.log(`📍 Environment: ${process.env.NODE_ENV}`);
    console.log(`💾 Database: ${DB_PATH}`);
    console.log(`\n Available endpoints:`);
    console.log(`  GET    /api/health`);
    console.log(`  GET    /api/events`);
    console.log(`  GET    /api/events/:id`);
    console.log(`  GET    /api/events/nearby?latitude=&longitude=&radius=`);
    console.log(`  POST   /api/events`);
    console.log(`  PUT    /api/events/:id`);
    console.log(`  DELETE /api/events/:id`);
    console.log(`  POST   /api/users`);
    console.log(`  GET    /api/users/:id`);
    console.log(`  PUT    /api/users/:id`);
    console.log(`  POST   /api/interactions`);
    console.log(`  GET    /api/interactions/:userId`);
    console.log(`  GET    /api/stats`);
});

// Graceful shutdown
process.on('SIGINT', () => {
    console.log('\nClosing database connection...');
    db.close((err) => {
        if (err) {
            console.error('Error closing database:', err);
        } else {
            console.log('Database connection closed');
        }
        process.exit(0);
    });
});
