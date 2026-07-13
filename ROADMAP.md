# MVP Roadmap

## Phase 1: KMP Project Setup (1–3 days)

### Deliverables
- KMP project with Gradle configuration
- SQLDelight schema and basic database
- First Compose screen (quest catalog)

### Definition of Done
- Project builds for Android, Desktop, and Web
- SQLDelight generates code from `.sq` files
- Catalog screen displays a list of quests (mock data)
- Unit tests for geo-logic pass

### Risks
- Library version incompatibilities
- Compose Multiplatform issues on Web target

### Mitigations
- Use verified versions from official documentation
- Test on each platform after every change

---

## Phase 2: Map and JSON Quests (1–3 days)

### Deliverables
- Map with markers and radius circles (OSMDroid + Compose wrapper)
- JSON quest loaded into the database
- Offline mode for map display

### Definition of Done
- Map displays markers parsed from JSON
- Radius circles rendered around locations
- Quest loads from a JSON file at startup
- Progress persists in SQLDelight

### Risks
- OSMDroid not available on Desktop/Web
- Geolocation unavailable on Desktop

### Mitigations
- Use Canvas placeholder for Desktop/Web
- Implement manual coordinate input for testing

---

## Phase 3: Task Screen and Verification (1–3 days)

### Deliverables
- Task screen with three task types
- Answer verification and geo-check
- Timer and hints system

### Definition of Done
- All three task types work (quiz, count, fact_or_fict)
- Answers are verified correctly
- Countdown timer runs and is visible
- Hints display on demand
- Wrong-answer penalties are applied

### Risks
- Complex timer animation
- Input issues on mobile devices

### Mitigations
- Use simple Compose animations
- Test on real devices

---

## Phase 4: Polish and Tests (1–3 days)

### Deliverables
- Polished catalog UI
- Progress screen with bar charts
- Unit and instrumentation tests
- README with setup instructions

### Definition of Done
- UI looks consistent across all platforms
- Progress displays correctly with scores
- Tests cover core business logic
- README contains run instructions for all platforms

### Risks
- Inconsistent behavior across platforms
- Slow test execution

### Mitigations
- Regular testing on each platform
- Use mocks to speed up tests

---

## Summary

| Phase | Duration | Key Outcome |
|-------|----------|-------------|
| 1. Project setup | 1–3 days | Buildable KMP project with DB and catalog screen |
| 2. Map & quests | 1–3 days | Map with markers, JSON loading, offline mode |
| 3. Task screen | 1–3 days | Working quiz/count/fact tasks with timer |
| 4. Polish & tests | 1–3 days | Consistent UI, tests, documentation |

**Total estimated time:** 4–12 days for a working MVP.
