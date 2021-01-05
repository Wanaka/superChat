package com.haag.superchat.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor(private val auth: FirebaseAuth) {

    suspend fun signIn(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email, password).await()
}