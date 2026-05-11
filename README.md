# Forwardex

Forwardex is an offline-first Android automation engine for SMS/call triggers, OTP extraction, rule evaluation, and local action execution.

## Implemented baseline
- Kotlin + Jetpack Compose + Material 3
- Hilt dependency injection
- Room schema for rules, conditions, actions, history, logs, settings, SIM, analytics
- Rule engine pipeline (trigger -> conditions -> actions -> history)
- SMS/call/boot receivers, foreground service, WorkManager recovery worker, alarm fallback scheduler
- OTP parser and URL parser heuristics
- Compose screens: dashboard, rules, rule builder scaffold, history, settings
- Security baseline with encrypted preferences
- Backup/export + import scaffold with encrypted backup payload support
- Permission onboarding and OEM battery-optimization guide screens
- Rule execution upgrades: cooldown enforcement, retry/backoff, and stronger anti-loop guard
- GitHub Actions CI workflow

## Build
1. Install Android Studio Iguana+ and Android SDK 35.
2. Run `./gradlew :app:assembleDebug`.
3. Run unit tests: `./gradlew :app:testDebugUnitTest`.

## Privacy
- Local-only execution by default
- No telemetry uploads
- User-controlled forwarding actions

## Permissions rationale
See `app/src/main/java/com/forwardex/notifications/PermissionRationale.kt`.
