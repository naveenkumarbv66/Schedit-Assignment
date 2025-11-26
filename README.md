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

## 🔧 Troubleshooting & Common Issues

This section documents errors encountered during development and their solutions. This can help developers understand common pitfalls and how to resolve them.

### 1. CoroutineScope Type Mismatch Error

**Error:**
```
e: file:///.../ScheduleApplication.kt:17:44 Argument type mismatch: 
actual type is 'com.naveen.schedittestapp.ScheduleApplication', 
but 'kotlinx.coroutines.CoroutineScope' was expected.
```

**Cause:**
The `InitialDataProvider.initializeData()` function expected a `CoroutineScope`, but the `Application` instance (`this`) was passed instead.

**Solution:**
Created an explicit `CoroutineScope` within `ScheduleApplication` using `SupervisorJob()` and `Dispatchers.Default`:

```kotlin
class ScheduleApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        InitialDataProvider.initializeData(applicationScope, ...)
    }
}
```

**Key Takeaway:** Always pass the correct type to functions. Use `SupervisorJob()` for application-level coroutine scopes to prevent cancellation of child coroutines from affecting the parent.

---

### 2. Smart Cast Error with Delegated Properties

**Error:**
```
e: file:///.../EmployeeDetailScreen.kt:68:36 Smart cast to 'kotlin.String' is impossible, 
because 'error' is a delegated property.
```

**Cause:**
Kotlin cannot smart cast delegated properties (like `uiState.error` from `StateFlow`) after a null check because the property access might return a different value on subsequent reads.

**Solution:**
Store the delegated property value in a local, non-delegated variable after the null check:

```kotlin
// ❌ Wrong - doesn't work with delegated properties
if (uiState.error != null) {
    Text(uiState.error) // Error: smart cast impossible
}

// ✅ Correct - store in local variable
val errorMessage = uiState.error
if (errorMessage != null) {
    Text(errorMessage) // Works!
}
```

**Affected Files:**
- `EmployeeDetailScreen.kt`
- `ShiftDetailScreen.kt`
- `EmployeesScreen.kt`
- `ShiftsScreen.kt`
- `ScheduleScreen.kt`

**Key Takeaway:** Always extract delegated property values to local variables before using them in conditional blocks.

---

### 3. Private Function Access Error

**Error:**
```
e: file:///.../ShiftsScreen.kt:114:58 Cannot access 'fun loadShifts(): Unit': 
it is private in 'com/naveen/schedittestapp/ui/shifts/ShiftsViewModel'.
```

**Cause:**
The `loadShifts()` function in `ShiftsViewModel` was marked as `private`, but it was being called from the UI (specifically, the "Retry" button).

**Solution:**
Changed the function visibility from `private` to `public` (or removed the visibility modifier):

```kotlin
// ❌ Wrong
private fun loadShifts() { ... }

// ✅ Correct
fun loadShifts() { ... }
```

**Key Takeaway:** Functions that need to be called from UI should be public. Only internal helper functions should be private.

---

### 4. NoSuchMethodError on Older Android Versions

**Error:**
```
FATAL EXCEPTION: main java.lang.NoSuchMethodError: 
No static method ofInstant(Ljava/time/Instant;Ljava/time/ZoneId;)Ljava/time/LocalDate; 
in class Ljava/time/LocalDate;
```

**Cause:**
`LocalDate.ofInstant()` is not available on Android API level 25 (minSdk). This method was added in API level 26.

**Solution:**
Two-part fix:

1. **Enable Java 8+ API Desugaring:**
   In `app/build.gradle.kts`:
   ```kotlin
   compileOptions {
       isCoreLibraryDesugaringEnabled = true
       sourceCompatibility = JavaVersion.VERSION_1_8
       targetCompatibility = JavaVersion.VERSION_1_8
   }
   
   dependencies {
       coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
   }
   ```

2. **Use Alternative Method:**
   Created a helper function that works on all API levels:
   ```kotlin
   fun epochSecondsToLocalDate(epochSeconds: Long): LocalDate {
       return Instant.ofEpochSecond(epochSeconds)
           .atZone(ZoneId.systemDefault())
           .toLocalDate()
   }
   ```

**Key Takeaway:** Always check API level compatibility when using newer Java/Kotlin APIs. Use desugaring for `java.time` APIs on older Android versions.

---

### 5. Click Events Not Working in Dialogs

**Error:**
User reported: "on clicking on employee (Jane Doe) in the Assign employee to shift screen, nothing is happening"

**Cause:**
The `Card`'s `onClick` parameter within an `AlertDialog` was not reliably registering click events. This is a known issue with nested clickable components in Compose dialogs.

