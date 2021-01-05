package com.haag.superchat.ui.chat

import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log.d
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.repository.ChatsRepository
import com.haag.superchat.repository.LoginRepository
import com.haag.superchat.util.Constants
import com.haag.superchat.util.toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ChatViewModel @ViewModelInject constructor(private val repo: ChatsRepository) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun getCurrentUser() = repo.getCurrentUser()


    fun getFriendsList(): LiveData<List<User>> {
        return repo.getFriendsList()
    }

    fun searchUserByEmail(userEmail: String, context: Context?) {
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
                } else {
                    withContext(Dispatchers.Main) {
                        context?.toaster(context.getString(R.string.toast_user_doesnt_exists))
                    }
                }
            } catch (e: Exception) {
                d(",,", "Exception: $e")

                var exception = e.message
                withContext(Dispatchers.Main) {
                    context?.toaster("$exception")
                }
            }
        }
    }

    fun addUserToFriendsList(user: User, userList: List<User>?, context: Context) {
        when (user.id) {
            getCurrentUser()?.uid -> context.toaster(context.getString(R.string.toast_adding_yourself_not_possible))
            else -> checkIfNewUser(user, userList, context)
        }
    }

    private fun checkIfNewUser(user: User, userList: List<User>?, context: Context) {
        var count = 0
        userList?.iterator()?.forEach { i ->
            when (i) {
                user -> {
                    context.toaster("${user.userName} ${context.getString(R.string.toast_user_exists)}")
                    return
                }
                else -> count++
            }
        }
        if (count == userList?.size) addUserToFriendsList(user)
    }

    private fun addUserToFriendsList(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var id = UUID.randomUUID().toString()
                repo.addUserToFriendsList(user)
                repo.addChatIdToFriend(user, Chat(id))
            } catch (e: Exception) {
                d(",,", "Exception: $e")
            }
        }
    }


    fun getChatId(friend: String): LiveData<Chat> {
        val getchatId = MutableLiveData<Chat>()

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

//    fun checkForNewMessage(message: Message, friendId: String, sharedPreference: SharedPreferences){
//        if (friendId == message.userId) {
//            if (sharedPreference?.getString(message.userId, "") != message.message) {
//                if (backStackFromUser == message.userId) {
//                    sharedPreference.put(
//                        "${message.userId}+${Constants.SHARED_PREF_BOOLEAN}",
//                        false
//                    )
//                    sharedPreference.put(message.userId, message.message)
//                    backStackFromUser = ""
//                } else {
//                    sharedPreference.put("${message.userId}+${Constants.SHARED_PREF_BOOLEAN}", true)
//                }
//            }
//        } else if (getCurrentUser()?.uid == message.userId) {
//            sharedPreference.put("${friendId}+${Constants.SHARED_PREF_BOOLEAN}", false)
//            sharedPreference.put("${friendId}+${Constants.SHARED_PREF_STRING}", message.message)
//            backStackFromUser = ""
//        } else {
//            sharedPreference.put("${friendId}+${Constants.SHARED_PREF_BOOLEAN}", true)
//
//        }
//        sharedPreference.put("${friendId}+${Constants.SHARED_PREF_STRING}", message.message)
//    }

    fun signOut(view: View) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.signOut()

                withContext(Dispatchers.Main) {
                    signOutUser(view)
                }
            } catch (e: Exception) {
                d(",,", "Exception: $e")
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
        bundle.putString(Constants.FRIEND_ID, user.id)
        bundle.putString(Constants.USER, user.userName)
        Navigation.findNavController(view)
            .navigate(fragmentId, bundle)
    }
}