package com.example.encuestassaaki.ui.userinfo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.encuestassaaki.MainActivity
import com.example.encuestassaaki.R
import com.example.encuestassaaki.ui.selection.SurveySelectionFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UserInfoFragment : Fragment() {

    private var code: String? = null

    companion object {
        private const val ARG_CODE = "arg_code"

        fun newInstance(code: String): UserInfoFragment {
            val fragment = UserInfoFragment()
            val args = Bundle()
            args.putString(ARG_CODE, code)
            fragment.arguments = args
            return fragment
        }
    }

    interface UserInfoListener {
        fun onUserInfoSaved(code: String, year: String, sex: String)
    }

    private var listener: UserInfoListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UserInfoListener) listener = context
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        code = arguments?.getString(ARG_CODE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_userinfo, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.speak("Ingresa tus datos")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val txtCode: TextView = view.findViewById(R.id.txt_code)
        val editYear: EditText = view.findViewById(R.id.edit_year)
        val spinnerSex: Spinner = view.findViewById(R.id.spinner_sex)
        val textDate: TextView = view.findViewById(R.id.text_date)
        val btnSave: Button = view.findViewById(R.id.btn_save)

        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        textDate.text = getString(R.string.today_date, today)

        txtCode.text = getString(R.string.user_code, code)

        // Configurar spinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sex_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = adapter

        // Archivo CSV de usuarios
        val file = File(requireContext().getExternalFilesDir(null), "usuarios.csv")

        var userExists = false

        // Revisar si el código ya existe
        if (file.exists()) {
            file.forEachLine { line ->
                val parts = line.split(",")
                if (parts.isNotEmpty() && parts[0] == code) {
                    // Recuperar datos guardados
                    if (parts.size >= 3) {
                        editYear.setText(parts[1])
                        val sex = parts[2]
                        val sexIndex = resources.getStringArray(R.array.sex_options).indexOf(sex)
                        if (sexIndex >= 0) spinnerSex.setSelection(sexIndex)
                        // Bloquear edición porque ya estaba guardado
                        editYear.isEnabled = false
                        spinnerSex.isEnabled = false
                        userExists = true
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            val year = editYear.text.toString().trim()
            val sex = spinnerSex.selectedItem.toString()

            if (!userExists) {
                val lines = mutableListOf<String>()
                var updated = false

                if (file.exists()) {
                    file.forEachLine { line ->
                        val parts = line.split(",")
                        if (parts.isNotEmpty() && parts[0] == code) {
                            lines.add("$code,$year,$sex")
                            updated = true
                        } else {
                            lines.add(line)
                        }
                    }
                } else {
                    // Cabecera cuando el archivo aún no existe
                    lines.add("codigo,año,sexo")
                }

                if (!updated) {
                    lines.add("$code,$year,$sex")
                }

                file.writeText(lines.joinToString("\n"))

                Toast.makeText(requireContext(), getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
            }

            // Notificar a MainActivity
            listener?.onUserInfoSaved(code ?: "", year, sex)

            goToSurveySelection()
        }

    }

    private fun goToSurveySelection() {
        val fragment = SurveySelectionFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
