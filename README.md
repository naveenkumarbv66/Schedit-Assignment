# Workforce Scheduling Android App

A native Android application for workforce scheduling that allows managers to view shifts, browse employees, and assign employees to shifts with comprehensive business rule validation.

## 📱 Features

### Core Features

1. **Shift Management**
   - Display list of available shifts with details (time, location, required skills, staffing)
   - Filter shifts by date and location
   - View detailed shift information with assigned employees

2. **Employee Management**
   - Browse employee roster with search functionality
   - View employee profiles with skills, availability, and employment type
   - See employee schedules and assigned shifts

3. **Shift Assignment**
   - Assign employees to shifts with real-time validation
   - Business rule validation:
     - ✅ Skill matching (employee must have all required skills)
     - ✅ Availability checking (no overlapping shifts)
     - ✅ Conflict detection (no double-booking)
     - ✅ Hour limits (daily and weekly hour constraints)
   - Clear error messages for validation failures

4. **Schedule View**
   - Weekly and daily schedule views
   - Employee-centric view (all shifts for an employee)
   - Shift-centric view (all assignments for a shift)

## 🏗️ Architecture

### Architecture Pattern: MVVM (Model-View-ViewModel)

The app follows the **MVVM architecture pattern** with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│           UI Layer (Compose)            │
│  - Screens (Shifts, Employees, etc.)   │
│  - Navigation                           │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         ViewModel Layer                 │
│  - ShiftsViewModel                      │
│  - EmployeesViewModel                   │
│  - ScheduleViewModel                    │
│  - AssignmentViewModel                  │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      Repository Layer                   │
│  - ScheduleRepository                  │
│  - Business Logic Validation            │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Data Layer                      │
│  - Room Database                        │
│  - DAOs (EmployeeDao, ShiftDao, etc.)  │
│  - Entities (Employee, Shift, etc.)     │
└─────────────────────────────────────────┘
```

### Key Components

- **UI Layer**: Jetpack Compose screens with Material Design 3
- **ViewModel**: State management using StateFlow
- **Repository**: Single source of truth with business logic
- **Data Layer**: Room database for local persistence
- **Dependency Injection**: Hilt for DI

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Components**:
  - ViewModel
  - LiveData/StateFlow
  - Room Database
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose
- **Async Operations**: Kotlin Coroutines
- **Testing**: JUnit, MockK, Turbine

## 📋 Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or higher
- Android SDK 25 (minimum) / 36 (target)
- Gradle 8.11.2

## 🚀 Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ScheditTestApp
```

### 2. Open in Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the project directory
4. Wait for Gradle sync to complete

### 3. Build the Project

```bash
./gradlew build
```

### 4. Run the App

- Connect an Android device or start an emulator
- Click "Run" in Android Studio or use:
```bash
./gradlew installDebug
```

### 5. Generate APK

```bash
./gradlew assembleDebug
```

