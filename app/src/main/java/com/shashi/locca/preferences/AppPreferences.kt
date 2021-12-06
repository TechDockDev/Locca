package com.shashi.locca.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


//top level
private const val PREFERENCE_NAME = "LoccaPref"
val Context.userDatastore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

/**
 * Created by Shashi on 05/10/21
 * @description : Contains datastore preferences (New version of shared preferences)
 */

object UserPreferences {

    //Constants
    private val KEY_LOCATION = stringPreferencesKey("KEY_LOCATION")

    /**set Lng  of the user in datastore*/
    suspend fun setLocation(context: Context, loc:String) {
        context.userDatastore.apply {
            edit { it[KEY_LOCATION] = loc }
        }
    }

    /**Get Lat*/
    fun getLocation(context: Context): Flow<String?> =
        context.userDatastore.data.map {
            if ((it[KEY_LOCATION]) != null) it[KEY_LOCATION]!! else null
        }


}