package com.haag.superchat.ui.detailChat

import android.util.Log
import android.util.Log.d
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.repository.DetailChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DetailChatViewModel constructor() : ViewModel() {
    private val repo = DetailChatRepository()

    private val _userData = MutableLiveData<User>()
    private val getchatId = MutableLiveData<Chat>()


    fun getInstance() = repo.getInstance()
    fun getCurrentUser() = repo.getCurrentUser()

    fun getUser(userId: String): LiveData<User> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var user = repo.getUser(userId)

                withContext(Dispatchers.Main) {
                    _userData.value = User(
                        user?.get("userName").toString(),
                        user?.get("email").toString(),
                        user?.get("id").toString()
                    )
                }

            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }

        return _userData
    }

    fun sendMessage(message: String, chatId: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.sendMessage(Message(message, getCurrentUser()?.uid.toString(), chatId))
            } catch (e: Exception) {
                Log.d(",,", "Exception: $e")
            }
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