package com.equipo5.safestep.network

import android.content.Context
import com.equipo5.safestep.utils.CompressorBitmapImage
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class StorageService() {
    private var storage = FirebaseStorage.getInstance().reference


    fun saveImage(context: Context, file: File, callback: Callback<Void>) {
        val imageByte = CompressorBitmapImage.getImage(context, file.path, 500, 500)
        val store = storage.child(("${Date()}.jpg"))
        store.putBytes(imageByte)
            .addOnSuccessListener {
                storage = store
                callback.onSuccess(null)
            }
            .addOnFailureListener {
                callback.onFailure(it)
            }
    }

    fun getStorage(): StorageReference {
        return storage
    }

}