package com.example.encuestassaaki

import android.app.Application
import android.content.Context
import com.example.encuestassaaki.locale.LocaleHelper

class App : Application() {

    override fun attachBaseContext(base: Context) {
        val sharedPref = base.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val lang = sharedPref.getString("lang", "es") ?: "es"
        super.attachBaseContext(LocaleHelper.setLocale(base, lang))
    }
}
