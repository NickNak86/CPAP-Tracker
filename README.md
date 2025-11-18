# CPAP Tracker

A modern Android application for tracking CPAP/BiPAP equipment parts and managing replacement schedules. Built specifically for VA patients using ResMed equipment.

## Overview

CPAP Tracker helps you stay on top of your CPAP/BiPAP equipment maintenance by:
- Tracking replacement schedules for all equipment parts
- Sending timely notifications for upcoming replacements
- Managing equipment inventory and reorder process
- Following manufacturer-recommended replacement intervals

## Features

### Equipment Tracking
- **ResMed AirCurve 10 VAuto BiPAP Machine**
  - Air Filter (Monthly replacement)
  - Water Chamber (6-month replacement)
  - Tubing (3-month replacement)
  - Elbow Connector (6-month replacement)

- **ResMed AirFit F40 Full Face Mask**
  - Mask Cushion (Monthly replacement)
  - Mask Frame (6-month replacement)
  - Headgear (6-month replacement)

### Smart Notifications
- Daily checks for upcoming part replacements
- Notifications for parts due within 7 days
- Overdue part alerts
- Low battery notification settings

### Replacement Management
- Mark parts as replaced with automatic schedule updates
- Track order status for VA reorders
- View replacement history
- Customizable replacement intervals

### User Interface
- Material Design 3 with dynamic colors
- Dashboard with upcoming replacements summary
- Detailed parts inventory view
- Status badges (OK, Due Soon, Overdue, Ordered)
- Optimized for Samsung Galaxy devices (S22 Ultra, S10 Ultra)

## Technical Stack

### Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Async Processing**: Kotlin Coroutines & Flow
- **Background Tasks**: WorkManager
- **Navigation**: Jetpack Navigation Compose

### Key Technologies
- **Material Design 3** - Modern, adaptive UI
- **Room Database** - Local data persistence
- **WorkManager** - Scheduled notification checks
- **Jetpack Compose** - Declarative UI
- **Flow** - Reactive data streams

### Requirements
- **Minimum SDK**: API 26 (Android 8.0 Oreo)
- **Target SDK**: API 34 (Android 14)
- **Compile SDK**: API 34

## Project Structure

```
app/
├── src/main/
│   ├── java/com/cpaptracker/
│   │   ├── data/
│   │   │   ├── models/          # Data models and entities
│   │   │   │   ├── Equipment.kt
│   │   │   │   ├── Part.kt
│   │   │   │   ├── PartReplacement.kt
│   │   │   │   └── Converters.kt
│   │   │   ├── database/        # Room database and DAOs
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── EquipmentDao.kt
│   │   │   │   ├── PartDao.kt
│   │   │   │   └── PartReplacementDao.kt
│   │   │   └── repository/      # Data repository layer
│   │   │       └── CPAPRepository.kt
│   │   ├── ui/
│   │   │   ├── components/      # Reusable UI components
│   │   │   │   └── PartCard.kt
│   │   │   ├── screens/         # App screens
│   │   │   │   ├── DashboardScreen.kt
│   │   │   │   └── PartsListScreen.kt
│   │   │   └── theme/           # Material Design theme
│   │   │       ├── Color.kt
│   │   │       ├── Type.kt
│   │   │       └── Theme.kt
│   │   ├── utils/               # Utility classes
│   │   │   ├── NotificationHelper.kt
│   │   │   └── NotificationWorker.kt
│   │   ├── MainActivity.kt
│   │   └── CPAPTrackerApp.kt
│   ├── res/                     # Android resources
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API 34

### Building the Project

1. **Clone the repository**
   ```bash
   git clone https://github.com/NickNak86/CPAP-Tracker.git
   cd CPAP-Tracker
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Sync Gradle**
   - Android Studio will automatically prompt to sync
   - Click "Sync Now" if prompted

4. **Build the project**
   ```bash
   ./gradlew build
   ```

5. **Run on device or emulator**
   - Connect your Samsung Galaxy device via USB (with USB debugging enabled)
   - Or start an Android emulator
   - Click the "Run" button in Android Studio

### Installation

#### Option 1: Build from Source
Follow the steps above to build and install directly to your device

