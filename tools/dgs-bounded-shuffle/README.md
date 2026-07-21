# Matematik — ±5 kaymalı karışık sıra

## Kural

- **Kanonik sıra:** Önce **2025** matematik 1…N, sonra **2024** 1…N, … **2020** (`counts.json`).
- **Uygulama sırası:** Her soru, kanonik sıradaki yerinden en fazla **5** pozisyon yukarı veya aşağı gidebilir (1. ile 45. yer değiştirmez).
- Örnek (seed `ismail2004` / `392653168`): Sıra 2 = **2025 · Soru 5**; Sıra 3 = **2025 · Soru 1** (kayma +2).

## Çalıştırma

```text
node tools/dgs-bounded-shuffle/bounded-order.mjs ismail2004
```

Önce `counts.json` içindeki yıllık soru sayılarını kendi PDF / kitabına göre düzenle (hepsi 80 olmak zorunda değil).

## Çıktı

- `DGS_matematik_bounded_shuffle.md` / `.json` (proje kökü + OneDrive `dgs` kopyası)
- PDF içinde kutu kırpma yok; sıra listesi + PDF’den soru açarsın.