The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## 📦 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/naveen/schedittestapp/
│   │   │   ├── data/
│   │   │   │   ├── dao/              # Room DAOs
│   │   │   │   ├── database/          # Room Database
│   │   │   │   ├── model/             # Data models/entities
│   │   │   │   ├── repository/        # Repository layer
│   │   │   │   └── InitialDataProvider.kt
│   │   │   ├── di/                    # Dependency Injection (Hilt)
│   │   │   ├── navigation/            # Navigation setup
│   │   │   ├── ui/
│   │   │   │   ├── assignment/        # Assignment screen
│   │   │   │   ├── employees/         # Employee screens
│   │   │   │   ├── schedule/          # Schedule screens
│   │   │   │   ├── shifts/            # Shift screens
│   │   │   │   └── theme/              # App theme
│   │   │   ├── MainActivity.kt
│   │   │   └── ScheduleApplication.kt
│   │   └── res/                       # Resources
│   └── test/                          # Unit tests
│       └── java/com/naveen/schedittestapp/
└── build.gradle.kts
```

## 🧪 Testing

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "ScheduleRepositoryTest"

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Test Coverage

- **Unit Tests**: Business logic and ViewModels
  - Repository validation logic
  - ViewModel state management
  - Business rule enforcement

### Test Files

- `ScheduleRepositoryTest.kt` - Tests for business logic validation
- `ShiftsViewModelTest.kt` - Tests for ViewModel functionality

## 📐 Architecture Decisions

### 1. MVVM Pattern

**Decision**: Use MVVM instead of MVP or Clean Architecture

**Rationale**:
- Native Android support with ViewModel lifecycle awareness
- Better separation of UI and business logic
- Easier testing with ViewModels
- Works seamlessly with Jetpack Compose

### 2. Jetpack Compose

**Decision**: Use Jetpack Compose for UI instead of XML

**Rationale**:
- Modern declarative UI framework
- Less boilerplate code
- Better state management
- Easier to maintain and test

### 3. Room Database

**Decision**: Use Room for local data persistence

**Rationale**:
- Type-safe database access
- Built-in support for Kotlin coroutines
- Compile-time query validation
- Offline-first approach

### 4. Hilt for Dependency Injection

**Decision**: Use Hilt instead of manual DI or Koin

**Rationale**:
- Official Android DI solution
- Compile-time code generation
- Better integration with Android components
- Less boilerplate than Dagger

### 5. StateFlow over LiveData

**Decision**: Use StateFlow for state management

**Rationale**:
- Better integration with coroutines
- More flexible and composable
- Works better with Compose
- Type-safe state management

### 6. Repository Pattern

**Decision**: Single repository for all data operations

**Rationale**:
- Single source of truth
- Centralized business logic
- Easier to test and maintain
- Can easily swap data sources

## 🔒 Business Rules Implementation

The app enforces the following scheduling rules:

1. **Skill Matching**
   - Employee must have ALL required skills for a shift
   - Validation happens before assignment

2. **No Double-Booking**
   - Employee cannot be assigned to overlapping shifts
   - Checks shift time ranges for conflicts

3. **Hour Limits**
   - Daily hour limit per employee
   - Weekly hour limit per employee
   - Validates against existing assignments

4. **Availability**
   - Implicitly checked through overlap detection
   - Can be extended for explicit availability windows

## 📝 Sample Data

The app includes sample data that is automatically loaded on first launch:

**Employees**:
- John Smith (Cashier, Supervisor) - Full-time
- Jane Doe (Barista, Cashier) - Part-time
- Bob Wilson (Cook, Supervisor) - Full-time

**Shifts**:
- Monday 9AM-5PM, Retail, needs: Cashier, min staff: 2
- Monday 2PM-10PM, Kitchen, needs: Cook, min staff: 1
- Tuesday 6AM-2PM, Cafe, needs: Barista, min staff: 2

## 🎨 UI/UX Features

- **Material Design 3**: Modern, polished UI
- **Loading States**: Progress indicators during data loading
- **Error Handling**: User-friendly error messages
- **Empty States**: Clear messaging when no data is available
- **Navigation**: Intuitive navigation between screens
- **Search & Filter**: Easy-to-use search and filter functionality

## 🔄 Known Limitations & Future Improvements

### Current Limitations

1. **No Calendar View**: Currently shows list view only (calendar view is a bonus feature)
2. **No Rest Period Validation**: Minimum rest periods between shifts not implemented
3. **No Availability Windows**: Employees don't have explicit availability schedules
4. **No Shift Templates**: Cannot create recurring shifts
5. **No Notifications**: No push notifications for shift assignments

### Future Improvements

1. **Calendar View**: Visual calendar representation of shifts
2. **Rest Period Validation**: Enforce minimum rest between shifts
3. **Availability Management**: Allow employees to set availability windows
4. **Shift Templates**: Create and manage recurring shift patterns
5. **Notifications**: Push notifications for assignments and reminders
6. **Offline Sync**: Better offline support with sync capabilities
7. **Export/Import**: Export schedules to CSV/PDF
8. **Multi-language Support**: Internationalization

## 📄 License

This project is created as part of an assignment and is not intended for public distribution.

## 👤 Author

Naveen Kumar BV

## 📞 Support

For issues or questions, please contact the development team.

---

**Note**: This app demonstrates production-quality Android development with modern architecture patterns, comprehensive business logic validation, and a polished user interface.

