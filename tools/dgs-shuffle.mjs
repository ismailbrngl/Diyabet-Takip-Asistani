/**
 * DGS PDF'lerinden çıkarılan cevap anahtarlarını karıştırır, Markdown üretir.
 * Kullanım: node tools/dgs-shuffle.mjs [tohum_sayısı]
 */
import { writeFileSync } from "fs";
import { fileURLToPath } from "url";
import { dirname, join } from "path";

const __dirname = dirname(fileURLToPath(import.meta.url));
const outPath = join(__dirname, "..", "DGS_karisik_pratik.md");

/** 2021 DGS.pdf — Sayısal bölüm 1–60 (PDF metin çıktısından) */
const dgs2021Sayisal = [
  "E","C","A","C","A","B","B","E","A","A","D","C","E","E","B","E","D","A","B","C","B","B","D","A","D","C","B","B","B","A","D","A","C","E","C","D","E","A","C","D","E","C","C","E","A","D","D","A","D","E","D","D","B","B","E","C","B","C","D","B",
].map((ans, i) => ({
  kaynak: "2021 DGS.pdf",
  alt: "Sayısal",
  soru: i + 1,
  cevap: ans,
}));

/** 2023 DGS Matematik.pdf — 1–50 (PDF metin çıktısından; 40–42 arası sayfa bölünmesi düzeltildi) */
const dgs2023Mat = [
  "B","A","C","D","E","B","A","D","C","D","E","B","C","C","C","E","A","B","D","C","D","E","D","A","E","A","B","E","A","C","A","E","B","D","C","C","B","D","C","A","D","C","E","B","D","A","D","D","C","B",
].map((ans, i) => ({
  kaynak: "2023 DGS Matematik.pdf",
  alt: "Matematik",
  soru: i + 1,
  cevap: ans,
}));

function mulberry32(a) {
  return function () {
    let t = (a += 0x6d2b79f5);
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

function hashSeed(str) {
  let h = 1779033703 ^ str.length;
  for (let i = 0; i < str.length; i++) {
    h = Math.imul(h ^ str.charCodeAt(i), 3432918353);
    h = (h << 13) | (h >>> 19);
  }
  return h >>> 0;
}

function shuffle(arr, seed) {
  const rng = mulberry32(seed);
  const a = [...arr];
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(rng() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

const seedArg = process.argv[2];
const seed = seedArg !== undefined ? (Number(seedArg) || hashSeed(String(seedArg))) : (Date.now() >>> 0);

const tumu = [...dgs2021Sayisal, ...dgs2023Mat];
const karisik = shuffle(tumu, seed);

const baslik = `# DGS — Karışık pratik (cevap her sorunun altında)

**Üretim:** ${new Date().toISOString().slice(0, 10)}  
**Karıştırma tohumu (seed):** \`${seed}\` — Aynı sırayı tekrar üretmek için: \`node tools/dgs-shuffle.mjs ${seed}\`

## Bu dosyada neler var?

- **2021 DGS.pdf** sayısal 1–60 ve **2023 DGS Matematik.pdf** 1–50 sorularının **cevap anahtarı**, rastgele sırada.
- Her maddede **hangi PDF** ve **orijinal soru numarası** yazıyor; soru metnine PDF’ten bakarsın.

## PDF’ler hakkında (önemli)

| Dosya | Durum |
|-------|--------|
| 2021 DGS.pdf, 2023 DGS Matematik.pdf | Metin katmanı var; cevaplar çıkarıldı. |
| 2020, 2022, 2024 matematik/sayısal PDF’ler | Çıkan içerik çoğunlukla tarama/görüntü; burada otomatik soru karıştırması yapılamadı. |
| Açık 2025 vb. | Bu scripte eklemedik; metin çıkan başka PDF verirsen genişletilebilir. |

---

`;

const govde = karisik
  .map((x, i) => {
    return `### Sıra ${i + 1}

**Kaynak:** \`${x.kaynak}\` · **Bölüm:** ${x.alt} · **Orijinal soru no:** ${x.soru}

A) B) C) D) E)

**Cevap anahtarı:** **${x.cevap}**

---
`;
  })
  .join("\n");

writeFileSync(outPath, baslik + govde, "utf8");
console.log("Yazıldı:", outPath);
console.log("Toplam soru:", karisik.length, "| Seed:", seed);
