package com.example.prawkochecker

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore

object PreferenceKeys {
    val NAME = stringPreferencesKey("name")
    val SURNAME = stringPreferencesKey("surname")
    val PKK_NUMBER = stringPreferencesKey("pkk_number")
    val EMAIL = stringPreferencesKey("email")
}

class StateSavingService(context: Context) {
    private val dataStore = context.createDataStore("pkk_data")

    suspend fun writePkkData(pkkData: PkkData) {
        dataStore.edit {
            it[PreferenceKeys.NAME] = pkkData.name
            it[PreferenceKeys.SURNAME] = pkkData.surname
            it[PreferenceKeys.PKK_NUMBER] = pkkData.pkkNumber
            it[PreferenceKeys.EMAIL] = pkkData.email
        }
    }

    fun readPkkData() {
        dataStore.
    }

    fun clearPkkData(pkkNumber: String) {

    }
}