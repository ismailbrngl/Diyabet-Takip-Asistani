package com.example.insulinneedlereminder.ui.report

import android.content.Context
import android.os.Environment
import com.example.insulinneedlereminder.data.entity.GlucoseRecord
import com.example.insulinneedlereminder.data.entity.InsulinRecord
import com.example.insulinneedlereminder.util.DateUtils
import com.example.insulinneedlereminder.util.GlucoseStatus
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File

object PdfReportGenerator {

    fun generate(
        context: Context,
        glucoseRecords: List<GlucoseRecord>,
        insulinRecords: List<InsulinRecord>,
        period: String
    ): File {
        val fileName = "Insu_Rapor_${period}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        val writer = PdfWriter(file)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc)

        // Türkçe karakterlerin düzgün görünmesi için dinamik font yükleme
        val (font, boldFont) = createPdfFonts(context)
        document.setFont(font)

        // BAŞLIK
        document.add(
            Paragraph("İnsülin ve Kan Şekeri Raporu")
                .setFont(boldFont)
                .setFontSize(20f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        document.add(
            Paragraph("Periyot: $period  •  Oluşturma Tarihi: ${DateUtils.formatDate(System.currentTimeMillis())}")
                .setFontSize(11f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20f)
        )

        // KAN ŞEKERİ ÖZETİ VE TABLOSU
        if (glucoseRecords.isNotEmpty()) {
            document.add(Paragraph("Kan Şekeri Özeti").setFont(boldFont).setFontSize(14f))

            val values = glucoseRecords.map { it.value }
            val low = glucoseRecords.count { GlucoseStatus.from(it.value) == GlucoseStatus.LOW }
            val normal = glucoseRecords.count { GlucoseStatus.from(it.value) == GlucoseStatus.NORMAL }
            val high = glucoseRecords.count { GlucoseStatus.from(it.value) == GlucoseStatus.HIGH }
            val tirPercent = (normal * 100) / glucoseRecords.size

            val statsTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f, 1f, 1f)))
                .useAllAvailableWidth().setMarginBottom(10f)

            listOf("Ort", "Min", "Maks", "Kayıt").forEach {
                statsTable.addCell(Cell().add(Paragraph(it).setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            }
            statsTable.addCell("${values.average().toInt()} mg/dL")
            statsTable.addCell("${values.minOrNull() ?: "-"} mg/dL")
            statsTable.addCell("${values.maxOrNull() ?: "-"} mg/dL")
            statsTable.addCell("${glucoseRecords.size}")
            document.add(statsTable)

            val tirTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1f, 1f, 1f)))
                .useAllAvailableWidth().setMarginBottom(10f)
            listOf("Hedef Aralık (70-180)", "Düşük", "Normal", "Yüksek").forEach {
                tirTable.addCell(Cell().add(Paragraph(it).setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            }
            tirTable.addCell("%$tirPercent")
            tirTable.addCell(low.toString())
            tirTable.addCell(normal.toString())
            tirTable.addCell(high.toString())
            document.add(tirTable)

            // Detay Tablosu
            val glucoseTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1f, 1f, 2f))).useAllAvailableWidth()
            listOf("Tarih & Saat", "Değer", "Durum", "Not").forEach {
                glucoseTable.addHeaderCell(Cell().add(Paragraph(it).setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            }

            glucoseRecords.forEach { record ->
                glucoseTable.addCell(DateUtils.formatDateTime(record.date))
                glucoseTable.addCell("${record.value} mg/dL")
                val statusText = if (record.value < 70) "Düşük" else if (record.value > 180) "Yüksek" else "Normal"
                glucoseTable.addCell(statusText)
                glucoseTable.addCell(record.note.ifEmpty { "-" })
            }
            document.add(glucoseTable.setMarginBottom(20f))
        }

        // İNSÜLİN KAYITLARI TABLOSU
        if (insulinRecords.isNotEmpty()) {
            document.add(Paragraph("İnsülin Uygulama Kayıtları").setFont(boldFont).setFontSize(14f))
            val insulinTotal = insulinRecords.sumOf { it.units }
            val morningTotal = insulinRecords.filter { it.timeLabel.contains("Sabah", ignoreCase = true) }.sumOf { it.units }
            val noonTotal = insulinRecords.filter { it.timeLabel.contains("Öğle", ignoreCase = true) || it.timeLabel.contains("Ogle", ignoreCase = true) }.sumOf { it.units }
            val eveningTotal = insulinRecords.filter { it.timeLabel.contains("Akşam", ignoreCase = true) || it.timeLabel.contains("Aksam", ignoreCase = true) }.sumOf { it.units }

            val insulinSummary = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f, 1f, 1f)))
                .useAllAvailableWidth().setMarginBottom(10f)
            listOf("Toplam", "Sabah", "Öğle", "Akşam").forEach {
                insulinSummary.addCell(Cell().add(Paragraph(it).setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            }
            insulinSummary.addCell("$insulinTotal u")
            insulinSummary.addCell("$morningTotal u")
            insulinSummary.addCell("$noonTotal u")
            insulinSummary.addCell("$eveningTotal u")
            document.add(insulinSummary)

            val insulinTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1f, 1f, 2f))).useAllAvailableWidth()

            listOf("Tarih & Saat", "Öğün", "Ünite", "Not").forEach {
                insulinTable.addHeaderCell(Cell().add(Paragraph(it).setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            }

            insulinRecords.forEach { record ->
                insulinTable.addCell(DateUtils.formatDateTime(record.date))
                insulinTable.addCell(record.timeLabel)
                insulinTable.addCell("${record.units} u")
                insulinTable.addCell(record.note.ifEmpty { "-" })
            }
            document.add(insulinTable)
        }

        document.add(
            Paragraph("\nBu rapor mobil uygulama tarafından otomatik üretilmiştir.")
                .setFontSize(9f)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY)
        )

        document.close()
        return file
    }

    /**
     * Assets içerisindeki font dosyalarını yükler.
     * Eğer asset bulunamazsa standart HELVETICA fontlarına fallback yapar.
     */
    private fun createPdfFonts(context: Context): Pair<PdfFont, PdfFont> {
        return try {
            val regularBytes = context.assets.open("fonts/Roboto-Regular.ttf").readBytes()
            val boldBytes = context.assets.open("fonts/Roboto-Bold.ttf").readBytes()

            val regularFont = PdfFontFactory.createFont(
                regularBytes,
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            )
            val boldFont = PdfFontFactory.createFont(
                boldBytes,
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            )

            Pair(regularFont, boldFont)
        } catch (e: Exception) {
            val regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA, "CP1254")
            val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD, "CP1254")
            Pair(regularFont, boldFont)
        }
    }
}