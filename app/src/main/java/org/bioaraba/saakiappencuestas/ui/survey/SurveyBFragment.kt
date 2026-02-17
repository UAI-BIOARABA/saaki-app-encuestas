package org.bioaraba.saakiappencuestas.ui.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import org.bioaraba.saakiappencuestas.MainActivity
import org.bioaraba.saakiappencuestas.R
import org.bioaraba.saakiappencuestas.ui.summary.SummaryBFragment

/**
 * Fragmento de la Encuesta B.
 *
 * Gestiona el segundo tipo de cuestionario.
 * A diferencia de la Encuesta A (que usa imágenes/emojis), esta encuesta utiliza
 * botones de texto para las respuestas ("Sí", "Más o menos", "No", "NS/NC").
 *
 * NOTA DE DISEÑO:
 * Este fragmento REUTILIZA el layout `fragment_survey_a` porque la estructura
 * (Título, Pregunta, Contenedor de opciones, Botones de navegación) es idéntica.
 * La diferencia es el contenido que se inyecta dinámicamente en el `RadioGroup`.
 */
class SurveyBFragment : Fragment() {

    // Datos del usuario
    private lateinit var codeUser: String
    private lateinit var yearUser: String
    private lateinit var sexUser: String

    // Lista de preguntas
    private lateinit var questions: List<String>

    private var currentIndex = 0

    // Elementos de UI
    private lateinit var txtQuestionNumber: TextView
    private lateinit var txtQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    /**
     * Almacena las respuestas del usuario.
     * * IMPORTANTE: A diferencia de SurveyA (que guarda Ints), aquí se guardan Strings.
     * Se almacena el texto literal del botón seleccionado (ej: "Sí" o "Bai").
     */
    private val answers = ArrayList<String>()

    companion object {
        private const val ARG_CODE = "code"
        private const val ARG_YEAR = "year"
        private const val ARG_SEX = "sex"

        fun newInstance(code: String, year: String, sex: String) =
            SurveyBFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CODE, code)
                    putString(ARG_YEAR, year)
                    putString(ARG_SEX, sex)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            codeUser = it.getString(ARG_CODE) ?: ""
            yearUser = it.getString(ARG_YEAR) ?: ""
            sexUser = it.getString(ARG_SEX) ?: ""
        }
    }

    /**
     * Inflado de la vista.
     * Nótese el uso de `R.layout.fragment_survey_a`. No existe un `fragment_survey_b`
     * porque estructuralmente son iguales.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_survey_a, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtQuestionNumber = view.findViewById(R.id.txt_question_number)
        txtQuestion = view.findViewById(R.id.txt_question)
        radioGroup = view.findViewById(R.id.radio_group)
        btnNext = view.findViewById(R.id.btn_next)
        btnBack = view.findViewById(R.id.btn_back)

        // Cargar preguntas específicas de la Encuesta B (8 preguntas)
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

        // Inicializar respuestas con cadenas vacías
        answers.apply { repeat(questions.size) { add("") } }

        btnBack.visibility = View.GONE
        loadQuestion()

        // ========================================================================
        // LÓGICA DE NAVEGACIÓN (SIGUIENTE)
        // ========================================================================
        btnNext.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), getString(R.string.error_select_option), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtener el botón seleccionado y guardar su TEXTO
            val selectedRb = radioGroup.findViewById<RadioButton>(selectedId)
            answers[currentIndex] = selectedRb.text.toString()

            if (currentIndex < questions.size - 1) {
                currentIndex++
                loadQuestion()
            } else {
                // Finalizar: Pasar datos al resumen B
                val fragment = SummaryBFragment.newInstance(
                    code = codeUser,
                    year = yearUser,
                    sex = sexUser,
                    answers = answers
                )
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        btnBack.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadQuestion()
            }
        }
    }

    /**
     * Carga la pregunta actual y genera las opciones de respuesta TEXTUALES.
     */
    private fun loadQuestion() {
        txtQuestionNumber.text = getString(R.string.question_number, currentIndex + 1, questions.size)
        txtQuestion.text = questions[currentIndex]

        // Leer pregunta (TTS)
        (activity as? MainActivity)?.speak(questions[currentIndex])

        radioGroup.removeAllViews()
        radioGroup.clearCheck()

        // Opciones de respuesta para esta encuesta (Sí, Más o menos, No, Ns/Nc)
        val options = listOf(getString(R.string.yes), getString(R.string.moreorless),
            getString(R.string.no), getString(R.string.answer))

        // Generación dinámica de botones de texto
        for (i in options.indices) {
            val rb = RadioButton(requireContext()).apply {
                // Dimensiones rectangulares para botones de texto (más anchos)
                layoutParams = RadioGroup.LayoutParams(200.dp, 120.dp).apply {
                    setMargins(32.dp, 32.dp, 32.dp, 32.dp)
                }
                text = options[i]
                textSize = 25f
                setTypeface(typeface, android.graphics.Typeface.BOLD)  // Texto en negrita
                gravity = android.view.Gravity.CENTER // Texto centrado
                buttonDrawable = null // Quitamos el círculo de selección por defecto

                // Fondo con selector de estado (cambia de color al pulsar)
                setBackgroundResource(R.drawable.radio_selector)
                // Color de texto con selector (blanco al seleccionar, negro por defecto)
                setTextColor(resources.getColorStateList(R.color.selector_text, null))

            }
            radioGroup.addView(rb)
        }

        // ========================================================================
        // RESTAURACIÓN DE ESTADO
        // Como guardamos texto ("Sí"), buscamos el botón que tenga ese texto
        // ========================================================================
        val prevAnswer = answers[currentIndex]
        if (prevAnswer.isNotEmpty()) {
            for (j in 0 until radioGroup.childCount) {
                val rb = radioGroup.getChildAt(j) as RadioButton
                if (rb.text == prevAnswer) {
                    rb.isChecked = true
                    break
                }
            }
        }

        btnBack.visibility = if (currentIndex == 0) View.GONE else View.VISIBLE
        btnNext.text = if (currentIndex == questions.size - 1) getString(R.string.finish) else getString(R.string.next)
    }

    /**
     * Helper para convertir dp a píxeles.
     */
    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}