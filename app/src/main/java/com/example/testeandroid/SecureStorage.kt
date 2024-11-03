// SecureStorage.kt
package com.example.testeandroid

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecureStorage {
    private const val FILE_NAME = "secure_prefs"
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private fun getSharedPreferences(context: Context) =
        EncryptedSharedPreferences.create(
            FILE_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    fun saveToken(context: Context, token: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString("auth_token", null)
    }

    fun clearToken(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().remove("auth_token").apply()
    }
}
