# DGS PDF — karışık sayfa birleştirici

## Ne yapar?

Tüm kaynak PDF’lerdeki **sayfaları** tek havuza atar, **karıştırır**, **tek bir PDF** olarak yazar. Görseller ve vektör çizimler **orijinal kalitede** kalır (ekran görüntüsüne çevrilmez).

## Ne yapmaz?

**Tek tek soru kırpmaz.** Bir DGS sayfasında genelde birden fazla soru vardır; otomatik “soru kutusu” bulmak için ya PDF içinde metin koordinatları gerekir ya da elle kırpma / OCR. Bu araç **sayfa** düzeyinde karıştırır.

## Çalıştırma

Önce `vendor` içinde `pdf-lib` olmalı (bir kez indir):

```powershell
cd tools\dgs-merge-pdf
Invoke-WebRequest -Uri "https://registry.npmjs.org/pdf-lib/-/pdf-lib-1.17.1.tgz" -OutFile "vendor\pdf-lib.tgz" -UseBasicParsing
New-Item -ItemType Directory -Force -Path vendor | Out-Null
tar -xzf vendor\pdf-lib.tgz -C vendor
```

Sonra (Node yolunu kendi kurulumuna göre düzenle):

```powershell
node merge-shuffled.mjs ismail2004
```

- Çıktı: proje kökü `DGS_karisik_sayisal.pdf` ve OneDrive `...\dgs\` altına kopya.
- **Yalnızca sayısal:** Matematik + `2024 DGS SAYISAL` + `2021` dosyasının sayısal kısmı (ilk 18 sayfa). Tam kitap PDF’leri (ör. tek dosyada Türkçe+sayısal) bu scripte eklenmez; yanlış sayfa tahminiyle Türkçe karışmasın diye.
- Aynı sırayı tekrar üretmek için aynı **seed** argümanını kullan.
- Her sayfanın altına gri küçük etiket: `2022-mat p3` gibi (kaynak + sayfa).

## Kaynak yollar

`merge-shuffled.mjs` içindeki `SOURCES` ve `tryAçık2025()` yolları senin OneDrive yapına göre ayarlı; klasör taşınırsa orayı güncelle.
