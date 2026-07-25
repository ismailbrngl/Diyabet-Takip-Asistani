<div align="center">

[Türkçe](README.md) | **English**

# 💉 Diabetes Tracking Assistant

**A modern, practical, and offline-first blood glucose and insulin tracking Android application for diabetic individuals.**

[Features](#-key-features) • [Screenshots](#-screenshots) • [Tech Stack](#%EF%B8%8F-tech-stack--architecture) • [Installation](#-installation--setup) • [Privacy](#-privacy--security)

---

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android)](https://developer.android.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![UI](https://img.shields.io/badge/UI-Material%20Design%203-blueviolet?style=for-the-badge&logo=materialdesign)](https://m3.material.io/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

</div>

---

## 📖 About

**Diabetes Tracking Assistant** is an **Android** application designed to help diabetic patients log their daily blood glucose levels, never miss their insulin injection times, and export comprehensive PDF reports for doctor visits.

Built with an **Offline-First** architecture, user data privacy and security are prioritized by design.

---

## ✨ Key Features

* ⏰ **Smart Insulin Reminder:** Scheduled injection notifications using `WorkManager` for reliable, battery-friendly performance.
* 🩸 **Glucose Level Tracking:** Log blood glucose readings with automatic color-coding based on status (Low, Normal, High) and quick filtering.
* 📱 **Home Screen Widget:** View the latest reading and status directly on the Android home screen without opening the app.
* 📊 **Visual Analytics & Charts:** Track blood glucose trends over time using `MPAndroidChart`.
* 📄 **Doctor PDF Reports:** Export records within selected date ranges into professional PDF documents via `iText7`.
* 🌙 **Dynamic Material 3 Theme:** Full support for both Light and Dark themes according to Material Design 3 guidelines.
* 🔒 **100% On-Device Data Security:** All measurements and personal data are stored locally using `Room Database`.

---

## 📸 Screenshots

| Home (Light) | Home (Dark) | History & Filter | PDF Report |
| :---: | :---: | :---: | :---: |
| ![Home Light](docs/screenshots/home_light.png) | ![Home Dark](docs/screenshots/home_dark.png) | ![Glucose](docs/screenshots/glucose.png) | ![Report](docs/screenshots/report.png) |

---

## 🛠️ Tech Stack & Architecture

The app follows Google's **Modern Android Development (MAD)** guidelines and the **MVVM** architecture pattern.

* **Language:** Kotlin & Kotlin Coroutines
* **Architecture:** MVVM (Model-View-ViewModel), Repository Pattern
* **UI:** Material Design 3, ViewBinding, ConstraintLayout, Navigation Component
* **Local Database:** Room Database
* **Background Processing:** WorkManager
* **Data Visualization:** MPAndroidChart
* **PDF Generation:** iText7 Core
* **Code Optimization:** R8 / ProGuard
* **Monetization:** Google AdMob SDK

---

## 📁 Project Structure

```text
com.example.insulinneedlereminder/
├── data/
│   ├── db/          # Room Database and DAO layer
│   ├── entity/      # Database entity models (GlucoseRecord, etc.)
│   └── repository/  # Data source management
├── ui/
│   ├── adapter/     # RecyclerView Adapters (DiffUtil / ListAdapter)
│   ├── glucose/     # Glucose entry, list view, and ViewModel
│   ├── history/     # Measurement history & filtering screen
│   ├── widget/      # Home screen Widget and BroadcastReceiver
│   └── report/      # PDF report generation UI
└── util/            # Helpers (PrefsManager, Constants)