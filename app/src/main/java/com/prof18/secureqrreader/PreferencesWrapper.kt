package com.prof18.secureqrreader

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "qr_reader_secure_settings")

class PreferencesWrapper(
    private val context: Context,
) {

    suspend fun isOnboardingDone(): Boolean = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_DONE_KEY] ?: false
        }.first()

    suspend fun setOnboardingDone() {
        context.dataStore.edit { settings ->
            settings[ONBOARDING_DONE_KEY] = true
        }
    }

    private companion object {
        val ONBOARDING_DONE_KEY = booleanPreferencesKey("onboarding_done_key")
    }
}

