package com.example.encuestassaaki.utils

import android.content.Context
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, langCode: String): Context {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        val newContext = context.createConfigurationContext(config)

        // Guardar preferencia
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit().putString("My_Lang", langCode).apply()

        return newContext
    }

    fun loadLocale(context: Context): String {
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return prefs.getString("My_Lang", "es") ?: "es"
    }
}

