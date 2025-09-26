package com.example.encuestassaaki.ui.selection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.encuestassaaki.R

class SurveySelectionFragment : Fragment() {

    interface SurveySelectionListener {
        fun onSurveySelected(type: String) // "A" o "B"
    }

    private var listener: SurveySelectionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SurveySelectionListener) listener = context
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_survey_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnSurveyA: Button = view.findViewById(R.id.btn_survey_a)
        val btnSurveyB: Button = view.findViewById(R.id.btn_survey_b)

        btnSurveyA.setOnClickListener {
            listener?.onSurveySelected("A")
        }

        btnSurveyB.setOnClickListener {
            listener?.onSurveySelected("B")
        }
    }
}
