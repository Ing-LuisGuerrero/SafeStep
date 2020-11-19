package com.equipo5.safestep.network

import com.equipo5.safestep.models.Crime
import com.equipo5.safestep.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

const val USERS_COLLECTION_NAME = "Users"

class FirestoreService {

    private val db = FirebaseFirestore.getInstance()

    fun getUser(id: String, callback: Callback<User>) {
        db.collection(USERS_COLLECTION_NAME).document(id)
            .get()
            .addOnSuccessListener {result ->
                if(result.exists()) {
                    val user = result.toObject(User::class.java)
                    callback.onSuccess(user)
                }
            }
            .addOnFailureListener {exception ->
                callback.onFailure(exception)
            }
    }

    fun insertUser(user: Pair<String, User>, callback: Callback<Void>) {
        db.collection(USERS_COLLECTION_NAME).document(user.first).set(user.second)
            .addOnSuccessListener { callback.onSuccess(it) }
            .addOnFailureListener { exception -> callback.onFailure(exception)  }
    }

    fun insertCrimeRegister(crime: Crime, callback: Callback<Task<Void>>) {
        db.collection("Crimes").document().set(crime)
            .addOnCompleteListener {
                callback.onSuccess(it)
            }
            .addOnFailureListener { exception ->
                callback.onFailure(exception)
            }
    }



}