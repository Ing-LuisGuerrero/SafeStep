package com.equipo5.safestep.network

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class AuthService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser() = auth.currentUser

    fun getUid() = getCurrentUser()?.uid

    fun getEmail() = getCurrentUser()?.email

    fun signUpWithEmailAndPassword(email: String, password: String, callback: Callback<AuthResult>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {result -> callback.onSuccess(result) }
            .addOnFailureListener { exception -> callback.onFailure(exception) }
    }

    fun sendEmailVerification(callback: Callback<Void>) {
        try {
            getCurrentUser()?.sendEmailVerification()!!
                .addOnSuccessListener { callback.onSuccess(it) }
                .addOnFailureListener { exception -> callback.onFailure(exception) }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
    }

    fun sendPasswordResetEmail(email: String, callback: Callback<Void>) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { callback.onSuccess(it) }
            .addOnFailureListener { exception -> callback.onFailure(exception) }
    }

    fun logInWithEmailAndPassword(email: String, password: String, callback: Callback<AuthResult>) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result -> callback.onSuccess(result) }
            .addOnFailureListener { exception -> callback.onFailure(exception) }
    }

    fun logOut() = auth.signOut()

}