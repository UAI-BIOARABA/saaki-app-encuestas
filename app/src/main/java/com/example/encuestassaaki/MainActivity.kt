package com.example.encuestassaaki

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.encuestassaaki.ui.login.LoginFragment
import com.example.encuestassaaki.ui.survey.SurveyAFragment
import com.example.encuestassaaki.ui.survey.SurveyBFragment
import com.example.encuestassaaki.ui.survey.SurveySelectionFragment
import com.example.encuestassaaki.ui.userinfo.UserInfoFragment
import java.io.File

class MainActivity : AppCompatActivity(), LoginFragment.LoginListener, SurveySelectionFragment.SurveySelectionListener, UserInfoFragment.UserInfoListener {

    var codeUser: String = ""
    var yearUser: String = ""
    var sexUser: String = ""

    override fun onUserInfoSaved(code: String, year: String, sex: String) {
        codeUser = code
        yearUser = year
        sexUser = sex
    }

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
        if (type == "A") {
            val fragment = SurveyAFragment.newInstance(codeUser, yearUser, sexUser)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        if (type == "B") {
            val fragment = SurveyBFragment.newInstance(codeUser, yearUser, sexUser)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
