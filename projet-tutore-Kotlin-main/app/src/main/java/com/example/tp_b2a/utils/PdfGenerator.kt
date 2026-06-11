package com.example.tp_b2a.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.tp_b2a.data.model.Etudiant
import com.example.tp_b2a.data.model.Seance
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Générateur de PDF (API Android native) ───────────────────────────────

object PdfGenerator {

    private const val PAGE_WIDTH  = 595  // A4 en points (72 dpi)
    private const val PAGE_HEIGHT = 842
    private const val MARGIN      = 40f

    /**
     * Génère un PDF pour une séance donnée et retourne l'URI partageable.
     * Retourne null en cas d'erreur.
     */
    fun genererFeuille(
        context: Context,
        seance: Seance,
        etudiants: List<Etudiant>
    ): Uri? = runCatching {
        val document = PdfDocument()
        val pageInfo  = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page      = document.startPage(pageInfo)
        val canvas    = page.canvas

        drawDocument(canvas, seance, etudiants)
        document.finishPage(page)

        // Si plus d'une page (beaucoup d'étudiants), ajouter des pages supplémentaires
        val totalEtudiants = etudiants.size
        if (totalEtudiants > 20) {
            val remaining = etudiants.drop(20)
            val pageInfo2 = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 2).create()
            val page2     = document.startPage(pageInfo2)
            drawStudentList(page2.canvas, remaining, seance, startIndex = 20)
            document.finishPage(page2)
        }

