package com.equipo5.safestep.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.equipo5.safestep.R
import com.equipo5.safestep.ValidateEmail
import com.equipo5.safestep.providers.AuthProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity(), ValidateEmail {

    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        btnSubmitRestablecer.setOnClickListener() {
            if (validateEmail()) {
                sendEmail()
            }
        }
    }

    private fun sendEmail() {
        val authProvider = AuthProvider()
        authProvider.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful) {
                dialogOnSuccessful()
            } else {
                Toast.makeText(this, "Hubo un error en la operación", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun dialogOnSuccessful() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Primary)
            .setTitle("Confirmacion")
            .setMessage("El email ha sido enviado")
            .setPositiveButton("Entendido") { dialogInterface, _ ->
                dialogInterface.dismiss()
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun validateEmail(): Boolean {
        var isCorrect = true
        email = etEmailResetPassword.text.toString().trim()

        if (email.isEmpty()) {
            tivEmailResetPassword.error = "El campo es obligatorio"
            tivEmailResetPassword.isErrorEnabled = true
            isCorrect = false

        } else if (!super.isEmailValid(email)) {
            tivEmailResetPassword.error = "Email invalido"
            tivEmailResetPassword.isErrorEnabled = true
            isCorrect = false
        } else {
            tivEmailResetPassword.isErrorEnabled = false
        }
        return isCorrect
    }
}