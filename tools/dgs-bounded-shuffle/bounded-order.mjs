/**
 * 2025 → 2020 yılları, sadece matematik soruları tek havuzda.
 * Her sorunun "kanonik sıra" indeksinden uygulama sırasına kayması en fazla MAX_DELTA (varsayılan 5).
 * Örnek kısıt: kanonik 0. soru, final listede en fazla 5. slot'a; 44. soru 49. slot'tan öteye gidemez vb.
 *
 * Kullanım:
 *   node tools/dgs-bounded-shuffle/bounded-order.mjs [seed_metni_veya_sayı]
 *
 * Çıktı: proje kökü DGS_matematik_bounded_shuffle.md + .json
 */
import { readFileSync, writeFileSync, copyFileSync, existsSync } from "fs";
import { dirname, join } from "path";
import { fileURLToPath } from "url";

const __dirname = dirname(fileURLToPath(import.meta.url));
const projectRoot = join(__dirname, "..", "..");
const COUNTS_PATH = join(__dirname, "counts.json");
const MAX_DELTA = 5;
const YEARS_DESC = [2025, 2024, 2023, 2022, 2021, 2020];

const ONEDRIVE_OUT = join(
  "C:",
  "Users",
  "gskay",
  "OneDrive",
  "Masaüstü",
  "Kütahyadan Kalan Ne Kaldıysa",
  "dgs"
);

