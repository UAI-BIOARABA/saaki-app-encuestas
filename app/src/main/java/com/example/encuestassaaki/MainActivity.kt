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

/**
 * Actividad Principal (MainActivity).
 *
 * Funciona como el contenedor y orquestador principal de la aplicación.
 * Sus responsabilidades son:
 * 1. Gestionar la navegación entre los diferentes Fragmentos (Login, Datos, Encuestas).
 * 2. Mantener el estado global de la sesión del usuario (código, año, sexo).
 * 3. Gestionar el servicio de Texto a Voz (TTS) de forma centralizada para que lo usen los fragmentos.
 * 4. Gestionar la configuración de idioma (Castellano/Euskera) a nivel de contexto.
 */
class MainActivity : AppCompatActivity(),
    LoginFragment.LoginListener,
    SurveySelectionFragment.SurveySelectionListener,
    UserInfoFragment.UserInfoListener {

    // =================================================================================
    // VARIABLES DE ESTADO DEL USUARIO
    // Se almacenan aquí para persistir los datos mientras se navega entre fragmentos.
    // =================================================================================
    var codeUser: String = ""
    var yearUser: String = ""
    var sexUser: String = ""

    // =================================================================================
    // VARIABLES PARA TEXT-TO-SPEECH (TTS)
    // =================================================================================
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    // Almacena texto si se intenta hablar antes de que el motor TTS esté listo.
    private var pendingText: String? = null

    /**
     * Configura el contexto base de la aplicación para soportar el cambio de idioma.
     * Se ejecuta antes de `onCreate`.
     *
     * @param newBase El contexto original de Android.
     */
    override fun attachBaseContext(newBase: Context) {
        // Carga el idioma guardado en preferencias y envuelve el contexto
        val lang = LocaleHelper.loadLocale(newBase)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    /**
     * Punto de entrada principal de la actividad.
     * Inicializa la interfaz, el motor de voz y carga el primer fragmento (Login).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar TTS con idioma actual de la app
        initTTS(LocaleHelper.loadLocale(this))

        // Si es la primera vez que se crea la actividad (no es una rotación de pantalla),
        // cargamos el fragmento de Login.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    /**
     * Inicializa el motor de Texto a Voz (TTS).
     *
     * Configura el idioma y ajusta el tono y la velocidad de la voz para que sea
     * adecuada para el público objetivo (pediátrico).
     *
     * @param langCode Código del idioma actual ("es" o "eu").
     */
    private fun initTTS(langCode: String) {
        val locale = when(langCode) {
            "eu" -> Locale("eu") // Euskera
            else -> Locale("es", "ES") // Español por defecto
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Verificar si el idioma solicitado está disponible en el motor TTS del dispositivo
                val available = tts?.isLanguageAvailable(locale)

                // Si el idioma (ej. Euskera) no está instalado en el dispositivo, usamos Español como fallback
                val langToUse = if (available == TextToSpeech.LANG_AVAILABLE || available == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    locale
                } else {
                    Locale("es", "ES") // Fallback a español
                }

                val result = tts?.setLanguage(langToUse)

                // Ajustes de voz para que suene más amigable/natural
                tts?.setPitch(0.9f)       // más grave (menos artificial)
                tts?.setSpeechRate(1.4f) // un poquito más lento (aunque 1.4 suele ser rápido, depende del motor)

                // Marca el sistema como listo si no hubo errores críticos de idioma
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED

                // Si había algún texto esperando ser leído mientras se iniciaba, leerlo ahora
                pendingText?.let { speak(it) }
                pendingText = null
            }
        }
    }

    /**
     * Método público accesible desde los Fragments para leer texto en voz alta.
     * Gestiona la cola de reproducción.
     *
     * @param text El texto que se debe leer.
     */
    fun speak(text: String) {
        if (isTtsReady) {
            // QUEUE_FLUSH: Interrumpe lo que se esté diciendo para decir lo nuevo inmediatamente
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            // Si el motor no está listo, guardamos el texto para decirlo en cuanto inicialice
            pendingText = text
        }
    }

    /**
     * Limpieza de recursos al cerrar la app para evitar fugas de memoria.
     */
    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    // =================================================================================
    // IMPLEMENTACIÓN DE LISTENERS (Comunicación Fragment -> Activity)
    // =================================================================================

    /**
     * Callback recibido desde UserInfoFragment cuando se guardan los datos del usuario.
     * Actualiza las variables globales en la Activity.
     */
    override fun onUserInfoSaved(code: String, year: String, sex: String) {
        codeUser = code
        yearUser = year
        sexUser = sex
    }

    /**
     * Callback recibido desde LoginFragment cuando el login es correcto.
     * Navega hacia la pantalla de información de usuario.
     *
     * @param code El código de usuario introducido.
     */
    override fun onLogin(code: String) {
        val fragment = UserInfoFragment.newInstance(code)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Permite volver atrás con el botón del dispositivo
            .commit()
    }

    /**
     * Callback recibido desde SurveySelectionFragment.
     * Navega hacia la encuesta específica seleccionada (A o B).
     *
     * @param type El tipo de encuesta ("A" o "B").
     */
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