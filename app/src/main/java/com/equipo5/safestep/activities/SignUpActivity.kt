package com.equipo5.safestep.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.equipo5.safestep.R
import com.equipo5.safestep.ValidateEmail
import com.equipo5.safestep.models.User
import com.equipo5.safestep.providers.AuthProvider
import com.equipo5.safestep.providers.UsersProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_signup.*
import kotlin.Exception

class SignUpActivity : AppCompatActivity(), ValidateEmail {

    private lateinit var fullName: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var passwordConfirm: String
    private val authProvider = AuthProvider()
    private val usersProvider = UsersProvider()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setTermsAndConditionsString()

        btnGotoLogin.setOnClickListener { finish() }

        btnSubmitSignUp.setOnClickListener {
            if(isFormValid()) {
                signUp()
            }
        }
    }

    private fun signUp() {

        authProvider.signUpWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                authProvider.sendEmailVerification()
                authProvider.getUid()?.let { insertUserInDatabase(it) }

            } else {

                MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Primary)
                .setTitle("Error")
                .setMessage(task.exception?.message)
                .setPositiveButton("Entendido") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()

            }
        }
    }

    private fun insertUserInDatabase(id: String) {
        val user = User(fullName, email)

        try {
            usersProvider.insert(id, user)!!.addOnCompleteListener {result ->
                if (result.isSuccessful) {

                    MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Primary)
                        .setTitle("Correo de verificacion enviado")
                        .setMessage("Una vez verifique su correo podra iniciar sesion. Si el correo no se encuentra revise su bandeja de correo no deseado.")
                        .setPositiveButton("Entendido") { dialog, _ ->
                            authProvider.logOut()
                            finish()
                        }
                        .setCancelable(false)
                        .show()

                } else {
                    MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Primary)
                        .setTitle("Correo de verificacion enviado")
                        .setMessage("Una vez verifique su correo podra iniciar sesion para completar su registro. Si el correo no se encuentra revise su bandeja de correo no deseado.")
                        .setPositiveButton("Entendido") { _, _ ->
                            authProvider.logOut()
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        } catch (e: Exception) {
            Log.e("Error", "Error inserting user in firebase: ", e)
        }
    }


    private fun isFormValid(): Boolean {
        var isCorrect = true

        fullName = etFullName.text.toString().trim()
        email = etEmailSignUp.text.toString().trim()
        password = etPasswordSignUp.text.toString()
        passwordConfirm = etConfirmPassword.text.toString()

        if(fullName.isEmpty()) {
            tivFullName.error = "El campo es obligatorio"
            tivFullName.isErrorEnabled = true
            isCorrect = false
        } else if(fullName.length < 6) {
            tivFullName.error = "Ingrese un nombre valido"
            tivFullName.isErrorEnabled = true
            isCorrect = false
        } else {
            tivFullName.isErrorEnabled = false
        }

        if(email.isEmpty()) {
            tivEmailSignUp.error = "El campo es obligatorio"
            tivEmailSignUp.isErrorEnabled = true
            isCorrect = false
        } else if (!super.isEmailValid(email)) {
            tivEmailSignUp.error = "Email invalido"
            tivEmailSignUp.isErrorEnabled = true
            isCorrect = false
        } else {
            tivEmailSignUp.isErrorEnabled = false
        }

        if(password.isEmpty()) {
            tivPasswordSignUp.error = "El campo es obligatorio"
            tivPasswordSignUp.isErrorEnabled = true
            isCorrect = false
        } else if(password.length < 6) {
            tivPasswordSignUp.error = "Debe contener al menos 6 caracteres"
            tivPasswordSignUp.isErrorEnabled = true
            isCorrect = false
        } else {
            tivPasswordSignUp.isErrorEnabled = false
        }

        if(passwordConfirm.isEmpty()) {
            tivConfirmPassword.error = "El campo es obligatorio"
            tivConfirmPassword.isErrorEnabled = true
            isCorrect = false
        } else if(passwordConfirm != password) {
            tivConfirmPassword.error = "Las contraseñas no coinciden"
            tivConfirmPassword.isErrorEnabled = true
            isCorrect = false
        } else {
            tivConfirmPassword.isErrorEnabled = false
        }

        if(!cbTermsAndConditions.isChecked) {
            Toast.makeText(this, "Acepte los terminos y condiciones", Toast.LENGTH_LONG).show()
            isCorrect = false
        }

        return isCorrect
    }

    private fun setTermsAndConditionsString() {
        val textTarget = getString(R.string.string_target)

        val text =  getString(R.string.terms_conditions)

        val htmlText = "<b><font color=" + getColor(R.color.colorAccent) + ">" + textTarget + "</font></b>"

        val newText = text.replace(textTarget, htmlText)

        tvTermsConditions.text = HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

}