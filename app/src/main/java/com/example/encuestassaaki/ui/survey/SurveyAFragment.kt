package com.example.encuestassaaki.ui.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.encuestassaaki.MainActivity
import com.example.encuestassaaki.R
import com.example.encuestassaaki.ui.summary.SummaryAFragment

/**
 * Fragmento de la Encuesta A.
 *
 * Esta clase gestiona el flujo de preguntas de la primera encuesta.
 * A diferencia de una vista estática, este fragmento reutiliza la misma interfaz
 * para mostrar secuencialmente una lista de preguntas.
 *
 * Características principales:
 * 1. Carga las preguntas desde los recursos (strings.xml).
 * 2. Genera dinámicamente botones con imágenes (emojis) como opciones de respuesta.
 * 3. Gestiona la navegación (Atrás/Siguiente) y el almacenamiento temporal de respuestas.
 * 4. Al finalizar, envía todas las respuestas al Fragmento de Resumen.
 */
class SurveyAFragment : Fragment() {

    // Datos del usuario (recibidos como argumentos)
    private lateinit var codeUser: String
    private lateinit var yearUser: String
    private lateinit var sexUser: String

    // Lista de textos de las preguntas
    private lateinit var questions: List<String>

    // Índice que controla en qué pregunta estamos (empieza en 0)
    private var currentIndex = 0

    // Elementos de la interfaz
    private lateinit var txtQuestionNumber: TextView
    private lateinit var txtQuestion: TextView
    private lateinit var radioGroup: RadioGroup // Contenedor de las opciones
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    /**
     * Almacena las respuestas del usuario.
     * Es una lista de Enteros donde:
     * 0 = No respondido
     * 1 = Muy triste ... 5 = Muy contento
     */
    private val answers = ArrayList<Int>()

