# Workforce Scheduling Android App

A native Android application for workforce scheduling that allows managers to view shifts, browse employees, and assign employees to shifts with comprehensive business rule validation.

## 📱 Features

### Core Features

1. **Shift Management**
   - Display list of available shifts with details (time, location, required skills, staffing)
   - Filter shifts by date and location
   - View detailed shift information with assigned employees
   - Edit shifts: Modify location, time, skills, and staffing requirements
   - Delete shifts with automatic cascade deletion of all assignments
   - Data integrity: All related employee assignments are automatically removed

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

5. **Shift Templates** ✨
   - Create reusable shift templates with recurring patterns
   - Define shift details: location, time, duration, required skills, staffing
   - Support for daily or specific day-of-week templates
   - Generate multiple shifts from templates for a date range
   - Edit and delete templates
   - Efficient bulk shift creation

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
│  - ShiftTemplatesViewModel              │
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
│  - ShiftTemplate entity                 │
└─────────────────────────────────────────┘
```

### Key Components

- **UI Layer**: Jetpack Compose screens with Material Design 3
- **ViewModel**: State management using StateFlow
- **Repository**: Single source of truth with business logic
- **Data Layer**: Room database for local persistence with cascade deletion support
- **Dependency Injection**: Hilt for DI
- **Data Integrity**: Foreign key constraints with CASCADE ensure automatic cleanup

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
│   │   │   │   ├── templates/         # Shift templates screens
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

**Database Entities**:
- `Employee`: Employee information with skills and hour limits
- `Shift`: Shift details with time, location, and requirements
- `ShiftAssignment`: Many-to-many relationship between employees and shifts
- `ShiftTemplate`: Reusable shift patterns for bulk generation

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

## 🗑️ Shift Deletion Feature

### Overview

The app supports safe deletion of shifts with automatic cleanup of all related data. When a shift is deleted, all employee assignments for that shift are automatically removed to maintain data integrity.

### How to Delete a Shift

1. **Navigate to Shift Details**
   - Open the Shifts screen
   - Tap on any shift card to view details

2. **Delete the Shift**
   - Tap the "Delete Shift" button (red button at the bottom)
   - A confirmation dialog will appear showing:
     - Shift details (location and time)
     - Number of employee assignments that will be removed
     - Warning that the action cannot be undone

3. **Confirm Deletion**
   - Review the information in the dialog
   - Tap "Delete" to confirm or "Cancel" to abort
   - Upon confirmation, the shift and all assignments are permanently deleted
   - You'll be automatically navigated back to the shifts list

### Data Integrity & Cascade Deletion

The app ensures complete data integrity when deleting shifts:

- **Automatic Assignment Cleanup**: All `ShiftAssignment` records for the deleted shift are automatically removed
- **No Orphaned Data**: Database foreign key constraints with `CASCADE` ensure no orphaned assignment records remain
- **Employee Safety**: Employee records are never deleted - only the assignment relationship is removed
- **Other Shifts Unaffected**: Deleting one shift does not affect other shifts or their assignments

### Technical Implementation

- **Database Level**: Foreign key constraint with `onDelete = ForeignKey.CASCADE` ensures automatic cleanup
- **Repository Layer**: `deleteShift()` method handles the deletion process
- **UI Layer**: Confirmation dialog prevents accidental deletions
- **User Feedback**: Clear warnings and error messages guide the user

### Use Cases

- **Cleanup Generated Shifts**: Delete shifts created from templates that are no longer needed
- **Correct Mistakes**: Remove incorrectly created shifts
- **Schedule Adjustments**: Delete shifts when schedule changes occur
- **Bulk Operations**: Delete multiple shifts individually as needed

## ✏️ Edit Shift Feature

### Overview

The app allows you to edit existing shifts to update their details such as location, time, required skills, and staffing requirements. This is useful for making adjustments to shifts without having to delete and recreate them.

### How to Edit a Shift

1. **Navigate to Shift Details**
   - Open the Shifts screen
   - Tap on any shift card to view details

2. **Open Edit Dialog**
   - Tap the "Edit" icon in the top-right corner of the screen, OR
   - Tap the "Edit Shift" button (secondary colored button) at the bottom

3. **Modify Shift Details**
   - **Location**: Update the shift location
   - **Start Date**: Change the date (format: YYYY-MM-DD)
   - **Start Time**: Modify start time (format: HH:mm, 24-hour)
   - **End Time**: Modify end time (format: HH:mm, 24-hour)
   - **Required Skills**: Update comma-separated list of skills
   - **Minimum Staffing**: Adjust the number of employees needed

4. **Save Changes**
   - Review all changes in the dialog
   - Tap "Save" to apply changes
   - The shift details will be updated immediately
   - Any validation errors will be shown in the dialog

### Validation & Safety

The edit feature includes comprehensive validation:

- **Time Validation**: Ensures end time is after start time
- **Date Format**: Validates date format (YYYY-MM-DD)
- **Time Format**: Validates time format (HH:mm, 24-hour)
- **Required Fields**: Location, date, and times are mandatory
- **Data Integrity**: Existing employee assignments are preserved (unless time conflicts occur)
- **Error Feedback**: Clear error messages for invalid inputs

### Important Notes

- **Time Conflicts**: If you change shift times, existing assignments may need to be reviewed for conflicts
- **Skill Changes**: Updating required skills may affect which employees can be assigned
- **Assignment Preservation**: Employee assignments are maintained unless they conflict with new times
- **Real-time Updates**: Changes are immediately reflected in the shift list and detail views

### Technical Implementation

- **Repository Layer**: `updateShift()` method handles the update process
- **Database**: Room's `@Update` annotation ensures efficient updates
- **UI Layer**: Form validation prevents invalid data entry
- **State Management**: ViewModel automatically reloads shift details after update

### Use Cases

- **Time Adjustments**: Change shift start/end times when schedules change
- **Location Updates**: Move shifts to different locations
- **Skill Requirements**: Update required skills as job requirements evolve
- **Staffing Changes**: Adjust minimum staffing levels based on business needs
- **Corrections**: Fix mistakes in shift details without deleting and recreating

## 📋 Shift Templates Feature

### Overview

Shift Templates allow you to create reusable shift patterns and generate multiple shifts efficiently. This feature is perfect for recurring schedules like weekly retail shifts, daily kitchen coverage, or regular cafe operations.

### How to Use Shift Templates

1. **Access Templates**
   - Navigate to the Shifts screen
   - Tap the Templates icon (📅) in the top-right corner
   - Or use the navigation menu

2. **Create a Template**
   - Tap the "+" (Add) button
   - Fill in template details:
     - **Name**: Descriptive name (e.g., "Morning Retail Shift")
     - **Location**: Where the shift takes place
     - **Start Time**: Hour and minute (24-hour format)
     - **Duration**: Number of hours
     - **Required Skills**: Comma-separated list (e.g., "Cashier, Supervisor")
     - **Minimum Staffing**: Number of employees needed
     - **Day Pattern**: 
       - Select "Daily" for every day
       - Or select specific days (Mon, Tue, Wed, etc.)

3. **Generate Shifts from Template**
   - Open a template card
   - Tap "Generate Shifts" button
   - Enter start date and end date (format: YYYY-MM-DD)
   - Tap "Generate"
   - Shifts are automatically created based on the template settings

4. **Manage Templates**
   - **Edit**: Tap the edit icon on any template card
   - **Delete**: Tap the delete icon to remove a template
   - **View**: Tap a template card to see details

### Template Features

- **Flexible Scheduling**: Create templates for daily or specific day-of-week patterns
- **Bulk Generation**: Generate multiple shifts at once for a date range
- **Reusable Patterns**: Save common shift patterns for quick reuse
- **Time Management**: Set start time and duration for consistent scheduling
- **Skill Requirements**: Define required skills that apply to all generated shifts
- **Staffing Levels**: Set minimum staffing requirements per template

### Example Use Cases

- **Weekly Retail Shifts**: Create a template for "Monday-Friday 9AM-5PM Retail" and generate for the entire month
- **Daily Kitchen Coverage**: Template for "Daily 2PM-10PM Kitchen" to ensure consistent coverage
- **Weekend Shifts**: Template for "Saturday-Sunday 6AM-2PM Cafe" for weekend operations

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
- **Edit Functionality**: In-place editing of shifts with form validation
- **Confirmation Dialogs**: Safe deletion with clear warnings and impact information
- **Cascade Deletion Feedback**: Shows number of assignments that will be removed before deletion
- **Data Safety**: All destructive actions require explicit confirmation
- **Input Validation**: Real-time validation with helpful error messages

## 🔄 Known Limitations & Future Improvements

### Current Limitations

1. **No Calendar View**: Currently shows list view only (calendar view is a bonus feature)
2. **No Rest Period Validation**: Minimum rest periods between shifts not implemented
3. **No Availability Windows**: Employees don't have explicit availability schedules
4. **No Notifications**: No push notifications for shift assignments
5. **Basic Date Input**: Date selection in templates uses text input (could be improved with date picker)

### Future Improvements

1. **Calendar View**: Visual calendar representation of shifts
2. **Rest Period Validation**: Enforce minimum rest between shifts
3. **Availability Management**: Allow employees to set availability windows
4. **Enhanced Templates**: 
   - Template preview before generation
   - Template duplication
   - Advanced date picker UI
   - Template usage statistics
5. **Notifications**: Push notifications for assignments and reminders
6. **Offline Sync**: Better offline support with sync capabilities
7. **Export/Import**: Export schedules to CSV/PDF
8. **Multi-language Support**: Internationalization
9. **Template Scheduling**: Schedule automatic shift generation
10. **Template Groups**: Organize templates by department or location

## 📄 License

This project is created as part of an assignment and is not intended for public distribution.

## 👤 Author

Naveen Kumar BV

## 📞 Support

For issues or questions, please contact the development team.

---

**Note**: This app demonstrates production-quality Android development with modern architecture patterns, comprehensive business logic validation, and a polished user interface.

