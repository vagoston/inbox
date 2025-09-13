# Google API Setup Guide for PhotoBox

This guide explains how to set up Google APIs required for the PhotoBox Android app.

## Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click "Select a project" → "New Project"
3. Enter project name (e.g., "PhotoBox App")
4. Click "Create"

## Step 2: Enable Required APIs

1. In your Google Cloud project, go to **APIs & Services** → **Library**
2. Search for and enable these APIs:
   - **Google Drive API**
   - **Google Sign-In API** (if not automatically enabled)

## Step 3: Create OAuth 2.0 Credentials

### For Development (Debug)

1. Go to **APIs & Services** → **Credentials**
2. Click **"+ CREATE CREDENTIALS"** → **OAuth client ID**
3. Select **Android** as application type
4. Fill in the form:
   - **Name**: PhotoBox Debug
   - **Package name**: `com.inbox.photobox`
   - **SHA-1 certificate fingerprint**: Get from debug keystore (see below)

### Getting Debug SHA-1 Fingerprint

Run this command in your terminal:

```bash
# On Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# On macOS/Linux
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Copy the SHA-1 fingerprint from the output.

### For Production (Release)

1. Create another OAuth client ID for your release keystore
2. Use your production package name and release SHA-1 fingerprint

## Step 4: Configure the Android App

### Option A: Using OAuth Client ID directly (Recommended)

No additional files needed. The app uses the Google Sign-In configuration programmatically.

### Option B: Using google-services.json (If using Firebase)

1. Go to **Firebase Console** → Add Project → Select your Google Cloud project
2. Add Android app with package name `com.inbox.photobox`
3. Download `google-services.json`
4. Place it in `app/` directory
5. Add Firebase dependencies to your build files

## Step 5: Test the Setup

1. Build and install the app on a device or emulator
2. Tap "Sign in with Google"
3. Complete the sign-in flow
4. Take a photo and try uploading
5. Check your Google Drive for a "PhotoBox" folder with the uploaded photo

## Scopes Used

The app requests these permissions from Google:
- **Drive File Scope**: To create folders and upload files to Google Drive
- **Email Scope**: To get user email for display

## Security Notes

- The OAuth client ID is public information and safe to include in the app
- Users authenticate directly with Google - no credentials are stored in the app
- The app only has access to files it creates in Google Drive

## Troubleshooting

### Common Issues:

1. **Sign-in fails**: Check SHA-1 fingerprint matches your keystore
2. **Upload fails**: Ensure Drive API is enabled
3. **Permission denied**: Check OAuth scopes are correctly configured

### Testing on Emulator:

- Use an emulator with Google Play Services
- Sign in with a test Google account
- Ensure emulator has camera support for photo capture

### Production Release:

1. Create production OAuth client ID with release keystore SHA-1
2. Test thoroughly with release build
3. Consider rate limiting and error handling for production usage