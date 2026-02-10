# API Key Setup Guide

This guide will help you securely configure your Google Maps API key for the Beyond Binary app.

## 🔐 Security First

The app uses `local.properties` to store API keys securely:
- ✅ **Never committed to git** (automatically gitignored)
- ✅ **Not hardcoded** in source files
- ✅ **Easy to manage** per developer/environment

## 📝 Step-by-Step Setup

### Step 1: Get Your Google Maps API Key

1. **Go to Google Cloud Console:**
   - Visit: [https://console.cloud.google.com/](https://console.cloud.google.com/)
   - Sign in with your Google account

2. **Create or Select a Project:**
   - Click "Select a Project" → "New Project"
   - Name: "Beyond Binary" (or your choice)
   - Click "Create"

3. **Enable Required APIs:**
   - Go to **"APIs & Services"** → **"Library"**
   - Search and enable:
     - ✅ **Maps SDK for Android**
     - ✅ **Geocoding API** (for address → coordinates)

4. **Create API Key:**
   - Go to **"APIs & Services"** → **"Credentials"**
   - Click **"Create Credentials"** → **"API Key"**
   - Your API key will be displayed (starts with `AIza...`)
   - **Copy this key!**

5. **Restrict API Key (Recommended):**
   - Click on your new API key to edit
   - Under "Application restrictions":
     - Select **"Android apps"**
     - Click **"Add an item"**
     - Package name: `com.beyondbinary.eventapp`
     - SHA-1 certificate fingerprint: (get from Android Studio - see below)
   - Under "API restrictions":
     - Select **"Restrict key"**
     - Check: Maps SDK for Android, Geocoding API
   - Click **"Save"**

### Step 2: Get SHA-1 Certificate (Optional but Recommended)

**For Debug Key:**
```bash
# On Mac/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# On Windows
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Copy the **SHA-1** fingerprint and add it to your API key restrictions.

### Step 3: Configure local.properties

1. **Navigate to the frontend directory:**
   ```bash
   cd Beyond_Binary/frontend
   ```

2. **Create local.properties file:**
   - Copy the template:
     ```bash
     cp local.properties.example local.properties
     ```
   - Or create manually:
     ```bash
     touch local.properties
     ```

3. **Edit local.properties:**
   Open the file and add your API key:
   ```properties
   # Android SDK location (automatically set by Android Studio)
   sdk.dir=/path/to/Android/sdk

   # Google Maps API Key
   MAPS_API_KEY=AIzaSyC8xxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

4. **Save the file** (it's automatically gitignored)

### Step 4: Verify Setup

1. **Sync Gradle:**
   - Open project in Android Studio
   - Click **"Sync Project with Gradle Files"**

2. **Check Build Success:**
   - Build → Clean Project
   - Build → Rebuild Project
   - No errors = Success! ✅

3. **Run the App:**
   - Click Run button (green play icon)
   - Map should load with Singapore location
   - Sample event markers should appear

## 🔍 Troubleshooting

### Problem: Map shows gray screen

**Causes:**
1. API key not configured
2. Wrong API key
3. APIs not enabled
4. API restrictions too strict

**Solutions:**
1. Check `local.properties` exists and has correct key
2. Verify key in Google Cloud Console
3. Ensure "Maps SDK for Android" is enabled
4. Try removing restrictions temporarily for testing

### Problem: "Error loading map"

**Check:**
```bash
# View Logcat in Android Studio
# Look for errors containing "API_KEY" or "Maps"
```

**Common fixes:**
- Sync Gradle files
- Clean and rebuild project
- Check internet connection
- Verify API key format (should start with AIza)

### Problem: Build error "MAPS_API_KEY not found"

**Solution:**
- Ensure `local.properties` exists in `/frontend/` directory
- Check file contains: `MAPS_API_KEY=your_key_here`
- Sync Gradle files

### Problem: Geocoding not working (no markers appear)

**Check:**
1. "Geocoding API" is enabled in Google Cloud Console
2. API key restrictions allow Geocoding API
3. Internet connection is active
4. Check Logcat for geocoding errors

## 📊 API Usage and Quotas

**Free Tier (as of 2024):**
- Maps SDK for Android: $0 for unlimited loads
- Geocoding API: $5 per 1000 requests (first $200/month free)

**Monitor Usage:**
- Go to: [Google Cloud Console](https://console.cloud.google.com/)
- Navigate to: **APIs & Services** → **Dashboard**
- View: Usage graphs and quotas

**Set Billing Alerts (Optional):**
- Go to: **Billing** → **Budgets & Alerts**
- Create alert at $10, $50, etc.

## 🔄 Switching API Keys

**For Different Environments:**

```properties
# local.properties - Development
MAPS_API_KEY=AIzaSyC_DEVELOPMENT_KEY

# For production, use separate restricted key
```

**For Team Members:**
Each developer should:
1. Get their own API key
2. Add to their own `local.properties`
3. Never commit `local.properties` to git

## ✅ Security Checklist

- [ ] API key stored in `local.properties`
- [ ] `local.properties` is in `.gitignore`
- [ ] API key has Android app restrictions
- [ ] API key has API restrictions
- [ ] SHA-1 fingerprint added to restrictions
- [ ] Billing alerts set up
- [ ] Usage monitored regularly

## 📱 Production Release

When releasing to production:

1. **Create Production API Key:**
   - Separate from development key
   - Strict restrictions
   - Package name: `com.beyondbinary.eventapp`
   - SHA-1: Your release keystore fingerprint

2. **Get Release SHA-1:**
   ```bash
   keytool -list -v -keystore /path/to/release.keystore -alias your_alias
   ```

3. **Add to Build Variants:**
   ```gradle
   buildTypes {
       release {
           buildConfigField "String", "MAPS_API_KEY", "\"${releaseApiKey}\""
       }
   }
   ```

## 🆘 Need Help?

- Google Maps Platform Support: [https://developers.google.com/maps/support](https://developers.google.com/maps/support)
- Stack Overflow: Tag `google-maps-android-api`
- Project Issues: Check [TESTING_GUIDE.md](TESTING_GUIDE.md)

---

**Remember:** Never commit your actual API key to version control! Always use `local.properties`.
