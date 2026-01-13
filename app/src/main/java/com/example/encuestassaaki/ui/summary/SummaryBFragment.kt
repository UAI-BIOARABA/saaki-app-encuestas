package com.example.encuestassaaki.ui.summary

import android.content.res.Configuration
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
 * Fragmento de Resumen para la Encuesta B.
 *
 * Muestra las respuestas seleccionada y gestiona el guardado en CSV.
 *
 * DIFERENCIA CLAVE CON SUMMARY A:
 * Mientras que la encuesta A guarda números (Int), esta encuesta trabaja con Texto (String).
 * Esto introduce un desafío: Si la app está en Euskera, las respuestas vienen en Euskera (ej: "Bai").
 *
 * Este fragmento es responsable de "Traducir al vuelo" esas respuestas al Castellano
 * antes de escribirlas en el archivo CSV, para asegurar que la base de datos sea uniforme.
 */
class SummaryBFragment : Fragment() {

    private var code: String? = null
    private var year: String? = null
    private var sex: String? = null
    private var answers: ArrayList<String>? = null // Lista de textos (Strings)
    private lateinit var questions: List<String>

    companion object {
        /**
         * Crea una nueva instancia recibiendo las respuestas como lista de Strings.
         */
        fun newInstance(
            code: String,
            year: String,
            sex: String,
            answers: ArrayList<String>
        ): SummaryBFragment {
            val fragment = SummaryBFragment()
            val bundle = Bundle()
            bundle.putString("code", code)
            bundle.putString("year", year)
            bundle.putString("sex", sex)
            bundle.putStringArrayList("answers", answers)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString("code")
            year = it.getString("year")
            sex = it.getString("sex")
            answers = it.getStringArrayList("answers")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    /**
     * Configuración de la vista. Muestra el resumen al usuario.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textSummary: TextView = view.findViewById(R.id.text_summary)
        val btnSend: Button = view.findViewById(R.id.btn_send)

        questions = listOf(
            getString(R.string.qq1),
            getString(R.string.qq2),
            getString(R.string.qq3),
            getString(R.string.qq4),
            getString(R.string.qq5),
            getString(R.string.qq6),
            getString(R.string.qq7),
            getString(R.string.qq8)
        )

        // Construcción del texto para mostrar en pantalla (UI)
        // Aquí mostramos los datos tal cual están (en el idioma de la app)
        val sb = StringBuilder()
        sb.append(getString(R.string.code),": $code\n")
        sb.append(getString(R.string.year),": $year\n")
        sb.append(getString(R.string.genero),": ${getLocalizedSex(sex)}\n\n") // Traducir sexo para visualización
        sb.append(getString(R.string.resume),"\n")

        answers?.forEachIndexed { index, ans ->
            val question = if (index < questions.size) questions[index] else "Pregunta ${index + 1}"
            sb.append("$question\nRespuesta: $ans\n\n")
        }
        textSummary.text = sb.toString()

        // Botón ENVIAR
        btnSend.setOnClickListener {
            // Guardamos normalizando al Español
            if (saveToCSV()) {
                requireActivity().supportFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.speak(getString(R.string.ttssummary))
    }

    /**
     * Guarda las respuestas en `encuesta_b.csv` y su backup `eb.bak`.
     *
     * IMPORTANTE: Realiza una normalización lingüística.
     * Convierte las respuestas del idioma actual (Euskera/Español) a Español estándar
     * antes de guardar.
     */
    private fun saveToCSV(): Boolean {
        return try {
            val file = File(requireContext().getExternalFilesDir(null), "encuesta_b.csv")
            val isNew = !file.exists()
            val writer = FileWriter(file, true)

            // Escribir cabecera si el archivo es nuevo
            if (isNew) {
                writer.append("codigo,año,sexo,fecha")
                answers?.forEachIndexed { index, _ ->
                    writer.append(",p${index + 1}")
                }
                writer.append("\n")
            }

            val fecha = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
            val data = StringBuilder()

            // Metadatos iniciales
            data.append("${clean(code)},${clean(year)},${clean(sex)},$fecha")

            // Procesamiento de respuestas para traducción
            answers?.forEach { ans ->
                // LÓGICA DE TRADUCCIÓN INVERSA:
                // Comparamos el texto que tenemos (ans) con los recursos actuales strings.xml.
                // Si coincide con alguna opción conocida, forzamos la obtención de su versión en español.
                val spanishAns = when (ans) {
                    getString(R.string.yes) -> getSpanishString(R.string.yes)       // Si ans es "Bai", guarda "Si"
                    getString(R.string.moreorless) -> getSpanishString(R.string.moreorless)
                    getString(R.string.no) -> getSpanishString(R.string.no)
                    getString(R.string.answer) -> getSpanishString(R.string.answer)
                    else -> ans // Si no coincide (error raro), guarda lo que tenga
                }
                data.append(",${clean(spanishAns)}")
            }
            data.append("\n")

            // Guardar en el CSV principal
            writer.append(data.toString())
            writer.flush()
            writer.close()

            // Guardar en el backup (.bak)
            val bakFile = File(requireContext().getExternalFilesDir(null), "eb.bak")
            val bakWriter = FileWriter(bakFile, true)
            bakWriter.append(data.toString())
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
     * Helper avanzado: Obtiene el valor de un String Resource forzando el idioma Español.
     *
     * Crea un contexto de configuración temporal configurado en "es_ES" para leer
     * el archivo `values/strings.xml` (por defecto) en lugar de `values-eu/strings.xml`.
     *
     * @param resId El ID del recurso (R.string.yes, etc.)
     * @return El texto en castellano, independientemente del idioma del dispositivo.
     */
    private fun getSpanishString(resId: Int): String {
        val spanishConfig = resources.configuration
        val config = Configuration(spanishConfig)
        config.setLocale(Locale("es", "ES")) // Forzar configuración local
        val spanishContext = requireContext().createConfigurationContext(config)
        return spanishContext.getString(resId) // Leer recurso del contexto forzado
    }

    /**
     * Traduce el sexo almacenado (Español) al idioma visual actual.
     */
    private fun getLocalizedSex(sex: String?): String {
        val s = sex?.toLowerCase(Locale("es", "ES"))?.trim()

        return when (s) {
            "masculino" -> getString(R.string.male)
            "femenino" -> getString(R.string.female)
            else -> sex ?: ""
        }
    }

    /**
     * Función para limpiar imputs que irán al CSV.
     * Sirve para evitar que el archivo se rompa si alguien introduce caracteres que no debería
     * @param input Texto de un input
     * @return El string limpio para evitar que rompa CSVs
     */
    private fun clean(input: String?): String {
        return input?.replace(",", ".")   // Cambia comas por puntos
            ?.replace("\n", " ")   // Quita saltos de línea
            ?.trim()               // Quita espacios extra
            ?: ""
    }

}