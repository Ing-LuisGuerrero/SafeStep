package com.equipo5.safestep.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.equipo5.safestep.R
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setTermsAndConditionsString()

        btnGotoLogin.setOnClickListener { finish() }
    }

    private fun setTermsAndConditionsString() {
        val textTarget = getString(R.string.string_target)

        val text =  getString(R.string.terms_conditions)

        val htmlText = "<b><font color=" + getColor(R.color.colorAccent) + ">" + textTarget + "</font></b>"

        val newText = text.replace(textTarget, htmlText)

        tvTermsConditions.text = HtmlCompat.fromHtml(newText, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

}