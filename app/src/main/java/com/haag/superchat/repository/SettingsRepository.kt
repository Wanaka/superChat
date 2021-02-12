package com.haag.superchat.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.haag.superchat.BuildConfig
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    fun getCurrentUser() = auth.currentUser

    fun uploadImageToFirebaseStorage(img: Uri) { // Doesn't work w/ coroutines!
        img?.let {
            storage.reference.child("images/${getCurrentUser()?.uid.toString()}").putFile(it)
        }
    }

    fun getMyProfilePicture(): StorageReference =
        storage.getReferenceFromUrl("${BuildConfig.IMAGE_KEY}${getCurrentUser()?.uid.toString()}")

    suspend fun updateUserName(userName: String) {
        firestore.collection("users").document(getCurrentUser()?.uid.toString())
            .update("userName", userName).await()
    }

    suspend fun getUser(): DocumentSnapshot =
        firestore.collection("users").document(getCurrentUser()?.uid.toString()).get().await()

    fun signOut() {
        auth.signOut()
    }
}