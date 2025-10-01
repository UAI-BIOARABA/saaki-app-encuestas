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

class SurveyAFragment : Fragment() {

    private lateinit var codeUser: String
    private lateinit var yearUser: String
    private lateinit var sexUser: String
    private lateinit var questions: List<String>



    private var currentIndex = 0

    private lateinit var txtQuestionNumber: TextView
    private lateinit var txtQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button
    private val answers = ArrayList<Int>()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            codeUser = it.getString(ARG_CODE) ?: ""
            yearUser = it.getString(ARG_YEAR) ?: ""
            sexUser = it.getString(ARG_SEX) ?: ""
        }
    }

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
        questions = listOf(
                getString(R.string.q1),
                getString(R.string.q2),
                getString(R.string.q3)
            )

        answers.apply {
            repeat(questions.size) { add(0) }
        }

        btnBack.visibility = View.GONE
        loadQuestion()

        btnNext.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), getString(R.string.error_select_option), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val answer = radioGroup.indexOfChild(radioGroup.findViewById(selectedId)) + 1
            answers[currentIndex] = answer

            if (currentIndex < questions.size - 1) {
                currentIndex++
                loadQuestion()
            } else {
                val fragment = SummaryAFragment.newInstance(
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

    private fun loadQuestion() {
        txtQuestionNumber.text = getString(R.string.question_number, currentIndex + 1, questions.size)
        txtQuestion.text = questions[currentIndex]

        (activity as? MainActivity)?.speak(questions[currentIndex])

        radioGroup.removeAllViews()
        radioGroup.clearCheck()

        val images = listOf(
            R.drawable.muy_triste,
            R.drawable.triste,
            R.drawable.normal,
            R.drawable.contento,
            R.drawable.muy_contento
        )

        for (i in images.indices) {
            val rb = RadioButton(requireContext()).apply {
                layoutParams = RadioGroup.LayoutParams(140.dp, 140.dp).apply {
                    setMargins(32.dp, 32.dp, 32.dp, 32.dp)
                }
                text = ""
                buttonDrawable = null
                setBackgroundResource(R.drawable.radio_selector)
                gravity = android.view.Gravity.CENTER

                val drawable = resources.getDrawable(images[i], null)
                val size = 80.dp
                drawable.setBounds(0, 0, size, size)

                // poner la imagen centrada usando padding
                setPadding(0, 32.dp, 0, 32.dp)
                setCompoundDrawables(null, drawable, null, null)
                compoundDrawablePadding = 0
            }
            radioGroup.addView(rb)
        }


        // mantener selección si ya estaba
        val prevAnswer = answers[currentIndex]
        if (prevAnswer != 0) {
            (radioGroup.getChildAt(prevAnswer - 1) as RadioButton).isChecked = true
        }

        btnBack.visibility = if (currentIndex == 0) View.GONE else View.VISIBLE
        btnNext.text = if (currentIndex == questions.size - 1) getString(R.string.finish) else getString(R.string.next)
    }

    // Helper para convertir dp a px
    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

}
