# TimTim - Your Private Work Hours Companion

<div align="center">
  <img src="app/src/main/assets/app_logo.svg" width="120" height="120" alt="TimTim Logo" />
  <p><strong>A professional, privacy-first work hours and attendance tracker for Android.</strong></p>
</div>

---

**TimTim** is a minimalist, high-performance application built with the latest Android standards. It empowers users to track daily attendance, manage work-life balance through intelligent goal tracking, and visualize monthly productivity—all within a beautiful, modern UI.

## 🚀 Key Features

*   **🍱 Bento-Style Dashboard**: A modern "Bento" design system for instant visualization of your monthly performance, including total hours, overtime, and work deficit at a glance.
*   **📅 Dual Calendar Support**: Seamless support for both **Gregorian** and **Hijri Shamsi** (Solar Hijri) calendar systems, including accurate conversions and month initializations.
*   **⏲️ Live Work Clock**: A dedicated real-time screen with an animated circular progress dial and countdown to your estimated checkout time.
*   **⚡ Intelligent Logging**: "Exit Now" functionality that intelligently handles midnight shifts and respects user-defined work limits.
*   **🛡️ Privacy First**: 100% Offline. All your data stays on your device in a local **Room Database**. No cloud tracking, no analytics, no external servers.
*   **🔄 Data Portability**: A robust Backup & Restore system using JSON files. Export your records to keep them safe or migrate to a new device seamlessly.
*   **🎨 Deep Personalization**:
    *   **Profiles**: Multiple high-quality avatar styles and customizable display names.
    *   **Theming**: Support for Light/Dark modes with vibrant accent palettes (Emerald, Purple, Amber).
    *   **Policies**: Customizable daily targets, minimum entry limits, and maximum exit times to fit any workspace.

## 🛠 Architecture & Tech Stack

This project follows **Clean Architecture** principles and the **MVI (Model-View-Intent)** design pattern to ensure a scalable and testable codebase.

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% Declarative UI)
*   **DI**: [Koin](https://insert-koin.io/) (Dependency Injection)
*   **Database**: [Room](https://developer.android.com/training/data-storage/room) (SQLite abstraction)
*   **Concurrency**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Navigation**: Single Activity with custom State-driven navigation
*   **Architecture**: Domain-driven design with specialized Use Cases and a reactive Presentation layer.

## 📂 Project Structure

```text
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java/com/example
│   │   │   │   ├── data          # Repository implementations, Room DB & Mappers
│   │   │   │   ├── di            # Koin Modules for Dependency Injection
│   │   │   │   ├── domain        # Pure Kotlin Business Logic (Entities, Use Cases, Repository Interface)
│   │   │   │   ├── ui            # Presentation Layer
│   │   │   │   │   ├── mvi       # Immutable State, Intent, and Effect definitions
│   │   │   │   │   ├── components# Reusable UI Atoms and Molecules
│   │   │   │   │   ├── screens   # Feature-level screens (Timesheet, Report, Settings, etc.)
│   │   │   │   │   └── theme     # Material 3 Design System implementation
│   │   │   │   └── util          # Helpers for Calendars, Backup, and PDF Export
```

## ⚙️ Getting Started

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/alimokarian/TimTim.git
    ```
2.  **Open in Android Studio**:
    *   Recommended: **Android Studio Ladybug** or newer.
    *   Gradle Wrapper: **9.3.1**
3.  **Setup Secrets**:
    *   Copy `.env.example` to `.env`.
    *   The app will automatically package these as `BuildConfig` fields using the Secrets Gradle Plugin.
4.  **Build & Run**: Select the `app` module and deploy to an emulator or physical device (Min SDK 24).

## 🔒 Security & Privacy

**TimTim** is built on the belief that your work schedule is your private business.
*   **No Internet Permission**: The core app does not require internet access to function.
*   **No Tracking**: No identifiers or telemetry are ever collected.
*   **Local Ownership**: You have full control over your data—clear, reset, or export it at any time.

---

**Developed with precision by [Ali Mokarian (balius)](mailto:alimokarian32@gmail.com)**
*Expert Senior Android Engineer committed to Clean Code and superior UX.*
