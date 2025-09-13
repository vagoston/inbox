# PhotoBox - Android Photo Upload App

PhotoBox is an Android application that allows users to:
- Sign in to Google using their own credentials
- Take photos using the device camera
- Upload photos to their Google Drive in a predefined "PhotoBox" folder

## Features

- **Google Sign-In**: Secure authentication using Google's OAuth 2.0
- **Camera Integration**: Take photos directly from the app using CameraX
- **Google Drive Upload**: Automatic upload to a dedicated "PhotoBox" folder
- **Permission Management**: Handles camera and storage permissions properly
- **Modern UI**: Built with Jetpack Compose and Material Design 3

## Setup Instructions

### Prerequisites

1. **Android Studio**: Download and install the latest version
2. **Google Cloud Project**: Create a project with Drive API enabled
3. **OAuth 2.0 Credentials**: Configure for Android app

### Google Cloud Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the Google Drive API
4. Create OAuth 2.0 credentials for Android:
   - Application type: Android
   - Package name: `com.inbox.photobox`
   - SHA-1 certificate fingerprint: Get from your debug keystore

### Project Setup

1. Clone this repository
2. Open in Android Studio
3. Sync the project with Gradle files
4. Add your `google-services.json` (if using Firebase) or configure OAuth client ID
5. Build and run on device or emulator

### Permissions

The app automatically requests the following permissions:
- `CAMERA`: To take photos
- `READ_EXTERNAL_STORAGE`: To access saved photos
- `INTERNET`: For Google API communication

### Usage

1. **Sign In**: Tap "Sign in with Google" and authenticate
2. **Take Photo**: Use "Take Photo" button to capture an image
3. **Upload**: Photos are automatically uploaded to "PhotoBox" folder in Google Drive
4. **Sign Out**: Use sign out option when done

### Architecture

- **MVVM Pattern**: Uses ViewModel with StateFlow for reactive UI updates
- **Jetpack Compose**: Modern declarative UI framework
- **CameraX**: Camera implementation with lifecycle awareness
- **Google APIs**: Drive API for file uploads and Google Sign-In for authentication

### File Structure

```
app/src/main/
├── java/com/inbox/photobox/
│   ├── MainActivity.kt          # Main activity
│   ├── PhotoBoxApp.kt          # Main UI composable
│   ├── PhotoBoxViewModel.kt    # Business logic and state management
│   └── ui/theme/               # UI theme components
├── res/
│   ├── values/
│   │   ├── strings.xml         # App strings
│   │   ├── colors.xml          # Color definitions
│   │   └── themes.xml          # Material themes
│   └── xml/
│       ├── file_paths.xml      # File provider configuration
│       ├── backup_rules.xml    # Backup configuration
│       └── data_extraction_rules.xml
└── AndroidManifest.xml         # App configuration and permissions
```

### Dependencies

Key dependencies used in this project:
- Jetpack Compose for UI
- Google Play Services for authentication
- Google APIs for Drive integration
- CameraX for camera functionality
- Accompanist for permission handling

### Security Notes

- The app uses OAuth 2.0 for secure authentication
- No user credentials are stored locally
- Photos are uploaded directly to user's personal Google Drive
- All API calls use secure HTTPS connections

### Building

To build the project:
```bash
./gradlew assembleDebug
```

To run tests:
```bash
./gradlew test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
