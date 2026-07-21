/**
 * Yalnızca sayısal içerik: matematik PDF'leri + "SAYISAL" adlı dosya + 2021'in sayısal kısmı (ilk 18 sayfa).
 * Tam kitap (2025 Açık vb.) sayfa sırası belirsiz olduğu için bu birleştirmeye alınmaz — Türkçe karışmasın diye.
 *
 *   node tools/dgs-merge-pdf/merge-shuffled.mjs [seed]
 *
 * Çıktı: ./DGS_karisik_sayisal.pdf
 */
import { readFileSync, writeFileSync, copyFileSync, existsSync } from "fs";
import { dirname, join } from "path";
import { fileURLToPath } from "url";
import { PDFDocument, StandardFonts, rgb } from "./vendor/package/dist/pdf-lib.esm.min.js";

const __dirname = dirname(fileURLToPath(import.meta.url));
const projectRoot = join(__dirname, "..", "..");
const outName = "DGS_karisik_sayisal.pdf";
const outPdf = join(projectRoot, outName);

const ONEDRIVE_COPY = join(
  "C:",
  "Users",
  "gskay",
  "OneDrive",
  "Masaüstü",
  "Kütahyadan Kalan Ne Kaldıysa",
  "dgs",
  outName
);

/** 2021 tam kitapta 19. sayfada sözel başlıyor → son sayısal sayfa 0-index = 17. */
const SAYISAL_2021_LAST_PAGE = 17;

/** Kaynak PDF'ler — kısa etiket (footer ASCII). firstPage / lastPage: 0-index, ikisi de dahil. */
const SOURCES = [
  {
    id: "2020-mat",
    path: join(
      "C:",
      "Users",
      "gskay",
      "OneDrive",
      "Masaüstü",
      "Kütahyadan Kalan Ne Kaldıysa",
      "dgs",
      "2020 DGS Matematik.pdf"
    ),
  },
  {
    id: "2021-say",
    path: join(
      "C:",
      "Users",
      "gskay",
      "OneDrive",
      "Masaüstü",
      "Kütahyadan Kalan Ne Kaldıysa",
      "dgs",
      "2021 DGS.pdf"
    ),
    firstPage: 0,
    lastPage: SAYISAL_2021_LAST_PAGE,
  },
  {
    id: "2022-mat",
    path: join(
      "C:",
      "Users",
      "gskay",
      "OneDrive",
      "Masaüstü",
      "Kütahyadan Kalan Ne Kaldıysa",
      "dgs",
      "2022 DGS Matematik.pdf"
    ),
  },
  {
    id: "2023-mat",
    path: join(
      "C:",
      "Users",
      "gskay",
      "OneDrive",
      "Masaüstü",
      "Kütahyadan Kalan Ne Kaldıysa",
      "dgs",
      "2023 DGS Matematik.pdf"
    ),
  },
  {
    id: "2024-say",
    path: join(
      "C:",
      "Users",
      "gskay",
      "OneDrive",
      "Masaüstü",
      "Kütahyadan Kalan Ne Kaldıysa",
      "dgs",
      "2024 DGS SAYISAL.pdf"
    ),
  },
];

function mulberry32(a) {
  return function () {
    let t = (a += 0x6d2b79f5);
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

function shuffle(arr, seed) {
  const rng = mulberry32(seed >>> 0);
  const a = [...arr];
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(rng() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

function hashStr(s) {
  let h = 2166136261 >>> 0;
  for (let i = 0; i < s.length; i++) {
    h ^= s.charCodeAt(i);
    h = Math.imul(h, 16777619);
  }
  return h >>> 0;
}

const allSources = SOURCES;

const seedArg = process.argv[2];
const seed =
  seedArg !== undefined
    ? /^\d+$/.test(seedArg)
      ? Number(seedArg)
      : hashStr(seedArg)
    : (Date.now() ^ hashStr("dgs")) >>> 0;

async function main() {
  const pool = [];
  const docs = [];

  for (const src of allSources) {
    if (!existsSync(src.path)) {
      console.warn("Atlandı (dosya yok):", src.path);
      continue;
    }
    const bytes = readFileSync(src.path);
    const doc = await PDFDocument.load(bytes, { ignoreEncryption: true });
    const n = doc.getPageCount();
    const first = src.firstPage != null ? Math.max(0, src.firstPage) : 0;
    const last =
      src.lastPage != null ? Math.min(n - 1, src.lastPage) : n - 1;
    docs.push(doc);
    const docIdx = docs.length - 1;
    for (let i = first; i <= last; i++) {
      pool.push({ docIdx, pageIndex: i, tag: `${src.id} p${i + 1}` });
    }
    console.log(src.id, "PDF sayfa:", n, "→ havuza:", last - first + 1, "(", first + 1, "-", last + 1, ")");
  }

  if (pool.length === 0) {
    console.error("Hiç PDF yüklenemedi. Yolları kontrol et.");
    process.exit(1);
  }

  const order = shuffle(pool, seed);
  const out = await PDFDocument.create();
  const font = await out.embedFont(StandardFonts.Helvetica);

  for (const item of order) {
    const srcDoc = docs[item.docIdx];
    const [copied] = await out.copyPages(srcDoc, [item.pageIndex]);
    const page = out.addPage(copied);
    const { width } = page.getSize();
    const footer = item.tag;
    page.drawText(footer, {
      x: Math.min(36, width - 200),
      y: 14,
      size: 7,
      font,
      color: rgb(0.35, 0.35, 0.35),
    });
  }

  out.setTitle(`DGS karisik sayisal seed=${seed}`);
  const pdfBytes = await out.save();
  writeFileSync(outPdf, pdfBytes);
  console.log("Yazıldı:", outPdf, "toplam sayfa:", order.length, "seed:", seed);

  if (existsSync(dirname(ONEDRIVE_COPY))) {
    try {
      copyFileSync(outPdf, ONEDRIVE_COPY);
      console.log("Kopya:", ONEDRIVE_COPY);
    } catch (e) {
      console.warn("OneDrive kopyası yazılamadı:", e.message);
    }
  }
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
