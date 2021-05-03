package com.equipo5.safestep.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.equipo5.safestep.R
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_reports_per_city.*

class ReportsPerCity : AppCompatActivity() {

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports_per_city)

        val btnCerrar = findViewById<ImageView>(R.id.cerrar)

        btnCerrar.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val numberCrimes1 = findViewById<TextView>(R.id.tvNumberCrimes)
        val numberCrimes2 = findViewById<TextView>(R.id.tvNumberCrimes2)
        val numberCrimes3 = findViewById<TextView>(R.id.tvNumberCrimes3)
        val numberCrimes4 = findViewById<TextView>(R.id.tvNumberCrimes4)
        val numberCrimes5 = findViewById<TextView>(R.id.tvNumberCrimes5)
        val numberCrimes6 = findViewById<TextView>(R.id.tvNumberCrimes6)
        val numberCrimes7 = findViewById<TextView>(R.id.tvNumberCrimes7)
        val numberCrimes8 = findViewById<TextView>(R.id.tvNumberCrimes8)
        val numberCrimes9 = findViewById<TextView>(R.id.tvNumberCrimes9)
        val numberCrimes10 = findViewById<TextView>(R.id.tvNumberCrimes10)
        val numberCrimes11 = findViewById<TextView>(R.id.tvNumberCrimes11)
        val numberCrimes12 = findViewById<TextView>(R.id.tvNumberCrimes12)
        val numberCrimes13 = findViewById<TextView>(R.id.tvNumberCrimes13)

        val db = Firebase.firestore

        var crime = 1
        db.collection("Cities")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    if(crime==1){
                        numberCrimes1.text = document.get("Delitos").toString()
                    }else if(crime==2){
                        numberCrimes2.text = document.get("Delitos").toString()
                    }else if(crime==3){
                        numberCrimes3.text = document.get("Delitos").toString()
                    }else if(crime==4){
                        numberCrimes4.text = document.get("Delitos").toString()
                    }else if(crime==5){
                        numberCrimes5.text = document.get("Delitos").toString()
                    }else if(crime==6){
                        numberCrimes6.text = document.get("Delitos").toString()
                    }else if(crime==7){
                        numberCrimes7.text = document.get("Delitos").toString()
                    }else if(crime==8){
                        numberCrimes8.text = document.get("Delitos").toString()
                    }else if(crime==9){
                        numberCrimes9.text = document.get("Delitos").toString()
                    }else if(crime==10){
                        numberCrimes10.text = document.get("Delitos").toString()
                    }else if(crime==11){
                        numberCrimes11.text = document.get("Delitos").toString()
                    }else if(crime==12){
                        numberCrimes12.text = document.get("Delitos").toString()
                    }else if(crime==13){
                        numberCrimes13.text = document.get("Delitos").toString()
                    }
                    crime += 1
                }
            }



    }



}