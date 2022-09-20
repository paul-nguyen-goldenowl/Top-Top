package com.ln.toptop.data.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ln.toptop.model.User
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val APP_DATASTORE = "appstore"

class AppDataStoreManager @Inject constructor(
    val context: Application
) : AppDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

    override suspend fun setValue(key: String, value: String) {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun readValue(key: String): String? {
        return context.dataStore.data.first()[stringPreferencesKey(key)]
    }

    suspend fun getUser(): User? {
        val gson = Gson()
        val type = object : TypeToken<User>() {}.type
        val json = readValue(USER) ?: return null
        return gson.fromJson(json, type)
    }

    suspend fun setUser(user: User) {
        val gson = Gson()
        val type = object : TypeToken<User>() {}.type
        val userStr = gson.toJson(user, type)
        setValue(USER, userStr)
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }


    companion object {
        const val USER = "Datastore.user"
    }
}