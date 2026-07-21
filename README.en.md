[Türkçe](README.md) | English

---

# 💉 Diabetes Tracking Assistant

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?style=flat-square&logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange?style=flat-square)
![UI](https://img.shields.io/badge/UI-Material%20Design%203-blueviolet?style=flat-square)

Diabetes Tracking Assistant is a modern Android application developed to help diabetes patients never miss their insulin injection times, keep track of their blood glucose levels systematically, and generate doctor-friendly PDF reports.

---

## 📸 Screenshots

| Home (Light) | Home (Dark) | Glucose Logs | Reporting |
| :---: | :---: | :---: | :---: |
| ![Home Light](docs/screenshots/home_light.png) | ![Home Dark](docs/screenshots/home_dark.png) | ![Glucose](docs/screenshots/glucose.png) | ![Report](docs/screenshots/report.png) |

---

## ✨ Key Features

*   **Smart Insulin Reminder:** Scheduled injection notifications powered by `WorkManager` infrastructure.
*   **Blood Glucose Tracking:** Log, monitor, and visualize historical blood sugar values.
*   **Dynamic Dark / Light Mode:** Full theme support adhering to Material 3 standards.
*   **PDF Report Export:** Export tracked data into professional PDF formats optimized for clinical reviews.
*   **Offline-First Storage:** Secure local data persistence utilizing `Room Database`.

---

## 🛠️ Tech Stack & Architecture

*   **Language:** Kotlin
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **UI Components:** Material Design 3, ConstraintLayout, Navigation Component
*   **Local Database:** Room Database
*   **Background Processing:** WorkManager
*   **Data Visualization:** MPAndroidChart
*   **PDF Generation:** iText7
*   **Monetization:** Google AdMob

---

## 🚀 Getting Started

1. Clone the project:
   ```bash
   git clone [https://github.com/ismailbrngl/Diyabet-Takip-Asistani.git](https://github.com/ismailbrngl/Diyabet-Takip-Asistani.git)