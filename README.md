# NIT3213 Assignment 2 — Book Browser

An Android application that authenticates a student, retrieves a book
collection from the assignment API, and displays details about a selected book.

## Features

- Login using a student ID and case-sensitive first name.
- Input validation and login error messages.
- Loading feedback during authentication and dashboard requests.
- Redesigned login interface with styled input fields and icons.
- Dashboard data retrieved using the keypass returned after login.
- RecyclerView showing book title, author, genre, and publication year.
- Locally bundled cover images for known book titles.
- Book rows with interaction styling.
- Details screen showing the selected book's cover and information,
  including its full description.
- Dashboard error messages and Retry handling.
- Back navigation from book details to the dashboard.

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

- Android Studio compatible with the project's Android Gradle Plugin.
- The Android SDK version specified in the app's Gradle configuration.
- An Android device or emulator meeting the configured minimum SDK version.
- Internet access to download dependencies and use the API.
- Valid assignment login credentials.

Use the included Gradle wrapper to build and test the project.

## Build and run

1. Clone this repository.
2. Open the project folder in Android Studio.
3. Install any requested SDK components.
4. Allow Gradle sync to complete.
5. Select the `app` run configuration.
6. Select an emulator or connected Android device.
7. Click Run.
8. Enter valid assignment credentials.

## Login

Enter your student ID without the leading `s`.

Use your first name as the password, preserving its capitalization.

The current login endpoint is `/br/auth`.
Use the endpoint assigned to your class location.

Do not commit real credentials to the repository.

## API

Base URL:

```text
[https://nit3213apinew.onrender.com/](https://nit3213apinew.onrender.com/)
```

Endpoints:

- `POST /br/auth`: Authenticate and receive a keypass.
- `GET /dashboard/{keypass}`: Retrieve dashboard entities.

The observed dashboard response contains books with these fields:

- `title`
- `author`
- `genre`
- `publicationYear`
- `description`

The dashboard displays summary information. Selecting a book opens
its details, including the full description.

## Book-cover images

Book covers are bundled locally with the application and matched to
books by title. They are not downloaded from the assignment API.

The dashboard and Details screen use the matching cover for a known
title. Unmatched titles use a generic book icon.

Authentication and dashboard data retrieval still require internet access.

## Tests

The local unit test suite includes tests for:

- Login repository success and failure handling.
- Login ViewModel validation and UI-state changes.
- Dashboard repository response handling.
- Dashboard ViewModel loading, retry, and refresh behaviour.

Refer to the individual test methods for the exact cases implemented.

### Android Studio

Right-click the local test package and select its Run tests option.

### Windows

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

### macOS or Linux

```bash
./gradlew :app:testDebugUnitTest
```

Unit tests and manual end-to-end checks serve different purposes.
Live login and dashboard requests should also be checked in the running app.

## Manual testing checklist

Mark each item complete only after verifying it in the running app.

- [ ] Empty login fields show a validation message.
- [ ] Incorrect credentials show a login error.
- [ ] Valid credentials open the dashboard.
- [ ] Loading indicators appear during requests.
- [ ] The dashboard displays the returned books.
- [ ] The full book list is accessible by scrolling.
- [ ] Known book titles display their matching covers.
- [ ] Selecting different books displays the matching details.
- [ ] Full descriptions are accessible on the Details screen.
- [ ] Back navigation returns to the dashboard.
- [ ] Network failure shows an error.
- [ ] Retry can recover after connectivity is restored.
- [ ] Screen rotation does not crash the app.

## Limitations

- Live login and dashboard requests depend on network connectivity
  and assignment API availability.
- Local cover matching supports the configured book titles.
  Additional titles may require new cover resources and mappings.