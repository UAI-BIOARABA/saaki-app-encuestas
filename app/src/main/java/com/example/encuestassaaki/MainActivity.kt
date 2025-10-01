package com.example.encuestassaaki

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.example.encuestassaaki.ui.login.LoginFragment
import com.example.encuestassaaki.ui.selection.SurveySelectionFragment
import com.example.encuestassaaki.ui.survey.SurveyAFragment
import com.example.encuestassaaki.ui.survey.SurveyBFragment
import com.example.encuestassaaki.ui.userinfo.UserInfoFragment
import com.example.encuestassaaki.utils.LocaleHelper
import java.util.Locale

class MainActivity : AppCompatActivity(),
    LoginFragment.LoginListener,
    SurveySelectionFragment.SurveySelectionListener,
    UserInfoFragment.UserInfoListener {

    var codeUser: String = ""
    var yearUser: String = ""
    var sexUser: String = ""

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var pendingText: String? = null

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.loadLocale(newBase)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar TTS con idioma actual de la app
        initTTS(LocaleHelper.loadLocale(this))

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    private fun initTTS(langCode: String) {
        val locale = when(langCode) {
            "eu" -> Locale("eu") // Euskera
            else -> Locale("es", "ES") // Español por defecto
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Verificar si el idioma está disponible
                val available = tts?.isLanguageAvailable(locale)
                val langToUse = if (available == TextToSpeech.LANG_AVAILABLE || available == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    locale
                } else {
                    Locale("es", "ES") // Fallback a español
                }

                val result = tts?.setLanguage(langToUse)
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED

                pendingText?.let { speak(it) } // Decir lo pendiente
                pendingText = null
            }
        }
    }

    // Método público para que los fragments puedan hablar
    fun speak(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            pendingText = text
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    // ====================== Fragments Listeners ======================
    override fun onUserInfoSaved(code: String, year: String, sex: String) {
        codeUser = code
        yearUser = year
        sexUser = sex
    }

    override fun onLogin(code: String) {
        val fragment = UserInfoFragment.newInstance(code)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onSurveySelected(type: String) {
        val fragment = when(type) {
            "A" -> SurveyAFragment.newInstance(codeUser, yearUser, sexUser)
            "B" -> SurveyBFragment.newInstance(codeUser, yearUser, sexUser)
            else -> null
        }
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .addToBackStack(null)
                .commit()
        }
    }
}
