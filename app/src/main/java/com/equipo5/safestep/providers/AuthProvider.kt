package com.equipo5.safestep.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthProvider {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun logInWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun getEmail(): String? {
        return if(auth.currentUser != null) {
            auth.currentUser!!.email
        } else {
            null
        }
    }

    fun sendPasswordResetEmail(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }

    fun signUpWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getUid(): String? {
        return if(auth.currentUser != null) {
            auth.currentUser!!.uid
        } else {
            null
        }
    }

    fun logOut() = auth.signOut()
}