#### Option 2: Install APK
1. Download the latest APK from the releases page
2. Enable "Install from Unknown Sources" on your device
3. Open the APK file to install

## Usage

### Initial Setup
1. Launch the app
2. Grant notification permissions (Android 13+)
3. The app will automatically populate with your equipment:
   - ResMed AirCurve 10 VAuto (Serial: 23233592809)
   - ResMed AirFit F40 mask
   - All associated replacement parts

### Tracking Part Replacements

#### Mark a Part as Replaced
1. Navigate to Dashboard or All Parts
2. Find the part you replaced
3. Tap "Replaced" button
4. The app automatically calculates the next replacement date

#### Order a Part
1. Find the part in the Dashboard or All Parts view
2. Tap "Order" button
3. Part status changes to "Ordered"
4. Continue tracking until you receive and replace the part

### Understanding Status Indicators
- **OK** (Green) - Part is within normal replacement window
- **Due Soon** (Orange) - Part should be replaced within 7 days
- **Overdue** (Red) - Part is past its replacement date
- **Ordered** (Blue) - Part has been ordered from VA
- **Not Tracked** (Gray) - Part hasn't been initialized

## Manufacturer Replacement Schedules

### ResMed AirFit F40 Mask
| Part | Replacement Interval |
|------|---------------------|
| Mask Cushion | 30 days (Monthly) |
| Mask Frame | 180 days (6 months) |
| Headgear | 180 days (6 months) |

### ResMed AirCurve 10 VAuto Machine
| Part | Replacement Interval |
|------|---------------------|
| Air Filter (Disposable) | 30 days (Monthly) |
| Water Chamber | 180 days (6 months) |
| Standard Tubing | 90 days (3 months) |
| Elbow Connector | 180 days (6 months) |

## VA Integration (Future Enhancement)

Currently in development:
- Direct VA reorder integration
- VA supply tracking
- Automatic reorder reminders based on VA schedules
- VA prescription tracking

## Customization

### Adding Custom Parts
Future versions will support:
- Custom equipment models
- User-defined replacement intervals
- Multiple CPAP machines
- Additional accessory tracking

## Notifications

The app checks for upcoming replacements daily and sends notifications:
- **7 days before** replacement is due
- **On the day** replacement is due
- **Daily reminders** for overdue parts

### Notification Settings
Notifications can be managed through:
1. App Settings (coming soon)
2. Android System Settings > Apps > CPAP Tracker > Notifications

## Permissions

### Required Permissions
- **POST_NOTIFICATIONS** - Send replacement reminders (Android 13+)
- **SCHEDULE_EXACT_ALARM** - Schedule precise notification times

## Troubleshooting

### Notifications Not Working
1. Check notification permissions in Android settings
2. Ensure "Battery Optimization" is disabled for CPAP Tracker
3. Verify notifications are enabled in app settings

### Data Not Syncing
1. Force close and reopen the app
2. Clear app cache (Settings > Apps > CPAP Tracker > Storage > Clear Cache)
3. If issues persist, clear app data (this will reset all tracking)

## Roadmap

### Version 1.1 (Planned)
- [ ] Manual part addition and editing
- [ ] Custom replacement intervals
- [ ] Export replacement history
- [ ] Backup and restore functionality

### Version 1.2 (Planned)
- [ ] VA integration for automatic reorders
- [ ] Multiple equipment profiles
- [ ] Wear and tear tracking
- [ ] Compliance reporting

### Version 2.0 (Future)
- [ ] Cloud sync across devices
- [ ] Family/caregiver sharing
- [ ] Integration with CPAP data apps
- [ ] Supply cost tracking

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Built for VA patients managing CPAP/BiPAP equipment
- Replacement schedules based on ResMed manufacturer recommendations
- Designed for Samsung Galaxy S22 Ultra and S10 Ultra devices

## Support

For issues, questions, or suggestions:
- Open an issue on GitHub

## Disclaimer

This app is designed to help track CPAP equipment maintenance schedules. It is not a medical device and should not replace medical advice. Always consult with your healthcare provider regarding your CPAP therapy and equipment needs.

---

**Version**: 1.0.0
**Last Updated**: November 2025
**Author**: Nick
**Device**: Samsung Galaxy S22 Ultra / S10 Ultra