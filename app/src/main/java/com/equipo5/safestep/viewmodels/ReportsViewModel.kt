package com.equipo5.safestep.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.equipo5.safestep.models.Report
import com.equipo5.safestep.network.AuthService
import com.equipo5.safestep.network.Callback
import com.equipo5.safestep.network.FirestoreService

class ReportsViewModel(): ViewModel() {

    private val firestoreService = FirestoreService()
    val listReports: MutableLiveData<List<Report>> = MutableLiveData()
    var isLoading = MutableLiveData<Boolean>()
    private val authService = AuthService()
    val id: String
    var getAll: Boolean? = null


    fun refresh() {
        if(getAll == true) {
            getReportsFromFirebase()
        } else {
            getMyReports()
        }
    }

    init {
        id = authService.getUid().toString()
    }

    private fun getMyReports() {
        firestoreService.getMyReports(id, object: Callback<List<Report>> {
            override fun onSuccess(result: List<Report>?) {
                listReports.postValue(result)
                processFinished()
            }

            override fun onFailure(exception: Exception) {
                processFinished()
            }

        })
    }

    private fun getReportsFromFirebase() {
        firestoreService.getReports(object: Callback<List<Report>> {
            override fun onSuccess(result: List<Report>?) {
                listReports.postValue(result)
                processFinished()
            }

            override fun onFailure(exception: Exception) {
                processFinished()
            }

        })
    }


    fun processFinished() {
        isLoading.value = false
    }


}