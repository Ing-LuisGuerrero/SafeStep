package com.equipo5.safestep.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.equipo5.safestep.R
import com.equipo5.safestep.network.AuthService
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    val authService = AuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ivLogo.alpha = 0f
        ivLogo.animate().setDuration(1000).alpha(1f).withEndAction {
            try {
                val user = authService.getCurrentUser()

                if(user != null) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            } catch (e: Exception) {
                println("Error $e")
            }
            finally {
                println("Final")
            }
        }

        /*
        val animation = AnimationUtils.loadAnimation(this, R.anim.animation_splash)
        llSplashScreen.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                try {
                    val user = authService.getCurrentUser()

                    if(user != null) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    }

                    finish()
                } catch (e: Exception) {
                    println("Error $e")
                }
                finally {
                    println("Final")
                }

            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        }) */

    }
}