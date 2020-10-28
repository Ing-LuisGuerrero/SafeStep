package com.equipo5.safestep.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.equipo5.safestep.R
import com.equipo5.safestep.providers.AuthProvider
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    val authProvider = AuthProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animation = AnimationUtils.loadAnimation(this, R.anim.animation_splash)
        tvAppName.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {

                val user = authProvider.getCurrentUser()

                if(user != null) {
                    Toast.makeText(this@SplashActivity, "Redireccionando", Toast.LENGTH_LONG).show()
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })

    }
}