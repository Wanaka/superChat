package com.haag.superchat.repository

import android.util.Log
import android.util.Log.d
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class DetailChatRepository @Inject constructor() {
    private lateinit var mAuth: FirebaseAuth
    val db = Firebase.firestore

    fun getInstance() {
        mAuth = FirebaseAuth.getInstance()
    }

    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    fun getChat(chatId: String?): LiveData<List<User>> {
        var friends = MutableLiveData<List<User>>()
        var users = mutableListOf<User>()

        db.collection("chats").document(chatId.toString()).collection("chat")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d(",,", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    snapshot.documents.forEach {
                        //save in correct format

                        d(",,", "snapshot: ${it["message"]}")
                    }
//                    users.clear()

//                    snapshot.documents.forEach {
//                        users.add(
//                            User(
//                                it["userName"].toString(),
//                                it["email"].toString(),
//                                it["id"].toString()
//                            )
//                        )
//                    }
//
//                    friends.value = users
                } else {
                    Log.d(",,", "Current data: null")
                }

            }

        return friends
    }

    suspend fun sendMessage(message: Message, chatId: String) {

        db.collection("chats").document(chatId)
            .collection("chat")
            .document(UUID.randomUUID().toString()).set(message).await()
    }

}