package com.example.prawkochecker

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

object PreferenceKeys {
    val NAME = stringPreferencesKey("name")
    val SURNAME = stringPreferencesKey("surname")
    val PKK_NUMBER = stringPreferencesKey("pkk_number")
    val EMAIL = stringPreferencesKey("email")
}

class StateSavingService(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "pkk_data")

    suspend fun writePkkData(pkkData: PkkData) {
        context.dataStore.edit {
            it[PreferenceKeys.NAME] = pkkData.name
            it[PreferenceKeys.SURNAME] = pkkData.surname
            it[PreferenceKeys.PKK_NUMBER] = pkkData.pkkNumber
            it[PreferenceKeys.EMAIL] = pkkData.email
        }
    }

    suspend fun readPkkData(): PkkData {
        return mapStoredPkkData().firstOrNull()
                ?: PkkData("",  "", "", "")
    }

    suspend fun clearPkkData() {
        context.dataStore.edit {
            it[PreferenceKeys.NAME] = ""
            it[PreferenceKeys.SURNAME] = ""
            it[PreferenceKeys.PKK_NUMBER] = ""
            it[PreferenceKeys.EMAIL] = ""
        }
    }

    private fun mapStoredPkkData(): Flow<PkkData> {
        return context.dataStore.data.map {
            val name = it[PreferenceKeys.NAME] ?: ""
            val surname = it[PreferenceKeys.SURNAME] ?: ""
            val pkkNumber = it[PreferenceKeys.PKK_NUMBER] ?: ""
            val email = it[PreferenceKeys.EMAIL] ?: ""
            PkkData(name, surname, pkkNumber, email)
        }
    }
}