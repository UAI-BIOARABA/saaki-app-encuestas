package com.example.encuestassaaki.ui.selection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.encuestassaaki.MainActivity
import com.example.encuestassaaki.R

/**
 * Fragmento de Selección de Encuesta.
 *
 * Actúa como un menú intermedio donde el usuario decide qué tipo de cuestionario
 * va a realizar (A o B).
 *
 * Su única responsabilidad es capturar la intención del usuario y comunicarla
 * a la MainActivity para que esta realice la transición al fragmento correspondiente.
 */
class SurveySelectionFragment : Fragment() {

    /**
     * Interfaz de comunicación (Patrón Listener).
     * Define el contrato que debe cumplir la Activity contenedora para manejar
     * la selección del usuario.
     */
    interface SurveySelectionListener {
        /**
         * Se invoca cuando el usuario selecciona una encuesta.
         * @param type Identificador de la encuesta ("A" o "B").
         */
        fun onSurveySelected(type: String) // "A" o "B"
    }

    private var listener: SurveySelectionListener? = null

    /**
     * Ciclo de vida: Vinculación.
     * Asegura que la Activity implementa la interfaz necesaria.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SurveySelectionListener) listener = context
    }

    /**
     * Ciclo de vida: Desvinculación.
     * Limpieza de referencias para evitar fugas de memoria.
     */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Ciclo de vida: Resumen.
     * Al mostrarse la pantalla, la app "lee" las instrucciones de selección
     * mediante el sistema TTS de la MainActivity.
     */
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.speak(getString(R.string.ttssurvey))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño visual (dos botones grandes)
        return inflater.inflate(R.layout.fragment_survey_selection, container, false)
    }

    /**
     * Configuración de los escuchadores de eventos (clicks).
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnSurveyA: Button = view.findViewById(R.id.btn_survey_a)
        val btnSurveyB: Button = view.findViewById(R.id.btn_survey_b)

        // Opción Encuesta A -> Notifica "A"
        btnSurveyA.setOnClickListener {
            listener?.onSurveySelected("A")
        }

        // Opción Encuesta B -> Notifica "B"
        btnSurveyB.setOnClickListener {
            listener?.onSurveySelected("B")
        }
    }
}