# NIT3213 Assignment 2 — Book Browser

An Android application with login, a book dashboard, and a book-details screen.

## Features

- Login using student ID and case-sensitive first name.
- Input validation and login error messages.
- Dashboard data retrieved using the login keypass.
- RecyclerView showing book title, author, genre, and publication year.
- Details screen showing all book information, including description.
- Loading indicators and dashboard retry handling.

## Technology

- Kotlin and XML layouts
- Android ViewModel and StateFlow
- Kotlin coroutines
- Retrofit and Gson
- Koin dependency injection
- RecyclerView
- JUnit and kotlinx-coroutines-test

## Project structure

- `data/model`: API request and response models
- `data/remote`: Retrofit API interface
- `data/repository`: Login and dashboard data access
- `di`: Koin dependency definitions
- `ui/login`: Login state and ViewModel
- `ui/dashboard`: Dashboard Activity, state, ViewModel, and adapter
- `ui/details`: Selected-book details Activity
- `MainActivity`: Login Activity

## Requirements

- Android Studio compatible with the project's Android Gradle Plugin
- Android SDK 37
- Android device or emulator running API 24 or higher
- Internet access to download dependencies and use the API
- Valid assignment login credentials

## Build and run

1. Clone this repository.
2. Open the project in Android Studio.
3. Install any requested SDK components.
4. Allow Gradle sync to complete.
5. Select the `app` run configuration.
6. Select an emulator or connected Android device.
7. Click Run.

## Login

Enter your student ID without the leading `s`.
Use your first name as the password, preserving its capitalization.

The current login endpoint is `/br/auth`.
Use the endpoint assigned to your class location.

Do not commit real credentials to the repository.

## API

Base URL: `https://nit3213apinew.onrender.com/`

- `POST /br/auth`: Login and receive a keypass
- `GET /dashboard/{keypass}`: Retrieve dashboard entities

The observed dashboard response contains books with:
`title`, `author`, `genre`, `publicationYear`, and `description`.

## Tests

Local unit tests cover:

- Login repository success and failure handling
- Login ViewModel validation and UI-state changes
- Dashboard repository response handling
- Dashboard ViewModel loading, retry, and refresh behavior

Run tests in Android Studio by right-clicking the local test package
and selecting its Run tests option.

Alternatively, on Windows:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

On macOS or Linux:

```bash
./gradlew :app:testDebugUnitTest
```

## Manual testing

Before submission, check:

- Empty login fields show a validation message.
- Incorrect credentials show a login error.
- Valid credentials open the dashboard.
- The dashboard displays the returned books.
- Selecting different books displays the matching details.
- Back navigation returns to the dashboard.
- Network failure shows an error and Retry can recover.
- Screen rotation does not crash the app.