**Solution:**
Replaced `Card(onClick = ...)` with `Surface(modifier = Modifier.clickable { ... })`:

```kotlin
// ❌ Wrong - unreliable in dialogs
Card(onClick = { onEmployeeSelected(employee) }) {
    // content
}

// ✅ Correct - reliable click handling
Surface(
    modifier = Modifier.clickable { onEmployeeSelected(employee) },
    color = MaterialTheme.colorScheme.surface
) {
    // content
}
```

**Key Takeaway:** Use `Modifier.clickable()` on `Surface` instead of `Card`'s `onClick` parameter when inside dialogs for more reliable event handling.

---

### 6. Infinite Loading - Flow Collection Blocking

**Error:**
User reported: "assign employee to shift screen, where both Employees and Shifts column nothing is displaying and its showing only loading circular animation"

**Cause:**
The `AssignmentViewModel.loadData()` function used two sequential `collect` calls:
```kotlin
repository.getAllEmployees().collect { employees -> ... }
repository.getAllShifts().collect { shifts -> ... }
```
Since Room's `Flow`s are infinite streams, the first `collect` never completes, preventing the second one from ever running.

**Solution:**
Used `combine()` to collect both flows concurrently:

```kotlin
// ❌ Wrong - blocks forever
viewModelScope.launch {
    repository.getAllEmployees().collect { employees ->
        _uiState.value = _uiState.value.copy(employees = employees)
    }
    repository.getAllShifts().collect { shifts -> // Never reached!
        _uiState.value = _uiState.value.copy(shifts = shifts)
    }
}

// ✅ Correct - collects both concurrently
viewModelScope.launch {
    combine(
        repository.getAllEmployees(),
        repository.getAllShifts()
    ) { employees, shifts ->
        _uiState.value = _uiState.value.copy(
            employees = employees,
            shifts = shifts,
            isLoading = false
        )
    }.collect()
}
```

**Key Takeaway:** Use `combine()` or `zip()` when you need to collect multiple `Flow`s concurrently. Sequential `collect` calls on infinite flows will block.

---

### 7. Dropdown Menu Not Appearing

**Error:**
User reported: "In the Schedule screen, top right icon is not working."

**Cause:**
The top-right icon was setting a boolean state (`showViewModeMenu = true`), but no UI element was reacting to this state to display a menu.

**Solution:**
Implemented a `DropdownMenu` that appears when the state is true:

```kotlin
// ❌ Wrong - only sets state, no UI response
IconButton(onClick = { showViewModeMenu = true }) {
    Icon(Icons.Default.ViewModule, null)
}

// ✅ Correct - shows dropdown menu
IconButton(onClick = { showViewModeMenu = true }) {
    Icon(Icons.Default.ViewModule, null)
}

DropdownMenu(
    expanded = showViewModeMenu,
    onDismissRequest = { showViewModeMenu = false }
) {
    // Menu items
}
```

**Key Takeaway:** Always pair state changes with corresponding UI elements that react to those states.

---

### 8. Missing Import Errors

**Errors:**
- `Unresolved reference 'Alignment'` in `CreateTemplateDialog.kt`
- `Unresolved reference 'FontWeight'` in `GenerateShiftsDialog.kt`

**Cause:**
Missing import statements for commonly used Compose components.

**Solution:**
Added the missing imports:

```kotlin
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
```

**Key Takeaway:** Always check imports when encountering "Unresolved reference" errors. Android Studio's auto-import (Alt+Enter) can help, but sometimes manual imports are needed.

---

### 9. Git Authentication Issues

#### Issue 9.1: Permission Denied (403 Error)

**Error:**
```
remote: Permission to naveenkumarbv66/Schedit-Assignment.git denied to naveen-kumar-bv. 
fatal: unable to access 'https://github.com/naveenkumarbv66/Schedit-Assignment.git/': 
The requested URL returned error: 403
```

**Cause:**
Git was using cached credentials for a different GitHub account (`naveen-kumar-bv` instead of `naveenkumarbv66`), or HTTPS authentication was not properly configured.

**Solution Steps:**

1. **Check current remote URL:**
   ```bash
   git remote -v
   ```

2. **Check current Git user configuration:**
   ```bash
   git config user.name
   git config user.email
   ```

3. **Switch remote to HTTPS with username in URL:**
   ```bash
   git remote set-url origin https://naveenkumarbv66@github.com/naveenkumarbv66/Schedit-Assignment.git
   ```

