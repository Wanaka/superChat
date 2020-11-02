package com.haag.superchat.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.util.currentDate
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DetailChatRepository @Inject constructor() {
    private lateinit var mAuth: FirebaseAuth
    val db = Firebase.firestore

    fun getInstance() {
        mAuth = FirebaseAuth.getInstance()
    }

    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    suspend fun getUser(userId: String): DocumentSnapshot =
        db.collection("users").document(userId).get().await()

    fun getChat(chatId: String): LiveData<List<Message>> {
        var chatList = MutableLiveData<List<Message>>()
        var messages = mutableListOf<Message>()

        db.collection("chats").document(chatId).collection("chat")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d(",,", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messages.clear()
                    snapshot.documents.forEach {
                        messages.add(Message(it["message"].toString(), it["userId"].toString()))
                    }
                    chatList.value = messages
                } else {
                    Log.d(",,", "Current data: null")
                }
            }

        return chatList
    }

    suspend fun sendMessage(message: Message, chatId: Chat) {
        db.collection("chats").document(chatId.id)
            .collection("chat")
            .document(currentDate()).set(message).await()
    }

    suspend fun addUserToFriendsList(user: User, friend: String) {
        db.collection("users").document(friend)
            .collection("friends")
            .document(user.id).set(user).await()
    }

    suspend fun addChatIdToFriend(user: User, chatId: Chat, friend: String) {
        db.collection("users").document(friend)
            .collection("friends")
            .document(user.id).collection("chat").document(friend).set(chatId).await()
    }

    suspend fun getChatId(userId: String, friend: String): QuerySnapshot =
        db.collection("users").document(userId)
            .collection("friends")
            .document(friend).collection("chat").get().await()


}