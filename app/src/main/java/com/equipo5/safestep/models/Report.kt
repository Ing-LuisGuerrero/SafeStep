package com.equipo5.safestep.models

import com.google.firebase.Timestamp
import java.io.Serializable

class Report: Serializable {

    lateinit var title: String
    lateinit var category: String
    lateinit var description: String
    var image1: String? = null
    var image2: String? = null
    var image3: String? = null
    lateinit var idUser: String
    lateinit var latitude: String
    lateinit var longitude: String
    lateinit var whenWasItReported: Timestamp
    lateinit var whenItHappened: Timestamp
    val isValidated = false
}