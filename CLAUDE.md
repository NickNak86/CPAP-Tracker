# Mike's BiPAP Tracker - Project Context for Claude Code

## Project Overview

**Mike's BiPAP Tracker** is an Android application for tracking BiPAP/CPAP equipment parts and managing replacement schedules, built specifically for VA patients using ResMed equipment.

- **Platform**: Android (Native)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Database**: Room (SQLite)
- **Min SDK**: API 26 (Android 8.0)
- **Target SDK**: API 34 (Android 14)
- **Target Devices**: Samsung Galaxy S22 Ultra, S10 Ultra

---

## Repository Information

- **GitHub**: https://github.com/NickNak86/Mike_BiPAP_Tracker
- **Primary Branch**: `main`
- **Package Name**: `com.cpaptracker`

---

## Directory Structure

```
/home/user/Mike_BiPAP_Tracker/
├── CLAUDE.md                    # This file - Claude Code context
├── QUICK_START.md               # Beginner guide for Android Studio
├── README.md                    # Full project documentation
├── .gitignore                   # Git ignore rules
│
├── build.gradle.kts             # Root Gradle build config
├── settings.gradle.kts          # Gradle settings and plugins
├── gradle.properties            # Gradle configuration properties
│
└── app/                         # Main Android application module
    ├── build.gradle.kts         # App-level Gradle config with dependencies
    ├── proguard-rules.pro       # ProGuard rules for release builds
    │
    └── src/main/
        ├── AndroidManifest.xml  # App manifest with permissions
        │
        ├── java/com/cpaptracker/
        │   ├── CPAPTrackerApp.kt           # Application class (entry point)
        │   ├── MainActivity.kt              # Main Activity with navigation
        │   │
        │   ├── data/
        │   │   ├── models/                  # Data classes and Room entities
        │   │   │   ├── Equipment.kt         # Equipment entity (CPAP machines)
        │   │   │   ├── Part.kt              # Part entity (mask, tubing, etc.)
        │   │   │   ├── PartReplacement.kt   # Replacement history entity
        │   │   │   └── Converters.kt        # Room type converters for dates
        │   │   │
        │   │   ├── database/                # Room database layer
        │   │   │   ├── AppDatabase.kt       # Database definition + prepopulation
        │   │   │   ├── EquipmentDao.kt      # Equipment data access
        │   │   │   ├── PartDao.kt           # Parts data access
        │   │   │   └── PartReplacementDao.kt # Replacement history access
        │   │   │
        │   │   └── repository/              # Repository pattern layer
        │   │       └── CPAPRepository.kt    # Main data repository
        │   │
        │   ├── ui/
        │   │   ├── components/              # Reusable Compose components
        │   │   │   └── PartCard.kt          # Card component for displaying parts
        │   │   │
        │   │   ├── screens/                 # App screens (Composables)
        │   │   │   ├── DashboardScreen.kt   # Main dashboard view
        │   │   │   └── PartsListScreen.kt   # All parts list view
        │   │   │
        │   │   └── theme/                   # Material Design 3 theming
        │   │       ├── Color.kt             # Color definitions
        │   │       ├── Type.kt              # Typography definitions
        │   │       └── Theme.kt             # Theme configuration
        │   │
        │   └── utils/                       # Utility classes
        │       ├── NotificationHelper.kt    # Notification creation/management
        │       └── NotificationWorker.kt    # Background notification scheduler
        │
        └── res/                             # Android resources
            ├── drawable/
            │   └── ic_notification.xml      # Notification icon
            ├── mipmap-anydpi-v26/
            │   ├── ic_launcher.xml          # App launcher icon
            │   └── ic_launcher_round.xml    # Round launcher icon
            ├── values/
            │   ├── colors.xml               # Color resources
            │   ├── strings.xml              # String resources
            │   └── themes.xml               # XML theme fallback
            └── xml/
                ├── backup_rules.xml         # Backup configuration
                └── data_extraction_rules.xml # Data extraction rules
```

---

## Key Files Reference

### Entry Points
| File | Location | Purpose |
|------|----------|---------|
| `CPAPTrackerApp.kt` | `app/src/main/java/com/cpaptracker/` | Application class, initializes database and notification scheduler |
| `MainActivity.kt` | `app/src/main/java/com/cpaptracker/` | Main activity, sets up navigation and bottom tabs |

