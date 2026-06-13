package com.example.tp_b2a.data

import com.example.tp_b2a.data.local.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val allEtudiants: Flow<List<EtudiantEntity>> = appDao.getAllEtudiants()
    val allEnseignants: Flow<List<EnseignantEntity>> = appDao.getAllEnseignants()

    suspend fun loginEnseignant(nom: String, code: String): EnseignantEntity? {
        return appDao.getEnseignantByNameAndCode(nom, code)
    }

    suspend fun loginDelegue(nom: String, code: String): DelegueEntity? {
        return appDao.getDelegueByNameAndCode(nom, code)
    }

    suspend fun saveSession(session: SessionEntity, attendance: List<AttendanceRecordEntity>) {
        val sessionId = appDao.insertSession(session).toInt()
        val recordsWithId = attendance.map { it.copy(sessionId = sessionId) }
        appDao.insertAttendanceRecords(recordsWithId)
    }

    suspend fun seedDatabase() {
        if (appDao.getEtudiantCount() > 0) return

        val etudiants = DataSource.etudiants.map { EtudiantEntity(nom = it.nom, prenom = it.prenom) }
        val enseignants = DataSource.enseignants.map { EnseignantEntity(nom = it.nom, prenom = it.prenom, matiere = it.matiere, code = it.code) }
        val delegues = DataSource.delegues.map { DelegueEntity(nom = it.nom, prenom = it.prenom, code = it.code) }

        appDao.insertEtudiants(etudiants)
        appDao.insertEnseignants(enseignants)
        appDao.insertDelegues(delegues)
    }
}
