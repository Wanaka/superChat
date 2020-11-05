package com.haag.superchat.ui.chat

import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.repository.ChatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ChatViewModel constructor() : ViewModel() {
    private val repo = ChatsRepository()

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData
    private val getchatId = MutableLiveData<Chat>()

    fun getInstance() = repo.getInstance()
    fun getCurrentUser() = repo.getCurrentUser()


    fun getFriendsList(): LiveData<List<User>> {
        return repo.getFriendsList()
    }

    fun searchUserByEmail(userEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = repo.searchUserByEmail(userEmail)

                if (user.signInMethods!!.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                    val userId = repo.getUserId(userEmail)
                    val id = userId?.get("id").toString()

                    val user = repo.getUser(id)

                    withContext(Dispatchers.Main) {
                        _userData.value = User(
                            user?.get("userName").toString(),
                            user?.get("email").toString(),
                            user?.get("id").toString()
                        )
                    }
                }
            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }
    }

    fun addUserToFriendsList(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.addUserToFriendsList(user)
                repo.addChatIdToFriend(user, Chat(UUID.randomUUID().toString()))
            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }
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

    fun getLastMessage(chatId: String): LiveData<Message> {
        return repo.getLastMessage(chatId)
    }

    fun signOut(view: View) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.signOut()

                withContext(Dispatchers.Main) {
                    signOutUser(view)
                }
            } catch (e: Exception) {
                Log.d(",,", "Exception: $e")
            }
        }
    }


    // Navigation
    private fun signOutUser(view: View) {
        Navigation.findNavController(view)
            .navigate(R.id.action_chatFragment_to_createUserLoginFragment)
    }

    fun navigateTo(view: View, fragmentId: Int, user: User) {
        val bundle = Bundle()
        bundle.putString("friendId", user.id)
        bundle.putString("user", user.userName)
        Navigation.findNavController(view)
            .navigate(fragmentId, bundle)
    }
}