package com.equipo5.safestep.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.equipo5.safestep.R
import com.equipo5.safestep.ValidateEmail
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), ValidateEmail {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnGoToResetPassword.setOnClickListener{
            startActivity(Intent( this, ResetPasswordActivity::class.java))
        }

        btnSubmitLogin.setOnClickListener(){
            if(ValidarLogIn()){
                Toast.makeText(this, "Es Correcto", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun ValidarLogIn() :Boolean{
        var isCorrect= true
        val email=etEmailLogIn.text.toString().trim()
        val password=etPasswordLogIn.text.toString().trim()

        if (email.isEmpty()){
            tivEmailLogIn.error= "El campo es obligatorio"
            tivEmailLogIn.isErrorEnabled=true
            isCorrect=false

        }else if(!super.isEmailValid(email)){
            tivEmailLogIn.error= "Email invalido"
            tivEmailLogIn.isErrorEnabled= true
            isCorrect=false
        } else{
            tivEmailLogIn.isErrorEnabled=false
        }

        if(password.isEmpty()){
            tivPasswordLogIn.error="El campo es obligatorio"
            tivPasswordLogIn.isErrorEnabled=true
            isCorrect=false
        }else if(password.length<6){
            tivPasswordLogIn.error="Debe contener al menos 6 caracteres"
            tivPasswordLogIn.isErrorEnabled=true
            isCorrect=false
        }else{
            tivPasswordLogIn.isErrorEnabled=false
        }

        return isCorrect
    }

}

