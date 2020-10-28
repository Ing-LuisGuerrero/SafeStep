package com.equipo5.safestep.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.equipo5.safestep.models.User

class UsersProvider {
    val collection = FirebaseFirestore.getInstance().collection("Users")

    fun getUser(id: String): Task<DocumentSnapshot>? {
        return collection.document(id).get()
    }

    fun insert(id: String, user: User): Task<Void>? {
        return collection.document(id).set(user)
    }


}