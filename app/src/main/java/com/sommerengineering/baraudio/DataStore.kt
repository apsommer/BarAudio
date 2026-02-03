package com.sommerengineering.baraudio

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(localCache)

fun readFromDataStore(
    context: Context,
    key: String) = runBlocking {
        context.dataStore.data
            .map { it[stringPreferencesKey(key)] }
            .first()
    }


fun writeToDataStore(
    context: Context,
    key: String,
    value: String) {

    CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }
}