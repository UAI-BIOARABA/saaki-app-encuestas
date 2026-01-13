package com.example.encuestassaaki.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.encuestassaaki.MainActivity
import com.example.encuestassaaki.R
import java.io.File
import java.io.FileWriter
import java.util.Locale

/**
 * Fragmento de Resumen para la Encuesta A.
 *
 * Esta pantalla tiene dos responsabilidades principales:
 * 1. Visualización: Muestra al usuario un resumen legible de lo que ha contestado antes de guardar.
 * 2. Persistencia: Es la encargada de escribir físicamente los datos en el almacenamiento del dispositivo.
 */
class SummaryAFragment : Fragment() {

    // Variables para almacenar temporalmente los datos recibidos antes de guardarlos
    private var code: String? = null
    private var year: String? = null
    private var sex: String? = null
    private var answers: ArrayList<Int>? = null // Lista de enteros (1-5)

    // Recursos para mostrar el texto en pantalla
    private lateinit var questions: List<String>
    private lateinit var answerMap: Map<Int, String>

    /**
     * Patrón Factory.
     * Recibe todos los datos recopilados (Usuario + Respuestas) para mostrarlos y guardarlos.
     */
    companion object {
        fun newInstance(
            code: String,
            year: String,
            sex: String,
            answers: ArrayList<Int>
        ): SummaryAFragment {
            val fragment = SummaryAFragment()
            val bundle = Bundle()
            bundle.putString("code", code)
            bundle.putString("year", year)
            bundle.putString("sex", sex)
            bundle.putIntegerArrayList("answers", answers)
            fragment.arguments = bundle
            return fragment
        }
    }

    // Recuperación de argumentos al crear el fragmento
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString("code")
            year = it.getString("year")
            sex = it.getString("sex")
            answers = it.getIntegerArrayList("answers")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    /**
     * Configuración de la interfaz de usuario.
     * Convierte los códigos numéricos de las respuestas en texto legible para el usuario.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textSummary: TextView = view.findViewById(R.id.text_summary)
        val btnSend: Button = view.findViewById(R.id.btn_send)

        // Cargar los enunciados de las preguntas
        questions = listOf(
            getString(R.string.q1),
            getString(R.string.q2),
            getString(R.string.q3)
        )

        // Mapa para traducir la puntuación numérica (1-5) a texto legible (ej: "Muy contento")
        // Esto se usa solo para MOSTRAR en pantalla, en el CSV se guardan números.
        answerMap = mapOf(
            1 to getString(R.string.one),
            2 to getString(R.string.two),
            3 to getString(R.string.three),
            4 to getString(R.string.four),
            5 to getString(R.string.five)
        )

        // Construcción del texto del resumen usando StringBuilder (eficiente para concatenar Strings)
        val sb = StringBuilder()
        sb.append(getString(R.string.code),": $code\n")
        sb.append(getString(R.string.year),": $year\n")
        // Convertimos el sexo guardado (ej: "Masculino") al idioma actual de la app (ej: "Gizonezkoa")
        sb.append(getString(R.string.genero),": ${getLocalizedSex(sex)}\n\n")
        sb.append(getString(R.string.resume),"\n")

        // Iterar sobre las respuestas para listar Pregunta -> Respuesta
        answers?.forEachIndexed { index, ans ->
            val question = if (index < questions.size) questions[index] else "Pregunta ${index + 1}"
            // Si la respuesta es 0 o no está en el mapa, mostramos el número crudo
            val answerText = answerMap[ans] ?: ans.toString()
            sb.append("$question\nRespuesta: $answerText\n\n")
        }
        textSummary.text = sb.toString()

        // Botón ENVIAR / FINALIZAR
        btnSend.setOnClickListener {
            // Intentar guardar en CSV. Si tiene éxito, salir.
            if (saveToCSVAndBak()) {
                // Volver al inicio limpiando toda la pila de fragmentos (resetear flujo)
                requireActivity().supportFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }
    }

    /**
     * TTS: Lee un mensaje de confirmación/resumen al usuario.
     */
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.speak(getString(R.string.ttssummary))
    }

    /**
     * Guarda los datos en archivos CSV persistentes.
     *
     * Estrategia de guardado:
     * 1. Genera una línea CSV con formato: codigo,año,sexo,fecha,p1,p2,p3...
     * 2. Escribe en "encuesta_a.csv" (añadiendo cabeceras si es nuevo).
     * 3. Escribe en "ea.bak" (archivo de respaldo simple).
     *
     * @return `true` si se guardó correctamente, `false` si hubo error.
     */
    private fun saveToCSVAndBak(): Boolean {
        return try {
            // Obtener fecha actual
            val fecha = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())

            // Construir la línea de datos (Comma Separated Values)
            val line = buildString {
                append("$code,$year,$sex,$fecha") // Metadatos
                answers?.forEach { ans ->
                    append(",$ans") // Respuestas numéricas (1-5)
                }
            }

            // ====================================================================
            // ESCRITURA EN ARCHIVO PRINCIPAL (encuesta_a.csv)
            // ====================================================================
            val file = File(requireContext().getExternalFilesDir(null), "encuesta_a.csv")
            val isNew = !file.exists()

            // FileWriter con segundo parámetro 'true' activa el modo APPEND (añadir al final)
            // para no sobrescribir los datos anteriores.
            val writer = FileWriter(file, true)

            // Si el archivo es nuevo, escribimos primero la cabecera de columnas
            if (isNew) {
                writer.append("codigo,año,sexo,fecha")
                answers?.forEachIndexed { index, _ ->
                    writer.append(",p${index + 1}") // p1, p2, p3...
                }
                writer.append("\n")
            }

            // Escribir la línea de datos del usuario actual
            writer.append(line).append("\n")
            writer.flush() // Forzar escritura en disco
            writer.close() // Cerrar flujo y liberar recurso

            // ====================================================================
            // ESCRITURA EN ARCHIVO DE RESPALDO (ea.bak)
            // Copia de seguridad redundante por si se corrompe el principal.
            // ====================================================================
            val bakFile = File(requireContext().getExternalFilesDir(null), "ea.bak")
            val bakWriter = FileWriter(bakFile, true)
            bakWriter.append(line).append("\n")
            bakWriter.flush()
            bakWriter.close()

            Toast.makeText(requireContext(), "Respuestas guardadas", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al guardar respuestas", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Traduce el sexo almacenado internamente (siempre en Español) al idioma de la UI.
     *
     * @param sex El valor almacenado (ej: "Masculino" o "Femenino").
     * @return El valor traducido para mostrar en pantalla (ej: "Gizonezkoa" si la app está en EU).
     */
    private fun getLocalizedSex(sex: String?): String {
        // Normalizar entrada para comparación segura
        val s = sex?.toLowerCase(Locale("es", "ES"))?.trim()

        return when (s) {
            "masculino" -> getString(R.string.male)   // Devuelve recurso traducido
            "femenino" -> getString(R.string.female)  // Devuelve recurso traducido
            else -> sex ?: "" // Si no coincide, devuelve el original
        }
    }

}