/*
 * Copyright 2022 Marco Gomiero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

