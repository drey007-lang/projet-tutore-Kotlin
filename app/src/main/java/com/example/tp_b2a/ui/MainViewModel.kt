package com.example.tp_b2a.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.tp_b2a.data.AppRepository
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.Etudiant
import com.example.tp_b2a.data.local.*
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

sealed class UserProfile {
    object Guest : UserProfile()
    data class Student(val entity: EtudiantEntity) : UserProfile()
    data class Teacher(val entity: EnseignantEntity) : UserProfile()
    data class Delegate(val entity: DelegueEntity) : UserProfile()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository

    val allEtudiants: StateFlow<List<EtudiantEntity>>
    var currentUserProfile by mutableStateOf<UserProfile>(UserProfile.Guest)
        private set

    init {
        val dao = AppDatabase.getDatabase(application).appDao()
        repository = AppRepository(dao)
        allEtudiants = repository.allEtudiants.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.seedDatabase()
        }
    }

    suspend fun loginTeacher(nom: String, code: String): Boolean {
        val teacher = repository.loginEnseignant(nom, code)
        return if (teacher != null) {
            currentUserProfile = UserProfile.Teacher(teacher)
            true
        } else {
            false
        }
    }

    suspend fun loginDelegate(nom: String, code: String): Boolean {
        val delegate = repository.loginDelegue(nom, code)
        return if (delegate != null) {
            currentUserProfile = UserProfile.Delegate(delegate)
            true
        } else {
            false
        }
    }

    fun logout() {
        currentUserProfile = UserProfile.Guest
    }

    fun saveAttendance(etudiants: List<Etudiant>) {
        etudiants.forEach { update ->
            DataSource.etudiants.find { it.id == update.id }?.let { original ->
                original.estPresent = update.estPresent
                original.justificatif = update.justificatif
            }
        }

        viewModelScope.launch {
            val session = SessionEntity(
                teacherId = 1,
                date = System.currentTimeMillis(),
                matiere = "General Attendance",
                isValidated = true
            )
            val records = etudiants.map { 
                AttendanceRecordEntity(sessionId = 0, studentId = it.id, isPresent = it.estPresent)
            }
            repository.saveSession(session, records)
        }
    }

    fun exportAttendanceToPdf(matiere: String, enseignantName: String) {
        val context = getApplication<Application>()
        val etudiants = DataSource.etudiants
        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val fileName = "Presence_${matiere.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)

        try {
            val writer = PdfWriter(file)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            // Header
            document.add(Paragraph("FICHE DE PRÉSENCE - PresenZ 🚀")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20f)
                .setBold())
            
            document.add(Paragraph("\n"))
            document.add(Paragraph("Matière : $matiere"))
            document.add(Paragraph("Enseignant : $enseignantName"))
            document.add(Paragraph("Date : $dateStr"))
            document.add(Paragraph("\n"))

            // Stats
            val presents = etudiants.count { it.estPresent }
            document.add(Paragraph("Résumé : $presents présents sur ${etudiants.size} étudiants."))
            document.add(Paragraph("\n"))

            // Table
            val table = Table(UnitValue.createPointArray(floatArrayOf(50f, 200f, 150f, 100f)))
            table.width = UnitValue.createPercentValue(100f)

            table.addHeaderCell("ID")
            table.addHeaderCell("Nom & Prénom")
            table.addHeaderCell("Statut")
            table.addHeaderCell("Justificatif")

            etudiants.sortedBy { it.nom }.forEach { e ->
                table.addCell(e.id.toString())
                table.addCell("${e.nom} ${e.prenom}")
                
                val statusText = if (e.estPresent) "PRÉSENT" else if (e.justificatif != null) "JUSTIFIÉ" else "ABSENT"
                val cell = Paragraph(statusText)
                if (e.estPresent) cell.setFontColor(DeviceRgb(46, 125, 50))
                else if (e.justificatif == null) cell.setFontColor(DeviceRgb(198, 40, 40))
                
                table.addCell(cell)
                table.addCell(if (e.justificatif != null) "OUI" else "-")
            }

            document.add(table)
            document.close()

            // Open PDF
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exportToCSV() {
        viewModelScope.launch {
            val etudiants = allEtudiants.value
            val csvHeader = "ID,Nom,Prenom\n"
            val csvData = etudiants.joinToString("\n") { "${it.id},${it.nom},${it.prenom}" }
            val fileContent = csvHeader + csvData
            val file = File(getApplication<Application>().cacheDir, "presence_export.csv")
            file.writeText(fileContent)
        }
    }
}
