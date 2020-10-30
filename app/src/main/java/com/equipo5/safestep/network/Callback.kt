package com.equipo5.safestep.network

interface Callback<T> {
    fun onSuccess(result: T?)
    fun onFailure(exception: Exception)
}