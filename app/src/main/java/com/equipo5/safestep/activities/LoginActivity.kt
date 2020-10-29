package com.equipo5.safestep.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.equipo5.safestep.R
import com.equipo5.safestep.ValidateEmail
import com.equipo5.safestep.providers.AuthProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.math.log

class LoginActivity : AppCompatActivity(), ValidateEmail {

    val authProvider = AuthProvider()
    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnGoToResetPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        btnSubmitLogin.setOnClickListener() {
            if (validateLogIn()) {
                logIn()
            }
        }

    }

    private fun logIn() {
        rlLoadingLogIn.visibility = View.VISIBLE

        authProvider.logInWithEmailAndPassword(email, password).addOnCompleteListener {
            rlLoadingLogIn.visibility = View.INVISIBLE

            if(it.isSuccessful) {
                val user = authProvider.getCurrentUser()

                if(user != null) {
                    if(user.isEmailVerified) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        MaterialAlertDialogBuilder(
                            this,
                            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Primary
                        )
                            .setTitle(getString(R.string.no_verified_email))
                            .setMessage(getString(R.string.ask_send_new_email))
                            .setPositiveButton(R.string.send) { dialog, _ ->

                                user.sendEmailVerification()
                                dialog.dismiss()
                                Toast.makeText(this, getString(R.string.email_sent), Toast.LENGTH_LONG).show()
                            }
                            .setNegativeButton(getString(R.string.cancel)) {dialog, _ ->

                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .show()
                    }
                }

            } else {
                Toast.makeText(this, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateLogIn(): Boolean {
        var isCorrect = true

        email = etEmailLogIn.text.toString().trim()
        password = etPasswordLogIn.text.toString().trim()

        if (email.isEmpty()) {
            tivEmailLogIn.error = getString(R.string.required_field)
            tivEmailLogIn.isErrorEnabled = true
            isCorrect = false

        } else if (!super.isEmailValid(email)) {
            tivEmailLogIn.error = getString(R.string.invalid_email)
            tivEmailLogIn.isErrorEnabled = true
            isCorrect = false
        } else {
            tivEmailLogIn.isErrorEnabled = false
        }

        if (password.isEmpty()) {
            tivPasswordLogIn.error = getString(R.string.required_field)
            tivPasswordLogIn.isErrorEnabled = true
            isCorrect = false
        } else {
            tivPasswordLogIn.isErrorEnabled = false
        }

        return isCorrect
    }

}

