const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const fs = require('fs');
const bcrypt = require('bcrypt');
const multer = require('multer');
require('dotenv').config();

const { initializeVectorStore, reindexEvents } = require('./chatbot/vectorStore');
const { createChatbotGraph } = require('./chatbot/graph');

const app = express();
const PORT = process.env.PORT || 3000;
const DB_PATH = process.env.DATABASE_PATH || './database/events.db';
let chatbotGraph = null;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Serve uploaded photos as static files
const uploadsDir = path.join(__dirname, 'public', 'uploads');
if (!fs.existsSync(uploadsDir)) {
    fs.mkdirSync(uploadsDir, { recursive: true });
}
app.use('/uploads', express.static(uploadsDir));

// Multer storage configuration for event photo uploads
const storage = multer.diskStorage({
    destination: (req, file, cb) => cb(null, uploadsDir),
    filename: (req, file, cb) => {
        const uniqueName = `event_${req.params.id}_${Date.now()}${path.extname(file.originalname)}`;
        cb(null, uniqueName);
    }
});
const upload = multer({ storage, limits: { fileSize: 10 * 1024 * 1024 } });

// Database connection
const db = new sqlite3.Database(DB_PATH, (err) => {
    if (err) {
        console.error('Error connecting to database:', err);
    } else {
        console.log('Connected to SQLite database');
        initializeDatabase();
        // Initialize chatbot vector store and graph after a short delay for DB setup
        setTimeout(async () => {
            try {
                await initializeVectorStore(db);
                chatbotGraph = createChatbotGraph(db);
                console.log('Chatbot vector store and graph initialized');
            } catch (e) {
                console.error('Error initializing chatbot:', e);
            }
        }, 2000);
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
            password_hash TEXT,
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

    // Add password_hash column for existing databases (ignore if already exists)
    db.run('ALTER TABLE users ADD COLUMN password_hash TEXT', (err) => {
        if (err && !err.message.includes('duplicate column')) {
            console.error('Error adding password_hash column:', err);
        }
    });

    // Add profile setup columns (ignore if already exists)
    db.run('ALTER TABLE users ADD COLUMN username TEXT', (err) => {
        if (err && !err.message.includes('duplicate column')) {
            console.error('Error adding username column:', err);
        }
    });
    db.run('ALTER TABLE users ADD COLUMN dob TEXT', (err) => {
        if (err && !err.message.includes('duplicate column')) {
            console.error('Error adding dob column:', err);
        }
    });
    db.run('ALTER TABLE users ADD COLUMN address TEXT', (err) => {
        if (err && !err.message.includes('duplicate column')) {
            console.error('Error adding address column:', err);
        }
    });
    db.run('ALTER TABLE users ADD COLUMN caption TEXT', (err) => {
        if (err && !err.message.includes('duplicate column')) {
            console.error('Error adding caption column:', err);
        }
    });

    // Migrate existing users with NULL password_hash to default password
    bcrypt.hash('password123', 10, (err, hash) => {
        if (err) {
            console.error('Error hashing default password:', err);
            return;
        }
        db.run('UPDATE users SET password_hash = ? WHERE password_hash IS NULL', [hash], function(err) {
            if (err) {
                console.error('Error migrating existing users:', err);
            } else if (this.changes > 0) {
                console.log(`Migrated ${this.changes} existing user(s) with default password`);
            }
        });
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

    db.run(`
        CREATE TABLE IF NOT EXISTS messages (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            sender_id INTEGER NOT NULL,
            receiver_id INTEGER NOT NULL,
            text TEXT,
            type TEXT DEFAULT 'text',
            event_id INTEGER,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (sender_id) REFERENCES users(id),
            FOREIGN KEY (receiver_id) REFERENCES users(id),
            FOREIGN KEY (event_id) REFERENCES events(id)
        )
    `, (err) => {
        if (err) {
            console.error('Error creating messages table:', err);
        } else {
            console.log('Messages table ready');
        }
    });

    db.run(`
        CREATE TABLE IF NOT EXISTS event_photos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            event_id INTEGER NOT NULL,
            user_id INTEGER NOT NULL,
            image_url TEXT NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (event_id) REFERENCES events(id),
            FOREIGN KEY (user_id) REFERENCES users(id)
        )
    `, (err) => {
        if (err) {
            console.error('Error creating event_photos table:', err);
        } else {
            console.log('Event photos table ready');
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
                reindexEvents(db);
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
                reindexEvents(db);
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
            reindexEvents(db);
        }
    });
});

// ==================== CHATBOT ROUTES ====================

// Chatbot chat endpoint
app.post('/api/chatbot/chat', async (req, res) => {
    const { message, userId, conversationHistory } = req.body;

    if (!message) {
        return res.status(400).json({ error: 'Message is required' });
    }

    if (!chatbotGraph) {
        return res.status(503).json({
            type: 'text',
            message: 'Chatbot is still initializing. Please try again in a moment.'
        });
    }

    try {
        const result = await chatbotGraph.invoke({
            userMessage: message,
            userId: userId || null,
            conversationHistory: conversationHistory || [],
        });

        res.json(result.finalResponse || { type: 'text', message: 'Sorry, I had trouble processing that.' });
    } catch (e) {
        console.error('Chatbot error:', e);
        res.status(500).json({
            type: 'text',
            message: 'Sorry, I encountered an error. Please try again.'
        });
    }
});

// ==================== USER ROUTES ====================

// Register user
app.post('/api/users', (req, res) => {
    const { email, password } = req.body;

    if (!email) {
        return res.status(400).json({ error: 'Email is required' });
    }

    if (!password || password.length < 6) {
        return res.status(400).json({ error: 'Password must be at least 6 characters' });
    }

    // Check if user already exists
    db.get('SELECT * FROM users WHERE email = ?', [email], (err, existing) => {
        if (err) {
            console.error('Error checking user:', err);
            return res.status(500).json({ error: 'Failed to check user' });
        }

        if (existing) {
            return res.status(409).json({
                success: false,
                message: 'An account with this email already exists'
            });
        }

        // Hash password and create new user
        bcrypt.hash(password, 10, (err, hash) => {
            if (err) {
                console.error('Error hashing password:', err);
                return res.status(500).json({ error: 'Failed to create user' });
            }

            db.run('INSERT INTO users (email, password_hash) VALUES (?, ?)', [email, hash], function(err) {
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
});

// Login user (verify email + password)
app.post('/api/users/login', (req, res) => {
    const { email, password } = req.body;

    if (!email) {
        return res.status(400).json({ error: 'Email is required' });
    }

    if (!password) {
        return res.status(400).json({ error: 'Password is required' });
    }

    db.get('SELECT * FROM users WHERE email = ?', [email], (err, user) => {
        if (err) {
            console.error('Error looking up user:', err);
            return res.status(500).json({ error: 'Failed to look up user' });
        }

        if (!user) {
            return res.status(404).json({
                success: false,
                message: 'No account found with this email'
            });
        }

        bcrypt.compare(password, user.password_hash, (err, match) => {
            if (err) {
                console.error('Error comparing password:', err);
                return res.status(500).json({ error: 'Failed to verify password' });
            }

            if (!match) {
                return res.status(401).json({
                    success: false,
                    message: 'Incorrect password'
                });
            }

            res.json({
                success: true,
                message: 'Login successful',
                userId: user.id,
                user: user
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
    const { bio, interest_tags, username, dob, address, caption } = req.body;

    db.run(
        `UPDATE users SET
            bio = COALESCE(?, bio),
            interest_tags = COALESCE(?, interest_tags),
            username = COALESCE(?, username),
            dob = COALESCE(?, dob),
            address = COALESCE(?, address),
            caption = COALESCE(?, caption)
        WHERE id = ?`,
        [bio, interest_tags, username, dob, address, caption, id],
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

// Get user's events (created and joined, excluding left/attended/not_attended events)
app.get('/api/users/:userId/events', (req, res) => {
    const { userId } = req.params;

    db.all(
        `SELECT DISTINCT e.*, ui.interaction_type
         FROM user_interactions ui
         JOIN events e ON ui.event_id = e.id
         WHERE ui.user_id = ?
           AND ui.interaction_type IN ('created', 'joined')
           AND NOT EXISTS (
               SELECT 1 FROM user_interactions ui_done
               WHERE ui_done.user_id = ?
                 AND ui_done.event_id = e.id
                 AND ui_done.interaction_type IN ('left', 'attended', 'not_attended')
                 AND ui_done.created_at > ui.created_at
           )
         ORDER BY e.createdAt DESC`,
        [userId, userId],
        (err, rows) => {
            if (err) {
                console.error('Error fetching user events:', err);
                return res.status(500).json({ error: 'Failed to fetch user events' });
            }

            res.json({ success: true, events: rows });
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

// ==================== EVENT PHOTOS ROUTES ====================

// Upload a photo for an event
app.post('/api/events/:id/photos', upload.single('photo'), (req, res) => {
    const eventId = req.params.id;
    const userId = req.body.user_id;

    if (!userId) {
        return res.status(400).json({ error: 'user_id is required' });
    }

    if (!req.file) {
        return res.status(400).json({ error: 'No photo file provided' });
    }

    const imageUrl = `/uploads/${req.file.filename}`;

    db.run(
        'INSERT INTO event_photos (event_id, user_id, image_url) VALUES (?, ?, ?)',
        [eventId, userId, imageUrl],
        function(err) {
            if (err) {
                console.error('Error saving event photo:', err);
                return res.status(500).json({ error: 'Failed to save photo' });
            }

            res.status(201).json({
                success: true,
                message: 'Photo uploaded successfully',
                photoId: this.lastID,
                image_url: imageUrl
            });
        }
    );
});

// Get photos for a specific event
app.get('/api/events/:id/photos', (req, res) => {
    const eventId = req.params.id;

    db.all(
        'SELECT * FROM event_photos WHERE event_id = ? ORDER BY created_at DESC',
        [eventId],
        (err, rows) => {
            if (err) {
                console.error('Error fetching event photos:', err);
                return res.status(500).json({ error: 'Failed to fetch photos' });
            }

            res.json({
                success: true,
                photos: rows
            });
        }
    );
});

// Get attended event galleries for a user (profile grid)
app.get('/api/users/:userId/attended-galleries', (req, res) => {
    const { userId } = req.params;

    // Find events the user attended that have at least one photo
    db.all(
        `SELECT DISTINCT e.id, e.title, e.eventType, e.time, e.location
         FROM user_interactions ui
         JOIN events e ON ui.event_id = e.id
         WHERE ui.user_id = ?
           AND ui.interaction_type = 'attended'
         ORDER BY ui.created_at DESC`,
        [userId],
        (err, events) => {
            if (err) {
                console.error('Error fetching attended galleries:', err);
                return res.status(500).json({ error: 'Failed to fetch galleries' });
            }

            if (events.length === 0) {
                return res.json({ success: true, galleries: [] });
            }

            // For each attended event, fetch all photos
            const eventIds = events.map(e => e.id);
            const placeholders = eventIds.map(() => '?').join(',');

            db.all(
                `SELECT event_id, image_url FROM event_photos
                 WHERE event_id IN (${placeholders})
                 ORDER BY created_at ASC`,
                eventIds,
                (err, photos) => {
                    if (err) {
                        console.error('Error fetching gallery photos:', err);
                        return res.status(500).json({ error: 'Failed to fetch gallery photos' });
                    }

                    // Group photos by event_id
                    const photosByEvent = {};
                    for (const photo of photos) {
                        if (!photosByEvent[photo.event_id]) {
                            photosByEvent[photo.event_id] = [];
                        }
                        photosByEvent[photo.event_id].push(photo.image_url);
                    }

                    // Build gallery response — include events even if they have no photos yet
                    const galleries = events.map(event => ({
                        eventId: event.id,
                        title: event.title,
                        eventType: event.eventType,
                        time: event.time,
                        location: event.location,
                        imageUrls: photosByEvent[event.id] || []
                    }));

                    res.json({ success: true, galleries });
                }
            );
        }
    );
});

// ==================== MESSAGING ROUTES ====================

// Send an event invite message
app.post('/api/messages/invite', (req, res) => {
    const { sender_id, receiver_id, event_id } = req.body;

    if (!sender_id || !receiver_id || !event_id) {
        return res.status(400).json({ error: 'sender_id, receiver_id, and event_id are required' });
    }

    // Fetch event details for the invite text
    db.get('SELECT * FROM events WHERE id = ?', [event_id], (err, event) => {
        if (err || !event) {
            return res.status(404).json({ error: 'Event not found' });
        }

        const inviteText = `invited you to: ${event.title}`;

        db.run(
            'INSERT INTO messages (sender_id, receiver_id, text, type, event_id) VALUES (?, ?, ?, ?, ?)',
            [sender_id, receiver_id, inviteText, 'event_invite', event_id],
            function(err) {
                if (err) {
                    console.error('Error saving invite message:', err);
                    return res.status(500).json({ error: 'Failed to send invite' });
                }

                res.status(201).json({
                    success: true,
                    messageId: this.lastID,
                    message: {
                        id: this.lastID,
                        sender_id,
                        receiver_id,
                        text: inviteText,
                        type: 'event_invite',
                        event_id,
                        event_title: event.title,
                        event_time: event.time,
                        event_location: event.location,
                        event_type: event.eventType,
                        current_participants: event.currentParticipants,
                        max_participants: event.maxParticipants
                    }
                });
            }
        );
    });
});

// Send a text message
app.post('/api/messages', (req, res) => {
    const { sender_id, receiver_id, text } = req.body;

    if (!sender_id || !receiver_id || !text) {
        return res.status(400).json({ error: 'sender_id, receiver_id, and text are required' });
    }

    db.run(
        'INSERT INTO messages (sender_id, receiver_id, text, type) VALUES (?, ?, ?, ?)',
        [sender_id, receiver_id, text, 'text'],
        function(err) {
            if (err) {
                console.error('Error saving message:', err);
                return res.status(500).json({ error: 'Failed to send message' });
            }

            res.status(201).json({
                success: true,
                messageId: this.lastID
            });
        }
    );
});

// Get messages between two users (conversation thread)
app.get('/api/messages/:userId/:otherUserId', (req, res) => {
    const { userId, otherUserId } = req.params;

    db.all(
        `SELECT m.*, e.title as event_title, e.time as event_time,
                e.location as event_location, e.eventType as event_type,
                e.currentParticipants as current_participants,
                e.maxParticipants as max_participants
         FROM messages m
         LEFT JOIN events e ON m.event_id = e.id
         WHERE (m.sender_id = ? AND m.receiver_id = ?)
            OR (m.sender_id = ? AND m.receiver_id = ?)
         ORDER BY m.created_at ASC`,
        [userId, otherUserId, otherUserId, userId],
        (err, rows) => {
            if (err) {
                console.error('Error fetching messages:', err);
                return res.status(500).json({ error: 'Failed to fetch messages' });
            }

            res.json({ success: true, messages: rows });
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
    console.log(`  POST   /api/users/login`);
    console.log(`  GET    /api/users/:id`);
    console.log(`  PUT    /api/users/:id`);
    console.log(`  GET    /api/users/:userId/events`);
    console.log(`  POST   /api/interactions`);
    console.log(`  GET    /api/interactions/:userId`);
    console.log(`  POST   /api/events/:id/photos`);
    console.log(`  GET    /api/events/:id/photos`);
    console.log(`  GET    /api/users/:userId/attended-galleries`);
    console.log(`  POST   /api/messages/invite`);
    console.log(`  POST   /api/messages`);
    console.log(`  GET    /api/messages/:userId/:otherUserId`);
    console.log(`  GET    /api/stats`);
    console.log(`  POST   /api/chatbot/chat`);
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
