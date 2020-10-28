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
                        Toast.makeText(this, "Email verificado", Toast.LENGTH_LONG).show()
                    } else {
                        MaterialAlertDialogBuilder(
                            this,
                            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Primary
                        )
                            .setTitle("Email no verificado")
                            .setMessage("¿Desea mandar un nuevo correo de verificacion?")
                            .setPositiveButton("Enviar") { dialog, _ ->

                                user.sendEmailVerification()
                                dialog.dismiss()
                                Toast.makeText(this, "El email ha sido enviado", Toast.LENGTH_LONG).show()
                            }
                            .setNegativeButton("Cancelar") {dialog, _ ->

                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .show()
                    }
                    authProvider.logOut()
                }

            } else {
                Toast.makeText(this, "Credenciales no validas", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateLogIn(): Boolean {
        var isCorrect = true

        email = etEmailLogIn.text.toString().trim()
        password = etPasswordLogIn.text.toString().trim()

        if (email.isEmpty()) {
            tivEmailLogIn.error = "El campo es obligatorio"
            tivEmailLogIn.isErrorEnabled = true
            isCorrect = false

        } else if (!super.isEmailValid(email)) {
            tivEmailLogIn.error = "Email invalido"
            tivEmailLogIn.isErrorEnabled = true
            isCorrect = false
        } else {
            tivEmailLogIn.isErrorEnabled = false
        }

        if (password.isEmpty()) {
            tivPasswordLogIn.error = "El campo es obligatorio"
            tivPasswordLogIn.isErrorEnabled = true
            isCorrect = false
        } else {
            tivPasswordLogIn.isErrorEnabled = false
        }

        return isCorrect
    }

}

