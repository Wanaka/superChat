package com.haag.superchat.ui.chat

import android.content.Context
import android.os.Bundle
import android.util.Log.d
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.haag.superchat.R
import com.haag.superchat.model.Chat
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.repository.ChatsRepository
import com.haag.superchat.sealedClasses.FriendListResponse
import com.haag.superchat.util.Constants
import com.haag.superchat.util.toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.*


class ChatViewModel @ViewModelInject constructor(private val repo: ChatsRepository) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    private val _friendList = MutableLiveData<FriendListResponse>()
    val friendList: LiveData<FriendListResponse> get() = _friendList

    fun getCurrentUser() = repo.getCurrentUser()

    fun getFriendsList() {
        val users = mutableListOf<User>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseUserId =
                    withContext(Dispatchers.Default) { repo.getFriendsList() }

                responseUserId?.forEach { it ->
                    var user =
                        withContext(Dispatchers.Default) {
                            repo.getUser(it.id)
                        }

                    users.add(
                        User(
                            user["userName"].toString(),
                            user["email"].toString(),
                            user["id"].toString()
                        )
                    )
                }
                _friendList.postValue(FriendListResponse.Success(users))
            } catch (ioe: IOException) {
                _friendList.postValue(FriendListResponse.Failure("[IO] error please retry", ioe))
            } catch (he: HttpException) {
                _friendList.postValue(FriendListResponse.Failure("[HTTP] error please retry", he))
            }
        }
    }

//    // sealed classes
//    fun getFriendsList(): LiveData<FriendListResponse> {
//        val list = MutableLiveData<FriendListResponse>()
//        val users = mutableListOf<User>()
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = async { repo.getFriendsList() }
//
//                withContext(Dispatchers.Main) {
//                    response.await()?.documents?.forEach { it ->
//                        users.add(
//                            User(
//                                it["userName"].toString(),
//                                it["email"].toString(),
//                                it["id"].toString()
//                            )
//                        )
//                    }
//
//                    list.postValue(FriendListResponse.Success(users))
//                }
//
//            } catch (ioe: IOException) {
//                list.postValue(FriendListResponse.Failure("[IO] error please retry", ioe))
//            } catch (he: HttpException) {
//                list.postValue(FriendListResponse.Failure("[HTTP] error please retry", he))
//            }
//        }
//
//        return list
//    }


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
        if (count == userList?.size) {
            addUserToFriendsList(user)
            getFriendsList()
        }

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


    // Navigation
    fun navigateTo(view: View, fragmentId: Int, user: User) {
        val bundle = Bundle()
        bundle.putString(Constants.FRIEND_ID, user.id)
        bundle.putString(Constants.USER, user.userName)
        Navigation.findNavController(view)
            .navigate(fragmentId, bundle)
    }

    fun navigateToSettings(view: View, fragmentId: Int, userId: String) {
        val bundle = Bundle()
        bundle.putString(Constants.USER, userId)
        Navigation.findNavController(view)
            .navigate(fragmentId, bundle)
    }
}