package com.equipo5.safestep

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val text =  "Acepto los <b><font color=#F85F6A>Terminos, Condiciones</font></b> y el aviso de privacidad."
        tvTermsConditions.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        btnGotoLogin.setOnClickListener { finish() }
    }
}