package com.haag.superchat.util

interface ConnectivityListener {
    fun onNetworkConnectionChanged(isConnected: Boolean)
}