        // Sauvegarde dans le cache
        val fileName = "presence_${seance.matiereNom.replace(" ", "_")}_${
            SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date(seance.date))
        }.pdf"
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        // URI partageable via FileProvider
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }.getOrNull()

    /**
     * Ouvre le sélecteur de partage Android avec le PDF généré.
     */
    fun partagerPdf(context: Context, seance: Seance, etudiants: List<Etudiant>) {
        val uri = genererFeuille(context, seance, etudiants) ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type     = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Feuille de présence — ${seance.matiereNom}")
            putExtra(Intent.EXTRA_TEXT, "Veuillez trouver ci-joint la feuille de présence du ${
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date(seance.date))
            }.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Partager la feuille de présence"))
    }

    // ─── Dessin du document ────────────────────────────────────────────────

    private fun drawDocument(canvas: Canvas, seance: Seance, etudiants: List<Etudiant>) {
        val dateStr = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE).format(Date(seance.date))
        var y = MARGIN

        // ── En-tête bleu ─────────────────────────────────────────────────
        val headerPaint = Paint().apply {
            color = Color.rgb(26, 58, 107)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 80f, headerPaint)

        val titlePaint = Paint().apply {
            color     = Color.WHITE
            textSize  = 22f
            typeface  = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("📋 Feuille de Présence", MARGIN, 35f, titlePaint)

        val subtitlePaint = Paint().apply {
            color    = Color.rgb(200, 220, 255)
            textSize = 12f
            isAntiAlias = true
        }
        canvas.drawText("Gestion des Présences — B2A", MARGIN, 55f, subtitlePaint)
        canvas.drawText("Généré le $dateStr", MARGIN, 70f, subtitlePaint)

        y = 100f

        // ── Infos séance ─────────────────────────────────────────────────
        drawInfoBox(canvas, y, seance, dateStr)
        y += 80f

        // ── Stats ─────────────────────────────────────────────────────────
        drawStats(canvas, y, seance)
        y += 60f

        // ── Liste étudiants ───────────────────────────────────────────────
        val firstBatch = etudiants.take(20)
        drawStudentList(canvas, firstBatch, seance, y, startIndex = 0)
    }

    private fun drawInfoBox(canvas: Canvas, y: Float, seance: Seance, dateStr: String) {
        val boxPaint = Paint().apply {
            color = Color.rgb(244, 246, 250)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 65f, 8f, 8f, boxPaint)

        val labelPaint = Paint().apply {
            color    = Color.rgb(100, 100, 100)
            textSize = 10f
            isAntiAlias = true
        }
        val valuePaint = Paint().apply {
            color    = Color.rgb(26, 58, 107)
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        canvas.drawText("MATIÈRE",      MARGIN + 10f, y + 18f, labelPaint)
        canvas.drawText(seance.matiereNom, MARGIN + 10f, y + 34f, valuePaint)

        canvas.drawText("ENSEIGNANT",   MARGIN + 200f, y + 18f, labelPaint)
        canvas.drawText(seance.enseignantNom, MARGIN + 200f, y + 34f, valuePaint)

        canvas.drawText("DATE & HEURE", MARGIN + 380f, y + 18f, labelPaint)
        canvas.drawText(dateStr,        MARGIN + 380f, y + 34f, Paint().apply {
            color    = Color.rgb(26, 58, 107)
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        })
    }

    private fun drawStats(canvas: Canvas, y: Float, seance: Seance) {
        fun drawStatBox(x: Float, label: String, value: String, color: Int) {
            val boxPaint = Paint().apply { this.color = color; style = Paint.Style.FILL }
            canvas.drawRoundRect(x, y, x + 100f, y + 45f, 6f, 6f, boxPaint)
            val vPaint = Paint().apply {
                this.color = Color.WHITE; textSize = 20f
                typeface   = Typeface.DEFAULT_BOLD; isAntiAlias = true
            }
            val lPaint = Paint().apply {
                this.color = Color.rgb(220, 240, 220); textSize = 10f; isAntiAlias = true
            }
            canvas.drawText(value, x + 10f, y + 25f, vPaint)
            canvas.drawText(label, x + 10f, y + 40f, lPaint)
        }

        drawStatBox(MARGIN,        "Présents",  "${seance.nbPresents}", Color.rgb(46, 125, 50))
        drawStatBox(MARGIN + 115f, "Absents",   "${seance.nbAbsents}",  Color.rgb(198, 40, 40))
        drawStatBox(MARGIN + 230f, "Total",     "${seance.total}",      Color.rgb(46, 109, 180))
        drawStatBox(MARGIN + 345f, "Taux",      "${seance.taux} %",     Color.rgb(230, 81, 0))
    }

    private fun drawStudentList(
        canvas: Canvas,
        etudiants: List<Etudiant>,
        seance: Seance,
        startY: Float = MARGIN,
        startIndex: Int = 0
    ) {
        var y = startY

        // En-tête tableau
        val headerPaint = Paint().apply {
            color = Color.rgb(26, 58, 107); style = Paint.Style.FILL
        }
        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 22f, headerPaint)

        val headerTextPaint = Paint().apply {
            color = Color.WHITE; textSize = 11f
            typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true
        }
        canvas.drawText("#",       MARGIN + 5f,   y + 15f, headerTextPaint)
        canvas.drawText("NOM & PRÉNOM",      MARGIN + 25f,  y + 15f, headerTextPaint)
        canvas.drawText("STATUT",  MARGIN + 340f, y + 15f, headerTextPaint)
        y += 22f

        val rowPaint    = Paint().apply { style = Paint.Style.FILL }
        val namePaint   = Paint().apply { textSize = 11f; isAntiAlias = true }
        val statusPaint = Paint().apply {
            textSize = 10f; typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true
        }

        etudiants.forEachIndexed { index, etudiant ->
            val present = seance.estPresent(etudiant.id)
            val rowH    = 22f

            // Fond alterné
            rowPaint.color = if (index % 2 == 0) Color.rgb(248, 250, 252) else Color.WHITE
            canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowH, rowPaint)

            // Couleur de fond statut
            val statusColor = if (present) Color.rgb(46, 125, 50) else Color.rgb(198, 40, 40)
            val statusBg    = Paint().apply { color = statusColor; style = Paint.Style.FILL }
            canvas.drawRoundRect(
                PAGE_WIDTH - MARGIN - 80f, y + 3f,
                PAGE_WIDTH - MARGIN - 5f,  y + rowH - 3f,
                10f, 10f, statusBg
            )

            namePaint.color = Color.rgb(30, 30, 30)
            canvas.drawText("${startIndex + index + 1}", MARGIN + 5f, y + 15f, namePaint)
            canvas.drawText(etudiant.nomComplet,          MARGIN + 25f, y + 15f, namePaint)

            statusPaint.color = Color.WHITE
            canvas.drawText(
                if (present) "✓ Présent" else "✗ Absent",
                PAGE_WIDTH - MARGIN - 75f, y + 14f, statusPaint
            )
            y += rowH
        }

        // Ligne de séparation finale
        val linePaint = Paint().apply { color = Color.rgb(200, 200, 200); strokeWidth = 1f }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
    }
}
