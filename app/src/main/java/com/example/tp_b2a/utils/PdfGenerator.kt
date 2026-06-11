package com.example.tp_b2a.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.tp_b2a.data.Enseignant
import com.example.tp_b2a.data.Etudiant
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ─── Générateur PDF avec l'API Android native ────────────────────────────────

object PdfGenerator {

    /**
     * Génère un PDF de la feuille de présence et le sauvegarde dans
     * le dossier Downloads de l'appareil.
     * @return Le fichier créé, ou null en cas d'erreur.
     */
    fun genererPdf(
        context: Context,
        enseignant: Enseignant,
        etudiants: List<Etudiant>,
        presencesMap: Map<Int, Boolean>
    ): File? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
            val page     = document.startPage(pageInfo)
            val canvas   = page.canvas

            dessinerPage(canvas, enseignant, etudiants, presencesMap)

            document.finishPage(page)

            val dateStr  = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val nom      = "Presence_${enseignant.matiere.replace(" ", "_")}_$dateStr.pdf"

            // Sauvegarde dans Downloads
            val dossier  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fichier  = File(dossier, nom)
            document.writeTo(FileOutputStream(fichier))
            document.close()

            fichier
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun dessinerPage(
        canvas: Canvas,
        enseignant: Enseignant,
        etudiants: List<Etudiant>,
        presencesMap: Map<Int, Boolean>
    ) {
        val margin = 40f
        var y      = 60f

        // ── En-tête ──────────────────────────────────────────────
        val paintTitre = Paint().apply {
            color     = Color.rgb(26, 58, 107)
            textSize  = 22f
            isFakeBoldText = true
        }
        canvas.drawText("Feuille de Présence", margin, y, paintTitre)
        y += 30f

        val paintSousTitre = Paint().apply {
            color    = Color.rgb(46, 109, 180)
            textSize = 14f
        }
        canvas.drawText(
            "Matière : ${enseignant.matiere}  —  Prof : ${enseignant.prenom} ${enseignant.nom}",
            margin, y, paintSousTitre
        )
        y += 18f
        canvas.drawText(
            "Date : ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
            margin, y, paintSousTitre
        )
        y += 28f

        // ── Ligne de séparation ──────────────────────────────────
        val paintLine = Paint().apply {
            color     = Color.rgb(200, 200, 200)
            strokeWidth = 1f
        }
        canvas.drawLine(margin, y, 555f, y, paintLine)
        y += 16f

        // ── En-tête tableau ──────────────────────────────────────
        val paintEntete = Paint().apply {
            color          = Color.rgb(26, 58, 107)
            textSize       = 12f
            isFakeBoldText = true
        }
        canvas.drawText("#",        margin,        y, paintEntete)
        canvas.drawText("Nom",      margin + 30f,  y, paintEntete)
        canvas.drawText("Prénom",   margin + 170f, y, paintEntete)
        canvas.drawText("Statut",   margin + 310f, y, paintEntete)
        y += 6f
        canvas.drawLine(margin, y, 555f, y, paintLine)
        y += 16f

        // ── Lignes étudiants ─────────────────────────────────────
        val paintTexte = Paint().apply {
            color    = Color.BLACK
            textSize = 11f
        }
        val paintPresent = Paint().apply {
            color    = Color.rgb(46, 125, 50)
            textSize = 11f
            isFakeBoldText = true
        }
        val paintAbsent = Paint().apply {
            color    = Color.rgb(198, 40, 40)
            textSize = 11f
            isFakeBoldText = true
        }

        etudiants.forEachIndexed { index, etudiant ->
            val estPresent = presencesMap[etudiant.id] ?: false

            if (index % 2 == 0) {
                val bgPaint = Paint().apply { color = Color.rgb(244, 246, 250) }
                canvas.drawRect(margin - 4, y - 12, 559f, y + 6, bgPaint)
            }

            canvas.drawText("${index + 1}", margin, y, paintTexte)
            canvas.drawText(etudiant.nom,    margin + 30f,  y, paintTexte)
            canvas.drawText(etudiant.prenom, margin + 170f, y, paintTexte)
            canvas.drawText(
                if (estPresent) "✓ Présent" else "✗ Absent",
                margin + 310f,
                y,
                if (estPresent) paintPresent else paintAbsent
            )
            y += 22f

            if (y > 800f) return // protection si trop d'étudiants
        }

        // ── Résumé ───────────────────────────────────────────────
        y += 10f
        canvas.drawLine(margin, y, 555f, y, paintLine)
        y += 20f
        val nbPresents = presencesMap.values.count { it }
        val paintResume = Paint().apply {
            color          = Color.rgb(26, 58, 107)
            textSize       = 12f
            isFakeBoldText = true
        }
        canvas.drawText("Présents : $nbPresents / ${etudiants.size}", margin, y, paintResume)
    }
}
