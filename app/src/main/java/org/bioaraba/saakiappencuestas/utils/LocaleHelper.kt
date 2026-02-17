package org.bioaraba.saakiappencuestas.utils

import android.content.Context
import java.util.Locale

/**
 * Clase de utilidad (Singleton) para gestionar la Internacionalización (I18n).
 *
 * Su función es permitir cambiar el idioma de la aplicación dinámicamente
 * (Castellano <-> Euskera) y persistir esa elección para futuros inicios.
 *
 * NOTA TÉCNICA:
 * En versiones modernas de Android, no basta con cambiar la configuración global.
 * Se debe crear un nuevo "Contexto" configurado con el idioma deseado e inyectarlo
 * en las actividades.
 */
object LocaleHelper {

    /**
     * Establece el idioma deseado para la aplicación.
     *
     * Realiza dos acciones:
     * 1. Persiste la preferencia en SharedPreferences.
     * 2. Genera y devuelve un nuevo Contexto con la configuración de idioma aplicada.
     *
     * @param context El contexto actual.
     * @param langCode Código del idioma ("es" para Español, "eu" para Euskera).
     * @return Un nuevo Contexto configurado (ContextWrapper) que debe usarse en attachBaseContext.
     */
    fun setLocale(context: Context, langCode: String): Context {
        // 1. Configurar el objeto Locale estándar de Java
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        // 2. Preparar la configuración de recursos de Android
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)

        // Establecer dirección del layout (LTR/RTL), buena práctica aunque ES y EU son LTR.
        config.setLayoutDirection(locale)

        // 3. Crear el nuevo contexto con la configuración actualizada
        // Esto es necesario porque el contexto original es inmutable respecto a estos cambios
        val newContext = context.createConfigurationContext(config)

        // 4. Guardar la elección del usuario en memoria persistente
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        prefs.edit().putString("My_Lang", langCode).apply()

        return newContext
    }

    /**
     * Recupera el código de idioma guardado previamente.
     *
     * @param context Contexto para acceder a las preferencias.
     * @return El código de idioma ("es" o "eu"). Por defecto devuelve "es".
     */
    fun loadLocale(context: Context): String {
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        // Si no hay nada guardado, asumimos Español ("es")
        return prefs.getString("My_Lang", "es") ?: "es"
    }
}