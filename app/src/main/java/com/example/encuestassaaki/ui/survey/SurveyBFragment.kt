package com.example.encuestassaaki.ui.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.encuestassaaki.MainActivity
import com.example.encuestassaaki.R
import com.example.encuestassaaki.ui.summary.SummaryBFragment

class SurveyBFragment : Fragment() {

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
            getString(R.string.qq1),
            getString(R.string.qq2),
            getString(R.string.qq3),
            getString(R.string.qq4),
            getString(R.string.qq5),
            getString(R.string.qq6),
            getString(R.string.qq7),
            getString(R.string.qq8)
        )

        answers.apply { repeat(questions.size) { add("") } }

        btnBack.visibility = View.GONE
        loadQuestion()

        btnNext.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), getString(R.string.error_select_option), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRb = radioGroup.findViewById<RadioButton>(selectedId)
            answers[currentIndex] = selectedRb.text.toString()

            if (currentIndex < questions.size - 1) {
                currentIndex++
                loadQuestion()
            } else {
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

    private fun loadQuestion() {
        txtQuestionNumber.text = getString(R.string.question_number, currentIndex + 1, questions.size)
        txtQuestion.text = questions[currentIndex]

        (activity as? MainActivity)?.speak(questions[currentIndex])

        radioGroup.removeAllViews()
        radioGroup.clearCheck()

        val options = listOf(getString(R.string.yes), getString(R.string.moreorless),
            getString(R.string.no), getString(R.string.answer))

        for (i in options.indices) {
            val rb = RadioButton(requireContext()).apply {
                layoutParams = RadioGroup.LayoutParams(200.dp, 120.dp).apply {
                    setMargins(32.dp, 32.dp, 32.dp, 32.dp)
                }
                text = options[i]
                textSize = 20f
                gravity = android.view.Gravity.CENTER
                buttonDrawable = null
                setBackgroundResource(R.drawable.radio_selector)
                setTextColor(resources.getColorStateList(R.color.selector_text, null))
            }
            radioGroup.addView(rb)
        }

        // mantener selección si ya estaba
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

    // Helper para convertir dp a px
    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}