### Data Layer
| File | Location | Purpose |
|------|----------|---------|
| `Equipment.kt` | `app/.../data/models/` | Equipment entity (BiPAP machines with serial numbers) |
| `Part.kt` | `app/.../data/models/` | Part entity (mask cushion, tubing, etc.) |
| `PartReplacement.kt` | `app/.../data/models/` | Tracks replacement history for each part |
| `AppDatabase.kt` | `app/.../data/database/` | Room database definition, prepopulates default data |
| `CPAPRepository.kt` | `app/.../data/repository/` | Repository pattern - single source of truth for data |

### UI Layer
| File | Location | Purpose |
|------|----------|---------|
| `DashboardScreen.kt` | `app/.../ui/screens/` | Main dashboard with summary cards and quick actions |
| `PartsListScreen.kt` | `app/.../ui/screens/` | Full list of all parts with status |
| `PartCard.kt` | `app/.../ui/components/` | Reusable card component showing part status |
| `Theme.kt` | `app/.../ui/theme/` | Material Design 3 theme with dynamic colors |

### Background Services
| File | Location | Purpose |
|------|----------|---------|
| `NotificationHelper.kt` | `app/.../utils/` | Creates and displays notifications |
| `NotificationWorker.kt` | `app/.../utils/` | WorkManager job for daily notification checks |

### Build Configuration
| File | Location | Purpose |
|------|----------|---------|
| `build.gradle.kts` | `app/` | Dependencies, SDK versions, build config |
| `settings.gradle.kts` | Root | Plugin management, repository configuration |
| `AndroidManifest.xml` | `app/src/main/` | Permissions, activities, services |

---

## Architecture Pattern

This project uses **MVVM with Repository Pattern**:

```
┌─────────────────────────────────────────────────────────────┐
│                         UI Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐                   │
│  │ DashboardScreen │  │ PartsListScreen │                   │
│  └────────┬────────┘  └────────┬────────┘                   │
│           │                    │                            │
│           └──────────┬─────────┘                            │
│                      ▼                                      │
│              ┌───────────────┐                              │
│              │  MainActivity │  (ViewModel holder)          │
│              └───────┬───────┘                              │
└──────────────────────┼──────────────────────────────────────┘
                       ▼
┌──────────────────────────────────────────────────────────────┐
│                    Repository Layer                          │
│              ┌────────────────────┐                          │
│              │   CPAPRepository   │                          │
│              └─────────┬──────────┘                          │
└────────────────────────┼─────────────────────────────────────┘
                         ▼
┌──────────────────────────────────────────────────────────────┐
│                    Database Layer                            │
│  ┌──────────────┐ ┌─────────┐ ┌─────────────────────────┐    │
│  │ EquipmentDao │ │ PartDao │ │ PartReplacementDao      │    │
│  └──────────────┘ └─────────┘ └─────────────────────────┘    │
│              ┌────────────────────┐                          │
│              │    AppDatabase     │  (Room)                  │
│              └────────────────────┘                          │
└──────────────────────────────────────────────────────────────┘
```

---

## Pre-Populated Data

The database is pre-populated with the user's actual equipment:

### Equipment
| Equipment | Model | Serial Number |
|-----------|-------|---------------|
| BiPAP Machine | ResMed AirCurve 10 VAuto | 23233592809 |
| Mask | ResMed AirFit F40 | - |

### Parts with Replacement Schedules
| Part | Equipment | Replacement Interval |
|------|-----------|---------------------|
| Mask Cushion | AirFit F40 | 30 days |
| Mask Frame | AirFit F40 | 180 days |
| Headgear | AirFit F40 | 180 days |
| Air Filter | AirCurve 10 | 30 days |
| Water Chamber | AirCurve 10 | 180 days |
| Tubing | AirCurve 10 | 90 days |
| Elbow Connector | AirCurve 10 | 180 days |

---

## Part Status States

Parts can be in one of these states (defined in `Part.kt`):

| Status | Description | Color |
|--------|-------------|-------|
| `OK` | Within normal replacement window | Green |
| `DUE_SOON` | Due within 7 days | Orange |
| `OVERDUE` | Past replacement date | Red |
| `ORDERED` | Ordered from VA, awaiting delivery | Blue |
| `NOT_TRACKED` | Never been replaced/initialized | Gray |

---

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Clean build
./gradlew clean

