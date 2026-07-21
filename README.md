Türkçe | [English](README.en.md)

---

# 💉 Diyabet Takip Asistanı

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?style=flat-square&logo=kotlin)
![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange?style=flat-square)
![UI](https://img.shields.io/badge/UI-Material%20Design%203-blueviolet?style=flat-square)

Diyabet Takip Asistanı, diyabet hastalarının insülin enjeksiyon saatlerini kaçırmaması, kan şekeri (glikoz) değerlerini düzenli kayıt altına alması ve doktor kontrolleri için PDF raporları oluşturabilmesi amacıyla geliştirilmiş modern bir Android uygulamasıdır.

---

## 📸 Ekran Görüntüleri

| Ana Sayfa (Gündüz) | Ana Sayfa (Gece) | Glikoz Kaydı | Raporlama |
| :---: | :---: | :---: | :---: |
| ![Home Light](docs/screenshots/home_light.png) | ![Home Dark](docs/screenshots/home_dark.png) | ![Glucose](docs/screenshots/glucose.png) | ![Report](docs/screenshots/report.png) |

---

## ✨ Öne Çıkan Özellikler

*   **Akıllı İnsülin Hatırlatıcı:** `WorkManager` altyapısı ile zamanlanmış enjeksiyon bildirimleri.
*   **Glikoz Seviyesi Takibi:** Ölçülen kan şekeri değerlerini geçmişe dönük kaydetme ve görselleştirme.
*   **Dinamik Gece / Gündüz Modu:** Material 3 standartlarında tam uyumlu tema desteği.
*   **PDF Rapor Çıktısı:** Kaydedilen verileri doktor kontrolüne uygun PDF formatında dışa aktarma.
*   **Yerel Depolama (Offline-First):** Tüm veriler `Room Database` ile cihazda güvenli şekilde saklanır.

---

## 🛠️ Teknoloji Yığını ve Mimari

*   **Dil:** Kotlin
*   **Mimari:** MVVM (Model-View-ViewModel)
*   **Arayüz Bileşenleri:** Material Design 3, ConstraintLayout, Navigation Component
*   **Yerel Veritabanı:** Room Database
*   **Arka Plan İşlemleri:** WorkManager
*   **Veri Görselleştirme:** MPAndroidChart
*   **PDF Oluşturma:** iText7
*   **Gelir Modeli:** Google AdMob

---

## 📄 Lisans

Bu proje açık kaynaklıdır ve [MIT Lisansı](LICENSE) altında lisanslanmıştır.