package com.example.encuestassaaki.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.encuestassaaki.R

class LoginFragment : Fragment() {

    interface LoginListener {
        fun onLogin(code: String)
    }

    private var listener: LoginListener? = null

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        if (context is LoginListener) listener = context
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val editCode: EditText = view.findViewById(R.id.edit_code)
        val btnLogin: Button = view.findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val code = editCode.text.toString().trim()
            if (code.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_empty_code, Toast.LENGTH_SHORT).show()
            } else {
                listener?.onLogin(code)
            }
        }
/*
        // Botón compartir usuarios
        val botonUsuarios: Button? = view.findViewById(R.id.botonUsuarios)
        botonUsuarios?.setOnClickListener {
            val file = File(requireContext().getExternalFilesDir(null), "usuarios.csv")
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    file
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(intent, "Compartir usuarios"))
            } else {
                Toast.makeText(requireContext(), "No hay usuarios guardados todavía", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón compartir respuestas
        val botonCompartir: Button? = view.findViewById(R.id.botonCompartir)
        botonCompartir?.setOnClickListener {
            val file = File(requireContext().getExternalFilesDir(null), "encuesta_a.csv")
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    file
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(intent, "Compartir respuestas"))
            } else {
                Toast.makeText(requireContext(), "No hay respuestas guardadas todavía", Toast.LENGTH_SHORT).show()
            }
        }

        val botonLimpiar = view.findViewById<Button>(R.id.botonLimpiar)
        botonLimpiar?.setOnClickListener {
            val file = File(requireContext().getExternalFilesDir(null), "encuesta_a.csv")
            if (file.exists()) {
                if (file.delete()) {
                    Toast.makeText(requireContext(), "Archivo eliminado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "No se pudo eliminar el archivo", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No hay archivo para eliminar", Toast.LENGTH_SHORT).show()
            }
        }
*/
    }

}
