package com.example.tp_b2a.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tp_b2a.data.DataSource
import com.example.tp_b2a.data.local.dao.EtudiantDao
import com.example.tp_b2a.data.local.dao.PresenceDao
import com.example.tp_b2a.data.local.dao.SeanceDao
import com.example.tp_b2a.data.local.entities.EtudiantEntity
import com.example.tp_b2a.data.local.entities.PresenceEntity
import com.example.tp_b2a.data.local.entities.SeanceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [EtudiantEntity::class, SeanceEntity::class, PresenceEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun etudiantDao(): EtudiantDao
    abstract fun seanceDao(): SeanceDao
    abstract fun presenceDao(): PresenceDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "presence_db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pré-remplir la table étudiants au premier lancement
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.etudiantDao()?.insertAll(
                                DataSource.etudiants.map {
                                    EtudiantEntity(id = it.id, nom = it.nom, prenom = it.prenom)
                                }
                            )
                        }
                    }
                })
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