4. **Clear cached credentials:**
   ```bash
   # For credential helper cache
   git credential-cache exit
   
   # Or reject specific URL
   git credential reject https://github.com
   
   # For macOS Keychain (if using)
   git credential-osxkeychain erase
   host=github.com
   protocol=https
   # (Press Enter twice)
   ```

5. **Verify remote URL:**
   ```bash
   git remote -v
   ```

6. **Create Personal Access Token (PAT):**
   - Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Click "Generate new token (classic)"
   - Select scopes: `repo` (full control of private repositories)
   - Copy the token (you won't see it again!)

7. **Push using PAT:**
   ```bash
   git push -u origin main
   # When prompted:
   # Username: naveenkumarbv66
   # Password: <paste your PAT here>
   ```

**Alternative Solution (SSH):**

1. **Check for existing SSH keys:**
   ```bash
   ls -al ~/.ssh
   ```

2. **Generate SSH key (if needed):**
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   # Press Enter to accept default location
   # Enter passphrase (optional but recommended)
   ```

3. **Add SSH key to ssh-agent:**
   ```bash
   eval "$(ssh-agent -s)"
   ssh-add ~/.ssh/id_ed25519
   ```

4. **Copy public key to clipboard:**
   ```bash
   # macOS
   pbcopy < ~/.ssh/id_ed25519.pub
   
   # Linux
   cat ~/.ssh/id_ed25519.pub | xclip -selection clipboard
   ```

5. **Add SSH key to GitHub:**
   - Go to GitHub → Settings → SSH and GPG keys
   - Click "New SSH key"
   - Paste your public key
   - Click "Add SSH key"

6. **Switch remote to SSH:**
   ```bash
   git remote set-url origin git@github.com:naveenkumarbv66/Schedit-Assignment.git
   ```

7. **Test SSH connection:**
   ```bash
   ssh -T git@github.com
   # Should see: "Hi naveenkumarbv66! You've successfully authenticated..."
   ```

8. **Push using SSH:**
   ```bash
   git push -u origin main
   ```

**Key Takeaway:** 
- For HTTPS, use Personal Access Tokens instead of passwords (GitHub deprecated password authentication in 2021)
- For SSH, ensure SSH keys are properly configured and added to GitHub
- Always verify remote URL with `git remote -v` before pushing

---

#### Issue 9.2: Files Not Tracked - Only README.md Visible

**Error:**
User reported: "the code or project is not pushed. Only README.md file visible in github"

**Cause:**
Project files were not added to Git staging area before commit. Only `README.md` was tracked and committed.

**Solution Steps:**

1. **Check Git status:**
   ```bash
   git status
   # Shows untracked files
   ```

2. **Check what files are tracked:**
   ```bash
   git ls-files
   # Only shows README.md
   ```

3. **Add all files to staging:**
   ```bash
   git add .
   # Or add specific files:
   git add app/
   git add gradle/
   git add build.gradle.kts
   ```

4. **Verify files are staged:**
   ```bash
   git status
   # Should show all files as "Changes to be committed"
   ```

5. **Commit all files:**
   ```bash
   git commit -m "Add complete Workforce Scheduling Android App"
   ```

6. **Check commit includes all files:**
   ```bash
   git show --name-only --oneline HEAD
   # Lists all files in the commit
   ```

7. **Push to remote:**
   ```bash
   git push -u origin main
   ```

8. **Verify on GitHub:**
   - Check repository on GitHub
   - All project files should now be visible

**Key Takeaway:** Always check `git status` before committing to ensure all intended files are staged. Use `git add .` to add all untracked files.

---

#### Issue 9.3: Remote Not Configured

**Error:**
```
fatal: No configured push destination.
Either specify the URL on the command line or configure a remote repository using
git remote add <name> <url>
```

**Cause:**
Local repository doesn't have a remote configured, or remote was removed.

**Solution:**

1. **Check if remote exists:**
   ```bash
   git remote -v
   # Shows nothing if no remote configured
   ```

2. **Add remote repository:**
   ```bash
   # For HTTPS
   git remote add origin https://github.com/naveenkumarbv66/Schedit-Assignment.git
   
   # For SSH
   git remote add origin git@github.com:naveenkumarbv66/Schedit-Assignment.git
   ```

3. **Verify remote added:**
   ```bash
   git remote -v
   ```

4. **Push to remote:**
   ```bash
   git push -u origin main
   ```

**Key Takeaway:** Always configure remote before pushing. Use `-u` flag on first push to set upstream tracking.

---

#### Issue 9.4: Branch Name Mismatch

**Error:**
```
error: src refspec main does not match any
error: failed to push some refs to 'origin'
```

**Cause:**
Local branch might be named `master` but trying to push to `main`, or branch doesn't exist.

**Solution:**

1. **Check current branch:**
   ```bash
   git branch
   # Shows current branch with *
   ```

2. **Check all branches (local and remote):**
   ```bash
   git branch -a
   ```

3. **Rename branch if needed:**
   ```bash
   # If on master, rename to main
   git branch -m master main
   ```

4. **Or push to correct branch:**
   ```bash
   # If branch is master
   git push -u origin master
   ```

5. **Set default branch on GitHub:**
   - Go to repository Settings → Branches
   - Change default branch from `master` to `main` (if needed)

**Key Takeaway:** Ensure local and remote branch names match. Modern Git defaults to `main` instead of `master`.

---

#### Issue 9.5: Large File or .gitignore Issues

**Error:**
```
remote: error: File app/build/... is 150.00 MB; this exceeds GitHub's file size limit of 100.00 MB
```

**Cause:**
Build artifacts or large files are being committed.

**Solution:**

1. **Check .gitignore file exists:**
   ```bash
   cat .gitignore
   ```

2. **Add Android build directories to .gitignore:**
   ```bash
   # If .gitignore doesn't exist, create it
   cat > .gitignore << EOF
   # Built application files
   *.apk
   *.ap_
   *.aab
   
   # Files for the ART/Dalvik VM
   *.dex
   
   # Java class files
   *.class
   
   # Generated files
   bin/
   gen/
   out/
   release/
   
   # Gradle files
   .gradle/
   build/
   
   # Local configuration file (sdk path, etc)
   local.properties
   
   # Proguard folder generated by Eclipse
   proguard/
   
   # Log Files
   *.log
   
   # Android Studio Navigation editor temp files
   .navigation/
   
   # Android Studio captures folder
   captures/
   
   # IntelliJ
   *.iml
   .idea/workspace.xml
   .idea/tasks.xml
   .idea/gradle.xml
   .idea/assetWizardSettings.xml
   .idea/dictionaries
   .idea/libraries
   .idea/jarRepositories.xml
   .idea/caches
   .idea/modules.xml
   .idea/.name
   .idea/compiler.xml
   .idea/misc.xml
   .idea/vcs.xml
   .idea/shelf
   
   # Keystore files
   *.jks
   *.keystore
   
   # External native build folder generated in Android Studio 2.2 and later
   .externalNativeBuild
   .cxx/
   
   # Google Services (e.g. APIs or Firebase)
   google-services.json
   
   # Freeline
   freeline.py
   freeline/
   freeline_project_description.json
   
   # fastlane
   fastlane/report.xml
   fastlane/Preview.html
   fastlane/screenshots
   fastlane/test_output
   fastlane/readme.md
   
   # Version control
   vcs.xml
   
   # lint
   lint/intermediates/
   lint/generated/
   lint/outputs/
   lint/tmp/
   lint-results*.xml
   EOF
   ```

3. **Remove already tracked files:**
   ```bash
   # Remove from Git but keep locally
   git rm -r --cached app/build/
   git rm -r --cached .gradle/
   ```

4. **Commit .gitignore:**
   ```bash
   git add .gitignore
   git commit -m "Add .gitignore to exclude build files"
   ```

5. **Push changes:**
   ```bash
   git push origin main
   ```

**Key Takeaway:** Always use `.gitignore` to exclude build artifacts, IDE files, and sensitive data. Never commit `build/` directories or `.gradle/` folders.

---

### 10. Only README.md Visible on GitHub

**Error:**
User reported: "the code or project is not pushed. Only README.md file visible in github"

**Cause:**
Project files were not added to Git staging area before commit. Only `README.md` was tracked.

**Solution:**
Added all files and committed:

```bash
git add .
git commit -m "Add complete Workforce Scheduling Android App"
git push -u origin main
```

**Key Takeaway:** Always check `git status` before committing to ensure all intended files are staged. Use `git add .` to add all untracked files.

---

### General Debugging Tips

1. **Check Logcat:** Most runtime errors appear in Android Studio's Logcat with stack traces
2. **Build Output:** Compilation errors show in the Build output window with file paths and line numbers
3. **Linter:** Use `read_lints` or Android Studio's inspection to catch errors early
4. **State Flow Debugging:** Use `.onEach { Log.d("TAG", it) }` to debug Flow emissions
5. **Compose Preview:** Use `@Preview` annotations to test UI components in isolation
6. **Database Inspection:** Use Android Studio's Database Inspector to verify Room database state
7. **Git Status:** Always check `git status` before committing to see what files are tracked

---

**Note**: This app demonstrates production-quality Android development with modern architecture patterns, comprehensive business logic validation, and a polished user interface.

