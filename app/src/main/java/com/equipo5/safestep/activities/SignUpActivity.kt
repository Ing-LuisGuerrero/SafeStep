package com.equipo5.safestep.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.equipo5.safestep.R
import com.equipo5.safestep.ValidateEmail
import com.equipo5.safestep.models.User
import com.equipo5.safestep.network.AuthService
import com.equipo5.safestep.network.Callback
import com.equipo5.safestep.network.FirestoreService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.AuthResult
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : AppCompatActivity(), ValidateEmail {

    private lateinit var fullName: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var passwordConfirm: String
    private val authService = AuthService()
    private val firestoreService = FirestoreService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setTermsAndConditionsString()

        btnGotoLogin.setOnClickListener { finish() }

        btnSubmitSignUp.setOnClickListener {
            if (isFormValid()) {
                signUp()
            }
        }
    }

    private fun signUp() {

        rlLoadingSignUp.visibility = View.VISIBLE

        authService.signUpWithEmailAndPassword(email, password, object: Callback<AuthResult>{

            override fun onSuccess(result: AuthResult?) {
                val user = authService.getCurrentUser()

                Log.d("ENTRE", "Entramos a onSuccessSignUp")

                if (user != null) {
                    insertUserInDatabase(user.uid)
                }
            }

            override fun onFailure(exception: Exception) {
                rlLoadingSignUp.visibility = View.INVISIBLE
                MaterialAlertDialogBuilder(this@SignUpActivity, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Primary)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.sign_up_failed))
                    .setPositiveButton(getString(R.string.got_it)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }

        })
    }

    private fun insertUserInDatabase(id: String) {

        val userData = User()

        userData.name = fullName
        userData.email = email
        userData.profilePicturePath = ""

        val user = Pair(id, userData)
        Log.d("name", user.second.name)
        firestoreService.insertUser(user, object: Callback<Void>{
            override fun onSuccess(result: Void?) {
                Log.d("Firestore", "Information has been saved.")
                authService.sendEmailVerification(object: Callback<Void>{
                    override fun onSuccess(result: Void?) {
                        authService.logOut()
                        rlLoadingSignUp.visibility = View.INVISIBLE
                        MaterialAlertDialogBuilder(this@SignUpActivity, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Primary)
                            .setTitle(getString(R.string.email_sent))
                            .setMessage(getString(R.string.instructions_verify_email))
                            .setPositiveButton(getString(R.string.got_it)) { _, _ ->
                                finish()
                            }
                            .setCancelable(false)
                            .show()
                    }

                    override fun onFailure(exception: Exception) {
                        authService.logOut()
                        rlLoadingSignUp.visibility = View.INVISIBLE
                        Log.d("AuthError", exception.message.toString())
                    }
                })
            }

            override fun onFailure(exception: Exception) {
                authService.logOut()
                rlLoadingSignUp.visibility = View.INVISIBLE
                Log.d("FirestoreError", exception.message.toString())
                finish()
            }
        })

    }


    private fun isFormValid(): Boolean {
        var isCorrect = true

        fullName = etFullName.text.toString().trim()
        email = etEmailSignUp.text.toString().trim()
        password = etPasswordSignUp.text.toString()
        passwordConfirm = etConfirmPassword.text.toString()

        when {
            fullName.isEmpty() -> {
                tivFullName.error = getString(R.string.required_field)
                tivFullName.isErrorEnabled = true
                isCorrect = false
            }
            fullName.length < 6 -> {
                tivFullName.error = getString(R.string.invalid_name)
                tivFullName.isErrorEnabled = true
                isCorrect = false
            }
            else -> {
                tivFullName.isErrorEnabled = false
            }
        }

        when {
            email.isEmpty() -> {
                tivEmailSignUp.error = getString(R.string.required_field)
                tivEmailSignUp.isErrorEnabled = true
                isCorrect = false
            }
            !isEmailValid(email) -> {
                tivEmailSignUp.error = getString(R.string.invalid_email)
                tivEmailSignUp.isErrorEnabled = true
                isCorrect = false
            }
            else -> {
                tivEmailSignUp.isErrorEnabled = false
            }
        }

        when {
            password.isEmpty() -> {
                tivPasswordSignUp.error = getString(R.string.required_field)
                tivPasswordSignUp.isErrorEnabled = true
                isCorrect = false
            }
            password.length < 6 -> {
                tivPasswordSignUp.error = getString(R.string.password_length)
                tivPasswordSignUp.isErrorEnabled = true
                isCorrect = false
            }
            else -> {
                tivPasswordSignUp.isErrorEnabled = false
            }
        }

        when {
            passwordConfirm.isEmpty() -> {
                tivConfirmPassword.error = getString(R.string.required_field)
                tivConfirmPassword.isErrorEnabled = true
                isCorrect = false
            }
            passwordConfirm != password -> {
                tivConfirmPassword.error = getString(R.string.passwords_not_match)
                tivConfirmPassword.isErrorEnabled = true
                isCorrect = false
            }
            else -> {
                tivConfirmPassword.isErrorEnabled = false
            }
        }

        if (!cbTermsAndConditions.isChecked) {
            Toast.makeText(this, getString(R.string.accept_terms_conditions), Toast.LENGTH_LONG)
                .show()
            isCorrect = false
        }

        return isCorrect
    }

    private fun setTermsAndConditionsString() {
        val textTarget = getString(R.string.string_target)

        val text = getString(R.string.terms_conditions)

        val htmlText =
            "<b><font color=" + getColor(R.color.colorAccent) + ">" + textTarget + "</font></b>"

        val newText = text.replace(textTarget, htmlText)

        tvTermsConditions.text = HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

}