function mulberry32(a) {
  return function () {
    let t = (a += 0x6d2b79f5);
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

function hashStr(s) {
  let h = 2166136261 >>> 0;
  for (let i = 0; i < s.length; i++) {
    h ^= s.charCodeAt(i);
    h = Math.imul(h, 16777619);
  }
  return h >>> 0;
}

function buildCanonicalList(counts) {
  const items = [];
  let idx = 0;
  for (const y of YEARS_DESC) {
    const k = counts[String(y)] ?? counts[y];
    if (!k || k < 1) throw new Error(`counts.json içinde ${y} için geçerli soru sayısı yok`);
    for (let q = 1; q <= k; q++) {
      items.push({ canon: idx, year: y, question: q });
      idx++;
    }
  }
  return items;
}

/** f[i] = item i'nin gideceği slot (0..n-1). Başta f[i]=i. Swap ile rastgele karıştır. */
function mcmcBoundedAssignment(n, maxDelta, rng, iterations) {
  const f = new Int32Array(n);
  for (let i = 0; i < n; i++) f[i] = i;

  function valid(i, slot) {
    return Math.abs(slot - i) <= maxDelta;
  }

  let ok = 0;
  for (let t = 0; t < iterations; t++) {
    const i = (rng() * n) | 0;
    const j = (rng() * n) | 0;
    if (i === j) continue;
    const pi = f[i];
    const pj = f[j];
    if (valid(i, pj) && valid(j, pi)) {
      f[i] = pj;
      f[j] = pi;
      ok++;
    }
  }
  return f;
}

function verify(f, n, maxDelta) {
  const used = new Uint8Array(n);
  for (let i = 0; i < n; i++) {
    const s = f[i];
    if (s < 0 || s >= n) return `Geçersiz slot: item ${i} -> ${s}`;
    if (Math.abs(s - i) > maxDelta) return `Kayma aşıldı: kanonik ${i} -> slot ${s} (|Δ|=${Math.abs(s - i)})`;
    if (used[s]) return `Çakışma: slot ${s} iki kez dolu`;
    used[s] = 1;
  }
  for (let s = 0; s < n; s++) if (!used[s]) return `Boş slot: ${s}`;
  return null;
}

/** slot -> kanonik item index */
function invert(f, n) {
  const slotToItem = new Int32Array(n);
  for (let i = 0; i < n; i++) slotToItem[f[i]] = i;
  return slotToItem;
}

function main() {
  const raw = JSON.parse(readFileSync(COUNTS_PATH, "utf8"));
  const counts = { ...raw };
  delete counts._yorum;

  const items = buildCanonicalList(counts);
  const n = items.length;

  const seedArg = process.argv[2];
  const seed =
    seedArg !== undefined
      ? /^\d+$/.test(seedArg)
        ? Number(seedArg)
        : hashStr(String(seedArg))
      : (Date.now() ^ hashStr("dgs-math")) >>> 0;

  const rng = mulberry32(seed >>> 0);
  const iterations = Math.min(5_000_000, Math.max(500_000, n * 8000));
  const f = mcmcBoundedAssignment(n, MAX_DELTA, rng, iterations);
  const err = verify(f, n, MAX_DELTA);
  if (err) {
    console.error(err);
    process.exit(1);
  }

  const slotToItem = invert(f, n);
  // Int32Array.map nesneleri sayıya çevirir; mutlaka düz diziye çevir.
  const practice = Array.from({ length: n }, (_, slot) => {
    const canonIdx = slotToItem[slot];
    const it = items[canonIdx];
    const delta = slot - canonIdx;
    return {
      sira: slot + 1,
      yil: it.year,
      soru: it.question,
      kanonikSira: canonIdx + 1,
      kayma: delta,
    };
  });

  const md = [];
  md.push(`# DGS matematik — karışık sıra (±${MAX_DELTA} kayma)`);
  md.push("");
  md.push(`**Üretim:** ${new Date().toISOString().slice(0, 10)}`);
  md.push(`**Tohum (seed):** \`${seed}\` — Tekrar: \`node tools/dgs-bounded-shuffle/bounded-order.mjs ${seed}\``);
  md.push(`**Toplam soru:** ${n}`);
  md.push("");
  md.push(`## Kural`);
  md.push("");
  md.push(`- Kanonik sıra: **2025** matematik soru 1…n, sonra **2024** 1…n, … **2020** (counts.json).`);
  md.push(`- Uygulama sırasında her soru, kendi kanonik sıra numarasından **en fazla ${MAX_DELTA}** pozisyon yukarı veya aşağı kayabilir.`);
  md.push(`- Böylece 1. sıradaki soru 45. yere falan **sıçyamaz**.`);
  md.push("");
  md.push(`## PDF notu`);
  md.push("");
  md.push(`Bu dosya **çalışma sırasını** verir; PDF içinde kutuları otomatik kesmedim. Her satırda yıl + soru numarasına göre kendi PDF’inden açarsın.`);
  md.push("");
  md.push(`---`);
  md.push("");

  for (const row of practice) {
    md.push(`### Sıra ${row.sira}`);
    md.push("");
    md.push(`**${row.yil} matematik · Soru ${row.soru}**`);
    md.push("");
    md.push(`- Kanonik sıra (havuzdaki yer): ${row.kanonikSira}`);
    md.push(`- Kayma (uygulama − kanonik): **${row.kayma >= 0 ? "+" : ""}${row.kayma}**`);
    md.push("");
    md.push(`---`);
    md.push("");
  }

  const jsonOut = {
    seed,
    maxDelta: MAX_DELTA,
    yearsOrder: YEARS_DESC,
    counts,
    total: n,
    practice,
  };

  const mdPath = join(projectRoot, "DGS_matematik_bounded_shuffle.md");
  const jsPath = join(projectRoot, "DGS_matematik_bounded_shuffle.json");
  writeFileSync(mdPath, md.join("\n"), "utf8");
  writeFileSync(jsPath, JSON.stringify(jsonOut, null, 2), "utf8");
  const maxKay = Math.max(...practice.map((r) => Math.abs(r.kayma)));
  console.log("Yazıldı:", mdPath);
  console.log("Yazıldı:", jsPath);
  console.log("Seed:", seed, "iterasyon:", iterations, "max|kayma|:", maxKay);

  if (existsSync(ONEDRIVE_OUT)) {
    try {
      copyFileSync(mdPath, join(ONEDRIVE_OUT, "DGS_matematik_bounded_shuffle.md"));
      copyFileSync(jsPath, join(ONEDRIVE_OUT, "DGS_matematik_bounded_shuffle.json"));
      console.log("OneDrive kopya:", ONEDRIVE_OUT);
    } catch (e) {
      console.warn("OneDrive kopyası:", e.message);
    }
  }
}

main();