    /**
     * Patrón Factory para instanciar el fragmento con los datos del usuario.
     */
    companion object {
        private const val ARG_CODE = "code"
        private const val ARG_YEAR = "year"
        private const val ARG_SEX = "sex"

        fun newInstance(code: String, year: String, sex: String) =
            SurveyAFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CODE, code)
                    putString(ARG_YEAR, year)
                    putString(ARG_SEX, sex)
                }
            }
    }

    // Recuperación de argumentos al crear el fragmento
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            codeUser = it.getString(ARG_CODE) ?: ""
            yearUser = it.getString(ARG_YEAR) ?: ""
            sexUser = it.getString(ARG_SEX) ?: ""
        }
    }

    // Inflado de la vista XML
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_survey_a, container, false)

    /**
     * Inicialización lógica de la encuesta.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtQuestionNumber = view.findViewById(R.id.txt_question_number)
        txtQuestion = view.findViewById(R.id.txt_question)
        radioGroup = view.findViewById(R.id.radio_group)
        btnNext = view.findViewById(R.id.btn_next)
        btnBack = view.findViewById(R.id.btn_back)

        // Cargar las preguntas definidas en strings.xml
        questions = listOf(
            getString(R.string.q1),
            getString(R.string.q2),
            getString(R.string.q3)
        )

        // Inicializar la lista de respuestas con 0 (tantos ceros como preguntas haya)
        answers.apply {
            repeat(questions.size) { add(0) }
        }

        // Configuración inicial
        btnBack.visibility = View.GONE // En la primera pregunta no se puede volver atrás
        loadQuestion() // Cargar la primera pregunta

        // ========================================================================
        // LÓGICA BOTÓN SIGUIENTE / FINALIZAR
        // ========================================================================
        btnNext.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId

            // Validación: Obligar a seleccionar una opción
            if (selectedId == -1) {
                Toast.makeText(requireContext(), getString(R.string.error_select_option), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calcular respuesta (índice del hijo + 1 para que sea base 1-5)
            val answer = radioGroup.indexOfChild(radioGroup.findViewById(selectedId)) + 1
            answers[currentIndex] = answer

            // Decidir si avanzar a la siguiente pregunta o finalizar
            if (currentIndex < questions.size - 1) {
                // Caso: Hay más preguntas
                currentIndex++
                loadQuestion()
            } else {
                // Caso: Última pregunta, ir al Resumen
                val fragment = SummaryAFragment.newInstance(
                    code = codeUser,
                    year = yearUser,
                    sex = sexUser,
                    answers = answers // Pasamos la lista completa de respuestas
                )
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // ========================================================================
        // LÓGICA BOTÓN ATRÁS
        // ========================================================================
        btnBack.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                loadQuestion()
            }
        }
    }

    /**
     * Método central que actualiza la interfaz para la pregunta actual.
     *
     * Responsabilidades:
     * 1. Actualizar textos (número de pregunta y enunciado).
     * 2. Disparar el TTS (Audio) para leer la pregunta.
     * 3. Generar PROGRAMÁTICAMENTE los RadioButtons con imágenes.
     * 4. Restaurar la selección si el usuario vuelve atrás a una pregunta ya contestada.
     */
    private fun loadQuestion() {
        // Actualizar textos
        txtQuestionNumber.text = getString(R.string.question_number, currentIndex + 1, questions.size)
        txtQuestion.text = questions[currentIndex]

        // Leer la pregunta en voz alta usando la instancia de MainActivity
        (activity as? MainActivity)?.speak(questions[currentIndex])

        // Limpiar opciones anteriores para regenerarlas
        radioGroup.removeAllViews()
        radioGroup.clearCheck()

        // Recursos de imágenes para la escala de Likert (1 a 5)
        val images = listOf(
            R.drawable.muy_triste,
            R.drawable.triste,
            R.drawable.normal,
            R.drawable.contento,
            R.drawable.muy_contento
        )

        // Bucle para crear los 5 RadioButtons dinámicamente
        for (i in images.indices) {
            val rb = RadioButton(requireContext()).apply {
                // Configurar tamaño (140dp) y márgenes
                layoutParams = RadioGroup.LayoutParams(140.dp, 140.dp).apply {
                    setMargins(32.dp, 32.dp, 32.dp, 32.dp)
                }
                text = "" // Sin texto, solo imagen
                buttonDrawable = null // Quitar el círculo estándar de selección
                setBackgroundResource(R.drawable.radio_selector) // Fondo que cambia de color al seleccionar
                gravity = android.view.Gravity.CENTER

                // Cargar y dimensionar la imagen del emoji
                val drawable = resources.getDrawable(images[i], null)
                val size = 80.dp
                drawable.setBounds(0, 0, size, size)

                // Colocar la imagen centrada usando padding
                setPadding(0, 32.dp, 0, 32.dp)
                setCompoundDrawables(null, drawable, null, null)
                compoundDrawablePadding = 0
            }
            // Añadir el botón al grupo
            radioGroup.addView(rb)
        }


        // Restaurar estado: Si ya había respondido esta pregunta (al volver atrás), marcar la opción
        val prevAnswer = answers[currentIndex]
        if (prevAnswer != 0) {
            // Restamos 1 porque el índice de la vista empieza en 0
            (radioGroup.getChildAt(prevAnswer - 1) as RadioButton).isChecked = true
        }

        // Gestionar visibilidad de botones de navegación
        btnBack.visibility = if (currentIndex == 0) View.GONE else View.VISIBLE
        // Cambiar texto de "Siguiente" a "Finalizar" si es la última pregunta
        btnNext.text = if (currentIndex == questions.size - 1) getString(R.string.finish) else getString(R.string.next)
    }

    /**
     * Extensión Helper para convertir dp (density-independent pixels) a px (píxeles reales).
     * Necesario porque al crear vistas por código (RadioButton), las dimensiones se piden en píxeles,
     * pero nosotros queremos pensar en dp para soportar múltiples pantallas.
     */
    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

}