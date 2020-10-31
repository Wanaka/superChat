package com.haag.superchat.ui.detailChat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.haag.superchat.model.Message
import com.haag.superchat.model.User
import com.haag.superchat.repository.ChatsRepository
import com.haag.superchat.repository.DetailChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailChatViewModel constructor() : ViewModel() {
    private val repo = DetailChatRepository()

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> get() = _userData

    fun getInstance() = repo.getInstance()
    fun getCurrentUser() = repo.getCurrentUser()

    fun getChat(chatId: String?): LiveData<List<User>> {
        return repo.getChat(chatId)
    }

    fun sendMessage(message: String, chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.sendMessage(Message(message, getCurrentUser()?.uid.toString()), chatId)
            } catch (e: Exception) {
                Log.d(",,", "Exception: $e")
            }
        }
    }

}