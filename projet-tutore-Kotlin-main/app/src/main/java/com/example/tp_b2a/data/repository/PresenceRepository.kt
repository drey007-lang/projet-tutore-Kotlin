package com.example.tp_b2a.data.repository

import android.content.Context
import com.example.tp_b2a.data.model.Seance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// ─── Persistance des séances via SharedPreferences + Gson ─────────────────

class PresenceRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson  = Gson()

    // ── Séances ────────────────────────────────────────────────────────────

    fun sauvegarderSeance(seance: Seance) {
        val seances = getSeances().toMutableList()
        seances.add(seance)
        prefs.edit()
            .putString(KEY_SEANCES, gson.toJson(seances))
            .apply()
    }

    fun getSeances(): List<Seance> {
        val json = prefs.getString(KEY_SEANCES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Seance>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun supprimerTout() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME  = "presence_db"
        private const val KEY_SEANCES = "seances"
    }
}
