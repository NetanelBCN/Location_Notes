# LocationNotes

**LocationNotes** is an Android application that allows users to create, view, and manage location-based notes with optional image attachments. Users can register and log in via Firebase Authentication, store notes in Firebase Realtime Database, and upload images to Firebase Storage. Notes can be viewed as a list or on a map.

## Features
- User authentication (registration & login) using Firebase Authentication.
- Create, read, update, and delete notes:
  - Note title and body.
  - Automatic geolocation tagging.
  - Optional image attachment uploaded to Firebase Storage.
- Display notes:
  - List view using a generic RecyclerView adapter (imported via JitPack).
  - Map view displaying note locations with Google Maps.
- Offline support using Firebase Realtime Database persistence.
- Input validation with a custom `Validator` utility.
- Clean architecture using Fragments (Login, Registration, List, Map) and Activities (`MainActivity`, `Login_Reg_Activity`, `Note_Screen`).

## Getting Started

### Prerequisites
- Android Studio (Arctic Fox or later) with Android SDK.
- Android device or emulator running Android 8.0 (API level 26) or above.
- Google Firebase project with Authentication, Realtime Database, and Storage enabled.
- Google Maps API key.

### Installation
1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/LocationNotes.git
   cd LocationNotes
   ```
2. Add JitPack to your repositories in `settings.gradle.kts` or root `build.gradle.kts`:
   ```kotlin
   repositories {
       maven { url = uri("https://jitpack.io") }
       // other repositories...
   }
   ```
3. Add the GenericRV library dependency in your module `build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation("com.github.NetanelBCN:GenericRV_Project:1.2.2")
       // other dependencies...
   }
   ```
4. Copy your `google-services.json` into `app/`.
5. Create a `secrets.properties` file in the project root with your Firebase credentials:
   ```properties
   FIREBASE_API_KEY=your_api_key
   FIREBASE_AUTH_DOMAIN=your_auth_domain
   FIREBASE_DATABASE_URL=your_database_url
   FIREBASE_PROJECT_ID=your_project_id
   FIREBASE_STORAGE_BUCKET=your_storage_bucket
   FIREBASE_MESSAGING_SENDER_ID=your_messaging_sender_id
   FIREBASE_APP_ID=your_app_id
   ```
6. Open the project in Android Studio.
7. Sync Gradle and build the project.
8. Run the app on an emulator or a physical device.

## Project Structure
```
LocationNotes/
├── app/
│   ├── src/main/java/dev/netanelbcn/locationnotes/
│   │   ├── fragmentViews/
│   │   │   ├── LoginFragment.java
│   │   │   ├── RegistrationFragment.java
│   │   │   ├── ListFragment.java
│   │   │   └── MapFragment.java
│   │   ├── views/
│   │   │   ├── MainActivity.java
│   │   │   ├── Login_Reg_Activity.java
│   │   │   └── Note_Screen.java
│   │   ├── controllers/
│   │   │   └── DataManager.java
│   │   ├── utilities/
│   │   │   ├── FBManager.java
│   │   │   └── Validator.java
│   │   ├── models/
│   │   │   └── NoteItem.java
│   │   └── interfaces/
│   │       ├── DataLoadListener.java
│   │       └── OnImageUploadComplete.java
│   ├── src/main/res/layout/  # Layout XML files
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md  # <-- You are here (updated)
```

## Built With
- Kotlin DSL Gradle
- Firebase BOM
- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage
- Google Play Services Maps & Location
- AndroidX Navigation Component
- Material Design Components
- **GenericRV** library for RecyclerView (imported via JitPack)
- Glide for image loading

## Usage
1. Register a new user or log in with existing credentials.
2. Grant location permissions when prompted.
3. Create a new note by tapping the "+" button.
4. Fill in the title, body, and optionally attach an image.
5. Save the note to upload data and image to Firebase.
6. View your notes in the list or on the map.
   
## Known Issues
- When creating multiple notes at the same location, they share a single pin on the map. You can switch between notes by clicking on the pin.

## Contributing
Contributions are welcome! Please open issues or submit pull requests.

## Authors

- Netanel Boris Cohen Niazov

## License

Copyright (c) 2025 Netanel Boris Cohen Niazov 


