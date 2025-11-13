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

class SummaryAFragment : Fragment() {

    private var code: String? = null
    private var year: String? = null
    private var sex: String? = null
    private var answers: ArrayList<Int>? = null
    private lateinit var questions: List<String>
    private lateinit var answerMap: Map<Int, String>

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textSummary: TextView = view.findViewById(R.id.text_summary)
        val btnSend: Button = view.findViewById(R.id.btn_send)

        questions = listOf(
            getString(R.string.q1),
            getString(R.string.q2),
            getString(R.string.q3)
        )

        answerMap = mapOf(
            1 to getString(R.string.one),
            2 to getString(R.string.two),
            3 to getString(R.string.three),
            4 to getString(R.string.four),
            5 to getString(R.string.five)
        )

        // Mostrar resumen en pantalla
        val sb = StringBuilder()
        sb.append(getString(R.string.code),": $code\n")
        sb.append(getString(R.string.year),": $year\n")
        sb.append(getString(R.string.genero),": ${getLocalizedSex(sex)}\n\n")
        sb.append(getString(R.string.resume),"\n")

        answers?.forEachIndexed { index, ans ->
            val question = if (index < questions.size) questions[index] else "Pregunta ${index + 1}"
            val answerText = answerMap[ans] ?: ans.toString()
            sb.append("$question\nRespuesta: $answerText\n\n")
        }
        textSummary.text = sb.toString()

        // Botón ENVIAR
        btnSend.setOnClickListener {
            if (saveToCSVAndBak()) {
                requireActivity().supportFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.speak(getString(R.string.ttssummary))
    }

    private fun saveToCSVAndBak(): Boolean {
        return try {
            val fecha = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
            val line = buildString {
                append("$code,$year,$sex,$fecha")
                answers?.forEach { ans ->
                    append(",$ans")
                }
            }

            // Archivo CSV principal
            val file = File(requireContext().getExternalFilesDir(null), "encuesta_a.csv")
            val isNew = !file.exists()
            val writer = FileWriter(file, true)

            if (isNew) {
                writer.append("codigo,año,sexo,fecha")
                answers?.forEachIndexed { index, _ ->
                    writer.append(",p${index + 1}")
                }
                writer.append("\n")
            }
            writer.append(line).append("\n")
            writer.flush()
            writer.close()

            // Archivo de backup .bak
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

    private fun getLocalizedSex(sex: String?): String {
        val s = sex?.toLowerCase(Locale("es", "ES"))?.trim()

        return when (s) {
            "masculino" -> getString(R.string.male)
            "femenino" -> getString(R.string.female)
            else -> sex ?: ""
        }
    }

}
