# City Movie Quest

A Kotlin Multiplatform (KMP) app for exploring movie filming locations through city quests. Entire UI built with Compose Multiplatform.

## Stack

- **Language:** Kotlin 1.9.22
- **UI:** Compose Multiplatform 1.6.1
- **Database:** SQLDelight 2.0.1
- **Maps:** OSMDroid (Android), Canvas fallback (Desktop/Web)
- **Serialization:** Kotlinx Serialization 1.6.3
- **Target platforms:** Android, Desktop (JVM), Web (JS)

## Project Structure

```
quest-city/
├── shared/                    # Shared module (all logic and UI)
│   └── src/
│       ├── commonMain/        # Shared code
│       ├── androidMain/       # Android-specific code
│       ├── desktopMain/       # Desktop-specific code
│       └── jsMain/            # Web-specific code
├── app-android/               # Android app
├── app-desktop/               # Desktop app
└── app-web/                   # Web app
```

## Running

### Android
```bash
JAVA_HOME=/path/to/jdk17 ./gradlew :app-android:installDebug
```

### Desktop
```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew :app-desktop:run
```

### Web
```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew :app-web:jsBrowserDevelopmentRun
```

> **Note:** JDK 21 is required. The default system JDK (26) is not compatible with Kotlin 1.9.22.

## Adding a New Quest

1. Create a JSON file in `shared/src/commonMain/composeResources/files/`
2. Follow the format from `quest_blade_petersburg.json`
3. Load images into `shared/src/commonMain/composeResources/files/`
4. Import via `QuestRepository.importQuestFromJson()`

## Quest JSON Format

```json
{
  "id": "unique_id",
  "title": "Quest Title",
  "description": "Description",
  "city": "City",
  "difficulty": "easy|medium|hard",
  "durationMin": 60,
  "locations": [
    {
      "id": "loc_id",
      "lat": 59.9398,
      "lon": 30.3146,
      "radiusM": 150.0,
      "orderIndex": 0,
      "tasks": [
        {
          "id": "task_id",
          "type": "quiz|count|fact_or_fict",
          "prompt": "Question text",
          "answerKey": "Correct answer",
          "hints": ["Hint 1"],
          "rewardPoints": 10,
          "imdbFact": "IMDb fact"
        }
      ]
    }
  ]
}
```

## Task Types

- **Quiz** — multiple choice question
- **Count** — numeric input (counting task)
- **Fact or Fiction** — true/false with IMDb fact explanation

## Testing

```bash
# Unit tests
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew :shared:desktopTest

# Instrumentation tests (Android)
JAVA_HOME=/opt/homebrew/opt/openjdk@21 ./gradlew :shared:connectedAndroidTest
```

## Architecture

- **Domain layer:** models, repository interfaces, use cases (geo, answer checking, progress)
- **Data layer:** repository implementations, JSON parser, SQLDelight (planned)
- **UI layer:** Compose screens, ViewModels with StateFlow, platform map adapters
- **Platform layer:** OSMDroid integration (Android), Canvas fallback (Desktop/Web)

## License

MIT License — see [LICENSE](LICENSE).
