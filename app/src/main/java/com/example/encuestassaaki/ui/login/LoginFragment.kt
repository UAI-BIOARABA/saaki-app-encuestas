package com.example.encuestassaaki.ui.login

import android.content.Context
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

    override fun onAttach(context: Context) {
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
    }
}
