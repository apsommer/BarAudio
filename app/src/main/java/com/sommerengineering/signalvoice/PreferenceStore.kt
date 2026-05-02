package com.sommerengineering.signalvoice

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("dataStore")
val ONBOARDING = booleanPreferencesKey("onboarding")
val EMPTY_STATE = booleanPreferencesKey("emptyState")
val VOICE_NAME = stringPreferencesKey("voice")
val SPEED = floatPreferencesKey("speed")
val PITCH = floatPreferencesKey("pitch")
val LISTENING = booleanPreferencesKey("isListening")
val FULLSCREEN = booleanPreferencesKey("isFullScreen")
val FEED_MODE = stringPreferencesKey("feedMode")
val ZN = booleanPreferencesKey("isZN")
val NQ = booleanPreferencesKey("isNQ")
val ES = booleanPreferencesKey("isES")
val BTC = booleanPreferencesKey("isBTC")
val GC = booleanPreferencesKey("isGC")
val SI = booleanPreferencesKey("isSI")
val UID = stringPreferencesKey("uid")
val PREMIUM = booleanPreferencesKey("isPremium")

@Singleton
class PreferenceStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun <T> read(key: Preferences.Key<T>): T? = dataStore.data.first()[key]
    suspend fun <T> write(key: Preferences.Key<T>, value: T) = dataStore.edit { it[key] = value }
}