package com.example.encuestassaaki

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.example.encuestassaaki.ui.login.LoginFragment
import com.example.encuestassaaki.ui.selection.SurveySelectionFragment
import com.example.encuestassaaki.ui.survey.SurveyAFragment
import com.example.encuestassaaki.ui.survey.SurveyBFragment
import com.example.encuestassaaki.ui.userinfo.UserInfoFragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar TTS
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("es", "ES"))
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                    pendingText?.let { speak(it) } // Reproducir lo que estaba pendiente
                    pendingText = null
                }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    // Metodo público para que los fragments puedan hablar
    fun speak(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            pendingText = text // 🔹 Guardar para decirlo cuando TTS esté listo
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

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
        if (type == "A") {
            val fragment = SurveyAFragment.newInstance(codeUser, yearUser, sexUser)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        if (type == "B") {
            val fragment = SurveyBFragment.newInstance(codeUser, yearUser, sexUser)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
