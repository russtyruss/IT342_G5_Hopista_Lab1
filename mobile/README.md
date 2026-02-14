# Hopista Mobile (Android Kotlin)

A simple mobile client mirroring the web app, using your Spring Boot backend.

## Features
- Login, Register against `/api/auth/*`
- Stores `accessToken` and `refreshToken` in SharedPreferences
- Profile screen shows JWT claims (subject, issued-at, expires)
- Refresh and Logout actions
- Dashboard with welcome text

## Backend
- Expects backend at `http://localhost:8081/api`.
- On Android emulator, host `localhost` is `10.0.2.2` (already configured).

## Structure
- Project: `mobile/android-kotlin-app`
- App module: `app/`
- Key files:
  - `app/src/main/java/com/example/hopista/mobile/data/ApiService.kt`
  - `app/src/main/java/com/example/hopista/mobile/data/AuthRepository.kt`
  - `app/src/main/java/com/example/hopista/mobile/ui/*Activity.kt`

## Build & Run

### Option A: Android Studio (recommended)
1. Open Android Studio.
2. Select "Open" and choose the `mobile/android-kotlin-app` folder.
3. Let Gradle sync; install SDK/platform if prompted.
4. Run the app on an Emulator or a device.

### Option B: Gradle CLI (if you have Gradle installed)
From the project root:
```powershell
cd mobile/android-kotlin-app
gradle assembleDebug
```
The APK will be in `app/build/outputs/apk/debug/`.

> Note: Gradle wrapper JARs are not committed here. Android Studio will generate/upgrade them automatically when you open the project.

## Notes
- Ensure the backend runs on port `8081` with CORS allowing `http://localhost:3000` (already set). For mobile emulator, CORS is not relevant, but API base URL uses `10.0.2.2`.
- If you use a real device, replace `BASE_URL` in `ApiService.kt` to point to your PC's LAN IP (e.g., `http://192.168.1.100:8081/api/`).
