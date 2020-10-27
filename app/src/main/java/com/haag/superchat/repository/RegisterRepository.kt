package com.haag.superchat.repository

import android.util.Log.d
import android.view.View
import androidx.navigation.Navigation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haag.superchat.R
import com.haag.superchat.model.User
import com.haag.superchat.model.UserEmailUid
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterRepository @Inject constructor() {
    private lateinit var mAuth: FirebaseAuth
    val db = Firebase.firestore

    fun getInstance() {
        mAuth = FirebaseAuth.getInstance()
    }

    fun getCurrentUser() = mAuth!!.currentUser

    suspend fun createAccount(email: String, password: String): AuthResult {
        return mAuth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun addToUserList(user: UserEmailUid): Void? =
        db.collection("userList").document(user.email).set(user).await()

    suspend fun addToUsers(user: User): Void? =
        db.collection("users").document(user.id).set(user).await()


}