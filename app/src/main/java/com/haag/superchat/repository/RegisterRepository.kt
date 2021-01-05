package com.haag.superchat.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.haag.superchat.model.User
import com.haag.superchat.model.UserEmailUid
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun getCurrentUser() = auth.currentUser

    suspend fun createAccount(email: String, password: String): AuthResult =
        auth.createUserWithEmailAndPassword(email, password).await()

    suspend fun addToUserList(user: UserEmailUid): Void? =
        firestore.collection("userList").document(user.email).set(user).await()

    suspend fun addToUsers(user: User): Void? =
        firestore.collection("users").document(user.id).set(user).await()
}