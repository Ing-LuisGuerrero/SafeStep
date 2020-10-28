package com.equipo5.safestep.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.equipo5.safestep.R
import com.equipo5.safestep.ValidateEmail
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity(), ValidateEmail {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

       btnSubmitRestablecer.setOnClickListener(){
            if(ValidateEmail()){
                Toast.makeText(this, "Se ha mandado un Correo Electronico", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun ValidateEmail() :Boolean{
        var isCorrect= true
        val email=etEmailResetPassword.text.toString().trim()

        if (email.isEmpty()){
            tivEmailResetPassword.error= "El campo es obligatorio"
            tivEmailResetPassword.isErrorEnabled=true
            isCorrect=false

        }else if(!super.isEmailValid(email)){
            tivEmailResetPassword.error= "Email invalido"
            tivEmailResetPassword.isErrorEnabled= true
            isCorrect=false
        } else{
            tivEmailResetPassword.isErrorEnabled=false
        }
        return isCorrect
    }
}