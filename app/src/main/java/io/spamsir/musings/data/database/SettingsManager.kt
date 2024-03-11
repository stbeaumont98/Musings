package io.spamsir.musings.data.database

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.spamsir.musings.data.database.PreferencesKeys.END_HOUR
import io.spamsir.musings.data.database.PreferencesKeys.END_MINUTE
import io.spamsir.musings.data.database.PreferencesKeys.FIRST_LAUNCH
import io.spamsir.musings.data.database.PreferencesKeys.START_HOUR
import io.spamsir.musings.data.database.PreferencesKeys.START_MINUTE
import io.spamsir.musings.data.database.PreferencesKeys.USER_NAME
import io.spamsir.musings.data.domain.Settings
import io.spamsir.musings.data.domain.Time
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore("user")

object PreferencesKeys {
    val USER_NAME: Preferences.Key<String> = stringPreferencesKey("user_name")
    val START_HOUR: Preferences.Key<Int> = intPreferencesKey("start_hour")
    val START_MINUTE: Preferences.Key<Int> = intPreferencesKey("start_minute")
    val END_HOUR: Preferences.Key<Int> = intPreferencesKey("end_hour")
    val END_MINUTE: Preferences.Key<Int> = intPreferencesKey("end_minute")
    val FIRST_LAUNCH: Preferences.Key<Boolean> = booleanPreferencesKey("first_launch")
}

class SettingsManager @Inject constructor(private val context: Context) {

    suspend fun saveSettings(settings: Settings) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[USER_NAME] = settings.userName
            preferences[START_HOUR] = settings.startTime.hour
            preferences[START_MINUTE] = settings.startTime.minute
            preferences[END_HOUR] = settings.endTime.hour
            preferences[END_MINUTE] = settings.startTime.minute
            preferences[FIRST_LAUNCH] = settings.firstLaunch
        }
    }

    fun getSettings(): Flow<Settings> {
        return context.userPreferencesDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    Log.i("Error reading preferences: ", exception.toString())
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                Settings(
                    userName = preferences[USER_NAME] ?: "",
                    startTime = Time(preferences[START_HOUR] ?: 8, preferences[START_MINUTE] ?: 0),
                    endTime = Time(preferences[END_HOUR] ?: 22, preferences[END_MINUTE] ?: 0),
                    firstLaunch = preferences[FIRST_LAUNCH] ?: true
                )
            }
    }
}