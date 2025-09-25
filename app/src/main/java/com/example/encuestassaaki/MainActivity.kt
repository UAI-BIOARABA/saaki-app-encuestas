package com.example.encuestassaaki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.encuestassaaki.ui.login.LoginFragment
import com.example.encuestassaaki.ui.survey.SurveySelectionFragment
import com.example.encuestassaaki.ui.userinfo.UserInfoFragment

class MainActivity : AppCompatActivity(), LoginFragment.LoginListener, SurveySelectionFragment.SurveySelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    override fun onLogin(code: String) {
        val fragment = UserInfoFragment.newInstance(code)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // permite volver atrás con el botón físico
            .commit()
    }

    override fun onSurveySelected(type: String) {
        when (type) {
            "A" -> {
                // TODO: abrir fragmento de preguntas tipo A
            }
            "B" -> {
                // TODO: abrir fragmento de preguntas tipo B
            }
        }
    }
}
