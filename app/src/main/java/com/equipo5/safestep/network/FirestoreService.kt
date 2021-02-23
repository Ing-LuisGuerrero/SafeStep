package com.equipo5.safestep.network

import com.equipo5.safestep.models.Report
import com.equipo5.safestep.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

const val USERS_COLLECTION_NAME = "Users"
const val REPORTS_COLLECTION_NAME = "Reports"

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

    fun insertCrimeRegister(report: Report, callback: Callback<Task<Void>>) {
        db.collection(REPORTS_COLLECTION_NAME).document().set(report)
            .addOnCompleteListener {
                callback.onSuccess(it)
            }
            .addOnFailureListener { exception ->
                callback.onFailure(exception)
            }
    }

    fun getReports(callback: Callback<List<Report>>) {
        db.collection(REPORTS_COLLECTION_NAME)
            .whereEqualTo("validated", true)
            .orderBy("whenWasItReported", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if(result.isEmpty) {
                    callback.onSuccess(result.toObjects(Report::class.java))
                } else {
                    for (document in result) {
                        val list = result.toObjects(Report::class.java)
                        callback.onSuccess(list)
                        break
                    }
                }
            }
            .addOnFailureListener { exception ->
                callback.onFailure(exception)
            }
    }

    fun getMyReports(id: String, callback: Callback<List<Report>>) {
        db.collection(REPORTS_COLLECTION_NAME)
            .whereEqualTo("idUser", id)
            .orderBy("whenWasItReported", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if(result.isEmpty) {
                    callback.onSuccess(result.toObjects(Report::class.java))
                } else {
                    for (document in result) {
                        val list = result.toObjects(Report::class.java)
                        callback.onSuccess(list)
                        break
                    }
                }
            }
            .addOnFailureListener { exception ->
                callback.onFailure(exception)
            }
    }



}