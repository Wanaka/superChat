package com.haag.superchat.ui.detailChat

import android.util.Log
import android.util.Log.d
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.repository.ChatsRepository
import com.haag.superchat.repository.DetailChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DetailChatViewModel constructor() : ViewModel() {
    private val repo = DetailChatRepository()

    private val _userData = MutableLiveData<User>()
    private val getchatId = MutableLiveData<Chat>()
    private val getMessages = MutableLiveData<List<Message>>()

    fun getInstance() = repo.getInstance()
    fun getCurrentUser() = repo.getCurrentUser()

    fun getUser(userId: String): LiveData<User> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var user = repo.getUser(userId)


                d(",,", "user test: ${user["friends"]}")
                withContext(Dispatchers.Main) {
                    _userData.value = User(
                        user?.get("userName").toString(),
                        user?.get("email").toString(),
                        user?.get("id").toString()
                    )
                }

            } catch (e: Exception) {
                Log.d(",,", "Exception: $e")
            }
        }

        return _userData
    }

    fun sendMessage(message: String, chatId: Chat) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.sendMessage(Message(message, getCurrentUser()?.uid.toString()), chatId)
            } catch (e: Exception) {
                Log.d(",,", "Exception: $e")
            }
        }
    }

    fun addUserToFriendsList(user: User, chatId: Chat, friend: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.addUserToFriendsList(user, friend)
                //get chatid from friendList
                repo.addChatIdToFriend(user, chatId, friend)
            } catch (e: Exception) {
                Log.d(",,", "Exception: $e")
            }
        }
    }

//    fun getChat(chatId: Chat): LiveData<List<Message>> {
//        return repo.getChat(chatId)
//    }

    fun getChat(friend: String): LiveData<List<Message>> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var querySnapShot = repo.getChatId(getCurrentUser()?.uid.toString(), friend)

                var chatId = querySnapShot.documents[0]["id"].toString()
                var chat = repo.getChat(chatId)

                withContext(Dispatchers.Main) {
                    getMessages.value = chat.value
                }

            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }

        return getMessages
    }

    fun getChatId(friend: String): LiveData<Chat> {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var t = repo.getChatId(getCurrentUser()?.uid.toString(), friend)

                withContext(Dispatchers.Main) {
                    getchatId.value = Chat(t.documents[0]["id"].toString())
                }

            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }

        return getchatId
    }
}