package com.example.encuestassaaki

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.encuestassaaki.ui.login.LoginFragment

class MainActivity : AppCompatActivity(), LoginFragment.LoginListener {

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
        Toast.makeText(this, getString(R.string.code_entered, code), Toast.LENGTH_SHORT).show()
        // Aquí luego navegaremos a UserInfoFragment o SurveySelectionFragment
    }
}
