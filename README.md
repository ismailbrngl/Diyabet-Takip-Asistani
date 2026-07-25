<div align="center">

# 💉 Diyabet Takip Asistanı

**Diyabet hastaları için modern, pratik ve tamamen cihaz içi (offline-first) kan şekeri ve insülin takip uygulaması.**

[Özellikler](#-öne-çıkan-özellikler) • [Ekran Görüntüleri](#-ekran-görüntüleri) • [Teknolojiler](#%EF%B8%8F-teknoloji-yığını-ve-mimari) • [Kurulum](#-kurulum-ve-çalıştırma) • [Gizlilik](#-gizlilik-ve-güvenlik)

---

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android)](https://developer.android.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![UI](https://img.shields.io/badge/UI-Material%20Design%203-blueviolet?style=for-the-badge&logo=materialdesign)](https://m3.material.io/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

</div>

---

## 📖 Hakkında

**Diyabet Takip Asistanı**, diyabet hastalarının günlük kan şekeri (glikoz) ölçümlerini kolayca kaydetmelerine, insülin enjeksiyon saatlerini kaçırmamalarına ve doktor kontrollerinde kullanılmak üzere detaylı PDF raporları oluşturmalarına olanak tanıyan **Android** uygulamasıdır. 

Kullanıcı verilerinin gizliliği ön planda tutularak **Offline-First** (İnternetsiz Çalışabilen) mimariyle geliştirilmiştir.

---

## ✨ Öne Çıkan Özellikler

* ⏰ **Akıllı İnsülin Hatırlatıcı:** `WorkManager` altyapısı ile pil dostu, tam zamanında çalışan enjeksiyon bildirimleri.
* 🩸 **Glikoz Seviyesi Takibi:** Ölçülen kan şekeri değerlerini kategoriye göre (Düşük, Normal, Yüksek) renklendirerek kaydetme ve hızlı filtreleme.
* 📱 **Ana Ekran Widget'ı (App Widget):** Uygulamayı açmaya gerek kalmadan son ölçümü ve sağlık durumunu doğrudan telefonun ana ekranında görebilme.
* 📊 **Görsel Analiz & Grafikler:** `MPAndroidChart` ile kan şekeri seyir grafiklerini inceleme.
* 📄 **Doktor Kontrolü İçin PDF Raporu:** `iText7` entegrasyonu sayesinde seçilen tarih aralığındaki tüm kayıtları resmi bir PDF raporuna dönüştürüp paylaşabilme.
* 🌙 **Dinamik Material 3 Tema:** Aydınlık (Light) ve Karanlık (Dark) mod ile tam uyumlu modern arayüz.
* 🔒 **%100 Cihaz İçi Veri Güvenliği:** Tüm ölçümler ve kişisel veriler yalnızca cihazınızdaki `Room Database` veritabanında saklanır.

---

## 📸 Ekran Görüntüleri

| Ana Sayfa (Aydınlık) | Ana Sayfa (Karanlık) | Geçmiş & Filtreleme | PDF Raporlama |
| :---: | :---: | :---: | :---: |
| ![Home Light](docs/screenshots/home_light.png) | ![Home Dark](docs/screenshots/home_dark.png) | ![Glucose](docs/screenshots/glucose.png) | ![Report](docs/screenshots/report.png) |

---

## 🛠️ Teknoloji Yığını ve Mimari

Uygulama, Google'ın Android için önerdiği **Modern Android Development (MAD)** standartlarına ve **MVVM** mimari desenine uygun olarak geliştirilmiştir.

* **Dil:** Kotlin & Kotlin Coroutines
* **Mimari:** MVVM (Model-View-ViewModel), Repository Pattern
* **Arayüz (UI):** Material Design 3, ViewBinding, ConstraintLayout, Navigation Component
* **Veritabanı:** Room Database
* **Arka Plan İşlemleri:** WorkManager
* **Veri Görselleştirme:** MPAndroidChart
* **PDF Oluşturma:** iText7 Core
* **Performans & Kod Koruma:** R8 / ProGuard Optimization
* **Gelir Modeli:** Google AdMob SDK

---

## 📁 Proje Yapısı

```text
com.example.insulinneedlereminder/
├── data/
│   ├── db/          # Room Veritabanı ve DAO katmanı
│   ├── entity/      # Veritabanı model sınıfları (GlucoseRecord vb.)
│   └── repository/  # Veri kaynağı yönetim katmanı
├── ui/
│   ├── adapter/     # RecyclerView Adapter'ları (DiffUtil / ListAdapter)
│   ├── glucose/     # Glikoz ekleme, listeleme ve ViewModel katmanı
│   ├── history/     # Geçmiş ölçümler ve filtreleme ekranı
│   ├── widget/      # Ana ekran Widget bileşeni ve BroadcastReceiver
│   └── report/      # PDF rapor oluşturma ekranı
└── util/            # Yardımcı sınıflar (PrefsManager, Constants)