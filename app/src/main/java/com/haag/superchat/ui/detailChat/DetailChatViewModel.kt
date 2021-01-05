package com.haag.superchat.ui.detailChat

import android.util.Log.d
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haag.superchat.model.*
import com.haag.superchat.repository.DetailChatRepository
import com.haag.superchat.repository.NotificationRepository
import com.haag.superchat.util.FCMConstants
import com.haag.superchat.util.messageNumber
import kotlinx.coroutines.*

class DetailChatViewModel @ViewModelInject constructor(
    private val repo: DetailChatRepository,
    private val notificationRepo: NotificationRepository
) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    private val getchatId = MutableLiveData<Chat>()


    fun getCurrentUser() = repo.getCurrentUser()

    fun getUser(userId: String): LiveData<User> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var user = async { repo.getUser(userId) }

                withContext(Dispatchers.Main) {
                    _userData.value = User(
                        user?.await().get("userName").toString(),
                        user?.await().get("email").toString(),
                        user?.await().get("id").toString()
                    )
                }

            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }
        return _userData
    }

    fun sendMessage(
        message: String,
        chatId: Chat,
        friendId: String,
        user: User,
        msgNmbr: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                launch {
                    repo.sendMessage(
                        Message(
                            message,
                            getCurrentUser()?.uid.toString(),
                            chatId
                        ),
                        messageNumber(msgNmbr)
                    )
                }

                launch {
                    sendNotification(
                        PushNotification(
                            NotificationData(
                                "SuperChat",
                                "${user.userName}: $message"
                            ),
                            "${FCMConstants.TOPIC}/$friendId"
                        )
                    )
                }
            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }
    }

    private suspend fun sendNotification(notification: PushNotification) =
        viewModelScope.launch {
            try {
                val t = async { notificationRepo.post(notification) }

                if (t.await().isSuccessful) {
                    d(",,", "response: completed ${t.getCompleted()}")
                } else {
                    d(",,", "response: cancelled ${t.isCancelled}")
                }

            } catch (e: Exception) {
                d(",,", "exeption: ${e}")
            }
        }

    fun addUserToFriendsList(user: User, chatId: Chat, friend: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.addUserToFriendsList(user, friend)
                repo.addChatIdToFriend(user, chatId, friend)
            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }
    }

    fun getChat(chatId: String): LiveData<List<Message>> {
        return repo.getChat(chatId)
    }

    fun getChatId(friend: String): LiveData<Chat> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var querySnapshot = repo.getChatId(getCurrentUser()?.uid.toString(), friend)

                withContext(Dispatchers.Main) {
                    getchatId.value = Chat(querySnapshot.documents[0]["id"].toString())
                }

            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }

        return getchatId
    }
}