# Using .env File for API Keys

You have **TWO options** for storing your API key:

## Option 1: Using .env file (Root Directory) ✨

### Step 1: Edit the .env file
The `.env` file is already created at the project root. Just add your API key:

```bash
# Beyond_Binary/.env
MAPS_API_KEY=AIzaSyC_your_actual_key_here
```

### Step 2: Copy to Android project
Since Android uses `local.properties`, you need to also add it there:

```bash
# Beyond_Binary/frontend/local.properties
MAPS_API_KEY=AIzaSyC_your_actual_key_here
```

### Quick Setup Script:
```bash
# From Beyond_Binary directory
cd Beyond_Binary

# 1. Edit .env and add your key
nano .env  # or use any text editor

# 2. Copy the key to local.properties
cd frontend
echo "MAPS_API_KEY=$(grep MAPS_API_KEY ../.env | cut -d '=' -f2)" >> local.properties
```

## Option 2: Using local.properties (Android Standard) 🤖

**Recommended for Android projects**

```bash
cd Beyond_Binary/frontend

# Create local.properties
cp local.properties.example local.properties

# Edit and add your key
nano local.properties
```

```properties
# frontend/local.properties
MAPS_API_KEY=AIzaSyC_your_actual_key_here
```

## Which One Should I Use?

| Feature | .env (Root) | local.properties (Android) |
|---------|-------------|---------------------------|
| **Location** | Project root | `frontend/` directory |
| **Standard for Android** | ❌ No | ✅ Yes |
| **Gitignored** | ✅ Yes | ✅ Yes |
| **Auto-detected by Android Studio** | ❌ No | ✅ Yes |
| **Works with Gradle** | ⚠️ Needs setup | ✅ Built-in |
| **For backend too** | ✅ Yes | ❌ Android only |

### Recommendation:

**For Android-only project:** Use `local.properties` ✅
- Native Android solution
- Auto-detected by Android Studio
- Standard practice
- No extra setup needed

**For full-stack project (Android + Backend):** Use both ⚡
- `.env` for backend API keys
- `local.properties` for Android (copy from .env)

## Current Setup Status

✅ **Already working:** `local.properties` (Android standard)
✅ **Also available:** `.env` (created at root)
✅ **Both are gitignored**

## Quick Start (Choose One)

### Option A: Just .env file
```bash
# 1. Edit .env
cd Beyond_Binary
nano .env
# Add: MAPS_API_KEY=your_key_here

# 2. Copy to Android
cd frontend
echo "MAPS_API_KEY=$(grep MAPS_API_KEY ../.env | cut -d '=' -f2)" > local.properties
```

### Option B: Just local.properties (Recommended)
```bash
cd Beyond_Binary/frontend
cp local.properties.example local.properties
nano local.properties
# Add: MAPS_API_KEY=your_key_here
```

## File Locations

```
Beyond_Binary/
├── .env                          # ← Root level (for backend or scripts)
├── .env.example                  # ← Template
└── frontend/
    ├── local.properties          # ← Android uses THIS ONE
    └── local.properties.example  # ← Template
```

## How It Works

1. **You put key in `.env` OR `local.properties`**
2. **Build script reads it** (`app/build.gradle`)
3. **Injects into AndroidManifest.xml** (at build time)
4. **App runs with your API key**

## Security

Both files are in `.gitignore`:
```gitignore
# API Keys
.env
local.properties
```

✅ Your actual keys are **NEVER** committed to git!

## Troubleshooting

### Problem: Build can't find API key

**Check this order:**
1. Does `frontend/local.properties` exist?
2. Does it have `MAPS_API_KEY=...` line?
3. Is the key correct (starts with `AIza`)?
4. Did you sync Gradle? (File → Sync Project with Gradle Files)

### Problem: Want to use .env for everything

**Install dotenv plugin for Gradle:**
```gradle
// frontend/app/build.gradle
plugins {
    id "co.uzzu.dotenv.gradle" version "2.0.0"
}

// Then access:
android {
    defaultConfig {
        manifestPlaceholders = [mapsApiKey: env.MAPS_API_KEY]
    }
}
```

## Example: Full Setup

```bash
# 1. Navigate to project
cd Beyond_Binary

# 2. Create and edit .env
cat > .env << EOF
MAPS_API_KEY=AIzaSyC_your_actual_key_here_12345
EOF

# 3. Copy to Android local.properties
cd frontend
echo "MAPS_API_KEY=AIzaSyC_your_actual_key_here_12345" > local.properties

# 4. Verify
cat local.properties
# Should show: MAPS_API_KEY=AIzaSyC...

# 5. Open in Android Studio and run!
```

## What Happens at Build Time

```
Your .env or local.properties
         ↓
    Gradle reads it
         ↓
  Replaces ${mapsApiKey} in AndroidManifest.xml
         ↓
    Compiles APK
         ↓
  APK has your actual key (but source code doesn't!)
```

## Both Files Are Ready!

✅ `.env` - Created at project root
✅ `.env.example` - Template for team members
✅ `local.properties.example` - Android template
✅ Both in `.gitignore`

**Just add your API key to either file and you're good to go!** 🚀
