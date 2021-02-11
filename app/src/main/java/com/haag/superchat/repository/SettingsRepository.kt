package com.haag.superchat.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.haag.superchat.util.randomUUID
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
}