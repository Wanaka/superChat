package com.haag.superchat.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor() {
    private lateinit var mAuth: FirebaseAuth

    fun getInstance() {
        mAuth = FirebaseAuth.getInstance()
    }

    suspend fun signIn(email: String, password: String): AuthResult =
        mAuth.signInWithEmailAndPassword(email, password).await()

}