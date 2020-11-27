package com.haag.superchat.repository

import android.util.Log
import android.util.Log.d
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatsRepository @Inject constructor() {
    private lateinit var mAuth: FirebaseAuth
    val db = Firebase.firestore

    fun getInstance() {
        mAuth = FirebaseAuth.getInstance()
    }

    fun getCurrentUser() = FirebaseAuth.getInstance().currentUser

    suspend fun searchUserByEmail(userEmail: String): SignInMethodQueryResult =
        mAuth.fetchSignInMethodsForEmail(userEmail).await()

    suspend fun getUserId(userEmail: String): DocumentSnapshot =
        db.collection("userList").document(userEmail).get().await()

    suspend fun getUser(userId: String?): DocumentSnapshot =
        db.collection("users").document(userId.toString()).get().await()


    fun getFriendsList(): LiveData<List<User>> {
        var friends = MutableLiveData<List<User>>()
        var users = mutableListOf<User>()

        db.collection("users").document(getCurrentUser()?.uid.toString()).collection("friends")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    d(",,", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    users.clear()

                    snapshot.documents.forEach {
                        users.add(
                            User(
                                it["userName"].toString(),
                                it["email"].toString(),
                                it["id"].toString()
                            )
                        )
                    }

                    friends.value = users
                } else {
                    d(",,", "Current data: null")
                }
            }

        return friends
    }

    suspend fun addUserToFriendsList(user: User) {
        db.collection("users").document(getCurrentUser()?.uid.toString())
            .collection("friends")
            .document(user.id).set(user).await()
    }

    suspend fun addChatIdToFriend(user: User, chatId: Chat) {
        db.collection("users").document(getCurrentUser()?.uid.toString())
            .collection("friends")
            .document(user.id).collection("chat").document(chatId.id).set(chatId).await()
    }

    fun getLastMessage(chatId: String): LiveData<Message> {
        var message = MutableLiveData<Message>()

        db.collection("chats").document(chatId).collection("chat")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    d(",,", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    if (snapshot.documents.size == 0) {
                        message.value = Message(
                            "",
                            ""
                        )
                    } else {
                        message.value = Message(
                            snapshot.documents.last()["message"].toString(),
                            snapshot.documents.last()["userId"].toString()
                        )
                    }
                } else {
                    d(",,", "Current data: null")
                }
            }
        return message
    }

    suspend fun getChatId(userId: String, friend: String): QuerySnapshot =
        db.collection("users").document(userId)
            .collection("friends")
            .document(friend).collection("chat").get().await()


    fun signOut() {
        mAuth.signOut()
    }
}