# Install on connected device
./gradlew installDebug
```

---

## Common Development Tasks

### Adding a New Screen
1. Create new Composable in `app/src/main/java/com/cpaptracker/ui/screens/`
2. Add navigation route in `MainActivity.kt`
3. Add bottom navigation item if needed

### Adding a New Part Type
1. Update prepopulation in `AppDatabase.kt`
2. Add part to appropriate equipment in the `prepopulateData()` function

### Modifying Replacement Intervals
1. Edit intervals in `AppDatabase.kt` prepopulation
2. Intervals are in days (30 = monthly, 90 = quarterly, 180 = 6 months)

### Adding New Notifications
1. Modify `NotificationHelper.kt` to create new notification types
2. Update `NotificationWorker.kt` to trigger at appropriate times

### Changing Theme Colors
1. Edit `app/src/main/java/com/cpaptracker/ui/theme/Color.kt`
2. Update `Theme.kt` to use new colors in light/dark schemes

---

## Known Issues / TODOs

### Current Limitations
- [ ] No manual part editing UI (can only mark as replaced)
- [ ] No settings screen
- [ ] No data export/backup
- [ ] Single equipment profile only

### Planned Features (Priority Order)
1. Settings screen with customizable notification times
2. Manual interval editing for parts
3. Replacement history view
4. Data backup/restore
5. VA API integration (direct ordering)
6. Multiple equipment profiles
7. Cloud sync

---

## VA Integration

### Current Implementation
- **"VA" button** on each PartCard opens VA reorder website in browser
- URL: `https://www.va.gov/health-care/order-hearing-aid-or-CPAP-supplies-form/introduction`
- Requires Login.gov authentication

### Key Files
- `MainActivity.kt` - Contains `VA_REORDER_URL` constant and `openVAReorderSite()` function
- `PartCard.kt` - Contains the VA button UI
- `strings.xml` - Contains VA-related string resources

### Future VA API Integration
When/if VA provides an API:
1. Create `VAApiService.kt` in `data/api/`
2. Add Retrofit dependency for API calls
3. Implement OAuth flow with Login.gov
4. Add order status tracking to database

---

## Testing

### Manual Testing Checklist
- [ ] App launches without crash
- [ ] Dashboard shows all parts
- [ ] "Replaced" button updates part dates
- [ ] "Order" button changes status to Ordered
- [ ] "VA" button opens VA reorder website in browser
- [ ] Bottom navigation works
- [ ] Notifications appear (check after 24 hours)

### Test on Devices
- Primary: Samsung Galaxy S22 Ultra
- Secondary: Samsung Galaxy S10 Ultra
- Emulator: Pixel 6 API 34

---

## Dependencies (from app/build.gradle.kts)

| Library | Purpose |
|---------|---------|
| Jetpack Compose | Modern declarative UI |
| Room | SQLite database abstraction |
| WorkManager | Background task scheduling |
| Material 3 | Design system and components |
| Navigation Compose | Screen navigation |
| Coroutines | Async programming |
| Flow | Reactive data streams |

---

## User Context

- **User**: Mike - VA patient with BiPAP equipment
- **Experience Level**: New to Android development
- **IDE**: Android Studio (primary), VS Code with Claude Code (secondary)
- **Device**: Samsung Galaxy S22 Ultra
- **Goal**: Track BiPAP part replacements and get reminders for VA reorders

---

## Quick Reference Paths

```
# Main source code
app/src/main/java/com/cpaptracker/

# UI screens
app/src/main/java/com/cpaptracker/ui/screens/

# Data models
app/src/main/java/com/cpaptracker/data/models/

# Database
app/src/main/java/com/cpaptracker/data/database/

# Resources (strings, colors, icons)
app/src/main/res/

# Build configuration
app/build.gradle.kts

# App manifest
app/src/main/AndroidManifest.xml
```

---

## Getting Help

When working on this project with Claude Code:

1. **For UI changes**: Reference files in `ui/screens/` and `ui/components/`
2. **For data changes**: Reference `data/models/` and `data/database/`
3. **For notifications**: Reference `utils/NotificationHelper.kt` and `NotificationWorker.kt`
4. **For dependencies**: Check `app/build.gradle.kts`
5. **For permissions**: Check `AndroidManifest.xml`

---

*Last Updated: November 2025*
*Project Status: Version 1.0 - Core functionality complete*
